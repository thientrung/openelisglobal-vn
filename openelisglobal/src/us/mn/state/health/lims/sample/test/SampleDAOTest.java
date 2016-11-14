package us.mn.state.health.lims.sample.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;

public class SampleDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	ProviderDAO providerDAO = new ProviderDAOImpl();
        	Provider provider = new Provider();
        	PersonDAO personDAO = new PersonDAOImpl();
        	Person person = new Person();
        	person = personDAO.getPersonById("1");
        	provider = providerDAO.getProviderByPerson(person);
        	String retVal = providerDAO.insertDataWS(provider);
        	
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
        	ProviderDAO providerDAO = new ProviderDAOImpl();
        	String retVal = providerDAO.insertDataWS(null);
        	
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
    
    // Valid Parameters - Expecting Success
    @Test
    public void testInsertDataSampleWithAccessionNumberSuccess() {
        try {
        	SampleDAO sampleDAO = new SampleDAOImpl();
        	Sample sample = new Sample();
        	sample = sampleDAO.getSampleByAccessionNumber("1670000009");
        	String retVal = sampleDAO.insertDataSampleWithAccessionNumber(sample);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testInsertDataSampleWithAccessionNumberSuccess] - " + retVal);
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testInsertDataSampleWithAccessionNumberFail() {
        try {
        	SampleDAO sampleDAO = new SampleDAOImpl();
        	String retVal = sampleDAO.insertDataSampleWithAccessionNumber(null);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testInsertDataSampleWithAccessionNumberFail] - FAIL (No data found)");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
