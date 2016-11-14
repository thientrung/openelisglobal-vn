/**
 * @(#) DataExchange.java 01-00 May 3, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 3, 2016
 */
package vi.mn.state.health.lims.dataexchange.dao;

import java.util.List;

import javax.jws.WebService;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
@WebService
public interface DataExchangeDAO {

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
    public List<Object[]> getAllRequiredFields();

    /**
     * Get All Gender Types 4. List of All Gender Types
     * 
     * @return List<Object[]>
     */
    List<Object[]> getGenders();

    /**
     * Get Dictionary By CategoryId for example: 1. list of all cities with id = 239 2. List of All Districts with id =
     * 240
     * 
     * @return List<Object[]>
     */
    List<Object[]> getCities();

    /**
     * Get districts
     * 
     * @return List<Object[]>
     */
    List<Object[]> getDistricts();

    /**
     * Get All Sample Types 5. List of All Sample Types
     * 
     * @return List<Object[]>
     */
    List<Object[]> getSampleTypes();

    /**
     * Get All Tests
     * 
     * @return List<Object[]>
     */
    List<Object[]> getTests();

    /**
     * Add new order
     * 
     * @param Type
     *            OrderVO class
     * @return String (return a string "success" or "false")
     * @throws LIMSRuntimeException
     */
    public String addNewOrder(OrderVO order) throws LIMSRuntimeException;

    /**
     * Get user id and org id by username
     * 
     * @param username
     * @return
     */
    Object[] getIdForUsername(String username);
    
    /**
     * Validate AIM user login
     * 
     * @param aimUserDTO
     * @return String
     * @throws LIMSRuntimeException
     */
    String validateAIMLogin(AimUserDTO aimUserDTO) throws LIMSRuntimeException;
    
    /**
     * Insert test result from AIM
     * 
     * @param testResultParameterDTO
     * @return String "success" or "fail"
     */
    String addAIMTestResult(TestResultParameterDTO testResultParameterDTO);
    String insertTestAndResultDAO(String accessionNumber, String testId, String mainResult, String subResult, String beginDate, int instrumentId);

}
