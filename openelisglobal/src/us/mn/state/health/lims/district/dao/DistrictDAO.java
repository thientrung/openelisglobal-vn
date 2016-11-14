 /**
 * @(#) DistrictDAO.java 01-00 Aug 15, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 15, 2016
 */
package us.mn.state.health.lims.district.dao;

import java.util.List;

import us.mn.state.health.lims.common.dao.BaseDAO;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.district.valueholder.District;

/**
 * Detail description of processing of this interface.
 *
 * @author trungtt.kd
 */
public interface DistrictDAO extends BaseDAO {
    
    public boolean insertData(District district)
            throws LIMSRuntimeException;

    public List getAllDistricts() throws LIMSRuntimeException;

    public List getPageOfDistricts(int startingRecNo)
            throws LIMSRuntimeException;

    public void getData(District district) throws LIMSRuntimeException;

    public void updateData(District district) throws LIMSRuntimeException;
    
    public List getNextDistrictRecord(String id) throws LIMSRuntimeException;
    
    public List getPreviousDistrictRecord(String id)
            throws LIMSRuntimeException;
    
    public Integer getTotalDistrictCount() throws LIMSRuntimeException;
    
    public List<Dictionary> getDistrictsEntyByName (String name) throws LIMSRuntimeException;
    
    public List<Dictionary> getDistrictByName (String name) throws LIMSRuntimeException;
    
    public List<String> getAllDistrictsByCity(String cityId) throws LIMSRuntimeException;
    
    public List<Dictionary> getAllDistinctCitiesInDistrictTable() throws LIMSRuntimeException;
    
    public List<Dictionary> getAllDistinctDistrictsInDistrictTable() throws LIMSRuntimeException;
    
    public List<District> getPagesOfSearchedDistricts(Integer startingRecNo, String searchString) throws LIMSRuntimeException;

}
