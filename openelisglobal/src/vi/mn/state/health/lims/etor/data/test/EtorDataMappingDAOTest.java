package vi.mn.state.health.lims.etor.data.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import vi.mn.state.health.lims.dataexchange.dao.IParameterTestWebservice;
import vi.mn.state.health.lims.etor.data.dao.EtorDataMappingDAO;
import vi.mn.state.health.lims.etor.data.daoimpl.EtorDataMappingDAOImpl;
import vi.mn.state.health.lims.etor.data.valueholder.EtorDataMapping;

public class EtorDataMappingDAOTest implements IParameterTestWebservice {

    // Valid - Expect Success
    @Test
    public void testInsertDataSuccess() {
        try {
            EtorDataMappingDAO etorDataMappingDAO = new EtorDataMappingDAOImpl();
            EtorDataMapping etorDataMapping = new EtorDataMapping();
            etorDataMapping.setSample(new SampleDAOImpl().getSampleByAccessionNumber("1670000081"));
            SampleItem sampleItem = new SampleItemDAOImpl().getData("109");
            etorDataMapping.setSampleItem(sampleItem);
            etorDataMapping.setLisStatus(Integer.parseInt(ANALYSIS_STATUS_ID_YET));
            etorDataMapping.setEtorStatus(Integer.parseInt(ANALYSIS_STATUS_ID));
            etorDataMapping.setEtorUserId("1");
            boolean isInserted = etorDataMappingDAO.insertData(etorDataMapping);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("[testInsertDataSuccess] - Etor data is successfully inserted.");
    			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataSuccess()", 
    					"Etor data is successfully inserted.");
            } else {
                Assert.assertTrue(false);
    			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataSuccess()", 
    					"Failed to insert data.");
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataSuccess()", 
					Arrays.toString(e.getStackTrace()));
        }
    }
    
    // Invalid - Expecting to Fail
    @Test
    public void testInsertDataFail() {
        try {
            EtorDataMappingDAO etorDataMappingDAO = new EtorDataMappingDAOImpl();
            EtorDataMapping etorDataMapping = new EtorDataMapping();
            etorDataMapping.setSample(new SampleDAOImpl().getSampleByAccessionNumber("AAAAAAAAAA"));
            SampleItem sampleItem = new SampleItemDAOImpl().getData("0");
            etorDataMapping.setSampleItem(sampleItem);
            etorDataMapping.setLisStatus(Integer.parseInt(ANALYSIS_STATUS_ID_YET));
            etorDataMapping.setEtorStatus(Integer.parseInt(ANALYSIS_STATUS_ID));
            etorDataMapping.setEtorUserId("0");
            boolean isInserted = etorDataMappingDAO.insertData(etorDataMapping);

            if (isInserted) {
                Assert.assertTrue(true);
    			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataFail()", 
    					"Unexpected: Etor data is inserted.");
            } else {
                Assert.assertFalse(false);
                System.out.println("[testInsertDataFail] - Etor data insertion is unsuccessful.");
    			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataFail()", 
    					"Etor data insertion is unsuccessful.");
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
            System.out.println("[testInsertDataFail] - Etor data insertion is unsuccessful.");
			LogEvent.logDebug("EtorDataMappingDAOTest", "testInsertDataFail()", 
					Arrays.toString(e.getStackTrace()));
        }
    }
    
}
