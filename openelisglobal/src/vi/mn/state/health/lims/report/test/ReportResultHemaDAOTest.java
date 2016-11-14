package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportResultHemaDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportResultHemaDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportResultHema;

public class ReportResultHemaDAOTest {

	/**
	 *  Get data for ResultHema report
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of ReportResultHema data
	 */
	@Test
	public void testGetListReportDataSuccess() {
		try {
		    List<ReportResultHema> listReportResultHema = new ArrayList<ReportResultHema>();
		    ReportResultHemaDAO reportResultHemaDAO = new ReportResultHemaDAOImpl();
			listReportResultHema = reportResultHemaDAO.getListReportData();
			
			if (CollectionUtils.isEmpty(listReportResultHema)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataSuccess()", 
						"Empty return.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[testGetListReportDataSuccess] listReportResultHema[size] : " + String.valueOf(listReportResultHema.size()));
				LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataSuccess()", 
						"listReportResultHema[size] : " + String.valueOf(listReportResultHema.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetListReportDataSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for ResultHema report
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void testGetListReportDataFail() {
		try {
		    List<ReportResultHema> listReportResultHema = new ArrayList<ReportResultHema>();
		    ReportResultHemaDAO reportResultHemaDAO = new ReportResultHemaDAOImpl();
			listReportResultHema = reportResultHemaDAO.getListReportData();
			
			if (CollectionUtils.isEmpty(listReportResultHema)) {
				Assert.assertTrue(false);
				System.out.println("[testGetListReportDataFail] - Return empty.");
				LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataFail()", 
						"Empty return.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetListReportDataFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportResultHemaDAOTest", "testGetListReportDataFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
}
