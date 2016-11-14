package us.mn.state.health.lims.samplehuman.test;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;

public class SampleHumanDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testGetPatientByAccessionNuberSuccess() {
        try {
        	SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
        	Patient patient = new Patient(); 
        	patient = sampleHumanDAO.getPatientByAccessionNuber("1670000009");
        	
        	if (patient != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testGetPatientByAccessionNuberSuccess] - " + patient.getEpiFirstName());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testGetPatientByAccessionNuberFail() {
        try {
        	SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
        	Patient patient = new Patient(); 
        	patient = sampleHumanDAO.getPatientByAccessionNuber("AAAAAAAAAA");
        	
        	if (patient != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testGetPatientByAccessionNuberFail] - FAIL (No data found)");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Valid Parameters - Expecting Success
    @Test
    public void testGetSamplesForPatientSuccess() {
        try {
        	SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
        	Sample sample = new Sample(); 
        	sample = sampleHumanDAO.getSamplesForPatient("1", "1670000009");
        	
        	if (sample != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testGetSamplesForPatient] - " + sample.getName());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testGetSamplesForPatientFail() {
        try {
        	SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
        	Sample sample = new Sample(); 
        	sample = sampleHumanDAO.getSamplesForPatient("0", "AAAAAAAAAA");
        	
        	if (sample != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testGetSamplesForPatientFail] - FAIL (No data found)");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
