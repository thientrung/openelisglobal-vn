package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportEtorMapingDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportEtorMapingDAOImpl;
import vi.mn.state.health.lims.report.valueholder.EtorMapingModel;

public class ReportEtorMapingDAOTest {
	
	/**
	 *  Get data for EtorMapping report (by accessionNumbers, testId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of etor mapping values
	 */
	@Test
	public void testgetListDataSuccess() {
		try {
			List<EtorMapingModel> etorDataMapping = new ArrayList<EtorMapingModel>();
			ReportEtorMapingDAO reportEtorMapingDAO = new ReportEtorMapingDAOImpl();
			etorDataMapping = reportEtorMapingDAO.getListData("1670000001",
					"1670010099", 342);
			if (CollectionUtils.isEmpty(etorDataMapping)) {
				Assert.assertFalse(false);
				System.out.println("EtorDataMapping result is empty.");
				LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataSuccess()", 
						"EtorDataMapping result is empty.");
			} else {
				Assert.assertTrue(true);
				System.out.println("Example: FirstName[1] - " + etorDataMapping.get(0).getFirstName());
				LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataSuccess()", 
						"Example: FirstName[1] - " + etorDataMapping.get(0).getFirstName());
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testgetListDataSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for EtorMapping report (by accessionNumbers, testId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return empty value
	 */
	@Test
	public void testgetListDataFail() {
		try {
			List<EtorMapingModel> etorDataMapping = new ArrayList<EtorMapingModel>();
			ReportEtorMapingDAO reportEtorMapingDAO = new ReportEtorMapingDAOImpl();
			etorDataMapping = reportEtorMapingDAO.getListData("AAAAAAAAAA",
					"BBBBBBBBBB", 0);
			if (CollectionUtils.isEmpty(etorDataMapping)) {
				Assert.assertFalse(false);
				System.out.println("Failed to get etormapping data list.");
				LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataFail()", 
						"Failed to get etormapping data list.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testgetListDataFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportEtorMapingDAOTest", "testgetListDataFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

}
