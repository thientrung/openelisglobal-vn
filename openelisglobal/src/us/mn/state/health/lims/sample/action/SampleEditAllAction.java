package us.mn.state.health.lims.sample.action;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.address.dao.AddressPartDAO;
import us.mn.state.health.lims.address.dao.PersonAddressDAO;
import us.mn.state.health.lims.address.daoimpl.AddressPartDAOImpl;
import us.mn.state.health.lims.address.daoimpl.PersonAddressDAOImpl;
import us.mn.state.health.lims.address.valueholder.AddressPart;
import us.mn.state.health.lims.address.valueholder.PersonAddress;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.services.IPatientService;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.SampleService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.XMLUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patienttype.dao.PatientPatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientPatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.valueholder.PatientType;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.sample.bean.SampleEditItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleTestDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleTestDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleTest;
import us.mn.state.health.lims.userrole.daoimpl.UserRoleDAOImpl;

public class SampleEditAllAction extends BaseAction {
	private static final TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
	private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static final UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
	private static final SampleEditItemComparator testComparator = new SampleEditItemComparator();
	private static final Set<Integer> excludedAnalysisStatusList;
	private static final Set<Integer> ENTERED_STATUS_SAMPLE_LIST = new HashSet<Integer>();
	private static final Collection<String> ABLE_TO_CANCEL_ROLE_NAMES = new ArrayList<String>();
	private static PatientDAO patientDAO = new PatientDAOImpl();
	private static PersonAddressDAO addressDAO = new PersonAddressDAOImpl();
	private static String ADDRESS_PART_VILLAGE_ID;
	private static String ADDRESS_PART_COMMUNE_ID;
	private static String ADDRESS_PART_DEPT_ID;
	private static String ADDRESS_PART_WARD_ID;
	private static String ADDRESS_PART_DISTRICT_ID;
	private static Set<SystemUserSection> userSection;

	static {
		AddressPartDAO addressPartDAO = new AddressPartDAOImpl();
		List<AddressPart> partList = addressPartDAO.getAll();

		for (AddressPart addressPart : partList) {
			if ("department".equals(addressPart.getPartName())) {
				ADDRESS_PART_DEPT_ID = addressPart.getId();
			} else if ("commune".equals(addressPart.getPartName())) {
				ADDRESS_PART_COMMUNE_ID = addressPart.getId();
			} else if ("village".equals(addressPart.getPartName())) {
				ADDRESS_PART_VILLAGE_ID = addressPart.getId();
			} else if ("ward".equals(addressPart.getPartName())) {
				ADDRESS_PART_WARD_ID = addressPart.getId();
			} else if ("district".equals(addressPart.getPartName())) {
				ADDRESS_PART_DISTRICT_ID = addressPart.getId();
			}
		}
	}

	private boolean isEditable = false;
	private String maxAccessionNumber;

	static {
		excludedAnalysisStatusList = new HashSet<Integer>();
		excludedAnalysisStatusList.add(Integer.parseInt(StatusService
				.getInstance().getStatusID(AnalysisStatus.ReferredIn)));
		excludedAnalysisStatusList.add(Integer.parseInt(StatusService
				.getInstance().getStatusID(AnalysisStatus.Canceled)));

		ENTERED_STATUS_SAMPLE_LIST.add(Integer.parseInt(StatusService
				.getInstance().getStatusID(SampleStatus.Entered)));
		ABLE_TO_CANCEL_ROLE_NAMES.add("Validator");
		ABLE_TO_CANCEL_ROLE_NAMES.add("Validation");
		ABLE_TO_CANCEL_ROLE_NAMES.add("Biologist");
	}

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = "success";

		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		String accessionNumber = request.getParameter("accessionNumber");
		boolean allowedToCancelResults = userModuleDAO.isUserAdmin(request);
		userSection = getUserSectionForView();

		if (GenericValidator.isBlankOrNull(accessionNumber)) {
			accessionNumber = getMostRecentAccessionNumberForPaitient(request
					.getParameter("patientID"));
		}

		dynaForm.initialize(mapping);

		isEditable = "readwrite".equals(request.getSession().getAttribute(
				IActionConstants.SAMPLE_EDIT_WRITABLE))
				|| "readwrite".equals(request.getParameter("type"));
		PropertyUtils.setProperty(dynaForm, "isEditable", isEditable);
		
