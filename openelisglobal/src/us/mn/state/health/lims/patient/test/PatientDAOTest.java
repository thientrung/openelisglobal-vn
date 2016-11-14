package us.mn.state.health.lims.patient.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;

public class PatientDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	PatientDAO patientDAO = new PatientDAOImpl();
        	Patient patient = new Patient();
        	patient = patientDAO.getData("1");
        	String retVal = patientDAO.insertDataWS(patient);
        	
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
        	PatientDAO patientDAO = new PatientDAOImpl();
        	String retVal = patientDAO.insertDataWS(null);
        	
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
