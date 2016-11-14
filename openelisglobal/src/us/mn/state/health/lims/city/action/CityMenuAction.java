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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;

/**
 * @author markaae.fr
 * 
 */
public class CityMenuAction extends BaseMenuAction {

	protected List createMenuList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List cities = new ArrayList();

		String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
		int startingRecNo = Integer.parseInt(stringStartingRecNo);
		
		String searchString = (String) request.getParameter("searchString");
		String doingSearch = (String) request.getParameter("search");

		DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
		
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES)) {
			cities = dictionaryDAO.getPagesOfSearchedDictionarys(startingRecNo, searchString, true, false);
		} else {
			cities = dictionaryDAO.getPageOfDictionarys(startingRecNo, true);
		}
		request.setAttribute("menuDefinition", "CityMenuDefinition");

        // set pagination variables for searched results
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES))
			request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(dictionaryDAO.getTotalSearchedDictionaryCount(searchString, IActionConstants.CITY_CATEGORY_ID.toString())));
		else 
		    request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(dictionaryDAO.getTotalCityCount()));
		
		request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
		int numOfRecs = 0;
		
		if (cities != null) {
			if (cities.size() > SystemConfiguration.getInstance().getDefaultPageSize()) {
				numOfRecs = SystemConfiguration.getInstance().getDefaultPageSize();
			} else {
				numOfRecs = cities.size();
			}
			numOfRecs--;
		}
		int endingRecNo = startingRecNo + numOfRecs;
		request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));
		request.setAttribute(MENU_SEARCH_BY_TABLE_COLUMN, "city.cityName");
			
		if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES) ) {
		   request.setAttribute(IN_MENU_SELECT_LIST_HEADER_SEARCH, "true");
		   request.setAttribute(MENU_SELECT_LIST_HEADER_SEARCH_STRING, searchString);
		}
		
		return cities;
	}
	
	protected String getPageTitleKey() {
		return "city.browse.title";
	}

	protected String getPageSubtitleKey() {
		return "city.browse.title";
	}

	protected int getPageSize() {
		return SystemConfiguration.getInstance().getDefaultPageSize();
	}

	protected String getDeactivateDisabled() {
		return "false";
	}
	
}
