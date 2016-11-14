/**
 * 
 */
package vi.mn.state.health.lims.report.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.reports.action.implementation.IReportCreator;
import us.mn.state.health.lims.reports.action.implementation.Report;
import us.mn.state.health.lims.reports.action.implementation.ReportSpecificationList;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;
import vi.mn.state.health.lims.report.dao.ReportTestResultDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportTestResultDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportModelTestResult;

/**
 * Report for page DENGUE page list JE.
 * 
 * @author nhuql.gv
 * 
 */
public class ReportTestResult extends Report implements IReportCreator {

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
	private String testId = "";

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
	public ReportTestResult(String inParamMenu) {
		this.paramMenu = inParamMenu;
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

			if (dynaForm.get("selectList") != null) {
				ReportSpecificationList selection = (ReportSpecificationList) dynaForm
						.get("selectList");
				testId = selection.getSelection();
				getProcedureFromConfig();
			} else {
				testId = SystemConfiguration.getInstance().getTestOfJE();
				setReportProcedure(SystemConfiguration.getInstance()
						.getProcedureOfJE());
			}

			//add
		     startAccessionNumber = fromAccessionNumber;
	         endAccessionNumber = toAccessionNumber;
	         startReceivedDate = lowerDateRange;
	         endReceivedDate = upperDateRange;
	         startIllnessDate= lowerIllnessDateRange;
	         endIllnessDate = upperIllnessDateRange;
	         startResultDate = lowerResultDateRange;
	         endResultDate = upperResultDateRange;
	         testID = testId;
	         //isLogable ="true": this report will be saved to history log when printing; isLogable ="false": this report will not be saved to history log
	         isLogable = "true";
	         //end
			setLeftFooter(SystemConfiguration.getInstance().getLeftFooter());
			createReportParameters();
			// comment because. Don't need to validate accession numbers
			/*
			 * errorFound = !validateSubmitParameters(); if (errorFound) {
			 * return; }
			 */
			listReportData = new ArrayList<ReportModelTestResult>();
			listReportData = reportTestResultDAO.getListData(
					fromAccessionNumber, toAccessionNumber,
					testId.equals("")?null :Integer.parseInt(testId), lowerIllnessDateRange,
					upperIllnessDateRange, lowerDateRange, upperDateRange,
					lowerResultDateRange, upperResultDateRange);

			if (this.listReportData.size() == 0) {
				add1LineErrorMessage("report.error.message.notfounddata");
			}
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
		if (ReportFileNameAndParams.Param_PatientHaitiClinicalJE
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_PI_Patient_Report_JE;
		} else if (ReportFileNameAndParams.Param_PatientResultTestAntigenIgm
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_Test_Result;
		}
		return "";
	}

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
		try {
			fromAccessionNumber = hashmap.get("accessionDirect");
			toAccessionNumber = hashmap.get("highAccessionDirect");
			testId = hashmap.get("testId");

			if (testId.equalsIgnoreCase("0")) {
				testId = SystemConfiguration.getInstance().getTestOfJE();
				setReportProcedure(SystemConfiguration.getInstance()
						.getProcedureOfJE());
			} else {
				getProcedureFromConfig();
			}

			setLeftFooter(SystemConfiguration.getInstance().getLeftFooter());
			createReportParameters();
			errorFound = !validateSubmitParameters();
			if (errorFound) {
				return;
			}
			listReportData = new ArrayList<ReportModelTestResult>();
			listReportData = reportTestResultDAO.getListDataWS(
					fromAccessionNumber, toAccessionNumber,
					Integer.parseInt(testId));

			if (this.listReportData.size() == 0) {
				add1LineErrorMessage("report.error.message.notfounddata");
			}
		} catch (Exception e) {
			e.printStackTrace();
			add1LineErrorMessage("report.error.message.noPrintableItems");
		}
	}

}
