/**
 * 
 */
package vi.mn.state.health.lims.etor.data.dao;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.etor.data.valueholder.EtorDataMapping;

/**
 * @author dungtdo.sl
 *
 */
public interface EtorDataMappingDAO {
	public boolean insertData(EtorDataMapping etorDataMapping) throws LIMSRuntimeException;
}
