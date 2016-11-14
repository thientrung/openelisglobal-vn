/**
 * 
 */
package vi.mn.state.health.lims.dataexchange.valueholder;

import java.io.Serializable;

/**
 * @author dungtdo.sl
 * 
 */
public class TestResultVO implements Serializable {
    private String testId;

    private String result;

    public TestResultVO(String id, String rs) {
        this.testId = id;
        this.result = rs;
    }

    public TestResultVO() {
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
