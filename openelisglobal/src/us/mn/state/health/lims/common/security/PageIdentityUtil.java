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
package us.mn.state.health.lims.common.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;

public class PageIdentityUtil {

	private static final String REPORT_PARAMETER = "report";
	private static final String TYPE_PARAMETER = "type";
	private static final String ACCESSIONNUMBER_PARAMETER = "accessionNumber";
	private static final String TESTSECTION_PARAMETER = "testSectionId";

	public static boolean isMainPage(HttpServletRequest request) {
		String actionName = (String) request.getAttribute(IActionConstants.ACTION_KEY);

		return (IActionConstants.MAIN_PAGE.equals(actionName));
	}

	/*
	 * This is ripped and simplified from UserModuleDAOImp.  In it's most basic form it gets the ACTION_KEY
	 */
	public static String getActionName(HttpServletRequest request, boolean useParameterExtention) throws LIMSRuntimeException {

		String actionName = null;

		actionName = (String) request.getAttribute(IActionConstants.ACTION_KEY);

		if (actionName.equals("QuickEntryAddTestPopup")) {
			actionName = "QuickEntry";
		} else if (actionName.equals("TestManagementAddTestPopup")) {
			actionName = "TestManagement";
		} else if (actionName.equals("TestAnalyteTestResultAddDictionaryRGPopup")
				|| actionName.equals("TestAnalyteTestResultAddNonDictionaryRGPopup")
				|| actionName.equals("TestAnalyteTestResultAddRGPopup")
				|| actionName.equals("TestAnalyteTestResultAssignRGPopup")
				|| actionName.equals("TestAnalyteTestResultEditDictionaryRGPopup")
				|| actionName.equals("TestAnalyteTestResultEditNonDictionaryRGPopup")) {
			actionName = "TestAnalyteTestResult";
		} else if (actionName.equals("QaEventsEntryAddQaEventsToTestsPopup")
				|| actionName.equals("QaEventsEntryAddActionsToQaEventsPopup")) {
			actionName = "QaEventsEntry";
		}

		actionName = actionName.endsWith("Menu") ? actionName.substring(0, actionName.length() - 4) : actionName;

		if(useParameterExtention){
			/*	3 Scenarios/Cases to use ResultValidationByAccessionNumber: 
				(a) ?type=accessionnumber&test= 
				(b) ?testSectionId=&test=&type=&accessionNumber=1670000002 
				(c) ?testSectionId=2&test=&type=Biochemistry&receivedDate=12/12/2015 
			 */
			if (actionName.equalsIgnoreCase("ResultValidation") && (request.getParameter(TYPE_PARAMETER).equalsIgnoreCase("accessionnumber") || 
					!GenericValidator.isBlankOrNull(request.getParameter(ACCESSIONNUMBER_PARAMETER)) || !GenericValidator.isBlankOrNull(request.getParameter(TESTSECTION_PARAMETER)))) {
				actionName = "ResultValidationByAccessionNumber";
				
			} else {
				// Special Case - need to use ResultValidation (?type=biochemistry&test=) 
				String parameter = request.getParameter(TYPE_PARAMETER);
				String testSectionParameter = request.getParameter(TESTSECTION_PARAMETER);
				
				if (GenericValidator.isBlankOrNull(parameter)) {
					parameter = request.getParameter(REPORT_PARAMETER);
				}
				
				if ((actionName.equalsIgnoreCase("LogbookResults") || actionName.equalsIgnoreCase("Workplan")) && 
						!GenericValidator.isBlankOrNull(parameter) && !GenericValidator.isBlankOrNull(testSectionParameter)) {
					//actionName = actionName;
				} else {
					if (!GenericValidator.isBlankOrNull(parameter)) {
						actionName += ":" + parameter;
					}
				}
			}
		}

		return actionName;
	}

}
