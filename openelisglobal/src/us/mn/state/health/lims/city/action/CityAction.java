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
package us.mn.state.health.lims.city.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;

/**
 * @author markaae.fr
 * 
 */
public class CityAction extends BaseAction {

	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// The first job is to determine if we are coming to this action with an
		// ID parameter in the request. If there is no parameter, we are
		// creating a new City.
		// If there is a parameter present, we should bring up an existing
		// City to edit.
		String id = request.getParameter(ID);

		String forward = FWD_SUCCESS;
		request.setAttribute(RECORD_FROZEN_EDIT_DISABLED_KEY, "false");
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "true");
		request.setAttribute(NEXT_DISABLED, "true");

		BaseActionForm dynaForm = (BaseActionForm) form;
		// initialize the form
		dynaForm.initialize(mapping);

		Dictionary dictionary = new Dictionary();
		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		
		isNew = (id == null) || "0".equals(id);

		if (!isNew) {
			dictionary.setId(id);
			dictionaryDAO.getData(dictionary);

		} else { 
			// this is a new city; default isActive to 'Y'
			dictionary.setIsActive(YES);
		}

		if (dictionary.getId() != null && !dictionary.getId().equals("0")) {
			request.setAttribute(ID, dictionary.getId());
		}
		PropertyUtils.copyProperties(form, dictionary);
		PropertyUtils.setProperty(form, "cityName", dictionary.getDictEntry());

		return mapping.findForward(forward);
	}

	protected String getPageTitleKey() {
		if (isNew) {
			return "city.add.title";
		} else {
			return "city.edit.title";
		}
	}

	protected String getPageSubtitleKey() {
		if (isNew) {
			return "city.add.subtitle";
		} else {
			return "city.edit.subtitle";
		}
	}

}
