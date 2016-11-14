package us.mn.state.health.lims.testcodes.test;

import org.junit.Assert;
import org.junit.Test;

import us.mn.state.health.lims.testcodes.dao.OrgHL7SchemaDAO;
import us.mn.state.health.lims.testcodes.daoimpl.OrgHL7SchemaDAOImpl;
import us.mn.state.health.lims.testcodes.valueholder.OrganizationHL7Schema;

public class OrgHL7SchemaDAOTest {
    
    @Test
    public void testGetOrganizationHL7SchemaByOrgId() {
        try {
        	OrgHL7SchemaDAO orgHL7SchemaDAO = new OrgHL7SchemaDAOImpl();
        	OrganizationHL7Schema organizationHL7Schema = new OrganizationHL7Schema();
        	organizationHL7Schema = orgHL7SchemaDAO.getOrganizationHL7SchemaByOrgId("2");
        	
        	if (organizationHL7Schema != null) {
                Assert.assertTrue(true);
        	} else {
                Assert.assertTrue(false);
        	}
            
        } catch (Exception e) {
            Assert.assertFalse(false);
        }
    }
    
}
