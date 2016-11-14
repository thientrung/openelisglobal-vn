/**
 * @(#) DataExchangeTransformerImpl.java 01-00 May 10, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 10, 2016
 */
package vi.mn.state.health.lims.dataexchange.transformerimpl;

import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultDTO;
import vi.mn.state.health.lims.dataexchange.transformer.DataExchangeTransformer;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
public class DataExchangeTransformerImpl implements DataExchangeTransformer {

    private static final String TEST_ID_1 = "207";

    private static final String TEST_ID_2 = "208";

    public static final String TEST_RESULT_ID_1 = "288";

    public static final String TEST_RESULT_ID_2 = "292";

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderVO convertToOrderVO(OrderDataDTO orderDataDTO) {
        return new OrderVO();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EtorUserDTO convertToEtorUserDTO(Object[] obj) {
        EtorUserDTO etorUserDTO = new EtorUserDTO();
        if (obj != null) {
            etorUserDTO.setUserId(String.valueOf(obj[0]));
            etorUserDTO.setOrgId(String.valueOf(obj[1]));
        }
        return etorUserDTO;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TestResultDTO convertToTestResultDTO(Object[] obj) {
        TestResultDTO testResultDTO = new TestResultDTO();
        if (obj != null) {
            testResultDTO.setSampleAccessionNumber(String.valueOf(obj[0]));
            testResultDTO.setSampleItemId(String.valueOf(obj[1]));
            testResultDTO.setAnalysisTestId(String.valueOf(obj[2]));
            testResultDTO.setTestResultId(String.valueOf(obj[3]));
            testResultDTO.setTestResultType(String.valueOf(obj[4]));
            testResultDTO.setTestResultValue(String.valueOf(obj[5]));
            testResultDTO.setIsQuantifiable(String.valueOf(obj[6]));
            testResultDTO.setSysUserId(String.valueOf(obj[7]));
            testResultDTO.setAnalysisId(String.valueOf(obj[8])); 
        }
        return testResultDTO;
    }
    
}
