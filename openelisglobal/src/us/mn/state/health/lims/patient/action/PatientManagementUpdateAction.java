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
package us.mn.state.health.lims.patient.action;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import us.mn.state.health.lims.address.dao.AddressPartDAO;
import us.mn.state.health.lims.address.dao.PersonAddressDAO;
import us.mn.state.health.lims.address.daoimpl.AddressPartDAOImpl;
import us.mn.state.health.lims.address.daoimpl.PersonAddressDAOImpl;
import us.mn.state.health.lims.address.valueholder.AddressPart;
import us.mn.state.health.lims.address.valueholder.PersonAddress;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.provider.query.PatientSearchResults;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.action.bean.PatientSearch;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.util.PatientUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patientidentity.dao.PatientIdentityDAO;
import us.mn.state.health.lims.patientidentity.daoimpl.PatientIdentityDAOImpl;
import us.mn.state.health.lims.patientidentity.valueholder.PatientIdentity;
import us.mn.state.health.lims.patientidentitytype.util.PatientIdentityTypeMap;
import us.mn.state.health.lims.patienttype.dao.PatientPatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientPatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.util.PatientTypeMap;
import us.mn.state.health.lims.patienttype.valueholder.PatientPatientType;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.dao.SearchResultsDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.daoimpl.SearchResultsDAOImp;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;

