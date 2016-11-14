/**
 * 
 */
package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.ReportModelPCRBM05;

/**
 * @author nhuql.gv
 *
 */
public interface ReportPCRBM05DAO {
	/**
	 * Get report data by accession_number and collection_date
	 * @param accessNumberFrom
	 * @param accessNumberTo
	 * @param datefrom
	 * @param dateTo
	 * @return
	 * @throws LIMSRuntimeException
	 */
	public List<ReportModelPCRBM05> getListDataByNumberAndDate(String accessNumberFrom,String accessNumberTo,String datefrom, String dateTo , int testId , String organizationName) throws LIMSRuntimeException;
	
	/**
	 * Get report data by accession_number
	 * @param accessNumberFrom
	 * @param accessNumberTo
	 * @return
	 * @throws LIMSRuntimeException
	 */
	public List<ReportModelPCRBM05> getListDataByAccessionNumber(String accessNumberFrom,String accessNumberTo , int testId, String organizationName) throws LIMSRuntimeException;
	
	/**
	 * Get report data by collection_date
	 * @param datefrom
	 * @param dateTo
	 * @return
	 * @throws LIMSRuntimeException
	 */
	public List<ReportModelPCRBM05> getListDataByCollectionDate(String datefrom, String dateTo , int testId, String organizationName) throws LIMSRuntimeException;
}
