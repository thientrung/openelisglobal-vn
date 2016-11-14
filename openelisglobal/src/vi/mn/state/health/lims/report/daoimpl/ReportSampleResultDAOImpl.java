package vi.mn.state.health.lims.report.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.report.dao.ReportSampleResultDAO;
import vi.mn.state.health.lims.report.valueholder.ReportSampleResult;

public class ReportSampleResultDAOImpl implements ReportSampleResultDAO {

	@Override
	public List<ReportSampleResult> getAllSamplePersion(String accessNumber)
			throws LIMSRuntimeException {
		List<ReportSampleResult> list=null;
		try {
			list=new ArrayList<ReportSampleResult>();
			String sql="from ReportSampleResult r where r.accessNumber  BETWEEN :assNum AND :num";
			Query query=  HibernateUtil.getSession().createQuery(sql);
			query.setParameter("assNum", accessNumber);
			query.setParameter("num", "1670010082");
			//query.setParameter("assNumFrom", "1670010079");
			list=query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
		} catch (LIMSRuntimeException e) {
			// TODO: handle exception
		}
		return list;
	}

}
