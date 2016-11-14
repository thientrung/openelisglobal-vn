package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

public class TestResultParameterDTO implements Serializable {

    /**
     * field explanation
     */
    private static final long serialVersionUID = 1L;
    private String accessNumber;
    private String testId;
    private String mainResult;
    private String subResult;
    private String beginDate;
    private int instrumentId;

    public TestResultParameterDTO() {
    	
    }

    /**
     * Get accessNumber
     * @return accessNumber
     */
    public String getAccessNumber() {
        return accessNumber;
    }
    /**
     * Set accessNumber
     * @param accessNumber
     *            the accessNumber to set
     */
    public void setAccessNumber(String accessNumber) {
        this.accessNumber = accessNumber;
    }

    /**
     * Get testId
     * @return testId
     */
    public String getTestId() {
        return testId;
    }
    /**
     * Set testId
     * @param testId
     *            the testId to set
     */
    public void setTestId(String testId) {
        this.testId = testId;
    }
    
    /**
     * Get mainResult
     * @return mainResult
     */
    public String getMainResult() {
        return mainResult;
    }
    /**
     * Set mainResult
     * @param mainResult
     *            the mainResult to set
     */
    public void setMainResult(String mainResult) {
        this.mainResult = mainResult;
    }
    
    /**
     * Get subResult
     * @return subResult
     */
    public String getSubResult() {
        return subResult;
    }
    /**
     * Set subResult
     * @param subResult
     *            the subResult to set
     */
    public void setSubResult(String subResult) {
        this.subResult = subResult;
    }
    
    /**
     * Get beginDate
     * @return beginDate
     */
    public String getBeginDate() {
        return beginDate;
    }
    /**
     * Set beginDate
     * @param beginDate
     *            the beginDate to set
     */
    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}

}
