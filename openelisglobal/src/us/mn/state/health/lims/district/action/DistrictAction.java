/**
 * @(#) DistrictAction.java 01-00 Aug 15, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 15, 2016
 */
package us.mn.state.health.lims.district.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.daoimpl.DistrictDAOImpl;
import us.mn.state.health.lims.district.valueholder.District;

/**
 * Detail description of processing of this class.
 * 
 * @author trungtt.kd
 */
public class DistrictAction extends BaseAction {

    private boolean isNew = false;

    protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // The first job is to determine if we are coming to this action with an
        // ID parameter in the request. If there is no parameter, we are
        // creating a new district.
        // If there is a parameter present, we should bring up an existing
        // district to edit.
        String id = request.getParameter(ID);

        String forward = FWD_SUCCESS;
        request.setAttribute(ALLOW_EDITS_KEY, "true");
        request.setAttribute(PREVIOUS_DISABLED, "true");
        request.setAttribute(NEXT_DISABLED, "true");

        DynaActionForm dynaForm = (DynaActionForm) form;
        // initialize the form
        dynaForm.initialize(mapping);

        // District is a special type of dictionary
        District district = new District();
        
        if ((id != null) && (!"0".equals(id))) { 
            // this is an existing district

            district.setId(id);
            DistrictDAO districtDAO = new DistrictDAOImpl();
            districtDAO.getData(district);
            isNew = false; // this is to set correct page title

            // do we need to enable next or previous?
            List districts = districtDAO.getNextDistrictRecord(district.getId());
            if (districts.size() > 0) {
                // enable next button
                request.setAttribute(NEXT_DISABLED, "false");
            }
            districts = districtDAO.getPreviousDistrictRecord(district.getId());
            if (districts.size() > 0) {
                // enable next button
                request.setAttribute(PREVIOUS_DISABLED, "false");
            }
            // end of logic to enable next or previous button

        } else { 
            // this is a new district
            isNew = true;
            district.setDistrictEntry(new Dictionary());
            district.setCity(new Dictionary());
        }
        
        if (district.getId() != null && !district.getId().equals("0")) {
            request.setAttribute(ID, district.getId());
            // bugzilla 2062 initialize selectedDictionaryCategoryId
            if (district.getCity() != null) {
                district.setSelectedCityId(district.getCity().getId());
            }
        }
        
        PropertyUtils.copyProperties(form, district);
        // populate form from valueholder
        DictionaryDAO dictDAO = new DictionaryDAOImpl();
        List<Dictionary> cities = dictDAO.getDictionaryEntriesByCategoryId(IActionConstants.CITY_CATEGORY_ID.toString());
        
        PropertyUtils.setProperty(form, "cities", cities);
        PropertyUtils.setProperty(form, "districtName", district.getDistrictEntry().getDictEntry());
        
        return mapping.findForward(forward);

    }
    
    
    protected String getPageTitleKey() {
        if (isNew) {
            return "district.add.title";
        } else {
            return "district.edit.title";
        }
    }
    
    protected String getPageSubtitleKey() {
        if (isNew) {
            return "district.add.title";
        } else {
            return "district.edit.title";
        }
    }

}
