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
package us.mn.state.health.lims.testmapping.daoimpl;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.testmapping.dao.TestMappingDAO;
import us.mn.state.health.lims.testmapping.valueholder.TestMapping;

/**
 * @author markaae.fr
 */
public class TestMappingDAOImpl extends BaseDAOImpl implements TestMappingDAO, Serializable {

    private static final long serialVersionUID = 1L;


    @SuppressWarnings("unchecked")
    public TestMapping getTestMappingByInternalTestId(String internalTestId) throws LIMSRuntimeException {
        TestMapping result = null;
        if (!GenericValidator.isBlankOrNull(internalTestId)) {
            try {
                String sql = "from TestMapping tm where tm.internalTestId = :internalTestId";

                org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
                query.setInteger("internalTestId", Integer.parseInt(internalTestId));
                List<TestMapping> list = query.list();
                if (list.size() > 0) {
                    result = list.get(0);
                }
                
                closeSession();
                
            } catch (Exception e) {
                handleException(e, "getTestMappingByInternalTestId");
                LogEvent.logError("TestMappingDAOImpl", "getTestMappingByInternalTestId()", e.toString());
            }
        }
        
        return result;
    }

}
