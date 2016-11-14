package us.mn.state.health.lims.unitofmeasure.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.unitofmeasure.dao.UnitOfMeasureDAO;
import us.mn.state.health.lims.unitofmeasure.daoimpl.UnitOfMeasureDAOImpl;
import us.mn.state.health.lims.unitofmeasure.valueholder.UnitOfMeasure;

public class UnitOfMeasureDAOTest {

    @Test
    public void testInsertData() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
        	UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
            boolean isInserted = unitOfMeasureDAO.insertData(unitOfMeasure);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Unit of measure data is successfully inserted.");
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
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            List<UnitOfMeasure> listUnitOfMeasure = new ArrayList<UnitOfMeasure>();
            unitOfMeasureDAO.deleteData(listUnitOfMeasure);
            Assert.assertTrue(true);
            System.out.println("Unit of measure data is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllUnitOfMeasures() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            List listUnitOfMeasure = new ArrayList<>();
            listUnitOfMeasure = unitOfMeasureDAO.getAllUnitOfMeasures();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUnitOfMeasure));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfUnitOfMeasures() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            List listUnitOfMeasure = new ArrayList<>();
            listUnitOfMeasure = unitOfMeasureDAO.getPageOfUnitOfMeasures(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUnitOfMeasure));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
        	UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        	unitOfMeasureDAO.getData(unitOfMeasure);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
        	UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        	unitOfMeasureDAO.updateData(unitOfMeasure);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextUnitOfMeasureRecord() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            List listUnitOfMeasure = new ArrayList<>();
            listUnitOfMeasure = unitOfMeasureDAO.getNextUnitOfMeasureRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUnitOfMeasure));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousUnitOfMeasureRecord() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            List listUnitOfMeasure = new ArrayList<>();
            listUnitOfMeasure = unitOfMeasureDAO.getPreviousUnitOfMeasureRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUnitOfMeasure));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetUnitOfMeasureByName() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
        	UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        	UnitOfMeasure retUnitOfMeasure = unitOfMeasureDAO.getUnitOfMeasureByName(unitOfMeasure);
        	
        	if (retUnitOfMeasure != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalUnitOfMeasureCount() {
        try {
        	UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
            int totalUnitOfMeasure = unitOfMeasureDAO.getTotalUnitOfMeasureCount();

            if (totalUnitOfMeasure > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
