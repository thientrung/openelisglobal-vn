/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.reports.action.implementation;

import static org.apache.commons.validator.GenericValidator.isBlankOrNull;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.connection.PostgreSQLConnector;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.ErrorMessages;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;

public abstract class Report implements IReportCreator {

	// public static ImageDAO imageDAO = new ImageDAOImpl();
	public static SiteInformationDAO siteInformationDAO = new SiteInformationDAOImpl();
	public static final String ERROR_REPORT = "NoticeOfReportError";
	public String imageUrl = "";
	protected static final String CSV = "csv";

	protected boolean initialized = false;
	protected boolean errorFound = false;
	protected List<ErrorMessages> errorMsgs = new ArrayList<ErrorMessages>();
	protected HashMap<String, Object> reportParameters = null;
	private String fullReportFilename;

	// nhuql.gv ADD for display image on right of the report
	public String rightImageUrl = "";
	public String procedueReport = "";
	public String organizationName = "";
	public String leftFooter = "";
	public String rightHeader = "";
	public int month;
	public int year;
	public String startAccessionNumber;
	public String endAccessionNumber;
	public String startReceivedDate;
	public String endReceivedDate;
	public String projectDengue;
	//new add
	public String isLogable="";
	public String startIllnessDate ="";
	public String endIllnessDate ="";
	public String startResultDate="";
	public String endResultDate="";
	public String testID="";
	public String organizationID="";
	public String emergency="";
	public String doctor="";
	public String password="";
	//end
	
	public boolean isUseDataSource = true;

	// END nhuql.gv ADD

	protected void initializeReport() {
		initialized = true;
	}

	public String getResponseHeaderName() {
		return null;
	}

	public String getResponseHeaderContent() {
		return null;
	}

	/**
	 * @see us.mn.state.health.lims.reports.action.implementation.IReportCreator#getContentType()
	 */
	public String getContentType() {
		return "application/pdf; charset=UTF-8";
	}

	/**
	 * Make sure we have a reportParameters map and make sure there is lab
	 * director in that map (for any possible error report). All reports need a
	 * director name either in their header including or on their error report
	 * page."
	 */
	protected void createReportParameters() {
		reportParameters = (reportParameters != null) ? reportParameters
				: new HashMap<String, Object>();
		reportParameters.put("directorName", ConfigurationProperties
				.getInstance().getPropertyValue(Property.labDirectorName));
		reportParameters.put("siteName", ConfigurationProperties.getInstance()
				.getPropertyValue(Property.SiteName));
		reportParameters.put("additionalSiteInfo", ConfigurationProperties
				.getInstance().getPropertyValue(Property.ADDITIONAL_SITE_INFO));
		reportParameters.put(
				"usePageNumbers",
				ConfigurationProperties.getInstance().getPropertyValue(
						Property.USE_PAGE_NUMBERS_ON_REPORTS));
		reportParameters.put("localization", createLocalizationMap());
		reportParameters.put("leftHeaderImage", imageUrl);

		// nhuql.gv ADD
		reportParameters.put("rightHeaderImage", rightImageUrl);
		reportParameters.put("leftFooter", leftFooter);
		reportParameters.put("procedure", procedueReport);
		reportParameters.put("organizationName", organizationName);
		reportParameters.put("rightHeader", rightHeader);
		// END nhuql.gv ADD
		reportParameters.put("REPORT_LOCALE", SystemConfiguration.getInstance()
				.getDefaultLocale());
		reportParameters.put("month", month);
		reportParameters.put("year", year);
		reportParameters.put("startAccessionNumber", startAccessionNumber);
		reportParameters.put("endAccessionNumber", endAccessionNumber);
		reportParameters.put("startReceivedDate", startReceivedDate);
		reportParameters.put("endReceivedDate", endReceivedDate);
        reportParameters.put("projectDengue" , projectDengue);
        //new add
        reportParameters.put("isLogable" , isLogable);
        reportParameters.put("startIllnessDate", startIllnessDate);
        reportParameters.put("endIllnessDate", endIllnessDate);
        reportParameters.put("startResultDate", startResultDate);
        reportParameters.put("endResultDate", endResultDate);
        reportParameters.put("testID", testID);
        reportParameters.put("organizationID", organizationID);
        //end
        //For Basic Reports
        reportParameters.put("accession_number", startAccessionNumber);
        reportParameters.put("accession_number_1", endAccessionNumber);
        reportParameters.put("Start_Received_Date", DateUtil.convertStringDateToSqlDate(startReceivedDate));
        reportParameters.put("End_Received_Date", DateUtil.convertStringDateToSqlDate(endReceivedDate));
        reportParameters.put("normal_emergency", emergency);
        reportParameters.put("doctor", doctor);
        reportParameters.put("password", password);
	}

