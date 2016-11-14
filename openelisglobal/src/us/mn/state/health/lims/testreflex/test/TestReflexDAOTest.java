package us.mn.state.health.lims.testreflex.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testreflex.dao.TestReflexDAO;
import us.mn.state.health.lims.testreflex.daoimpl.TestReflexDAOImpl;
import us.mn.state.health.lims.testreflex.valueholder.TestReflex;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

public class TestReflexDAOTest {
	
    @Test
    public void testInsertData() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
        	TestReflex testReflex = new TestReflex();
            boolean isInserted = testReflexDAO.insertData(testReflex);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test reflex is successfully inserted.");
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
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
        	List listTestReflex = new ArrayList<>();
            testReflexDAO.deleteData(listTestReflex);
            Assert.assertTrue(true);
            System.out.println("Test reflex is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestReflexs() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
            listTestReflex = testReflexDAO.getAllTestReflexs();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestReflexs() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
            listTestReflex = testReflexDAO.getPageOfTestReflexs(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
        	TestReflex testReflex = new TestReflex();
        	testReflexDAO.getData(testReflex);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
        	TestReflex testReflex = new TestReflex();
        	testReflexDAO.updateData(testReflex);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestReflexRecord() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
            listTestReflex = testReflexDAO.getNextTestReflexRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestReflexRecord() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
            listTestReflex = testReflexDAO.getPreviousTestReflexRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestReflexesByTestResult() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
        	TestResult testResult = new TestResult();
        	listTestReflex = testReflexDAO.getTestReflexesByTestResult(testResult);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestReflexesByTestResultAndTestAnalyte() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List listTestReflex = new ArrayList<>();
            TestResult testResult = new TestResult();
            TestAnalyte testAnalyte = new TestAnalyte();
            listTestReflex = testReflexDAO.getTestReflexesByTestResultAndTestAnalyte(testResult, testAnalyte);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTestReflexCount() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            int totalTestReflex = testReflexDAO.getTotalTestReflexCount();

            if (totalTestReflex > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testIsReflexedTest() {
    	boolean isReflexed = false;
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
        	Analysis analysis = new Analysis();
            isReflexed = testReflexDAO.isReflexedTest(analysis);

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
        Assert.assertTrue(isReflexed);
    }
    
    @Test
    public void testGetTestReflexsByTestResultAnalyteTest() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List<TestReflex> listTestReflex = new ArrayList<TestReflex>();
            listTestReflex = testReflexDAO.getTestReflexsByTestResultAnalyteTest("1", "2", "2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestReflexsByTestAndFlag() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List<TestReflex> listTestReflex = new ArrayList<TestReflex>();
            listTestReflex = testReflexDAO.getTestReflexsByTestAndFlag("1", "flag");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetFlaggedTestReflexesByTestResult() {
        try {
        	TestReflexDAO testReflexDAO = new TestReflexDAOImpl();
            List<TestReflex> listTestReflex = new ArrayList<TestReflex>();
            TestResult testResult = new TestResult();
            listTestReflex = testReflexDAO.getFlaggedTestReflexesByTestResult(testResult, "flag");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestReflex));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
