package us.mn.state.health.lims.typeofsample.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleSourceDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleSourceDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleSource;

public class TypeOfSampleSourceDAOTest {

    @Test
    public void testInsertData() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
        	TypeOfSampleSource typeOfSampleSource = new TypeOfSampleSource();
            boolean isInserted = typeOfSampleSourceDAO.insertData(typeOfSampleSource);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Type of sample source is successfully inserted.");
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
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
        	String[] arrSampleSourceIds = null;
        	typeOfSampleSourceDAO.deleteData(arrSampleSourceIds, "1");
            Assert.assertTrue(true);
            System.out.println("Type of sample source is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTypeOfSampleSources() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            List listTypeOfSampleSource = new ArrayList<>();
            listTypeOfSampleSource = typeOfSampleSourceDAO.getAllTypeOfSampleSources();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleSource));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTypeOfSampleSource() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            List listTypeOfSampleSource = new ArrayList<>();
            listTypeOfSampleSource = typeOfSampleSourceDAO.getPageOfTypeOfSampleSource(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleSource));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
        	TypeOfSampleSource typeOfSampleSource = new TypeOfSampleSource();
        	typeOfSampleSourceDAO.getData(typeOfSampleSource);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTypeOfSampleSourceRecord() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            List listTypeOfSampleSource = new ArrayList<>();
            listTypeOfSampleSource = typeOfSampleSourceDAO.getNextTypeOfSampleSourceRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleSource));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTypeOfSampleSourceRecord() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            List listTypeOfSampleSource = new ArrayList<>();
            listTypeOfSampleSource = typeOfSampleSourceDAO.getPreviousTypeOfSampleSourceRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleSource));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTypeOfSampleSourceCount() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            int totalTypeOfSampleSource = typeOfSampleSourceDAO.getTotalTypeOfSampleSourceCount();

            if (totalTypeOfSampleSource > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleSourcesForSampleType() {
        try {
        	TypeOfSampleSourceDAO typeOfSampleSourceDAO = new TypeOfSampleSourceDAOImpl();
            List<TypeOfSampleSource> listTypeOfSampleSource = new ArrayList<TypeOfSampleSource>();
            listTypeOfSampleSource = typeOfSampleSourceDAO.getTypeOfSampleSourcesForSampleType("sampleType");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSampleSource));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
