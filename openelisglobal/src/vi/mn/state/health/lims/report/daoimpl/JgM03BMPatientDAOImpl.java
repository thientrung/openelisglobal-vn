package vi.mn.state.health.lims.report.daoimpl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Query;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import vi.mn.state.health.lims.report.dao.JgM03BMPatientDAO;
import vi.mn.state.health.lims.report.valueholder.JgM03_BM_Patient;

public class JgM03BMPatientDAOImpl implements JgM03BMPatientDAO {
	String sql = "";
	private final String invalidAccession = "0";

	@Override
	public List<JgM03_BM_Patient> getAllJgM03BMPatient(String accessNumberFrom,
			String accessNumberTo, int testId, String organizationName) {
		if (GenericValidator.isBlankOrNull(accessNumberTo)) {
			accessNumberTo = accessNumberFrom;
		}
		if (accessNumberTo.compareTo(accessNumberFrom) < 0) {
			accessNumberTo = accessNumberFrom;
		}

		String testIdOfJE = SystemConfiguration.getInstance().getTestOfJE();
		String listTestOfJE = SystemConfiguration.getInstance()
				.getListTestOfJE();

		sql = "from JgM03_BM_Patient p where p.accessionNumber>= :accessFrom AND p.accessionNumber <=:accessTo AND (p.testId = ";

		if (!StringUtil.isNullorNill(testIdOfJE)
				&& testIdOfJE.equals(String.valueOf(testId))) {
			String[] listTest = listTestOfJE.split(",");
			for (int i = 0; i < listTest.length; i++) {
				if (i < listTest.length - 1) {
					sql += Integer.valueOf(listTest[i]) + " OR p.testId = ";
				} else {
					sql += Integer.valueOf(listTest[i])
							+ ") and p.organizationName=:organizationName ORDER BY p.accessionNumber ";
				}
			}
		} else {
			sql += Integer.valueOf(testId)
					+ ") and p.organizationName=:organizationName ORDER BY p.accessionNumber ";
		}
		if (organizationName == null || organizationName.equals("")) {
			sql = sql.replace("and p.organizationName=:organizationName", " ");
		}
		Query query = HibernateUtil.getSession().createQuery(sql);
		query.setParameter("accessFrom", accessNumberFrom);
		query.setParameter("accessTo", accessNumberTo);
		// query.setParameter("testid", testId);
		if (organizationName != null && !organizationName.equals("")) {
			query.setParameter("organizationName", organizationName);
		}
		List<JgM03_BM_Patient> list = query.list();
		return list;
	}

	@Override
	public List<JgM03_BM_Patient> getAllJgM03BMPatientDate(String dateFrom,
			String DateTo, int testId, String organizationName) {
		Calendar start = getCalendarForDateString(dateFrom);
		if (GenericValidator.isBlankOrNull(DateTo)) {
			DateTo = dateFrom;
		}
		Calendar end = getCalendarForDateString(DateTo);
		// worried about time stamps including time information, so might be
		// missed comparing to midnight (00:00:00.00) on the last day of range.
		end.add(Calendar.DAY_OF_YEAR, 1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);

		if (organizationName != null && !organizationName.equalsIgnoreCase("")) {
			sql = "from JgM03_BM_Patient p where (p.receivedDate >= :dateFrom) AND (p.receivedDate < :dateTo) AND (p.testId=:testid) "
					+ "AND (p.organizationName=:organizationName) order by p.accessionNumber";
		} else {
			sql = "from JgM03_BM_Patient p where (p.receivedDate >= :dateFrom) AND (p.receivedDate < :dateTo) AND (p.testId=:testid) order by p.accessionNumber";
		}
		Query query = HibernateUtil.getSession().createQuery(sql);
		query.setParameter("testid", testId);
		query.setParameter("dateFrom", start);
		query.setParameter("dateTo", end);
		if (organizationName != null && !organizationName.equalsIgnoreCase("")) {
			query.setParameter("organizationName", organizationName);
		}
		List<JgM03_BM_Patient> list = query.list();
		return list;
	}

	private Calendar getCalendarForDateString(String recievedDate) {
		String localeName = SystemConfiguration.getInstance()
				.getDefaultLocale().toString();
		Locale locale = new Locale(localeName);
		Calendar calendar = Calendar.getInstance(locale);

		Date date = DateUtil.convertStringDateToSqlDate(recievedDate,
				localeName);
		calendar.setTime(date);
		return calendar;
	}

