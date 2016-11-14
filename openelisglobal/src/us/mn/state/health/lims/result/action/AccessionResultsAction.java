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
package us.mn.state.health.lims.result.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.services.IPatientService;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.inventory.action.InventoryUtility;
import us.mn.state.health.lims.inventory.form.InventoryKitItem;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.requester.dao.SampleRequesterDAO;
import us.mn.state.health.lims.requester.daoimpl.SampleRequesterDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.result.action.util.ResultsLoadUtility;
import us.mn.state.health.lims.result.action.util.ResultsPaging;
import us.mn.state.health.lims.role.daoimpl.RoleDAOImpl;
import us.mn.state.health.lims.role.valueholder.Role;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleproject.dao.SampleProjectDAO;
import us.mn.state.health.lims.sampleproject.daoimpl.SampleProjectDAOImpl;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.test.beanItems.TestResultItem;
import us.mn.state.health.lims.userrole.dao.UserRoleDAO;
import us.mn.state.health.lims.userrole.daoimpl.UserRoleDAOImpl;

public class AccessionResultsAction extends BaseAction {

	private String accessionNumber;
	private Sample sample;
	private InventoryUtility inventoryUtility = new InventoryUtility();
	private static SampleDAO sampleDAO = new SampleDAOImpl();
	private static UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
	private static String RESULT_EDIT_ROLE_ID;
	
	static{
		Role editRole = new RoleDAOImpl().getRoleByName("Results modifier");
		
		if( editRole != null){
			RESULT_EDIT_ROLE_ID = editRole.getId();
		}
	}

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;
		PropertyUtils.setProperty(dynaForm, "referralReasons", DisplayListService.getList( DisplayListService.ListType.REFERRAL_REASONS));
        PropertyUtils.setProperty( dynaForm, "rejectReasons", DisplayListService.getNumberedListWithLeadingBlank( DisplayListService.ListType.REJECTION_REASONS ) );

