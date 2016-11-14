/**
 * 
 */
package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.ReportModelTestResult;

/**
 * @author nhuql.gv
 * 
 */
public interface ReportTestResultDAO {

	/**
	 * Get data by accession_number
	 * 
	 * @param accessNumberFrom
	 * @param accessNumberTo
	 * @param testId
	 * @return
	 * @throws LIMSRuntimeException
	 */
	public List<ReportModelTestResult> getListDataWS(String accessNumberFrom,
			String accessNumberTo, int testId) throws LIMSRuntimeException;

	public List<ReportModelTestResult> getListData(String accessNumberFrom,
			String accessNumberTo, int testId, String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange) throws LIMSRuntimeException;
}
