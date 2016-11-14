/**
 * @(#) DataExchangeTransformer.java 01-00 May 10, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 10, 2016
 */
package vi.mn.state.health.lims.dataexchange.transformer;

import vi.mn.state.health.lims.dataexchange.dto.EtorUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.OrderDataDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultDTO;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
public interface DataExchangeTransformer {
    
    /**
     * Convert from OrderDataDTO object to OrderVO object
     * 
     * @param orderDataDto
     * @return the OrderVO object
     */
    OrderVO convertToOrderVO(OrderDataDTO orderDataDTO);

    /**
     * Convert from obj[] to EtorUserDTO object
     * 
     * @param obj
     * @return the EtorUserDTO object
     */
    EtorUserDTO convertToEtorUserDTO(Object[] obj);
    
    /**
     * Convert from obj[] to TestResultDTO object
     * 
     * @param obj
     * @return the TestResultDTO object
     */
    TestResultDTO convertToTestResultDTO(Object[] obj);
    
}
