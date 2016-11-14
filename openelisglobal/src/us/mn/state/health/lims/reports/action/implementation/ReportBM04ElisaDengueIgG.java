/**
 * 
 */
package us.mn.state.health.lims.reports.action.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import vi.mn.state.health.lims.report.dao.JgM03BMPatientDAO;
import vi.mn.state.health.lims.report.daoimpl.JgM03BMPatientDAOImpl;
import vi.mn.state.health.lims.report.valueholder.JgM03_BM_Patient;

/**
 * Create report for DENGUE IgG
 * 
 * @author dungtdo.sl
 * 
 */
public class ReportBM04ElisaDengueIgG extends Report implements IReportCreator {
    /**
     * min value of date to search.
     */
    private String lowDateStr;

    /**
     * max value of date to search.
     */
    private String highDateStr;

    /**
     * min value of accession_number to search.
     */
    private String accessionDirect;

    /**
     * max value of accession_number to search.
     */
    private String highAccessionDirect;

    /**
     * Rnage between two dates.
     */
    private DateRange dateRange;

    /**
     * list data for export in file.
     */
    private List<JgM03_BM_Patient> listReportData;

    /**
     * Instance of DAO.
     */
    private JgM03BMPatientDAO jgM03BMPatientDAO = new JgM03BMPatientDAOImpl();

    /**
     * init parameters get values for report.
     */
    @Override
    public void initializeReport(final BaseActionForm dynaForm) {
        super.initializeReport();
        lowDateStr = dynaForm.getString("lowerDateRange");
        highDateStr = dynaForm.getString("upperDateRange");
        accessionDirect = dynaForm.getString("accessionDirect");
        highAccessionDirect = dynaForm.getString("highAccessionDirect");
        dateRange = new DateRange(lowDateStr, highDateStr);

        createReportParameters();
        errorFound = !validateSubmitParameters();
        if (errorFound) {
            return;
        }
        listReportData = new ArrayList<JgM03_BM_Patient>();
        // listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr, highDateStr);
        checkNumberAndDate(lowDateStr, highDateStr, accessionDirect, highAccessionDirect);
        if (this.listReportData.size() == 0) {
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
    }

    /**
     * Get data follow accession_number and collection_date
     * 
     * @param lowDateStr2
     * @param highDateStr2
     * @param accessionDirect2
     * @param highAccessionDirect2
     */
    private void checkNumberAndDate(String lowDateStr2, String highDateStr2, String accessionDirect2,
            String highAccessionDirect2) {
        // 191 is testId of this report
        if (!(GenericValidator.isBlankOrNull(lowDateStr2) || GenericValidator.isBlankOrNull(highDateStr2))
                && !(GenericValidator.isBlankOrNull(accessionDirect2) || GenericValidator
                        .isBlankOrNull(highAccessionDirect2))) {
            listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientNumDate(accessionDirect2, highAccessionDirect2,
                    lowDateStr2, highDateStr2, 191, "");
        } else if ((GenericValidator.isBlankOrNull(lowDateStr2) || GenericValidator.isBlankOrNull(highDateStr2))
                && !GenericValidator.isBlankOrNull(accessionDirect2)
                || !GenericValidator.isBlankOrNull(highAccessionDirect2)) {
            listReportData = jgM03BMPatientDAO.getAllJgM03BMPatient(accessionDirect2, highAccessionDirect2, 191, "");
        } else if (!GenericValidator.isBlankOrNull(lowDateStr2)
                || !GenericValidator.isBlankOrNull(highDateStr2)
                && (GenericValidator.isBlankOrNull(accessionDirect2) || GenericValidator
                        .isBlankOrNull(highAccessionDirect2))) {
            listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr2, highDateStr2, 191, "");
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
     * Check date
     * 
     * @return
     */
    private boolean validateSubmitParameters() {
        if ((GenericValidator.isBlankOrNull(lowDateStr) && GenericValidator.isBlankOrNull(highDateStr))
                && (GenericValidator.isBlankOrNull(accessionDirect) && GenericValidator
                        .isBlankOrNull(highAccessionDirect))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String reportFileName() {
        return "JgM03_BM_Patient_Report";
    }

    @Override
    public void initializeReport(HashMap<String, String> hashmap) {
        super.initializeReport();
        // lowDateStr = hashmap.get("lowerDateRange");
        // highDateStr = hashmap.get("upperDateRange");
        accessionDirect = hashmap.get("accessionDirect");
        highAccessionDirect = hashmap.get("highAccessionDirect");
        // dateRange = new DateRange(lowDateStr, highDateStr);

        createReportParameters();
        errorFound = !validateSubmitParameters();
        if (errorFound) {
            return;
        }
        listReportData = new ArrayList<JgM03_BM_Patient>();
        // listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr, highDateStr);
        checkNumberAndDate(lowDateStr, highDateStr, accessionDirect, highAccessionDirect);
        if (this.listReportData.size() == 0) {
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
    }

}
