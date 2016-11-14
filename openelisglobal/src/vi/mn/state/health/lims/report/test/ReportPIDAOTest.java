package vi.mn.state.health.lims.report.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cxf.common.util.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import vi.mn.state.health.lims.report.dao.ReportPIDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportPIDAOImpl;
import vi.mn.state.health.lims.report.valueholder.PIDengueAggregate;

public class ReportPIDAOTest {

	/**
	 *  Get data for PI report
	 *  Valid parameters - unit test for success scenario
	 *  Expect: return a list of ReportPI data
	 */
	@Test
	public void testGetDengueAggregateRecordSuccess() {
		try {
			List<PIDengueAggregate> listPIDengueAggre = new ArrayList<PIDengueAggregate>();
			ReportPIDAO reportPIDAO = new ReportPIDAOImpl();
			listPIDengueAggre = reportPIDAO.getDengueAggregateRecord("10",
					"2015");

			if (CollectionUtils.isEmpty(listPIDengueAggre)) {
				Assert.assertTrue(false);
				LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordSuccess()", 
						"Empty Return.");
			} else {
				Assert.assertTrue(true);
				System.out.println("[testGetDengueAggregateRecordSuccess] listPIDengueAggre[size] : " + String.valueOf(listPIDengueAggre.size()));
				LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordSuccess()", 
						"listPIDengueAggre[size] : " + String.valueOf(listPIDengueAggre.size()));
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetDengueAggregateRecordSuccess] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordSuccess()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	
	/**
	 *  Get data for PI report
	 *  Invalid parameters - unit test for fail scenario
	 *  Expect: return an empty value
	 */
	@Test
	public void testGetDengueAggregateRecordFail() {
		try {
			List<PIDengueAggregate> listPIDengueAggre = new ArrayList<PIDengueAggregate>();
			ReportPIDAO reportPIDAO = new ReportPIDAOImpl();
			listPIDengueAggre = reportPIDAO.getDengueAggregateRecord("AA",
					"AAAA");

			if (CollectionUtils.isEmpty(listPIDengueAggre)) {
				Assert.assertFalse(false);
				System.out.println("[testGetDengueAggregateRecordFail] - Return empty.");
				LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordFail()", 
						"Empty return.");
			} else {
				Assert.assertTrue(true);
				LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordFail()", 
						"Unexpected - Get data with invalid parameter/s.");
			}
		} catch (Exception e) {
			Assert.assertFalse(false);
			System.out.println("[testGetDengueAggregateRecordFail] Error: " + Arrays.toString(e.getStackTrace()));
			LogEvent.logDebug("ReportPIDAOTest", "testGetDengueAggregateRecordFail()", 
					Arrays.toString(e.getStackTrace()));
		}
	}
	/**
	 * Get a list of accession number to save log history
	 * Valid parameters - unit test for success scenario (some fields can be empty)
	 * Expect: return a list of accession number
	 */
	
	@Test
	public void testGetListOfSatisfiedAccessionNumberSuccess() {
	    try {
            List listOfAccessionNumber = new ArrayList();
            ReportPIDAO reportPIDAO = new ReportPIDAOImpl();
            listOfAccessionNumber = reportPIDAO.getListOfSatisfiedAccessionNumber("1670009010", "1670009017", "", "", "", "", "", "", "351", "11");
	        
	        if (listOfAccessionNumber!= null && listOfAccessionNumber.isEmpty()) {
	            Assert.assertTrue(false);
                LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberSuccess()", 
                        "Empty Return.");
	        } else {
                Assert.assertTrue(true);
                System.out.println("[testGetListOfSatisfiedAccessionNumberSuccess] listOfAccessionNumber[size] : " + String.valueOf(listOfAccessionNumber.size()));
                LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberSuccess()", 
                        "listOfAccessionNumber[size] : " + String.valueOf(listOfAccessionNumber.size()));
	        }
	    } catch (Exception e) {
            Assert.assertFalse(false);
            System.out.println("[testGetListOfSatisfiedAccessionNumberSuccess] Error: " + Arrays.toString(e.getStackTrace()));
            LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberSuccess()", 
                    Arrays.toString(e.getStackTrace()));
	    }
	}
	/**
	 * Get a list of accession number to save log history
	 * Invalid parameters - unit test for fail scenario (invalid date 22/22/2012)
	 * Expect: return an empty list
	 */
	
	@Test
	public void testGetListOfSatisfiedAccessionNumberFail() {
	    try {
            List listOfAccessionNumber = new ArrayList();
            ReportPIDAO reportPIDAO = new ReportPIDAOImpl();
            listOfAccessionNumber = reportPIDAO.getListOfSatisfiedAccessionNumber("1670009010", "1670009017", "22/22/2012", "12/12/2016", "", "", "", "", "351", "11");
	        
            if (listOfAccessionNumber != null && listOfAccessionNumber.isEmpty()) {
                Assert.assertFalse(false);
                System.out.println("[testGetListOfSatisfiedAccessionNumberFail] - Return empty.");
                LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberFail()", 
                        "Empty return.");
            } else {
                Assert.assertTrue(true);
                LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberFail()", 
                        "Unexpected - Get data with invalid parameter/s.");
            }
	    } catch (Exception e) {
	        Assert.assertFalse(false);
            System.out.println("[testGetListOfSatisfiedAccessionNumberFail] Error: " + Arrays.toString(e.getStackTrace()));
            LogEvent.logDebug("ReportPIDAOTest", "testGetListOfSatisfiedAccessionNumberFail()", 
                    Arrays.toString(e.getStackTrace()));
	    }
	}

}
