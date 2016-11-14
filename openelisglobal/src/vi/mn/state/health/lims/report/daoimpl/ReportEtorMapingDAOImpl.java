/**
 * 
 */
package vi.mn.state.health.lims.report.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.report.dao.ReportEtorMapingDAO;
import vi.mn.state.health.lims.report.valueholder.EtorMapingModel;

/**
 * @author nhuql.gv
 *
 */
public class ReportEtorMapingDAOImpl implements ReportEtorMapingDAO {

	@Override
	public List<EtorMapingModel> getListData(String accessNumberFrom , String accessNumberTo , int testId)throws LIMSRuntimeException {
		List<EtorMapingModel> list=null;
		
		String testIdOfJE = SystemConfiguration.getInstance().getTestOfJE();
		String listTestOfJE = SystemConfiguration.getInstance().getListTestOfJE();
		
		try {
			list=new ArrayList<EtorMapingModel>();
			String sql="from EtorMapingModel a where a.accessionNumber>= :accessNumberFrom AND a.accessionNumber <=:accessNumberTo and (a.testId = ";
			
			if (!StringUtil.isNullorNill(testIdOfJE) && testIdOfJE.equals(String.valueOf(testId))) {
				String[] listTest = listTestOfJE.split(",");
				for (int i = 0; i < listTest.length; i++) {
					if (i < listTest.length - 1) {
						sql += Integer.valueOf(listTest[i]) + " OR a.testId = ";
					} else {
						sql += Integer.valueOf(listTest[i]) + ") ORDER BY a.accessionNumber ";
					}
				}
			} else {
				sql += Integer.valueOf(testId) + ") ORDER BY a.accessionNumber ";
			}
			
			Query query=  HibernateUtil.getSession().createQuery(sql);
			query.setParameter("accessNumberFrom", accessNumberFrom);
			
			if (!StringUtil.isNullorNill(accessNumberTo)) {
				query.setParameter("accessNumberTo", accessNumberTo);
			} else {
				query.setParameter("accessNumberTo", accessNumberFrom);
			}
			
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (LIMSRuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}

}
