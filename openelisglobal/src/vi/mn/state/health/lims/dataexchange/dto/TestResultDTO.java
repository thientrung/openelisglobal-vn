/**
 * 
 */
package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

/**
 * @author markaae.fr
 */
public class TestResultDTO implements Serializable {
    
    /**
     * field explanation
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String resultId;
    private String sampleAccessionNumber;
    private String sampleItemId;
    private String analysisId;
    private String analysisTestId;
    private String testResultId;
    private String testResultType;
    private String testResultValue;
    private String testResultIsQuantifiable;
    private String sysUserId;
    
    public TestResultDTO() {
        
    }

    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getResultId() {
        return this.resultId;
    }
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getSampleAccessionNumber() {
        return this.sampleAccessionNumber;
    }
    public void setSampleAccessionNumber(String sampleAccessionNumber) {
        this.sampleAccessionNumber = sampleAccessionNumber;
    }

    public String getSampleItemId() {
        return sampleItemId;
    }
    public void setSampleItemId(String sampleItemId) {
        this.sampleItemId = sampleItemId;
    }

    public String getAnalysisId() {
        return analysisId;
    }
    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisTestId() {
        return analysisTestId;
    }
    public void setAnalysisTestId(String analysisTestId) {
        this.analysisTestId = analysisTestId;
    }
    
    public String getTestResultId() {
        return testResultId;
    }
    public void setTestResultId(String testResultId) {
        this.testResultId = testResultId;
    }

    public String getTestResultType() {
        return testResultType;
    }
    public void setTestResultType(String testResultType) {
        this.testResultType = testResultType;
    }

    public String getTestResultValue() {
        return testResultValue;
    }
    public void setTestResultValue(String testResultValue) {
        this.testResultValue = testResultValue;
    }

    public String getIsQuantifiable(){
        return testResultIsQuantifiable;
    }
    public void setIsQuantifiable( String testResultIsQuantifiable ){
        this.testResultIsQuantifiable = testResultIsQuantifiable;
    }
    
    public String getSysUserId(){
        return sysUserId;
    }
    public void setSysUserId( String sysUserId ){
        this.sysUserId = sysUserId;
    }
    
}
