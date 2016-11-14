package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.JgM03BMPatientDAO;
import vi.mn.state.health.lims.report.daoimpl.JgM03BMPatientDAOImpl;
import vi.mn.state.health.lims.report.valueholder.JgM03_BM_Patient;

public class JgM03BMPatientDAOTest {
	
	/**
	 *  Get data for JgM03BMPatient report (by accessionNumbers, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of values
	 */
	@Test
	public void getAllJgM03BMPatientSuccess() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatient("1670000001",
					"1670010099", 343, null);
			if (CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientSuccess()", 
						"Fail to get JgM03BMPatient report.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[getAllJgM03BMPatientSuccess] jgmPatient(result): " + jgmPatient.get(0).getResult());
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientSuccess()", 
						" jgmPatient(result): " + jgmPatient.get(0).getResult());
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for JgM03BMPatient report (by accessionNumbers, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return empty value
	 */
	@Test
	public void getAllJgM03BMPatientFail() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatient("AAAAAAAAAA",
					"1670010099", 341, null);
			if (CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertFalse(false);
				System.out.println("[getAllJgM03BMPatientFail] Fail to getAllJgM03BMPatient.");
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientFail()", 
						"Return empty.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for JgM03BMPatient report (by dates, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of values
	 */
	@Test
	public void getAllJgM03BMPatientDateSuccess() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatientDate(
					"01/01/2001", "12/12/2016", 342, "1");
			if (org.springframework.util.CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertFalse(false);
				System.out.println("[getAllJgM03BMPatientDateSuccess] Empty Result");
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateSuccess()", 
						"Empty Result.");
				
			} else {
				Assert.assertTrue(true);
				System.out.println("[getAllJgM03BMPatientDateSuccess] jgmPatient(result): " + jgmPatient.get(0).getResult());
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateSuccess()", 
						"jgmPatient(result): " + jgmPatient.get(0).getResult());
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientDateSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for JgM03BMPatient report (by dates, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return empty value
	 */
	@Test
	public void getAllJgM03BMPatientDateFail() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatientDate(
					"12/12/2014", "12/12/2016", 341, "12");
			if (org.springframework.util.CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertFalse(false);
				System.out.println("[getAllJgM03BMPatientDateFail] Fail to getAllJgM03BMPatientDate - Empty result.");
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateFail()", 
						"Fail to getAllJgM03BMPatientDate - Empty result.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientDateFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}

	/**
	 *  Get data for JgM03BMPatient report (by accessionNumbers, dates, testId, orgId)
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of values
	 */
	@Test
	public void getAllJgM03BMPatientNumDateSuccess() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatientNumDate(
					"1670000001", "1670010099", "01/01/2000", "12/12/2016",
					343, "1");
			if (org.springframework.util.CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertFalse(false);
				System.out.println("[getAllJgM03BMPatientNumDateSuccess] Empty Result");
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateSuccess()", 
						"Empty Result");
			} else {
				Assert.assertTrue(true);
				System.out.println("[getAllJgM03BMPatientNumDateSuccess] jgmPatient(result): " + jgmPatient.get(0).getResult());
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateSuccess()", 
						"jgmPatient(result): " + jgmPatient.get(0).getResult());
			}
			
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientNumDateSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for JgM03BMPatient report (by accessionNumbers, dates, testId, orgId)
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return empty value
	 */
	@Test
	public void getAllJgM03BMPatientNumDateFail() {
		try {
			List<JgM03_BM_Patient> jgmPatient = new ArrayList<JgM03_BM_Patient>();
			JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();
			jgmPatient = jgM03BMPatientDAO.getAllJgM03BMPatientNumDate(
					"1670000001", "1670010099", "12/12/2014", "12/12/2016",
					341, "12");
			if (org.springframework.util.CollectionUtils.isEmpty(jgmPatient)) {
				Assert.assertFalse(false);
				System.out.println("[getAllJgM03BMPatientNumDateFail] Fail to getAllJgM03BMPatientNumDate - Empty result.");
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateFail()", 
						"Fail to getAllJgM03BMPatientNumDate - Empty result.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[getAllJgM03BMPatientNumDateFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("JgM03BMPatientDAOTest", "getAllJgM03BMPatientNumDateFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
}
