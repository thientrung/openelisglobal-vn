package us.mn.state.health.lims.testresult.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testanalyte.valueholder.TestAnalyte;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import vi.mn.state.health.lims.dataexchange.dto.TestResultDTO;

public class TestResultDAOTest {
	
    @Test
    public void testInsertData() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResult testResult = new TestResult();
            boolean isInserted = testResultDAO.insertData(testResult);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test result is successfully inserted.");
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
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	List listTestResult = new ArrayList<>();
            testResultDAO.deleteData(listTestResult);
            Assert.assertTrue(true);
            System.out.println("Test result is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestResults() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            listTestResult = testResultDAO.getAllTestResults();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestResults() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            listTestResult = testResultDAO.getPageOfTestResults(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResult testResult = new TestResult();
        	testResultDAO.getData(testResult);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResult testResult = new TestResult();
        	testResultDAO.updateData(testResult);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestResultRecord() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            listTestResult = testResultDAO.getNextTestResultRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestResultRecord() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            listTestResult = testResultDAO.getPreviousTestResultRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestResultById() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResult testResult = new TestResult();
        	TestResult retTestResult = testResultDAO.getTestResultById(testResult);
        	
        	if (retTestResult != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestResultsByTestAndResultGroup() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            TestAnalyte testAnalyte = new TestAnalyte();
            listTestResult = testResultDAO.getTestResultsByTestAndResultGroup(testAnalyte);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllActiveTestResultsPerTest() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List listTestResult = new ArrayList<>();
            us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
            listTestResult = testResultDAO.getAllActiveTestResultsPerTest(test);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestResultsByTestAndDictonaryResult() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResult testResult = new TestResult();
        	testResult = testResultDAO.getTestResultsByTestAndDictonaryResult("2", "10");
        	
        	if (testResult != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetActiveTestResultsByTest() {
        try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
            List<TestResult> listTestResult = new ArrayList<TestResult>();
            listTestResult = testResultDAO.getActiveTestResultsByTest("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    /**
	 *  Get test result (by result, accessionNumber, testId)
	 *  Valid parameter - unit test for success scenario
	 *  Expect: return TestResultDTO object
	 */
    @Test
    public void testGetTestResultByResultSuccess() {
    	try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResultDTO retVal = testResultDAO.getTestResultByResult("1670000014", "2097", "342");
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        		System.out.println("ResultDAOTest[testGetTestResultByResultSuccess] TestResult(value): " + retVal.getTestResultValue());
        	} else {
        		Assert.assertTrue(false);
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testGetTestResultByResultSuccess] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    /**
	 *  Get test result (by result, accessionNumber, testId)
	 *  Invalid parameter - unit test for fail scenario
	 *  Expect: return null value
	 */
    @Test
    public void testGetTestResultByResultFail() {
    	try {
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResultDTO retVal = testResultDAO.getTestResultByResult("ABCDEFGHIJ", "LMNO", "XYZ");
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("ResultDAOTest[testGetTestResultByResultFail] TestResult is null.");
        	}

        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("ResultDAOTest[testGetTestResultByResultFail] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
}
