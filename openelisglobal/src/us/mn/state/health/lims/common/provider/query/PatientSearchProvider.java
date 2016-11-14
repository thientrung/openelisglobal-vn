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
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.provider.query.workerObjects.PatientSearchLocalAndClinicWorker;
import us.mn.state.health.lims.common.provider.query.workerObjects.PatientSearchLocalWorker;
import us.mn.state.health.lims.common.provider.query.workerObjects.PatientSearchWorker;
import us.mn.state.health.lims.common.services.ObservationHistoryService;
import us.mn.state.health.lims.common.services.ObservationHistoryService.ObservationType;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.SampleStatus;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

public class PatientSearchProvider extends BaseQueryProvider {

	protected AjaxServlet ajaxServlet = null;

	@Override
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String lastName = request.getParameter("lastName");
		String firstName = StringUtil.isNullorNill(request.getParameter("firstName"))
		        ?null:request.getParameter("firstName");
		if (FormFields.getInstance().useField(Field.SINGLE_NAME_FIELD)) {
			firstName = "%" + firstName + "%";
		}
		String STNumber = request.getParameter("STNumber");
		// N.B. This is a bad name, it is other than STnumber
		String subjectNumber = request.getParameter("subjectNumber");
		String nationalID = request.getParameter("nationalID");
		String externalID = request.getParameter("externalID");
		String labNumber = request.getParameter("labNumber");
		String guid = request.getParameter("guid");
		String suppressExternalSearch = request
				.getParameter("suppressExternalSearch");
		String patientID = null;

		String result = VALID;
		StringBuilder xml = new StringBuilder();
		// If we have a lab number then the patient is in the system and we just
		// have to get the patient and format the xml
		if (!GenericValidator.isBlankOrNull(labNumber)) {
			Patient patient = getPatientForLabNumber(labNumber);
			if (patient == null
					|| GenericValidator.isBlankOrNull(patient.getId())) {
				result = IActionConstants.INVALID;
				xml.append("No results were found for search.  Check spelling or remove some of the fields");
			} else {
				PatientSearchResults searchResults = getSearchResultsForPatient(patient);
				//tien add: 
				searchResults.setAccessionNumber(labNumber);
				//end
				PatientSearchWorker localWorker = new PatientSearchLocalWorker();
				localWorker.appendSearchResultRow(searchResults, xml);
			}
		} else {

			PatientSearchWorker worker = getAppropriateWorker(request,
					"true".equals(suppressExternalSearch));

			if (worker != null) {
				result = worker.createSearchResultXML(lastName, firstName,
						STNumber, subjectNumber, nationalID, externalID,
						patientID, guid, xml);
			} else {
				result = INVALID;
				xml.append("System is not configured correctly for searching for patients. Contact Administrator");
			}
		}
		ajaxServlet.sendData(xml.toString(), result, request, response);

	}

	private PatientSearchResults getSearchResultsForPatient(Patient patient) {
		PatientService service = new PatientService(patient);

		return new PatientSearchResults(BigDecimal.valueOf(Long
				.parseLong(patient.getId())), service.getFirstName(),
				service.getLastName(), service.getGender(), service.getDOB(),
				service.getNationalId(), service.getExternalId(),
				service.getSTNumber(), service.getSubjectNumber(),
				service.getGUID(),
				ObservationHistoryService.getMostRecentValueForPatient(
						ObservationType.REFERRERS_PATIENT_ID,
						service.getPatientId()));
	}

	private Patient getPatientForLabNumber(String labNumber) {

		SampleDAO sampleDAO = new SampleDAOImpl();
		Sample sample = sampleDAO.getSampleByAccessionNumber(labNumber);
//		String accessionNumber= sample.getAccessionNumber();

		if (sample != null && !GenericValidator.isBlankOrNull(sample.getId())) {
			// Handle configurations where orders can be entered without
			// specimens (i.e. VN), we don't want any patient info returned for
			// those orders
			SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
			Set<Integer> includedSampleStatusList = new HashSet<Integer>();
			includedSampleStatusList.add(Integer.parseInt(StatusService
					.getInstance().getStatusID(SampleStatus.Entered)));
			// Dung add show all
			includedSampleStatusList.add(Integer.parseInt(StatusService
					.getInstance().getStatusID(SampleStatus.Canceled)));
			List<SampleItem> sampleItemList = sampleItemDAO
					.getSampleItemsBySampleIdAndStatus(sample.getId(),
							includedSampleStatusList);
			if (!sampleItemList.isEmpty()) {
				SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
				return sampleHumanDAO.getPatientForSample(sample);
//				return sampleHumanDAO.getPatientByAccessionNuber(accessionNumber);
			}
		}

		return new Patient();
	}

	private PatientSearchWorker getAppropriateWorker(
			HttpServletRequest request, boolean suppressExternalSearch) {

		if (ConfigurationProperties.getInstance()
				.isCaseInsensitivePropertyValueEqual(
						Property.UseExternalPatientInfo, "false")
				|| suppressExternalSearch) {
			return new PatientSearchLocalWorker();
		} else {
			UserSessionData usd = (UserSessionData) request.getSession()
					.getAttribute(USER_SESSION_DATA);

			return new PatientSearchLocalAndClinicWorker(String.valueOf(usd
					.getSystemUserId()));
		}
	}

	@Override
	public void setServlet(AjaxServlet as) {
		this.ajaxServlet = as;
	}

	@Override
	public AjaxServlet getServlet() {
		return this.ajaxServlet;
	}

}
