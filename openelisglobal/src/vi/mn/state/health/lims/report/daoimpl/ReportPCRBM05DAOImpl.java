/**
 * 
 */
package vi.mn.state.health.lims.report.daoimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.report.common.ReportUtil;
import vi.mn.state.health.lims.report.dao.ReportPCRBM05DAO;
import vi.mn.state.health.lims.report.valueholder.ReportModelPCRBM05;

/**
 * @author nhuql.gv
 *
 */
public class ReportPCRBM05DAOImpl implements ReportPCRBM05DAO {

	@Override
	public List<ReportModelPCRBM05> getListDataByNumberAndDate(String accessNumberFrom, String accessNumberTo,String datefrom, String dateTo , int testId,String organizationName) throws LIMSRuntimeException {
		List<ReportModelPCRBM05> list=null;
		try {
			//check valid access number
			if(GenericValidator.isBlankOrNull(accessNumberTo)){
				accessNumberTo=accessNumberFrom;
			}
			if(accessNumberTo.compareTo(accessNumberFrom) <0){
				accessNumberTo=accessNumberFrom;
			}
			Calendar start = ReportUtil.convertToSqlDateByLocal(datefrom);
	        if ( GenericValidator.isBlankOrNull(dateTo)) {
	        	dateTo = datefrom;
	        }
	        Calendar end = ReportUtil.GetExactDate(dateTo);
			
			list=new ArrayList<ReportModelPCRBM05>();
			
		    String sql  = "";
			if(organizationName != null && !organizationName.equals("")){
				sql="from ReportModelPCRBM05 a where (a.receivedDate >= :dateFrom AND a.receivedDate < :dateTo) AND (a.accessionNumber >=:accessNumberFrom AND a.accessionNumber <=:accessNumberTo) AND (a.testId =:testId) AND (p.organizationName=:organizationName)";
			}else{
				sql="from ReportModelPCRBM05 a where (a.receivedDate >= :dateFrom AND a.receivedDate < :dateTo) AND (a.accessionNumber >=:accessNumberFrom AND a.accessionNumber <=:accessNumberTo) AND (a.testId =:testId)";
			}
			Query query=  HibernateUtil.getSession().createQuery(sql);
			query.setParameter("dateFrom", start);
			query.setParameter("dateTo", end);
			query.setParameter("accessNumberFrom", accessNumberFrom);
			query.setParameter("accessNumberTo", accessNumberTo);
			query.setParameter("testId", testId);
			if(organizationName != null && !organizationName.equals("")){
				query.setParameter("organizationName", organizationName);
			}
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			
		} catch (LIMSRuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ReportModelPCRBM05> getListDataByAccessionNumber(String accessNumberFrom, String accessNumberTo , int testId, String organizationName)throws LIMSRuntimeException {
		List<ReportModelPCRBM05> list=null;
		try {
			if(GenericValidator.isBlankOrNull(accessNumberTo)){
				accessNumberTo=accessNumberFrom;
			}
			if(accessNumberTo.compareTo(accessNumberFrom) <0){
				accessNumberTo=accessNumberFrom;
			}
			
			list=new ArrayList<ReportModelPCRBM05>();
			String sql = "";
			if(organizationName != null && !organizationName.equals("")){
				sql="from ReportModelPCRBM05 a where (a.accessionNumber >=:accessNumberFrom AND a.accessionNumber <=:accessNumberTo) AND (a.testId =:testId) AND (p.organizationName=:organizationName)";
			}else{
				sql="from ReportModelPCRBM05 a where (a.accessionNumber >=:accessNumberFrom AND a.accessionNumber <=:accessNumberTo) AND (a.testId =:testId)";
			}
			Query query=  HibernateUtil.getSession().createQuery(sql);
			query.setParameter("accessNumberFrom", accessNumberFrom);
			query.setParameter("accessNumberTo", accessNumberTo);
			query.setParameter("testId", testId);
			if(organizationName != null && !organizationName.equals("")){
				query.setParameter("organizationName", organizationName);
			}
			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			
		} catch (LIMSRuntimeException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ReportModelPCRBM05> getListDataByCollectionDate(String dateFrom, String dateTo , int testId, String organizationName) throws LIMSRuntimeException {
		List<ReportModelPCRBM05> list=null;
		try {
			
			Calendar start = ReportUtil.convertToSqlDateByLocal(dateFrom);
	        if ( GenericValidator.isBlankOrNull(dateTo)) {
	        	dateTo = dateFrom;
	        }
	        Calendar end = ReportUtil.GetExactDate(dateTo);
	        
			list=new ArrayList<ReportModelPCRBM05>();
			String sql = "";
			if(organizationName != null && !organizationName.equals("")){
				sql="from ReportModelPCRBM05 a where (a.receivedDate >= :dateFrom AND a.receivedDate < :dateTo) AND (a.testId =:testId) AND (p.organizationName=:organizationName)";
			}else{
				sql="from ReportModelPCRBM05 a where (a.receivedDate >= :dateFrom AND a.receivedDate < :dateTo) AND (a.testId =:testId)";
			}
			Query query=  HibernateUtil.getSession().createQuery(sql);
			query.setParameter("dateFrom", start);
			query.setParameter("dateTo", end);
			query.setParameter("testId", testId);
			if(organizationName != null && !organizationName.equals("")){
				query.setParameter("organizationName", organizationName);
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
