package vi.mn.state.health.lims.report.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.report.dao.ReportResultHemaDAO;
import vi.mn.state.health.lims.report.valueholder.ReportResultHema;

public class ReportResultHemaDAOImpl implements ReportResultHemaDAO {

	@Override
	public List<ReportResultHema> getListReportData()
			throws LIMSRuntimeException {
		List<ReportResultHema> list=null;
		try {
			list=new ArrayList<ReportResultHema>();
			String sql="from ReportResultHema";
			Query query=  HibernateUtil.getSession().createQuery(sql);
			list=query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (LIMSRuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}

}
