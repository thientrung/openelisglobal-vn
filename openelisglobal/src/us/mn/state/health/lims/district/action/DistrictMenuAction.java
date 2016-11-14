/**
 * @(#) DistrictMenuAction.java 01-00 Aug 16, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 16, 2016
 */
package us.mn.state.health.lims.district.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import us.mn.state.health.lims.common.action.BaseMenuAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.daoimpl.DistrictDAOImpl;

/**
 * Detail description of processing of this class.
 * 
 * @author trungtt.kd
 */
public class DistrictMenuAction extends BaseMenuAction {

    protected List createMenuList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // System.out.println("I am in ProgramMenuAction createMenuList()");

        List districts = new ArrayList();

        String stringStartingRecNo = (String) request.getAttribute("startingRecNo");
        int startingRecNo = Integer.parseInt(stringStartingRecNo);

        String searchString = (String) request.getParameter("searchString");
        String doingSearch = (String) request.getParameter("search");
        
        DistrictDAO districtDAO = new DistrictDAOImpl();
        districts = districtDAO.getPageOfDistricts(startingRecNo);
        DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();

        HttpSession session = request.getSession();

        // System.out.println("I am in ProgramMenuAction setting menuDefinition");
        
        if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES)) {
            districts = districtDAO.getPagesOfSearchedDistricts(startingRecNo, searchString);
        } else {
            districts = districtDAO.getPageOfDistricts(startingRecNo);
        }
        request.setAttribute("menuDefinition", "DistrictMenuDefinition");

        // bugzilla 1411 set pagination variables
        if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES)) {
            request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(
                    dictionaryDAO.getTotalSearchedDictionaryCount(searchString, IActionConstants.DISTRICT_CATEGORY_ID.toString())));
        } else {
            request.setAttribute(MENU_TOTAL_RECORDS, String.valueOf(districtDAO.getTotalDistrictCount()));
        }
        request.setAttribute(MENU_FROM_RECORD, String.valueOf(startingRecNo));
        
        request.setAttribute(MENU_SEARCH_BY_TABLE_COLUMN, "district.name");
        int numOfRecs = 0;
        if (districts != null ) {
            if (districts.size() > SystemConfiguration.getInstance().getDefaultPageSize()) {
                numOfRecs = SystemConfiguration.getInstance().getDefaultPageSize();
            } else {
                numOfRecs = districts.size();
            }
            numOfRecs--;
        }
        int endingRecNo = startingRecNo + numOfRecs;
        request.setAttribute(MENU_TO_RECORD, String.valueOf(endingRecNo));

        if (!StringUtil.isNullorNill(doingSearch) && doingSearch.equals(YES) ) {
            request.setAttribute(IN_MENU_SELECT_LIST_HEADER_SEARCH, "true");
            request.setAttribute(MENU_SELECT_LIST_HEADER_SEARCH_STRING, searchString);
        }
        
        return districts;
    }

    protected String getPageTitleKey() {
        return "district.browse.title";
    }

    protected String getPageSubtitleKey() {
        return "district.browse.title";
    }

    protected int getPageSize() {
        return SystemConfiguration.getInstance().getDefaultPageSize();
    }

    protected String getDeactivateDisabled() {
        return "true";
    }

}
