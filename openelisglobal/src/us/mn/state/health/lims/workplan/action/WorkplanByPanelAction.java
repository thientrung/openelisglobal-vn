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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.services.ObservationHistoryService;
import us.mn.state.health.lims.common.services.ObservationHistoryService.ObservationType;
import us.mn.state.health.lims.common.services.QAService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.test.beanItems.TestResultItem;

public class WorkplanByPanelAction extends BaseWorkplanAction {

	private final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private final PanelDAO panelDAO = new PanelDAOImpl();
	private final PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

	@Override
	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		BaseActionForm dynaForm = (BaseActionForm) form;

		request.getSession().setAttribute(SAVE_DISABLED, "true");

		dynaForm.initialize(mapping);
		/* -- change from "panel" to Panel
		 * in Workplan By Panel, the header before was "Workplan panel" was inconsist format style
		 * now it is "Workplan Panel" -- */
		setRequestType("Panel");

		List<TestResultItem> workplanTests;

		String panelID = request.getParameter("selectedSearchID");
        String receivedDate = request.getParameter("receivedDate");
        String completedDate = request.getParameter("completedDate");
        String startedDate =  request.getParameter("startedDate");

		if (!GenericValidator.isBlankOrNull(panelID)/* && 
		        (!GenericValidator.isBlankOrNull(receivedDate) || !GenericValidator.isBlankOrNull(resultDate))*/) {
			String panelName = getPanelName(panelID);
			workplanTests = getWorkplanByPanel(panelID, receivedDate, startedDate, completedDate);
			
			// Added by markaae.fr 2016-10-03 10:57AM
            PropertyUtils.setProperty(dynaForm, "receivedDate", receivedDate);
            PropertyUtils.setProperty(dynaForm, "completedDate", completedDate);
            PropertyUtils.setProperty(dynaForm, "startedDate", startedDate);
            // End of Modification
			
			//Dung fix selected combo box
			// resultsLoadUtility.sortByAccessionAndSequence(workplanTests);
			PropertyUtils.setProperty(dynaForm, "selectedSearchID", panelID);
			PropertyUtils.setProperty(dynaForm, "testName", panelName);
			PropertyUtils.setProperty(dynaForm, "workplanTests", workplanTests);
			PropertyUtils.setProperty(dynaForm, "searchFinished", Boolean.TRUE);
			
		} else {
			// no search done, set workplanTests as empty
			PropertyUtils
					.setProperty(dynaForm, "searchFinished", Boolean.FALSE);
			PropertyUtils.setProperty(dynaForm, "testName", null);
			PropertyUtils.setProperty(dynaForm, "workplanTests",
					new ArrayList<TestResultItem>());
		}
		//Dung add fix bug workplan by panel
		// load testSections for drop down
		PropertyUtils.setProperty(dynaForm, "testSections",
				DisplayListService.getList(ListType.TEST_SECTION));
		PropertyUtils.setProperty(dynaForm, "testSectionsByName",
				DisplayListService.getList(ListType.TEST_SECTION_BY_NAME));

		PropertyUtils.setProperty(dynaForm, "workplanType", "panel");
		PropertyUtils.setProperty(dynaForm, "searchTypes",
				DisplayListService.getList(DisplayListService.ListType.PANELS));
		PropertyUtils.setProperty(dynaForm, "searchLabel",
				StringUtil.getMessageForKey("workplan.panel.types"));
		PropertyUtils.setProperty(dynaForm, "searchAction",
				"WorkPlanByPanel.do");