		ResultsPaging paging = new ResultsPaging();
		String newPage = request.getParameter("page");
		if (GenericValidator.isBlankOrNull(newPage)) {

			accessionNumber = request.getParameter("accessionNumber");
			PropertyUtils.setProperty(dynaForm, "displayTestKit", false);

			if (!GenericValidator.isBlankOrNull(accessionNumber)) {
				ResultsLoadUtility resultsUtility = new ResultsLoadUtility(currentUserId);
				//This is for Haiti_LNSP if it gets more complicated use the status set stuff
				resultsUtility.addExcludedAnalysisStatus(AnalysisStatus.ReferredIn);
				resultsUtility.addExcludedAnalysisStatus(AnalysisStatus.Canceled);

				resultsUtility.setLockCurrentResults(modifyResultsRoleBased() && userNotInRole(request));
				ActionMessages errors = new ActionMessages();
				errors = validateAll(request, errors, dynaForm);

				if (errors != null && errors.size() > 0) {
					saveErrors(request, errors);
					request.setAttribute(ALLOW_EDITS_KEY, "false");

					setEmptyResults(dynaForm);

					return mapping.findForward(FWD_FAIL);
				}
				PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.TRUE);

				getSample();

				if (!GenericValidator.isBlankOrNull(sample.getId())) {
					PropertyUtils.setProperty(dynaForm, "receivedDate", sample.getReceivedDateForDisplay());
					Patient patient = getPatient();
					// Get patient information
					if (patient != null) {
						IPatientService patientService = new PatientService(patient);
						
				        PropertyUtils.setProperty(dynaForm, "firstName", patientService.getLastFirstName());
						PropertyUtils.setProperty(dynaForm, "dob", patientService.getDOB());
						PropertyUtils.setProperty(dynaForm, "gender", patientService.getGender());
						PropertyUtils.setProperty(dynaForm, "externalId", patient.getExternalId());
						PropertyUtils.setProperty(dynaForm, "chartNumber", patient.getChartNumber());
						
						// Get observation history information
						ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
						List<ObservationHistory> obList = observationDAO.getAll(patient, sample);
						String ageUnit = "";
						String age = "";
						String department = "";
						for (ObservationHistory observationHistory : obList) {
							if (observationHistory.getObservationHistoryTypeId().equals(IActionConstants.OBSERVATION_TYPE_DEPARTMENT)) {
								DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
								if (!(StringUtil.isNullorNill(observationHistory.getValue())) 
										&& StringUtil.isInteger(observationHistory.getValue())
										&& !observationHistory.getValue().equals("0")) {
									department = dictionaryDAO.getDataForId(observationHistory.getValue()).getDictEntry();
								}
								continue;
							}
							if (observationHistory.getObservationHistoryTypeId().equals(IActionConstants.OBSERVATION_TYPE_AGE_VALUE)) {
								age = observationHistory.getValue();
								continue;
							}
							if (observationHistory.getObservationHistoryTypeId().equals(IActionConstants.OBSERVATION_TYPE_AGE_UNIT)) {
								ageUnit = observationHistory.getValue();
								continue;
							}
							if (observationHistory.getObservationHistoryTypeId().equals(IActionConstants.OBSERVATION_TYPE_DIAGNOSIS)) {
								PropertyUtils.setProperty(dynaForm, "diagnosis", observationHistory.getValue());
							}
						}
						// If observation history does not have age value
						// Get age value from person
						if (ageUnit.equals("") || ageUnit.equals(StringUtil.getMessageForKey( "patient.ageUnit.year" ))) {
							age = patientService.getPerson().getAge();
						} else {
							age += " " + ageUnit;
						}
						if (department.equals("")) {
							PropertyUtils.setProperty(dynaForm, "department", StringUtil.getMessageForKey( "department.unknown" ));
						} else {
							PropertyUtils.setProperty(dynaForm, "department", department );
						}
						PropertyUtils.setProperty(dynaForm, "age", age);
						
						// Get address information
						
						HashMap<String, String> addressList = (HashMap<String, String>) patientService.getAddressComponents();
						String street = StringUtil.isNullorNill(addressList.get("street")) ? "" : addressList.get("street") + " ";
						String ward = StringUtil.isNullorNill(addressList.get("ward")) ? "" : addressList.get("ward") + " ";
						String district = StringUtil.isNullorNill(addressList.get("district")) ? "" : addressList.get("district") + " ";
						String city = StringUtil.isNullorNill(addressList.get("City")) ? "" : addressList.get("City");
						String address = street + ward + district + city;
						PropertyUtils.setProperty(dynaForm, "address", address);
						
						// Get requester information
						SampleRequesterDAO sampleRequesterDAO = new SampleRequesterDAOImpl();
						OrganizationDAO organizationDAO = new OrganizationDAOImpl();
						String organization = "";
						List<SampleRequester> requesters = sampleRequesterDAO.getRequestersForSampleId(sample.getId());
						for (SampleRequester sampleRequester : requesters) {
							if (sampleRequester.getRequesterTypeId() == 1) {
								organization = organizationDAO.getOrganizationById((String) String.valueOf(sampleRequester.getRequesterId())).getOrganizationName();
								break;
							}
						}
						PropertyUtils.setProperty(dynaForm, "organization", organization);
					}
					
					
					// Get project information
					SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
					SampleProject sampleProject = sampleProjectDAO.getSampleProjectBySampleId(sample.getId());
					if (sampleProject != null) {
						PropertyUtils.setProperty(dynaForm, "projectName", sampleProject.getProject().getProjectName());
					}
					
					
					resultsUtility.addIdentifingPatientInfo(patient, dynaForm);
					List<TestResultItem> results = resultsUtility.getGroupedTestsForSample(sample, patient, getUserSection());
					
					if (resultsUtility.inventoryNeeded()) {
						addInventory(dynaForm);
						PropertyUtils.setProperty(dynaForm, "displayTestKit", true);
					} else {
						addEmptyInventoryList(dynaForm);
					}
					
					// load testSectionsByName for drop down
		     		PropertyUtils.setProperty(dynaForm, "testSectionsByName", DisplayListService.getList(ListType.TEST_SECTION_BY_NAME));
					paging.setDatabaseResults(request, dynaForm, results);
				} else {
					setEmptyResults(dynaForm);
				}
			} else {
				setEmptyResults(dynaForm);
				PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.FALSE);
			}
		} else {
			paging.page(request, dynaForm, newPage);
		}

		return mapping.findForward(forward);
	}

	private boolean modifyResultsRoleBased() {
		return "true".equals(ConfigurationProperties.getInstance().getPropertyValue(Property.roleRequiredForModifyResults));
	}

	private boolean userNotInRole(HttpServletRequest request) {
		if( userModuleDAO.isUserAdmin(request)){
			return false;
		}
		
		UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
		
		List<String> roleIds = userRoleDAO.getRoleIdsForUser( currentUserId );
		
		return !roleIds.contains(RESULT_EDIT_ROLE_ID);
	}

	private void setEmptyResults(DynaActionForm dynaForm) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		PropertyUtils.setProperty(dynaForm, "testResult", new ArrayList<TestResultItem>());
		PropertyUtils.setProperty(dynaForm, "firstName", "");
		PropertyUtils.setProperty(dynaForm, "dob", "");
		PropertyUtils.setProperty(dynaForm, "gender", "");
		PropertyUtils.setProperty(dynaForm, "externalId", "");
		PropertyUtils.setProperty(dynaForm, "displayTestKit", false);
		addEmptyInventoryList(dynaForm);
	}

	private void addInventory(DynaActionForm dynaForm) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		List<InventoryKitItem> list = inventoryUtility.getExistingActiveInventory();
		PropertyUtils.setProperty(dynaForm, "inventoryItems", list);
	}

	private void addEmptyInventoryList(DynaActionForm dynaForm) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		PropertyUtils.setProperty(dynaForm, "inventoryItems", new ArrayList<InventoryKitItem>());
	}

	private ActionMessages validateAll(HttpServletRequest request, ActionMessages errors, DynaActionForm dynaForm) {

		Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);

		if (sample == null) {
			ActionError error = new ActionError("sample.edit.sample.notFound", accessionNumber, null, null);
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
		}
		//added by Dung 2016-07-22 for user session not exists in the system( table system_user_section)
		if(getUserSection()==null){
		    ActionError error = new ActionError("sample.edit.user.session", currentUserId, null, null);
		    errors.add(ActionMessages.GLOBAL_MESSAGE,error);
		}

		return errors;
	}

	private Patient getPatient() {
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		return sampleHumanDAO.getPatientForSample(sample);
	}

	private void getSample() {
		sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
	}

	protected String getPageTitleKey() {
		return "banner.menu.results";

	}

	protected String getPageSubtitleKey() {
		return "banner.menu.results";
	}

}
