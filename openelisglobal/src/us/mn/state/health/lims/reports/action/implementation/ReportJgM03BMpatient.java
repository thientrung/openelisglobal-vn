package us.mn.state.health.lims.reports.action.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;
import vi.mn.state.health.lims.report.dao.JgM03BMPatientDAO;
import vi.mn.state.health.lims.report.daoimpl.JgM03BMPatientDAOImpl;
import vi.mn.state.health.lims.report.valueholder.JgM03_BM_Patient;

/**
 * report for patient.
 * 
 * @author dungtdo.sl
 */
public class ReportJgM03BMpatient extends Report implements IReportCreator {

	/**
	 * min value of accession_number.
	 */
	private String accessionDirect;

	/**
	 * max value of accession_number
	 */
	private String highAccessionDirect;

	private String lowerIllnessDateRange = "";
	private String upperIllnessDateRange = "";
	private String lowerDateRange = "";
	private String upperDateRange = "";
	private String lowerResultDateRange = "";
	private String upperResultDateRange = "";

	/**
	 * Get range between two dates.
	 */
	private DateRange dateRange;

	/**
	 * list data for export in file.
	 */
	private List<JgM03_BM_Patient> listReportData;

	/**
	 * Instance of JgM03BMPatientDAO.
	 */
	private JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();

	/**
	 * organization name string.
	 */
	private String organizationId = "";

	/**
	 * id of test.
	 */
	private String testId = "0";

	/**
	 * proceduce of test.
	 */
	private String procedureOfTest;

	/**
	 * param from browser.
	 */
	private String paramMenu;

	/**
	 * Constructor of class.
	 * 
	 * @param inParamMenu
	 */
	public ReportJgM03BMpatient(String inParamMenu) {
		this.paramMenu = inParamMenu;
	}

