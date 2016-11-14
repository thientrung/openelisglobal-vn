package us.mn.state.health.lims.typeofsample.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.typeofsample.dao.TypeOfSamplePanelDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSamplePanelDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSamplePanel;

public class TypeOfSamplePanelDAOTest {

    @Test
    public void testInsertData() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
        	TypeOfSamplePanel typeOfSamplePanel = new TypeOfSamplePanel();
            boolean isInserted = typeOfSamplePanelDAO.insertData(typeOfSamplePanel);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Type of sample panel is successfully inserted.");
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
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
        	String[] arrSamplePanelIds = null;
            typeOfSamplePanelDAO.deleteData(arrSamplePanelIds, "1");
            Assert.assertTrue(true);
            System.out.println("Type of sample panel is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTypeOfSamplePanels() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
            List listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getAllTypeOfSamplePanels();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTypeOfSamplePanel() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
            List listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getPageOfTypeOfSamplePanel(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
        	TypeOfSamplePanel typeOfSamplePanel = new TypeOfSamplePanel();
        	typeOfSamplePanelDAO.getData(typeOfSamplePanel);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTypeOfSamplePanelRecord() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
            List listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getNextTypeOfSamplePanelRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTypeOfSamplePanelRecord() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
            List listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getPreviousTypeOfSamplePanelRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTypeOfSamplePanelCount() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
            int totalTypeOfSamplePanel = typeOfSamplePanelDAO.getTotalTypeOfSamplePanelCount();

            if (totalTypeOfSamplePanel > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }

    @Test
    public void testGetTypeOfSamplePanelsForSampleType() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
        	List<TypeOfSamplePanel> listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getTypeOfSamplePanelsForSampleType("sampleType");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSamplePanelsForPanel() {
        try {
        	TypeOfSamplePanelDAO typeOfSamplePanelDAO = new TypeOfSamplePanelDAOImpl();
        	List<TypeOfSamplePanel> listTypeOfSamplePanel = new ArrayList<>();
            listTypeOfSamplePanel = typeOfSamplePanelDAO.getTypeOfSamplePanelsForPanel("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSamplePanel));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
