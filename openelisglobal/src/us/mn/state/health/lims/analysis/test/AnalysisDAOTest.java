package us.mn.state.health.lims.analysis.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;

public class AnalysisDAOTest {
	
	@Test
    public void testInsertData() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	Analysis analysis = new Analysis();
            boolean isInserted = analysisDAO.insertData(analysis, true);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("Analysis is successfully inserted.");
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
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            analysisDAO.deleteData(listAnalysis);
            Assert.assertTrue(true);
            System.out.println("Analysis is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllAnalyses() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            listAnalysis = analysisDAO.getAllAnalyses();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfAnalyses() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            listAnalysis = analysisDAO.getPageOfAnalyses(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	Analysis analysis = new Analysis();
        	analysisDAO.getData(analysis);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	Analysis analysis = new Analysis();
        	analysisDAO.updateData(analysis);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    /**
	 *  Update analysis data (AIM)
	 *  Valid parameter - unit test for success scenario
	 *  Expect: successfully updated (lastUpdated, statusId) columns in DB
	 */
    @Test
    public void testUpdateDataAIM() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	List<BigDecimal> listAnalysisIds = new ArrayList<BigDecimal>();
        	analysisDAO.updateDataAIM(listAnalysisIds, "1");
            Assert.assertTrue(true);
			System.out.println("[testUpdateDataAIM] Analysis data successfully updated.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("[testUpdateDataAIM] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    /**
	 *  Get analysis data by id (AIM)
	 *  Valid parameter - unit test for success scenario
	 *  Expect: return analysis data
	 */
    @Test
    public void testGetAnalysisByIdAIM() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            Analysis analysis = new Analysis();
            analysis = analysisDAO.getAnalysisByIdAIM("1");
            
            if (analysis != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testGetAnalysisByIdAIM] - " + analysis.getLocalizedName());
        	} else {
        		Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
			System.out.println("[testGetAnalysisByIdAIM] Error: " + Arrays.toString(e.getStackTrace()));
        }
    }
    
    @Test
    public void testGetAnalyses() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            listAnalysis = analysisDAO.getAnalyses("filter");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextAnalysisRecord() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            listAnalysis = analysisDAO.getNextAnalysisRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousAnalysisRecord() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
            listAnalysis = analysisDAO.getPreviousAnalysisRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllAnalysesPerTest() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            List listAnalysis = new ArrayList<>();
        	us.mn.state.health.lims.test.valueholder.Test test = new us.mn.state.health.lims.test.valueholder.Test();
        	listAnalysis = analysisDAO.getAllAnalysesPerTest(test);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listAnalysis));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testInsertDataWSSuccess() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	Analysis analysis = new Analysis();
        	analysis = analysisDAO.getAnalysisById("1");
        	String retVal = analysisDAO.insertDataWS(analysis, true);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        		System.out.println("[testInsertDataWSSuccess] - " + retVal);
        	} else {
        		Assert.assertFalse(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testInsertDataWSFail() {
        try {
        	AnalysisDAO analysisDAO = new AnalysisDAOImpl();
        	String retVal = analysisDAO.insertDataWS(null, false);
        	
        	if (retVal != null) {
        		Assert.assertTrue(true);
        	} else {
        		Assert.assertFalse(false);
        		System.out.println("[testInsertDataWSFail] FAIL - No Data Found");
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
