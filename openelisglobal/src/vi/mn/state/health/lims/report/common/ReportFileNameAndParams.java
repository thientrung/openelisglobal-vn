/**
 * 
 */
package vi.mn.state.health.lims.report.common;

/**
 * @author nhuql.gv
 * 
 */
public class ReportFileNameAndParams {

	// Get the parameter from browser to invoke report form
	public static String Param_HaitiClinicalNonConformityHema = "haitiClinicalNonConformityHema";
	public static String Param_PatientAnalysisAntigenNS1 = "patientAnalysisAntigenNS1";
	public static String Param_PatientAnalysisFoundVirusPCR = "patientAnalysisFoundVirusPCR";
	public static String Param_PatientDeterminePLVR = "patientDeterminePLVR";
	public static String Param_PatientResultTestAntigenIgm = "patientResultTestAntigenIgm";
	public static String Param_PatientHaitiClinicalJE = "patientHaitiClinicalJE";
	public static String Param_JgM03_BM_Patient_Report = "JgM03_BM_Patient_Report";
	public static String Param_PatientResultListJE = "patientResultListJE";
	public static String Param_ZikaPCR = "zikaPCR";

	public static String Param_EtorDengue = "etorDengue";
	public static String Param_EtorJE = "etorJE";

	// Get the name of report file
	public static String Report_NS1_Analysis_Antigen = "NS1_Analysis_Antigen";
	public static String Report_PCR_Analysis_Found_Virus = "PCR_Analysis_Found_Virus";
	public static String Report_PLVR_Determine_Virus = "PI_Isolation_Patient_Report";
	public static String Report_Test_Result = "PI_Dengue_Patient_Report";
	public static String Report_PI_Patient_Report_JE = "PI_JE_Patient_Report";
	public static String Report_JgM03_BM_Patient_Report = "PI_Dengue_Patient_List";
	public static String Report_JE_Patient_Report_List = "PI_JE_Patient_Report_List";
	public static String Report_ZikaPCR = "PI_Zika_Patient_Report_List";

	public static String Param_PIProcessing = "piProcessing";
	public static String Report_PIProcessing = "PI_Processing";
	
	public static String Param_DengueMonthlyAggregate = "dengueMonthyAggregate";
	public static String Report_DengueMonthlyAggregate = "PI_Dengue_Aggregate";
	public static String Param_PILogbook = "piLogbook";
	public static String Report_PILogbook = "PI_Logbook";
	public static String Param_PIDenYearlyAgg = "PI_Den_Yearly_Agg";
	public static String Param_PIDengueAggregate = "PI_Dengue_Aggregate";
	public static String Param_PIDenAggByCity = "PI_Den_Agg_By_City";
	public static String Param_PI_WHO_Dengue_logbook = "PI_WHO_Dengue_Logbook";
	
