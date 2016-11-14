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
 *               I-TECH, University of Washington, Seattle WA.
 */
package us.mn.state.health.lims.resultvalidation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analyte.dao.AnalyteDAO;
import us.mn.state.health.lims.analyte.daoimpl.AnalyteDAOImpl;
import us.mn.state.health.lims.analyte.valueholder.Analyte;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.services.AnalysisService;
import us.mn.state.health.lims.common.services.NoteService;
import us.mn.state.health.lims.common.services.NoteService.NoteType;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.ResultLimitService;
import us.mn.state.health.lims.common.services.ResultService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.RecordStatus;
import us.mn.state.health.lims.common.services.TestIdentityService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.observationhistorytype.dao.ObservationHistoryTypeDAO;
import us.mn.state.health.lims.observationhistorytype.daoImpl.ObservationHistoryTypeDAOImpl;
import us.mn.state.health.lims.observationhistorytype.valueholder.ObservationHistoryType;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.resultvalidation.action.util.ResultValidationItem;
import us.mn.state.health.lims.resultvalidation.bean.AnalysisItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.statusofsample.util.StatusRules;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult.ResultType;

public class ResultsValidationUtility {

	
	protected static String ANALYTE_CD4_CT_GENERATED_ID;

	protected static String CONCLUSION_ID;

	protected final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
	protected final TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
	protected final ResultDAO resultDAO = new ResultDAOImpl();
	protected final TestResultDAO testResultDAO = new TestResultDAOImpl();
	protected final TestDAO testDAO = new TestDAOImpl();
	protected final SampleDAO sampleDAO = new SampleDAOImpl();
	protected final ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
	protected static String SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID;
	protected static String CD4_COUNT_SORT_NUMBER;

	protected static List<Integer> notValidStatus = new ArrayList<Integer>();
	protected Map<String, String> testIdToUnits = new HashMap<String, String>();
	protected Map<String, Boolean> accessionToValidMap;
	protected String totalTestName = "";

