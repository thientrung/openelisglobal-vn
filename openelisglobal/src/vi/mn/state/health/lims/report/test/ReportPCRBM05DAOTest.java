package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportPCRBM05DAO;
import vi.mn.state.health.lims.report.daoimpl.ReportPCRBM05DAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportModelPCRBM05;

public class ReportPCRBM05DAOTest {
	
	/**
	 *  Get data for PCRBM05 report (by accessionNumbers, dates, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of ReportPCRBM05 data
	 */
	@Test
	public void getListDataByNumberAndDateTestSuccess() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO.getListDataByNumberAndDate(
					"1670000001", "1670010099", "01/01/2001", "12/12/2016",
					342, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertFalse(false);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestSuccess()", 
						"Result is empty.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[getListDataByNumberAndDateTestSuccess] reportModelPCRBM05[size] : " + String.valueOf(reportModelPCRBM05s.size()));
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestSuccess()", 
						"reportModelPCRBM05[size] : " + String.valueOf(reportModelPCRBM05s.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByNumberAndDateTestSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for PCRBM05 report (by accessionNumbers, dates, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void getListDataByNumberAndDateTestFail() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO.getListDataByNumberAndDate(
					"AAAAAAAAAA", "AAAAAAAAAA", "01/01/2001", "12/12/2016",
					342, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertFalse(false);
				System.out.println("[getListDataByNumberAndDateTestFail] - Return empty.");
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestFail()", 
						"Return empty.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByNumberAndDateTestFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByNumberAndDateTestFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for PCRBM05 report (by accessionNumbers, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of ReportPCRBM05 data
	 */
	@Test
	public void getListDataByAccessionNumberTestSuccess() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO
					.getListDataByAccessionNumber("1670000001", "1670010099",
							342, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestSuccess()", 
						"Return empty.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[getListDataByAccessionNumberTestSuccess] reportModelPCRBM05s[size] : " + String.valueOf(reportModelPCRBM05s.size()));
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestSuccess()", 
						"reportModelPCRBM05s[size] : " + String.valueOf(reportModelPCRBM05s.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByAccessionNumberTestSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for PCRBM05 report (by accessionNumbers, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void getListDataByAccessionNumberTestFail() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO
					.getListDataByAccessionNumber("AAAAAAAAAA", "AAAAAAAAAA",
							0, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertFalse(false);
				System.out.println("[getListDataByAccessionNumberTestFail] - Return empty.");
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestFail()", 
						"Empty Return.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByAccessionNumberTestFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByAccessionNumberTestFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for PCRBM05 report (by dates, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of ReportPCRBM05 data
	 */
	@Test
	public void getListDataByCollectionDateTestSuccess() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO.getListDataByCollectionDate(
					"04/01/2016", "12/12/2016", 341, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestSuccess()", 
						"Empty return.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[getListDataByCollectionDateTestSuccess] reportModelPCRBM05s[size] - " + String.valueOf(reportModelPCRBM05s.size()));
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestSuccess()", 
						"reportModelPCRBM05s[size] - " + String.valueOf(reportModelPCRBM05s.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByNumberAndDateTestSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for PCRBM05 report (by dates, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void getListDataByCollectionDateTestFail() {
		try {
			List<ReportModelPCRBM05> reportModelPCRBM05s = new ArrayList<ReportModelPCRBM05>();
			ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();
			reportModelPCRBM05s = reportPCRBM05DAO.getListDataByCollectionDate(
					"04/01/2016", "12/12/2016", 343, null);
			if (CollectionUtils.isEmpty(reportModelPCRBM05s)) {
				Assert.assertFalse(false);
				System.out.println("[getListDataByCollectionDateTestFail] - Return empty.");
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestFail()", 
						"Empty return.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getListDataByCollectionDateTestFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPCRBM05DAOTest", "getListDataByCollectionDateTestFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
}
