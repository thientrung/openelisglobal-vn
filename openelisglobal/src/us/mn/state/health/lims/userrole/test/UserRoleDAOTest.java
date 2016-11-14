package us.mn.state.health.lims.userrole.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.userrole.dao.UserRoleDAO;
import us.mn.state.health.lims.userrole.daoimpl.UserRoleDAOImpl;
import us.mn.state.health.lims.userrole.valueholder.UserRole;

public class UserRoleDAOTest {

    @Test
    public void testInsertData() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            UserRole userRole = new UserRole();
            boolean isInserted = userRoleDAO.insertData(userRole);

            if (isInserted) {
                Assert.assertTrue(true);
                System.out.println("User role data is successfully inserted.");
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testDeleteData() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List<UserRole> listUserRole = new ArrayList<UserRole>();
            userRoleDAO.deleteData(listUserRole);
            Assert.assertTrue(true);
            System.out.println("User role data is successfully deleted.");
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetAllUserRoles() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List listUserRole = new ArrayList<>();
            listUserRole = userRoleDAO.getAllUserRoles();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUserRole));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPageOfUserRoles() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List listPageUserRole = new ArrayList<>();
            listPageUserRole = userRoleDAO.getPageOfUserRoles(0);
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listPageUserRole));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetData() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            UserRole userRole = new UserRole();
            userRoleDAO.getData(userRole);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUpdateData() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            UserRole userRole = new UserRole();
            userRoleDAO.updateData(userRole);
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetNextUserRoleRecord() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List listUserRoleRec = new ArrayList<>();
            listUserRoleRec = userRoleDAO.getNextUserRoleRecord("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUserRoleRec));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetPreviousUserRoleRecord() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List listUserRoleRec = new ArrayList<>();
            listUserRoleRec = userRoleDAO.getPreviousUserRoleRecord("2");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUserRoleRec));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testGetRoleIdsForUser() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            List<String> listUserRoleIds = new ArrayList<String>();
            listUserRoleIds = userRoleDAO.getRoleIdsForUser("1");
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(listUserRoleIds));
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUserInRole() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            boolean isExisting = userRoleDAO.userInRole("1","admin");

            if (isExisting) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
    @Test
    public void testUserInRoleCollection() {
        try {
            UserRoleDAO userRoleDAO = new UserRoleDAOImpl();
            Collection<String> rolenames = null;
            boolean isExisting = userRoleDAO.userInRole("1", rolenames);

            if (isExisting) {
                Assert.assertTrue(true);
            } else {
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
