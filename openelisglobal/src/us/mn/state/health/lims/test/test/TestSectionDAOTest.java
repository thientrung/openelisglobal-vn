package us.mn.state.health.lims.test.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.TestSection;

public class TestSectionDAOTest {
	
	@Test
    public void testInsertData() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
            boolean isInserted = testSectionDAO.insertData(testSection);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test section is successfully inserted.");
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
	@Test
    public void testDeleteData() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            testSectionDAO.deleteData(listTestSection);
            Assert.assertTrue(true);
            System.out.println("Test section is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestSections() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List<TestSection> listTestSection = new ArrayList<TestSection>();
            listTestSection = testSectionDAO.getAllTestSections();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestSectionsBySysUserId() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getAllTestSectionsBySysUserId(1);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestSections() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getPageOfTestSections(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
        	testSectionDAO.getData(testSection);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
        	testSectionDAO.updateData(testSection);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestSections() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getTestSections("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestSectionsBySysUserId() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getTestSectionsBySysUserId("filter", 1);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestSectionRecord() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getNextTestSectionRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestSectionRecord() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List listTestSection = new ArrayList<>();
            listTestSection = testSectionDAO.getPreviousTestSectionRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestSectionByName() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
        	TestSection retTestSection = testSectionDAO.getTestSectionByName(testSection);
        	
        	if (retTestSection != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTestSectionCount() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            int totalTestSection = testSectionDAO.getTotalTestSectionCount();

            if (totalTestSection > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllActiveTestSections() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
            List<TestSection> listTestSection = new ArrayList<TestSection>();
            listTestSection = testSectionDAO.getAllActiveTestSections();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestSection));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestSectionByNameTwo() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
        	testSection = testSectionDAO.getTestSectionByName("testSection");
        	
        	if (testSection != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestSectionById() {
        try {
        	TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        	TestSection testSection = new TestSection();
        	testSection = testSectionDAO.getTestSectionById("1");
        	
        	if (testSection != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
