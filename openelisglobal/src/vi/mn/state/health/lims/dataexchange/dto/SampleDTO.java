/**
 * @(#) SampleDTO.java 01-00 May 14, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 14, 2016
 */
package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author thonghh
 * 
 */
public class SampleDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -3414576537219288741L;

    /**
     * sample type
     */
    private String sampleType;

    /**
     * result object
     */
    private List<TestDTO> tests;

    public SampleDTO() {
    }

    /**
     * @return the sampleType
     */
    public String getSampleType() {
        return sampleType;
    }

    /**
     * @param sampleType
     *            the sampleType to set
     */
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /**
     * @return the tests
     */
    public List<TestDTO> getTests() {
        return tests;
    }

    /**
     * @param tests
     *            the tests to set
     */
    public void setTests(List<TestDTO> tests) {
        this.tests = tests;
    }

}
