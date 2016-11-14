/**
 * @(#) DataExchangeServiceImpl.java 01-00 May 10, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 10, 2016
 */
package vi.mn.state.health.lims.dataexchange.servicesimpl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.provider.validation.YearSiteNumAccessionValidator;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.gender.dao.GenderDAO;
import us.mn.state.health.lims.gender.daoimpl.GenderDAOImpl;
import us.mn.state.health.lims.gender.valueholder.Gender;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.reports.action.implementation.IReportCreator;
import us.mn.state.health.lims.reports.action.implementation.ReportImplementationFactory;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO;
import vi.mn.state.health.lims.dataexchange.dao.IParameterTestWebservice;
import vi.mn.state.health.lims.dataexchange.daoimpl.DataExchangeDAOImpl;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorStructureDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.ResultDTO;
import vi.mn.state.health.lims.dataexchange.dto.SampleDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;
import vi.mn.state.health.lims.dataexchange.services.DataExchangeService;
import vi.mn.state.health.lims.dataexchange.transformer.DataExchangeTransformer;
import vi.mn.state.health.lims.dataexchange.transformerimpl.DataExchangeTransformerImpl;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;
import vi.mn.state.health.lims.dataexchange.valueholder.TestResultVO;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
public class DataExchangeServiceImpl implements DataExchangeService, IParameterTestWebservice, IActionConstants {

    private static String reportPath = null;

    private static String imageUrl = null;

    private static String rightImageUrl = null;

    private static String TAG = "DataExchangeServiceImpl";