	/**
	 * init parameters get values for report.
	 */
	@Override
	public void initializeReport(final BaseActionForm dynaForm) {
		super.initializeReport();
		try {
			lowerIllnessDateRange = dynaForm.getString("lowerIllnessDateRange");
			upperIllnessDateRange = dynaForm.getString("upperIllnessDateRange");
			lowerDateRange = dynaForm.getString("lowerDateRange");
			upperDateRange = dynaForm.getString("upperDateRange");
			lowerResultDateRange = dynaForm.getString("lowerResultDateRange");
			upperResultDateRange = dynaForm.getString("upperResultDateRange");
			accessionDirect = dynaForm.getString("accessionDirect");
			highAccessionDirect = dynaForm.getString("highAccessionDirect");
			dateRange = new DateRange(lowerDateRange, upperDateRange);
			if (dynaForm.get("selectListName") != null) {
			    ReportSpecificationListName selection = (ReportSpecificationListName) dynaForm
                        .get("selectListName");
			    organizationId = selection.getSelection() != null ? selection
                        .getSelection() : "";
            }
			if (dynaForm.get("selectList") != null) {
				ReportSpecificationList selection = (ReportSpecificationList) dynaForm
						.get("selectList");
				testId = selection.getSelection() != null ? selection
						.getSelection() : "0";
			} else {
				testId = SystemConfiguration.getInstance().getTestOfJE();
				setReportProcedure(SystemConfiguration.getInstance()
						.getProcedureOfJE());
			}

			if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR
					.equals(paramMenu)) {
				testId = SystemConfiguration.getInstance().getTestIdOfPCR();
				setReportProcedure(SystemConfiguration.getInstance()
						.getProcedureOfPCR());
			} else if (ReportFileNameAndParams.Param_PatientDeterminePLVR
					.equals(paramMenu)) {
				if (dynaForm.get("testIsolationList") != null) {
					ParameterTestIsolationList testIsolationList = (ParameterTestIsolationList) dynaForm
							.get("testIsolationList");
					testId = testIsolationList.getSelection() != null ? testIsolationList
							.getSelection() : "0";
				}
				setReportProcedure(SystemConfiguration.getInstance()
						.getProcedureOfPLVR());
			} else if (ReportFileNameAndParams.Param_ZikaPCR.equals(paramMenu)) {
				//testId = SystemConfiguration.getInstance().getZikaPRCTestId();
				if (dynaForm.get("testPCRList") != null) {
                    ParameterTestPCRList testPCRList = (ParameterTestPCRList) dynaForm
                            .get("testPCRList");
                    testId = testPCRList.getSelection() != null ? testPCRList.getSelection() : "0";
                }
			}
			
			//add: set parameter for parameter map
            startAccessionNumber = accessionDirect;
            endAccessionNumber = highAccessionDirect;
            startReceivedDate = lowerDateRange;
            endReceivedDate = upperDateRange;
            startIllnessDate= lowerIllnessDateRange;
            endIllnessDate = upperIllnessDateRange;
            startResultDate = lowerResultDateRange;
            endResultDate = upperResultDateRange;
            testID = testId;
            organizationID = organizationId;
            //isLogable ="true": this report will be saved to history log when printing; isLogable ="false": this report will not be saved to history log
            isLogable = "true";
            //end

			// END nhuql.gv ADD

			getProcedureFromConfig();
			createReportParameters();
/*			errorFound = !validateSubmitParameters();
			if (errorFound) {
				return;
			}*/
			listReportData = new ArrayList<JgM03_BM_Patient>();
			// listReportData =
			// jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr,
			// highDateStr);
			listReportData = jgM03BMPatientDAO.getListData(accessionDirect,
					highAccessionDirect,
					!testId.equals("") ? Integer.parseInt(testId) : 0,
					lowerIllnessDateRange, upperIllnessDateRange,
					lowerDateRange, upperDateRange, lowerResultDateRange,
					upperResultDateRange,organizationId);
			
			if (this.listReportData.size() == 0) {
				add1LineErrorMessage("report.error.message.noPrintableItems");
				
			} else {
				int listReportSize = listReportData.size();
				List<String> projectList = new ArrayList<String>();
				StringBuilder projectNameStr = new StringBuilder();
				String projectName = "";
				String currentOrgName = "";
				String newOrgName = "";
				
				for (int i = 0; i < listReportSize; i++) {
					newOrgName = String.valueOf(listReportData.get(i).getOrganizationName());
					// Initial state (assign newOrgName to currentOrgName)
					if (currentOrgName.equals("")) {
						currentOrgName = newOrgName;
					}
					// Check if new organization group
					if (!newOrgName.equals(currentOrgName)) {
						projectName = projectNameStr.length() > 0 ? projectNameStr.substring(0, projectNameStr.length() - 2) : "";
						// Set the project name of the last project in the organization group
						listReportData.get(i-1).setProjectName(projectName);
						// Clear the project list
						projectList = new ArrayList<String>();
						// Clear or empty the StringBuilder
						projectNameStr.setLength(0); // set length of buffer to 0
						projectNameStr.trimToSize(); // trim the underlying buffer
						currentOrgName = newOrgName;
					}
					
					if (listReportData.get(i).getProjectName() != null) {
						if (projectList.size() > 0) {
							boolean projectDuplicated = false;
							// Check if project already exist in the list
							for (String project : projectList) {
								if ( project.equals(listReportData.get(i).getProjectName()) ) {
									projectDuplicated = true;
									break;
								}
							}
							if (!projectDuplicated) {
								projectList.add(listReportData.get(i).getProjectName());
								projectNameStr.append(listReportData.get(i).getProjectName());
								projectNameStr.append(", ");
							}
						} else {
							// Initial addition of project to the list
							projectList.add(listReportData.get(i).getProjectName());
							projectNameStr.append(listReportData.get(i).getProjectName());
							projectNameStr.append(", ");
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkNumberAndDate(String lowDateStr2, String highDateStr2,
			String accessionDirect2, String highAccessionDirect2,
			String organizationName) {
		// 190 is testId of this report
		if (!(GenericValidator.isBlankOrNull(lowDateStr2) || GenericValidator
				.isBlankOrNull(highDateStr2))
				&& !(GenericValidator.isBlankOrNull(accessionDirect2) || GenericValidator
						.isBlankOrNull(highAccessionDirect2))) {
			listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientNumDate(
					accessionDirect2, highAccessionDirect2, lowDateStr2,
					highDateStr2, Integer.valueOf(testId), organizationName);
		} else if ((GenericValidator.isBlankOrNull(lowDateStr2) || GenericValidator
				.isBlankOrNull(highDateStr2))
				&& !GenericValidator.isBlankOrNull(accessionDirect2)
				|| !GenericValidator.isBlankOrNull(highAccessionDirect2)) {
			listReportData = jgM03BMPatientDAO.getAllJgM03BMPatient(
					accessionDirect2, highAccessionDirect2,
					Integer.valueOf(testId), organizationName);
		} else if (!GenericValidator.isBlankOrNull(lowDateStr2)
				|| !GenericValidator.isBlankOrNull(highDateStr2)
				&& (GenericValidator.isBlankOrNull(accessionDirect2) || GenericValidator
						.isBlankOrNull(highAccessionDirect2))) {
			listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientDate(
					lowDateStr2, highDateStr2, Integer.valueOf(testId),
					organizationName);
		}
	}

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

	private boolean validateSubmitParameters() {
		if ((GenericValidator.isBlankOrNull(lowerDateRange) && GenericValidator
				.isBlankOrNull(upperDateRange))
				&& (GenericValidator.isBlankOrNull(accessionDirect) && GenericValidator
						.isBlankOrNull(highAccessionDirect))) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected String reportFileName() {
		if (ReportFileNameAndParams.Param_JgM03_BM_Patient_Report
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_JgM03_BM_Patient_Report;
		} else if (ReportFileNameAndParams.Param_PatientResultListJE
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_JE_Patient_Report_List;
		} else if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_PCR_Analysis_Found_Virus;
		} else if (ReportFileNameAndParams.Param_PatientDeterminePLVR
				.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_PLVR_Determine_Virus;
		} else if (ReportFileNameAndParams.Param_ZikaPCR.equals(paramMenu)) {
			return ReportFileNameAndParams.Report_ZikaPCR;
		}
		return "";
	}

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
		// lowDateStr = hashmap.get("lowerDateRange");
		// highDateStr = hashmap.get("upperDateRange");
		// dateRange = new DateRange(lowDateStr, highDateStr);
		accessionDirect = hashmap.get("accessionDirect");
		highAccessionDirect = hashmap.get("highAccessionDirect");
		testId = hashmap.get("testId");
		// organizationName = hashmap.get("organizationName");

		if (testId.equalsIgnoreCase("0")) {
			testId = SystemConfiguration.getInstance().getTestOfJE();
			setReportProcedure(SystemConfiguration.getInstance()
					.getProcedureOfJE());
		}

		if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR
				.equals(paramMenu)) {
			testId = SystemConfiguration.getInstance().getTestIdOfPCR();
			setReportProcedure(SystemConfiguration.getInstance()
					.getProcedureOfPCR());

		} else if (ReportFileNameAndParams.Param_PatientDeterminePLVR
				.equals(paramMenu)) {
			// testId = SystemConfiguration.getInstance().getTestIdOfPLVR();
			setReportProcedure(SystemConfiguration.getInstance()
					.getProcedureOfPLVR());

		} else if (ReportFileNameAndParams.Param_ZikaPCR.equals(paramMenu)) {
			testId = SystemConfiguration.getInstance().getZikaPRCTestId();
		}

		getProcedureFromConfig();
		createReportParameters();
		errorFound = !validateSubmitParameters();
		if (errorFound) {
			return;
		}
		listReportData = new ArrayList<JgM03_BM_Patient>();
		// listReportData =
		// jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr, highDateStr);
		checkNumberAndDate(lowerDateRange, upperDateRange, accessionDirect,
				highAccessionDirect, organizationName);
		if (this.listReportData.size() == 0) {
			add1LineErrorMessage("report.error.message.noPrintableItems");
		}
	}

	/**
	 * Get value of procedure by testId
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
				if (index > -1) {
					String header = arrHeader[index];
					setRightHeader(header);
				}
			}

		}
	}

}
