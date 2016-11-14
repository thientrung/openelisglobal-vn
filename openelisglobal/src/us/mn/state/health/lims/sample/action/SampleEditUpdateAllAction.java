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
package us.mn.state.health.lims.sample.action;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator.ValidationResults;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.RequesterService;
import us.mn.state.health.lims.common.services.SampleAddService;
import us.mn.state.health.lims.common.services.SampleAddService.SampleTestCollection;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.SampleService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.note.dao.NoteDAO;
import us.mn.state.health.lims.note.daoimpl.NoteDAOImpl;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.dao.OrganizationOrganizationTypeDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.daoimpl.OrganizationOrganizationTypeDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.patient.action.IPatientUpdate;
import us.mn.state.health.lims.patient.action.PatientManagementUpdateAction;
import us.mn.state.health.lims.patient.action.PatientManagementUpdateAction.PatientUpdateStatus;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.util.PatientUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.qaevent.dao.QaEventDAO;
import us.mn.state.health.lims.qaevent.dao.QaObservationDAO;
import us.mn.state.health.lims.qaevent.daoimpl.QaEventDAOImpl;
import us.mn.state.health.lims.qaevent.daoimpl.QaObservationDAOImpl;
import us.mn.state.health.lims.qaevent.valueholder.QaEvent;
import us.mn.state.health.lims.qaevent.valueholder.QaObservation;
import us.mn.state.health.lims.requester.dao.SampleRequesterDAO;
import us.mn.state.health.lims.requester.daoimpl.SampleRequesterDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.sample.action.util.SamplePatientUpdateData;
import us.mn.state.health.lims.sample.bean.SampleEditItem;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.sampleproject.dao.SampleProjectDAO;
import us.mn.state.health.lims.sampleproject.daoimpl.SampleProjectDAOImpl;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;
import us.mn.state.health.lims.sampleqaevent.dao.SampleQaEventDAO;
import us.mn.state.health.lims.sampleqaevent.daoimpl.SampleQaEventDAOImpl;
import us.mn.state.health.lims.sampleqaevent.valueholder.SampleQaEvent;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;

public class SampleEditUpdateAllAction extends BaseAction {

	private static final String DEFAULT_ANALYSIS_TYPE = "MANUAL";
	private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private static final SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
	private static final SampleDAO sampleDAO = new SampleDAOImpl();
	private static final TestDAO testDAO = new TestDAOImpl();
	private static final String CANCELED_TEST_STATUS_ID;
	private static final String CANCELED_SAMPLE_STATUS_ID;
	private ObservationHistory paymentObservation = null;
	private static final ObservationHistoryDAO observationDAO = new ObservationHistoryDAOImpl();
	private static final TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
	private static final PersonDAO personDAO = new PersonDAOImpl();
	private static final SampleRequesterDAO sampleRequesterDAO = new SampleRequesterDAOImpl();
	private static final OrganizationDAO organizationDAO = new OrganizationDAOImpl();
	private static final OrganizationOrganizationTypeDAO orgOrgTypeDAO = new OrganizationOrganizationTypeDAOImpl();
	private static final SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
	private String patientId = "";

	static {
		CANCELED_TEST_STATUS_ID = StatusService.getInstance().getStatusID(
				AnalysisStatus.Canceled);
		CANCELED_SAMPLE_STATUS_ID = StatusService.getInstance().getStatusID(
				SampleStatus.Canceled);
	}

	@SuppressWarnings("unchecked")
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ActionMessages errors;
		request.getSession().setAttribute(SAVE_DISABLED, TRUE);

		DynaActionForm dynaForm = (DynaActionForm) form;

		boolean sampleChanged = accessionNumberChanged(dynaForm);
		Sample updatedSample = null;