    private int LENGTH = 19;

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getTotalSpecimensByUser(int)
     */
    @Override
    public int getTotalSpecimensByUser(int userId) {
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        int count = dataExchangeDAO.getTotalSpecimensByUser(userId);
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getTotalSpecimensByUserAndStatus(int, int)
     */
    @Override
    public int getTotalSpecimensByUserAndStatus(int userId, int status) {
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        int count = dataExchangeDAO.getTotalSpecimensByUserAndStatus(userId, status);
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getAllRequiredFields()
     */
    @Override
    public List<EtorStructureDTO> getAllRequiredFields() {
        List<EtorStructureDTO> result = new ArrayList<EtorStructureDTO>();
        List<Object[]> structureFields = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        structureFields = dataExchangeDAO.getAllRequiredFields();
        if (!org.springframework.util.CollectionUtils.isEmpty(structureFields)) {
            EtorStructureDTO dto = null;
            for (Object[] obj : structureFields) {
                dto = new EtorStructureDTO();
                // private String columnName;
                dto.setColumnName(String.valueOf(obj[1]));
                // private String dataType;
                dto.setDataType(String.valueOf(obj[2]));
                // private Boolean isIdentity;
                dto.setIsIdentity("YES".equals(String.valueOf(obj[3])));
                // private Boolean isNullable;
                dto.setIsNullable("YES".equals(String.valueOf(obj[4])));
                // private Integer maxLength;
                if (obj[5] != null) {
                    dto.setMaxLength(Integer.valueOf(String.valueOf(obj[5])));
                }

                result.add(dto);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getGenders()
     */
    @Override
    public List<Gender> getGenders() {
        List<Gender> genders = new ArrayList<Gender>();
        List<Object[]> objArray = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        objArray = dataExchangeDAO.getGenders();
        Gender gender = null;
        for (Object[] obj : objArray) {
            gender = new Gender();
            gender.setId(String.valueOf(obj[0]));
            gender.setGenderType(String.valueOf(obj[1]));
            genders.add(gender);
        }
        return genders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getCities()
     */
    @Override
    public List<Dictionary> getCities() {
        List<Dictionary> cities = new ArrayList<Dictionary>();
        List<Object[]> objArray = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        objArray = dataExchangeDAO.getCities();
        Dictionary city = null;
        for (Object[] obj : objArray) {
            city = new Dictionary();
            city.setId(String.valueOf(obj[0]));
            city.setDictEntry(String.valueOf(obj[1]));
            cities.add(city);
        }

        return cities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getDistricts()
     */
    @Override
    public List<Dictionary> getDistricts() {
        List<Dictionary> districts = new ArrayList<Dictionary>();
        List<Object[]> objArray = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        objArray = dataExchangeDAO.getDistricts();
        Dictionary district = null;
        for (Object[] obj : objArray) {
            district = new Dictionary();
            district.setId(String.valueOf(obj[0]));
            district.setDictEntry(String.valueOf(obj[1]));
            districts.add(district);
        }

        return districts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getSampleTypes()
     */
    @Override
    public List<TypeOfSample> getSampleTypes() {
        List<TypeOfSample> sampleTypes = new ArrayList<TypeOfSample>();
        List<Object[]> objArray = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        objArray = dataExchangeDAO.getSampleTypes();
        TypeOfSample typeOfSample = null;
        for (Object[] obj : objArray) {
            typeOfSample = new TypeOfSample();
            typeOfSample.setId(String.valueOf(obj[0]));
            typeOfSample.setDescription(String.valueOf(obj[1]));
            sampleTypes.add(typeOfSample);
        }

        return sampleTypes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getByteReport(java.lang.String,
     * java.lang.String, java.lang.String java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public byte[] getByteReport(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId, String lowerDateRange, String upperDateRange,
            String organizationName) {

        byte[] bFile = new byte[10];
        HashMap<String, String> parameterMap = new HashMap<String, String>();

        parameterMap.put("lowerDateRange", lowerDateRange);
        parameterMap.put("upperDateRange", upperDateRange);
        parameterMap.put("accessionDirect", accessDirect);
        parameterMap.put("highAccessionDirect", highAccessDirect);
        parameterMap.put("testId", testId);
        parameterMap.put("organizationName", organizationName);
        parameterMap.put("SUBREPORT_DIR", getReportPath());
        parameterMap.put("reportName", report);
        parameterMap.put("reportType", reportType);
        parameterMap.put("reportRequest", requestFormat);

        IReportCreator reportCreator = ReportImplementationFactory.getReportCreator(report);
        if (reportCreator != null) {
            reportCreator.setImageUrl(getImageUrl());
            reportCreator.setRightImageUrl(getRightImageUrl());
            reportCreator.initializeReport(parameterMap);
            reportCreator.setReportPath(getReportPath());
            HashMap<String, String> paramMap = (HashMap<String, String>) reportCreator.getReportParameters();
            paramMap.put("SUBREPORT_DIR", getReportPath());

            try {
                if (requestFormat.equals("pdf")) {
                    bFile = reportCreator.runReport();
                } else {
                    bFile = reportCreator.runReportForExcel();
                }
            } catch (Exception e) {
                LogEvent.logErrorStack(TAG, "getByteReport", e);
                e.printStackTrace();
            }
        }

        return bFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#getTests()
     */
    @Override
    public List<Test> getTests() {
        List<Test> tests = new ArrayList<Test>();
        List<Object[]> objArray = new ArrayList<Object[]>();
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        objArray = dataExchangeDAO.getTests();
        Test test = null;
        for (Object[] obj : objArray) {
            test = new Test();
            test.setId(String.valueOf(obj[0]));
            test.setDescription(String.valueOf(obj[1]));
            tests.add(test);
        }

        return tests;
    }

    // ***** Added methods used by getByteReport() *****//

    /**
     * Get directory path of the reports
     * 
     * @return String
     */
    public String getReportPath() {
        if (reportPath == null) {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = "";
            try {
                fullPath = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LogEvent.logErrorStack(TAG, "getReportPath", e);
                e.printStackTrace();
            }
            String webInfPath = fullPath.replace("/classes/", "");
            reportPath = webInfPath + "/reports/";
        }
        return reportPath;
    }

    /**
     * Get report image
     * 
     * @return String
     */
    public String getImageUrl() {
        if (imageUrl == null) {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = "";
            try {
                fullPath = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LogEvent.logErrorStack(TAG, "getImageUrl", e);
                e.printStackTrace();
            }
            String projectPath = fullPath.replace("/WEB-INF/classes/", "");
            imageUrl = projectPath + "/tree_images/Logo-Pasteur-Color.jpg";
        }
        return imageUrl;
    }

    /**
     * Get report top-right image
     * 
     * @return String
     */
    public String getRightImageUrl() {
        if (rightImageUrl == null) {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = "";
            try {
                fullPath = URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LogEvent.logErrorStack(TAG, "getRightImageUrl", e);
                e.printStackTrace();
            }
            String projectPath = fullPath.replace("/WEB-INF/classes/", "");
            rightImageUrl = projectPath + "/tree_images/right_header_img.png";
        }
        return rightImageUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addOrder(OrderDataDTO orderDataDTO) {
        String result = org.apache.commons.lang.StringUtils.EMPTY;
        /**
         * 1. orderDataDTO to OrderVO using transfer layer
         */
        DataExchangeTransformer dataExchangeTransformer = new DataExchangeTransformerImpl();

        // OrderVO orderVo = dataExchangeTransformer.convertToOrderVO(orderDataDTO);
        // TODO;
        OrderVO orderVo = null;
        /**
         * 2. add OrderVO into Database
         */
        // @before
        orderVo = AddDataOrderVO(orderDataDTO);
        // @after
        // orderVo2 = convertToOrderVO();

        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        result = dataExchangeDAO.addNewOrder(orderVo);

        return result;
    }

    /**
	 * 
	 */
    private OrderVO AddDataOrderVO(OrderDataDTO orderDataDTO) {
        // TODO Auto-generated method stub
        YearSiteNumAccessionValidator yearSiteNumAccessionValidator = new YearSiteNumAccessionValidator();
        OrderVO orderVO = new OrderVO();

        if (!StringUtil.isNullorNill(orderDataDTO.getEtor_id())) {
            orderVO.setEtorId(orderDataDTO.getEtor_id());
        } else {
            orderVO.setEtorId(orderDataDTO.getUserId());
        }
        orderVO.getSample().setAccessionNumber(yearSiteNumAccessionValidator.getNextAvailableAccessionNumber(null));

        // orderVO.getSample().setReceivedDate(new Date(new java.util.Date(orderDataDTO.getReceived_date()).getTime()));
        Timestamp receivedTimestamp = standardizeTimestamp(orderDataDTO.getReceivedDate());
        orderVO.getSample().setReceivedTimestamp(receivedTimestamp);
        Date sqlReceivedDate = DateUtil.convertTimestampToSqlDate(receivedTimestamp);
        orderVO.getSample().setReceivedDate(sqlReceivedDate);

        Timestamp collectionTimestamp = standardizeTimestamp(orderDataDTO.getCollectionDate());
        // Date sqlCollectionDate = DateUtil.convertTimestampToSqlDate(collectionTimestamp);
        orderVO.getSample().setCollectionDate(collectionTimestamp);
        // need

        orderVO.getSample().setEnteredDate(new Date(new java.util.Date().getTime()));
        orderVO.getSample().setSysUserId(SYSTEM_USER_ID);
        orderVO.getSample().setStatusId(SAMPLE_STATUS_ID);
        // illness_date
        orderVO.getSample().setOnsetOfDate(new Timestamp(new java.util.Date().getTime()));
        // end need

        GenderDAO gender = new GenderDAOImpl();
        Gender gen = gender.readGender(orderDataDTO.getGender());

        Person person = new Person();
        person.setSysUserId(SYSTEM_USER_ID);
        person.setFirstName(orderDataDTO.getDoctor());
        orderVO.getProvider().setSysUserId(SYSTEM_USER_ID);
        orderVO.getProvider().setPerson(person);
        orderVO.setDiagnosis(orderDataDTO.getDiagnosis());
        orderVO.setTypeOfSampleId(Integer.parseInt(orderDataDTO.getSampleType()));// mau
        person = new Person();
        person.setSysUserId(SYSTEM_USER_ID);
        person.setFirstName(orderDataDTO.getPatientName());
        person.setStreetAddress(orderDataDTO.getStreetAddress());
        orderVO.getPatient().setId(orderDataDTO.getPatientId());
        orderVO.getPatient().setSysUserId(SYSTEM_USER_ID);
        orderVO.getPatient().setPerson(person);

        Timestamp birthDateTimestamp = standardizeTimestamp(orderDataDTO.getBirthDate());
        orderVO.getPatient().setBirthDate(birthDateTimestamp);
        String birthDate = DateUtil.convertTimestampToStringDate(birthDateTimestamp);
        orderVO.getPatient().setBirthDateForDisplay(birthDate);
        orderVO.setAge(orderDataDTO.getAge());
        if (gen != null) {
            orderVO.getPatient().setGender(gen.getGenderType());
        }

        orderVO.getPatient().setExternalId(orderDataDTO.getExternalId());
        orderVO.setStreetAddress(orderDataDTO.getStreetAddress());
        orderVO.setWard(orderDataDTO.getWard());
        orderVO.setDistrict(orderDataDTO.getDistrict());
        orderVO.setCity(orderDataDTO.getCity());
        
        if (orderDataDTO.getSamples() != null) {
	        for (SampleDTO samp : orderDataDTO.getSamples()) {
	            TypeOfSample typeOfSample = new TypeOfSample();
	            TypeOfSampleDAO typeOfSampleDAO = new TypeOfSampleDAOImpl();
	            typeOfSample = typeOfSampleDAO.getTypeOfSampleById(String.valueOf(samp.getSampleType()));
	
	            for (TestDTO test : samp.getTests()) {
	                List<TypeOfSample> lst = new ArrayList<TypeOfSample>();
	                lst.add(typeOfSample);
	                orderVO.setTypeOfSample(lst);
	                // put test id when get from web service
	
	                orderVO.getMapTest().put(test.getTestId(), typeOfSample.getId());
	                // add result for test
	                for (ResultDTO result : test.getResults()) {
	                    // neu co nhieu type of sample thi dung map de phan biet
	                    // Hien tai co 1
	
	                    if (!StringUtil.isNullorNill(result.getValue())) {
	                        String testResultId = getTestResultId(test.getTestId(), result.getValue());
	                        TestResultVO testResultVO = new TestResultVO(test.getTestId(), testResultId);
	                        orderVO.getLiTestResultVO().add(testResultVO);
	                    }
	                }
	            }
	        }
        }    
	        
        // set testSelect
        TestSection t = new TestSection();
        TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
        orderVO.setTestSection(testSectionDAO.getTestSectionById(TEST_SELECTION_BY_ID));
        return orderVO;
    }

    @Override
    public EtorUserDTO getIdForUsername(String username) {
        EtorUserDTO result = null;
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        Object[] data = dataExchangeDAO.getIdForUsername(username);
        if (data != null) {
            DataExchangeTransformer dataExchangeTransformer = new DataExchangeTransformerImpl();
            result = dataExchangeTransformer.convertToEtorUserDTO(data);
        }
        return result;
    }

    private Timestamp standardizeTimestamp(String time) {
        Timestamp timeStamp = new Timestamp(0);

        if (time.contains("T") && time.length() > LENGTH) {
            time = time.replace("T", " ");
            time = time.substring(0, LENGTH);
            timeStamp = java.sql.Timestamp.valueOf(time);
        }

        return timeStamp;
    }

    private String getTestResultId(String testId, String result) {
        String testResId = "";
        String translatedResult = "";
        String[] inputTestResult = SystemConfiguration.getInstance().getInputTestResults().split(",");
        String[] translateTestResult = SystemConfiguration.getInstance().getTranslatedTestResults().split(",");

        for (int i = 0; i < inputTestResult.length; i++) {
            if (result.toUpperCase().contains(inputTestResult[i].toUpperCase())) {
                translatedResult = translateTestResult[i];
                break;
            }
        }

        Test test = new Test();
        test.setId(testId);
        TestResultDAO trDAO = new TestResultDAOImpl();
        List<TestResult> lstTestResult = new ArrayList<TestResult>();
        lstTestResult = trDAO.getAllActiveTestResultsPerTest(test);

        for (int i = 0; i < lstTestResult.size(); i++) {
            TestResult tr = lstTestResult.get(i);

            if (tr.getTestResultType().equals(TYPE_DICTIONARY)) {
                DictionaryDAO dictDAO = new DictionaryDAOImpl();
                Dictionary dict = dictDAO.getDictionaryById(tr.getValue());
                String dictEntry = dict.getDictEntry().toUpperCase();

                if (dictEntry.contains(translatedResult.toUpperCase())) {
                    testResId = tr.getId();
                    break;
                }
            }
        }

        return testResId;
    }

    @Override
    public byte[] getByteReportForEtor(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId) {

        byte[] bFile = new byte[10];
        HashMap<String, String> parameterMap = new HashMap<String, String>();

        // parameterMap.put("lowerDateRange", lowerDateRange);
        // parameterMap.put("upperDateRange", upperDateRange);
        parameterMap.put("accessionDirect", accessDirect);
        parameterMap.put("highAccessionDirect", highAccessDirect);
        parameterMap.put("testId", testId);
        // parameterMap.put("organizationName", organizationName);
        parameterMap.put("SUBREPORT_DIR", getReportPath());
        parameterMap.put("reportName", report);
        parameterMap.put("reportType", reportType);
        parameterMap.put("reportRequest", requestFormat);

        IReportCreator reportCreator = ReportImplementationFactory.getReportCreator(report);
        if (reportCreator != null) {
            reportCreator.setImageUrl(getImageUrl());
            reportCreator.setRightImageUrl(getRightImageUrl());
            reportCreator.initializeReport(parameterMap);
            reportCreator.setReportPath(getReportPath());
            HashMap<String, String> paramMap = (HashMap<String, String>) reportCreator.getReportParameters();
            paramMap.put("SUBREPORT_DIR", getReportPath());

            try {
                if (requestFormat.equals("pdf")) {
                    bFile = reportCreator.runReport();
                } else {
                    bFile = reportCreator.runReportForExcel();
                }
            } catch (Exception e) {
                LogEvent.logErrorStack(TAG, "getByteReport", e);
                e.printStackTrace();
            }
        }

        return bFile;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#validateAIMLogin()
     */
    @Override
    public String validateAIMLogin(AimUserDTO aimUserDTO) {
        DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
        String login = dataExchangeDAO.validateAIMLogin(aimUserDTO);
        
        return login;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.services.DataExchangeService#addAIMTestResult()
     */
    @Override
    public String addAIMTestResult(TestResultParameterDTO testResultParameterDTO) {
    	String result = "";
    	String errorMsg = "";
    	errorMsg = validateAIMParameters(testResultParameterDTO.getAccessNumber(), testResultParameterDTO.getTestId(), 
    										testResultParameterDTO.getMainResult());
        if (errorMsg.equalsIgnoreCase("")) {
	    	DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
	        result = dataExchangeDAO.addAIMTestResult(testResultParameterDTO);
	        
    	} else {
    		try {
    			JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", FWD_FAIL);
                jsonObject.put("error", errorMsg);
                result = jsonObject.toString();
                
			} catch (JSONException e) {
	            LogEvent.logError("DataExchangeServiceImpl", "addAIMTestResult()", Arrays.toString(e.getStackTrace()));
	        }
    	}
        
        return result;
    }
    
    /**
     * Validate the addAIMTestResult parameters
     * 
     * @param accessionNumber
     * @param testId
     * @param mainResult
     * @return String message
     */
    public String validateAIMParameters(String accessionNumber, String testId, String mainResult) {
    	StringBuilder errorMsg = new StringBuilder();
    	// Check if params is numeric, accession_number is not more than 10 digits, mainResult is not_null
    	if (accessionNumber == null) {
    		errorMsg.append(" Accession number is null.");
    	} 
    	if (!StringUtil.isInteger(accessionNumber)) {
    		errorMsg.append(" Accession number is not an integer.");
    	} 
    	if (accessionNumber.length()  != 10) {
    		errorMsg.append(" Accession number should be 10 digits.");
    	}
    	if (testId == null) {
    		errorMsg.append(" Test id is null.");
    	}
    	if (!StringUtil.isInteger(testId)) {
    		errorMsg.append(" Test id is not an integer.");
    	}
    	if (mainResult == null) {
    		errorMsg.append(" Main result is null.");
    	}
    	
    	return errorMsg.toString();
    }
    
    public String insertTestAndResultDAO(String accessionNumber, String testId, String mainResult, String subResult, String beginDate, int instrumentId) {
    	String result = "";
    	String errorMsg = "";
    	errorMsg = validateAIMParameters(accessionNumber, testId,mainResult);
        if (errorMsg.equalsIgnoreCase("")) {
	    	DataExchangeDAO dataExchangeDAO = new DataExchangeDAOImpl();
	        result = dataExchangeDAO.insertTestAndResultDAO(accessionNumber, testId, mainResult, subResult, beginDate, instrumentId);
	        
    	} else {
    		try {
    			JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", FWD_FAIL);
                jsonObject.put("error", errorMsg);
                result = jsonObject.toString();
                
			} catch (JSONException e) {
	            LogEvent.logError("DataExchangeServiceImpl", "insertTestAndResultDAO()", Arrays.toString(e.getStackTrace()));
	        }
    	}
        
        return result;
    }
    
}
