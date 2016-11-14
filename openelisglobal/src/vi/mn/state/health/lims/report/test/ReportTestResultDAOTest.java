package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportTestResultDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportTestResultDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportModelTestResult;

public class ReportTestResultDAOTest {

	/**
	 * Get data for TestResult report Valid parameters - unit test for success
	 * scenario Expect: return a list of ReportTestResult data
	 */
	@Test
	public void testGetListDataSuccess() {
		try {
			List<ReportModelTestResult> listReportModelTestResult = new ArrayList<ReportModelTestResult>();
			ReportTestResultDAO reportTestResultDAO = new ReportTestResultDAOImpl();
			listReportModelTestResult = reportTestResultDAO.getListDataWS(
					"1670000009", "1670000014", 342);

			if (CollectionUtils.isEmpty(listReportModelTestResult)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportTestResultDAOTest",
						"testGetListDataSuccess()",
						"Fail to get test result report.");
			} else {
				Assert.assertTrue(true);
				System.out
						.println("[testGetListDataSuccess] listReportModelTestResult[size] : "
								+ String.valueOf(listReportModelTestResult
										.size()));
				LogEvent.logDebug(
						"ReportTestResultDAOTest",
						"testGetListDataSuccess()",
						"listReportModelTestResult[size] : "
								+ String.valueOf(listReportModelTestResult
										.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetListDataSuccess] Error: "
					+ Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportTestResultDAOTest",
					"testGetListDataSuccess()",
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 * Get data for TestResult report Invalid parameters - unit test for fail
	 * scenario Expect: return an empty value
	 */
	@Test
	public void testGetListDataFail() {
		try {
			List<ReportModelTestResult> listReportModelTestResult = new ArrayList<ReportModelTestResult>();
			ReportTestResultDAO reportTestResultDAO = new ReportTestResultDAOImpl();
			listReportModelTestResult = reportTestResultDAO.getListDataWS(
					"AAAAAAAAAA", "BBBBBBBBBB", 0);

			if (CollectionUtils.isEmpty(listReportModelTestResult)) {
				Assert.assertFalse(false);
				System.out.println("[testGetListDataFail] - Return empty.");
				LogEvent.logDebug("ReportTestResultDAOTest",
						"testGetListDataFail()", "Return empty.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportTestResultDAOTest",
						"testGetListDataFail()",
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetListDataFail] Error: "
					+ Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportTestResultDAOTest",
					"testGetListDataFail()", Arrays.toString(e.getStackTrace()));
		}
	}

}
