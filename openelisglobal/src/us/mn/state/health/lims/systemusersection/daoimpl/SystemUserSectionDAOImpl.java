/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.systemusersection.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSDuplicateRecordException;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.systemusersection.dao.SystemUserSectionDAO;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;

/**
 *  @author     Hung Nguyen (Hung.Nguyen@health.state.mn.us)
 */
public class SystemUserSectionDAOImpl extends BaseDAOImpl implements SystemUserSectionDAO {

	public void deleteData(List systemUserSections) throws LIMSRuntimeException {
		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			for (int i = 0; i < systemUserSections.size(); i++) {
				SystemUserSection data = (SystemUserSection)systemUserSections.get(i);
			
				SystemUserSection oldData = (SystemUserSection)readSystemUserSection(data.getId());
				SystemUserSection newData = new SystemUserSection();

				String sysUserId = data.getSysUserId();
				String event = IActionConstants.AUDIT_TRAIL_DELETE;
				String tableName = "SYSTEM_USER_SECTION";
				auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
			}
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","AuditTrail deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection AuditTrail deleteData()", e);
		}  
		
		try {					
			for (int i = 0; i < systemUserSections.size(); i++) {
				SystemUserSection data = (SystemUserSection) systemUserSections.get(i);
				//bugzilla 2206
				data = (SystemUserSection)readSystemUserSection(data.getId());
				HibernateUtil.getSession().delete(data);
				HibernateUtil.getSession().flush();
				HibernateUtil.getSession().clear();
			}						
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","deleteData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection deleteData()", e);
		}
	}

	public boolean insertData(SystemUserSection systemUserSection) throws LIMSRuntimeException {	
		
		try {
			if (duplicateSystemUserSectionExists(systemUserSection)) {
				throw new LIMSDuplicateRecordException("Duplicate record exists for " + systemUserSection.getSysUserId());
			}
			
			String id = (String)HibernateUtil.getSession().save(systemUserSection);
			systemUserSection.setId(id);
			
			//add to audit trail
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = systemUserSection.getSysUserId();
			String tableName = "SYSTEM_USER_SECTION";
			auditDAO.saveNewHistory(systemUserSection,sysUserId,tableName);		
			
			HibernateUtil.getSession().flush();	
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","insertData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection insertData()", e);
		} 
		
		return true;
	}

	public void updateData(SystemUserSection systemUserSection) throws LIMSRuntimeException {
	
		try {
			if (duplicateSystemUserSectionExists(systemUserSection)) {
				throw new LIMSDuplicateRecordException("Duplicate record exists for " + systemUserSection.getSystemUserId());
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection updateData()", e);
		} 

		SystemUserSection oldData = (SystemUserSection)readSystemUserSection(systemUserSection.getId());
		SystemUserSection newData = systemUserSection;

		//add to audit trail
		try {
			AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
			String sysUserId = systemUserSection.getSysUserId();
			String event = IActionConstants.AUDIT_TRAIL_UPDATE;
			String tableName = "SYSTEM_USER_SECTION";
			auditDAO.saveHistory(newData,oldData,sysUserId,event,tableName);
		}  catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","AuditTrail updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection AuditTrail updateData()", e);
		}  
		
		try {
			HibernateUtil.getSession().merge(systemUserSection);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			HibernateUtil.getSession().evict(systemUserSection);
			HibernateUtil.getSession().refresh(systemUserSection);
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","updateData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection updateData()", e);
		} 
	}

	public void getData(SystemUserSection systemUserSection) throws LIMSRuntimeException {	
		try {					
			SystemUserSection sysUserSection = (SystemUserSection)HibernateUtil.getSession().get(SystemUserSection.class, systemUserSection.getId());
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			if (sysUserSection != null) {
			  PropertyUtils.copyProperties(systemUserSection, sysUserSection);
			} else {
				systemUserSection.setId(null);
			}
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","getData()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection getData()", e);
		}
	}

	public List getAllSystemUserSections() throws LIMSRuntimeException {		
		List list = new Vector();
		try {
			String sql = "from SystemUserSection";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch(Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","getAllSystemModules()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection getAllSystemModules()", e);
		} 
		
		return list;
	}

	public List getAllSystemUserSectionsBySystemUserId(int systemUserId) throws LIMSRuntimeException {		
		List list = new Vector();
		try {
			String sql = "from SystemUserSection s where s.systemUser.id = :param";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", systemUserId);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch(Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","getAllSystemUserSectionsBySystemUserId()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection getAllSystemUserSectionsBySystemUserId()", e);
		} 
		
		return list;
	}
    
    public List getPageOfSystemUserSectionsForAdmin(int startingRecNo) throws LIMSRuntimeException {        
        List list = new Vector();
        try {           
            // calculate maxRow to be one more than the page size
            int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
            
            String sql = "from SystemUserSection s ";
            org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
            query.setFirstResult(startingRecNo-1);
            query.setMaxResults(endingRecNo-1); 
            
            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch (Exception e) {
            //bugzilla 2154
            LogEvent.logError("SystemUserSectionDAOImpl","getPageOfSystemUserSectionsForAdmin()",e.toString());
            throw new LIMSRuntimeException("Error in SystemUserSection getPageOfSystemUserSectionsForAdmin()", e);
        } 
        
        return list;
    }
    
    public List getPageOfSystemUserSectionsForSectionAdmin(int startingRecNo, int sectionAdminId) throws LIMSRuntimeException {        
        List<SystemUser> list = new Vector();
        List<SystemUserSection> listSysUserSec = new Vector();
        //get all SystemUserSection belong to many sections of the same Section Admin
        List<SystemUser> finalList = new Vector();
        try {           
            // calculate maxRow to be one more than the page size
            int endingRecNo = startingRecNo + (SystemConfiguration.getInstance().getDefaultPageSize() + 1);
            
            // Get Test Section ID first
            String sqlTestSection = "from SystemUserSection s where s.systemUser.id = :param and s.isAdmin='Y'";
            org.hibernate.Query queryGetTestSection = HibernateUtil.getSession().createQuery(sqlTestSection);
            queryGetTestSection.setParameter("param", sectionAdminId);
            
            listSysUserSec = queryGetTestSection.list();
            if (listSysUserSec.size() == 0) {
                return list;
            } else {
                // listSysUserSec can have more than one value, it means one user can be admin of many test sections
                // The following code is used to get all users belong to many test sections by test section id
                for (int i=0; i< listSysUserSec.size(); i++) {
                    SystemUserSection testSectionAdmin = listSysUserSec.get(i);
                    String sql = "from SystemUserSection s where s.testSection = :testSectionId";
                    org.hibernate.Query queryGetSysUser = HibernateUtil.getSession().createQuery(sql);
                    queryGetSysUser.setInteger("testSectionId", Integer.parseInt(testSectionAdmin.getTestSection().getId()));
                    queryGetSysUser.setFirstResult(startingRecNo-1);
                    queryGetSysUser.setMaxResults(endingRecNo-1); 
                    
                    list = queryGetSysUser.list();
                    //after get all users belong to a test section, we add them into a list that store all users of all test sections
                    finalList.addAll(list);
                }
            }
                     
            /*SystemUserSection testSectionAdmin = listSysUserSec.get(0);
            String sql = "from SystemUserSection s where s.testSection = :testSectionId";
            org.hibernate.Query queryGetSysUser = HibernateUtil.getSession().createQuery(sql);
            queryGetSysUser.setInteger("testSectionId", Integer.parseInt(testSectionAdmin.getTestSection().getId()));
            queryGetSysUser.setFirstResult(startingRecNo-1);
            queryGetSysUser.setMaxResults(endingRecNo-1); 
            
            list = queryGetSysUser.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();           
            return list;*/
            
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
            return finalList;
            
        } catch (Exception e) {
            LogEvent.logError("SystemUserSectionDAOImpl","getPageOfSystemUserSectionsForSectionAdmin()",e.toString());
            throw new LIMSRuntimeException("Error in SystemUserSection getPageOfSystemUserSectionsForSectionAdmin()", e);
        } 
        
    }

	public SystemUserSection readSystemUserSection(String idString) {		
		SystemUserSection sysUserSection = null;
		try {			
			sysUserSection = (SystemUserSection)HibernateUtil.getSession().get(SystemUserSection.class, idString);
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","readSystemUserSection()",e.toString());
			throw new LIMSRuntimeException("Error in SystemUserSection readSystemUserSection(idString)", e);
		}			
		
		return sysUserSection;
	}
	
	public List getNextSystemUserSectionRecord(String id) throws LIMSRuntimeException {

		return getNextRecord(id, "SystemUserSection", SystemUserSection.class);
	}

	public List getPreviousSystemUserSectionRecord(String id) throws LIMSRuntimeException {

		return getPreviousRecord(id, "SystemUserSection", SystemUserSection.class);
	}
	
	public Integer getTotalSystemUserSectionCount() throws LIMSRuntimeException {
		return getTotalCount("SystemUserSection", SystemUserSection.class);
	}
	
	public List getNextRecord(String id, String table, Class clazz)
			throws LIMSRuntimeException {
		int currentId = (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);

		List list = new Vector();
		int rrn = 0;
		try {
			String sql = "select sus.id from SystemUserSection sus order by sus.systemUser.id";
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getNext").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","getNextRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getNextRecord() for "
					+ table, e);
		}
		
		return list;
	}

	public List getPreviousRecord(String id, String table, Class clazz)
			throws LIMSRuntimeException {
		int currentId = (Integer.valueOf(id)).intValue();
		String tablePrefix = getTablePrefix(table);

		List list = new Vector();
		int rrn = 0;
		try {
			String sql = "select sus.id from SystemUserSection sus order by sus.systemUser.id";
 			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			rrn = list.indexOf(String.valueOf(currentId));

			list = HibernateUtil.getSession().getNamedQuery(
					tablePrefix + "getPrevious").setFirstResult(
					rrn + 1).setMaxResults(2).list();

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","getPreviousRecord()",e.toString());
			throw new LIMSRuntimeException("Error in getPreviousRecord() for "
					+ table, e);
		}

		return list;
	}
	
	private boolean duplicateSystemUserSectionExists(SystemUserSection systemUserSection) throws LIMSRuntimeException {
		try {

			List list = new ArrayList();

			String sql = "from SystemUserSection s where s.systemUser.id = :param and s.testSection.id = :param2 and s.id != :param3";
			org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
			query.setParameter("param", Integer.parseInt(systemUserSection.getSystemUser().getId()));
			query.setParameter("param2", Integer.parseInt(systemUserSection.getTestSection().getId()));
			
			String systemUserSectionId = "0";
			if (!StringUtil.isNullorNill(systemUserSection.getId())) {
				systemUserSectionId = systemUserSection.getId();
			}
			query.setParameter("param3", Integer.parseInt(systemUserSectionId));

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();

			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			//bugzilla 2154
			LogEvent.logError("SystemUserSectionDAOImpl","duplicateSystemUserSectionExists()",e.toString());
			throw new LIMSRuntimeException("Error in duplicateSystemUserSectionExists()", e);
		}
	}
	
	/**
	 * Get all admin test sections for user
	 * {@inheritDoc}
	 */
	public List getAllSystemAdminUserSectionsBySystemUserId(int systemUserId) throws LIMSRuntimeException {     
        List list = new Vector();
        try {
            String sql = "from SystemUserSection s where s.systemUser.id = :param and s.isAdmin = 'Y'";
            org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
            query.setParameter("param", systemUserId);
            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch(Exception e) {
            //bugzilla 2154
            LogEvent.logError("SystemUserSectionDAOImpl","getAllSystemAdminUserSectionsBySystemUserId()",e.toString());
            throw new LIMSRuntimeException("Error in SystemUserSection getAllSystemAdminUserSectionsBySystemUserId()", e);
        } 
        
        return list;
    }
		
	public List getPageOfSearchSystemUserSectionsForAdmin(String searchString) throws LIMSRuntimeException {
	    List<SystemUser> listSystemUser = new Vector();
	    List list = new Vector();
	    SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
	    listSystemUser = systemUserDAO.getPageOfSearchSystemUsers(searchString);
	    for (SystemUser sysUser : listSystemUser) {
	        try {
	            List<SystemUserSection> listSysUserSection = new Vector();
	            String sql = "from SystemUserSection s where s.systemUser = :sysUserId";
	            org.hibernate.Query query = HibernateUtil.getSession().createQuery(sql);
	            query.setInteger("sysUserId", Integer.parseInt(sysUser.getId()));
	            
	            listSysUserSection = query.list();
	            HibernateUtil.getSession().flush();
	            HibernateUtil.getSession().clear();
	            for (SystemUserSection sysUserSec : listSysUserSection) {
	                list.add(sysUserSec);
	            }
                
            } catch (Exception e) {
                LogEvent.logError("SystemUserSectionDAOImpl","getPageOfSearchSystemUserSectionsForAdmin()",e.toString());
                throw new LIMSRuntimeException("Error in SystemUserSection getPageOfSearchSystemUserSectionsForAdmin()", e);
            }
	        
	    }
	    return list;
	}
	
	public List getPageOfSearchSystemUserSectionsForSectionAdmin(String searchString, int sectionAdminId) throws LIMSRuntimeException {
	    List<SystemUser> listSystemUser = new Vector();
        List<SystemUserSection> list = new Vector();
        List<SystemUserSection> listSysUserSec = new Vector();
        // Check is sectionAdmin
        try {
            String sqlTestSection = "from SystemUserSection s where s.systemUser.id = :param and s.isAdmin='Y'";
            org.hibernate.Query queryGetTestSection = HibernateUtil.getSession().createQuery(sqlTestSection);
            queryGetTestSection.setParameter("param", sectionAdminId);
            
            listSysUserSec = queryGetTestSection.list();
            if (listSysUserSec.size() == 0) {
                return list;
            }
            
        } catch (Exception e) {
            LogEvent.logError("SystemUserSectionDAOImpl","getPageOfSearchSystemUserSectionsForAdmin()",e.toString());
            throw new LIMSRuntimeException("Error in SystemUserSection getPageOfSearchSystemUserSectionsForAdmin()", e);
        }
        
        SystemUserDAO systemUserDAO = new SystemUserDAOImpl();
        listSystemUser = systemUserDAO.getPageOfSearchSystemUsers(searchString);
        for (SystemUser sysUser : listSystemUser) {
            try {
                // an user can be admin of two or more test section
                // the following code is used to get user belong to each test section, then add to the list that store all users belong to this admin user
                for (int i= 0; i <listSysUserSec.size(); i++) {
                    List<SystemUserSection> listSysUserSection = new Vector();
                    SystemUserSection testSectionAdmin = listSysUserSec.get(i);
                    String sql = "from SystemUserSection s where s.systemUser = :sysUserId and s.testSection = :testSectionId";
                    org.hibernate.Query queryGetSysUser = HibernateUtil.getSession().createQuery(sql);
                    queryGetSysUser.setInteger("testSectionId", Integer.parseInt(testSectionAdmin.getTestSection().getId()));
                    queryGetSysUser.setInteger("sysUserId", Integer.parseInt(sysUser.getId()));
                    
                    listSysUserSection = queryGetSysUser.list();
                    HibernateUtil.getSession().flush();
                    HibernateUtil.getSession().clear();
                    list.addAll(listSysUserSection);
                }
                
               /* List<SystemUserSection> listSysUserSection = new Vector();
                SystemUserSection testSectionAdmin = listSysUserSec.get(0);
                String sql = "from SystemUserSection s where s.systemUser = :sysUserId and s.testSection = :testSectionId";
                org.hibernate.Query queryGetSysUser = HibernateUtil.getSession().createQuery(sql);
                queryGetSysUser.setInteger("testSectionId", Integer.parseInt(testSectionAdmin.getTestSection().getId()));
                queryGetSysUser.setInteger("sysUserId", Integer.parseInt(sysUser.getId()));
                
                listSysUserSection = queryGetSysUser.list();
                HibernateUtil.getSession().flush();
                HibernateUtil.getSession().clear();
                
                for (SystemUserSection sysUserSec : listSysUserSection) {
                    list.add(sysUserSec);
                }*/
                
            } catch (Exception e) {
                LogEvent.logError("SystemUserSectionDAOImpl","getPageOfSearchSystemUserSectionsForAdmin()",e.toString());
                throw new LIMSRuntimeException("Error in SystemUserSection getPageOfSearchSystemUserSectionsForAdmin()", e);
            }
        }
        return list;
	}
	
}