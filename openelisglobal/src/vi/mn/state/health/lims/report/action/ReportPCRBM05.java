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
import us.mn.state.health.lims.reports.action.implementation.ReportSpecificationListName;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;
import vi.mn.state.health.lims.report.dao.ReportPCRBM05DAO;
import vi.mn.state.health.lims.report.daoimpl.ReportPCRBM05DAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportModelPCRBM05;

/**
 * Report for list DENGUE and list JE.
 * 
 * @author nhuql.gv
 * 
 */
public class ReportPCRBM05 extends Report implements IReportCreator {

    /**
     * min value of accession_number.
     */
    private String fromAccessionNumber;

    /**
     * max value of accession_number.
     */
    private String toAccessionNumber;

    /**
     * min value of collection_date.
     */
    private String fromCollectionDate;

    /**
     * max value of collection_date.
     */
    private String toCollectionDate;

    /**
     * value from browser.
     */
    private String param = "";

    /**
     * id of test.
     */
    private String testId = "";

    /**
     * organization name string.
     */
    private String organizationName = "";

    /**
     * list data for export in file.
     */
    private List<ReportModelPCRBM05> listReportData;

    /**
     * Instance of DAO.
     */
    private ReportPCRBM05DAO reportPCRBM05DAO = new ReportPCRBM05DAOImpl();

    /**
     * Constructor of class.
     * 
     * @param inParam
     */
    public ReportPCRBM05(String inParam) {
        param = inParam;
    }

    /**
     * init parameters get values for report.
     */
    @Override
    public void initializeReport(final BaseActionForm dynaForm) {
        super.initializeReport();
        try {
            fromCollectionDate = dynaForm.getString("lowerDateRange");
            toCollectionDate = dynaForm.getString("upperDateRange");

            fromAccessionNumber = dynaForm.getString("accessionDirect");
            toAccessionNumber = dynaForm.getString("highAccessionDirect");

            if (dynaForm.get("selectListName") != null) {
                ReportSpecificationListName selectionOrganizationName = (ReportSpecificationListName) dynaForm
                        .get("selectListName");
                organizationName = selectionOrganizationName.getSelection();
                organizationName = organizationName.trim();
                setOrgazitionName(organizationName);
            }

            if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR.equals(param)) {
                testId = SystemConfiguration.getInstance().getTestIdOfPCR();
                setReportProcedure(SystemConfiguration.getInstance().getProcedureOfPCR());
            } else if (ReportFileNameAndParams.Param_PatientDeterminePLVR.equals(param)) {
                testId = SystemConfiguration.getInstance().getTestIdOfPLVR();
                setReportProcedure(SystemConfiguration.getInstance().getProcedureOfPLVR());
            }

            createReportParameters();
            errorFound = !validateSubmitParameters();
            if (errorFound) {
                return;
            }
            listReportData = new ArrayList<ReportModelPCRBM05>();
            GetDataByCondition(fromCollectionDate, toCollectionDate, fromAccessionNumber, toAccessionNumber,
                    organizationName);

            if (this.listReportData.size() == 0) {
                add1LineErrorMessage("report.error.message.notfounddata");
            }
        } catch (Exception e) {
            e.printStackTrace();
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
    }

    /**
     * Get data report by conditions.
     * 
     * @param inFromDate
     * @param inToDate
     * @param inFromAccessionNumber
     * @param inToAccessionNumber
     */
    private void GetDataByCondition(String inFromDate, String inToDate, String inFromAccessionNumber,
            String inToAccessionNumber, String organizationName) {
        if (!(GenericValidator.isBlankOrNull(inFromDate) || GenericValidator.isBlankOrNull(inToDate))
                && !(GenericValidator.isBlankOrNull(inFromAccessionNumber) || GenericValidator
                        .isBlankOrNull(inToAccessionNumber))) {
            listReportData = reportPCRBM05DAO.getListDataByNumberAndDate(inFromDate, inToDate, inFromAccessionNumber,
                    inToAccessionNumber, Integer.parseInt(testId), organizationName);
        } else if ((GenericValidator.isBlankOrNull(inFromDate) || GenericValidator.isBlankOrNull(inToDate))
                && !(GenericValidator.isBlankOrNull(inFromAccessionNumber) || GenericValidator
                        .isBlankOrNull(inToAccessionNumber))) {
            listReportData = reportPCRBM05DAO.getListDataByAccessionNumber(inFromAccessionNumber, inToAccessionNumber,
                    Integer.parseInt(testId), organizationName);
        } else if (!(GenericValidator.isBlankOrNull(inFromDate) || GenericValidator.isBlankOrNull(inToDate))
                && (GenericValidator.isBlankOrNull(inFromAccessionNumber) || GenericValidator
                        .isBlankOrNull(inToAccessionNumber))) {
            listReportData = reportPCRBM05DAO.getListDataByCollectionDate(inFromDate, inToDate,
                    Integer.parseInt(testId), organizationName);
        }
    }

    /**
     * Check the parameters are empty.
     * 
     * @return
     */
    private boolean validateSubmitParameters() {
        if ((GenericValidator.isBlankOrNull(fromCollectionDate) && GenericValidator.isBlankOrNull(toCollectionDate))
                && (GenericValidator.isBlankOrNull(fromAccessionNumber) && GenericValidator
                        .isBlankOrNull(toAccessionNumber))) {
            return false;
        } else {
            return true;
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
        if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR.equals(param)) {
            return ReportFileNameAndParams.Report_PCR_Analysis_Found_Virus;
        } else if (ReportFileNameAndParams.Param_PatientDeterminePLVR.equals(param)) {
            return ReportFileNameAndParams.Report_PLVR_Determine_Virus;
        }
        return "";
    }

    @Override
    public void initializeReport(HashMap<String, String> hashmap) {
        super.initializeReport();
        try {
            // fromCollectionDate = hashmap.get("lowerDateRange");
            // toCollectionDate = hashmap.get("upperDateRange");
            fromAccessionNumber = hashmap.get("accessionDirect");
            toAccessionNumber = hashmap.get("highAccessionDirect");
            testId = hashmap.get("testId");
            // organizationName = hashmap.get("organizationName");

            setOrgazitionName(organizationName);
            if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR.equals(param)) {
                testId = SystemConfiguration.getInstance().getTestIdOfPCR();
                setReportProcedure(SystemConfiguration.getInstance().getProcedureOfPCR());

            } else if (ReportFileNameAndParams.Param_PatientDeterminePLVR.equals(param)) {
                testId = SystemConfiguration.getInstance().getTestIdOfPLVR();
                setReportProcedure(SystemConfiguration.getInstance().getProcedureOfPLVR());
            }

            createReportParameters();
            errorFound = !validateSubmitParameters();
            if (errorFound) {
                return;
            }
            listReportData = new ArrayList<ReportModelPCRBM05>();
            GetDataByCondition(fromCollectionDate, toCollectionDate, fromAccessionNumber, toAccessionNumber,
                    organizationName);

            if (this.listReportData.size() == 0) {
                add1LineErrorMessage("report.error.message.notfounddata");
            }
        } catch (Exception e) {
            e.printStackTrace();
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
    }

}
