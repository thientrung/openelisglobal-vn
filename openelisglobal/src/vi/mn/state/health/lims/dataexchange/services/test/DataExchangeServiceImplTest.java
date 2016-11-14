/**
 * Unit Test for the openELIS_vi - web services (service layer)
 * 
 * @(#) DataExchangeServiceImplTest.java 01-00 May 10, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 10, 2016
 */
package vi.mn.state.health.lims.dataexchange.services.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.gender.valueholder.Gender;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorStructureDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;
import vi.mn.state.health.lims.dataexchange.services.DataExchangeService;
import vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl;

/**
 * Detail description of processing of this class.
 * @author thonghh
 */
public class DataExchangeServiceImplTest {

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getTotalSpecimensByUser(int)}.
	 */
	@Test
	public void testGetTotalSpecimensByUser() {
		DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		int result = dataExchangeService.getTotalSpecimensByUser(22);
		Assert.assertTrue(result != 0);
		LogEvent.logDebug("DataExchangeServiceImplTest", "testGetTotalSpecimensByUser()", 
				"Count: " + result);
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getTotalSpecimensByUserAndStatus(int, int)}.
	 */
	@Test
	public void testGetTotalSpecimensByUserAndStatus() {
		DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		int result = dataExchangeService.getTotalSpecimensByUserAndStatus(
				22, 6);
		Assert.assertTrue(result != 0);
		LogEvent.logDebug("DataExchangeServiceImplTest", "testGetTotalSpecimensByUserAndStatus()", 
				"Count: " + result);
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getAllRequiredFields()}.
	 */
	@Test
	public void testGetAllRequiredFields() {
		DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		List<EtorStructureDTO> result = dataExchangeService
				.getAllRequiredFields();
		Assert.assertTrue(!org.springframework.util.CollectionUtils
				.isEmpty(result));
		LogEvent.logDebug("DataExchangeServiceImplTest", "testGetAllRequiredFields()", 
				"Example return value: " + result.get(0).getColumnName());
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getGenders()}.
	 */
	@Test
	public void testGetGenders() {
		 DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		 List<Gender> result = dataExchangeService.getGenders();
		 Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(result));
		 LogEvent.logDebug("DataExchangeServiceImplTest", "testGetGenders()", 
					"Example return value: " + result.get(0).getDescription());
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getCities()}.
	 */
	@Test
	public void testGetCities() {
		 DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		 List<Dictionary> result = dataExchangeService.getCities();
		 Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(result));
		 LogEvent.logDebug("DataExchangeServiceImplTest", "testGetCities()", 
					"Example return value: " + result.get(0).getDictEntry());
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getDistricts()}.
	 */
	@Test
	public void testGetDistricts() {
		 DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		 List<Dictionary> result = dataExchangeService.getDistricts();
		 Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(result));
		 LogEvent.logDebug("DataExchangeServiceImplTest", "testGetDistricts()", 
					"Example return value: " + result.get(0).getDictEntry());
	}

	/**
	 * Test method for
	 * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getSampleTypes()}.
	 */
	@Test
	public void testGetSampleTypes() {
		 DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
		 List<TypeOfSample> result = dataExchangeService.getSampleTypes();
		 Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(result));
		 LogEvent.logDebug("DataExchangeServiceImplTest", "testGetSampleTypes()", 
					"Example return value: " + result.get(0).getDescription());
	}

    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#addOrder(vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO)}.
     */
    @Test
    public void testAddOrder() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        OrderDataDTO orderDataDTO = new OrderDataDTO();
        orderDataDTO.setCollectionDate("2016-05-06T23:30:00-04:00");
        orderDataDTO.setCollector("Steve Frost");
        orderDataDTO.setSampleType("7");
        orderDataDTO.setDiagnosis("DiagnosisValue");
        orderDataDTO.setDoctor("Dr. Frost");
        orderDataDTO.setExternalId("166");
        orderDataDTO.setIllnessDate("2016-05-02T00:00:00-04:00");
        orderDataDTO.setReceivedDate("2016-05-05T00:00:00-04:00");
        orderDataDTO.setResultId("288");
        orderDataDTO.setResult("20/21");
        orderDataDTO.setAge("26");
        orderDataDTO.setBirthDate("1990-06-12T00:00:00-04:00");
        orderDataDTO.setCity("70000");
        orderDataDTO.setDistrict("3");
        orderDataDTO.setGender("145");
        orderDataDTO.setOrgId("1");
        orderDataDTO.setPatientId("166");
        orderDataDTO.setPatientName("FullName");
        orderDataDTO.setStreetAddress("121 Lý Chính Thắng");
        orderDataDTO.setUsername("admin");
        orderDataDTO.setUserId("01");
        orderDataDTO.setWard("06");
        orderDataDTO.setTestId("207");
        orderDataDTO.setPatientGender("M");
        orderDataDTO.setPatientWard("A");
        orderDataDTO.setTestSelectionId("1");
        
        try {
            String result = dataExchangeService.addOrder(orderDataDTO);
            //Assert.assertTrue(StringUtils.isNotEmpty(result));
            Assert.assertTrue(result.equalsIgnoreCase("success"));
            System.out.println("[testAddOrder] - " + result);
   		 	LogEvent.logDebug("DataExchangeServiceImplTest", "testAddOrder()", 
					"Add order successful.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
			LogEvent.logDebug("DataExchangeServiceImplTest", "testAddOrder()", 
					Arrays.toString(e.getStackTrace()));
        }
	}
    
    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getByteReport()}.
     */
    @Test
    public void testGetByteReport() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        byte[] result = null;
        result = dataExchangeService.getByteReport("patientResultTestAntigenIgm", "patient", "pdf", "1670000003",
                                            "1670000007", "342", "0", "0", "0");
        Assert.assertTrue(result != null);
	 	LogEvent.logDebug("DataExchangeServiceImplTest", "testGetByteReport()", 
				"GetByteReport - successful.");
    }
    
    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getByteReportForEtor()}.
     */
    @Test
    public void testGetByteReportForEtor() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        byte[] result = null;
        result = dataExchangeService.getByteReportForEtor("patientResultTestAntigenIgm", "patient", "pdf", "1670000003",
                                            "1670000007", "342");
        Assert.assertTrue(result != null);
	 	LogEvent.logDebug("DataExchangeServiceImplTest", "testGetByteReport()", 
				"GetByteReportForEtor - successful.");
    }
    
    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getTests()}.
     */
    @Test
    public void testGetTests() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        List<us.mn.state.health.lims.test.valueholder.Test> result = dataExchangeService.getTests();
        Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(result));
	    LogEvent.logDebug("DataExchangeServiceImplTest", "testGetTests()", 
					"Example return value: " + result.get(0).getDescription());
    }
    
    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#getIdForUsername(String)}.
     */
    @Test
    public void testGetIdForUsername() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        EtorUserDTO result = dataExchangeService.getIdForUsername("admin");
        Assert.assertTrue(result != null);
	    LogEvent.logDebug("DataExchangeServiceImplTest", "testGetIdForUsername()", 
					"Example return value: " + result.getUserId());
    }
	
	/**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#validateAIMLogin(AimUserDTO)}.
     */
    @Test
    public void testValidateAIMLogin() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        AimUserDTO aimUserDTO = new AimUserDTO();
        aimUserDTO.setUsername("admin");
        aimUserDTO.setPassword("admin");
        String result = dataExchangeService
                .validateAIMLogin(aimUserDTO);
        String message = "false";
        try {
            JSONObject jsonObject = new JSONObject(result);
            message = jsonObject.getString("isValid");
        } catch (Exception e) {
            Assert.assertFalse(false);
			LogEvent.logDebug("DataExchangeServiceImplTest", "testValidateAIMLogin()", 
					Arrays.toString(e.getStackTrace()));
        }
        Assert.assertTrue(!message.equalsIgnoreCase("false"));
	    LogEvent.logDebug("DataExchangeServiceImplTest", "testValidateAIMLogin()", 
					"ValidateAIMLogin - successful.");
    }
    
    /**
     * Test method for
     * {@link vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl#addAIMTestResult(TestResultParameterDTO)}.
     */
    @Test
    public void testAddAIMTestResult() {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        TestResultParameterDTO trpDTO = new TestResultParameterDTO();
        trpDTO.setAccessNumber("1670000014");
        trpDTO.setTestId("342");
        trpDTO.setMainResult("2097");
        trpDTO.setSubResult("0.065/0.934");
        String result = dataExchangeService
                .addAIMTestResult(trpDTO);
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(result);
            message = jsonObject.getString("message");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
			LogEvent.logDebug("DataExchangeServiceImplTest", "testAddAIMTestResult()", 
					Arrays.toString(e.getStackTrace()));
        }
        Assert.assertTrue(message.equalsIgnoreCase("success"));
	    LogEvent.logDebug("DataExchangeServiceImplTest", "testAddAIMTestResult()", 
					"Add AIM test result - successful.");
    }
    
}
