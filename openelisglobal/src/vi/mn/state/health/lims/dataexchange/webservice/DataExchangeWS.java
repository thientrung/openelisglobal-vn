package vi.mn.state.health.lims.dataexchange.webservice;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
 * @author thonghh, markaae.fr
 * 
 */
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public interface DataExchangeWS {

    /**
     * Get total specimens by user
     * 
     * @param userId
     * @return int
     */
    @Path("/getTotalSpecimensByUser/{userId}")
    @GET
    int getTotalSpecimensByUser(@PathParam("userId")
    int userId);

    /**
     * Get total specimens by user and status
     * 
     * @param userId
     * @param status
     * @return int
     */
    @Path("/getTotalSpecimensByUserAndStatus/{userId}/{status}")
    @GET
    int getTotalSpecimensByUserAndStatus(@PathParam("userId")
    int userId, @PathParam("status")
    int status);

    /**
     * Get list structure Fields
     * 
     * @return List<EtorStructureDTO>
     */
    @Path("/getAllRequiredFields")
    @GET
    List<EtorStructureDTO> getAllRequiredFields();

    /**
     * Get genders
     * 
     * @return List<Gender>
     */
    @Path("/getGenders")
    @GET
    List<Gender> getGenders();

    /**
     * Get cities
     * 
     * @return List<Dictionary>
     */
    @Path("/getCities")
    @GET
    List<Dictionary> getCities();

    /**
     * Get districts
     * 
     * @return List<Dictionary>
     */
    @Path("/getDistricts")
    @GET
    List<Dictionary> getDistricts();

    /**
     * Get sample types
     * 
     * @return List<TypeOfSample>
     */
    @Path("/getSampleTypes")
    @GET
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
    @Path("/getByteReport/{report}/{reportType}/{requestFormat}/{accessDirect}/{highAccessDirect}/{testId}/{lowerDateRange}/{upperDateRange}/{organizationName}")
    @GET
    byte[] getByteReport(@PathParam("report")
    String report, @PathParam("reportType")
    String reportType, @PathParam("requestFormat")
    String requestFormat, @PathParam("accessDirect")
    String accessDirect, @PathParam("highAccessDirect")
    String highAccessDirect, @PathParam("testId")
    String testId, @PathParam("lowerDateRange")
    String lowerDateRange, @PathParam("upperDateRange")
    String upperDateRange, @PathParam("organizationName")
    String organizationName);

    /**
     * Get all tests
     * 
     * @return List<Test>
     */
    @Path("/getTests")
    @GET
    List<Test> getTests();

    /**
     * Add new order
     * 
     * @param orderData
     * @return String
     */
    @Path("/addorder")
    @POST
    String addorder(OrderDataDTO orderData);

    /**
     * Get user id by username
     * 
     * @param username
     * @return
     */
    @Path("/getIdForUsername/{username}")
    @GET
    EtorUserDTO getIdForUsername(@PathParam("username")
    String username);
    
    
    @Path("/getByteReportForEtor/{report}/{reportType}/{requestFormat}/{accessDirect}/{highAccessDirect}/{testId}")
    @GET
    byte[] getByteReportForEtor(@PathParam("report")
    String report, @PathParam("reportType")
    String reportType, @PathParam("requestFormat")
    String requestFormat, @PathParam("accessDirect")
    String accessDirect, @PathParam("highAccessDirect")
    String highAccessDirect, @PathParam("testId")
    String testId);
    
    /**
     * Validate AIM user login
     * 
     * @param aimUserDTO
     * @return String
     */
    @Path("/validateAIMLogin")
    @POST
    String validateAIMLogin(AimUserDTO aimUserDTO);
    
    /**
     * Insert test result from AIM
     * 
     * @param testResultParameterDTO
     * @return String "success" or "fail"
     */
    @Path("/addAIMTestResult")
    @POST
    String addAIMTestResult(TestResultParameterDTO testResultParameterDTO);
    
    @Path("/addTestAndResultOpenLabConnect")
    @POST
    String addTestAndResultOpenLabConnect(TestResultParameterDTO testResultParameterDTO);
    
}
