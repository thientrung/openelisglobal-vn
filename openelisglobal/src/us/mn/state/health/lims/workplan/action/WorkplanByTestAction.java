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
 * Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
 *
 * Contributor(s): CIRG, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.workplan.action;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.ObservationHistoryService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.services.ObservationHistoryService.ObservationType;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.test.beanItems.TestResultItem;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkplanByTestAction extends BaseWorkplanAction {

	private final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static boolean HAS_NFS_PANEL = false;

	String testType = "";
	String testName = "";
    String receivedDate = "";
    String completedDate = "";
    String startedDate = "";

	static {
		HAS_NFS_PANEL = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.CONDENSE_NFS_PANEL, "true");
	}

	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		BaseActionForm dynaForm = (BaseActionForm) form;

		request.getSession().setAttribute(SAVE_DISABLED, "true");

		// Initialize the form.
		dynaForm.initialize(mapping);
		String workplan = (request.getParameter("type"));
		// Dung add the comment 2016.07.05
		// remove the title message <h1> on tab workplan
		//setRequestType(workplan);

		if (HAS_NFS_PANEL) {
			setNFSTestIdList();
		}

		List<TestResultItem> workplanTests;

		testType = request.getParameter("selectedSearchID");
		receivedDate = request.getParameter("receivedDate");
		completedDate = request.getParameter("completedDate");
		startedDate =  request.getParameter("startedDate");
		
		if (!GenericValidator.isBlankOrNull(testType)/* && 
		        (!GenericValidator.isBlankOrNull(receivedDate) || !GenericValidator.isBlankOrNull(resultDate))*/) {

			if (testType.equals("NFS")) {
				testName = "NFS";
				workplanTests = getWorkplanForNFSTest();
			} else {
				testName = getTestName(testType);
			    workplanTests = getWorkplanByTest(testType, receivedDate, startedDate, completedDate);
			}

			//resultsLoadUtility.sortByAccessionAndSequence(workplanTests);
			// Added by markaae.fr 2016-10-03 10:57AM
            PropertyUtils.setProperty(dynaForm, "receivedDate", receivedDate);
            PropertyUtils.setProperty(dynaForm, "completedDate", completedDate);
            PropertyUtils.setProperty(dynaForm, "startedDate", startedDate);
            // End of Modification
			
			//Dung fix selected combo box
			PropertyUtils.setProperty(dynaForm, "selectedSearchID", testType);
			PropertyUtils.setProperty(dynaForm, "testName", testName);
			PropertyUtils.setProperty(dynaForm, "workplanTests", workplanTests);
			PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.TRUE);

		} else {
			// no search done, set workplanTests as empty
			PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.FALSE);
			PropertyUtils.setProperty(dynaForm, "testName", null);
			PropertyUtils.setProperty(dynaForm, "workplanTests", new ArrayList<TestResultItem>());
		}

		PropertyUtils.setProperty(dynaForm, "searchTypes", getTestDropdownList(request));
		PropertyUtils.setProperty(dynaForm, "workplanType", request.getParameter("type"));
		PropertyUtils.setProperty(dynaForm, "searchLabel", StringUtil.getMessageForKey("workplan.test.types"));
		PropertyUtils.setProperty(dynaForm, "searchAction", "WorkPlanByTest.do");

		//Dung add fix bug workplan by test
		// load testSections for drop down
		PropertyUtils.setProperty(dynaForm, "testSections", DisplayListService.getList(ListType.TEST_SECTION));
		PropertyUtils.setProperty(dynaForm, "testSectionsByName", DisplayListService.getList(ListType.TEST_SECTION_BY_NAME));

		return mapping.findForward(FWD_SUCCESS);
	}

	private List<IdValuePair> getTestDropdownList(HttpServletRequest request) {
	    UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
		List<IdValuePair> testList;
		//Tien add
		//check user is admin, if true load all tests
		if(userModuleDAO.isUserAdmin(request)){
		    testList = DisplayListService.getList( DisplayListService.ListType.ALL_TESTS );		  
		}
		//if not admin, only load tests belong to this user
		else{
		    testList= DisplayListService.createTestListByUserId(Integer.parseInt(currentUserId));
		}
        //end
		if( HAS_NFS_PANEL){
			testList = adjustNFSTests(testList);
		}
		Collections.sort( testList, new valueComparator() );
        return testList;
	}

	private List<IdValuePair> adjustNFSTests(List<IdValuePair> allTestsList) {
        List<IdValuePair> adjustedList = new ArrayList<IdValuePair>( allTestsList.size() );
		for (IdValuePair idValuePair : allTestsList) {
			if (!nfsTestIdList.contains( idValuePair.getId() )) {
				adjustedList.add( idValuePair );
			}
		}
        // add NFS to the list
        adjustedList.add( new IdValuePair( "NFS", "NFS" ) );
        return adjustedList;
    }

	@SuppressWarnings("unchecked")
	private List<TestResultItem> getWorkplanByTest(String testType, String receivedDate, String startedDate, String completedDate) {

		List<Object[]> infoList;
		List<TestResultItem> workplanTestList = new ArrayList<TestResultItem>();
		String currentAccessionNumber = null;
		String subjectNumber = null;
		String patientName = null;
		String nextVisit = null;
		int sampleGroupingNumber = 0;

		if ( !(GenericValidator.isBlankOrNull(testType) || testType.equals("0"))/* && 
                (!GenericValidator.isBlankOrNull(receivedDate) || !GenericValidator.isBlankOrNull(resultDate))*/ ) {

			infoList = (List<Object[]>) analysisDAO.getAllAnalysisByTestAndStatusAndDate(testType, statusList, receivedDate, startedDate, completedDate);
			if (infoList.isEmpty()) {
				return new ArrayList<TestResultItem>();
			}
			
			for (Object[] analysisInfo : infoList) {
				String analysisId = (String) ((BigDecimal)analysisInfo[0]).toString();
				Analysis analysis = (Analysis) analysisDAO.getAnalysisById(analysisId);
				String emergency = (String) analysisInfo[3];
				String projectName = (String) analysisInfo[4];
			    try { 
    				TestResultItem testResultItem = new TestResultItem();
    				Sample sample = analysis.getSampleItem().getSample();
    				testResultItem.setAccessionNumber(sample.getAccessionNumber());
    				testResultItem.setReceivedDate( getReceivedDateDisplay(sample));
    				testResultItem.setStartedDate(analysis.getStartedDateForDisplay());
    				testResultItem.setCompletedDate(analysis.getCompletedDateForDisplay());
    				testResultItem.setNonconforming(QAService.isAnalysisParentNonConforming(analysis));
    				testResultItem.setEmergency(emergency);
    				if (projectName != null) {
    					testResultItem.setProjectName(projectName);
    				}
    
    				if (!testResultItem.getAccessionNumber().equals(currentAccessionNumber)) {
    					sampleGroupingNumber++;
    					currentAccessionNumber = testResultItem.getAccessionNumber();
    					subjectNumber = getSubjectNumber(analysis);
    					patientName = getPatientName(analysis);
    					nextVisit = ObservationHistoryService.getValueForSample( ObservationType.NEXT_VISIT_DATE, sample.getId() );
    				}
    				testResultItem.setSampleGroupingNumber(sampleGroupingNumber);
    				testResultItem.setPatientInfo(subjectNumber);
    				testResultItem.setPatientName(patientName);
    				testResultItem.setNextVisitDate(nextVisit);
				
    				workplanTestList.add(testResultItem);
    				
                } catch (NullPointerException ex) {
                    LogEvent.logError("WorkplanByTestAction", "getWorkplanByTest",
                            "If a other sample is null then next record: "+ex.getMessage());
                }
			}
		}

		return workplanTestList;
	}

	@SuppressWarnings("unchecked")
	private List<TestResultItem> getWorkplanForNFSTest() {

		List<Analysis> testList;
		List<TestResultItem> workplanTestList = new ArrayList<TestResultItem>();
		String currentAccessionNumber = null;
		int sampleGroupingNumber = 0;

		TestResultItem testResultItem;

		List<String> testIdList = new ArrayList<String>();

		if (!(GenericValidator.isBlankOrNull(testType) || testType.equals("0"))) {

			testList = analysisDAO.getAllAnalysisByTestsAndStatus(nfsTestIdList, statusList);

			if (testList.isEmpty()) {
				return new ArrayList<TestResultItem>();
			}

			for (Analysis analysis : testList) {

				Sample sample = analysis.getSampleItem().getSample();
				String analysisAccessionNumber = sample.getAccessionNumber();

				if (!analysisAccessionNumber.equals(currentAccessionNumber)) {
					sampleGroupingNumber++;
					currentAccessionNumber = analysisAccessionNumber;
					testIdList = new ArrayList<String>();

				}
				testResultItem = new TestResultItem();
				testResultItem.setAccessionNumber(currentAccessionNumber);
				testResultItem.setReceivedDate(sample.getReceivedDateForDisplay());
				testResultItem.setSampleGroupingNumber(sampleGroupingNumber);
				testResultItem.setNonconforming(QAService.isAnalysisParentNonConforming(analysis));
				testIdList.add(analysis.getTest().getId());

				if (allNFSTestsRequested(testIdList)) {
					workplanTestList.add(testResultItem);
				}

			}

		}

		return workplanTestList;
	}
		
	private String getTestName(String testId) {
		return TestService.getUserLocalizedTestName( testId );
	}

	class valueComparator implements Comparator<IdValuePair> {

		public int compare(IdValuePair p1, IdValuePair p2) {
			return p1.getValue().toUpperCase().compareTo(p2.getValue().toUpperCase());
		}

	}

}
