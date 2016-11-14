/**
 * 
 */
package vi.mn.state.health.lims.report.daoimpl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import vi.mn.state.health.lims.report.dao.ReportTestResultDAO;
import vi.mn.state.health.lims.report.valueholder.ReportModelTestResult;

/**
 * @author nhuql.gv
 * 
 */
public class ReportTestResultDAOImpl implements ReportTestResultDAO {
	
	private final String invalidAccession = "0";

	@Override
	public List<ReportModelTestResult> getListDataWS(String accessNumberFrom,
			String accessNumberTo, int testId) throws LIMSRuntimeException {
		List<ReportModelTestResult> list = null;

		String testIdOfJE = SystemConfiguration.getInstance().getTestOfJE();
		String listTestOfJE = SystemConfiguration.getInstance()
				.getListTestOfJE();

		try {
			list = new ArrayList<ReportModelTestResult>();
			String sql = "from ReportModelTestResult a where a.accessionNumber>= :accessNumberFrom AND a.accessionNumber <=:accessNumberTo and (a.testId = ";

			if (!StringUtil.isNullorNill(testIdOfJE)
					&& testIdOfJE.equals(String.valueOf(testId))) {
				String[] listTest = listTestOfJE.split(",");
				for (int i = 0; i < listTest.length; i++) {
					if (i < listTest.length - 1) {
						sql += Integer.valueOf(listTest[i]) + " OR a.testId = ";
					} else {
						sql += Integer.valueOf(listTest[i])
								+ ") ORDER BY a.accessionNumber ";
					}
				}
			} else {
				sql += Integer.valueOf(testId)
						+ ") ORDER BY a.accessionNumber ";
			}

			Query query = HibernateUtil.getSession().createQuery(sql);
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
			LogEvent.logError("ReportTestResultDAOImpl", "getListDataWS()", Arrays.toString(e.getStackTrace()));
		}
		return list;
	}

	@Override
	public List<ReportModelTestResult> getListData(String accessNumberFrom,
			String accessNumberTo, int testId, String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange) throws LIMSRuntimeException {
		// check and get sql query from parameter date
		String sqlPara = CheckParaAdd(lowerIllnessDateRange,
				upperIllnessDateRange, lowerDateRange, upperDateRange,
				lowerResultDateRange, upperResultDateRange);
		List<ReportModelTestResult> list = null;

		String testIdOfJE = SystemConfiguration.getInstance().getTestOfJE();
		String listTestOfJE = SystemConfiguration.getInstance()
				.getListTestOfJE();

		try {
			list = new ArrayList<ReportModelTestResult>();
			String sql = "";
			if (!accessNumberFrom.equals("") || !accessNumberTo.equals("")) {
				if (!accessNumberTo.equals("") && accessNumberFrom.equals("")) {
					accessNumberFrom = accessNumberTo;
				}
				if (!accessNumberFrom.equals("") && accessNumberTo.equals("")) {
					accessNumberTo = accessNumberFrom;
				}
				sql += "from ReportModelTestResult a where a.accessionNumber>= :accessNumberFrom AND a.accessionNumber <=:accessNumberTo AND ";
			} else {
				sql += "from ReportModelTestResult a where";
			}
			if (sqlPara.length() > 5) {
				sql += sqlPara + " (a.testId = ";
			} else {
				sql += " (a.testId = ";
			}
			if (!StringUtil.isNullorNill(testIdOfJE)
					&& testIdOfJE.equals(String.valueOf(testId))) {
				String[] listTest = listTestOfJE.split(",");
				for (int i = 0; i < listTest.length; i++) {
					if (i < listTest.length - 1) {
						sql += Integer.valueOf(listTest[i]) + " OR a.testId = ";
					} else {
						sql += Integer.valueOf(listTest[i])
								+ ") ORDER BY a.accessionNumber ";
					}
				}
			} else {
				sql += Integer.valueOf(testId)
						+ ") ORDER BY a.accessionNumber ";
			}

			Query query = HibernateUtil.getSession().createQuery(sql);
			
			// This code is to check the parameters of accession number
			// If users would like to print report from an accession number to an accession number
			if (!StringUtil.isNullorNill(accessNumberFrom) && !StringUtil.isNullorNill(accessNumberTo)) {
				SampleDAO sampleDAO = new SampleDAOImpl();
				// Need to check accessNumber To
				// If it is invalid, print nothing
				// If it is valid, set it as a parameter 
				Sample sampleTo = sampleDAO.getSampleByAccessionNumber(accessNumberTo);
				if (sampleTo != null) {
					query.setParameter("accessNumberFrom", accessNumberFrom);
					query.setParameter("accessNumberTo", accessNumberTo);
				} else {
					query.setParameter("accessNumberFrom", accessNumberFrom);
					// This code is to make the report print nothing
					query.setParameter("accessNumberTo", invalidAccession);
				}
			} else if (!StringUtil.isNullorNill(accessNumberFrom) && StringUtil.isNullorNill(accessNumberTo)) {
				query.setParameter("accessNumberFrom", accessNumberFrom);
				query.setParameter("accessNumberTo", accessNumberFrom);
			} else if (StringUtil.isNullorNill(accessNumberFrom) && !StringUtil.isNullorNill(accessNumberTo)) {
				query.setParameter("accessNumberFrom", accessNumberTo);
				query.setParameter("accessNumberTo", accessNumberTo);
			}

			list = query.list();
			HibernateUtil.getSession().flush();
			HibernateUtil.getSession().clear();
			
		} catch (LIMSRuntimeException e) {
            LogEvent.logError("ReportTestResultDAOImpl", "getListData()", Arrays.toString(e.getStackTrace()));
		}
		
		return list;
	}

