/**
 * @(#) DataExchangeService.java 01-00 May 10, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 10, 2016
 */
package vi.mn.state.health.lims.dataexchange.services;

import java.util.List;

import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.gender.valueholder.Gender;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorStructureDTO;
import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
public interface DataExchangeService {

    /**
     * Get total Specimens By User
     * 
     * @param userId
     * @return total records of userId
     */
    int getTotalSpecimensByUser(int userId);

    /**
     * Get total specimens by user and status
     * 
     * @param userId
     * @param status
     * @return
     */
    int getTotalSpecimensByUserAndStatus(int userId, int status);

    /**
     * Get list structure Fields
     * 
     * @return
     */
    public List<EtorStructureDTO> getAllRequiredFields();

    /**
     * Get All Gender Types 4. List of All Gender Types
     * 
     * @return List<Gender>
     */
    List<Gender> getGenders();

    /**
     * Get Dictionary By CategoryId for example: 1. list of all cities with id = 239 2. List of All Districts with id =
     * 240
     * 
     * @return List<Dictionary>
     */
    List<Dictionary> getCities();

    /**
     * Get Districts
     * 
     * @return List<Dictionary>
     */
    List<Dictionary> getDistricts();

    /**
     * Get All Sample Types 5. List of All Sample Types
     * 
     * @return List<TypeOfSample>
     */
    List<TypeOfSample> getSampleTypes();

    /**
     * Get generated report in byte
     * 
     * @param report
     * @param reportType
     * @param requestFormat
     * @param accessDirect
     * @param highAccessDirect
     * @param testId
     * @param lowerDateRange
     * @param upperDateRange
     * @param organizationName
     * @return byte[]
     */
    byte[] getByteReport(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId, String lowerDateRange, String upperDateRange, String organizationName);

    /**
     * Process business logic: 1. convert OrderDataDTO to OrderVO 2. convert OrderVO to Order Entity, and persit it into
     * database.
     * 
     * @param orderDataDTO
     * @return
     */
    String addOrder(OrderDataDTO orderDataDTO);

    /**
     * Get All Tests
     * 
     * @return List<Test>
     */
    List<Test> getTests();

    /**
     * 
     * Get user id by username
     * 
     * @param username
     * @return
     */
    EtorUserDTO getIdForUsername(String username);

    
    /**
     * 
     * @param report
     * @param reportType
     * @param requestFormat
     * @param accessDirect
     * @param highAccessDirect
     * @param testId
     * @return
     */
    byte[] getByteReportForEtor(String report, String reportType, String requestFormat, String accessDirect,
            String highAccessDirect, String testId);
    
    /**
     * Validate AIM user login
     * 
     * @param aimUserDTO
     * @return String
     */
    String validateAIMLogin(AimUserDTO aimUserDTO);
    
    /**
     * Insert test result from AIM
     * 
     * @param testResultParameterDTO
     * @return String "success" or "fail"
     */
    String addAIMTestResult(TestResultParameterDTO testResultParameterDTO);
    String insertTestAndResultDAO(String accessionNumber, String testId, String mainResult, String subResult, String beginDate, int instrumentId);
    
}
