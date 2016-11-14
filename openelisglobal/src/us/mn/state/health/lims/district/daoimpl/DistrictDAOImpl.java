 /**
 * @(#) DistrictDAOImpl.java 01-00 Aug 15, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 15, 2016
 */
package us.mn.state.health.lims.district.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.valueholder.District;
import us.mn.state.health.lims.hibernate.HibernateUtil;


/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class DistrictDAOImpl extends BaseDAOImpl implements DistrictDAO {

    public boolean insertData(District district) throws LIMSRuntimeException {
        try {
            // Throw Exception if record already exists
            if (duplicateDistrictExists(district)) {
                throw new LIMSDuplicateRecordException("Duplicate record or abrevation exists for "
                                + district.getDistrictEntry());
            }

            String id = (String) HibernateUtil.getSession().save(district);
            district.setId(id);

            // Inserts will be logged in history table
            AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
            String sysUserId = district.getSysUserId();
            String tableName = "DISTRICT";
            auditDAO.saveNewHistory(district, sysUserId, tableName);

            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch (Exception e) {
            LogEvent.logError(
                    "DistrictDAOImpl", "insertData()", e.toString());
            throw new LIMSRuntimeException(
                    "Error in District insertData()", e);
        }

        return true;
    }
    
    public void updateData(District district) throws LIMSRuntimeException {

        // bugzilla 1386 throw Exception if record already exists
        try {
            if (duplicateDistrictExists(district)) {
                throw new LIMSDuplicateRecordException("Duplicate record exists for " + district.getDistrictEntry().getDictEntry());
            }
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "duplicateDistrictExists()", e.toString());
            throw new LIMSRuntimeException("Error in District duplicateDistrictExists()", e);
        }

        District oldData = (District) readDistrict(district.getId());
        District newData = district;

        // add to audit trail
        try {
            AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
            String sysUserId = district.getSysUserId();
            String event = IActionConstants.AUDIT_TRAIL_UPDATE;
            String tableName = "DISTRICT";
            auditDAO.saveHistory(newData, oldData, sysUserId, event, tableName);
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "AuditTrail", e.toString());
            throw new LIMSRuntimeException("Error in District AuditTrail", e);
        }

        try {
            HibernateUtil.getSession().merge(district);
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            HibernateUtil.getSession().evict(district);
            HibernateUtil.getSession().refresh(district);
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "updateData()", e.toString());
            throw new LIMSRuntimeException("Error in District updateData()", e);
        }
    }
    
    public void getData(District district) throws LIMSRuntimeException {
        try {
            District d = (District) HibernateUtil.getSession().get(District.class, district.getId());
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            if (d != null) {
                PropertyUtils.copyProperties(district, d);
            } else {
                district.setId(null);
            }
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "getData()", e.toString());
            throw new LIMSRuntimeException("Error in District getData()", e);
        }
    }

    public List getAllDistricts() throws LIMSRuntimeException {
        List list = new Vector();
        try {
            String sql = "from District d order by d.city.dictEntry asc";
            Query query = HibernateUtil.getSession().createQuery(sql);

            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "getAllDistricts()", e.toString());
            throw new LIMSRuntimeException("Error in District getAllDistricts()", e);
        }

        return list;
    }
    
    public List<String> getAllDistrictsByCity(String cityId) throws LIMSRuntimeException {
        List<String> list = new Vector();
        try {
            String sql = "select d.districtEntry.id from District d where d.city.id = :param order by d.districtEntry.dictEntry";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("param", Integer.valueOf(cityId));

            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            
        } catch (Exception e) {
            LogEvent.logError("DistrictDAOImpl", "getAllDistrictsByCity()", e.toString());
            throw new LIMSRuntimeException("Error in District getAllDistrictsByCity()", e);
        }

        return list;
    }
    
    public List getPageOfDistricts(int startingRecNo) throws LIMSRuntimeException {
        List list = new Vector();

        try {
            int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);

            // bugzilla 1399
            String sql = "from District d order by d.city.dictEntry, d.id";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setFirstResult(startingRecNo - 1);
            query.setMaxResults(endingRecNo - 1);

            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "getPageOfDistricts()", e.toString());
            throw new LIMSRuntimeException("Error in District getPageOfDistricts()", e);
        }

        return list;
    }
    
    public List getNextDistrictRecord(String id) throws LIMSRuntimeException {

        return getNextRecord(id, "Dictionary", Dictionary.class);

    }

    public List getPreviousDistrictRecord(String id) throws LIMSRuntimeException {

        return getPreviousRecord(id, "Dictionary", Dictionary.class);
    }
    
    private boolean duplicateDistrictExists(District district)throws LIMSRuntimeException {
        try {

            List list = new ArrayList();

            // duplicates
            // description within category is unique AND local abbreviation
            // within category is unique
            String sql = null;
            sql = "from District d where d.city.id = :cityId and d.districtEntry.id = :districtName and d.id != :id";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("districtName", district.getDistrictEntry().getId());
            query.setParameter("cityId", Integer.parseInt(district.getCity().getId()));
            // initialize with 0 (for new records where no id has been generated
            // yet
            String id = district.getId();
            if (id != null) {
                query.setParameter("id", Integer.parseInt(district.getId()));
            } else {
                query.setParameter("id", 0);
            }
            

            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();

            if (list.size() > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "duplicateDictionaryExists()", e.toString());
            throw new LIMSRuntimeException("Error in district duplicateDictionaryExists()", e);
        }
    }
    
    public District readDistrict(String idString) {
        District district = null;
        try {
            district = (District) HibernateUtil.getSession().get(District.class, idString);
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch (Exception e) {
            // bugzilla 2154
            LogEvent.logError("DistrictDAOImpl", "readDistrict()", e.toString());
            throw new LIMSRuntimeException("Error in District readDistrict()", e);
        }

        return district;
    }
    
    public Integer getTotalDistrictCount() throws LIMSRuntimeException {
        return getTotalCount("District", District.class);
    }
    
    public List<Dictionary> getDistrictsEntyByName (String name) throws LIMSRuntimeException {
        try {
            String sql = "";
             sql = "from Dictionary d where upper(d.dictEntry) like upper(:param) and d.dictionaryCategory = :districtCatId";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("param", name);
            query.setParameter("districtCatId", IActionConstants.DISTRICT_CATEGORY_ID);

            List<Dictionary> list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            return list;

        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("DistrictDAOImpl","getDistrictsEntyByName()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in  District getDistrictsEntyByName(String filter)", e);
        }
    }
    
    public List<Dictionary> getDistrictByName (String name) throws LIMSRuntimeException {
        try {
            String sql = "";
            sql = "from Dictionary d where upper(d.dictEntry) = upper(:param) and d.dictionaryCategory = :districtCatId";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("param", name);
            query.setParameter("districtCatId", IActionConstants.DISTRICT_CATEGORY_ID);
            
            List<Dictionary> list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            return list;
            
        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("DistrictDAOImpl","getDistrictByName()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in District getDistrictByName(String filter)", e);
        }
    }
    
    public List<Dictionary> getAllDistinctCitiesInDistrictTable() throws LIMSRuntimeException {
        try {
            String sql = "";
            sql = "from Dictionary d where d.id in(select distinct ds.city.id from District ds) order by d.dictEntry";
            Query query = HibernateUtil.getSession().createQuery(sql);
            
            List<Dictionary> list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            
            return list;
            
        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("DistrictDAOImpl","getAllDistinctCitiesInDistrictTable()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in District getAllDistinctCitiesInDistrictTable()", e);
        }
    }
    
    public List<Dictionary> getAllDistinctDistrictsInDistrictTable() throws LIMSRuntimeException {
        try {
            String sql = "";
            sql = "from Dictionary d where cast(d.id as character varying) in(select distinct ds.districtEntry.id from District ds) order by d.dictEntry";
            Query query = HibernateUtil.getSession().createQuery(sql);
            
            List<Dictionary> list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            
            return list;
        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("DistrictDAOImpl","getAllDistinctDistrictsInDistrictTable()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in District getAllDistinctDistrictsInDistrictTable()", e);
        }
    }
    /*
     * Function get District by DistrictEntryId
     * Input: String districtEntryId
     * Output: List<District>
     */
    private List<District> getDistrictByDistrictEntryId (String districtEntryId) throws LIMSRuntimeException {
        List<District> list = new Vector<District>();
        try {
            String sql = "from District d where d.districtEntry = :districtEntryId";
            Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("districtEntryId", districtEntryId);
            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            
            return list;
        } catch (Exception e) {
            LogEvent.logError("DistrictDAOImpl","getDistrictByDistrictEntryId()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in District getDistrictByDistrictEntryId()", e);
        }
    }
    
    public List<District> getPagesOfSearchedDistricts(Integer startingRecNo, String searchString) throws LIMSRuntimeException {
        List<District> listDistrict = new Vector<District>();
        try {
            DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
            List<Dictionary> listDictionary = dictionaryDAO.getPagesOfSearchedDictionarys(startingRecNo, searchString, false, true);
            for (Dictionary dict : listDictionary) {
                List<District> list = new Vector<District>();
                list = getDistrictByDistrictEntryId(dict.getId());
                for (District dist : list) {
                    listDistrict.add(dist);
                }
            }
            return listDistrict;
        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("DistrictDAOImpl","getAllDistinctDistrictsInDistrictTable()",e.toString());
            throw new LIMSRuntimeException(
                    "Error in District getAllDistinctDistrictsInDistrictTable()", e);
        }
    }
    
    
}