	@Override
	public void setImageUrl(String url) {
		imageUrl = url;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.mn.state.health.lims.reports.action.implementation.IReportCreator#
	 * setRightImageUrl(java.lang.String)
	 */
	@Override
	public void setRightImageUrl(String url) {
		rightImageUrl = url;
	}

	// private Object getImage(String siteName){
	// SiteInformation siteInformation =
	// siteInformationDAO.getSiteInformationByName( siteName );
	// return GenericValidator.isBlankOrNull( siteInformation.getValue() ) ?
	// null: imageDAO.retrieveImageInputStream( siteInformation.getValue() );
	// }

	protected Map<String, String> createLocalizationMap() {
		HashMap<String, String> localizationMap = new HashMap<String, String>();
		localizationMap.put("requestOrderNumber",
				StringUtil.getMessageForKey("report.requestOrderNumber"));
		localizationMap.put("confirmationOrderNumber",
				StringUtil.getMessageForKey("report.confirmationOrderNumber"));
		localizationMap.put("sampleType",
				StringUtil.getMessageForKey("report.sampleType"));
		localizationMap.put("reception",
				StringUtil.getMessageForKey("report.reception"));
		localizationMap.put("initialResults",
				StringUtil.getMessageForKey("report.initialResults"));
		localizationMap.put("confirmationResults",
				StringUtil.getMessageForKey("report.confirmationResult"));
		localizationMap.put("requesterContact",
				StringUtil.getMessageForKey("report.requesterContact"));
		localizationMap.put("telephoneAbv",
				StringUtil.getMessageForKey("report.telephoneAbv"));
		localizationMap.put("completionDate",
				StringUtil.getMessageForKey("report.completionDate"));
		localizationMap.put("site", StringUtil.getMessageForKey("report.site"));
		localizationMap.put("fax", StringUtil.getMessageForKey("report.fax"));
		localizationMap.put("email",
				StringUtil.getMessageForKey("report.email"));
		localizationMap.put("test", StringUtil.getMessageForKey("report.test"));
		localizationMap.put("result",
				StringUtil.getMessageForKey("report.result"));
		localizationMap.put("note", StringUtil.getMessageForKey("report.note"));
		localizationMap.put("pageNumberOf",
				StringUtil.getMessageForKey("report.pageNumberOf"));
		localizationMap.put("labManager",
				StringUtil.getMessageForKey("report.labManager"));
		localizationMap.put("collectionDate",
				StringUtil.getMessageForKey("report.collectionDate"));
		/* For patient report CDI */
		localizationMap.put("patientCode",
				StringUtil.getMessageForKey("report.patientCode"));
		localizationMap.put("prescriber",
				StringUtil.getMessageForKey("report.prescriber"));
		localizationMap.put("sex", StringUtil.getMessageForKey("report.sex"));
		localizationMap.put("districtFacility",
				StringUtil.getMessageForKey("report.districtFacility"));
		localizationMap.put("regionFacility",
				StringUtil.getMessageForKey("report.regionFacility"));
		localizationMap.put("referringSite",
				StringUtil.getMessageForKey("report.referringSite"));
		localizationMap.put("ordinanceNo",
				StringUtil.getMessageForKey("report.ordinanceNo"));
		localizationMap.put("orderDate",
				StringUtil.getMessageForKey("report.orderDate"));
		localizationMap.put("receiptDate",
				StringUtil.getMessageForKey("report.receiptDate"));
		localizationMap.put("specimenAndNo",
				StringUtil.getMessageForKey("report.specimenAndNo"));
		localizationMap.put("collectionDate",
				StringUtil.getMessageForKey("report.collectionDate"));
		localizationMap.put("outcome",
				StringUtil.getMessageForKey("report.outcome"));
		localizationMap.put("referenceValue",
				StringUtil.getMessageForKey("report.referenceValue"));
		localizationMap.put("unit", StringUtil.getMessageForKey("report.unit"));
		localizationMap.put("labInfomation",
				StringUtil.getMessageForKey("report.labInfomation"));
		localizationMap.put("belowNormal",
				StringUtil.getMessageForKey("report.belowNormal"));
		localizationMap.put("thanNormal",
				StringUtil.getMessageForKey("report.thanNormal"));
		localizationMap.put("normal",
				StringUtil.getMessageForKey("report.normal"));
		localizationMap.put("extLabReference",
				StringUtil.getMessageForKey("report.extLabReference"));
		localizationMap.put("confirmTest",
				StringUtil.getMessageForKey("report.confirmTest"));
		localizationMap.put("serviceHead",
				StringUtil.getMessageForKey("report.serviceHead"));
		localizationMap.put("associateProfessor",
				StringUtil.getMessageForKey("report.associateProfessor"));
		localizationMap.put("assHeadOfBioclinicque",
				StringUtil.getMessageForKey("report.assHeadOfBioclinicque"));
		localizationMap.put("reportDate",
				StringUtil.getMessageForKey("report.reportDate"));
		localizationMap.put("about",
				StringUtil.getMessageForKey("report.about"));
		localizationMap.put("age", StringUtil.getMessageForKey("report.age"));
		localizationMap.put("idNational",
				StringUtil.getMessageForKey("report.idNational"));
		localizationMap.put("program",
				StringUtil.getMessageForKey("report.program"));
		localizationMap.put("status",
				StringUtil.getMessageForKey("report.status"));
		localizationMap.put("alert",
				StringUtil.getMessageForKey("report.alert"));
		localizationMap.put("correctedReport",
				StringUtil.getMessageForKey("report.correctedReport"));
		localizationMap.put("signValidation",
				StringUtil.getMessageForKey("report.signValidation"));
		localizationMap.put("date", StringUtil.getMessageForKey("report.date"));
		localizationMap.put("legend",
				StringUtil.getMessageForKey("report.legend"));
		localizationMap.put("analysisReport",
				StringUtil.getMessageForKey("report.analysisReport"));
		localizationMap.put("results",
				StringUtil.getMessageForKey("report.results"));
		/* HIV summary */
		localizationMap.put("total",
				StringUtil.getMessageForKey("report.total"));
		localizationMap.put("children",
				StringUtil.getMessageForKey("report.children"));
		localizationMap.put("women",
				StringUtil.getMessageForKey("report.women"));
		localizationMap.put("men", StringUtil.getMessageForKey("report.men"));
		localizationMap.put("population",
				StringUtil.getMessageForKey("report.population"));
		localizationMap.put("account",
				StringUtil.getMessageForKey("report.total"));
		localizationMap.put("accounTestsByAgeAndSex",
				StringUtil.getMessageForKey("report.accounTestsByAgeAndSex"));
		localizationMap.put("positive",
				StringUtil.getMessageForKey("report.positive"));
		localizationMap.put("accountHivTypeTest",
				StringUtil.getMessageForKey("report.accountHivTypeTest"));
		localizationMap.put("negative",
				StringUtil.getMessageForKey("report.negative"));
		localizationMap.put("undetermined",
				StringUtil.getMessageForKey("report.undetermined"));
		localizationMap.put("percentage",
				StringUtil.getMessageForKey("report.percentage"));
		localizationMap.put("waiting",
				StringUtil.getMessageForKey("report.percentage"));
		/* Summary of all Tests */
		localizationMap.put("globalLabReport",
				StringUtil.getMessageForKey("report.globalLabReport"));
		localizationMap.put("notStarted",
				StringUtil.getMessageForKey("report.notStarted"));
		localizationMap.put("inProgress",
				StringUtil.getMessageForKey("report.inProgress"));
		localizationMap.put("complete",
				StringUtil.getMessageForKey("report.complete"));
		localizationMap.put("footNote",
				StringUtil.getMessageForKey("report.footNote"));
		localizationMap.put("labTotal",
				StringUtil.getMessageForKey("report.labTotal"));
		/* Referred Test reports */
		localizationMap.put("orderNo",
				StringUtil.getMessageForKey("report.orderNo"));
		localizationMap.put("referredTest",
				StringUtil.getMessageForKey("report.referredTest"));
		localizationMap.put("referredResult",
				StringUtil.getMessageForKey("report.referredResult"));
		localizationMap.put("other",
				StringUtil.getMessageForKey("report.other"));
		localizationMap.put("report",
				StringUtil.getMessageForKey("report.report"));
		localizationMap.put("reason",
				StringUtil.getMessageForKey("report.reason"));
		localizationMap.put("reception",
				StringUtil.getMessageForKey("report.reception"));
		/* activity report */
		localizationMap.put("activity",
				StringUtil.getMessageForKey("report.activity"));
		localizationMap.put("from", StringUtil.getMessageForKey("report.from"));
		localizationMap.put("to", StringUtil.getMessageForKey("report.to"));
		localizationMap.put("printed",
				StringUtil.getMessageForKey("report.printed"));
		localizationMap.put("techId",
				StringUtil.getMessageForKey("report.techId"));
		localizationMap.put("collection",
				StringUtil.getMessageForKey("report.collection"));
		localizationMap.put("patientNameCode",
				StringUtil.getMessageForKey("report.patientNameCode"));
		localizationMap.put("status",
				StringUtil.getMessageForKey("report.status"));
		localizationMap.put("testName",
				StringUtil.getMessageForKey("report.testName"));
		localizationMap.put("dateFormat",
				StringUtil.getMessageForKey("report.dateFormat"));
		localizationMap.put("dateReviewedReceived",
				StringUtil.getMessageForKey("report.dateReviewedReceived"));
		/* Non Conformity by group/date */
		localizationMap.put("supervisorSign",
				StringUtil.getMessageForKey("report.supervisorSign"));
		localizationMap.put("for", StringUtil.getMessageForKey("report.for"));
		localizationMap.put("comments",
				StringUtil.getMessageForKey("report.comments"));
		localizationMap.put("biologist",
				StringUtil.getMessageForKey("report.biologist"));
		localizationMap.put("typeOfSample",
				StringUtil.getMessageForKey("report.typeOfSample"));
		localizationMap.put("reasonForRejection",
				StringUtil.getMessageForKey("report.reasonForRejection"));
		localizationMap.put("section",
				StringUtil.getMessageForKey("report.section"));
		localizationMap.put("service",
				StringUtil.getMessageForKey("report.service"));
		localizationMap.put("study",
				StringUtil.getMessageForKey("report.study"));
		localizationMap.put("siteSubjectNo",
				StringUtil.getMessageForKey("report.siteSubjectNo"));
		localizationMap.put("subjectNo",
				StringUtil.getMessageForKey("report.subjectNo"));
		/* Validation Report */
		localizationMap.put("validationReport",
				StringUtil.getMessageForKey("report.validationReport"));
		localizationMap.put("testSection",
				StringUtil.getMessageForKey("report.testSection"));
		localizationMap.put("labManager",
				StringUtil.getMessageForKey("report.labManager"));
		localizationMap.put("appointmentDate",
				StringUtil.getMessageForKey("report.appointmentDate"));
		/* No Report report */
		localizationMap.put("noReportMessage",
				StringUtil.getMessageForKey("report.noReportMessage"));

		return localizationMap;
	}

	@Override
	public byte[] runReport() throws Exception {
		System.out.println("------------- : " + fullReportFilename);
		if (isUseDataSource) {
			return JasperRunManager.runReportToPdf(fullReportFilename,
					getReportParameters(), getReportDataSource());
		} else {
			PostgreSQLConnector postgreSQLConnector = new PostgreSQLConnector();
			Connection con = postgreSQLConnector.getConnection();
			return JasperRunManager.runReportToPdf(fullReportFilename,
					getReportParameters(), con);
		}
	}

	@Override
	public byte[] runReportForExcel() throws Exception {
		if (isUseDataSource) {
			JasperPrint jasperPrint = JasperManager.fillReport(
					fullReportFilename, getReportParameters(),
					getReportDataSource());
			JExcelApiExporter exporter = new JExcelApiExporter();
			ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRXlsExporterParameter.IS_AUTO_DETECT_CELL_TYPE, Boolean.FALSE);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsReport);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
					"sample.xls");
			exporter.exportReport();
			byte bytes[] = xlsReport.toByteArray();
			return bytes;
		} else {
			PostgreSQLConnector postgreSQLConnector = new PostgreSQLConnector();
			Connection con = postgreSQLConnector.getConnection();
			JasperPrint jasperPrint = JasperManager.fillReport(
					fullReportFilename, getReportParameters(), con);
			JExcelApiExporter exporter = new JExcelApiExporter();
			ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsReport);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
					"sample.xls");
			exporter.exportReport();
			byte bytes[] = xlsReport.toByteArray();
			return bytes;
		}
	}

	public abstract JRDataSource getReportDataSource()
			throws IllegalStateException;

	public HashMap<String, ?> getReportParameters()
			throws IllegalStateException {
		if (!initialized) {
			throw new IllegalStateException("initializeReport not called first");
		}
		return reportParameters != null ? reportParameters
				: new HashMap<String, Object>();
	}

	/**
	 * Utility routine for a sequence done in many places. Adds a message to the
	 * errorMsgs
	 * 
	 * @param messageId
	 *            - name of resource
	 */
	protected void add1LineErrorMessage(String messageId) {
		errorFound = true;
		ErrorMessages msgs = new ErrorMessages();
		msgs.setMsgLine1(StringUtil.getMessageForKey(messageId));
		errorMsgs.add(msgs);
	}

	/**
	 * Utility routine for a sequence done in many places. Adds a message to the
	 * errorMsgs
	 * 
	 * @param messageId
	 *            - name of resource
	 */
	protected void add1LineErrorMessage(String messageId, String more) {
		errorFound = true;
		ErrorMessages msgs = new ErrorMessages();
		msgs.setMsgLine1(StringUtil.getMessageForKey(messageId) + more);
		errorMsgs.add(msgs);
	}

	/**
	 * Checks a given date to make sure it is ok, filling in with a default if
	 * not found, logging a message, if there is a problem.
	 * 
	 * @param checkDateStr
	 *            - date to check
	 * @param defaultDateStr
	 *            - will use this date if the 1st one is null or blank.
	 * @param badDateMessage
	 *            - message to report if the date is bad (blank or not valid
	 *            form).
	 * @return Date
	 */
	protected Date validateDate(String checkDateStr, String defaultDateStr,
			String badDateMessage) {
		checkDateStr = isBlankOrNull(checkDateStr) ? defaultDateStr
				: checkDateStr;
		Date checkDate;
		if (isBlankOrNull(checkDateStr)) {
			add1LineErrorMessage(badDateMessage);
			return null;
		}

		try {
			checkDate = DateUtil.convertStringDateToSqlDate(checkDateStr);
		} catch (LIMSRuntimeException re) {
			add1LineErrorMessage("report.error.message.date.format", " "
					+ checkDateStr);
			return null;
		}
		return checkDate;
	}

	/**
	 * @return true, if location is not blank or "0" is is found in the DB;
	 *         false otherwise
	 */
	protected Organization getValidOrganization(String locationStr) {
		if (isBlankOrNull(locationStr) || "0".equals(locationStr)) {
			add1LineErrorMessage("report.error.message.location.missing");
			return null;
		}
		OrganizationDAO dao = new OrganizationDAOImpl();
		Organization org = dao.getOrganizationById(locationStr);
		if (org == null) {
			add1LineErrorMessage("report.error.message.location.missing");
			return null;
		}
		return org;
	}

	public String getReportFileName() {
		return errorFound ? ERROR_REPORT : reportFileName();
	}

	public class DateRange {
		private String lowDateStr;
		private String highDateStr;
		private Date lowDate;
		private Date highDate;

		public Date getLowDate() {
			return lowDate;
		}

		public Date getHighDate() {
			return highDate;
		}

		/**
		 * If you need to compare a Date which started as a date string to a
		 * bunch of timestamps, you should move it from 00:00 at the beginning
		 * of the day to the end of the day at 23:59:59.999.
		 * 
		 * @return the high date with time set to the end of the day.
		 */
		public Date getHighDateAtEndOfDay() {
			// not perfect in areas with Daylight Savings Time. Will over shoot
			// on the spring forward day and undershoot on the fall back day.
			return new Date(highDate.getTime() + 24 * 60 * 60 * 1000);
		}

		public DateRange(String lowDateStr, String highDateStr) {
			this.lowDateStr = lowDateStr;
			this.highDateStr = highDateStr;
		}

		/**
		 * <ol>
		 * <li>High date picks up low date if it ain't filled in,
		 * <li>they can't both be empty
		 * <li>they have to be well formed.
		 * 
		 * @return true if valid, false otherwise
		 */
		public boolean validateHighLowDate(String missingDateMessage) {
			lowDate = validateDate(lowDateStr, null, missingDateMessage);
			highDate = validateDate(highDateStr, lowDateStr, missingDateMessage);
			if (lowDate == null || highDate == null) {
				return false;
			}
			if (highDate.getTime() < lowDate.getTime()) {
				Date tmpDate = highDate;
				highDate = lowDate;
				lowDate = tmpDate;

				String tmpString = highDateStr;
				highDateStr = lowDateStr;
				lowDateStr = tmpString;
			}
			return true;
		}

		public String toString() {
			String range = lowDateStr;
			try {
				if (!GenericValidator.isBlankOrNull(highDateStr)) {
					range += "  -  " + highDateStr;
				}
			} catch (Exception ignored) {
			}
			return range;
		}

		public String getLowDateStr() {
			return lowDateStr;
		}

		public String getHighDateStr() {
			if (isBlankOrNull(highDateStr) && highDate != null) {
				highDateStr = DateUtil.convertSqlDateToStringDate(highDate);
			}
			return highDateStr;
		}
	}

	public void setReportPath(String path) {
		fullReportFilename = path + getReportFileName() + ".jasper";
	}

	/**
	 * nhuql.gv set value value of procedure in report
	 * 
	 * @param inValue
	 */
	public void setReportProcedure(String inProcedure) {
		procedueReport = inProcedure;
	}

	/**
	 * nhuql.gv set value for organization name in report
	 * 
	 * @param inValue
	 */
	public void setOrgazitionName(String inValue) {
		organizationName = inValue;
	}

	/**
	 * nhuql.gv set value for footer in report
	 * 
	 * @param inValue
	 */
	public void setLeftFooter(String inValue) {
		leftFooter = inValue;
	}

	/**
	 * nhuql.gv set value for right of header in report
	 * 
	 * @param inValue
	 */
	public void setRightHeader(String inValue) {
		rightHeader = inValue;
	}

	public List<String> getReportedOrders() {
		return new ArrayList<String>();
	}

	protected abstract String reportFileName();

	public String getReportName() {
		return reportFileName();
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
