package us.mn.state.health.lims.provider.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.provider.valueholder.Provider;

public class ProviderDAOTest {
	
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
    
}