	private String CheckParaAdd(String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange) {
		String sql = " ";
		boolean flag = false;
		if ((!lowerIllnessDateRange.equals("") && lowerIllnessDateRange != null)
				|| (!upperIllnessDateRange.equals("") && upperIllnessDateRange != null)) {
			if ((!lowerIllnessDateRange.equals("") && lowerIllnessDateRange != null)
					&& (!upperIllnessDateRange.equals("") && upperIllnessDateRange != null)) {
				//
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerIllnessDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperIllnessDateRange
								+ " 00:00");
				sql += " a.illnessDate BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
			if ((!lowerIllnessDateRange.equals("") && lowerIllnessDateRange != null)
					&& (upperIllnessDateRange.equals("") || upperIllnessDateRange == null)) {
				// convert date
				upperIllnessDateRange = lowerIllnessDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerIllnessDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperIllnessDateRange
								+ " 00:00");
				sql += " a.illnessDate BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
			if ((lowerIllnessDateRange.equals("") || lowerIllnessDateRange == null)
					&& (!upperIllnessDateRange.equals("") && upperIllnessDateRange != null)) {
				lowerIllnessDateRange = upperIllnessDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerIllnessDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperIllnessDateRange
								+ " 00:00");
				sql += " a.illnessDate BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
		}
		if (flag == true) {
			sql += " AND ";
			flag = false;
		}
		if ((!lowerDateRange.equals("") && lowerDateRange != null)
				|| (!upperDateRange.equals("") && upperDateRange != null)) {
			if ((!lowerDateRange.equals("") && lowerDateRange != null)
					&& (!upperDateRange.equals("") && upperDateRange != null)) {
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerDateRange + " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperDateRange + " 00:00");
				sql += " to_char(a.receivedDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
			if ((!lowerDateRange.equals("") && lowerDateRange != null)
					&& (upperDateRange.equals("") || upperDateRange == null)) {
				upperDateRange = lowerDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerDateRange + " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperDateRange + " 00:00");
				sql += " to_char(a.receivedDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";

				flag = true;
			}
			if ((lowerDateRange.equals("") || lowerDateRange == null)
					&& (!upperDateRange.equals("") && upperDateRange != null)) {
				lowerDateRange = upperDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerDateRange + " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperDateRange + " 00:00");
				sql += " to_char(a.receivedDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
		}
		if (flag == true) {
			sql += " AND ";
			flag = false;
		}
		if ((!lowerResultDateRange.equals("") && lowerResultDateRange != null)
				|| (!upperResultDateRange.equals("") && upperResultDateRange != null)) {
			if ((!lowerResultDateRange.equals("") && lowerResultDateRange != null)
					&& (!upperResultDateRange.equals("") && upperResultDateRange != null)) {
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerResultDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperResultDateRange
								+ " 00:00");
				sql += " to_char(a.resultDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
			if ((!lowerResultDateRange.equals("") && lowerResultDateRange != null)
					&& (upperResultDateRange.equals("") || upperResultDateRange == null)) {
				upperResultDateRange = lowerResultDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerResultDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperResultDateRange
								+ " 00:00");
				sql += " to_char(a.resultDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				flag = true;
			}
			if ((lowerResultDateRange.equals("") || lowerResultDateRange == null)
					&& (!upperResultDateRange.equals("") && upperResultDateRange != null)) {
				lowerResultDateRange = upperResultDateRange;
				Timestamp start = DateUtil
						.convertStringDateToTimestamp(lowerResultDateRange
								+ " 00:00");
				Timestamp end = DateUtil
						.convertStringDateToTimestamp(upperResultDateRange
								+ " 00:00");
				sql += " to_char(a.resultDate,'yyyy-MM-dd') BETWEEN " + "'"
						+ parserDateTimestamp(start) + "'" + " AND " + "'"
						+ parserDateTimestamp(end) + "'" + " ";
				sql += " to_char(a.resultDate,'yyyy-MM-dd') BETWEEN " + lowerResultDateRange
						+ " AND " + upperResultDateRange + "";
				flag = true;
			}
		}
		if (flag == true) {
			sql += " AND ";
			flag = false;
		}
		return sql;
	}

	private String parserDateTimestamp(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new java.util.Date(timestamp.getTime()));
		return date;
	}

}
