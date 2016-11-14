/**
 * @(#) TestDTO.java 01-00 May 14, 2016
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
public class TestDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5397441063453299869L;

    /**
     * test id
     */
    private String testId;

    /**
     * list of Result
     */
    private List<ResultDTO> results;

    /**
     * default constructor
     */
    public TestDTO() {
    }

    /**
     * @return the testId
     */
    public String getTestId() {
        return testId;
    }

    /**
     * @param testId
     *            the testId to set
     */
    public void setTestId(String testId) {
        this.testId = testId;
    }

    /**
     * @return the results
     */
    public List<ResultDTO> getResults() {
        return results;
    }

    /**
     * @param results
     *            the results to set
     */
    public void setResults(List<ResultDTO> results) {
        this.results = results;
    }

}
