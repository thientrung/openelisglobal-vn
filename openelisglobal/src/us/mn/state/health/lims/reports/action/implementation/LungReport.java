 /**
 * @(#) LungReport.java 01-00 Sep 18, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Sep 18, 2016
 */
package us.mn.state.health.lims.reports.action.implementation;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;

/**
 * Detail description of processing of this class.
 *
 * @author haql
 */
public class LungReport extends Report implements IReportCreator {

    private String paramMenu = "";
    
    
    
    public LungReport(String inParamMenu) {
        this.paramMenu = inParamMenu;
        this.isUseDataSource = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeReport(BaseActionForm dynaForm) {
        super.initializeReport();
        try {
            startAccessionNumber = dynaForm.getString("accessionDirect");
            endAccessionNumber = dynaForm.getString("highAccessionDirect");
            startReceivedDate = dynaForm.getString("lowerDateRange");
            endReceivedDate = dynaForm.getString("upperDateRange");

            if (dynaForm.get("emergencySelectList") != null) {
                ReportSpecificationList emergencySelectList = (ReportSpecificationList) dynaForm
                        .get("emergencySelectList");
                emergency = emergencySelectList.getSelection() != null ? emergencySelectList
                        .getSelection() : "";
            }

            if (dynaForm.get("doctorSelectList") != null) {
                ReportSpecificationList doctorSelectList = (ReportSpecificationList) dynaForm
                        .get("doctorSelectList");
                doctor = doctorSelectList.getSelection() != null ? doctorSelectList
                        .getSelection() : "";
            }
            
            password = dynaForm.getString("password");
            createReportParameters();
        } catch (Exception e) {
            e.printStackTrace();
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeReport(HashMap<String, String> hashmap) {
        super.initializeReport();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JRDataSource getReportDataSource() throws IllegalStateException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String reportFileName() {
        String reportFileName = "";
        if (ReportFileNameAndParams.PARAM_LUNG_SPUTUM_DIRECT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_SPUTUM_DIRECT;
        }
        if (ReportFileNameAndParams.PARAM_PCR_LAO_PATIENT_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_PCR_LAO_PATIENT_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_XPERT_PATIENT_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_XPERT_PATIENT_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_LUNG_NTB_ANTIBIOTIC.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_NTB_ANTIBIOTIC;
        }
        if (ReportFileNameAndParams.PARAM_LUNG_MTB_ANTIBIOTIC.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_MTB_ANTIBIOTIC;
        }
        if (ReportFileNameAndParams.PARAM_PATIENT_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_PATIENT_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_HIV_PATIENT_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_HIV_PATIENT_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_HEMATOLOGY_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_HEMATOLOGY_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_BIOCHEMISTRY_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_BIOCHEMISTRY_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_LUNG_SPUTUM_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_SPUTUM_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_PCR_LAO_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_PCR_LAO_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_XPERT_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_XPERT_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_LUNG_MTB_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_MTB_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_LUNG_NTB_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_LUNG_NTB_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_HIV_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_HIV_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_BIOCHEMISTRY_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_BIOCHEMISTRY_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_HEMATOLOGY_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_HEMATOLOGY_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_IMMUNOLOGY_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_IMMUNOLOGY_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_URINE_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_URINE_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_RETURN_LOGBOOK.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_RETURN_LOGBOOK;
        }
        if (ReportFileNameAndParams.PARAM_XPERT_AGGREGATE.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_XPERT_AGGREGATE;
        }
        if (ReportFileNameAndParams.PARAM_BACTERIA_AGGREGATE.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_BACTERIA_AGGREGATE;
        }
        if (ReportFileNameAndParams.PARAM_MICROBIOLOGY_AGGREGATE.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_MICROBIOLOGY_AGGREGATE;
        }
        if (ReportFileNameAndParams.PARAM_DAILY_AGGREGATE_REPORT.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_DAILY_AGGREGATE_REPORT;
        }
        if (ReportFileNameAndParams.PARAM_HIV_AGGREGATE.equals(paramMenu)) {
            reportFileName = ReportFileNameAndParams.REPORT_HIV_AGGREGATE;
        }
        return reportFileName;
    }

}
