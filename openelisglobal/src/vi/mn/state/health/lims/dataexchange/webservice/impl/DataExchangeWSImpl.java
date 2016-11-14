package vi.mn.state.health.lims.dataexchange.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.gender.valueholder.Gender;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorStructureDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.ResultDTO;
import vi.mn.state.health.lims.dataexchange.dto.SampleDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;
import vi.mn.state.health.lims.dataexchange.services.DataExchangeService;
import vi.mn.state.health.lims.dataexchange.servicesimpl.DataExchangeServiceImpl;
import vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS;

/**
 * @author thonghh, markaae.fr
 * 
 */
public class DataExchangeWSImpl implements DataExchangeWS {

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getTotalSpecimensByUser(int)
     */
    @Override
    public int getTotalSpecimensByUser(int userId) {
        int result = 0;
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getTotalSpecimensByUser(userId);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getTotalSpecimensByUserAndStatus(int, int)
     */
    @Override
    public int getTotalSpecimensByUserAndStatus(int userId, int status) {
        int result = 0;
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getTotalSpecimensByUserAndStatus(userId, status);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getAllRequiredFields()
     */
    @Override
    public List<EtorStructureDTO> getAllRequiredFields() {
        List<EtorStructureDTO> result = new ArrayList<EtorStructureDTO>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getAllRequiredFields();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getGenders()
     */
    @Override
    public List<Gender> getGenders() {
        List<Gender> result = new ArrayList<Gender>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getGenders();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getCities()
     */
    @Override
    public List<Dictionary> getCities() {
        List<Dictionary> result = new ArrayList<Dictionary>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getCities();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getDistricts()
     */
    @Override
    public List<Dictionary> getDistricts() {
        List<Dictionary> result = new ArrayList<Dictionary>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getDistricts();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getSampleTypes()
     */
    @Override
    public List<TypeOfSample> getSampleTypes() {
        List<TypeOfSample> result = new ArrayList<TypeOfSample>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getSampleTypes();
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getByteReport(java.lang.String,
     * java.lang.String, java.lang.String java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public byte[] getByteReport(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId, String lowerDateRange, String upperDateRange,
            String organizationName) {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        byte[] result = dataExchangeService.getByteReport(report, reportType, requestFormat, accessDirect,
                highAccessDirect, testId, lowerDateRange, upperDateRange, organizationName);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.webservice.DataExchangeWS#getTests()
     */
    @Override
    public List<Test> getTests() {
        List<Test> result = new ArrayList<Test>();
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getTests();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addorder(OrderDataDTO orderData) {
        String result = StringUtils.EMPTY;
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        /*
         * List<SampleDTO> lstSamples = new ArrayList<SampleDTO>(); SampleDTO sample = createFakeData(orderData);
         * lstSamples.add(sample); orderData.setSamples(lstSamples);
         */
        result = dataExchangeService.addOrder(orderData);
        return result;
    }

    @Override
    public EtorUserDTO getIdForUsername(String username) {
        EtorUserDTO result = null;
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        result = dataExchangeService.getIdForUsername(username);
        return result;
    }

    public SampleDTO createFakeData(OrderDataDTO orderData) {

        SampleDTO sample = new SampleDTO();

        sample.setSampleType("7");
        List<TestDTO> lstTest = new ArrayList<TestDTO>();

        String[] strTests = orderData.getTestId().split(",");
        String[] strResults = orderData.getResult().split(",");
        for (int i = 0; i < strTests.length; i++) {
            TestDTO test = new TestDTO();
            test.setTestId(strTests[i]);

            List<ResultDTO> lstRes = new ArrayList<ResultDTO>();
            ResultDTO res = new ResultDTO();
            res.setValue(strResults[i]);
            lstRes.add(res);
            test.setResults(lstRes);

            lstTest.add(test);
        }

        sample.setTests(lstTest);
        return sample;
    }

    @Override
    public byte[] getByteReportForEtor(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId) {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        byte[] result = dataExchangeService.getByteReportForEtor(report, reportType, requestFormat, accessDirect,
                highAccessDirect, testId);
        return result;
    }
    
    @Override
    public String validateAIMLogin(AimUserDTO aimUserDTO) {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        String result = dataExchangeService.validateAIMLogin(aimUserDTO);
        
        return result;
    }
    
    @Override
    public String addAIMTestResult(TestResultParameterDTO testResultParameterDTO) {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        String result = dataExchangeService.addAIMTestResult(testResultParameterDTO);
        
        return result;
    }
    
    @Override
    public String addTestAndResultOpenLabConnect(TestResultParameterDTO testResultParameterDTO) {
        DataExchangeService dataExchangeService = new DataExchangeServiceImpl();
        String result = "failed";
        try{
        	result = dataExchangeService.insertTestAndResultDAO(testResultParameterDTO.getAccessNumber(),
            		testResultParameterDTO.getTestId(),
            		testResultParameterDTO.getMainResult(),
            		testResultParameterDTO.getSubResult(),
            		testResultParameterDTO.getBeginDate(),
            		testResultParameterDTO.getInstrumentId());
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        		
        
        return result;
    }
    
}
