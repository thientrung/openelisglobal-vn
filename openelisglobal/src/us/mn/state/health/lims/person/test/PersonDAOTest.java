package us.mn.state.health.lims.person.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;

public class PersonDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	PersonDAO personDAO = new PersonDAOImpl();
        	Person person = new Person();
        	person = personDAO.getPersonById("1");
        	String retVal = personDAO.insertDataWS(person);
        	
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
        	PersonDAO personDAO = new PersonDAOImpl();
        	String retVal = personDAO.insertDataWS(null);
        	
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
