/*
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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 */
package us.mn.state.health.lims.reports.action.implementation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.patienttype.daoimpl.PatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.valueholder.PatientType;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;

public class ReportSpecificationParameters implements IReportParameterSetter,
		IActionConstants {
    private SystemConfiguration conf = SystemConfiguration.getInstance();

    public enum Parameter {
        NO_SPECIFICATION,
        DATE_RANGE,
        ONLY_ACCESSION_RANGE,
        ACCESSION_RANGE,
        DATE_AND_ACESSION_RANGE,
        SINGLE_ACESSION,
        ACCESSION_RANGE_AND_OGANIZATION_NAME_AND_TEST,
        ACCESSION_RANGE_AND_OGANIZATION_NAME,
        VIRUS_ISOLATION_LOGBOOK,
        PCR_LOGBOOK,
        ACCESSION_AND_DATE_AND_ORGANIZATION,
        MONTHLY_REPORT,
        COMMON_REPORT,
        PI_DEN_YEARLY_AGG,
        PI_LOOKBOOK,
        PI_WHO_PARAMETER,
        DATE_RANGE_WITH_PASSWORD,
        ACCESSION_RANGE_WITH_PASSWORD,
        ACCESSION_RANGE_WITH_DOCTOR,
        DATE_RANGE_WITH_EMERGENCY
    }

	private static List<IdValuePair> MONTH_LIST;
	static {
		MONTH_LIST = new ArrayList<IdValuePair>();

		MONTH_LIST.add(new IdValuePair("1", StringUtil
				.getMessageForKey("month.january.abbrev")));
		MONTH_LIST.add(new IdValuePair("2", StringUtil
				.getMessageForKey("month.february.abbrev")));
		MONTH_LIST.add(new IdValuePair("3", StringUtil
				.getMessageForKey("month.march.abbrev")));
		MONTH_LIST.add(new IdValuePair("4", StringUtil
				.getMessageForKey("month.april.abbrev")));
		MONTH_LIST.add(new IdValuePair("5", StringUtil
				.getMessageForKey("month.may.abbrev")));
		MONTH_LIST.add(new IdValuePair("6", StringUtil
				.getMessageForKey("month.june.abbrev")));
		MONTH_LIST.add(new IdValuePair("7", StringUtil
				.getMessageForKey("month.july.abbrev")));
		MONTH_LIST.add(new IdValuePair("8", StringUtil
				.getMessageForKey("month.august.abbrev")));
		MONTH_LIST.add(new IdValuePair("9", StringUtil
				.getMessageForKey("month.september.abbrev")));
		MONTH_LIST.add(new IdValuePair("10", StringUtil
				.getMessageForKey("month.october.abbrev")));
		MONTH_LIST.add(new IdValuePair("11", StringUtil
				.getMessageForKey("month.november.abbrev")));
		MONTH_LIST.add(new IdValuePair("12", StringUtil
				.getMessageForKey("month.december.abbrev")));
	}
	private String reportTitle;
	private String instructions;
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();

	/**
	 * Constructor for a single parameter.
	 * 
	 * @param parameter
	 *            The parameter which will appear on the parameter page
	 * @param title
	 *            The title for the page, it will appear above the parameters
	 * @param instructions
	 *            The instructions for the user on how to fill in the parameters
	 */
	public ReportSpecificationParameters(Parameter parameter, String title,
			String instructions) {
		parameters.add(parameter);
		reportTitle = title;
		this.instructions = instructions;
	}

	public ReportSpecificationParameters(Parameter[] parameters, String title,
			String instructions) {
		reportTitle = title;
		this.instructions = instructions;

		for (Parameter newParameter : parameters) {
			this.parameters.add(newParameter);
		}

	}

	@Override
	public void setRequestParameters(BaseActionForm dynaForm) {
		try {
			PropertyUtils.setProperty(dynaForm, "reportName", reportTitle);
			if (!GenericValidator.isBlankOrNull(instructions)) {
				PropertyUtils.setProperty(dynaForm, "instructions",
						instructions);
			}
			PropertyUtils.setProperty(dynaForm, "reportName", reportTitle);
			for (Parameter parameter : parameters) {
				switch (parameter) {
				case DATE_RANGE: {
					setParametersForReport(dynaForm, false, false, true, true, false, false, false);
					break;
				}
				case ONLY_ACCESSION_RANGE: {
				    setParametersForReport(dynaForm, true, true, false, false, false, false, false);
				    break;
				}
				case ACCESSION_RANGE: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					// ReportSpecificationList reportSpecificationList=new
					// ReportSpecificationList(listTestData(),
					// NAME_SHOW_TEST_COMBOBOX);
					// PropertyUtils.setProperty( dynaForm, "selectList",
					// reportSpecificationList);
					break;
				}
				case DATE_AND_ACESSION_RANGE: {
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					ReportSpecificationList reportSpecificationList = new ReportSpecificationList(
							listTestData(), NAME_SHOW_TEST_COMBOBOX);
					PropertyUtils.setProperty(dynaForm, "selectList",
							reportSpecificationList);
					break;
				}
				case PI_WHO_PARAMETER: {
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					break;
				}
				case SINGLE_ACESSION: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					break;
				}
				case NO_SPECIFICATION: {
					PropertyUtils.setProperty(dynaForm,
							"noRequestSpecifications", true);
					break;
				}
				case ACCESSION_RANGE_AND_OGANIZATION_NAME_AND_TEST: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);

					ReportSpecificationList reportListTest = new ReportSpecificationList(
							listTestData(), NAME_SHOW_TEST_COMBOBOX);
					PropertyUtils.setProperty(dynaForm, "selectList",
							reportListTest);
					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					break;
				}
				case ACCESSION_RANGE_AND_OGANIZATION_NAME: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);

					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					break;
				}
				case VIRUS_ISOLATION_LOGBOOK: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);

					ParameterTestIsolationList testIsolationList = new ParameterTestIsolationList(
							getTestIsolationListData(),
							StringUtil
									.getMessageForKey("report.parameters.isolationTestIds"));
					PropertyUtils.setProperty(dynaForm, "testIsolationList",
							testIsolationList);

					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					break;
				}
				case PCR_LOGBOOK: {
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);

					ParameterTestPCRList testPCRList = new ParameterTestPCRList(
							getTestPCRListData(),
							StringUtil
									.getMessageForKey("report.parameters.pcrTestIds"));
					PropertyUtils.setProperty(dynaForm, "testPCRList",
							testPCRList);

					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					break;
				}
				case ACCESSION_AND_DATE_AND_ORGANIZATION: {
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					break;
				}
				case MONTHLY_REPORT: {
					PropertyUtils
							.setProperty(dynaForm, "monthList", MONTH_LIST);
					PropertyUtils.setProperty(dynaForm, "yearList",
							getYearList());
					ParameterProjectDengue reportProjectDengueList = new ParameterProjectDengue(
							listProjectDengue(), NAME_SHOW_PROJECT_DENGUE);
					PropertyUtils.setProperty(dynaForm, "listProjectDengue",
							reportProjectDengueList);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					break;
				}
				// Dung add
				case COMMON_REPORT: {
					// find by accession number from>to
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					// find by month and year option
					PropertyUtils
							.setProperty(dynaForm, "monthList", MONTH_LIST);
					PropertyUtils.setProperty(dynaForm, "yearList",
							getYearList());
					// find by received date from -> to
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					// find by Illness date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerIllnessDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperIllnessDateRange", true);
					// find by completed date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerCompleteDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperCompleteDateRange", true);
					// find by result date from -> to
					PropertyUtils.setProperty(dynaForm,
							"useLowerResultDateRange", true);
					PropertyUtils.setProperty(dynaForm,
							"useUpperResultDateRange", true);
					// find By project Dengues
					ParameterProjectDengue reportProjectDengueList = new ParameterProjectDengue(
							listProjectDengue(), NAME_SHOW_PROJECT_DENGUE);
					PropertyUtils.setProperty(dynaForm, "listProjectDengue",
							reportProjectDengueList);
					// find by requested(organization)
					ReportSpecificationListName reportListName = new ReportSpecificationListName(
							listOrganizationName(),
							conf.getReportOrganizationText());
					PropertyUtils.setProperty(dynaForm, "selectListName",
							reportListName);
					// find by PCR test
					ParameterTestPCRList testPCRList = new ParameterTestPCRList(
							getTestPCRListData(),
							StringUtil
									.getMessageForKey("report.parameters.pcrTestIds"+":"));
					PropertyUtils.setProperty(dynaForm, "testPCRList",
							testPCRList);
					// find by Dengue test
					ReportSpecificationList reportSpecificationList = new ReportSpecificationList(
							listTestData(), NAME_SHOW_TEST_COMBOBOX);
					PropertyUtils.setProperty(dynaForm, "selectList",
							reportSpecificationList);
					break;
				}
				case PI_DEN_YEARLY_AGG: {
					PropertyUtils.setProperty(dynaForm, "yearList",
							getYearList());
					// find By project Dengues
					ParameterProjectDengue reportProjectDengueList = new ParameterProjectDengue(
							listProjectDengue(), NAME_SHOW_PROJECT_DENGUE);
					PropertyUtils.setProperty(dynaForm, "listProjectDengue",
							reportProjectDengueList);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					break;
				}
				case PI_LOOKBOOK: {
					PropertyUtils.setProperty(dynaForm, "useAccessionDirect",
							true);
					PropertyUtils.setProperty(dynaForm,
							"useHighAccessionDirect", true);
					PropertyUtils.setProperty(dynaForm, "useLowerDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "useUpperDateRange",
							true);
					PropertyUtils.setProperty(dynaForm, "usepdf", true);
					PropertyUtils.setProperty(dynaForm, "useexcel", true);
					break;
				}
				case DATE_RANGE_WITH_PASSWORD: {
				    setParametersForReport(dynaForm, false, false, true, true, false, false, true);
                    break;
				}
				case ACCESSION_RANGE_WITH_PASSWORD: {
				    setParametersForReport(dynaForm, true, true, false, false, false, false, true);
                    break;
				}
				case ACCESSION_RANGE_WITH_DOCTOR: {
				    setParametersForReport(dynaForm, true, true, false, false, true, false, false);
                    break;
				}
				case DATE_RANGE_WITH_EMERGENCY: {
				    setParametersForReport(dynaForm, false, false, true, true, false, true, false);
                    break;
				}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<IdValuePair> listProjectDengue() {
		String projectDengue = SystemConfiguration.getInstance()
				.getprojectDengue();
		String[] arrayProjectDengue = projectDengue.split(",");
		List<IdValuePair> liValue = new ArrayList<>();
		for (String tmp : arrayProjectDengue) {
			if (tmp.equals("MTQG")) {
				IdValuePair idValuePair = new IdValuePair(tmp, MTQG);
				liValue.add(idValuePair);
			}
			if (tmp.equals("BCTX")) {
				IdValuePair idValuePair = new IdValuePair(tmp, BCTX);
				liValue.add(idValuePair);
			}
		}
		return liValue;
	}

	private List<IdValuePair> listTestData() {
		String aTestId = SystemConfiguration.getInstance().getReportTestIds();
		String[] arrayTest = aTestId.split(",");
		List<IdValuePair> liValue = new ArrayList<>();
		for (String tmp : arrayTest) {
			TestDAO testDAO = new TestDAOImpl();
			Test test = new Test();
			test = testDAO.getTestById(tmp);
			IdValuePair idValuePair = new IdValuePair(test.getId(),
					test.getDescription());
			liValue.add(idValuePair);
		}
		return liValue;
	}

	/**
	 * nhql.gv Convert to list IdValuePair for load to combobox
	 * 
	 * @return
	 */
	private List<IdValuePair> listOrganizationName() {
		List<IdValuePair> liValue = new ArrayList<>();
		List<Organization> listOrganizationName = new OrganizationDAOImpl()
				.getListOrganizationName();
		for (Organization organization : listOrganizationName) {
			IdValuePair idValuePair = new IdValuePair(
					organization.getId(),
					organization.getOrganizationName());
			liValue.add(idValuePair);
		}
		return liValue;
	}

	/**
	 * Haql Get list of Test Isolation IdValuePair for load to combobox
	 * 
	 * @return
	 */
	private List<IdValuePair> getTestIsolationListData() {
		String aTestId = SystemConfiguration.getInstance()
				.getIsolationTestIds();
		String[] arrayTest = aTestId.split(",");
		List<IdValuePair> liValue = new ArrayList<>();
		for (String tmp : arrayTest) {
			TestDAO testDAO = new TestDAOImpl();
			Test test = new Test();
			test = testDAO.getTestById(tmp);
			IdValuePair idValuePair = new IdValuePair(test.getId(),
					test.getDescription());
			liValue.add(idValuePair);
		}
		return liValue;
	}

	/**
	 * Haql Get list of Test PCR IdValuePair for load to combobox
	 * 
	 * @return
	 */
	private List<IdValuePair> getTestPCRListData() {
		String aTestId = SystemConfiguration.getInstance().getPCRTestIds();
		String[] arrayTest = aTestId.split(",");
		List<IdValuePair> liValue = new ArrayList<>();
		for (String tmp : arrayTest) {
			TestDAO testDAO = new TestDAOImpl();
			Test test = new Test();
			test = testDAO.getTestById(tmp);
			IdValuePair idValuePair = new IdValuePair(test.getId(),
					test.getDescription());
			liValue.add(idValuePair);
		}
		return liValue;
	}

	/**
	 * Haql Get list year
	 * 
	 * @return
	 */
	private List<IdValuePair> getYearList() {
		List<IdValuePair> list = new ArrayList<IdValuePair>();

		int currentYear = DateUtil.getCurrentYear();

		for (int i = 5; i >= 0; i--) {
			String year = String.valueOf(currentYear - i);
			list.add(new IdValuePair(year, year));
		}

		return list;
	}

    /**
     * Haql Get list of Patient Type IdValuePair for load to combobox
     * 
     * @return
     */
    private List<IdValuePair> getPatientTypeListData() {
        List<IdValuePair> liValue = new ArrayList<>();
        List<PatientType> listPatientType = new PatientTypeDAOImpl().getAllPatientTypes();
        
        for (PatientType patientType : listPatientType) {
            IdValuePair idValuePair = new IdValuePair(
                    patientType.getId(),
                    patientType.getDescription());
            liValue.add(idValuePair);
        }
        return liValue;
    }
    
    private void setParametersForReport(BaseActionForm dynaForm, boolean hasAccessionNumber, 
            boolean hasHighAccessionNumber, boolean hasLowerDateRange,boolean hasUpperDateRange,
            boolean hasDoctorList, boolean hasEmergencyList,boolean hasPassword) {
        try {
            PropertyUtils.setProperty(dynaForm, "useAccessionDirect", hasAccessionNumber);
            PropertyUtils.setProperty(dynaForm, "useHighAccessionDirect", hasHighAccessionNumber);

            PropertyUtils.setProperty(dynaForm, "useLowerDateRange", hasLowerDateRange);
            PropertyUtils.setProperty(dynaForm, "useUpperDateRange", hasUpperDateRange);
            
            if (hasEmergencyList) {
                ReportSpecificationList emergencySpecificationList = new ReportSpecificationList(
                        listEmergency(), StringUtil.getMessageForKey("reports.parameter.emergencyList"));
                PropertyUtils.setProperty(dynaForm, "emergencySelectList", emergencySpecificationList);
            }
            
            if (hasDoctorList) {
                ReportSpecificationList doctorSpecificationList = new ReportSpecificationList(
                        listDoctor(), StringUtil.getMessageForKey("reports.parameter.doctorList"));
                PropertyUtils.setProperty(dynaForm, "doctorSelectList", doctorSpecificationList);
            }
            
            PropertyUtils.setProperty(dynaForm, "usePassword", hasPassword);
            
            PropertyUtils.setProperty(dynaForm, "usepdf", true);
            PropertyUtils.setProperty(dynaForm, "useexcel", true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    private List<IdValuePair> listEmergency() {
        String emergency = SystemConfiguration.getInstance().getReportParameterEmergency();
        String[] arrayEmergency = emergency.split(",");
        List<IdValuePair> liValue = new ArrayList<>();
        for (String tmp : arrayEmergency) {
            IdValuePair idValuePair = new IdValuePair(tmp, tmp);
            liValue.add(idValuePair);
        }
        return liValue;
    }
    
    private List<IdValuePair> listDoctor() {
        String doctor = SystemConfiguration.getInstance().getReportParameterDoctor();
        String[] arrayDoctor = doctor.split(",");
        List<IdValuePair> liValue = new ArrayList<>();
        for (String tmp : arrayDoctor) {
            IdValuePair idValuePair = new IdValuePair(tmp, tmp);
            liValue.add(idValuePair);
        }
        return liValue;
    }
}