		// Trung Add for keep value after search
        String criteria= request.getParameter("criteria");
        if(!GenericValidator.isBlankOrNull(criteria)){
            request.setAttribute(IActionConstants.CIRITERIA, criteria);
        }else{
            request.setAttribute(IActionConstants.CIRITERIA, "");
        }
        
		if (!GenericValidator.isBlankOrNull(accessionNumber)) {

			PropertyUtils.setProperty(dynaForm, "accessionNumber",
					accessionNumber);
			PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.TRUE);

			Sample sample = getSample(accessionNumber);
			// part sample onset of date Dung add
			if (sample != null) {
				if (sample.getOnsetOfDate() != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					String onsetOfDate = dateFormat.format(sample
							.getOnsetOfDate());
					request.setAttribute("onsetOfDate", onsetOfDate);
				}
			}
			if (sample != null
					&& !GenericValidator.isBlankOrNull(sample.getId())) {

				List<SampleItem> sampleItemList = getSampleItems(sample);
				setPatientInfo(dynaForm, sample);
				List<SampleEditItem> currentTestList = getCurrentTestInfo(
						sampleItemList, accessionNumber, allowedToCancelResults);

            	PropertyUtils.setProperty(dynaForm, "existingTests", currentTestList);
                PropertyUtils.setProperty(dynaForm, "testNameList", currentTestList);

                setAddableTestInfo(dynaForm, sampleItemList, accessionNumber);
				setAddableSampleTypes(dynaForm);
				setSampleOrderInfo(dynaForm, sample);
				PropertyUtils.setProperty(dynaForm, "ableToCancelResults",
						hasResults(currentTestList, allowedToCancelResults));
				PropertyUtils.setProperty(dynaForm, "maxAccessionNumber",
						maxAccessionNumber);
				PropertyUtils.setProperty(dynaForm, "isConfirmationSample",
						new SampleService(sample).isConfirmationSample());
				// Dung add
				// Get patient for sample
				PropertyUtils.setProperty(dynaForm, "patientProperties",
						patientManagementInfo(sample));
			} else {
				PropertyUtils.setProperty(dynaForm, "noSampleFound",
						Boolean.TRUE);
			}
		} else {
			PropertyUtils
					.setProperty(dynaForm, "searchFinished", Boolean.FALSE);
			request.getSession().setAttribute(
					IActionConstants.SAMPLE_EDIT_WRITABLE,
					request.getParameter("type"));
		}

		if (FormFields.getInstance().useField(
				FormFields.Field.InitialSampleCondition)) {
			PropertyUtils.setProperty(dynaForm, "initialSampleConditionList",
					DisplayListService
							.getList(ListType.INITIAL_SAMPLE_CONDITION));
		}

		PropertyUtils.setProperty(form, "currentDate",
				DateUtil.getCurrentDateAsText());
		PatientSearch patientSearch = new PatientSearch();
		patientSearch.setLoadFromServerWithPatient(true);
		patientSearch.setSelectedPatientActionButtonText(StringUtil
				.getMessageForKey("label.patient.search.select"));
		PropertyUtils.setProperty(form, "patientSearch", patientSearch);

		// The following are for compatibility with modal version of sample
		// entry
		if (FormFields.getInstance().useField(
				FormFields.Field.SAMPLE_ENTRY_MODAL_VERSION)) {
			PropertyUtils.setProperty(dynaForm, "sampleSources",
					DisplayListService.getList(ListType.SAMPLE_SOURCE));
			PropertyUtils
					.setProperty(
							dynaForm,
							"initConditionFormErrorsList",
							DisplayListService
									.getList(ListType.SAMPLE_ENTRY_INIT_COND_FORM_ERRORS));
			PropertyUtils
					.setProperty(
							dynaForm,
							"initConditionLabelErrorsList",
							DisplayListService
									.getList(ListType.SAMPLE_ENTRY_INIT_COND_LABEL_ERRORS));
			PropertyUtils.setProperty(dynaForm, "initConditionMiscList",
					DisplayListService
							.getList(ListType.SAMPLE_ENTRY_INIT_COND_MISC));
			PropertyUtils
					.setProperty(
							dynaForm,
							"rejectionReasonFormErrorsList",
							DisplayListService
									.getList(ListType.SAMPLE_ENTRY_REJECTION_FORM_ERRORS));
			PropertyUtils
					.setProperty(
							dynaForm,
							"rejectionReasonLabelErrorsList",
							DisplayListService
									.getList(ListType.SAMPLE_ENTRY_REJECTION_LABEL_ERRORS));
			PropertyUtils.setProperty(dynaForm, "rejectionReasonMiscList",
					DisplayListService
							.getList(ListType.SAMPLE_ENTRY_REJECTION_MISC));
		}

		return mapping.findForward(forward);
	}

	private PatientManagementInfo patientManagementInfo(Sample sample) {
		try {
			PatientManagementInfo patientManagementInfo = new PatientManagementInfo();
			// get sample human
			Patient patient = new Patient();
			SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
			patient = sampleHumanDAO.getPatientForSample(sample);
			
			// set information for patient
			patientManagementInfo.setFirstName(patient.getPerson()
					.getFirstName());
			patientManagementInfo.setBirthDateForDisplay(patient
					.getBirthDateForDisplay());
			patientManagementInfo.setStreetAddress(patient.getPerson()
					.getStreetAddress());
			patientManagementInfo.setGender(patient.getGender());
			patientManagementInfo.setExternalId(patient.getExternalId());
			patientManagementInfo.setChartNumber(patient.getChartNumber());
			patientManagementInfo.setAddressWard(getAddress(
					patient.getPerson(), ADDRESS_PART_WARD_ID));
			patientManagementInfo.setAddressDistrict(getAddress(
					patient.getPerson(), ADDRESS_PART_DISTRICT_ID));
			patientManagementInfo.setCity(getAddress(patient.getPerson(),
					ADDRESS_PART_VILLAGE_ID));
			//patientManagementInfo.setAddressDepartment(getAddress(
			//		patient.getPerson(), ADDRESS_PART_DEPT_ID));
			/*Get department for the patient from observation history*/
            if (patient.getId() != null) {
                ObservationHistory observationHistory = new ObservationHistory();
                ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
                   observationHistory = observationHistoryDAO
                           .getObservationHistoriesByPatientIdAndTypeOnlyOne(
                                   patient.getId(), "9");
                   if (observationHistory != null) {
                       patientManagementInfo.setAddressDepartment(observationHistory.getValue());
                   }
            }
			patientManagementInfo.setPatientType(getPatientType(patient));
	         /*Get age unit for the patient from observation history*/
            if (patient.getId() != null) {
                ObservationHistory observationHistory = new ObservationHistory();
                ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
                   observationHistory = observationHistoryDAO
                           .getObservationHistoriesByPatientIdAndTypeOnlyOne(
                                   patient.getId(), "11");
                   if (observationHistory != null) {
                       patientManagementInfo.setPatientAgeUnit( observationHistory.getValue());
                   }
            }
            
			return patientManagementInfo;
			
		} catch (LIMSRuntimeException ex) {
			LogEvent.logError("patientManagementInfo",
					"patientManagementInfo()", ex.getMessage());
			return null;
		}
	}

	private String getWardOfPatient() {
		return "";
	}

	private String getPatientType(Patient patient) {
		PatientPatientTypeDAO patientPatientTypeDAO = new PatientPatientTypeDAOImpl();

		PatientType patientType = patientPatientTypeDAO
				.getPatientTypeForPatient(patient.getId());

		return patientType != null ? patientType.getType() : null;
	}

	private String getAddress(Person person, String addressPartId) {
		if (GenericValidator.isBlankOrNull(addressPartId)) {
			return "";
		}
		PersonAddress address = addressDAO.getByPersonIdAndPartId(
				person.getId(), addressPartId);

		return address != null ? address.getValue() : "";
	}

	private Boolean hasResults(List<SampleEditItem> currentTestList,
			boolean allowedToCancelResults) {
		if (!allowedToCancelResults) {
			return false;
		}

		for (SampleEditItem editItem : currentTestList) {
			if (editItem.isHasResults()) {
				return true;
			}
		}

		return false;
	}

	private void setSampleOrderInfo(DynaActionForm dynaForm, Sample sample)
			throws InvocationTargetException, NoSuchMethodException,
			IllegalAccessException {
		SampleOrderService sampleOrderService = new SampleOrderService(sample);
		PropertyUtils.setProperty(dynaForm, "sampleOrderItems",
				sampleOrderService.getSampleOrderItem());
	}

	private String getMostRecentAccessionNumberForPaitient(String patientID) {
		String accessionNumber = null;
		if (!GenericValidator.isBlankOrNull(patientID)) {
			List<Sample> samples = new SampleHumanDAOImpl()
					.getSamplesForPatient(patientID);

			int maxId = 0;
			for (Sample sample : samples) {
				if (Integer.parseInt(sample.getId()) > maxId) {
					maxId = Integer.parseInt(sample.getId());
					accessionNumber = sample.getAccessionNumber();
				}
			}

		}
		return accessionNumber;
	}

	private Sample getSample(String accessionNumber) {
		SampleDAO sampleDAO = new SampleDAOImpl();
		return sampleDAO.getSampleByAccessionNumber(accessionNumber);
	}

	private List<SampleItem> getSampleItems(Sample sample) {
		SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();

		return sampleItemDAO.getSampleItemsBySampleIdAndStatus(sample.getId(),
				ENTERED_STATUS_SAMPLE_LIST);
	}

	private void setPatientInfo(DynaActionForm dynaForm, Sample sample)
			throws InvocationTargetException, NoSuchMethodException,
			IllegalAccessException {

		Patient patient = new SampleHumanDAOImpl().getPatientForSample(sample);
		IPatientService patientService = new PatientService(patient);

		PropertyUtils.setProperty(dynaForm, "patientName",
				patientService.getLastFirstName());
		PropertyUtils.setProperty(dynaForm, "dob", patientService.getDOB());
		PropertyUtils.setProperty(dynaForm, "gender",
				patientService.getGender());
		PropertyUtils.setProperty(dynaForm, "nationalId",
				patientService.getNationalId());
		PropertyUtils.setProperty(dynaForm, "patientAgeUnit",
				patientService.getPatientAgeUnit());
	}

	private List<SampleEditItem> getCurrentTestInfo(
			List<SampleItem> sampleItemList, String accessionNumber,
			boolean allowedToCancelAll) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		List<SampleEditItem> currentTestList = new ArrayList<SampleEditItem>();

		for (SampleItem sampleItem : sampleItemList) {
			addCurrentTestsToList(sampleItem, currentTestList, accessionNumber,
					allowedToCancelAll);
		}

		return currentTestList;
	}

	private void addCurrentTestsToList(SampleItem sampleItem,
			List<SampleEditItem> currentTestList, String accessionNumber,
			boolean allowedToCancelAll) {

		TypeOfSample typeOfSample = new TypeOfSample();
		typeOfSample.setId(sampleItem.getTypeOfSampleId());
		typeOfSampleDAO.getData(typeOfSample);

		List<Analysis> analysisList = analysisDAO
				.getAnalysesBySampleItemsExcludingByStatusIds(sampleItem,
						excludedAnalysisStatusList);

		List<SampleEditItem> analysisSampleItemList = new ArrayList<SampleEditItem>();

		String collectionDate = DateUtil
				.convertTimestampToStringDate(sampleItem.getCollectionDate());
		String collectionTime = DateUtil
				.convertTimestampToStringTime(sampleItem.getCollectionDate());
		String sampleItemExternalId = sampleItem.getExternalId();
		boolean canRemove = true;
		for (Analysis analysis : analysisList) {
		    for (SystemUserSection systemUserSection : userSection) {
                if (systemUserSection.getTestSection().getId().equals(analysis.getTestSection().getId()) && "Y".equals(systemUserSection.getHasView())) {
        		    SampleEditItem sampleEditItem = new SampleEditItem();
        
        			sampleEditItem.setTestId(analysis.getTest().getId());
        			sampleEditItem.setTestName(TestService
        					.getUserLocalizedTestName(analysis.getTest()));
        			sampleEditItem.setSampleItemId(sampleItem.getId());
        
        			boolean canCancel = allowedToCancelAll || "Y".equals(systemUserSection.getHasCancel());
        					//|| (!StatusService.getInstance().matches(analysis.getStatusId(), AnalysisStatus.Canceled) &&
        					//StatusService.getInstance().matches(analysis.getStatusId(), AnalysisStatus.NotStarted));
        
        			if (!canCancel) {
        				canRemove = false;
        			}
        			sampleEditItem.setCanCancel(canCancel);
        			sampleEditItem.setAnalysisId(analysis.getId());
        			sampleEditItem.setStatus(StatusService.getInstance()
        					.getStatusNameFromId(analysis.getStatusId()));
        			sampleEditItem.setSortOrder(analysis.getTest().getSortOrder());
        			sampleEditItem.setHasResults(!StatusService.getInstance().matches(
        					analysis.getStatusId(), AnalysisStatus.NotStarted));
        
        			analysisSampleItemList.add(sampleEditItem);
                }
		    }
		}

		if (!analysisSampleItemList.isEmpty()) {
			Collections.sort(analysisSampleItemList, testComparator);
			SampleEditItem firstItem = analysisSampleItemList.get(0);
			
			firstItem.setAccessionNumber(accessionNumber + "-"
					+ sampleItem.getSortOrder());
			firstItem.setSampleType(typeOfSample.getLocalizedName());
			firstItem.setSampleItemExternalId(sampleItemExternalId);
			firstItem.setCanRemoveSample(canRemove);
			firstItem.setCollectionDate(collectionDate == null ? ""
					: collectionDate);
			firstItem.setCollectionTime(collectionTime);
			maxAccessionNumber = analysisSampleItemList.get(0)
					.getAccessionNumber();
			currentTestList.addAll(analysisSampleItemList);
		}
	}

	private void setAddableTestInfo(DynaActionForm dynaForm,
			List<SampleItem> sampleItemList, String accessionNumber)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		List<SampleEditItem> possibleTestList = new ArrayList<SampleEditItem>();

		for (SampleItem sampleItem : sampleItemList) {
			addPossibleTestsToList(sampleItem, possibleTestList,
					accessionNumber);
		}

		PropertyUtils.setProperty(dynaForm, "possibleTests", possibleTestList);
		PropertyUtils.setProperty(dynaForm, "testSectionList",
				DisplayListService.getList(ListType.TEST_SECTION));
	}

	private void setAddableSampleTypes(DynaActionForm dynaForm)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		PropertyUtils.setProperty(dynaForm, "sampleTypes",
				DisplayListService.getList(ListType.SAMPLE_TYPE_ACTIVE));
	}

	private void addPossibleTestsToList(SampleItem sampleItem,
			List<SampleEditItem> possibleTestList, String accessionNumber) {

		TypeOfSample typeOfSample = new TypeOfSample();
		typeOfSample.setId(sampleItem.getTypeOfSampleId());
		typeOfSampleDAO.getData(typeOfSample);

		TestDAO testDAO = new TestDAOImpl();
		Test test = new Test();

		TypeOfSampleTestDAO sampleTypeTestDAO = new TypeOfSampleTestDAOImpl();
		List<TypeOfSampleTest> typeOfSampleTestList = sampleTypeTestDAO
				.getTypeOfSampleTestsForSampleType(typeOfSample.getId());
		List<SampleEditItem> typeOfTestSampleItemList = new ArrayList<SampleEditItem>();

		for (TypeOfSampleTest typeOfSampleTest : typeOfSampleTestList) {
			SampleEditItem sampleEditItem = new SampleEditItem();

			sampleEditItem.setTestId(typeOfSampleTest.getTestId());
			test.setId(typeOfSampleTest.getTestId());
			testDAO.getData(test);
			if ("Y".equals(test.getIsActive()) && test.getOrderable()) {
				sampleEditItem.setTestName(TestService
						.getUserLocalizedTestName(test));
				sampleEditItem.setSampleItemId(sampleItem.getId());
				sampleEditItem.setSortOrder(test.getSortOrder());
				typeOfTestSampleItemList.add(sampleEditItem);
			}
		}

		if (!typeOfTestSampleItemList.isEmpty()) {
			Collections.sort(typeOfTestSampleItemList, testComparator);

			typeOfTestSampleItemList.get(0).setAccessionNumber(
					accessionNumber + "-" + sampleItem.getSortOrder());
			typeOfTestSampleItemList.get(0).setSampleType(
					typeOfSample.getLocalizedName());

			possibleTestList.addAll(typeOfTestSampleItemList);
		}

	}

	protected String getPageTitleKey() {
		return isEditable ? StringUtil
				.getContextualKeyForKey("sample.edit.title") : StringUtil
				.getContextualKeyForKey("sample.view.title");
	}

	protected String getPageSubtitleKey() {
		return isEditable ? StringUtil
				.getContextualKeyForKey("sample.edit.subtitle") : StringUtil
				.getContextualKeyForKey("sample.view.subtitle");
	}

	private static class SampleEditItemComparator implements
			Comparator<SampleEditItem> {

		public int compare(SampleEditItem o1, SampleEditItem o2) {
			if (GenericValidator.isBlankOrNull(o1.getSortOrder())
					|| GenericValidator.isBlankOrNull(o2.getSortOrder())) {
				return o1.getTestName().compareTo(o2.getTestName());
			}

			try {
				return Integer.parseInt(o1.getSortOrder())
						- Integer.parseInt(o2.getSortOrder());
			} catch (NumberFormatException e) {
				return o1.getTestName().compareTo(o2.getTestName());
			}
		}

	}
}
