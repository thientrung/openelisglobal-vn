 /**
 * @(#) DistrictUpdateAction.java 01-00 Aug 17, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 17, 2016
 */
package us.mn.state.health.lims.district.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Transaction;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
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
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.daoimpl.DistrictDAOImpl;
import us.mn.state.health.lims.district.valueholder.District;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class DistrictUpdateAction extends BaseAction {
    
    /**
     * Local variable.
     */
    private boolean isNew = false;
    
    protected ActionForward performAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // The first job is to determine if we are coming to this action with an
        // ID parameter in the request. If there is no parameter, we are
        // creating a new District.
        // If there is a parameter present, we should bring up an existing
        // District to edit.

        String forward = FWD_SUCCESS;
        request.setAttribute(ALLOW_EDITS_KEY, "true");
        request.setAttribute(PREVIOUS_DISABLED, "false");
        request.setAttribute(NEXT_DISABLED, "false");

        String id = request.getParameter(ID);
        
        if (StringUtil.isNullorNill(id) || "0".equals(id)) {
            isNew = true;
        } else {
            isNew = false;
        }
        
        BaseActionForm dynaForm = (BaseActionForm) form;
        
        ActionMessages errors = dynaForm.validate(mapping, request);        
        if (errors != null && errors.size() > 0) {
            // System.out.println("Server side validation errors "
            // + errors.toString());
            saveErrors(request, errors);
            // since we forward to jsp - not Action we don't need to repopulate
            // the lists here
            return mapping.findForward(FWD_FAIL);
        }
        
        String start = (String) request.getParameter("startingRecNo");
        String direction = (String) request.getParameter("direction");
        
        District district = new District();
        //get sysUserId from currentUserId
        district.setSysUserId(currentUserId);             
        org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
        
        // populate value holder from form
        PropertyUtils.copyProperties(district, dynaForm);
        // Get cityId from property "selectedCityId"
        String selectedCityId = PropertyUtils
                .getProperty(dynaForm, "selectedCityId").toString();
        // Get City object from cityId
        DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
        Dictionary city = new Dictionary();
        city = dictionaryDAO.getDataForId(selectedCityId);
        
        // Get DictrictEntry from districtName property from form
        String districtName = PropertyUtils
                .getProperty(dynaForm, "districtName").toString();
        
        DistrictDAO distDAO = new DistrictDAOImpl();
        
        List<Dictionary> districtEntry = distDAO.getDistrictsEntyByName(districtName);
        
        // Set some property for district
        district.setCity(city);
        district.setDescription("");
        
        try {
            // Check DistrictEntry is already exists or not.
            if (districtEntry.size() == 0) {
                Dictionary newDistrictEntry = new Dictionary();
                newDistrictEntry.setDictEntry(districtName);
                DictionaryCategory dicCategory = new DictionaryCategory();
                DictionaryCategoryDAO dicCategoryDAO = 
                        new DictionaryCategoryDAOImpl();
                dicCategory = dicCategoryDAO
                        .getDictionaryCategoryByName("districts");
                newDistrictEntry.setDictionaryCategory(dicCategory);
                newDistrictEntry.setSysUserId(currentUserId);
                newDistrictEntry.setIsActive("Y");
                dictionaryDAO.insertData(newDistrictEntry);
                district.setDistrictEntry(dictionaryDAO
                        .getDictionaryByDictEntry(districtName));
            } else {
                district.setDistrictEntry(districtEntry.get(0));
            }
            if (!isNew) {
                // UPDATE
                distDAO.updateData(district);
            } else {
                // INSERT
                distDAO.insertData(district);
            }
            tx.commit();
            
        } catch (LIMSRuntimeException lre) {
            //bugzilla 2154
            LogEvent.logError("DistrictUpdateAction", "performAction()",
                    lre.toString());
            tx.rollback();
            errors = new ActionMessages();
            java.util.Locale locale = (java.util.Locale) request.getSession()
            .getAttribute("org.apache.struts.action.LOCALE");
            ActionError error = null;
            if (lre.getException()
                    instanceof org.hibernate.StaleObjectStateException) {
                // how can I get popup instead of struts error at the top of
                // page?
                // ActionMessages errors = dynaForm.validate(mapping, request);
                error = new ActionError("errors.OptimisticLockException", null,
                        null);
            } else {
                // bugzilla 1482
                if (lre.getException()
                        instanceof LIMSDuplicateRecordException) {
                    String messageKey = "district.browse.title";
                    String msg = ResourceLocator.getInstance()
                            .getMessageResources().getMessage(locale,
                                    messageKey);
                    error = new ActionError("errors.DuplicateRecord",
                            msg, null);

                } else {
                    error = new ActionError("errors.UpdateException", null,
                            null);
                }
            }
            errors.add(ActionMessages.GLOBAL_MESSAGE, error);
            saveErrors(request, errors);
            request.setAttribute(Globals.ERROR_KEY, errors);
            //bugzilla 1485: allow change and try updating again 
            //enable save button & disable previous and next
            request.setAttribute(PREVIOUS_DISABLED, "true");
            request.setAttribute(NEXT_DISABLED, "true");
            forward = FWD_FAIL;

        } finally {
            HibernateUtil.closeSession();
        }
        
        if (forward.equals(FWD_FAIL)) {
            return mapping.findForward(forward);
        }
        // initialize the form
        dynaForm.initialize(mapping);
        // repopulate the form from valueholder
        PropertyUtils.copyProperties(dynaForm, district);
        if ("true".equalsIgnoreCase(request.getParameter("close"))) {
            forward = FWD_CLOSE;
        }

        if (district.getId() != null && !district.getId().equals("0")) {
            request.setAttribute(ID, district.getId());

        }

        //bugzilla 1400
        if (isNew) {
            forward = FWD_SUCCESS_INSERT;
        }
        //bugzilla 1467 added direction for redirect to NextPreviousAction
        return getForward(mapping.findForward(forward), id, start, direction);
        
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
