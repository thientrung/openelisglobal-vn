 /**
 * @(#) DistrictDeleteAction.java 01-00 Aug 16, 2016
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

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.district.valueholder.District;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.valueholder.UserSessionData;

/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class DistrictDeleteAction extends BaseAction {
    protected ActionForward performAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // The first job is to determine if we are coming to this action with an
        // ID parameter in the request. If there is no parameter, we are
        // creating a new Dictionary.
        // If there is a parameter present, we should bring up an existing
        // District to edit.
        String forward = "success";
        //System.out.println("I am in DictionaryDeleteAction");

        DynaActionForm dynaForm = (DynaActionForm) form;
        // get selected district
        String[] selectedIDs = (String[]) dynaForm.get("selectedIDs");

        //get sysUserId from login module
        UserSessionData usd = (UserSessionData)request.getSession().getAttribute(USER_SESSION_DATA);
        String sysUserId = String.valueOf(usd.getSystemUserId());
        
        List districts = new ArrayList();

        for (int i = 0; i < selectedIDs.length; i++) {
            District district = new District();
            district.setId(selectedIDs[i]);
            district.setSysUserId(sysUserId);
            districts.add(district.getDistrictEntry());
        }

        org.hibernate.Transaction tx = HibernateUtil.getSession().beginTransaction();
        ActionMessages errors = null;
        try {
            DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
            dictionaryDAO.deleteData(districts);
            // initialize the form
            dynaForm.initialize(mapping);
            tx.commit();
        } catch (LIMSRuntimeException lre) {
            LogEvent.logError("DictionaryDeleteAction","performAction()",lre.toString());           
            tx.rollback();
            
            errors = new ActionMessages();
            ActionError error = null;
            if (lre.getException() instanceof org.hibernate.StaleObjectStateException) {
                error = new ActionError("errors.OptimisticLockException", null, null);
            } else {
                error = new ActionError("errors.DeleteException", null, null);
            }
            errors.add(ActionMessages.GLOBAL_MESSAGE, error);
            saveErrors(request, errors);
            request.setAttribute(Globals.ERROR_KEY, errors);
            forward = FWD_FAIL;
            
        }  finally {
            HibernateUtil.closeSession();
        }                           
        if (forward.equals(FWD_FAIL))
            return mapping.findForward(forward);
        
        if ("true".equalsIgnoreCase(request.getParameter("close"))) {
            forward = FWD_CLOSE;
        }
        //System.out.println("I am in DictionaryMenuDeleteAction setting menuDefinition");
        request.setAttribute("menuDefinition", "DistrictMenuDefinition");

        return mapping.findForward(forward);
    }

    protected String getPageTitleKey() {
        return null;
    }

    protected String getPageSubtitleKey() {
        return null;
    }
}
