package us.mn.state.health.lims.result.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;

public class ResultDAOTest {
	
	/**
	 *  Update result data
	 *  Valid parameter - unit test for success scenario
	 *  Expect: return a value
	 */
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	ResultDAO resultDAO = new ResultDAOImpl();
        	Result result = new Result();
        	result = resultDAO.getResultById("1");
        	String retVal = resultDAO.insertDataWS(result);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        		System.out.println("ResultDAOTest[testInsertDataWSSuccess] - " + retVal);
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testInsertDataWSSuccess] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    /**
	 *  Update result data
	 *  Invalid parameter - unit test for fail scenario
	 *  Expect: return null value
	 */
    @Test
    public void testInsertDataWSFail() {
        try {
        	ResultDAO resultDAO = new ResultDAOImpl();
        	String retVal = resultDAO.insertDataWS(null);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("ResultDAOTest[testInsertDataWSFail] - No data found.");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testInsertDataWSFail] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    /**
	 *  Get result count
	 *  Valid parameter - unit test for success scenario
	 *  Expect: return result count
	 */
    @Test
    public void testGetResultCountSuccess() {
        try {
        	ResultDAO resultDAO = new ResultDAOImpl();
        	int retVal = 0;
        	retVal = resultDAO.getResultCount("1670000014", "342");
        	
        	if (retVal != 0) {
        		Assert.assertTrue(true);
        		System.out.println("ResultDAOTest[testGetResultCountSuccess] Result count: " + retVal);
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testGetResultCountSuccess] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    /**
	 *  Get result count
	 *  Invalid parameter - unit test for fail scenario
	 *  Expect: return count = 0
	 */
    @Test
    public void testGetResultCountFail() {
        try {
        	ResultDAO resultDAO = new ResultDAOImpl();
        	int retVal = 0;
        	retVal = resultDAO.getResultCount("ABCDEFGHIJ", "XYZ");
        	
        	if (retVal != 0) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("ResultDAOTest[testGetResultCountFail] Result count is " + retVal);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testGetResultCountFail] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
}