		return mapping.findForward(FWD_SUCCESS);
	}

	@SuppressWarnings("unchecked")
	private List<TestResultItem> getWorkplanByPanel(String panelId, String receivedDate, String startedDate, String completedDate ) {

		List<TestResultItem> workplanTestList = new ArrayList<TestResultItem>();
		// check for patient name addition
		boolean addPatientName = isPatientNameAdded();

		if ( !(GenericValidator.isBlankOrNull(panelId) || panelId.equals("0"))/* && 
                (!GenericValidator.isBlankOrNull(receivedDate) || !GenericValidator.isBlankOrNull(resultDate))*/ ) {

			List<PanelItem> panelItems = panelItemDAO
					.getPanelItemsForPanel(panelId);
			List<Object[]> testList = new ArrayList<Object[]>();
			String testIdList= "";
			for (PanelItem panelItem : panelItems) {
//				testList = (List<Object[]>) analysisDAO
//						.getAllAnalysisByTestAndStatusAndDate(panelItem.getTest()
//								.getId(), statusList, receivedDate, startedDate, completedDate);
				testIdList += testIdList.equals("") ? panelItem.getTest().getId() : "," + panelItem.getTest().getId();
			}
			testList = (List<Object[]>) analysisDAO.getAllAnalysisByTestAndStatusAndDate(testIdList, statusList, receivedDate, startedDate, completedDate); 

			for (Object[] analysisInfo : testList) {
				String analysisId = (String) ((BigDecimal)analysisInfo[0]).toString();
				Analysis analysis = (Analysis) analysisDAO.getAnalysisById(analysisId);
				String emergency = (String) (analysisInfo[3] != null ? analysisInfo[3] : "");
				String projectName = (String) (analysisInfo[4] != null ? analysisInfo[4] : "");
				
				TestResultItem testResultItem = new TestResultItem();
				Sample sample = analysis.getSampleItem().getSample();
				testResultItem.setAccessionNumber(sample.getAccessionNumber());
				testResultItem.setPatientInfo(getSubjectNumber(analysis));
				testResultItem.setNextVisitDate(ObservationHistoryService
						.getValueForSample(ObservationType.NEXT_VISIT_DATE, sample.getId()));
				testResultItem.setReceivedDate(getReceivedDateDisplay(sample));
				testResultItem.setStartedDate(analysis.getStartedDateForDisplay());
				testResultItem.setCompletedDate(analysis.getCompletedDateForDisplay());
				testResultItem.setEmergency(emergency);
				testResultItem.setProjectName(projectName);
				
				testResultItem.setTestName(TestService.getUserLocalizedTestName(analysis.getTest()));
				testResultItem.setNonconforming(QAService.isAnalysisParentNonConforming(analysis));
				if (addPatientName) testResultItem.setPatientName(getPatientName(analysis));

				workplanTestList.add(testResultItem);
			}
		

//			Collections.sort(workplanTestList,
//					new Comparator<TestResultItem>() {
//						@Override
//						public int compare(TestResultItem o1, TestResultItem o2) {
//							if (o1.getEmergency().compareTo(o2.getEmergency()) != 0) {
//								return o1.getEmergency().compareTo(o2.getEmergency());
//							} else if (o1.getProjectName().compareTo(o2.getProjectName()) != 0) {
//								return o1.getProjectName().compareTo(o2.getProjectName());
//							}
//							return o1.getAccessionNumber().compareTo(o2.getAccessionNumber());
//						}
//
//					});

			String currentAccessionNumber = null;
			int sampleGroupingNumber = 0;

			int newIndex = 0;
			int newElementsAdded = 0;
			int workplanTestListOrigSize = workplanTestList.size();

			for (int i = 0; newIndex < (workplanTestListOrigSize + newElementsAdded); i++) {

				TestResultItem testResultItem = workplanTestList.get(newIndex);

				if (!testResultItem.getAccessionNumber().equals(
						currentAccessionNumber)) {
					sampleGroupingNumber++;
					if (addPatientName) {
						addPatientNameToList(testResultItem, workplanTestList,
								newIndex, sampleGroupingNumber);
						newIndex++;
						newElementsAdded++;
					}

					currentAccessionNumber = testResultItem
							.getAccessionNumber();
				}
				testResultItem.setSampleGroupingNumber(sampleGroupingNumber);
				newIndex++;
			}

		}

		return workplanTestList;
	}

	private void addPatientNameToList(TestResultItem firstTestResultItem,
			List<TestResultItem> workplanTestList, int insertPosition,
			int sampleGroupingNumber) {
		TestResultItem testResultItem = new TestResultItem();
		testResultItem.setAccessionNumber(firstTestResultItem
				.getAccessionNumber());
		testResultItem.setPatientInfo(firstTestResultItem.getPatientInfo());
		testResultItem.setReceivedDate(firstTestResultItem.getReceivedDate());
		// Add Patient Name to top of test list
		testResultItem.setTestName(firstTestResultItem.getPatientName());
		testResultItem.setSampleGroupingNumber(sampleGroupingNumber);
		testResultItem.setServingAsTestGroupIdentifier(true);
		workplanTestList.add(insertPosition, testResultItem);

	}

	private boolean isPatientNameAdded() {
		return ConfigurationProperties.getInstance().isPropertyValueEqual(
				Property.configurationName, "Haiti LNSP");
	}

	private String getPanelName(String panelId) {
		return panelDAO.getNameForPanelId(panelId);
	}

}
