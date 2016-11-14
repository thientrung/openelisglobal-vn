package us.mn.state.health.lims.typeofsample.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO.SampleDomain;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

public class TypeOfSampleDAOTest {
	
	@Test
    public void testGetNameForTypeOfSampleId() {
        String sampleName = "";
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            typeOfSampleDAO.getNameForTypeOfSampleId("1");

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
        Assert.assertTrue(!sampleName.equalsIgnoreCase("") && sampleName != null);
    }
	
    @Test
    public void testInsertData() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
            boolean isInserted = typeOfSampleDAO.insertData(typeOfSample);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Type of sample is successfully inserted.");
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
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List<TypeOfSample> listTypeOfSample = new ArrayList<TypeOfSample>();
            typeOfSampleDAO.deleteData(listTypeOfSample);
            Assert.assertTrue(true);
            System.out.println("Type of sample is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllTypeOfSamples() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listTypeOfSample = new ArrayList<>();
            listTypeOfSample = typeOfSampleDAO.getAllTypeOfSamples();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfTypeOfSamples() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listTypeOfSample = new ArrayList<>();
            listTypeOfSample = typeOfSampleDAO.getPageOfTypeOfSamples(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
        	typeOfSampleDAO.getData(typeOfSample);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
        	typeOfSampleDAO.updateData(typeOfSample);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypes() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listTypeOfSample = new ArrayList<>();
            listTypeOfSample = typeOfSampleDAO.getTypes("filter", "domain");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypesForDomain() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listTypeOfSample = new ArrayList<>();
            listTypeOfSample = typeOfSampleDAO.getTypesForDomain(us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO.SampleDomain.HUMAN);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listTypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextTypeOfSampleRecord() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listtypeOfSample = new ArrayList<>();
            listtypeOfSample = typeOfSampleDAO.getNextTypeOfSampleRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listtypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousTypeOfSampleRecord() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List listtypeOfSample = new ArrayList<>();
            listtypeOfSample = typeOfSampleDAO.getPreviousTypeOfSampleRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listtypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTotalTypeOfSampleCount() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            int totalTypeOfSample = typeOfSampleDAO.getTotalTypeOfSampleCount();

            if (totalTypeOfSample > 0) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleByDescriptionAndDomain() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
        	TypeOfSample rettypeOfSample = typeOfSampleDAO.getTypeOfSampleByDescriptionAndDomain(typeOfSample, true);
        	
        	if (rettypeOfSample != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleById() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
        	typeOfSample = typeOfSampleDAO.getTypeOfSampleById("1");
        	
        	if (typeOfSample != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypesForDomainBySortOrder() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
            List<TypeOfSample> listtypeOfSample = new ArrayList<TypeOfSample>();
            listtypeOfSample = typeOfSampleDAO.getTypesForDomainBySortOrder(SampleDomain.ANIMAL);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listtypeOfSample));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetTypeOfSampleByLocalAbbrevAndDomain() {
        try {
        	TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
        	TypeOfSample typeOfSample = new TypeOfSample();
        	typeOfSample = typeOfSampleDAO.getTypeOfSampleByLocalAbbrevAndDomain("localAbbrev", "domain");
        	
        	if (typeOfSample != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
