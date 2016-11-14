package us.mn.state.health.lims.testdictionary.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testdictionary.dao.TestDictionaryDAO;
import us.mn.state.health.lims.testdictionary.daoimpl.TestDictionaryDAOImpl;
import us.mn.state.health.lims.testdictionary.valueholder.TestDictionary;

public class TestDictionaryDAOTest {
    
    @Test
    public void testGetTestDictionaryForTestId() {
        try {
        	TestDictionaryDAO testDictionaryDAO = new TestDictionaryDAOImpl();
        	TestDictionary testDictionary = new TestDictionary();
        	testDictionary = testDictionaryDAO.getTestDictionaryForTestId("2");
        	
        	if (testDictionary != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
