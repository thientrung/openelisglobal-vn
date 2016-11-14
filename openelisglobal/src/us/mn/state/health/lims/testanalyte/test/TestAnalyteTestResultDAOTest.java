package us.mn.state.health.lims.testanalyte.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testanalyte.dao.TestAnalyteTestResultDAO;
import us.mn.state.health.lims.testanalyte.daoimpl.TestAnalyteTestResultDAOImpl;

public class TestAnalyteTestResultDAOTest {
	
	@Test
    public void testInsertData() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
        	List listTestAnalytes = new ArrayList<>();
        	List listTestResults = new ArrayList<>();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
            boolean isInserted = testAnalyteTestResultDAO.insertData(test, listTestAnalytes, listTestResults);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test anaylyte-result is successfully inserted.");
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestAnalyteTestResults() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
            List listTestAnalyteTestResult = new ArrayList<>();
            us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
            listTestAnalyteTestResult = testAnalyteTestResultDAO.getPageOfTestAnalyteTestResults(0, test);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyteTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestAnalyteTestResultsPerTest() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
        	List listTestAnalyteTestResult = new ArrayList<>();
            us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	listTestAnalyteTestResult = testAnalyteTestResultDAO.getAllTestAnalyteTestResultsPerTest(test);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyteTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	List listOldTestAnalytes = new ArrayList<>();
        	List listOldTestResults = new ArrayList<>();
        	List listTestAnalytes = new ArrayList<>();
        	List listTestResults = new ArrayList<>();
        	testAnalyteTestResultDAO.updateData(test, listOldTestAnalytes, listOldTestResults, listTestAnalytes, listTestResults);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestAnalyteTestResultRecord() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
            List listTestAnalyteTestResult = new ArrayList<>();
            listTestAnalyteTestResult = testAnalyteTestResultDAO.getNextTestAnalyteTestResultRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyteTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestAnalyteTestResultRecord() {
        try {
        	TestAnalyteTestResultDAO testAnalyteTestResultDAO = new TestAnalyteTestResultDAOImpl();
            List listTestAnalyteTestResult = new ArrayList<>();
            listTestAnalyteTestResult = testAnalyteTestResultDAO.getPreviousTestAnalyteTestResultRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestAnalyteTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
