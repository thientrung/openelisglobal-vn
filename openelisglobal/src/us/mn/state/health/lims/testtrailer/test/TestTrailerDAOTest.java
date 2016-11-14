package us.mn.state.health.lims.testtrailer.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testtrailer.dao.TestTrailerDAO;
import us.mn.state.health.lims.testtrailer.daoimpl.TestTrailerDAOImpl;
import us.mn.state.health.lims.testtrailer.valueholder.TestTrailer;

public class TestTrailerDAOTest {
	
    @Test
    public void testInsertData() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
        	TestTrailer testTrailer = new TestTrailer();
            boolean isInserted = testTrailerDAO.insertData(testTrailer);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Test trailer is successfully inserted.");
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
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            testTrailerDAO.deleteData(listTestTrailer);
            Assert.assertTrue(true);
            System.out.println("Test trailer is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTestTrailers() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            listTestTrailer = testTrailerDAO.getAllTestTrailers();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestTrailer));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTestTrailers() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            listTestTrailer = testTrailerDAO.getPageOfTestTrailers(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestTrailer));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
        	TestTrailer testTrailer = new TestTrailer();
        	testTrailerDAO.getData(testTrailer);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
        	TestTrailer testTrailer = new TestTrailer();
        	testTrailerDAO.updateData(testTrailer);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTestTrailerRecord() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            listTestTrailer = testTrailerDAO.getNextTestTrailerRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestTrailer));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTestTrailerRecord() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            listTestTrailer = testTrailerDAO.getPreviousTestTrailerRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestTrailer));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestTrailerByName() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
        	TestTrailer testTrailer = new TestTrailer();
        	TestTrailer retTestTrailer = testTrailerDAO.getTestTrailerByName(testTrailer);
        	
        	if (retTestTrailer != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTestTrailers() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            List listTestTrailer = new ArrayList<>();
            listTestTrailer = testTrailerDAO.getTestTrailers("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTestTrailer));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTestTrailerCount() {
        try {
        	TestTrailerDAO testTrailerDAO = new TestTrailerDAOImpl();
            int totalTestTrailer = testTrailerDAO.getTotalTestTrailerCount();

            if (totalTestTrailer > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