    static {
		notValidStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.Finalized)));
		notValidStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalRejected)));
		AnalyteDAO analyteDAO = new AnalyteDAOImpl();
		Analyte analyte = new Analyte();
		analyte.setAnalyteName("Conclusion");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		CONCLUSION_ID = analyte.getId();
		analyte.setAnalyteName("generated CD4 Count");
		analyte = analyteDAO.getAnalyteByName(analyte, false);
		ANALYTE_CD4_CT_GENERATED_ID = analyte == null ? "" : analyte.getId();

		TestDAO testDAO = new TestDAOImpl();
		
		Test test = testDAO.getTestByName("CD4 absolute count");
		if( test != null){
			CD4_COUNT_SORT_NUMBER = test.getSortOrder();
		}

		ObservationHistoryTypeDAO ohTypeDAO = new ObservationHistoryTypeDAOImpl();
		ObservationHistoryType oht = ohTypeDAO.getByName("SampleRecordStatus");
		if( oht != null){
			SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID = oht.getId();
		}
	}

	protected final AnalysisDAO analysisDAO = new AnalysisDAOImpl();

    /** 
     * Get list data for search by status or date of validation
     *
     * @param testName
     * @param statusList
     * @param testSectionId
     * @param receivedDate
     * @return
     */
    public List<AnalysisItem> getResultValidationList(String testId, List<Integer> statusList, String testSectionId, String receivedDate) {
        //Dung 2016.07.01
        //Add the parameter receivedDate for search by condition  
    	List<ResultValidationItem> testList = new ArrayList<ResultValidationItem>();
    	List<AnalysisItem> resultList = new ArrayList<AnalysisItem>();
    
    	if (!GenericValidator.isBlankOrNull(testSectionId) || !GenericValidator.isBlankOrNull(receivedDate)) {
    		testList = getUnValidatedTestResultItemsInTestSection(testSectionId, testId, statusList, receivedDate);
    		resultList = testResultListToAnalysisItemList(testList);
    		sortByAccessionNumberAndOrder(resultList);
    		setGroupingNumbers(resultList);
    	}
    
    	return resultList;
    }
	
    public List<AnalysisItem> getResultValidationList(List<Integer> statusList, String testSectionId) {

        List<ResultValidationItem> testList = new ArrayList<ResultValidationItem>();
        List<AnalysisItem> resultList = new ArrayList<AnalysisItem>();

        if (!GenericValidator.isBlankOrNull(testSectionId)) {
            testList = getUnValidatedTestResultItemsInTestSection(testSectionId, statusList);
            resultList = testResultListToAnalysisItemList(testList);
            sortByAccessionNumberAndOrder(resultList);
            setGroupingNumbers(resultList);
        }

        return resultList;
    }
	
	public List<AnalysisItem> getResultValidationListByAccessionNumber(List<Integer> statusList, String accessionNumber) {

		List<ResultValidationItem> testList = new ArrayList<ResultValidationItem>();
		List<AnalysisItem> resultList = new ArrayList<AnalysisItem>();

		if (!GenericValidator.isBlankOrNull(accessionNumber)) {
			testList = getUnValidatedTestResultItems(accessionNumber, statusList);
			resultList = testResultListToAnalysisItemList(testList);
			sortByAccessionNumberAndOrder(resultList);
			setGroupingNumbers(resultList);
		}

		return resultList;
	}
	
	public final List<ResultValidationItem> getUnValidatedTestResultItemsInTestSection(String sectionId, String testId, List<Integer> statusList, String receivedDate) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestSectionAndStatus(sectionId, testId, statusList, receivedDate, false);
		return getGroupedTestsForAnalysisList(analysisList, !StatusRules.useRecordStatusForValidation());
	}
	public final List<ResultValidationItem> getUnValidatedTestResultItemsInTestSection(String sectionId, List<Integer> statusList) {

	        List<Analysis> analysisList = analysisDAO.getAllAnalysisByTestSectionAndStatus(sectionId, statusList, false);
	        return getGroupedTestsForAnalysisList(analysisList, !StatusRules.useRecordStatusForValidation());
	    }
	
	public final List<ResultValidationItem> getUnValidatedTestResultItems(String accessionNumber, List<Integer> statusList) {

		List<Analysis> analysisList = analysisDAO.getAllAnalysisByAccessionNumberAndStatus(accessionNumber, statusList, false);
		return getGroupedTestsForAnalysisList(analysisList, !StatusRules.useRecordStatusForValidation());
	}
	
	protected final void sortByAccessionNumberAndOrder(List<AnalysisItem> resultItemList) {
		Collections.sort(resultItemList, new Comparator<AnalysisItem>() {
			public final int compare(AnalysisItem a, AnalysisItem b) {
				int accessionComp = a.getAccessionNumber().compareTo(b.getAccessionNumber());	// accession_number (1st priority)
				if (accessionComp == 0) {
					if( !GenericValidator.isBlankOrNull(a.getTestSortNumber()) && !GenericValidator.isBlankOrNull(b.getTestSortNumber())){ // test.sort_order (2nd priority)
						try {
							return Integer.parseInt(a.getTestSortNumber()) - Integer.parseInt(b.getTestSortNumber());
						} catch (NumberFormatException e) {
							return a.getTestName().compareTo(b.getTestName());
						}
					} else {
						return a.getTestName().compareTo(b.getTestName()); // test.name (3rd priority)
					}
				} 
				
				return accessionComp;
			}
		});
	}
	
	protected final void setGroupingNumbers(List<AnalysisItem> resultList) {
		String currentAccessionNumber = null;
		AnalysisItem headItem = null;
		int groupingCount = 1;

		for (AnalysisItem analysisResultItem : resultList) {
			if (!analysisResultItem.getAccessionNumber().equals(currentAccessionNumber)) {
				currentAccessionNumber = analysisResultItem.getAccessionNumber();
				headItem = analysisResultItem;
				groupingCount++;
			} else {
				headItem.setMultipleResultForSample(true);
				analysisResultItem.setMultipleResultForSample(true);
			}

			analysisResultItem.setSampleGroupingNumber(groupingCount);
		}
	}
	
	/*
	 * N.B. The ignoreRecordStatus is an abomination and should be removed. It
	 * is a quick and dirty fix for workplan and validation using the same code
	 * but having different rules
	 */
	public final List<ResultValidationItem> getGroupedTestsForAnalysisList(Collection<Analysis> filteredAnalysisList, boolean ignoreRecordStatus)
			throws LIMSRuntimeException {

		List<ResultValidationItem> selectedTestList = new ArrayList<ResultValidationItem>();
		Dictionary dictionary;

		for (Analysis analysis : filteredAnalysisList) {

			if (ignoreRecordStatus || sampleReadyForValidation(analysis.getSampleItem().getSample())) {
				List<ResultValidationItem> testResultItemList = getResultItemFromAnalysis(analysis);
				//NB.  The resultValue is filled in during getResultItemFromAnalysis as a side effect of setResult
				for (ResultValidationItem validationItem : testResultItemList) {
					if (ResultType.isDictionaryVariant( validationItem.getResultType() )) {
						dictionary = new Dictionary();
						String resultValue = null;
						try {
							dictionary.setId(validationItem.getResultValue());
							dictionaryDAO.getData(dictionary);
							resultValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary.getDictEntry()
									: dictionary.getLocalAbbreviation();
						} catch (Exception e) {
                            //no-op
						}

						validationItem.setResultValue(resultValue);

					}

                    validationItem.setAnalysis( analysis );
					validationItem.setNonconforming( QAService.isAnalysisParentNonConforming( analysis ) ||
                    StatusService.getInstance().matches( analysis.getStatusId(), AnalysisStatus.TechnicalRejected ) );	
					
					if(!GenericValidator.isBlankOrNull(validationItem.getResultValue())){
						selectedTestList.add(validationItem);
					}
				}
			}
		}

		return selectedTestList;
	}

    protected final boolean sampleReadyForValidation(Sample sample) {

		Boolean valid = accessionToValidMap.get(sample.getAccessionNumber());

		if (valid == null) {
			valid = getSampleRecordStatus( sample ) == StatusService.RecordStatus.ValidationRegistration;
			accessionToValidMap.put(sample.getAccessionNumber(), valid);
		}

		return valid;
	}

	public final List<ResultValidationItem> getResultItemFromAnalysis(Analysis analysis) throws LIMSRuntimeException {
		List<ResultValidationItem> testResultList = new ArrayList<ResultValidationItem>();

		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
        NoteType[] noteTypes = { NoteType.EXTERNAL, NoteType.INTERNAL, NoteType.REJECTION_REASON, NoteType.NON_CONFORMITY};
        String notes = new NoteService( analysis ).getNotesAsString( true, true, "<br/>", noteTypes, false );

		if (resultList == null) {
			return testResultList;
		}

		// For historical reasons we add a null member to the collection if it
		// is empty
		// this should be refactored.
		// The result list are results associated with the analysis, if there is
		// none we want
		// to present the user with a blank one
		if (resultList.isEmpty()) {
			resultList.add(null);
		}

		ResultValidationItem parentItem = null;
		for (Result result : resultList) {
			if( parentItem != null && result.getParentResult() != null && parentItem.getResultId().equals(result.getParentResult().getId())){
				parentItem.setQualifiedResultValue(result.getValue());
				parentItem.setHasQualifiedResult( true );
                parentItem.setQualificationResultId(result.getId());
				continue;
			}
			
			ResultValidationItem resultItem = createTestResultItem(analysis, analysis.getTest(), analysis.getSampleItem().getSortOrder(),
					result, analysis.getSampleItem().getSample().getAccessionNumber(), notes);

            notes = null;//we only want it once
			if( resultItem.getQualifiedDictionaryId() != null){
				parentItem = resultItem;
			}			
			testResultList.add(resultItem);
			
		}

		return testResultList;
	}

	protected final ResultValidationItem createTestResultItem(Analysis analysis, Test test, String sequenceNumber, Result result,
			String accessionNumber, String notes) {

		List<TestResult> testResults = getPossibleResultsForTest(test);

		String displayTestName = TestService.getLocalizedTestNameWithType( test );
//		displayTestName = augmentTestNameWithRange(displayTestName, result);
		
		ResultValidationItem testItem = new ResultValidationItem();

		testItem.setAccessionNumber(accessionNumber);
        testItem.setAnalysis( analysis );
		testItem.setSequenceNumber(sequenceNumber);
		testItem.setTestName(displayTestName);
		testItem.setTestId(test.getId());
		testItem.setAnalysisMethod(analysis.getAnalysisType());
		testItem.setResult(result);
		testItem.setDictionaryResults(getAnyDictonaryValues(testResults));
		testItem.setResultType(getTestResultType(testResults ));
		testItem.setTestSortNumber(test.getSortOrder());
		testItem.setReflexGroup(analysis.getTriggeredReflex());
		testItem.setChildReflex(analysis.getTriggeredReflex() && isConclusion(result, analysis));
		testItem.setQualifiedDictionaryId(getQualifiedDictionaryId(testResults));
        testItem.setPastNotes( notes );

		return testItem;
	}

	protected final String getQualifiedDictionaryId(List<TestResult> testResults){
	    String qualDictionaryIds = "";
	    for( TestResult testResult : testResults){
			if( testResult.getIsQuantifiable()){
                if( !"".equals(qualDictionaryIds )){
                    qualDictionaryIds += ",";
                }
                qualDictionaryIds += testResult.getValue();
			}
		}
		return  "".equals(qualDictionaryIds) ?  null : "[" + qualDictionaryIds + "]";
	}

	protected final String augmentUOMWithRange(String uom,	Result result) {
        if( result == null){return uom;}
        String range = new ResultService( result ).getDisplayReferenceRange( true );
        uom = StringUtil.blankIfNull( uom );
        return GenericValidator.isBlankOrNull( range ) ? uom : (uom + " ( " + range + " )");
	}

	protected final boolean isConclusion(Result testResult, Analysis analysis) {
		List<Result> results = resultDAO.getResultsByAnalysis(analysis);
		if (results.size() == 1) {
			return false;
		}

		Long testResultId = Long.parseLong(testResult.getId());
		// This based on the fact that the conclusion is always added
		// after the shared result so if there is a result with a larger id
		// then this is not a conclusion
		for (Result result : results) {
			if (Long.parseLong(result.getId()) > testResultId) {
				return false;
			}
		}

		return true;
	}

	protected final List<TestResult> getPossibleResultsForTest(Test test) {
		return testResultDAO.getAllActiveTestResultsPerTest( test );
	}

	protected final List<IdValuePair> getAnyDictonaryValues(List<TestResult> testResults) {
		List<IdValuePair> values = null;
		Dictionary dictionary;

		if (testResults != null && testResults.size() > 0 && ResultType.isDictionaryVariant( testResults.get( 0 ).getTestResultType() )) {
			values = new ArrayList<IdValuePair>();
			values.add(new IdValuePair("0", ""));

			for (TestResult testResult : testResults) {
				// Note: result group use to be a criteria but was removed, if
				// results are not as expected investigate
				if ( ResultType.isDictionaryVariant( testResult.getTestResultType() )) {
					dictionary = dictionaryDAO.getDataForId(testResult.getValue());
					if (dictionary == null) {
					    return null;
					}
				    String displayValue = dictionary.getLocalizedName();

                    if ("unknown".equals(displayValue)) {
                        displayValue = GenericValidator.isBlankOrNull(dictionary.getLocalAbbreviation()) ? dictionary.getDictEntry()
                                : dictionary.getLocalAbbreviation();
                    }
                    values.add(new IdValuePair(testResult.getValue(), displayValue));
				}
			}
		}

		return values;
	}


	protected final String getTestResultType(List<TestResult> testResults) {
		String testResultType = ResultType.NUMERIC.getDBValue();

		if (testResults != null && testResults.size() > 0) {
			testResultType = testResults.get(0).getTestResultType();
		}

		return testResultType;
	}

	public final List<AnalysisItem> testResultListToELISAAnalysisList(List<ResultValidationItem> testResultList, List<Integer> statusList) {
		List<AnalysisItem> analysisItemList = new ArrayList<AnalysisItem>();
		AnalysisItem analysisResultItem = new AnalysisItem();
		String currentAccessionNumber = "";
		String currentInvalidAccessionNumber = "";
		boolean readyForValidation = true;

		for (int i = 0; i < testResultList.size(); i++) {

			ResultValidationItem tResultItem = testResultList.get(i);

			// create new bean
			if (!tResultItem.getAccessionNumber().equals(currentAccessionNumber)) {
				if (tResultItem.getAccessionNumber().equals(currentInvalidAccessionNumber)) {
					continue;
				}
				Sample sample = sampleDAO.getSampleByAccessionNumber(tResultItem.getAccessionNumber());
				if (sampleReadyForValidation(sample)) {
					if (!GenericValidator.isBlankOrNull(analysisResultItem.getFinalResult()) && readyForValidation) {
						analysisItemList.add(analysisResultItem);
					}
					readyForValidation = true;

					analysisResultItem = testResultItemToELISAAnalysisItem(tResultItem);

					currentAccessionNumber = analysisResultItem.getAccessionNumber();
					currentInvalidAccessionNumber = "";
				} else { // record status not valid
					currentInvalidAccessionNumber = tResultItem.getAccessionNumber();
					continue;
				}
				// or just add test result to elisaAlgorithm bean
			} else {
				analysisResultItem.setResult(tResultItem.getResultValue());
				analysisResultItem = addTestResultToELISAAnalysisItem(tResultItem, analysisResultItem);
			}

			if (!GenericValidator.isBlankOrNull(analysisResultItem.getStatusId())
					&& !statusList.contains(Integer.parseInt(analysisResultItem.getStatusId()))) {
				readyForValidation = false;
			}

			String finalResult = checkIfFinalResult(tResultItem.getAnalysis().getId());

			if (!GenericValidator.isBlankOrNull(finalResult)) {
				analysisResultItem.setFinalResult(finalResult);
			}

			// final time through
			if (i == (testResultList.size() - 1) && readyForValidation
					&& !GenericValidator.isBlankOrNull(analysisResultItem.getFinalResult())) {
				analysisItemList.add(analysisResultItem);
			}
		}

		return analysisItemList;
	}

	public final String checkIfFinalResult(String analysisId) {
		String finalResult = null;
		Analysis analysis = new Analysis();
		analysis.setId(analysisId);

		List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
		String conclusion = null;

		if (resultList.size() > 1) {
			for (Result result : resultList) {
				if (result.getAnalyte() != null && result.getAnalyte().getId().equals(CONCLUSION_ID)) {
					conclusion = result.getValue();
				}
			}

		}

		if (conclusion != null) {
			Dictionary dictionary = new Dictionary();
			dictionary.setId(conclusion);
			dictionaryDAO.getData(dictionary);
			finalResult = (dictionary.getDictEntry());
		}

		return finalResult;

	}

	public final AnalysisItem testResultItemToELISAAnalysisItem(ResultValidationItem testResultItem) {
		AnalysisItem elisaResultItem = new AnalysisItem();

		elisaResultItem.setAccessionNumber(testResultItem.getAccessionNumber());
		elisaResultItem.setTestName(testResultItem.getTestName());
		elisaResultItem.setResult(testResultItem.getResultValue());
		elisaResultItem.setSampleGroupingNumber(testResultItem.getSampleGroupingNumber());
		elisaResultItem.setAnalysisId(testResultItem.getAnalysis().getId());
		elisaResultItem.setStatusId(testResultItem.getAnalysis().getStatusId());
		elisaResultItem.setNote(testResultItem.getNote());
		elisaResultItem.setNoteId(testResultItem.getNoteId());
		elisaResultItem.setResultId(testResultItem.getResultId());
		elisaResultItem.setNonconforming(testResultItem.isNonconforming() );

		// elisaResultItem.setResult(testResultItem.getResult().getValue());

		// set elisa test to result
		elisaResultItem = setElisaTestResult(testResultItem.getTestName(), elisaResultItem);

		return elisaResultItem;

	}

	public final AnalysisItem addTestResultToELISAAnalysisItem(ResultValidationItem testResultItem, AnalysisItem eItem) {

		eItem.setAnalysisId(testResultItem.getAnalysis().getId());
		eItem.setStatusId(testResultItem.getAnalysis().getStatusId());
		if( testResultItem.isNonconforming()){
			eItem.setNonconforming(true);
		}
		// set elisa test to result
		eItem = setElisaTestResult(testResultItem.getTestName(), eItem);

		return eItem;

	}

	public final AnalysisItem setElisaTestResult(String testName, AnalysisItem eItem) {
		String result = eItem.getResult();
		String analysisId = eItem.getAnalysisId();

		if (testName.equals("Murex")) {
			eItem.setMurexResult(result);
			eItem.setMurexAnalysisId(analysisId);
		} else if (testName.equals("Integral")) {
			eItem.setIntegralResult(result);
			eItem.setIntegralAnalysisId(analysisId);
		} else if (testName.equals("Vironostika")) {
			eItem.setVironostikaResult(result);
			eItem.setVironostikaAnalysisId(analysisId);
		} else if (testName.equals("Genie II")) {
			eItem.setGenieIIResult(result);
			eItem.setGenieIIAnalysisId(analysisId);
		} else if (testName.equals("Genie II 10")) {
			eItem.setGenieII10Result(result);
			eItem.setGenieII10AnalysisId(analysisId);
		} else if (testName.equals("Genie II 100")) {
			eItem.setGenieII100Result(result);
			eItem.setGenieII100AnalysisId(analysisId);
		} else if (testName.equals("Western Blot 1")) {
			eItem.setWesternBlot1Result(result);
			eItem.setWesternBlot1AnalysisId(analysisId);
		} else if (testName.equals("Western Blot 2")) {
			eItem.setWesternBlot2Result(result);
			eItem.setWesternBlot2AnalysisId(analysisId);
		} else if (testName.equals("p24 Ag")) {
			eItem.setP24AgResult(result);
			eItem.setP24AgAnalysisId(analysisId);
		} else if (testName.equals("Bioline")) {
			eItem.setBiolineResult(result);
			eItem.setBiolineAnalysisId(analysisId);
		} else if (testName.equals("Innolia")) {
			eItem.setInnoliaResult(result);
			eItem.setInnoliaAnalysisId(analysisId);
		}

		return eItem;
	}

	public final List<AnalysisItem> testResultListToAnalysisItemList(List<ResultValidationItem> testResultList) {
		List<AnalysisItem> analysisResultList = new ArrayList<AnalysisItem>();

        /*
        The issue with multiselect results is that each selection is one ResultValidationItem but they all need to
        be condensed into one AnalysisItem.  There is a many to one mapping.  The first multiselect result we have gets rolled into
        one AnalysisItem and the rest are skipped but we want to capture any qualified results
         */
        boolean multiResultEntered = false;
        String currentAccession = null;
        AnalysisItem currentMultiSelectAnalysisItem = null;
		for (ResultValidationItem testResultItem : testResultList) {
            if( !testResultItem.getAccessionNumber().equals(currentAccession)){
                currentAccession = testResultItem.getAccessionNumber();
                currentMultiSelectAnalysisItem = null;
                multiResultEntered = false;
            }
            if( !multiResultEntered){
                AnalysisItem convertedItem = testResultItemToAnalysisItem(testResultItem);
                analysisResultList.add(convertedItem);
                if( ResultType.isMultiSelectVariant( testResultItem.getResultType() )){
                    multiResultEntered = true;
                    currentMultiSelectAnalysisItem = convertedItem;
                }
            }
            if(currentMultiSelectAnalysisItem != null && testResultItem.isHasQualifiedResult() ){
                currentMultiSelectAnalysisItem.setQualifiedResultValue(testResultItem.getQualifiedResultValue());
                currentMultiSelectAnalysisItem.setQualifiedDictionaryId(testResultItem.getQualifiedDictionaryId());
                currentMultiSelectAnalysisItem.setHasQualifiedResult(true);
            }
		}

		return analysisResultList;
	}

	protected final RecordStatus getSampleRecordStatus(Sample sample) {

		List<ObservationHistory> ohList = observationHistoryDAO.getAll(null, sample, SAMPLE_STATUS_OBSERVATION_HISTORY_TYPE_ID);

		if (ohList.isEmpty()) {
			return null;
		}

		return StatusService.getInstance().getRecordStatusForID(ohList.get(0).getValue());
	}

	public final AnalysisItem testResultItemToAnalysisItem(ResultValidationItem testResultItem) {
		AnalysisItem analysisResultItem = new AnalysisItem();
		String testUnits = getUnitsByTestId(testResultItem.getTestId());
		String testName = testResultItem.getTestName();
		String sortOrder = testResultItem.getTestSortNumber();
        Result result = testResultItem.getResult();

        if (result != null && result.getAnalyte() != null 
				&& ANALYTE_CD4_CT_GENERATED_ID.equals(testResultItem.getResult().getAnalyte().getId())) {
			testUnits = "";
			testName = StringUtil.getMessageForKey("result.conclusion.cd4");
			analysisResultItem.setShowAcceptReject(false);
			sortOrder = CD4_COUNT_SORT_NUMBER;
		} else if (testResultItem.getTestName().equals(totalTestName)) {
			analysisResultItem.setShowAcceptReject(false);
			analysisResultItem.setReadOnly(true);
			testUnits = testResultItem.getUnitsOfMeasure();
			analysisResultItem.setIsHighlighted(!"100.0".equals(testResultItem.getResult().getValue()));
		}

		testUnits = augmentUOMWithRange(testUnits,	testResultItem.getResult());

		analysisResultItem.setAccessionNumber(testResultItem.getAccessionNumber());
		analysisResultItem.setTestName(testName);
		analysisResultItem.setUnits(testUnits);
		analysisResultItem.setAnalysisId(testResultItem.getAnalysis().getId());
		analysisResultItem.setPastNotes(testResultItem.getPastNotes());
		analysisResultItem.setResultId(testResultItem.getResultId());
		analysisResultItem.setResultType(testResultItem.getResultType());
		analysisResultItem.setTestId(testResultItem.getTestId());
		analysisResultItem.setTestSortNumber(sortOrder);
		analysisResultItem.setDictionaryResults(testResultItem.getDictionaryResults());
		analysisResultItem.setDisplayResultAsLog(TestIdentityService.isTestNumericViralLoad(testResultItem.getTestId()));
        if( result != null){
            if( ResultType.isMultiSelectVariant( testResultItem.getResultType() ) ){
                analysisResultItem.setMultiSelectResultValues( new AnalysisService( testResultItem.getAnalysis() ).getJSONMultiSelectResults() );
            }else{
                analysisResultItem.setResult( getFormattedResult( testResultItem ) );
            }

            if( ResultType.NUMERIC.matches( testResultItem.getResultType() )){
                analysisResultItem.setSignificantDigits( result.getMinNormal().equals( result.getMaxNormal())? -1 : result.getSignificantDigits());
            }
        }
		analysisResultItem.setReflexGroup(testResultItem.isReflexGroup());
		analysisResultItem.setChildReflex(testResultItem.isChildReflex());
		analysisResultItem.setNonconforming( testResultItem.isNonconforming() ||
                StatusService.getInstance().matches( testResultItem.getAnalysis().getStatusId(), AnalysisStatus.TechnicalRejected ));
		analysisResultItem.setQualifiedDictionaryId(testResultItem.getQualifiedDictionaryId());
		analysisResultItem.setQualifiedResultValue(testResultItem.getQualifiedResultValue());
        analysisResultItem.setQualifiedResultId(testResultItem.getQualificationResultId());
        analysisResultItem.setHasQualifiedResult( testResultItem.isHasQualifiedResult() );
        
        boolean withInRange = true;
        boolean outOfRangeLow = false;
        boolean outOfRangeHigh = false;
        Test test = testDAO.getTestById(testResultItem.getTestId());
        Patient patient = getPatientByAccessionNumber(testResultItem.getAccessionNumber());
        if (test != null && patient != null) {
            ResultLimit resultLimit = new ResultLimitService().getResultLimitForTestAndPatient(test, patient);
            if (resultLimit != null) {
                if (Double.parseDouble(getFormattedResult( testResultItem )) < resultLimit.getLowNormal()) {
                	outOfRangeLow = true;
                    withInRange = false;
                } else if(Double.parseDouble(getFormattedResult( testResultItem )) > resultLimit.getHighNormal()) {
                    outOfRangeHigh = true;
                    withInRange = false;
                }
            }
        }
        analysisResultItem.setWithInRange(withInRange);
        analysisResultItem.setOutOfRangeLow(outOfRangeLow);
        analysisResultItem.setOutOfRangeHigh(outOfRangeHigh);

		return analysisResultItem;

	}
	
	private Patient getPatientByAccessionNumber(String accessionNumber) {
        SampleDAO sampleDAO = new SampleDAOImpl();
        Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);

        if (sample != null && !GenericValidator.isBlankOrNull(sample.getId())) {
            SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
            return sampleHumanDAO.getPatientForSample(sample);
        }

        return new Patient();
    }

	protected final String getFormattedResult(ResultValidationItem testResultItem) {
        String result = testResultItem.getResult().getValue();
		if( TestIdentityService.isTestNumericViralLoad(testResultItem.getTestId()) && !GenericValidator.isBlankOrNull(result)){
			return result.split("\\(")[0].trim();
		}else{
            return new ResultService(testResultItem.getResult()).getResultValue( false );
        }
	}

	public final String getUnitsByTestId(String testId) {

		String uomName = null;

		if (testId != null) {
			uomName = testIdToUnits.get(testId);
			if (uomName == null) {
				Test test = new Test();
				test.setId(testId);
				test = testDAO.getTestById(test);

				if (test.getUnitOfMeasure() != null) {
					uomName = test.getUnitOfMeasure().getName();
					testIdToUnits.put(testId, uomName);
				} else {
					testIdToUnits.put(testId, "");
				}
			}
		}
		return uomName;

	}

	public final String getTestSectionId(String testSectionName) {
		TestSection testSection = new TestSection();
		testSection.setTestSectionName(testSectionName);
		testSection = testSectionDAO.getTestSectionByName(testSection);

		return testSection == null ? "0" : testSection.getId();
	}

	protected final String getTestId(String testName) {
		Test test = new Test();
		test.setTestName(testName);
		test = testDAO.getTestByName(test);

		return test.getId();
	}



}
