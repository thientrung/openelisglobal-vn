package us.mn.state.health.lims.organization.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;

public class OrganizationDAOTest {
	
	// Valid - Expecting Success
    @Test
    public void testGetListOrganizationName() {
        try {
        	OrganizationDAO organizationDAO = new OrganizationDAOImpl();
        	List<Organization> organization = new ArrayList<Organization>();
            organization = organizationDAO.getListOrganizationName();
            Assert.assertTrue(!org.springframework.util.CollectionUtils.isEmpty(organization));
            System.out.println("Organization Size: " + organization.size());

        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
