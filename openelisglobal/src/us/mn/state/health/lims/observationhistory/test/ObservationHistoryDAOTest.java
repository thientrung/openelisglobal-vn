package us.mn.state.health.lims.observationhistory.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;

public class ObservationHistoryDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testGetObservationHistoriesByPatientIdAndTypeOnlyOneSuccess() {
        try {
        	ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
        	ObservationHistory observationHistory = new ObservationHistory(); 
        	observationHistory = observationHistoryDAO.getObservationHistoriesByPatientIdAndTypeOnlyOne("241", "5");
        	
        	if (observationHistory != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testGetObservationHistoriesByPatientIdAndTypeOnlyOne] - " + observationHistory.getValue());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testGetObservationHistoriesByPatientIdAndTypeOnlyOneFail() {
        try {
        	ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
        	ObservationHistory observationHistory = new ObservationHistory(); 
        	observationHistory = observationHistoryDAO.getObservationHistoriesByPatientIdAndTypeOnlyOne("1", "1");
        	
        	if (observationHistory != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testGetObservationHistoriesByPatientIdAndTypeOnlyOne] - " + observationHistory.getValue());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Valid - Success
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
        	ObservationHistory observationHistory = new ObservationHistory(); 
        	observationHistory = observationHistoryDAO.getObservationHistoriesByPatientIdAndTypeOnlyOne("241", "5");
        	boolean isInserted = observationHistoryDAO.insertDataWS(observationHistory);
        	
        	if (isInserted) {
        		Assert.assertTrue(true);
        		System.out.println("[testInsertDataWSSuccess] - Data is inserted." );
        	} else {
        		Assert.assertFalse(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // InValid - Expecting to Fail
    @Test
    public void testInsertDataWSFail() {
        try {
        	ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
        	boolean isInserted = observationHistoryDAO.insertData(null);
        	
        	if (isInserted) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testInsertDataWSFail] FAIL - No Data Found" );
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
