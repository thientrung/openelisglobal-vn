package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

public class EtorUserDTO implements Serializable {

    /**
     * field explanation
     */

    private static final long serialVersionUID = 1L;

    private String userId;

    private String orgId;

    public EtorUserDTO() {
    }

    /**
     * Get userId
     * 
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set userId
     * 
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get orgId
     * 
     * @return orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Set orgId
     * 
     * @param orgId
     *            the orgId to set
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

}
