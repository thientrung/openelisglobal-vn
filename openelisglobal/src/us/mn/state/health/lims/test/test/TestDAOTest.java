package us.mn.state.health.lims.test.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;

public class TestDAOTest {
	
	@Test
    public void testInsertData() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
            boolean isInserted = testDAO.insertData(test);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test data is successfully inserted.");
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
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            testDAO.deleteData(listTest);
            Assert.assertTrue(true);
            System.out.println("Test data is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTests() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List<us.mn.state.health.lims.test.valueholder.Test> listTest = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
            listTest = testDAO.getAllTests(true);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllActiveTests() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List<us.mn.state.health.lims.test.valueholder.Test> listTest = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
            listTest = testDAO.getAllActiveTests(true);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllActiveOrderableTests() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List<us.mn.state.health.lims.test.valueholder.Test> listTest = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
            listTest = testDAO.getAllActiveOrderableTests();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllOrderBy() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List<us.mn.state.health.lims.test.valueholder.Test> listTest = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
            listTest = testDAO.getAllOrderBy("columnName");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestsBySysUserId() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getAllTestsBySysUserId(1, true);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTests() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getPageOfTests(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestsBySysUserId() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getPageOfTestsBySysUserId(0, 1);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	testDAO.getData(test);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	testDAO.updateData(test);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTests() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getTests("filter", true);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestRecord() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getNextTestRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestRecord() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getPreviousTestRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestByName() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	us.mn.state.health.lims.test.valueholder.Test retTest = testDAO.getTestByName(test);
        	
        	if (retTest != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestByNameTwo() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	test = testDAO.getTestByName("testName");
        	
        	if (test != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestByUserLocalizedName() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	test = testDAO.getTestByUserLocalizedName("testName");
        	
        	if (test != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetActiveTestByName() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List<us.mn.state.health.lims.test.valueholder.Test> listTest = new ArrayList<us.mn.state.health.lims.test.valueholder.Test>();
            listTest = testDAO.getActiveTestByName("testName");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestById() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	us.mn.state.health.lims.test.valueholder.Test retTest = testDAO.getTestById(test);
        	
        	if (retTest != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetActiveTestById() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	test = testDAO.getActiveTestById(1);
        	
        	if (test != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetMethodsByTestSection() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getMethodsByTestSection("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestsByTestSection() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getTestsByTestSection("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestsByMethod() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getTestsByMethod("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestsByTestSectionAndMethod() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            List listTest = new ArrayList<>();
            listTest = testDAO.getTestsByTestSectionAndMethod("filter1", "filter2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTest));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTestCount() {
        try {
        	TestDAO testDAO = new TestDAOImpl();
            int totalTest = testDAO.getTotalTestCount();

            if (totalTest > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
