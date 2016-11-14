/**
 * @(#) EtorStructureDTO.java 01-00 Apr 29, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Apr 29, 2016
 */
package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;

/**
 * @author thonghh
 *
 */
public class EtorStructureDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String columnName;

    private String dataType;

    private Boolean isIdentity;

    private Boolean isNullable;

    private Integer maxLength;

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName
     *            the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the isIdentity
     */
    public Boolean getIsIdentity() {
        return isIdentity;
    }

    /**
     * @param isIdentity
     *            the isIdentity to set
     */
    public void setIsIdentity(Boolean isIdentity) {
        this.isIdentity = isIdentity;
    }

    /**
     * @return the isNullable
     */
    public Boolean getIsNullable() {
        return isNullable;
    }

    /**
     * @param isNullable
     *            the isNullable to set
     */
    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * @return the maxLength
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength
     *            the maxLength to set
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

}