	//For Ha Noi Lung Hospital
    public static String REPORT_LUNG_SPUTUM_DIRECT = "Lung_ZN_Patient_Report";
    public static String REPORT_PCR_LAO_PATIENT_REPORT = "Lung_PCR_Patient_Report";
    public static String REPORT_XPERT_PATIENT_REPORT = "Lung_Xpert_Patient_Report";
    public static String REPORT_LUNG_NTB_ANTIBIOTIC = "Lung_NTB_Patient_Report";
    public static String REPORT_LUNG_MTB_ANTIBIOTIC = "Lung_MTB_Patient_Report";
    public static String REPORT_PATIENT_REPORT = "Lung_Patient_Report";
    public static String REPORT_HIV_PATIENT_REPORT = "Lung_HIV_Patient_Report";
    public static String REPORT_HEMATOLOGY_REPORT = "Lung_Hematology_Report";
    public static String REPORT_BIOCHEMISTRY_REPORT = "Lung_Biochemistry_Report";
    public static String REPORT_LUNG_SPUTUM_LOGBOOK = "Lung_ZN_Logbook";
    public static String REPORT_PCR_LAO_LOGBOOK = "Lung_PCR_Logbook";
    public static String REPORT_XPERT_LOGBOOK = "Lung_Xpert_Logbook";
    public static String REPORT_LUNG_MTB_LOGBOOK = "Lung_MTB_Logbook";
    public static String REPORT_LUNG_NTB_LOGBOOK = "Lung_NTB_Logbook";
    public static String REPORT_HIV_LOGBOOK = "Lung_HIV_Logbook";
    public static String REPORT_BIOCHEMISTRY_LOGBOOK = "Lung_Biochemistry_Logbook";
    public static String REPORT_HEMATOLOGY_LOGBOOK = "Lung_Hematology_Logbook";
    public static String REPORT_IMMUNOLOGY_LOGBOOK = "Lung_Immunology_Logbook";
    public static String REPORT_URINE_LOGBOOK = "Lung_Urine_Logbook";
    public static String REPORT_RETURN_LOGBOOK = "Lung_Return_Logbook";
    public static String REPORT_XPERT_AGGREGATE = "Lung_VS_Xpert_Agg";
    public static String REPORT_BACTERIA_AGGREGATE = "Lung_VS_Culture_Agg";
    public static String REPORT_MICROBIOLOGY_AGGREGATE = "Lung_VS_Monthly_Agg";
    public static String REPORT_DAILY_AGGREGATE_REPORT = "Lung_HemaBio_Aggregate";
    public static String REPORT_HIV_AGGREGATE = "Lung_HIV_Aggregate";
    
    public static String PARAM_LUNG_SPUTUM_DIRECT = "Lung_Sputum_Direct";
    public static String PARAM_PCR_LAO_PATIENT_REPORT = "PCR_Lao_Patient_Report";
    public static String PARAM_XPERT_PATIENT_REPORT = "Xpert_Patient_Report";
    public static String PARAM_LUNG_NTB_ANTIBIOTIC = "Lung_NTB_Antibiotic";
    public static String PARAM_LUNG_MTB_ANTIBIOTIC = "Lung_MTB_Antibiotic";
    public static String PARAM_PATIENT_REPORT = "Patient_Report";
    public static String PARAM_HIV_PATIENT_REPORT = "HIV_Patient_Report";
    public static String PARAM_HEMATOLOGY_REPORT = "Hematology_Report";
    public static String PARAM_BIOCHEMISTRY_REPORT = "Biochemistry_Report";
    public static String PARAM_LUNG_SPUTUM_LOGBOOK = "Lung_Sputum_Logbook";
    public static String PARAM_PCR_LAO_LOGBOOK = "PCR_Lao_Logbook";
    public static String PARAM_XPERT_LOGBOOK = "Xpert_Logbook";
    public static String PARAM_LUNG_MTB_LOGBOOK = "Lung_MTB_Logbook";
    public static String PARAM_LUNG_NTB_LOGBOOK = "Lung_NTB_Logbook";
    public static String PARAM_HIV_LOGBOOK = "HIV_Logbook";
    public static String PARAM_BIOCHEMISTRY_LOGBOOK = "Biochemistry_Logbook";
    public static String PARAM_HEMATOLOGY_LOGBOOK = "Hematology_Logbook";
    public static String PARAM_IMMUNOLOGY_LOGBOOK = "Immunology_Logbook";
    public static String PARAM_URINE_LOGBOOK = "Urine_Logbook";
    public static String PARAM_RETURN_LOGBOOK = "Return_Logbook";
    public static String PARAM_XPERT_AGGREGATE = "Xpert_Aggregate";
    public static String PARAM_BACTERIA_AGGREGATE = "Bacteria_Aggregate";
    public static String PARAM_MICROBIOLOGY_AGGREGATE = "Microbiology_Aggregate";
    public static String PARAM_DAILY_AGGREGATE_REPORT = "Daily_Aggregate_Report";
    public static String PARAM_HIV_AGGREGATE = "HIV_Aggregate";
}
