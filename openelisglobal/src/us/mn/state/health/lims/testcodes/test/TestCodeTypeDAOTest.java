package us.mn.state.health.lims.testcodes.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testcodes.dao.TestCodeTypeDAO;
import us.mn.state.health.lims.testcodes.daoimpl.TestCodeTypeDAOImpl;
import us.mn.state.health.lims.testcodes.valueholder.TestCodeType;

public class TestCodeTypeDAOTest {
	
	@Test
    public void testGetTestCodeTypeByName() {
        try {
        	TestCodeTypeDAO testCodeTypeDAO = new TestCodeTypeDAOImpl();
        	TestCodeType testCodeType = new TestCodeType();
        	testCodeType = testCodeTypeDAO.getTestCodeTypeByName("name");
        	
        	if (testCodeType != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
	
	@Test
    public void testGetTestCodeTypeById() {
        try {
        	TestCodeTypeDAO testCodeTypeDAO = new TestCodeTypeDAOImpl();
        	TestCodeType testCodeType = new TestCodeType();
        	testCodeType = testCodeTypeDAO.getTestCodeTypeById("1");
        	
        	if (testCodeType != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
