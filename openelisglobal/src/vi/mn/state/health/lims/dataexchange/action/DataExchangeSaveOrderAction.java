/**
 * 
 */
package vi.mn.state.health.lims.dataexchange.action;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NumberUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Transaction;

import us.mn.state.health.lims.address.dao.PersonAddressDAO;
import us.mn.state.health.lims.address.daoimpl.PersonAddressDAOImpl;
import us.mn.state.health.lims.address.valueholder.PersonAddress;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.provider.validation.YearSiteNumAccessionValidator;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.ValidateDataWebService;
import vi.mn.state.health.lims.dataexchange.dao.IParameterTestWebservice;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;
import vi.mn.state.health.lims.dataexchange.valueholder.TestResultVO;
import vi.mn.state.health.lims.etor.data.dao.EtorDataMappingDAO;
import vi.mn.state.health.lims.etor.data.daoimpl.EtorDataMappingDAOImpl;
import vi.mn.state.health.lims.etor.data.valueholder.EtorDataMapping;

/**
 * @author dungtdo.sl /addOrderWebService (action)
 */
public class DataExchangeSaveOrderAction extends Action implements
		IActionConstants, IParameterTestWebservice {
	OrderVO orderVO = null;

	/**
	 * 
	 */
	public DataExchangeSaveOrderAction() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.
	 * ActionMapping, org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// show message when error
		//ActionMessages errors = new ActionMessages();
		// add sample
		// + validate Sample
		createNewDataOrderVO();
		Map<String, String> errors=new HashMap<String, String>();
		errors = ValidateDataWebService.validateOrderData(orderVO);
		if (errors.size() > 0) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			return mapping.findForward(FWD_FAIL);
		}
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		try {
			orderVO.getSample().setId(addSample(orderVO));
			// add doctor
			orderVO.getProvider().setSelectedPersonId(
					addPersion(orderVO.getProvider().getPerson()));
			orderVO.getProvider().setId(addProvider(orderVO));
			// add patient
			orderVO.getPatient().getPersonPatient()
					.setId(addPersion(orderVO.getPatient().getPerson()));
			orderVO.getPatient().setId(addPatient(orderVO));
			// add observation for sample
			addPatientObservationHistory(orderVO);
			// Add Stress,ward,district,city for patient
			addPersionAddresForpatient(orderVO);
			// add sample human
			addSampleHuman(orderVO);
			sampleItem(orderVO);
			tx.commit();
		} catch (LIMSRuntimeException lre) {
			tx.rollback();
			return mapping.findForward(FWD_FAIL);
		}
		return mapping.findForward("success");
	}

	/**
	 * data test
	 */
	private void createNewDataOrderVO() {
		YearSiteNumAccessionValidator yearSiteNumAccessionValidator=new YearSiteNumAccessionValidator();
		orderVO = new OrderVO();
		orderVO.setEtorId(ETOR_ID);
		orderVO.getSample().setAccessionNumber(yearSiteNumAccessionValidator.getNextAvailableAccessionNumber(null));
		orderVO.getSample().setReceivedDate(
				new Date(new java.util.Date(REC_DATE).getTime()));
		orderVO.getSample().setCollectionDate(
				new Timestamp(new java.util.Date(COLL_DATE).getTime()));
		// need
		orderVO.getSample().setEnteredDate(
				new Date(new java.util.Date().getTime()));
		orderVO.getSample().setSysUserId(orderVO.getEtorId());
		orderVO.getSample().setStatusId(SAMPLE_STATUS_ID);
		// end need
		Person person = new Person();
		person.setSysUserId(orderVO.getEtorId());
		person.setFirstName(PERSION_FIRSTNAME_DOCTOR);
		orderVO.getProvider().setSysUserId(orderVO.getEtorId());
		orderVO.getProvider().setPerson(person);
		orderVO.setDiagnosis(PATIENT_DIAGNOSIS);
		orderVO.setTypeOfSampleId(TYPE_OF_SAMPLE_ID);// mau
		person = new Person();
		person.setSysUserId(orderVO.getEtorId());
		person.setFirstName(PERSION_FIRSTNAME_PATIENT);
		person.setStreetAddress(PATIENT_STREET_ADDRESS);
		orderVO.getPatient().setSysUserId(orderVO.getEtorId());
		orderVO.getPatient().setPerson(person);
		orderVO.getPatient()
				.setBirthDate(
						new Timestamp(new java.util.Date(PATIENT_BIRTH_DATE)
								.getTime()));
		orderVO.getPatient().setBirthDateForDisplay(PATIENT_BIRTH_DATE);
		orderVO.setAge(PATIENT_AGE);
		orderVO.getPatient().setGender(PATIENT_GENDER);
		orderVO.setWard(PATIENT_WARD);
		orderVO.setDistrict(PATIENT_DISTRICT);
		orderVO.setCity(PATIENT_CITY);
		TypeOfSample typeOfSample = new TypeOfSample();
		TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
		typeOfSample = typeOfSampleDAO.getTypeOfSampleById(String
				.valueOf(TYPE_OF_SAMPLE_ID));
		List<TypeOfSample> lst = new ArrayList<TypeOfSample>();
		lst.add(typeOfSample);
		orderVO.setTypeOfSample(lst);
		// put test id when get from web service
		
		orderVO.getMapTest().put(TEST_ID_1, String.valueOf(TYPE_OF_SAMPLE_ID));
		//orderVO.getMapTest().put(TEST_ID_2, String.valueOf(TYPE_OF_SAMPLE_ID));
		
		
		// if have many result 
		TestResultVO testResultVO = new TestResultVO(TEST_ID_1,
				TEST_RESULT_ID_1);
		orderVO.getLiTestResultVO().add(testResultVO);
		//testResultVO = new TestResultVO(TEST_ID_1, "33/66");
		//orderVO.getLiTestResultVO().add(testResultVO);

		//testResultVO = new TestResultVO(TEST_ID_2, TEST_RESULT_ID_2);
		//orderVO.getLiTestResultVO().add(testResultVO);
		// set testSelect
		TestSection t = new TestSection();
		TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
		orderVO.setTestSection(testSectionDAO
				.getTestSectionById(TEST_SELECTION_BY_ID));
	}

	/**
	 * Add New sample get data from web service
	 */
	private String addSample(OrderVO orderVO) {
		SampleDAO sampleDAO = new SampleDAOImpl();
		// add new sample
		return sampleDAO.insertDataSampleWithAccessionNumber(orderVO
				.getSample());
	}

	private String addPersion(Person person) {
		PersonDAO personDAO = new PersonDAOImpl();
		return personDAO.insertDataWS(person);
	}

	private String addProvider(OrderVO orderVO) {
		if(orderVO.getProvider().getPerson().getFirstName()!=null){
		ProviderDAO providerDAO = new ProviderDAOImpl();
		return providerDAO.insertDataWS(orderVO.getProvider());
		}else return "";
	}

    private String addPatient(OrderVO orderVO) {
    	String patientId="";
		PatientDAO patientDAO = new PatientDAOImpl();
		if(orderVO.getPatient().getId() == null){
			patientId= patientDAO.insertDataWS(orderVO.getPatient());
		}else{
			//patientDAO.updateData(orderVO.getPatient());
			patientId= orderVO.getPatient().getId();
		}
		return patientId;
	}

	/**
	 * 
	 */
	private String addPatientObservationHistory(OrderVO orderVO) {
		ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
		if (!orderVO.getDiagnosis().equals("")
				&& orderVO.getDiagnosis() != null) {
			ObservationHistory observationHistory = new ObservationHistory();
			observationHistory.setSysUserId(orderVO.getEtorId());
			observationHistory
					.setObservationHistoryTypeId(OBSERVATION_HISTORY_TYPE_DIAGNOSIS_ID);
			observationHistory.setPatientId(orderVO.getPatient().getId());
			observationHistory.setSampleId(orderVO.getSample().getId());
			observationHistory.setValueType(OBSERVATION_VALUE_TYPE);
			observationHistory.setValue(orderVO.getDiagnosis());
			observationHistoryDAO.insertDataWS(observationHistory);
		}
		if (orderVO.getAge() != null && !orderVO.getAge().equals("")) {
			observationHistoryDAO = new ObservationHistoryDAOImpl();
			ObservationHistory observationHistory = new ObservationHistory();
			observationHistory.setSysUserId(orderVO.getEtorId());
			observationHistory
					.setObservationHistoryTypeId(OBSERVATION_HISTORY_TYPE_AGE_ID);
			observationHistory.setPatientId(orderVO.getPatient().getId());
			observationHistory.setSampleId(orderVO.getSample().getId());
			observationHistory.setValueType(OBSERVATION_VALUE_TYPE);
			observationHistory.setValue(orderVO.getAge());

			observationHistoryDAO.insertDataWS(observationHistory);
		}
		return FWD_SUCCESS;
	}

	/**
	 * Add Stress,ward,district,city for patient
	 */
	private String addPersionAddresForpatient(OrderVO orderVO) {
		PersonAddressDAO personAddressDAO = new PersonAddressDAOImpl();
		PersonAddress personAddress = new PersonAddress();
		if (orderVO.getCity() != null && !orderVO.getCity().equals("")) {
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_CITY);
			personAddress.setType(TYPE_DICTIONARY);
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getCity());
			personAddressDAO.insert(personAddress);
		}
		if (orderVO.getDistrict() != null && !orderVO.getDistrict().equals("")) {
			personAddressDAO = new PersonAddressDAOImpl();
			personAddress = new PersonAddress();
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_DISTRICT);
			personAddress.setType(TYPE_DICTIONARY);
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getDistrict());
			personAddressDAO.insert(personAddress);
		}
		if (orderVO.getWard() != null && !orderVO.getWard().equals("")) {
			personAddressDAO = new PersonAddressDAOImpl();
			personAddress = new PersonAddress();
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_WARD);
			personAddress.setType(TYPE_A);
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getWard());
			personAddressDAO.insert(personAddress);
		}
		return "";
	}

	private boolean addSampleHuman(OrderVO orderVO) {
		SampleHuman sampleHuman = new SampleHuman();
		sampleHuman.setSysUserId(orderVO.getEtorId());
		sampleHuman.setSampleId(orderVO.getSample().getId());
		sampleHuman.setProviderId(orderVO.getProvider().getId());
		sampleHuman.setPatientId(orderVO.getPatient().getId());
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		return sampleHumanDAO.insertData(sampleHuman);
	}

	private String sampleItem(OrderVO orderVO) {
		// get all list test
		int i = 0;
		for (TypeOfSample type : orderVO.getTypeOfSample()) {
			i++;
			// TypeOfSampleDAO typeOfSample=new TypeOfSampleDAOImpl();
			// add new all sample of
			SampleItem firstSampleItem = new SampleItem();
			firstSampleItem.setSysUserId(orderVO.getEtorId());
			// sampleItem.setTypeOfSample(new
			// TypeOfSample(typeOfSample.getTypeOfSampleById(type.getId())));
			firstSampleItem.setSample(orderVO.getSample());
			firstSampleItem.setTypeOfSample(orderVO.getTypeOfSample().get(0));
			firstSampleItem.setSortOrder(String.valueOf(i));
			firstSampleItem.setLastupdated(new Timestamp(new java.util.Date()
					.getTime()));
			firstSampleItem.setStatusId(SAMPLE_ITEM_STATUS_ID);
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();

			firstSampleItem.setId(sampleItemDAO.insertDataWS(firstSampleItem));
			orderVO.getLiSampleItem().add(firstSampleItem);
			// add data etor
			//addEtorDataMapping(orderVO, firstSampleItem);
			 
			addAnalysis(orderVO, type, firstSampleItem, true);
			// add analysis for LIS add result
			//sampleItem.setId(sampleItemDAO.insertDataWS(sampleItem));
			
			// Add the second sample item and use this one for etor data mapping
			try {
				SampleItem secondSampleItem = (SampleItem) firstSampleItem.clone();
				secondSampleItem.setSortOrder(String.valueOf(Integer.valueOf(firstSampleItem.getSortOrder()).intValue() + 1));
				firstSampleItem.setId(sampleItemDAO.insertDataWS(secondSampleItem));
				orderVO.getLiSampleItem().add(firstSampleItem);
				addEtorDataMapping(orderVO, secondSampleItem);
				addAnalysis(orderVO, type, secondSampleItem, false);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		}
		return FWD_SUCCESS;
	}

	private String addAnalysis(OrderVO orderVO, TypeOfSample typeOfSample,
			SampleItem sampleItem, boolean flag) {
		// get test from SET of order web service
		orderVO.getLiTest().clear();
		for (Entry<String, String> key : orderVO.getMapTest().entrySet()) {
			// if type of sample equal type of sample of test the
			if (typeOfSample.getId().equals(key.getValue())) {
				Test test = new Test();
				TestDAO testDAO = new TestDAOImpl();
				test = testDAO.getTestById(key.getKey());
				orderVO.getLiTest().add(test);
			}
		}
		// add analysis when have test and sample item
		for (Test test : orderVO.getLiTest()) {
			Analysis analysis = new Analysis();
			analysis.setSysUserId(orderVO.getEtorId());
			analysis.setSampleItem(sampleItem);
			analysis.setTestSection(test.getTestSection());
			analysis.setTest(test);
			analysis.setAnalysisType(IActionConstants.ANALYSIS_TYPE_MANUAL);
			if(flag==true){
				analysis.setStatusId(ANALYSIS_STATUS_ID);
			}else{
				analysis.setStatusId(ANALYSIS_STATUS_ID_YET);
			}
			analysis.setSampleTypeName(typeOfSample.getDescription());
			AnalysisDAO analysisDAO = new AnalysisDAOImpl();
			analysis.setId(analysisDAO.insertDataWS(analysis, false));
			// if flag =true then add result from etor else not have result
			if (flag == true) {
				// if have result child get rchild
				Result resultchild = new Result();
				// get result child
				if (orderVO.getLiTestResultVO().size() >= 2) {
					for (TestResultVO testResultVO : orderVO
							.getLiTestResultVO()) {
						if (NumberUtils.stringToInt(testResultVO.getResult()) == 0
								&& testResultVO.getTestId()
										.equals(test.getId())) {
							resultchild.setAnalysis(analysis);
							resultchild.setSysUserId(orderVO.getEtorId());
							resultchild.setIsReportable(IS_REPORTABLE);
							resultchild.setResultType(TYPE_A);
							resultchild.setValue(testResultVO.getResult());
							resultchild.setSortOrder(SORT_ORDER);
							resultchild.setSignificantDigits(SIGN_IF_CANTDIGIT);

						}
					}

				}

				// check if have result then insert resource
				Result result = new Result();
				ResultDAO resultDAO = new ResultDAOImpl();
				for (TestResultVO testResultVO : orderVO.getLiTestResultVO()) {
					if (test.getId().equals(testResultVO.getTestId())
							&& NumberUtils
									.stringToInt(testResultVO.getResult()) > 0) {

						TestResultDAO testResultDAO = new TestResultDAOImpl();
						TestResult testResult = new TestResult();
						// check if test result is true

						testResult.setId(testResultVO.getResult());
						testResult = testResultDAO
								.getTestResultById(testResult);
						// if test reslut not null then save test result with
						// analysis
						if (testResult.getValue() != null) {
							result.setSysUserId(orderVO.getEtorId());
							result.setAnalysis(analysis);
							result.setIsReportable(IS_REPORTABLE);
							result.setResultType(testResult.getTestResultType());
							result.setValue(testResult.getValue());
							result.setSortOrder(SORT_ORDER);
							result.setTestResultId(testResult.getId());
							result.setSignificantDigits(SIGN_IF_CANTDIGIT);
							result.setId(resultDAO.insertDataWS(result));
						}
					}// end if
					if (test.getId().equals(testResultVO.getTestId())
							&& NumberUtils
									.stringToInt(testResultVO.getResult()) == 0) {
						resultchild.setParentResult(result);
						resultDAO.insertDataWS(resultchild);
						resultchild = new Result();
					}
				}
			}

		}

		return FWD_SUCCESS;
	}

	/**
	 * @param orderVO
	 * @param sampleItem
	 *            success if insert data success
	 */
	private boolean addEtorDataMapping(OrderVO orderVO, SampleItem sampleItem) {
		EtorDataMappingDAO etorDataMappingDAO = new EtorDataMappingDAOImpl();
		EtorDataMapping etorDataMapping = new EtorDataMapping();
		etorDataMapping.setSample(orderVO.getSample());
		etorDataMapping.setSampleItem(sampleItem);
		etorDataMapping.setLisStatus(Integer.parseInt(ANALYSIS_STATUS_ID));
		etorDataMapping.setEtorStatus(Integer.parseInt(ANALYSIS_STATUS_ID));
		etorDataMapping.setEtorUserId(orderVO.getEtorId());
		return etorDataMappingDAO.insertData(etorDataMapping);
	}

}
