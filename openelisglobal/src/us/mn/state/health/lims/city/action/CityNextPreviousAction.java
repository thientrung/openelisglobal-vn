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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;

/**
 * @author markaae.fr
 * 
 */
public class CityNextPreviousAction extends BaseAction {
	
	private boolean isNew = false;

	protected ActionForward performAction(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String forward = FWD_SUCCESS;
		request.setAttribute(ALLOW_EDITS_KEY, "true");
		request.setAttribute(PREVIOUS_DISABLED, "false");
		request.setAttribute(NEXT_DISABLED, "false");
		
		String id = request.getParameter(ID);
		
		isNew = (StringUtil.isNullorNill(id) || "0".equals(id));
		
		BaseActionForm dynaForm = (BaseActionForm) form;

		String start = (String) request.getParameter("startingRecNo");
		String direction = (String) request.getParameter("direction");

		Dictionary city = new Dictionary();
		city.setId(id);
        
		try {
			DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
			//retrieve analyte by id since the name may have changed
			dictionaryDAO.getData(city);

			if (FWD_NEXT.equals(direction)) {
				List cities = dictionaryDAO.getNextDictionaryRecord(city.getId());
				if (cities != null && cities.size() > 0) {
					city = (Dictionary) cities.get(0);
					dictionaryDAO.getData(city);
					if (cities.size() < 2) {
						// disable next button
						request.setAttribute(NEXT_DISABLED, "true");
					}
					id = city.getId();
				} else {
					// just disable next button
					request.setAttribute(NEXT_DISABLED, "true");
				}
			}
			if (FWD_PREVIOUS.equals(direction)) {
				List cities = dictionaryDAO.getPreviousDictionaryRecord(city.getId());
				if (cities != null && cities.size() > 0) {
					city = (Dictionary) cities.get(0);
					dictionaryDAO.getData(city);
					if (cities.size() < 2) {
						// disable previous button
						request.setAttribute(PREVIOUS_DISABLED, "true");
					}
					id = city.getId();
				} else {
					// just disable next button
					request.setAttribute(PREVIOUS_DISABLED, "true");
				}
			}

		} catch (LIMSRuntimeException lre) {
			LogEvent.logError("CityNextPreviousAction", "performAction()", lre.toString());    		
			request.setAttribute(ALLOW_EDITS_KEY, "false");
			// disable previous and next
			request.setAttribute(PREVIOUS_DISABLED, "true");
			request.setAttribute(NEXT_DISABLED, "true");
			forward = FWD_FAIL;
		} 
		
		if (forward.equals(FWD_FAIL))
			return mapping.findForward(forward);

		if (city.getId() != null && !city.getId().equals("0")) {
			request.setAttribute(ID, city.getId());
		}

		return getForward(mapping.findForward(forward), id, start);
	}

	protected String getPageTitleKey() {
       return null;
	}

	protected String getPageSubtitleKey() {
       return null;
	}

}
