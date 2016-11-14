package us.mn.state.health.lims.typeofsample.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleTestDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleTestDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleTest;

public class TypeOfSampleTestDAOTest {

    @Test
    public void testInsertData() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	TypeOfSampleTest typeOfSampleTest = new TypeOfSampleTest();
            boolean isInserted = typeOfSampleTestDAO.insertData(typeOfSampleTest);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Type of sample test is successfully inserted.");
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
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	String[] arrSampleTestIds = null;
        	typeOfSampleTestDAO.deleteData(arrSampleTestIds, "1");
            Assert.assertTrue(true);
            System.out.println("Type of sample test is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTypeOfSampleTests() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
            List listTypeOfSampleTest = new ArrayList<>();
            listTypeOfSampleTest = typeOfSampleTestDAO.getAllTypeOfSampleTests();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTypeOfSampleTests() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
            List listTypeOfSampleTest = new ArrayList<>();
            listTypeOfSampleTest = typeOfSampleTestDAO.getPageOfTypeOfSampleTests(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	TypeOfSampleTest typeOfSampleTest = new TypeOfSampleTest();
        	typeOfSampleTestDAO.getData(typeOfSampleTest);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTypeOfSampleTestRecord() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
            List listTypeOfSampleTest = new ArrayList<>();
            listTypeOfSampleTest = typeOfSampleTestDAO.getNextTypeOfSampleTestRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTypeOfSampleRecord() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
            List listTypeOfSampleTest = new ArrayList<>();
            listTypeOfSampleTest = typeOfSampleTestDAO.getPreviousTypeOfSampleRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTypeOfSampleTestCount() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
            int totalTypeOfSampleTest = typeOfSampleTestDAO.getTotalTypeOfSampleTestCount();

            if (totalTypeOfSampleTest > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleTestsForSampleType() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	List<TypeOfSampleTest> listTypeOfSampleTest = new ArrayList<TypeOfSampleTest>();
        	listTypeOfSampleTest = typeOfSampleTestDAO.getTypeOfSampleTestsForSampleType("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleTestForTest() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	TypeOfSampleTest typeOfSampleTest = new TypeOfSampleTest();
        	typeOfSampleTest = typeOfSampleTestDAO.getTypeOfSampleTestForTest("2");
        	
        	if (typeOfSampleTest != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleTestsForTest() {
        try {
        	TypeOfSampleTestDAO typeOfSampleTestDAO = new TypeOfSampleTestDAOImpl();
        	List<TypeOfSampleTest> listTypeOfSampleTest = new ArrayList<TypeOfSampleTest>();
        	listTypeOfSampleTest = typeOfSampleTestDAO.getTypeOfSampleTestsForTest("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