	@Override
	public List<JgM03_BM_Patient> getAllJgM03BMPatientNumDate(
			String accessNumberFrom, String accessNumberTo, String datefrom,
			String dateTo, int testId, String organizationName) {
		// check access number
		if (GenericValidator.isBlankOrNull(accessNumberTo)) {
			accessNumberTo = accessNumberFrom;
		}
		if (accessNumberTo.compareTo(accessNumberFrom) < 0) {
			accessNumberTo = accessNumberFrom;
		}
		Calendar start = getCalendarForDateString(datefrom);
		if (GenericValidator.isBlankOrNull(dateTo)) {
			dateTo = datefrom;
		}
		Calendar end = getCalendarForDateString(dateTo);
		// worried about time stamps including time information, so might be
		// missed comparing to midnight (00:00:00.00) on the last day of range.
		end.add(Calendar.DAY_OF_YEAR, 1);
		end.set(Calendar.HOUR_OF_DAY, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.SECOND, 0);

		if (organizationName != null && !organizationName.equals("")) {
			sql = "from JgM03_BM_Patient p where (p.receivedDate >= :dateFrom AND p.receivedDate < :dateTo)"
					+ " AND (p.accessionNumber >=:accessNumberFrom AND p.accessionNumber >=:accessNumberTo) AND p.testId=:testid and p.organizationName=:organizationName order by p.accessionNumber";
		} else {
			sql = "from JgM03_BM_Patient p where (p.receivedDate >= :dateFrom AND p.receivedDate < :dateTo)"
					+ " AND (p.accessionNumber >=:accessNumberFrom AND p.accessionNumber >=:accessNumberTo) AND p.testId=:testid order by p.accessionNumber";
		}

		Query query = HibernateUtil.getSession().createQuery(sql);
		query.setParameter("dateFrom", start);
		query.setParameter("dateTo", end);
		query.setParameter("accessNumberFrom", accessNumberFrom);
		query.setParameter("accessNumberTo", accessNumberTo);
		query.setParameter("testid", testId);
		if (organizationName != null && !organizationName.equals("")) {
			query.setParameter("organizationName", organizationName);
		}
		List<JgM03_BM_Patient> list = query.list();
		return list;
	}

	@Override
	public List<JgM03_BM_Patient> getListData(String accessNumberFrom,
			String accessNumberTo, int testId, String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange,String organizationId) throws LIMSRuntimeException {
		// check and get sql query from parameter date
		String sqlPara = CheckParaAdd(lowerIllnessDateRange,
				upperIllnessDateRange, lowerDateRange, upperDateRange,
				lowerResultDateRange, upperResultDateRange,organizationId);
		List<JgM03_BM_Patient> list = null;

		String testIdOfJE = SystemConfiguration.getInstance().getTestOfJE();
		String listTestOfJE = SystemConfiguration.getInstance()
				.getListTestOfJE();

		try {
			list = new ArrayList<JgM03_BM_Patient>();
			String sql = "";
			if (!accessNumberFrom.equals("") || !accessNumberTo.equals("")) {
				if (!accessNumberTo.equals("") && accessNumberFrom.equals("")) {
					accessNumberFrom = accessNumberTo;
				}
				if (!accessNumberFrom.equals("") && accessNumberTo.equals("")) {
					accessNumberTo = accessNumberFrom;
				}
				sql += "from JgM03_BM_Patient a where a.accessionNumber>= :accessNumberFrom AND a.accessionNumber <=:accessNumberTo AND ";
			} else {
				sql += "from JgM03_BM_Patient a where";
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
								+ ") ORDER BY a.organizationName, a.accessionNumber ";
					}
				}
			} else {
				sql += Integer.valueOf(testId)
						+ ") ORDER BY a.organizationName, a.accessionNumber ";
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
			e.printStackTrace();
		}
		return list;
	}

	private String CheckParaAdd(String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange, String organizationId) {
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
		if(!StringUtil.isNullorNill(organizationId)) {
			sql += " a.organizationId = " + Integer.valueOf(organizationId) + " AND ";
		}
		
		return sql;
	}

	private String parserDateTimestamp(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new java.util.Date(timestamp.getTime()));
		return date;
	}

}
