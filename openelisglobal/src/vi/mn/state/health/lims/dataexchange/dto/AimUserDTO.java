package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

public class AimUserDTO implements Serializable {

    /**
     * field explanation
     */
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    public AimUserDTO() {
    	
    }

    /**
     * Get username
     * @return username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Set username
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password
     * @return password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set password
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
