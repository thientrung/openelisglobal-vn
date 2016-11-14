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

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.reports.action.implementation.ReportSpecificationParameters.Parameter;
import vi.mn.state.health.lims.report.action.ReportEtorMapping;
import vi.mn.state.health.lims.report.action.ReportTestResult;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;

public class ReportImplementationFactory {
	private static final boolean isLNSP = true;

	public static IReportParameterSetter getParameterSetter(String report) {
		if (!GenericValidator.isBlankOrNull(report)) {
			if (report.equals("patientARVInitial1")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.ARV.initial"),
						null);
			} else if (report.equals("patientARVInitial2")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.ARV.initial"),
						null);
			} else if (report.equals("patientARVFollowup1")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.ARV.followup"),
						null);
			} else if (report.equals("patientARVFollowup2")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.ARV.followup"),
						null);
			} else if (report.equals("patientEID1")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.EID"),
						null);
			} else if (report.equals("patientEID2")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.EID"),
						null);
			} else if (report.equals("patientIndeterminate1")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.indeterminate"),
						null);
			} else if (report.equals("patientIndeterminate2")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.label.patient.indeterminate"),
						null);
			} else if (report.equals("patientIndeterminateByLocation")) {
				return new PatientIndeterminateByLocationReport();
			} else if (report.equals("indicatorSectionPerformance")) {
				return new ReportSpecificationParameters(
						Parameter.NO_SPECIFICATION,
						StringUtil
								.getMessageForKey("reports.label.indicator.performance"),
						null);
			} else if (report.equals("patientHaitiClinical")
					|| report.equals("patientHaitiLNSP")
					|| report.equals("patientCILNSP")) {
				return new PatientClinicalReport();
			} else if (report.equals("indicatorHaitiClinicalHIV")) {
				return new IndicatorHIV();
			} else if (report.equals("indicatorHaitiLNSPHIV")) {
				return new IndicatorHIVLNSP();
			} else if (report.equals("indicatorCDILNSPHIV")) {
				return new IndicatorCDIHIVLNSP();
			} else if (report.equals("indicatorHaitiClinicalAllTests")) {
				return new IndicatorAllTestClinical();
			} else if (report.equals("indicatorHaitiLNSPAllTests")) {
				return new IndicatorAllTestLNSP();
			} else if (report.equals("CISampleExport")) {
				return new ExportProjectByDate();
			} else if (report.equals("referredOut")) {
				return new ReferredOutReport();
			} else if (report.equals("HaitiExportReport")
					|| report.equals("HaitiLNSPExportReport")) {
				return new ReportSpecificationParameters(
						Parameter.DATE_RANGE,
						StringUtil
								.getMessageForKey("reports.label.project.export")
								+ " "
								+ StringUtil
										.getContextualMessageForKey("sample.collectionDate"),
						null);
			} else if (report.equals("indicatorConfirmation")) {
				return new ConfirmationReport();
			} else if (isNonConformityByDateReport(report)) {
				return new ReportSpecificationParameters(
						Parameter.DATE_RANGE,
						StringUtil
								.getMessageForKey("openreports.nonConformityReport"),
						null);
			} else if (isNonConformityBySectionReport(report)) {
				return new ReportSpecificationParameters(
						Parameter.DATE_RANGE,
						StringUtil
								.getMessageForKey("reports.nonConformity.bySectionReason.title"),
						null);
			} else if (report.equals("patientSpecialReport")) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.specialRequest.title"),
						null);
			} else if (report.equals("indicatorHaitiLNSPSiteTestCount")) {
				return new IndicatorHaitiSiteTestCountReport();
			} else if (report.equals("retroCIFollowupRequiredByLocation")) {
				return new RetroCIFollowupRequiredByLocation();
			} else if (report.equals("retroCInonConformityNotification")) {
				return new RetroCINonConformityNotification();
			} else if (report.equals("patientCollection")) {
				return new RetroCIPatientCollectionReport();
			} else if (report.equals("patientAssociated")) {
				return new RetroCIPatientAssociatedReport();
			} else if (report.equals("indicatorRealisation")) {
				return new ReportSpecificationParameters(Parameter.DATE_RANGE,
						StringUtil.getMessageForKey("report.realisation"), null);
			} else if (report.equals("epiSurveillanceExport")) {
				return new ReportSpecificationParameters(
						Parameter.DATE_RANGE,
						StringUtil
								.getMessageForKey("banner.menu.report.epi.surveillance.export"),
						null);
			} else if (report.equals("activityReportByPanel")) {
				return new ActivityReportByPanel();
			} else if (report.equals("activityReportByTest")) {
				return new ActivityReportByTest();
			} else if (report.equals("activityReportByTestSection")) {
				return new ActivityReportByTestSection();
			} else if (report.equals("rejectionReportByPanel")) {
				return new RejectionReportByPanel();
			} else if (report.equals("rejectionReportByTest")) {
				return new RejectionReportByTest();
			} else if (report.equals("rejectionReportByTestSection")) {
				return new RejectionReportByTestSection();
			}

			// nhuql.gv ADD
			else if (ReportFileNameAndParams.Param_HaitiClinicalNonConformityHema
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.DATE_AND_ACESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.patient.anslysis.ns1.title"),
						null);
			} else if (ReportFileNameAndParams.Param_PatientResultTestAntigenIgm
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.DATE_AND_ACESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.patient.test_reult_igm.title"),
						null);
			} else if (ReportFileNameAndParams.Param_PatientHaitiClinicalJE
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("openreports.patientTestStatusJE"),
						null);
			}
			// END nhuql.gv ADD

			// Dung add
			// Create new report all parameter
			else if (report.equals("commonParameter")) {
				return new ReportSpecificationParameters(
						Parameter.COMMON_REPORT,
						StringUtil.getMessageForKey("openreports.allparameter"),
						null);
			} else if (ReportFileNameAndParams.Param_JgM03_BM_Patient_Report
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE_AND_OGANIZATION_NAME_AND_TEST,
						StringUtil
								.getMessageForKey("openreports.patientIgMBM03"),
						null);
			} else if (ReportFileNameAndParams.Param_PatientResultListJE
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE_AND_OGANIZATION_NAME,
						StringUtil
								.getMessageForKey("openreports.patientResultListJE"),
						null);
			} else if (ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE_AND_OGANIZATION_NAME,
						StringUtil
								.getMessageForKey("reports.patient.anslysis.pcr.title"),
						null);
			} else if (ReportFileNameAndParams.Param_PatientDeterminePLVR
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.VIRUS_ISOLATION_LOGBOOK,
						StringUtil
								.getMessageForKey("reports.patient.determine.plvr.title"),
						null);
			} else if (ReportFileNameAndParams.Param_ZikaPCR.equals(report)) {
				return new ReportSpecificationParameters(Parameter.PCR_LOGBOOK,
						StringUtil.getMessageForKey("openreports.zikaPCR"),
						null);
			} else if (report.equals("BM04ElisaDengueIgG")) {
				return new ReportSpecificationParameters(
						Parameter.DATE_AND_ACESSION_RANGE,
						StringUtil
								.getMessageForKey("openreports.BM04ElisaDengueIgG"),
						null);
			} else if (ReportFileNameAndParams.Param_PIDenYearlyAgg
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.PI_DEN_YEARLY_AGG,
						StringUtil
								.getMessageForKey("reports.pi.den.yearly.aggregate"),
						null);
			} else if (ReportFileNameAndParams.Param_PIDenAggByCity
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.MONTHLY_REPORT,
						StringUtil
								.getMessageForKey("reports.pi.den.agg.by.city"),
						null);
			} else if (ReportFileNameAndParams.Param_PI_WHO_Dengue_logbook
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.PI_WHO_PARAMETER,
						StringUtil
								.getMessageForKey("reports.pi.who.den.logbook"),
						null);
			} else if (ReportFileNameAndParams.Param_PILogbook.equals(report)) {
				return new ReportSpecificationParameters(Parameter.PI_LOOKBOOK,
						StringUtil.getMessageForKey("openreports.piLogbook"),
						null);
			} else if (ReportFileNameAndParams.Param_PIProcessing.equals(report)) {
				return new ReportSpecificationParameters(Parameter.PI_LOOKBOOK,
						StringUtil.getMessageForKey("openreports.piProcessing"),
						null);
			}
			// nhuql.gv ADD
			else if (ReportFileNameAndParams.Param_EtorDengue.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.DATE_AND_ACESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.patient.test_reult_igm.title"),
						null);
			} else if (ReportFileNameAndParams.Param_EtorJE.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.ACCESSION_RANGE,
						StringUtil
								.getMessageForKey("reports.patient.test_reult_igm.title"),
						null);
			} else if (ReportFileNameAndParams.Param_DengueMonthlyAggregate
					.equals(report)) {
				return new ReportSpecificationParameters(
						Parameter.MONTHLY_REPORT,
						StringUtil
								.getMessageForKey("openreports.dengueMonthyAggregate"),
						null);
			}
			// For Ha Noi Lung Hospital
			if (ReportFileNameAndParams.PARAM_LUNG_SPUTUM_DIRECT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ONLY_ACCESSION_RANGE, StringUtil.getMessageForKey("openreports.lungSputumDirect"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_PCR_LAO_PATIENT_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ONLY_ACCESSION_RANGE, StringUtil.getMessageForKey("openreports.PCRLaoPatientReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_XPERT_PATIENT_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ONLY_ACCESSION_RANGE, StringUtil.getMessageForKey("openreports.xpertPatientReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_LUNG_NTB_ANTIBIOTIC.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ONLY_ACCESSION_RANGE, StringUtil.getMessageForKey("openreports.NTBAntibiotic"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_LUNG_MTB_ANTIBIOTIC.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ONLY_ACCESSION_RANGE, StringUtil.getMessageForKey("openreports.MTBAntibiotic"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_PATIENT_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ACCESSION_RANGE_WITH_DOCTOR, StringUtil.getMessageForKey("openreports.patientReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_HIV_PATIENT_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ACCESSION_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.HIVPatientReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_HEMATOLOGY_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ACCESSION_RANGE_WITH_DOCTOR, StringUtil.getMessageForKey("openreports.hematologyReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_BIOCHEMISTRY_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.ACCESSION_RANGE_WITH_DOCTOR, StringUtil.getMessageForKey("openreports.biochemistryReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_LUNG_SPUTUM_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.lungSputumLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_PCR_LAO_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.PCRLaoLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_XPERT_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.xpertLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_LUNG_MTB_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.lungMTBLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_LUNG_NTB_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.lungNTBLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_HIV_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.HIVLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_BIOCHEMISTRY_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.biochemistryLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_HEMATOLOGY_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.hematologyLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_IMMUNOLOGY_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.immunologyLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_URINE_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_PASSWORD, StringUtil.getMessageForKey("openreports.urineLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_RETURN_LOGBOOK.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.returnLogbook"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_XPERT_AGGREGATE.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.xpertAggregate"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_BACTERIA_AGGREGATE.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.bacteriaAggregate"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_MICROBIOLOGY_AGGREGATE.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.microbiology_Aggregate"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_DAILY_AGGREGATE_REPORT.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE_WITH_EMERGENCY, StringUtil.getMessageForKey("openreports.dailyAggregateReport"),null);
	        }
	        if (ReportFileNameAndParams.PARAM_HIV_AGGREGATE.equals(report)) {
	            return new ReportSpecificationParameters(Parameter.DATE_RANGE, StringUtil.getMessageForKey("openreports.HIVAggregate"),null);
	        }
		}

		return null;
	}

	private static boolean isNonConformityByDateReport(String report) {
		return report.equals("retroCINonConformityByDate")
				|| report.equals("haitiNonConformityByDate")
				|| report.equals("haitiClinicalNonConformityByDate");
	}

	private static boolean isNonConformityBySectionReport(String report) {
		return report.equals("retroCInonConformityBySectionReason")
				|| report.equals("haitiNonConformityBySectionReason")
				|| report.equals("haitiClinicalNonConformityBySectionReason");
	}

	public static IReportCreator getReportCreator(String report) {
		if (!GenericValidator.isBlankOrNull(report)) {
			if (report.equals("patientARVInitial1")) {
				return new PatientARVInitialVersion1Report();
			} else if (report.equals("patientARVInitial2")) {
				return new PatientARVInitialVersion2Report();
			} else if (report.equals("patientARVFollowup1")) {
				return new PatientARVFollowupVersion1Report();
			} else if (report.equals("patientARVFollowup2")) {
				return new PatientARVFollowupVersion2Report();
			} else if (report.equals("patientEID1")) {
				return new PatientEIDVersion1Report();
			} else if (report.equals("patientEID2")) {
				return new PatientEIDVersion2Report();
			} else if (report.equals("patientIndeterminate1")) {
				return new PatientIndeterminateVersion1Report();
			} else if (report.equals("patientIndeterminate2")) {
				return new PatientIndeterminateVersion2Report();
			} else if (report.equals("patientIndeterminateByLocation")) {
				return new PatientIndeterminateByLocationReport();
			} else if (report.equals("indicatorSectionPerformance")) {
				return new IndicatorSectionPerformanceReport();
			} else if (report.equals("patientHaitiClinical")) {
				return new PatientClinicalReport(!isLNSP);
			} else if (report.equals("patientHaitiLNSP")) {
				return new PatientClinicalReport(isLNSP);
			} else if (report.equals("patientCILNSP")) {
				return new PatientCILNSPClinical();
			} else if (report.equals("indicatorHaitiClinicalHIV")) {
				return new IndicatorHIV();
			} else if (report.equals("indicatorHaitiLNSPHIV")) {
				return new IndicatorHIVLNSP();
			} else if (report.equals("indicatorHaitiClinicalAllTests")) {
				return new IndicatorAllTestClinical();
			} else if (report.equals("indicatorHaitiLNSPAllTests")) {
				return new IndicatorAllTestLNSP();
			} else if (report.equals("CISampleExport")) {
				return new ExportProjectByDate();
			} else if (report.equals("referredOut")) {
				return new ReferredOutReport();
			} else if (report.equals("HaitiExportReport")) {
				return new HaitiExportReport();
			} else if (report.equals("HaitiLNSPExportReport")) {
				return new HaitiLNSPExportReport();
			} else if (report.equals("indicatorConfirmation")) {
				return new ConfirmationReport();
			} else if (report.equals("retroCINonConformityByDate")) {
				return new RetroCINonConformityByDate();
			} else if (report.equals("haitiNonConformityByDate")) {
				return new HaitiNonConformityByDate();
			} else if (report.equals("haitiClinicalNonConformityByDate")) {
				return new HaitiNonConformityByDate();
			} else if (report.equals("retroCInonConformityBySectionReason")) {
				return new RetroCINonConformityBySectionReason();
			} else if (report.equals("haitiNonConformityBySectionReason")) {
				return new HaitiNonConformityBySectionReason();
			} else if (report
					.equals("haitiClinicalNonConformityBySectionReason")) {
				return new HaitiNonConformityBySectionReason();
			} else if (report.equals("indicatorHaitiLNSPSiteTestCount")) {
				return new IndicatorHaitiSiteTestCountReport();
			} else if (report.equals("retroCIFollowupRequiredByLocation")) {
				return new RetroCIFollowupRequiredByLocation();
			} else if (report.equals("patientSpecialReport")) {
				return new PatientSpecialRequestReport();
			} else if (report.equals("retroCInonConformityNotification")) {
				return new RetroCINonConformityNotification();
			} else if (report.equals("patientCollection")) {
				return new RetroCIPatientCollectionReport();
			} else if (report.equals("patientAssociated")) {
				return new RetroCIPatientAssociatedReport();
			} else if (report.equals("indicatorCDILNSPHIV")) {
				return new IndicatorCDIHIVLNSP();
			} else if (report.equals("validationBacklog")) {
				return new ValidationBacklogReport();
			} else if (report.equals("indicatorRealisation")) {
				return new IPCIRealisationReport();
			} else if (report.equals("epiSurveillanceExport")) {
				return new HaitiLnspEpiExportReport();
			} else if (report.equals("activityReportByPanel")) {
				return new ActivityReportByPanel();
			} else if (report.equals("activityReportByTest")) {
				return new ActivityReportByTest();
			} else if (report.equals("activityReportByTestSection")) {
				return new ActivityReportByTestSection();
			} else if (report.equals("rejectionReportByPanel")) {
				return new RejectionReportByPanel();
			} else if (report.equals("rejectionReportByTest")) {
				return new RejectionReportByTest();
			} else if (report.equals("rejectionReportByTestSection")) {
				return new RejectionReportByTestSection();
			}
			// nhuql.gv ADD
			else if (ReportFileNameAndParams.Param_HaitiClinicalNonConformityHema
					.equals(report)) {
				return new HaitiNonConformityHema();
			} else if (ReportFileNameAndParams.Param_PatientResultTestAntigenIgm
					.equals(report)
					|| ReportFileNameAndParams.Param_PatientHaitiClinicalJE
							.equals(report)) {
				return new ReportTestResult(report);
			} else if (ReportFileNameAndParams.Param_EtorDengue.equals(report)
					|| ReportFileNameAndParams.Param_EtorJE.equals(report)) {
				return new ReportEtorMapping(report);
			}
			// END nhuql.gv ADD
			// Dung add
			else if (ReportFileNameAndParams.Param_JgM03_BM_Patient_Report
					.equals(report)
					|| ReportFileNameAndParams.Param_PatientResultListJE
							.equals(report)
					|| ReportFileNameAndParams.Param_PatientAnalysisFoundVirusPCR
							.equals(report)
					|| ReportFileNameAndParams.Param_PatientDeterminePLVR
							.equals(report)
					|| ReportFileNameAndParams.Param_ZikaPCR.equals(report)) {
				return new ReportJgM03BMpatient(report);
			} else if (report.equals("BM04ElisaDengueIgG")) {
				return new ReportBM04ElisaDengueIgG();
			} else if (report.equals("dengueMonthyAggregate")) {
				return new PIDengueMonthlyAggregate(report);
			} else if (report.equals("piLogbook")) {
				return new PILogbook(report);
			}else if (report.equals("piProcessing")) {
				return new PIProcessing(report);
			} else if (report
					.equals(ReportFileNameAndParams.Param_PIDenYearlyAgg)) {
				return new PIDenYearlyAgg(report);
			} else if (report
					.equals(ReportFileNameAndParams.Param_PIDenAggByCity)) {
				return new PIDenAggByCity(report);
			}
			if (ReportFileNameAndParams.Param_PI_WHO_Dengue_logbook
					.equals(report)) {
				return new PIWHODengueLogbook(report);
			}
			//For Ha Noi Lung Hospital
			if (ReportFileNameAndParams.PARAM_LUNG_SPUTUM_DIRECT.equals(report)
			        || ReportFileNameAndParams.PARAM_PCR_LAO_PATIENT_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_XPERT_PATIENT_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_LUNG_NTB_ANTIBIOTIC.equals(report)
			        || ReportFileNameAndParams.PARAM_LUNG_MTB_ANTIBIOTIC.equals(report)
			        || ReportFileNameAndParams.PARAM_PATIENT_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_HIV_PATIENT_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_HEMATOLOGY_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_BIOCHEMISTRY_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_LUNG_SPUTUM_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_PCR_LAO_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_XPERT_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_LUNG_MTB_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_LUNG_NTB_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_HIV_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_BIOCHEMISTRY_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_HEMATOLOGY_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_IMMUNOLOGY_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_URINE_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_RETURN_LOGBOOK.equals(report)
			        || ReportFileNameAndParams.PARAM_XPERT_AGGREGATE.equals(report)
			        || ReportFileNameAndParams.PARAM_BACTERIA_AGGREGATE.equals(report)
			        || ReportFileNameAndParams.PARAM_MICROBIOLOGY_AGGREGATE.equals(report)
			        || ReportFileNameAndParams.PARAM_DAILY_AGGREGATE_REPORT.equals(report)
			        || ReportFileNameAndParams.PARAM_HIV_AGGREGATE.equals(report)) {
			    return new LungReport(report);
			}
		}

		return null;

	}

}
