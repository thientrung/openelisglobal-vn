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
*/
package us.mn.state.health.lims.sample.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.DisplayListService.ListType;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.sample.valueholder.Sample;

/**
 * The QuickEntryAction class represents the initial 
 * Action for the QuickEntry form of the application.
 * 
 * @author	- Ken Rosha		08/29/2006
 * 02/21/2007 bugzilla 1757 clear out collections for typeOfSample/sourceOfSample since we use ajax
 */
public class QuickEntryAction
	extends BaseAction 
{
	protected ActionForward performAction(ActionMapping 		mapping,
										  ActionForm			form,
										  HttpServletRequest	request,
										  HttpServletResponse	response)
		throws Exception 
	{
		// This is a new quick entry sample
		String forward = "success";
		request.setAttribute(ALLOW_EDITS_KEY, "false");
		
		HttpSession session = request.getSession();
		ArrayList selectedTestIds = new ArrayList();
		session.setAttribute("selectedTestIds", selectedTestIds);

		BaseActionForm dynaForm = (BaseActionForm) form;

		// Initialize the form.
		dynaForm.initialize(mapping);
		
		PropertyUtils.setProperty(dynaForm, "currentDate", DateUtil.getCurrentDateAsText());
		PropertyUtils.setProperty(dynaForm, "sampleTypes", DisplayListService.getList(ListType.SAMPLE_TYPE_ACTIVE));
		PropertyUtils.setProperty(dynaForm, "sampleSources", DisplayListService.getList(ListType.SAMPLE_SOURCE));
		PropertyUtils.setProperty(dynaForm, "initConditionFormErrorsList", DisplayListService.getList(ListType.SAMPLE_ENTRY_INIT_COND_FORM_ERRORS));
		PropertyUtils.setProperty(dynaForm, "initConditionLabelErrorsList", DisplayListService.getList(ListType.SAMPLE_ENTRY_INIT_COND_LABEL_ERRORS));
		PropertyUtils.setProperty(dynaForm, "initConditionMiscList", DisplayListService.getList(ListType.SAMPLE_ENTRY_INIT_COND_MISC));
		PropertyUtils.setProperty(dynaForm, "rejectionReasonFormErrorsList", DisplayListService.getList(ListType.SAMPLE_ENTRY_REJECTION_FORM_ERRORS));
		PropertyUtils.setProperty(dynaForm, "rejectionReasonLabelErrorsList", DisplayListService.getList(ListType.SAMPLE_ENTRY_REJECTION_LABEL_ERRORS));
		PropertyUtils.setProperty(dynaForm, "rejectionReasonMiscList", DisplayListService.getList(ListType.SAMPLE_ENTRY_REJECTION_MISC));
        PropertyUtils.setProperty(dynaForm, "genderList", DisplayListService.getList(ListType.GENDERS));
        PropertyUtils.setProperty(dynaForm, "cityList", DisplayListService.getList(ListType.CITY));
        PropertyUtils.setProperty(dynaForm, "districtList", DisplayListService.getList(ListType.DISTRICT));
        PropertyUtils.setProperty(dynaForm, "departmentList", DisplayListService.getList(ListType.DEPARTMENT));
        PropertyUtils.setProperty(dynaForm, "patientTypeList", DisplayListService.getList(ListType.PATIENT_TYPE));

        // for backwards compatibility with non-modal version of sample entry
		PropertyUtils.setProperty(dynaForm, "initialSampleConditionList", DisplayListService.getList(ListType.INITIAL_SAMPLE_CONDITION));
		PropertyUtils.setProperty(dynaForm, "testSectionList", DisplayListService.getList(ListType.TEST_SECTION));

		Sample	sample	= new Sample();

		// Set received date and entered date to today's date
		Date today = Calendar.getInstance().getTime();
		String dateAsText = DateUtil.formatDateAsText(today);

		SystemConfiguration sysConfig = SystemConfiguration.getInstance();
		
		sample.setReceivedDateForDisplay(dateAsText);
		sample.setEnteredDateForDisplay(dateAsText);
		sample.setReferredCultureFlag(sysConfig.getQuickEntryDefaultReferredCultureFlag());
		sample.setStickerReceivedFlag(sysConfig.getQuickEntryDefaultStickerReceivedFlag());
		
		// default nextItemSequence to 1 (for clinical - always 1)
		sample.setNextItemSequence(sysConfig.getQuickEntryDefaultNextItemSequence());

		// revision is set to 0 on insert
		sample.setRevision(sysConfig.getQuickEntryDefaultRevision());

		sample.setCollectionTimeForDisplay(sysConfig.getQuickEntryDefaultCollectionTimeForDisplay());

		if (sample.getId() != null && !sample.getId().equals("0"))
		{
			request.setAttribute(ID, sample.getId());
		}

		// populate form from valueholder
		PropertyUtils.copyProperties(form, sample);

		PropertyUtils.setProperty(form, "currentDate",		dateAsText);
		request.setAttribute("menuDefinition", "BatchEntryDefinition");
		return mapping.findForward(forward);
	}
	//==============================================================

	protected String getPageTitleKey()
	{
		return "quick.entry.edit.title";
	}
	//==============================================================

	protected String getPageSubtitleKey()
	{
		return "quick.entry.edit.title";
	}
	//==============================================================
}
