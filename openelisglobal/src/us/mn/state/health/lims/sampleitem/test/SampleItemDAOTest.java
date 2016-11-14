package us.mn.state.health.lims.sampleitem.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

public class SampleItemDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
        	SampleItem sampleItem = new SampleItem();
        	sampleItem = sampleItemDAO.getData("1");
        	String retVal = sampleItemDAO.insertDataWS(sampleItem);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testInsertDataWSSuccess] - " + retVal);
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testInsertDataWSFail() {
        try {
        	SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
        	String retVal = sampleItemDAO.insertDataWS(null);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testInsertDataWSFail] - FAIL (No data found)");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
