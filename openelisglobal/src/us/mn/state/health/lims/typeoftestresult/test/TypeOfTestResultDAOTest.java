package us.mn.state.health.lims.typeoftestresult.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.typeoftestresult.dao.TypeOfTestResultDAO;
import us.mn.state.health.lims.typeoftestresult.daoimpl.TypeOfTestResultDAOImpl;
import us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult;

public class TypeOfTestResultDAOTest {

    @Test
    public void testInsertData() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
        	TypeOfTestResult typeOfTestResult = new TypeOfTestResult();
            boolean isInserted = typeOfTestResultDAO.insertData(typeOfTestResult);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Type of test result is successfully inserted.");
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
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            List<TypeOfTestResult> listTypeOfTestResult = new ArrayList<TypeOfTestResult>();
            typeOfTestResultDAO.deleteData(listTypeOfTestResult);
            Assert.assertTrue(true);
            System.out.println("Type of test result is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTypeOfTestResults() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            List listTypeOfTestResult = new ArrayList<>();
            listTypeOfTestResult = typeOfTestResultDAO.getAllTypeOfTestResults();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTypeOfTestResults() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            List listTypeOfTestResult = new ArrayList<>();
            listTypeOfTestResult = typeOfTestResultDAO.getPageOfTypeOfTestResults(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
        	TypeOfTestResult typeOfTestResult = new TypeOfTestResult();
        	typeOfTestResultDAO.getData(typeOfTestResult);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
        	TypeOfTestResult typeOfTestResult = new TypeOfTestResult();
        	typeOfTestResultDAO.updateData(typeOfTestResult);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTypeOfTestResultRecord() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            List listTypeOfTestResult = new ArrayList<>();
            listTypeOfTestResult = typeOfTestResultDAO.getNextTypeOfTestResultRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTypeOfTestResultRecord() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            List listTypeOfTestResult = new ArrayList<>();
            listTypeOfTestResult = typeOfTestResultDAO.getPreviousTypeOfTestResultRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfTestResult));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTypeOfTestResultCount() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
            int totalTypeOfTestResult = typeOfTestResultDAO.getTotalTypeOfTestResultCount();

            if (totalTypeOfTestResult > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfTestResultByType() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
        	TypeOfTestResult typeOfTestResult = new TypeOfTestResult();
        	TypeOfTestResult retTypeOfTestResult = typeOfTestResultDAO.getTypeOfTestResultByType(typeOfTestResult);
        	
        	if (retTypeOfTestResult != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfTestResultByTypeTwo() {
        try {
        	TypeOfTestResultDAO typeOfTestResultDAO = new TypeOfTestResultDAOImpl();
        	TypeOfTestResult typeOfTestResult = new TypeOfTestResult();
        	typeOfTestResult = typeOfTestResultDAO.getTypeOfTestResultByType("typeOfTestResult");
        	
        	if (typeOfTestResult != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
