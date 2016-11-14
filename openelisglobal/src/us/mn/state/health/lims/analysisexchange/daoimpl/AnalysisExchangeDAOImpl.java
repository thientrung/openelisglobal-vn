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
package us.mn.state.health.lims.analysisexchange.daoimpl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisexchange.dao.AnalysisExchangeDAO;
import us.mn.state.health.lims.analysisexchange.valueholder.AnalysisExchange;
import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;

/**
 * @author markaae.fr
 */
public class AnalysisExchangeDAOImpl extends BaseDAOImpl implements AnalysisExchangeDAO, Serializable {

    private static final long serialVersionUID = 1L;
   
	public boolean insertData(AnalysisExchange analysisExchange) throws LIMSRuntimeException {
		try {
			String id = (String) HibernateUtil.getSession().save(analysisExchange);
			analysisExchange.setId(id);

			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = analysisExchange.getSysUserId();
			String tableName = "ANALYSIS_EXCHANGE";
			auditDAO.saveNewHistory(analysisExchange, sysUserId, tableName);

			closeSession();

		} catch (Exception e) {
			LogEvent.logError("AnalysisExchangeDAOImpl", "insertData()", e.toString());
			throw new LIMSRuntimeException("Error in AnalysisExchange insertData()", e);
		}

		return true;
	}
	
	public void updateData(AnalysisExchange analysisExchange, Analysis analysis, String sysUserId) throws LIMSRuntimeException {
        AnalysisExchange oldData = readAnalysisExchange(analysisExchange.getId());
        try {
            //AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
            //String event = IActionConstants.AUDIT_TRAIL_UPDATE;
            //String tableName = "ANALYSIS_EXCHANGE";
            AnalysisExchange newAnalysisExchange  = new AnalysisExchange();
            newAnalysisExchange = getAnalysisExchangeById(analysisExchange.getId());
            newAnalysisExchange.setSysUserId(sysUserId);
            newAnalysisExchange.setExchangedBy(sysUserId);
            newAnalysisExchange.setStatus(analysis.getStatusId());
            newAnalysisExchange.setInternalAnalysisId(analysis.getId());
            newAnalysisExchange.setExchangedDate(new Timestamp(new java.util.Date().getTime()));
            newAnalysisExchange.setLastupdated(new Timestamp(new java.util.Date().getTime()));
            
            HibernateUtil.getSession().merge(newAnalysisExchange);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(newAnalysisExchange);
			HibernateUtil.getSession().refresh(newAnalysisExchange);
			
            //HibernateUtil.getSession().update(newAnalysisExchange);
            //auditDAO.saveHistory(newAnalysisExchange, oldData, sysUserId, event, tableName);
            
            //HibernateUtil.getSession().flush();
            //HibernateUtil.getSession().clear();
            
        } catch (Exception e) {
            LogEvent.logError("AnalysisExchangeDAOImpl", "updateData()", e.toString());
            throw new LIMSRuntimeException("Error in AnalysisExchange updateData()", e);
        }
    }
	
	 public AnalysisExchange getAnalysisExchangeById(String analysisExchangeId) throws LIMSRuntimeException {
        try {
        	AnalysisExchange analysisExchange = (AnalysisExchange) HibernateUtil.getSession().get(AnalysisExchange.class, analysisExchangeId);
            return analysisExchange;
        } catch (Exception e) {
            handleException(e, "getAnalysisExchangeById");
        }

        return null;
    }
	
	public AnalysisExchange readAnalysisExchange(String idString) {
	    AnalysisExchange analysisExchange = null;
        try {
            analysisExchange = (AnalysisExchange) HibernateUtil.getSession().get(AnalysisExchange.class, idString);
            closeSession();
            
        } catch (Exception e) {
            LogEvent.logError("AnalysisExchangeDAOImpl", "readAnalysisExchange()", e.toString());
            throw new LIMSRuntimeException("Error in AnalysisExchange readAnalysisExchange()", e);
        }

        return analysisExchange;
    }
	
    @SuppressWarnings("unchecked")
    public AnalysisExchange getAnalysisExchangeByInternalAnalysisId(String internalAnalysisId) throws LIMSRuntimeException {
        AnalysisExchange result = null;
        if (!GenericValidator.isBlankOrNull(internalAnalysisId)) {
            try {
                String sql = "from AnalysisExchange ae where ae.internalAnalysisId = :internalAnalysisId";

                org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
                query.setInteger("internalAnalysisId", Integer.parseInt(internalAnalysisId));
                List<AnalysisExchange> list = query.list();
                if (list.size() > 0) {
                    result = list.get(0);
                }
                //closeSession();
                
            } catch (Exception e) {
                handleException(e, "getAnalysisExchangeByInternalAnalysisId");
                LogEvent.logError("AnalysisExchangeDAOImpl", "getAnalysisExchangeByInternalAnalysisId()", e.toString());
            }
        }
        
        return result;
    }
    
}
