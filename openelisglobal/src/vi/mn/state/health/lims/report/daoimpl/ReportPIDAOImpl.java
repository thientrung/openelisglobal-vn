package vi.mn.state.health.lims.report.daoimpl;

import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import vi.mn.state.health.lims.report.dao.ReportPIDAO;
import vi.mn.state.health.lims.report.valueholder.PIDengueAggregate;

public class ReportPIDAOImpl implements ReportPIDAO{

	@Override
	public List<PIDengueAggregate> getDengueAggregateRecord(String month, String year) {
		String sql="from PIDengueAggregate v where v.month <=:month AND v.year =:year";
		
		Query query= HibernateUtil.getSession().createQuery(sql);
		query.setParameter("month", Double.parseDouble(month));
		query.setParameter("year", Double.parseDouble(year));

		List<PIDengueAggregate> list = query.list();
		return list;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public List getListOfSatisfiedAccessionNumber(String fromAccessionNumber, String toAccessionNumber,
            String fromStartedIllnessDate, String toStartedIllnessDate, String fromRecievedDate, String toRecievedDate,
            String fromResultDate, String toResultDate, String testId, String organizationID) throws LIMSRuntimeException {
       
        List list;
        try {
          //convert string date (dd/MM/yyyy) to date format (yyyy-MM-dd)
            java.sql.Date fromIllnessDate = DateUtil.convertStringDateTimeToSqlDate(fromStartedIllnessDate);
            java.sql.Date toIllnessDate = DateUtil.convertStringDateTimeToSqlDate(toStartedIllnessDate);
            java.sql.Date fromReceivedDate = DateUtil.convertStringDateTimeToSqlDate(fromRecievedDate);
            java.sql.Date toReceivedDate = DateUtil.convertStringDateTimeToSqlDate(toRecievedDate);
            java.sql.Date fromResultedDate = DateUtil.convertStringDateTimeToSqlDate(fromResultDate);
            java.sql.Date toResultedDate = DateUtil.convertStringDateTimeToSqlDate(toResultDate);
            
            if (fromAccessionNumber.isEmpty()) {
                fromAccessionNumber = null;
            }
            if (toAccessionNumber.isEmpty()) {
                toAccessionNumber = null;
            }
            // convert string to int value
            int selectedTestId;
            if (!StringUtil.isNullorNill(testId)) {
                selectedTestId = Integer.valueOf(testId);
            } else {
                selectedTestId = 0;
            }
            //convert string to int value
            int selectedOrganizationId;
            if (!StringUtil.isNullorNill(organizationID)) {
                selectedOrganizationId = Integer.valueOf(organizationID);
            } else {
                selectedOrganizationId = 0;
            }
            
            org.hibernate.Query query = HibernateUtil.getSession().createSQLQuery(
                    "select sp_get_list_accession_number(:fromAccessionNumber,:toAccessionNumber,:fromStartedIllnessDate,:toStartedIllnessDate,:fromRecievedDate,:toRecievedDate,:fromResultDate,:toResultDate,:testId,:organizationId)");
            query.setString("fromAccessionNumber", fromAccessionNumber);
            query.setString("toAccessionNumber", toAccessionNumber);
            query.setDate("fromStartedIllnessDate", fromIllnessDate);
            query.setDate("toStartedIllnessDate", toIllnessDate);
            query.setDate("fromRecievedDate", fromReceivedDate);
            query.setDate("toRecievedDate", toReceivedDate);
            query.setDate("fromResultDate", fromResultedDate);
            query.setDate("toResultDate", toResultedDate);
            query.setInteger("testId", selectedTestId);
            query.setInteger("organizationId", selectedOrganizationId);
            
            list = query.list();
            HibernateUtil.getSession().flush();
            HibernateUtil.getSession().clear();
        } catch(Exception e) {
            LogEvent.logError("ReportPIDAOImpl","getListOfSatisfiedAccessionNumber()",e.toString());
            throw new LIMSRuntimeException("Error in ReportPIDAOImpl getListOfSatisfiedAccessionNumber()", e);
        }
        return list;
    }

}
