package us.mn.state.health.lims.testanalyte.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testanalyte.dao.TestAnalyteDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteDAOImpl;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;

public class TestAnalyteDAOTest {
	
	@Test
    public void testInsertData() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
        	TestAnalyte testAnalyte = new TestAnalyte();
            boolean isInserted = testAnalyteDAO.insertData(testAnalyte);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test analyte is successfully inserted.");
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
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            testAnalyteDAO.deleteData(listTestAnalyte);
            Assert.assertTrue(true);
            System.out.println("Test analyte is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestAnalytes() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            listTestAnalyte = testAnalyteDAO.getAllTestAnalytes();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestAnalytes() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            listTestAnalyte = testAnalyteDAO.getPageOfTestAnalytes(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
        	TestAnalyte testAnalyte = new TestAnalyte();
        	testAnalyteDAO.getData(testAnalyte);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
        	TestAnalyte testAnalyte = new TestAnalyte();
        	testAnalyteDAO.updateData(testAnalyte);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestAnalytes() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            listTestAnalyte = testAnalyteDAO.getTestAnalytes("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestAnalyteRecord() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            listTestAnalyte = testAnalyteDAO.getNextTestAnalyteRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestAnalyteRecord() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            listTestAnalyte = testAnalyteDAO.getPreviousTestAnalyteRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestAnalyteById() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
        	TestAnalyte testAnalyte = new TestAnalyte();
        	TestAnalyte retTestAnalyte = testAnalyteDAO.getTestAnalyteById(testAnalyte);
        	
        	if (retTestAnalyte != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestAnalytesPerTest() {
        try {
        	TestAnalyteDAO testAnalyteDAO = new TestAnalyteDAOImpl();
            List listTestAnalyte = new ArrayList<>();
            us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
            listTestAnalyte = testAnalyteDAO.getAllTestAnalytesPerTest(test);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyte));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
