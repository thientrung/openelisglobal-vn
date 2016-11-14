/**
 * 
 */
package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.EtorMapingModel;

/**
 * @author nhuql.gv
 *
 */
public interface ReportEtorMapingDAO {

	/**
	 * Get data by accession_number
	 * @param accessNumberFrom
	 * @param accessNumberTo
	 * @param testId
	 * @return
	 * @throws LIMSRuntimeException
	 */
	public List<EtorMapingModel> getListData(String accessNumberFrom, String accessNumberTo, int testId) throws LIMSRuntimeException;
}
