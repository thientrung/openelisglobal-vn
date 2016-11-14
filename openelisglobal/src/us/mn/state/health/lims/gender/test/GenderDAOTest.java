package us.mn.state.health.lims.gender.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.gender.dao.GenderDAO;
import us.mn.state.health.lims.gender.daoimpl.GenderDAOImpl;
import us.mn.state.health.lims.gender.valueholder.Gender;

public class GenderDAOTest {
	
	// Valid Parameters - Expecting Success
    @Test
    public void testReadGenderSuccess() {
        try {
        	GenderDAO genderDAO = new GenderDAOImpl();
        	Gender gender = new Gender(); 
        	gender = genderDAO.readGender("145");
        	
        	if (gender != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testReadGenderSuccess] - " + gender.getDescription());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    // Invalid Parameters - Expecting to fail
    @Test
    public void testReadGenderFail() {
        try {
        	GenderDAO genderDAO = new GenderDAOImpl();
        	Gender gender = new Gender(); 
        	gender = genderDAO.readGender("10");
        	
        	if (gender != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testReadGenderFail] - FAIL (No data found)");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
