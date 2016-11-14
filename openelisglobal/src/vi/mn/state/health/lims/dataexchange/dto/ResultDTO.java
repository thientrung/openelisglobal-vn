/**
 * @(#) ResultDTO.java 01-00 May 14, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 14, 2016
 */
package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

/**
 * @author thonghh
 * 
 */
public class ResultDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5056228948173817181L;

    private String value;

    public ResultDTO() {
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