		if (sampleChanged) {
			errors = validateNewAccessionNumber(dynaForm
					.getString("newAccessionNumber"));
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				request.setAttribute(Globals.ERROR_KEY, errors);
				return mapping.findForward(FWD_FAIL);
			} else {
				updatedSample = updateAccessionNumberInSample(dynaForm);
			}
		}
		
		// Trung adds for keep value of  combobox criteria Search
        String criteria= request.getParameter("criteria");
        if(!GenericValidator.isBlankOrNull(criteria)){
            request.setAttribute(IActionConstants.CIRITERIA, criteria);
        } else {
            request.setAttribute(IActionConstants.CIRITERIA, "");
        }

		List<SampleEditItem> existingTests = (List<SampleEditItem>) dynaForm
				.get("existingTests");
		List<Analysis> cancelAnalysisList = createRemoveList(existingTests);
		List<SampleItem> updateSampleItemList = createSampleItemUpdateList(existingTests);
		List<SampleItem> cancelSampleItemList = createCancelSampleList(
				existingTests, cancelAnalysisList);
		List<Analysis> addAnalysisList = createAddAanlysisList((List<SampleEditItem>) dynaForm
				.get("possibleTests"));

		if (updatedSample == null) {
			updatedSample = sampleDAO.getSampleByAccessionNumber(dynaForm
					.getString("accessionNumber"));
		}

		String receivedDateForDisplay = updatedSample
				.getReceivedDateForDisplay();
		String collectionDateFromRecieveDate = null;
		boolean useReceiveDateForCollectionDate = !FormFields.getInstance()
				.useField(Field.CollectionDate);

		if (useReceiveDateForCollectionDate) {
			collectionDateFromRecieveDate = receivedDateForDisplay
					+ " 00:00:00";
		}

		SampleAddService sampleAddService = new SampleAddService(
				dynaForm.getString("sampleXML"), currentUserId, updatedSample,
				collectionDateFromRecieveDate);
		SampleOrderService sampleOrderService = new SampleOrderService(
				(SampleOrderItem) dynaForm.get("sampleOrderItems"));
		SampleOrderItem sampleOrderItem = (SampleOrderItem) dynaForm
				.get("sampleOrderItems");
		if (sampleOrderItem.getOnsetOfDate() != null
				&& !sampleOrderItem.getOnsetOfDate().equals("")) {
			updatedSample.setOnsetOfDate(DateUtil
					.convertStringDateToTimestamp(sampleOrderItem
							.getOnsetOfDate() + " 00:00"));
		}
		SampleOrderService.SampleOrderPersistenceArtifacts orderArtifacts = sampleOrderService
				.getPersistenceArtifacts(updatedSample, currentUserId);

		if (orderArtifacts.getSample() != null) {
			sampleChanged = true;
			updatedSample = orderArtifacts.getSample();
		}

		Person referringPerson = orderArtifacts.getProviderPerson();
		// Dung add check person if have provider
		if (getPrivider(updatedSample) != null)
			referringPerson.setId(getPrivider(updatedSample)
					.getPersonProvider().getId());
		Patient patient = new SampleService(updatedSample).getPatient();
		// Dung add Update patient for sample
		SamplePatientUpdateData updateData = new SamplePatientUpdateData(
				getSysUserId(request));
		updateData.setSample(updatedSample);
		PatientManagementInfo patientInfo = (PatientManagementInfo) dynaForm
				.get("patientProperties");
		// dung add age
		patientInfo.setAge(sampleOrderItem.getPatientAgeValue());
		// add patientAgeUnit for patientInfo
		//patientInfo.setPatientAgeUnit(sampleOrderItem.getPatientAgeUnits());
				
		patientInfo.getProvider().getPersonProvider()
				.setFirstName(sampleOrderItem.getProviderFirstName());
		patientInfo.setSystemUserId(getSysUserId(request));
		IPatientUpdate patientUpdate = new PatientManagementUpdateAction();
		if (checkPatientOfSample(updatedSample.getAccessionNumber())) {
			// if this sample doesn't have patient, then create patient
			patientInfo.setPatientProcessingStatus("add");
		} else {
			patientInfo.setPatientProcessingStatus("update");
			patientInfo.setPatientPK(getPatient(updatedSample).getId());
		}
        // add: set patient department for sampleOrderItem
		sampleOrderItem.setPatientClinicalDeptId(patientInfo.getAddressDepartment());
		// end
		testAndInitializePatientForSaving(mapping, request, patientInfo,
				patientUpdate, updateData);
		if (patientUpdate.getPatientUpdateStatus() == PatientUpdateStatus.UPDATE)
			updatePatientInfo(patientInfo, patientUpdate, updateData, mapping,
					request);

		Transaction tx = HibernateUtil.getSession().beginTransaction();

		try {

			for (SampleItem sampleItem : updateSampleItemList) {
				sampleItemDAO.updateData(sampleItem);
			}

			for (Analysis analysis : cancelAnalysisList) {
				analysisDAO.updateData(analysis);
			}

			for (Analysis analysis : addAnalysisList) {
				if (analysis.getId() == null) {
					analysisDAO.insertData(analysis, false); // don't check for duplicates
				} else {
					analysisDAO.updateData(analysis);
				}
			}

			for (SampleItem sampleItem : cancelSampleItemList) {
				sampleItemDAO.updateData(sampleItem);
			}

			if (sampleChanged) {
				sampleDAO.updateData(updatedSample);
			}

			if (paymentObservation != null) {
				paymentObservation.setPatientId(patient.getId());
				observationDAO.insertOrUpdateData(paymentObservation);
			}

			if (FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_MODAL_VERSION)
					&& FormFields.getInstance().useField(
							Field.SAMPLE_ENTRY_REJECTION_IN_MODAL_VERSION))

				if (referringPerson != null) {
					// add or update person for Provider
					if (referringPerson.getId() == null) {
						personDAO.insertData(referringPerson);
					} else {
						personDAO.updateData(referringPerson);
					}
				}
			boolean isAgeUnitExisted = false;
			String patientId = "";
			String sampleId = "";
			for (ObservationHistory observation : orderArtifacts
					.getObservations()) {
				if (observation.getObservationHistoryTypeId().equals("11")) {
					observation.setValue(patientInfo.getPatientAgeUnit());
					isAgeUnitExisted = true;
				}
				observationDAO.insertOrUpdateData(observation);
				patientId = observation.getPatientId();
				sampleId = observation.getSampleId();
			}
			
			if(!isAgeUnitExisted) {
				ObservationHistory observation = new ObservationHistory();
				observation.setId(null);
				observation.setObservationHistoryTypeId("11");
				observation.setPatientId(patientId);
				observation.setSampleId(sampleId);
				observation.setValue(patientInfo.getPatientAgeUnit());
				observation.setValueType("L");
				observation.setLastupdated(new Timestamp(new java.util.Date().getTime()));
				observation.setSysUserId(getSysUserId(request));
				observationDAO.insertOrUpdateData(observation);
			}

			if (orderArtifacts.getSamplePersonRequester() != null) {
				SampleRequester samplePersonRequester = orderArtifacts
						.getSamplePersonRequester();
				samplePersonRequester.setRequesterId(orderArtifacts
						.getProviderPerson().getId());
				sampleRequesterDAO.insertOrUpdateData(samplePersonRequester);
			}

			if (orderArtifacts.getProviderOrganization() != null) {
				boolean link = orderArtifacts.getProviderOrganization().getId() == null;
				organizationDAO.insertOrUpdateData(orderArtifacts
						.getProviderOrganization());
				if (link) {
					orgOrgTypeDAO.linkOrganizationAndType(
							orderArtifacts.getProviderOrganization(),
							RequesterService.REFERRAL_ORG_TYPE_ID);
				}
			}

			if (orderArtifacts.getSampleOrganizationRequester() != null) {
				if (orderArtifacts.getProviderOrganization() != null) {
					orderArtifacts.getSampleOrganizationRequester()
							.setRequesterId(
									orderArtifacts.getProviderOrganization()
											.getId());
				}
				sampleRequesterDAO.insertOrUpdateData(orderArtifacts
						.getSampleOrganizationRequester());
			}

			if (orderArtifacts.getDeletableSampleOrganizationRequester() != null) {
				sampleRequesterDAO.delete(orderArtifacts
						.getDeletableSampleOrganizationRequester());
			}

			if (orderArtifacts.getSampleProjects() != null
					&& orderArtifacts.getSampleProjects().length > 0) {
				for (SampleProject sp : orderArtifacts.getSampleProjects())
					sampleProjectDAO.insertOrUpdateData(sp);
			}

			if (orderArtifacts.getDeletableSampleProjects() != null
					&& orderArtifacts.getDeletableSampleProjects().length > 0) {
				sampleProjectDAO.deleteData(Arrays.asList(orderArtifacts
						.getDeletableSampleProjects()), currentUserId);
			}
			if (updateData.isSavePatient()) {
				patientId = patientUpdate.persistPatientData(patientInfo);
				// update age and diagnosis
				PatientManagementUpdateAction patientupdate = new PatientManagementUpdateAction();
				//add: check status of patientUpdate, if it is not "NO_ACTION" then run saveObservationPatient to update/insert observations
				if (patientUpdate.getPatientUpdateStatus() != PatientManagementUpdateAction.PatientUpdateStatus.NO_ACTION) {
                    patientupdate.saveObservationPatient(patientId,
                            updatedSample.getAccessionNumber(),
                            getSysUserId(request), sampleOrderItem, patientInfo);
                }
		/*		if (patientupdate.getPatientUpdateStatus() != PatientManagementUpdateAction.PatientUpdateStatus.NO_ACTION) {
    				patientupdate.saveObservationPatient(patientId,
    						updatedSample.getAccessionNumber(),
    						getSysUserId(request), sampleOrderItem, patientInfo);
				}*/
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

			return mapping.findForward(FWD_FAIL);

		} finally {
			HibernateUtil.closeSession();
		}

		String sampleEditWritable = (String) request.getSession().getAttribute(
				IActionConstants.SAMPLE_EDIT_WRITABLE);

		if (GenericValidator.isBlankOrNull(sampleEditWritable) && GenericValidator.isBlankOrNull(criteria)) {
			return mapping.findForward(FWD_SUCCESS);
		} else {
			Map<String, String> params = new HashMap<String, String>();
			if (!GenericValidator.isBlankOrNull(sampleEditWritable)) {
                params.put("type", sampleEditWritable);
            }
            if (!GenericValidator.isBlankOrNull(criteria)) {
                params.put("criteria", criteria);
            }
			return getForwardWithParameters(mapping.findForward(FWD_SUCCESS),params);
		}
	}

	private void updatePatientInfo(PatientManagementInfo patientInfo,
			IPatientUpdate patientUpdate, SamplePatientUpdateData updateData,
			ActionMapping mapping, HttpServletRequest request)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		Patient existingPatient = sampleHumanDAO.getPatientForSample(updateData
				.getSample());
		updateData.setPatientId(existingPatient.getId());
		patientInfo.setPatientPK(existingPatient.getId());
		patientInfo.setPatientProcessingStatus("update");
		patientUpdate.setPatientUpdateStatus(patientInfo);
		updateData.setPatientErrors(patientUpdate.preparePatientData(mapping,
				request, patientInfo));
	}

	private void testAndInitializePatientForSaving(ActionMapping mapping,
			HttpServletRequest request, PatientManagementInfo patientInfo,
			IPatientUpdate patientUpdate, SamplePatientUpdateData updateData)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		patientUpdate.setPatientUpdateStatus(patientInfo);
		updateData
				.setSavePatient(patientUpdate.getPatientUpdateStatus() != PatientManagementUpdateAction.PatientUpdateStatus.NO_ACTION);

		if (updateData.isSavePatient()) {
			updateData.setPatientErrors(patientUpdate.preparePatientData(
					mapping, request, patientInfo));
		} else {
			updateData.setPatientErrors(new ActionMessages());
		}
	}

	private List<SampleItem> createSampleItemUpdateList(
			List<SampleEditItem> existingTests) {
		List<SampleItem> modifyList = new ArrayList<SampleItem>();

		for (SampleEditItem editItem : existingTests) {
			if (editItem.isSampleItemChanged()) {
				SampleItem sampleItem = sampleItemDAO.getData(editItem
						.getSampleItemId());
				if (sampleItem != null) {
					String collectionTime = editItem.getCollectionDate();
					String sampleItemExternalId = editItem.getSampleItemExternalId();
					if (sampleItemExternalId != null) {
					    sampleItem.setExternalId(sampleItemExternalId);
					}
					if (GenericValidator.isBlankOrNull(collectionTime)) {
						sampleItem.setCollectionDate(null);
					} else {
						collectionTime += " "
								+ (GenericValidator.isBlankOrNull(editItem
										.getCollectionTime()) ? "00:00"
										: editItem.getCollectionTime());
						sampleItem.setCollectionDate(DateUtil
								.convertStringDateToTimestamp(collectionTime));
					}
					sampleItem.setSysUserId(currentUserId);
					modifyList.add(sampleItem);
				}
			}
		}

		return modifyList;
	}

	private Analysis populateAnalysis(
			SampleTestCollection sampleTestCollection, Test test,
			String userSelectedTestSection, SampleAddService sampleAddService) {
		java.sql.Date collectionDateTime = DateUtil
				.convertStringDateTimeToSqlDate(sampleTestCollection.collectionDate);
		TestSection testSection = test.getTestSection();
		if (!GenericValidator.isBlankOrNull(userSelectedTestSection)) {
			testSection = testSectionDAO
					.getTestSectionById(userSelectedTestSection); // change
		}

		Panel panel = sampleAddService.getPanelForTest(test);

		Analysis analysis = new Analysis();
		analysis.setTest(test);
		analysis.setIsReportable(test.getIsReportable());
		analysis.setAnalysisType(DEFAULT_ANALYSIS_TYPE);
		analysis.setSampleItem(sampleTestCollection.item);
		analysis.setSysUserId(sampleTestCollection.item.getSysUserId());
		analysis.setRevision("0");
		analysis.setStartedDate(collectionDateTime == null ? DateUtil
				.getNowAsSqlDate() : collectionDateTime);
		analysis.setStatusId(StatusService.getInstance().getStatusID(
				AnalysisStatus.NotStarted));
		analysis.setTestSection(testSection);
		analysis.setPanel(panel);
		return analysis;
	}

	private List<SampleTestCollection> createAddSampleList(
			DynaActionForm dynaForm, SampleAddService sampleAddService) {

		String maxAccessionNumber = dynaForm.getString("maxAccessionNumber");
		if (!GenericValidator.isBlankOrNull(maxAccessionNumber)) {
			sampleAddService.setInitialSampleItemOrderValue(Integer
					.parseInt(maxAccessionNumber.split("-")[1]));
		}

		return sampleAddService.createSampleTestCollection();
	}

	private List<SampleItem> createCancelSampleList(List<SampleEditItem> list,
			List<Analysis> cancelAnalysisList) {
		List<SampleItem> cancelList = new ArrayList<SampleItem>();

		boolean cancelTest = false;

		for (SampleEditItem editItem : list) {
			if (editItem.getAccessionNumber() != null) {
				cancelTest = false;
			}
			if (cancelTest
					&& !cancelAnalysisListContainsId(editItem.getAnalysisId(),
							cancelAnalysisList)) {
				Analysis analysis = getCancelableAnalysis(editItem);
				cancelAnalysisList.add(analysis);
			}

			if (editItem.isRemoveSample()) {
				cancelTest = true;
				SampleItem sampleItem = getCancelableSampleItem(editItem);
				if (sampleItem != null) {
					cancelList.add(sampleItem);
				}
				if (!cancelAnalysisListContainsId(editItem.getAnalysisId(),
						cancelAnalysisList)) {
					Analysis analysis = getCancelableAnalysis(editItem);
					cancelAnalysisList.add(analysis);
				}
			}
		}

		return cancelList;
	}

	private SampleItem getCancelableSampleItem(SampleEditItem editItem) {
		String sampleItemId = editItem.getSampleItemId();
		SampleItem item = new SampleItem();
		item.setId(sampleItemId);
		sampleItemDAO.getData(item);

		if (item.getId() != null) {
			item.setStatusId(CANCELED_SAMPLE_STATUS_ID);
			item.setSysUserId(currentUserId);
			return item;
		}

		return null;
	}

	private boolean cancelAnalysisListContainsId(String analysisId,
			List<Analysis> cancelAnalysisList) {

		for (Analysis analysis : cancelAnalysisList) {
			if (analysisId.equals(analysis.getId())) {
				return true;
			}
		}

		return false;
	}

	private ActionMessages validateNewAccessionNumber(String accessionNumber) {
		ActionMessages errors = new ActionMessages();
		ValidationResults results = AccessionNumberUtil.correctFormat(
				accessionNumber, false);

		if (results != ValidationResults.SUCCESS) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
					"sample.entry.invalid.accession.number.format", null, null));
		} else if (AccessionNumberUtil.isUsed(accessionNumber)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionError(
					"sample.entry.invalid.accession.number.used", null, null));
		}

		return errors;
	}

	private Sample updateAccessionNumberInSample(DynaActionForm dynaForm) {
		Sample sample = sampleDAO.getSampleByAccessionNumber(dynaForm
				.getString("accessionNumber"));

		if (sample != null) {
			sample.setAccessionNumber(dynaForm.getString("newAccessionNumber"));
			sample.setSysUserId(currentUserId);
		}

		return sample;
	}

	private boolean accessionNumberChanged(DynaActionForm dynaForm) {
		String newAccessionNumber = dynaForm.getString("newAccessionNumber");

		return !GenericValidator.isBlankOrNull(newAccessionNumber)
				&& !newAccessionNumber.equals(dynaForm
						.getString("accessionNumber"));

	}

	private List<Analysis> createRemoveList(List<SampleEditItem> tests) {
		List<Analysis> removeAnalysisList = new ArrayList<Analysis>();

		for (SampleEditItem sampleEditItem : tests) {
			if (sampleEditItem.isCanceled()) {
				Analysis analysis = getCancelableAnalysis(sampleEditItem);
				removeAnalysisList.add(analysis);
			}
		}

		return removeAnalysisList;
	}

	private Analysis getCancelableAnalysis(SampleEditItem sampleEditItem) {
		Analysis analysis = new Analysis();
		analysis.setId(sampleEditItem.getAnalysisId());
		analysisDAO.getData(analysis);
		analysis.setSysUserId(currentUserId);
		analysis.setStatusId(StatusService.getInstance().getStatusID(
				AnalysisStatus.Canceled));
		return analysis;
	}

	private List<Analysis> createAddAanlysisList(List<SampleEditItem> tests) {
		List<Analysis> addAnalysisList = new ArrayList<Analysis>();

		for (SampleEditItem sampleEditItem : tests) {
			if (sampleEditItem.isAdd()) {

				Analysis analysis = newOrExistingCanceledAnalysis(sampleEditItem);

				if (analysis.getId() == null) {
					SampleItem sampleItem = new SampleItem();
					sampleItem.setId(sampleEditItem.getSampleItemId());
					sampleItemDAO.getData(sampleItem);
					analysis.setSampleItem(sampleItem);

					Test test = new Test();
					test.setId(sampleEditItem.getTestId());
					testDAO.getData(test);

					analysis.setTest(test);
					analysis.setRevision("0");
					analysis.setTestSection(test.getTestSection());
					analysis.setEnteredDate(DateUtil.getNowAsTimestamp());
					analysis.setIsReportable(test.getIsReportable());
					analysis.setAnalysisType("MANUAL");
					analysis.setStartedDate(DateUtil.getNowAsSqlDate());
				}

				analysis.setStatusId(StatusService.getInstance().getStatusID(
						AnalysisStatus.NotStarted));
				analysis.setSysUserId(currentUserId);

				addAnalysisList.add(analysis);
			}
		}

		return addAnalysisList;
	}

	private Analysis newOrExistingCanceledAnalysis(SampleEditItem sampleEditItem) {
		List<Analysis> canceledAnalysis = analysisDAO
				.getAnalysesBySampleItemIdAndStatusId(
						sampleEditItem.getSampleItemId(),
						CANCELED_TEST_STATUS_ID);

		for (Analysis analysis : canceledAnalysis) {
			if (sampleEditItem.getTestId().equals(analysis.getTest().getId())) {
				return analysis;
			}
		}

		return new Analysis();
	}

	private void persistSampleRejectionData(List<SampleTestCollection> stcList,
			Sample sample) {
		SampleQaEventDAO sampleQaEventDAO = new SampleQaEventDAOImpl();
		QaObservationDAO qaObservationDAO = new QaObservationDAOImpl();
		NoteDAO noteDAO = new NoteDAOImpl();

		QaEvent qaEvent = new QaEvent();
		QaEventDAO qaEventDAO = new QaEventDAOImpl();
		qaEvent.setQaEventName("Other");
		qaEvent = qaEventDAO.getQaEventByName(qaEvent);
		String otherId = qaEvent.getId();

		for (SampleTestCollection stc : stcList) {
			if (stc.rejections != null && !stc.rejections.isEmpty()) {
				for (QAService qaService : stc.rejections) {
					SampleQaEvent event = qaService.getSampleQaEvent();
					event.setSample(sample);
					sampleQaEventDAO.insertData(event);

					/*
					 * persist "Section" and "Authorizer" QaObservations for the
					 * SampleQaEvent
					 */
					for (QaObservation observation : qaService
							.getUpdatedObservations()) {
						observation.setObservedId(event.getId());
						qaObservationDAO.insertData(observation);
					}

					/* persist "Other Reason" note, if present */
					if (!GenericValidator.isBlankOrNull(otherId)
							&& otherId.equals(qaService.getQAEvent().getId())
							&& stc.rejectionOtherReason != null) {
						stc.rejectionOtherReason.setReferenceId(event.getId());
						noteDAO.insertData(stc.rejectionOtherReason);
					}
				}
			}
		}
	}

	protected String getPageTitleKey() {
		return StringUtil.getContextualKeyForKey("sample.edit.title");
	}

	protected String getPageSubtitleKey() {
		return StringUtil.getContextualKeyForKey("sample.edit.subtitle");
	}

	private boolean checkPatientOfSample(String accessionNumber) {
		boolean flag = false;
		Patient patient = new Patient();
		SampleHumanDAO patientDAO = new SampleHumanDAOImpl();
		patient = patientDAO.getPatientByAccessionNuber(accessionNumber);
		if (patient == null)
			flag = true;
		return flag;
	}

	private String addNewProvider(String userId, String personId) {
		Provider provider = new Provider();
		provider.setSelectedPersonId(personId);
		provider.setSysUserId(userId);
		ProviderDAO providerDAO = new ProviderDAOImpl();
		return providerDAO.insertDataWS(provider);
	}

	private void addNewSampleHuman(String sampId, String patientId,
			String providerId) {
		SampleHuman sampleHuman = new SampleHuman();
		sampleHuman.setSampleId(sampId);

		sampleHuman.setSysUserId(currentUserId);
		sampleHuman.setPatientId(patientId);
		if (providerId != null) {
			sampleHuman.setProviderId(providerId);
		}
		SampleHumanDAO humanDAO = new SampleHumanDAOImpl();
		humanDAO.insertData(sampleHuman);
	}

	private Patient getPatient(Sample sample) {
		return PatientUtil.getPatientForSample(sample);
	}

	private Provider getPrivider(Sample sample) {
		Provider provider = new Provider();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		return sampleHumanDAO.getProviderForSample(sample);
	}

}
