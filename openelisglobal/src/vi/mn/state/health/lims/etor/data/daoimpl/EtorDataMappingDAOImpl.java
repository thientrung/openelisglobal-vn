/**
 * 
 */
package vi.mn.state.health.lims.etor.data.daoimpl;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.etor.data.dao.EtorDataMappingDAO;
import vi.mn.state.health.lims.etor.data.valueholder.EtorDataMapping;

/**
 * @author dungtdo.sl
 *
 */
public class EtorDataMappingDAOImpl implements EtorDataMappingDAO{

	/* Add data for table etor from etor system
	 *type EtorDataMapping
	 */
	@Override
	public boolean insertData(EtorDataMapping etorDataMapping)
			throws LIMSRuntimeException {
		try {
			String id = (String) HibernateUtil.getSession().save(etorDataMapping);
			etorDataMapping.setEtorId(id);

			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

		} catch (Exception e) {
			// bugzilla 2154
			LogEvent.logError("EtorDataMapping", "insertData()", e.toString());
			throw new LIMSRuntimeException("Error in etorDataMapping EtorDataMapping()", e);
		}

		return true;
	}
	
}
