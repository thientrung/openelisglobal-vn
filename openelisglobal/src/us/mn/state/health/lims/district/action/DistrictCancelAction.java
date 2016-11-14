 /**
 * @(#) DistrictCancelAction.java 01-00 Aug 17, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 17, 2016
 */
package us.mn.state.health.lims.district.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import us.mn.state.health.lims.common.action.BaseAction;

/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class DistrictCancelAction extends BaseAction {
    
    protected ActionForward performAction(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DynaActionForm dynaForm = (DynaActionForm) form;

        return mapping.findForward(FWD_CLOSE);
    }
    
    protected String getPageTitleKey() {
        return "district.browse.title";
    }

    protected String getPageSubtitleKey() {
        return "district.browse.title";
    }

}
