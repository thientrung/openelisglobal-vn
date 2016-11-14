/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.reports.action.implementation;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.AnalysisService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.HaitiAggregateReportData;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

import java.util.*;

/**
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * Copyright (C) CIRG, University of Washington, Seattle WA. All Rights
 * Reserved.
 * 
 */
public abstract class IndicatorAllTest extends IndicatorReport implements IReportCreator,
		IReportParameterSetter {

	private List<HaitiAggregateReportData> reportItems;
	private Map<String, TestBucket> testNameToBucketList;
	private Map<String, TestBucket> concatSection_TestToBucketMap;
	private List<TestBucket> testBucketList;

	private static final String USER_TEST_SECTION_ID;

    static {
		USER_TEST_SECTION_ID = new TestSectionDAOImpl().getTestSectionByName("user").getId();
	}

	@Override
	protected String reportFileName() {
		return "LabAggregate";
	}

	public JRDataSource getReportDataSource() throws IllegalStateException {
		return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(reportItems);
	}

	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
		setDateRange(dynaForm);

		createReportParameters();

		setTestMapForAllTests();

		setAnalysisForDateRange();

		mergeLists();

		setTestAggregates();

	}

	private void setTestMapForAllTests() {
		testNameToBucketList = new HashMap<String, TestBucket>();
		concatSection_TestToBucketMap = new HashMap<String, TestBucket>();
		testBucketList = new ArrayList<TestBucket>();

		List<Test> testList = TestService.getAllActiveTests();

		for (Test test : testList) {

			TestBucket bucket = new TestBucket();

			bucket.testName = TestService.getUserLocalizedTestName( test );
			bucket.testSort = Integer.parseInt(test.getSortOrder());
			bucket.testSection = test.getTestSection().getLocalizedName();
			bucket.sectionSort = test.getTestSection().getSortOrderInt();
			
			testNameToBucketList.put( TestService.getUserLocalizedReportingTestName( test ), bucket);
			testBucketList.add(bucket);
		}

	}

    private void setAnalysisForDateRange(){
        HashMap<String, ArrayList<Analysis>> sampleToPanelAnalysisMap = new HashMap<String, ArrayList<Analysis>>();
        List<Analysis> rawAnalysisList = AnalysisService.getAnalysisStartedOrCompletedInDateRange( lowDate, highDate );
        ArrayList<Analysis> analysisList = new ArrayList<Analysis>();

        //group analysis w/ panels by samples (sampleItem)
        for( Analysis analysis : rawAnalysisList ){
            extractAnalysisInPanels( sampleToPanelAnalysisMap, analysisList, analysis );
        }

        //for each sample we will break it down into panels with list of analysis
        for( String sampleId : sampleToPanelAnalysisMap.keySet() ){
            HashMap<String, ArrayList<Analysis>> panelIdToAnalysisMap = new HashMap<String, ArrayList<Analysis>>();
            for( Analysis sampleAnalysis : sampleToPanelAnalysisMap.get( sampleId ) ){
                ArrayList<Analysis> panelAnalysisList = panelIdToAnalysisMap.get( sampleAnalysis.getPanel().getId() );
                if( panelAnalysisList == null ){
                    panelAnalysisList = new ArrayList<Analysis>();
                    panelIdToAnalysisMap.put( sampleAnalysis.getPanel().getId(), panelAnalysisList );
                }
                panelAnalysisList.add( sampleAnalysis );
            }

            //we will now go through each panel and either throw out all analysis if any one is canceled
            // or create the correct analysis thingy
            for( String panelId : panelIdToAnalysisMap.keySet() ){
                Analysis templateAnalysis = panelIdToAnalysisMap.get( panelId ).get( 0 );
                String panelName = templateAnalysis.getPanel().getLocalizedName();

                boolean canceled = false;
                boolean notStarted = false;
                boolean finished = false;
                boolean inProgress = false;
                for( Analysis panelAnalysis : panelIdToAnalysisMap.get( panelId ) ){
                    if( StatusService.getInstance().matches( panelAnalysis.getStatusId(), AnalysisStatus.Canceled ) ){
                        canceled = true;
                        break;
                    }else if( StatusService.getInstance().matches( panelAnalysis.getStatusId(), AnalysisStatus.NotStarted ) ){
                        notStarted = true;
                    }else if( StatusService.getInstance().matches( panelAnalysis.getStatusId(), AnalysisStatus.Finalized ) ){
                        finished = true;
                    }else{
                        inProgress = true;
                    }
                }

                if( canceled ){
                    //all analysis are treated as being out of the panel
                    for( Analysis analysis : panelIdToAnalysisMap.get( panelId ) ){
                        analysisList.add( analysis );
                    }
                }else{
                    //we will create a fake analysis and add it to the analysisList
                    // and make sure the panel name is in the bucket list

                    String status;
                    if( inProgress || (notStarted && finished)){
                        status = StatusService.getInstance().getStatusID( AnalysisStatus.TechnicalAcceptance );
                    }else if( notStarted){
                        status = StatusService.getInstance().getStatusID( AnalysisStatus.NotStarted );
                    }else{
                        status = StatusService.getInstance().getStatusID( AnalysisStatus.Finalized );
                    }

                    Analysis proxyAnalysis = getProxyAnalysis( templateAnalysis, panelName, status );
                    analysisList.add( proxyAnalysis );

                    createAndAddBucketIfNeeded( panelName, proxyAnalysis );
                }

            }
        }
        //go through map and both classify panels and return sets with any canceled analysis
        for( Analysis analysis : analysisList ){
            addStatusToBuckets( analysis );
        }
    }

    private void extractAnalysisInPanels( HashMap<String, ArrayList<Analysis>> sampleToPanalAnalysisMap, ArrayList<Analysis> analysisList, Analysis analysis ){
        if( analysis.getPanel() == null ){
            analysisList.add( analysis );
        }else{
            ArrayList<Analysis> itemAnalysisList = sampleToPanalAnalysisMap.get( analysis.getSampleItem().getId() );
            if( itemAnalysisList == null ){
                itemAnalysisList = new ArrayList<Analysis>();
                sampleToPanalAnalysisMap.put( analysis.getSampleItem().getId(), itemAnalysisList );
            }
            itemAnalysisList.add( analysis );
        }
    }

    private void addStatusToBuckets( Analysis analysis ){
        Test test = analysis.getTest();

        if( test != null ){
            TestBucket testBucket;
            //N.B. We need to look at the test->test section because the analysis test section reflects the user selection for the test section
            //that entry will not be in the test to test section map
            if( USER_TEST_SECTION_ID.equals( analysis.getTest().getTestSection().getId() ) ){
                String concatedName = analysis.getTestSection().getLocalizedName()
                        + TestService.getUserLocalizedTestName( analysis.getTest() );
                testBucket = concatSection_TestToBucketMap.get( concatedName );
                if( testBucket == null ){
                    testBucket = new TestBucket();
                    testBucket.testName = TestService.getUserLocalizedReportingTestName( test );
                    testBucket.testSort = Integer.parseInt( test.getSortOrder() );
                    testBucket.testSection = analysis.getTestSection().getLocalizedName();
                    testBucket.sectionSort = analysis.getTestSection().getSortOrderInt();
                    concatSection_TestToBucketMap.put( concatedName, testBucket );
                }
            }else{
                testBucket = testNameToBucketList.get( TestService.getUserLocalizedReportingTestName( test ));
            }

            if( testBucket != null ){
                if( StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.NotStarted ) ){
                    testBucket.notStartedCount++;
                }else if( inProgress( analysis ) ){
                    testBucket.inProgressCount++;
                }else if( StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.Finalized ) ){
                    testBucket.finishedCount++;
                }
            }
        }
    }

    private void createAndAddBucketIfNeeded( String panelName, Analysis proxyAnalysis ){
        TestBucket panelBucket = testNameToBucketList.get( panelName );
        if( panelBucket == null ){
            panelBucket = new TestBucket();
            panelBucket.testName = panelName;
            panelBucket.testSort = -1;
            panelBucket.testSection = proxyAnalysis.getTestSection().getLocalizedName();
            panelBucket.sectionSort = proxyAnalysis.getTestSection().getSortOrderInt();
            testNameToBucketList.put( panelName, panelBucket );
            testBucketList.add( panelBucket );
        }
    }

    private Analysis getProxyAnalysis( Analysis templateAnalysis, String panelName, String status ){
        Analysis proxyAnalysis = new Analysis();
        TestSection proxyTestSection = new TestSection();
        Test proxyTest = new Test();

        proxyAnalysis.setStatusId( status );
        proxyAnalysis.setTest( proxyTest );
        proxyAnalysis.setTestSection( proxyTestSection );

        proxyTestSection.setTestSectionName( templateAnalysis.getTestSection().getLocalizedName() );
        proxyTestSection.setSortOrderInt( templateAnalysis.getTestSection().getSortOrderInt() );
        proxyTestSection.setId( templateAnalysis.getTestSection().getId() );

        proxyTest.setTestSection( proxyTestSection );
        proxyTest.setReportingDescription( panelName );

        return proxyAnalysis;
    }


    private boolean inProgress(Analysis analysis) {
		return StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.TechnicalAcceptance ) ||
               StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.TechnicalRejected ) ||
               StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.BiologistRejected );
	}
	
	private void mergeLists() {
        //get rid of empty buckets, make sorting faster
        List<TestBucket> fullBucketList = new ArrayList<TestBucket>(  );
        for( TestBucket bucket : testBucketList){
            if((bucket.finishedCount + bucket.notStartedCount + bucket.inProgressCount) > 0){
                fullBucketList.add( bucket );
            }
        }

        testBucketList = fullBucketList;

		for (TestBucket bucket : concatSection_TestToBucketMap.values()) {
			testBucketList.add(bucket);
		}

		Collections.sort(testBucketList, new Comparator<TestBucket>() {
			@Override
			public int compare(TestBucket o1, TestBucket o2) {
				int order = o1.sectionSort - o2.sectionSort;
				
				if( order == 0){
					order = o1.testSort - o2.testSort;
				}
				
				return order;
			}

		});

	}

	@Override
	protected String getNameForReportRequest() {
		return StringUtil.getMessageForKey("openreports.all.tests.aggregate");
	}

	private void setTestAggregates() {
		reportItems = new ArrayList<HaitiAggregateReportData>();

        for( TestBucket bucket : testBucketList ){
            HaitiAggregateReportData data = new HaitiAggregateReportData();

            data.setFinished( bucket.finishedCount );
            data.setNotStarted( bucket.notStartedCount );
            data.setInProgress( bucket.inProgressCount );
            data.setTestName( bucket.testName );
            data.setSectionName( bucket.testSection );

            reportItems.add( data );
        }
    }

	private class TestBucket {
		public String testName = "";
		public int testSort = 0;
		public String testSection = "";
		public int sectionSort = 0;
		public int notStartedCount = 0;
		public int inProgressCount = 0;
		public int finishedCount = 0;
	}

	@Override
	protected String getNameForReport() {
		return StringUtil.getContextualMessageForKey("openreports.all.tests.aggregate");
	}
	
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
		
		setDateRange(hashmap);
		createReportParameters();
		setTestMapForAllTests();
		setAnalysisForDateRange();
		mergeLists();
		setTestAggregates();
	}
    
}