public class PatientManagementUpdateAction extends BaseAction implements
		IPatientUpdate, IActionConstants {

	protected Patient patient;
	protected Person person;
	private List<PatientIdentity> patientIdentities;
	private String patientID = "";
	private static PatientIdentityDAO identityDAO = new PatientIdentityDAOImpl();
	private static PatientDAO patientDAO = new PatientDAOImpl();
	private static PersonAddressDAO personAddressDAO = new PersonAddressDAOImpl();
	private static PersonDAO personDAO;
	private static ProviderDAO providerDAO;
	private static SampleHumanDAO sampleHumanDAO;
	protected PatientUpdateStatus patientUpdateStatus = PatientUpdateStatus.NO_ACTION;
	private boolean insertPersonOnUpdate = false;

	private static String ADDRESS_PART_VILLAGE_ID;
	private static String ADDRESS_PART_COMMUNE_ID;
	private static String ADDRESS_PART_DEPT_ID;
	private static String ADDRESS_PART_DISTRICT_ID;
	private static String ADDRESS_PART_WARD_ID;
	private String providerId = "";

	public static enum PatientUpdateStatus {
		NO_ACTION, UPDATE, ADD
	}

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
			} else if ("district".equals(addressPart.getPartName())) {
				ADDRESS_PART_DISTRICT_ID = addressPart.getId();
			} else if ("ward".equals(addressPart.getPartName())) {
				ADDRESS_PART_WARD_ID = addressPart.getId();
			}
		}
	}

	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;

		BaseActionForm dynaForm = (BaseActionForm) form;
		PatientManagementInfo patientInfo = (PatientManagementInfo) dynaForm
				.get("patientProperties");

		SampleOrderItem sampleOrderItems = (SampleOrderItem) dynaForm
				.get("sampleOrderItems");
		PatientSearch patientSearch = (PatientSearch) dynaForm
				.get("patientSearch");
		patientInfo.setAge(sampleOrderItems.getPatientAgeValue());
		patientInfo.setPatientAgeUnit(sampleOrderItems.getPatientAgeUnits());
		setPatientUpdateStatus(patientInfo);

		if (patientUpdateStatus != PatientUpdateStatus.NO_ACTION) {

			ActionMessages errors;

			errors = preparePatientData(mapping, request, patientInfo);

			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(FWD_FAIL);
			}

			Transaction tx = HibernateUtil.getSession().beginTransaction();
			try {
				String patientId = persistPatientData(patientInfo);
				// saveObservationPatient() only used when adding/updating a patient info
				if (patientUpdateStatus != PatientUpdateStatus.NO_ACTION) {
    				saveObservationPatient(patientId,
    						patientSearch.getSelectedPatientActionButtonText(),
    						getSysUserId(request), sampleOrderItems, patientInfo);
				}
				tx.commit();

			} catch (LIMSRuntimeException lre) {
				tx.rollback();
				errors = new ActionMessages();

				if (lre.getException() instanceof StaleObjectStateException) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
							"errors.OptimisticLockException", null, null));
				} else {
					lre.printStackTrace();
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
							"errors.UpdateException", null, null));
				}

				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				request.setAttribute(ALLOW_EDITS_KEY, "false");
				return mapping.findForward(FWD_FAIL);

			} finally {
				HibernateUtil.closeSession();
			}

			dynaForm.initialize(mapping);

		}

		setSuccessFlag(request, forward);

		return mapping.findForward(forward);
	}

	/**
	 * @param patientId2
	 * @param accession
	 *            number
	 * @param userId
	 * @param SampleOrderItem
	 */
	public void saveObservationPatient(String patientId,
			String selectedPatientActionButtonText, String userId,
			SampleOrderItem sampleOrderItems, PatientManagementInfo patientManagementInfo) {
		Sample sample = new Sample();
		SampleDAO sampleDAO = new SampleDAOImpl();
		sample = sampleDAO.getSampleByAccessionNumber(selectedPatientActionButtonText);

		String sampleId = IActionConstants.STR_SAMPLE_ID_OBS_HISTORY_REFERENCE; // Default samp_id to use in observation_history when patient is added via "Add Patient" page
		if (sample != null) {
		    sampleId = sample.getId();
		}
		
		if (patientId != null) {
			// get AgeUnits observation of sample
			ObservationHistory observationHistory = new ObservationHistory();
			observationHistory = getObservation(patientId,
					OBSERVATION_TYPE_AGE_UNIT);
			// if observation of sample has true then edit else insert
			ObservationHistoryDAO observationDAO;
			ObservationHistory observation;
			if (observationHistory != null) {
				observationDAO = new ObservationHistoryDAOImpl();
				observationHistory.setSysUserId(userId);
				if(patientManagementInfo.getPatientAgeUnit()!=null){
				    observationHistory.setValue(patientManagementInfo.getPatientAgeUnit());
				}else{
				observationHistory.setValue(sampleOrderItems
						.getPatientAgeUnits());
				}
				observationDAO.updateData(observationHistory);

			} else {
				observationDAO = new ObservationHistoryDAOImpl();
				observation = new ObservationHistory();
				observation.setValueType(OBSERVATION_VALUE_TYPE_L);
				observation
						.setObservationHistoryTypeId(OBSERVATION_TYPE_AGE_UNIT);
			    observation.setSampleId(sampleId);
				observation.setPatientId(patientId);
				observation.setValue(sampleOrderItems.getPatientAgeUnits());
				observation.setSysUserId(userId);
				observationDAO.insertData(observation);
			}
			// check age insert or update patient
			observationHistory = new ObservationHistory();
			observationHistory = getObservation(patientId,
					OBSERVATION_TYPE_AGE_VALUE);
			if (observationHistory != null) {
				observationDAO = new ObservationHistoryDAOImpl();
				observationHistory.setValue(sampleOrderItems
						.getPatientAgeValue());
				observationHistory.setSysUserId(userId);
				observationDAO.updateData(observationHistory);
			} else {
				observationDAO = new ObservationHistoryDAOImpl();
				observation = new ObservationHistory();
				observation.setValueType(OBSERVATION_VALUE_TYPE_L);
				observation
						.setObservationHistoryTypeId(OBSERVATION_TYPE_AGE_VALUE);
                observation.setSampleId(sampleId);
				observation.setPatientId(patientId);
				observation.setValue(sampleOrderItems.getPatientAgeValue());
				observation.setSysUserId(userId);
				observationDAO.insertData(observation);
			}
			
			//In case do not use department, then comment the following code
			// check department insert or update patient
			observationHistory = new ObservationHistory();
			observationHistory = getObservation(patientId,
					OBSERVATION_TYPE_DEPARTMENT);
			if (observationHistory != null) {
				observationDAO = new ObservationHistoryDAOImpl();
				observationHistory.setValue(patientManagementInfo
						.getAddressDepartment());
				observationHistory.setSysUserId(userId);
				observationDAO.updateData(observationHistory);
			} else {
				observationDAO = new ObservationHistoryDAOImpl();
				observation = new ObservationHistory();
				observation.setValueType(OBSERVATION_VALUE_TYPE_D);
				observation
						.setObservationHistoryTypeId(OBSERVATION_TYPE_DEPARTMENT);
                observation.setSampleId(sampleId);
				observation.setPatientId(patientId);
				observation.setValue(patientManagementInfo
						.getAddressDepartment());
				observation.setSysUserId(userId);
				observationDAO.insertData(observation);
			}
			
			// check patient Diagnosis
			observationHistory = new ObservationHistory();
			observationHistory = getObservation(patientId,
					OBSERVATION_TYPE_DIAGNOSIS);
			if (observationHistory != null) {
				observationDAO = new ObservationHistoryDAOImpl();
				observationHistory.setSysUserId(userId);
				observationHistory.setValue(sampleOrderItems
						.getPatientDiagnosis());
				observationDAO.updateData(observationHistory);
			} else {
				observationDAO = new ObservationHistoryDAOImpl();
				observation = new ObservationHistory();
				observation.setValueType(OBSERVATION_VALUE_TYPE_L);
				observation
						.setObservationHistoryTypeId(OBSERVATION_TYPE_DIAGNOSIS);
                observation.setSampleId(sampleId);
				observation.setPatientId(patientId);
				observation.setValue(sampleOrderItems.getPatientDiagnosis());
				observation.setSysUserId(userId);
				observationDAO.insertData(observation);
			}
			//check name of species insert or update
			observationHistory = new ObservationHistory();
            observationHistory = getObservation(patientId,
                    OBSERVATION_TYPE_SPECIES_NAME);
            if (observationHistory != null) {
                observationDAO = new ObservationHistoryDAOImpl();
                observationHistory.setValue(sampleOrderItems.getSpeciesName());
                observationHistory.setSysUserId(userId);
                observationDAO.updateData(observationHistory);
            } else {
                observationDAO = new ObservationHistoryDAOImpl();
                observation = new ObservationHistory();
                observation.setValueType(OBSERVATION_VALUE_TYPE_L);
                observation
                        .setObservationHistoryTypeId(OBSERVATION_TYPE_SPECIES_NAME);
                observation.setSampleId(sampleId);
                observation.setPatientId(patientId);
                observation.setValue(sampleOrderItems.getSpeciesName());
                observation.setSysUserId(userId);
                observationDAO.insertData(observation);
            }
		}

	}

	/**
	 * @param sample
	 */
	private ObservationHistory getObservation(String patientId, String typeId) {
		// TODO Auto-generated method stub
		ObservationHistory observationHistory = new ObservationHistory();
		ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
		observationHistory = observationDAO
				.getObservationHistoriesByPatientIdAndTypeOnlyOne(patientId,
						typeId);
		return observationHistory;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.mn.state.health.lims.patient.action.IPatientUpdate#preparePatientData
	 * (org.apache.struts.action.ActionMapping,
	 * javax.servlet.http.HttpServletRequest,
	 * us.mn.state.health.lims.common.action.BaseActionForm)
	 */
	public ActionMessages preparePatientData(ActionMapping mapping,
			HttpServletRequest request, PatientManagementInfo patientInfo)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		if (currentUserId == null) {
			currentUserId = getSysUserId(request);
		}

		ActionMessages errors = validatePatientInfo(patientInfo);
		if (!errors.isEmpty()) {
			return errors;
		}

		initMembers();

		if (patientUpdateStatus == PatientUpdateStatus.UPDATE) {
			loadForUpdate(patientInfo);
		}

		copyFormBeanToValueHolders(patientInfo);

		setSystemUserID();

		setLastUpdatedTimeStamps(patientInfo);

		return errors;
	}

	private ActionMessages validatePatientInfo(PatientManagementInfo patientInfo) {
		ActionMessages errors = new ActionMessages();
		if (ConfigurationProperties
				.getInstance()
				.isPropertyValueEqual(
						ConfigurationProperties.Property.ALLOW_DUPLICATE_SUBJECT_NUMBERS,
						"false")) {
			String newSTNumber = GenericValidator.isBlankOrNull(patientInfo
					.getSTnumber()) ? null : patientInfo.getSTnumber();
			String newSubjectNumber = GenericValidator
					.isBlankOrNull(patientInfo.getSubjectNumber()) ? null
					: patientInfo.getSubjectNumber();
			String newNationalId = GenericValidator.isBlankOrNull(patientInfo
					.getNationalId()) ? null : patientInfo.getNationalId();

			SearchResultsDAO search = new SearchResultsDAOImp();
			List<PatientSearchResults> results = search.getSearchResults(null,
					null, newSTNumber, newSubjectNumber, newNationalId, null,
					null, null);

			if (!results.isEmpty()) {

				for (PatientSearchResults result : results) {
					if (!result.getPatientID().equals(
							patientInfo.getPatientPK())) {
						if (newSTNumber != null
								&& newSTNumber.equals(result.getSTNumber())) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionError("error.duplicate.STNumber",
											null, null));
						}
						if (newSubjectNumber != null
								&& newSubjectNumber.equals(result
										.getSubjectNumber())) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionError(
											"error.duplicate.subjectNumber",
											null, null));
						}
						if (newNationalId != null
								&& newNationalId.equals(result.getNationalId())) {
							errors.add(ActionMessages.GLOBAL_MESSAGE,
									new ActionError(
											"error.duplicate.nationalId", null,
											null));
						}
					}
				}
			}
		}

		return errors;
	}

	private void setLastUpdatedTimeStamps(PatientManagementInfo patientInfo) {
		String patientUpdate = patientInfo.getPatientLastUpdated();
		// Update lastUpdated(timestamp) only when "updating" a patient (trapped not to update the lastupdated when "adding")
		if (!GenericValidator.isBlankOrNull(patientUpdate) && patientInfo.getPatientProcessingStatus().equals("update")) {
			Timestamp timeStamp = Timestamp.valueOf(patientUpdate);
			patient.setLastupdated(timeStamp);
		}

		String personUpdate = patientInfo.getPersonLastUpdated();
        // Update lastUpdated(timestamp) only when "updating" a patient (trapped not to update the lastupdated when "adding")
		if (!GenericValidator.isBlankOrNull(personUpdate) && patientInfo.getPatientProcessingStatus().equals("update")) {
			Timestamp timeStamp = Timestamp.valueOf(personUpdate);
			person.setLastupdated(timeStamp);
		}
	}

	private void initMembers() {
		patient = new Patient();
		person = new Person();
		patientIdentities = new ArrayList<PatientIdentity>();
	}

	private void loadForUpdate(PatientManagementInfo patientInfo) {

		patientID = patientInfo.getPatientPK();
		patient = patientDAO.readPatient(patientID);
		if (patient.getPerson().getId()
				.equals(PatientUtil.getUnknownPerson().getId())) {
			insertPersonOnUpdate = true;
		} else {
			person = patient.getPerson();
			patientIdentities = identityDAO
					.getPatientIdentitiesForPatient(patient.getId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.mn.state.health.lims.patient.action.IPatientUpdate#setPatientUpdateStatus
	 * (us.mn.state.health.lims.common.action.BaseActionForm)
	 */
	public void setPatientUpdateStatus(PatientManagementInfo patientInfo) {

		String status = patientInfo.getPatientProcessingStatus();

		if (status.equals("noAction")) {
			patientUpdateStatus = PatientUpdateStatus.NO_ACTION;
		} else if (status.equals("update")) {
			patientUpdateStatus = PatientUpdateStatus.UPDATE;
		} else {
			patientUpdateStatus = PatientUpdateStatus.ADD;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.mn.state.health.lims.patient.action.IPatientUpdate#getPatientUpdateStatus
	 * ()
	 */
	public PatientUpdateStatus getPatientUpdateStatus() {
		return patientUpdateStatus;
	}

	private void copyFormBeanToValueHolders(PatientManagementInfo patientInfo)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		PropertyUtils.copyProperties(patient, patientInfo);
		PropertyUtils.copyProperties(person, patientInfo);
	}

	private void setSystemUserID() {
		patient.setSysUserId(currentUserId);
		person.setSysUserId(currentUserId);

		for (PatientIdentity identity : patientIdentities) {
			identity.setSysUserId(currentUserId);
		}
	}

	public String persistPatientData(PatientManagementInfo patientInfo)
			throws LIMSRuntimeException {
		String patientId = "";
		PersonDAO personDAO = new PersonDAOImpl();

		if (patientUpdateStatus == PatientUpdateStatus.ADD
				|| insertPersonOnUpdate) {
			personDAO.insertData(person);
		} else if (patientUpdateStatus == PatientUpdateStatus.UPDATE) {
			personDAO.updateData(person);
		}
		patient.setPerson(person);

		if (patientUpdateStatus == PatientUpdateStatus.ADD) {
			patientId = patientDAO.insertDataWS(patient);
		} else if (patientUpdateStatus == PatientUpdateStatus.UPDATE) {
			patientDAO.updateData(patient);
		}

		persistPatientRelatedInformation(patientInfo);
		patientID = patient.getId();
		if (patientId.equals("")) {
			patientId = patient.getId();
		}
		return patientId;
	}

	private String addNewPerson(PatientManagementInfo patientInfo) {
		Person person = new Person();
		person.setFirstName(patientInfo.getProvider().getPerson()
				.getFirstName());
		person.setSysUserId(patientInfo.getSystemUserId());
		personDAO = new PersonDAOImpl();
		return personDAO.insertDataWS(person);
	}

	protected void persistPatientRelatedInformation(
			PatientManagementInfo patientInfo) {
		persistIdentityTypes(patientInfo);
		persistExtraPatientAddressInfo(patientInfo);
		persistPatientType(patientInfo);
	}

	protected void persistIdentityTypes(PatientManagementInfo patientInfo) {

		persistIdentityType(patientInfo.getSTnumber(), "ST");
		persistIdentityType(patientInfo.getMothersName(), "MOTHER");
		persistIdentityType(patientInfo.getAka(), "AKA");
		persistIdentityType(patientInfo.getInsuranceNumber(), "INSURANCE");
		persistIdentityType(patientInfo.getOccupation(), "OCCUPATION");
		persistIdentityType(patientInfo.getEmployerName(), "EMPLOYER_NAME");
		persistIdentityType(patientInfo.getSubjectNumber(), "SUBJECT");
		persistIdentityType(patientInfo.getMothersInitial(), "MOTHERS_INITIAL");
		persistIdentityType(patientInfo.getEducation(), "EDUCATION");
		persistIdentityType(patientInfo.getMaritialStatus(), "MARITIAL");
		persistIdentityType(patientInfo.getNationality(), "NATIONALITY");
		persistIdentityType(patientInfo.getHealthDistrict(), "HEALTH DISTRICT");
		persistIdentityType(patientInfo.getHealthRegion(), "HEALTH REGION");
		persistIdentityType(patientInfo.getOtherNationality(),
				"OTHER NATIONALITY");
	}

	private void persistExtraPatientAddressInfo(
			PatientManagementInfo patientInfo) {
		PersonAddress village = null;
		PersonAddress commune = null;
		PersonAddress dept = null;
		PersonAddress district = null;
		PersonAddress ward = null;
		List<PersonAddress> personAddressList = personAddressDAO
				.getAddressPartsByPersonId(person.getId());

		for (PersonAddress address : personAddressList) {
			if (address.getAddressPartId().equals(ADDRESS_PART_COMMUNE_ID)) {
				commune = address;
				commune.setValue(patientInfo.getCommune());
				commune.setSysUserId(currentUserId);
				personAddressDAO.update(commune);
			} else if (address.getAddressPartId().equals(
					ADDRESS_PART_VILLAGE_ID)) {
				village = address;
				village.setValue(patientInfo.getCity());
				village.setSysUserId(currentUserId);
				personAddressDAO.update(village);
				
			//In case do not use department, then comment the following code
			/*} else if (address.getAddressPartId().equals(ADDRESS_PART_DEPT_ID)) {
				dept = address;
				if (!GenericValidator.isBlankOrNull(patientInfo
						.getAddressDepartment())
						&& !patientInfo.getAddressDepartment().equals("0")) {
					dept.setValue(patientInfo.getAddressDepartment());
					dept.setType("D");
					dept.setSysUserId(currentUserId);
					personAddressDAO.update(dept);
				}*/
			} else if (address.getAddressPartId().equals(
					ADDRESS_PART_DISTRICT_ID)) {
				district = address;
				if (!GenericValidator.isBlankOrNull(patientInfo
						.getAddressDistrict())) {
					district.setValue(patientInfo.getAddressDistrict());
					district.setType("D");
					district.setSysUserId(currentUserId);
					personAddressDAO.update(district);
				}
			} else if (address.getAddressPartId().equals(ADDRESS_PART_WARD_ID)) {
				ward = address;
				ward.setValue(patientInfo.getAddressWard());
				ward.setSysUserId(currentUserId);
				personAddressDAO.update(ward);
			}
		}

		if (commune == null) {
			insertNewPatientInfo(ADDRESS_PART_COMMUNE_ID,
					patientInfo.getCommune(), "T");
		}

		if (village == null) {
			insertNewPatientInfo(ADDRESS_PART_VILLAGE_ID,
					patientInfo.getCity(), "T");
		}
        
		//In case do not use department, then comment the following code
	/*	if (dept == null && patientInfo.getAddressDepartment() != null
				&& !patientInfo.getAddressDepartment().equals("0")) {
			insertNewPatientInfo(ADDRESS_PART_DEPT_ID,
					patientInfo.getAddressDepartment(), "D");
		}*/

		if (district == null && patientInfo.getAddressDistrict() != null
				&& !patientInfo.getAddressDistrict().equals("0")) {
			insertNewPatientInfo(ADDRESS_PART_DISTRICT_ID,
					patientInfo.getAddressDistrict(), "D");
		}

		if (ward == null) {
			insertNewPatientInfo(ADDRESS_PART_WARD_ID,
					patientInfo.getAddressWard(), "T");
		}

	}

	private void insertNewPatientInfo(String partId, String value, String type) {
		PersonAddress address;
		address = new PersonAddress();
		address.setPersonId(person.getId());
		address.setAddressPartId(partId);
		address.setType(type);
		address.setValue(value);
		address.setSysUserId(currentUserId);
		personAddressDAO.insert(address);
	}

	public void persistIdentityType(String paramValue, String type)
			throws LIMSRuntimeException {

		Boolean newIdentityNeeded = true;
		String typeID = PatientIdentityTypeMap.getInstance().getIDForType(type);

		if (patientUpdateStatus == PatientUpdateStatus.UPDATE) {

			for (PatientIdentity listIdentity : patientIdentities) {
				if (listIdentity.getIdentityTypeId().equals(typeID)) {

					newIdentityNeeded = false;

					if ((listIdentity.getIdentityData() == null && !GenericValidator
							.isBlankOrNull(paramValue))
							|| (listIdentity.getIdentityData() != null && !listIdentity
									.getIdentityData().equals(paramValue))) {
						listIdentity.setIdentityData(paramValue);
						identityDAO.updateData(listIdentity);
					}

					break;
				}
			}
		}

		if (newIdentityNeeded && !GenericValidator.isBlankOrNull(paramValue)) {
			// either a new patient or a new identity item
			PatientIdentity identity = new PatientIdentity();
			identity.setPatientId(patient.getId());
			identity.setIdentityTypeId(typeID);
			identity.setSysUserId(currentUserId);
			identity.setIdentityData(paramValue);
			identity.setLastupdatedFields();
			identityDAO.insertData(identity);
		}
	}

	protected void persistPatientType(PatientManagementInfo patientInfo) {

		PatientPatientTypeDAO patientPatientTypeDAO = new PatientPatientTypeDAOImpl();

		String typeName = null;

		try {
			typeName = patientInfo.getPatientType();
		} catch (Exception ignored) {
		}

		if (!GenericValidator.isBlankOrNull(typeName)) {
			String typeID = "";
			// Patient type value "0" when set to blank (PatientManagementInfo)
			if (!typeName.equals("0")) {
				typeID = PatientTypeMap.getInstance().getIDForType(typeName);
			}

			PatientPatientType patientPatientType = patientPatientTypeDAO
					.getPatientPatientTypeForPatient(patient.getId());

			if (patientPatientType == null) {
				patientPatientType = new PatientPatientType();
				patientPatientType.setSysUserId(currentUserId);
				patientPatientType.setPatientId(patient.getId());
				patientPatientType.setPatientTypeId(typeID);
				patientPatientTypeDAO.insertData(patientPatientType);
			} else {
				patientPatientType.setSysUserId(currentUserId);
				patientPatientType.setPatientTypeId(typeID);
				patientPatientTypeDAO.updateData(patientPatientType);
			}
		}
	}

	@Override
	protected String getPageTitleKey() {
		return "patient.management.title";
	}
	

	@Override
	protected String getPageSubtitleKey() {
		return "patient.management.title";
	}

	public String getPatientId(BaseActionForm dynaForm) {
		return GenericValidator.isBlankOrNull(patientID) && dynaForm != null ? dynaForm
				.getString("patientPK") : patientID;
	}

	private void persistObservations(Sample sample, Patient patient,
			List<ObservationHistory> observationHistories) {

		ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
		for (ObservationHistory observation : observationHistories) {
			observation.setSampleId(sample.getId());
			observation.setPatientId(patient.getId());
			observationDAO.insertData(observation);
		}
	}

}