 /**
 * @(#) District.java 01-00 Aug 15, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 15, 2016
 */
package us.mn.state.health.lims.district.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;

/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class District extends BaseObject {
    
    private static final long serialVersionUID = 1L;
    
    private String id;

    private ValueHolderInterface city;
    
    private ValueHolderInterface districtEntry;
    
    private String description;
    
    private String lastUpdated;
    
    private String selectedCityId;
    
    /**
     * Get selectedCityId 
     *
     * @return selectedCityId
     */
    public String getSelectedCityId() {
        return selectedCityId;
    }

    /**
     * Set selectedCityId 
     *
     * @param selectedCityId the selectedCityId to set
     */
    public void setSelectedCityId(String selectedCityId) {
        this.selectedCityId = selectedCityId;
    }

    /**
     * Get lastUpdated 
     *
     * @return lastUpdated
     */
    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Set lastUpdated 
     *
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public District() {
        super();
        this.city = new ValueHolder();
        this.districtEntry = new ValueHolder();
    }
    
    /**
     * Get description 
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description 
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Get city 
     *
     * @return city
     */
    public Dictionary getCity() {
        return (Dictionary) this.city.getValue();
    }

    /*
     * Set city 
     *
     * @param city the city to set
     */
    public void setCity(Dictionary city) {
        this.city.setValue(city);
    }

    /*
     * Get district 
     *
     * @return district
     */
    public Dictionary getDistrictEntry() {
        return (Dictionary) this.districtEntry.getValue();
    }

    /*
     * Set district 
     *
     * @param district the district to set
     */
    public void setDistrictEntry(Dictionary district) {
        this.districtEntry.setValue(district);
    }

    /*
     * Get id 
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /*
     * Set id 
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

}
