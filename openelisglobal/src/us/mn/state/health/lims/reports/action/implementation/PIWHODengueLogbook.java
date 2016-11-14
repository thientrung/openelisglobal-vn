/**
 * 
 */
package us.mn.state.health.lims.reports.action.implementation;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;
import vi.mn.state.health.lims.report.dao.ReportTestResultDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportTestResultDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportModelTestResult;

/**
 * Report for page DENGUE page list JE.
 * 
 * @author nhuql.gv/
 * 
 */
public class PIWHODengueLogbook extends Report implements IReportCreator {

	/**
	 * min value of accession_number.
	 */
	private String fromAccessionNumber = "";

	/**
	 * max value of accession_number
	 */
	private String toAccessionNumber = "";
	private String lowerIllnessDateRange = "";
	private String upperIllnessDateRange = "";
	private String lowerDateRange = "";
	private String upperDateRange = "";
	private String lowerResultDateRange = "";
	private String upperResultDateRange = "";

	/**
	 * id of test.
	 */
	private String testId = "0";

	/**
	 * list data for export in file.
	 */
	private List<ReportModelTestResult> listReportData;

	/**
	 * Instance of DAO.
	 */
	private ReportTestResultDAO reportTestResultDAO = new ReportTestResultDAOImpl();

	/**
	 * value for procedure of test.
	 */
	private String procedureOfTest;

	/**
	 * value of report from browser.
	 */
	private String paramMenu = "";

	/**
	 * Constructor of class.
	 * 
	 * @param inParamMenu
	 */
	public PIWHODengueLogbook(String inParamMenu) {
		this.paramMenu = inParamMenu;
		this.isUseDataSource = false;
	}

	/**
	 * init parameters get values for report.
	 */
	@Override
	public void initializeReport(final BaseActionForm dynaForm) {
		super.initializeReport();
		try {
			fromAccessionNumber = dynaForm.getString("accessionDirect");
			toAccessionNumber = dynaForm.getString("highAccessionDirect");
			lowerIllnessDateRange = dynaForm.getString("lowerIllnessDateRange");
			upperIllnessDateRange = dynaForm.getString("upperIllnessDateRange");
			lowerDateRange = dynaForm.getString("lowerDateRange");
			upperDateRange = dynaForm.getString("upperDateRange");
			lowerResultDateRange = dynaForm.getString("lowerResultDateRange");
			upperResultDateRange = dynaForm.getString("upperResultDateRange");
			// Dung add
			// get list test went submit report

			/*
			 * if (dynaForm.get("selectList") != null) { ReportSpecificationList
			 * selection = (ReportSpecificationList) dynaForm
			 * .get("selectList"); testId = selection.getSelection();
			 * getProcedureFromConfig(); } else { testId =
			 * SystemConfiguration.getInstance().getTestOfJE();
			 * setReportProcedure(SystemConfiguration.getInstance()
			 * .getProcedureOfJE()); }
			 */
			setLeftFooter(SystemConfiguration.getInstance().getLeftFooter());
			createReportParameters();
		} catch (Exception e) {
			e.printStackTrace();
			add1LineErrorMessage("report.error.message.noPrintableItems");
		}
	}

	/**
	 * Check the parameters are empty.
	 * 
	 * @return
	 */
	private boolean validateSubmitParameters() {
		if (GenericValidator.isBlankOrNull(fromAccessionNumber)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Get value of procedure by testId.
	 */
	private void getProcedureFromConfig() {

		String testIds = SystemConfiguration.getInstance().getReportTestIds();
		if (testIds != null && testIds.indexOf(",") > -1) {
			String[] arrTmp = testIds.split(",");
			int index = -1;

			if (arrTmp != null) {
				for (int i = 0; i < arrTmp.length; i++) {
					if (arrTmp[i].equals(testId)) {
						index = i;
						break;
					}
				}
			}

			// Get procedure by testId
			String procedures = SystemConfiguration.getInstance()
					.getReportProcedures();
			if (procedures != null && procedures.indexOf(",") > -1) {
				String[] arr = procedures.split(",");
				if (index > -1) {
					procedureOfTest = arr[index];
					setReportProcedure("(" + procedureOfTest + ")");
				}
			}

			// Get header by testid
			String rightHeaders = SystemConfiguration.getInstance()
					.getRightHeaders();
			if (rightHeaders != null && rightHeaders.indexOf(",") > -1) {
				String[] arrHeader = rightHeaders.split(",");
				if (index > -1 && arrHeader.length > index) {
					String header = arrHeader[index];
					setRightHeader(header);
				}
			}

		}
	}

	/**
	 * Convert data to data source for Jasper Report.
	 */
	@Override
	public JRDataSource getReportDataSource() throws IllegalStateException {
		JRDataSource jrds = null;
		if (errorFound) {
			jrds = new JRBeanCollectionDataSource(errorMsgs);
		} else {
			jrds = new JRBeanCollectionDataSource(listReportData);
		}
		return jrds;
	}

	/**
	 * Return file name for PDF file or Excel file.
	 */
	@Override
	protected String reportFileName() {
		if (ReportFileNameAndParams.Param_PI_WHO_Dengue_logbook
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Param_PI_WHO_Dengue_logbook;
		}
		return "";
	}

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
	}

	@Override
	protected void createReportParameters() {
		super.createReportParameters();
		reportParameters.put("fromAccessionNumber",
				fromAccessionNumber.equals("") ? null : fromAccessionNumber);
		reportParameters.put("toAccessionNumber",
				toAccessionNumber.equals("") ? null : toAccessionNumber);
		if (lowerIllnessDateRange.equals(""))
			reportParameters
					.put("lowerIllnessDateRange",
							lowerIllnessDateRange.equals("") ? null
									: converTimestapToString(DateUtil
											.convertStringDateToTimestamp(lowerIllnessDateRange
													+ " 00:00")));
		reportParameters
				.put("upperIllnessDateRange",
						upperIllnessDateRange.equals("") ? null
								: converTimestapToString(DateUtil
										.convertStringDateToTimestamp(upperIllnessDateRange
												+ " 00:00")));
		reportParameters.put(
				"lowerDateRange",
				lowerDateRange.equals("") ? null
						: converTimestapToString(DateUtil
								.convertStringDateToTimestamp(lowerDateRange
										+ " 00:00")));
		reportParameters.put(
				"upperDateRange",
				upperDateRange.equals("") ? null
						: converTimestapToString(DateUtil
								.convertStringDateToTimestamp(upperDateRange
										+ " 00:00")));
		reportParameters
				.put("lowerResultDateRange",
						lowerResultDateRange.equals("") ? null
								: converTimestapToString(DateUtil
										.convertStringDateToTimestamp(lowerResultDateRange
												+ " 00:00")));
		reportParameters
				.put("upperResultDateRange",
						upperResultDateRange.equals("") ? null
								: converTimestapToString(DateUtil
										.convertStringDateToTimestamp(upperResultDateRange
												+ " 00:00")));
	}

	private String converTimestapToString(Timestamp timestamp) {
		return DateUtil.convertTimestampToStringDatePg(timestamp);
	}

}
