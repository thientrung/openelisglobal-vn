package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportSampleResultDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportSampleResultDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportSampleResult;

public class ReportSampleResultDAOTest {

	/**
	 *  Get data for SampleResult report
	 *  Valid parameter - unit test for success scenario
	 *  Expect: return a list of ReportSampleResult data
	 */
	@Test
	public void testGetAllSamplePersionSuccess() {
		try {
		    List<ReportSampleResult> listReportSampleResult = new ArrayList<ReportSampleResult>();
		    ReportSampleResultDAO reportSampleResult = new ReportSampleResultDAOImpl();
		    listReportSampleResult = reportSampleResult.getAllSamplePersion("1670000001");
			
			if (CollectionUtils.isEmpty(listReportSampleResult)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionSuccess()", 
						"Return empty");
			} else {
				Assert.assertTrue(true);
				System.out.println("[testGetAllSamplePersionSuccess] listReportSampleResult[size] : " + String.valueOf(listReportSampleResult.size()));
				LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionSuccess()", 
						"listReportSampleResult[size] : " + String.valueOf(listReportSampleResult.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetAllSamplePersionSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for SampleResult report
	 *  Invalid parameter - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void testGetAllSamplePersionFail() {
		try {
		    List<ReportSampleResult> listReportSampleResult = new ArrayList<ReportSampleResult>();
		    ReportSampleResultDAO reportSampleResult = new ReportSampleResultDAOImpl();
		    listReportSampleResult = reportSampleResult.getAllSamplePersion("AAAAAAAAAA");
			
			if (CollectionUtils.isEmpty(listReportSampleResult)) {
				Assert.assertTrue(false);
				System.out.println("[testGetAllSamplePersionFail] - Return empty.");
				LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionFail()", 
						"Return empty.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetAllSamplePersionFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportSampleResultDAOTest", "testGetAllSamplePersionFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
}
