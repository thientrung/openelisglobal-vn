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
package us.mn.state.health.lims.resultvalidation.action;

import java.util.ArrayList;
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
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.AnalysisStatus;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.login.dao.UserModuleDAO;
import us.mn.state.health.lims.login.daoimpl.UserModuleDAOImpl;
import us.mn.state.health.lims.resultvalidation.action.util.ResultValidationPaging;
import us.mn.state.health.lims.resultvalidation.bean.AnalysisItem;
import us.mn.state.health.lims.resultvalidation.util.ResultsValidationUtility;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.TestSection;

public class ResultValidationAction extends BaseResultValidationAction {

    @Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		BaseActionForm dynaForm = (BaseActionForm) form;

		request.getSession().setAttribute(SAVE_DISABLED, "true");
		String testSectionId = (request.getParameter("testSectionId"));
		String selectedTestId = (request.getParameter("test"));
		String accessionNumber = (request.getParameter("accessionNumber"));
		String receivedDate = (request.getParameter("receivedDate"));
		ResultValidationPaging paging = new ResultValidationPaging();
		String newPage = request.getParameter("page");

		TestSection ts = null;
		
		if (GenericValidator.isBlankOrNull(newPage)) {

			// Initialize the form.
			dynaForm.initialize(mapping);
			
			// load testSections for drop down
			TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            // load testnames for drop down
            TestDAO testDAO = new TestDAOImpl();
			//Tien add
			UserModuleDAO userModuleDAO = new UserModuleDAOImpl();
			
			if (!GenericValidator.isBlankOrNull(testSectionId)) {
                ts = testSectionDAO.getTestSectionById(testSectionId);
                PropertyUtils.setProperty(dynaForm, "testSectionId", testSectionId);
            }
			if (!GenericValidator.isBlankOrNull(receivedDate)) {
                PropertyUtils.setProperty(dynaForm, "receivedDate", receivedDate);
            }
			// Added by Mark 2016-08-29 04:18PM
            if (!GenericValidator.isBlankOrNull(selectedTestId)) {
                PropertyUtils.setProperty(dynaForm, "selectedTestId", selectedTestId);
            }
            // End of Modification
			
			//check if user is admin, load all test section
	        if(userModuleDAO.isUserAdmin(request)){
	            PropertyUtils.setProperty(dynaForm, "testSections", DisplayListService.getList(ListType.TEST_SECTION));
	            /*if (!GenericValidator.isBlankOrNull(testSectionId)) {     
                    PropertyUtils.setProperty(dynaForm, "testNames", DisplayListService.createTestListBySectionId(Integer.parseInt(testSectionId)));
	            } else {*/
	                PropertyUtils.setProperty(dynaForm, "testNames", DisplayListService.getList(ListType.ALL_TESTS));
	            //}
	        }
	        // if user is not admin, load test sections belong to this user
	        else {
                PropertyUtils.setProperty(dynaForm, "testSections", DisplayListService.createTestSectionListByUserId(currentUserId));
                /*if (!GenericValidator.isBlankOrNull(testSectionId)) {               
                    PropertyUtils.setProperty(dynaForm, "testNames", DisplayListService.createTestListBySectionId(Integer.parseInt(testSectionId)));
                } else {*/
                    PropertyUtils.setProperty(dynaForm, "testNames", DisplayListService.createTestListByUserId(Integer.parseInt(currentUserId)));
                //}
	        }
	        //end
//			PropertyUtils.setProperty(dynaForm, "testSections", DisplayListService.createTestSectionListByUserId(currentUserId));
			PropertyUtils.setProperty(dynaForm, "testSectionsByName", DisplayListService.getList(ListType.TEST_SECTION_BY_NAME));
			
			List<AnalysisItem> resultList = null;
			ResultsValidationUtility resultsValidationUtility = new ResultsValidationUtility();
			setRequestType(ts == null ? StringUtil.getMessageForKey("workplan.unit.types") : ts.getLocalizedName());
			
			if (!GenericValidator.isBlankOrNull(testSectionId) || !GenericValidator.isBlankOrNull(receivedDate)) {
				resultList = resultsValidationUtility.getResultValidationList( selectedTestId, getValidationStatus(), testSectionId, receivedDate);
				// Added by Mark 2016-08-25 - To set system_user_section values to resultList (AnalysisItem)
                for (AnalysisItem analysisItem : resultList) {
                    Analysis analysis = new Analysis();
                    AnalysisDAO analysisDAO = new AnalysisDAOImpl();
                    analysis = analysisDAO.getAnalysisById(analysisItem.getAnalysisId());
                    for (SystemUserSection system : getUserSection()) {
                        if ( analysis.getTestSection().getId().equals(system.getTestSection().getId()) ) {
                            system.setTestSectionId(system.getTestSection().getId());
                            analysisItem.setStatusUserSession(system);
                            break;
                        }
                    }
                }
                // End of Modification
			} else if (!GenericValidator.isBlankOrNull(accessionNumber)) {
				resultList = resultsValidationUtility.getResultValidationListByAccessionNumber( getValidationStatus(), accessionNumber );
			
			} else {
				resultList = new ArrayList<AnalysisItem>();
			}
			paging.setDatabaseResults(request, dynaForm, resultList);
			
		} else {
			paging.page(request, dynaForm, newPage);
		}
		//Not found message appears right after Validation by Unit is displayed (OEG-534)
        if (StringUtil.isNullorNill(testSectionId)) {
            PropertyUtils.setProperty(dynaForm, "displayTestSections", false);
        }
        
		return mapping.findForward(FWD_SUCCESS);
	}

	public List<Integer> getValidationStatus() {
        List<Integer> validationStatus = new ArrayList<Integer>();
        validationStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalAcceptance)));
        if( ConfigurationProperties.getInstance().isPropertyValueEqual( ConfigurationProperties.Property.VALIDATE_REJECTED_TESTS , "true")){
            validationStatus.add(Integer.parseInt(StatusService.getInstance().getStatusID(AnalysisStatus.TechnicalRejected)));
        }

        return validationStatus;
	}
	
}
