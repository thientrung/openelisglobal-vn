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
package us.mn.state.health.lims.city.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.resources.ResourceLocator;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.dictionarycategory.dao.DictionaryCategoryDAO;
import us.mn.state.health.lims.dictionarycategory.daoimpl.DictionaryCategoryDAOImpl;
import us.mn.state.health.lims.dictionarycategory.valueholder.DictionaryCategory;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * @author markaae.fr
 *
 */
public class CityUpdateAction extends BaseAction {

	private boolean isNew = false;

	private static DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();

	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");

		String id = request.getParameter(ID);
		isNew = (StringUtil.isNullorNill(id) || "0".equals(id));

		BaseActionForm dynaForm = (BaseActionForm) form;
		
		// server-side validation (validation.xml)
		ActionMessages errors = dynaForm.validate(mapping, request);		
		if (errors != null && errors.size() > 0) {
			saveErrors(request, errors);
			return mapping.findForward(FWD_FAIL);
		}

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");
		String cityName = (String) dynaForm.get("cityName");
		String cityIsActive = (String) dynaForm.get("isActive");

		DictionaryCategoryDAO dictionaryCategoryDAO = new DictionaryCategoryDAOImpl();
		Dictionary dictionary = new Dictionary();
		dictionary.setId(id);
		dictionaryDAO.getData(dictionary);
		dictionary.setDictEntry(cityName);
		dictionary.setIsActive(cityIsActive);

		DictionaryCategory dictionaryCategory = new DictionaryCategory();
		dictionaryCategory.setId(String.valueOf(IActionConstants.CITY_CATEGORY_ID));
		dictionaryCategoryDAO.getData(dictionaryCategory);
		dictionary.setDictionaryCategory(dictionaryCategory);
		
		dictionary.setSortOrder(IActionConstants.CITY_SORT_ORDER);
		dictionary.setSysUserId(currentUserId);
		
		PropertyUtils.copyProperties(dictionary, dynaForm);
		org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
		
		try {
			if (!isNew) {
				dictionaryDAO.updateData(dictionary, false);
			} else {
				dictionaryDAO.insertData(dictionary);
			}
			tx.commit();
			
		} catch (LIMSRuntimeException lre) {
			LogEvent.logError("CityUpdateAction", "performAction()", lre.toString());
			tx.rollback();
			errors = new ActionMessages();
			ActionError error = null;
			
			if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
				error = new ActionError("errors.OptimisticLockException", null, null);
				
			} else {
				if (lre.getException() instanceof LIMSDuplicateRecordException) {
					java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute("org.apache.struts.action.LOCALE");
					String messageKey = "city.city";
					String msg = ResourceLocator.getInstance().getMessageResources().getMessage(locale, messageKey);
					error = new ActionError("errors.DuplicateRecord.activeonly", msg, null);

				} else {
					error = new ActionError("errors.UpdateException", null, null);
				}
			}

			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			saveErrors(request, errors);
			request.setAttribute(Globals.ERROR_KEY, errors);

			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;

		} finally {
			HibernateUtil.closeSession();
		}
		
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		dynaForm.initialize(mapping);
		PropertyUtils.copyProperties(dynaForm, dictionary);

		if ("true".equalsIgnoreCase(request.getParameter("close"))) {
			forward = FWD_CLOSE;
		}
		if (dictionary.getId() != null && !dictionary.getId().equals("0")) {
			request.setAttribute(ID, dictionary.getId());
		}
		if (isNew) {
			forward = FWD_SUCCESS_INSERT;
		}

		return getForward(mapping.findForward(forward), id, start, direction);
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
			return "city.add.title";
		} else {
			return "city.edit.title";
		}
	}

}
