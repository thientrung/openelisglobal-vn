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
package us.mn.state.health.lims.reports.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Transaction;

import us.mn.state.health.lims.audittrail.dao.AuditTrailDAO;
import us.mn.state.health.lims.audittrail.daoimpl.AuditTrailDAOImpl;
import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.ReportTrackingService;
import us.mn.state.health.lims.common.services.ReportTrackingService.ReportType;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.dataexchange.aggregatereporting.daoimpl.ReportExternalExportDAOImpl;
import us.mn.state.health.lims.dataexchange.aggregatereporting.valueholder.ReportExternalExport;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.reports.action.implementation.IReportCreator;
import us.mn.state.health.lims.reports.action.implementation.ReportImplementationFactory;
import vi.mn.state.health.lims.report.dao.ReportPIDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportPIDAOImpl;

public class CommonReportPrintAction extends BaseAction {

	private static String reportPath = null;
	private static String imageUrl = null;
	
	//nhuql.gv ADD
	private static String rightImageUrl = null;
	
	//created a bype[];
	private static byte[] bytes = new byte[10];

	@SuppressWarnings("unchecked")
	@Override
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		BaseActionForm dynaForm = (BaseActionForm) form;

		PropertyUtils.setProperty(dynaForm, "reportType", request.getParameter("type"));
		
		IReportCreator reportCreator = ReportImplementationFactory.getReportCreator(request.getParameter("report"));

		String forward = FWD_FAIL;
		
		if (reportCreator != null) {
			reportCreator.setImageUrl(getImageUrl());
			
			//nhuql.gv ADD
			reportCreator.setRightImageUrl(getRightImageUrl());
			
			reportCreator.initializeReport(dynaForm);
			reportCreator.setReportPath(getReportPath());
			HashMap<String, Object> parameterMap = (HashMap<String, Object>) reportCreator.getReportParameters();
			parameterMap.put("SUBREPORT_DIR", getReportPath());

			try {
				String reportType = request.getParameter("type");
				response.setContentType(reportCreator.getContentType());
				String responseHeaderName = reportCreator.getResponseHeaderName();
				String responseHeaderContent = reportCreator.getResponseHeaderContent();
				if ( !GenericValidator.isBlankOrNull(responseHeaderName) && !GenericValidator.isBlankOrNull(responseHeaderContent) ) {
					response.setHeader(responseHeaderName, responseHeaderContent);
				}
				
				String reportName = reportCreator.getReportName();
				/*
				 * Generate PDF file
				 */
				if (request.getParameter("typeFile").trim().equals("pdf")) {
					bytes = reportCreator.runReport();
					response.setContentLength(bytes.length);
					
				} else { // return if web service call
					/*
					 * Generate Excel file
					 */
					bytes = reportCreator.runReportForExcel();
					response.setContentLength(bytes.length);
					response.setContentType("application/vnd.ms-excel");
					response.setHeader("Content-Disposition", "attachment; filename=" + reportName + ".xls"); 
					//newly added
					// create PDF or ���xcel file in /WEBcontent/web-inf/reports/generated/File.pdf File.xls
				}
				
				ServletOutputStream servletOutputStream = response.getOutputStream();
				servletOutputStream.write(bytes, 0, bytes.length);
				servletOutputStream.flush();
				servletOutputStream.close();
				
			} catch (Exception e) {
				LogEvent.logError("CommonReportPrintAction", "performAction()", Arrays.toString(e.getStackTrace()));
			}
			
			//add: get necessary parameters to get list of satisfied accession number for save log
            String fromAccessionNumber = (String) parameterMap.get("startAccessionNumber");
            String toAccessionNumber = (String) parameterMap.get("endAccessionNumber");            
            String fromStartedIllnessDate = (String) parameterMap.get("startIllnessDate");
            String toStartedIllnessDate = (String) parameterMap.get("endIllnessDate");
            String fromRecievedDate = (String) parameterMap.get("startReceivedDate");
            String toRecievedDate = (String) parameterMap.get("endReceivedDate");
            String fromResultDate = (String) parameterMap.get("startResultDate");
            String toResultDate = (String) parameterMap.get("endResultDate");
            String testId = (String) parameterMap.get("testID");
            String isLog = (String) parameterMap.get("isLogable");
            String organizationID = (String) parameterMap.get("organizationID");
            String printReportName = (String) dynaForm.get("reportName");
            
            List accessionNumberList = null;
            //check if this report is going to be logged to history or ignored
            if (isLog == "true") {
                ReportPIDAO reportPIDAO = new ReportPIDAOImpl();
                accessionNumberList= reportPIDAO.getListOfSatisfiedAccessionNumber(fromAccessionNumber, toAccessionNumber, fromStartedIllnessDate, toStartedIllnessDate, fromRecievedDate, toRecievedDate, fromResultDate, toResultDate, testId, organizationID);                           
            }           
            //end
            
            if (accessionNumberList!= null && !accessionNumberList.isEmpty() ) {
                
                //add: prepare content of print report
                    // the following code use to separated list of accession number by comma
                String accessionNumberSeparatedByComma = "";
                for (Object accNum : accessionNumberList) {
                    accessionNumberSeparatedByComma += accNum.toString() + ",";
                }
                
                accessionNumberSeparatedByComma = accessionNumberSeparatedByComma.substring(0, accessionNumberSeparatedByComma.lastIndexOf(","));
                
                String msg= "Report name: "+printReportName+"; "+ accessionNumberSeparatedByComma;
                ReportExternalExport report = new ReportExternalExport();
                report.setData(msg);
                report.setSysUserId(currentUserId);
                report.setEventDate(DateUtil.getNowAsTimestamp());
                report.setCollectionDate(DateUtil.getNowAsTimestamp());
                report.setTypeId("1");
                report.setBookkeepingData("");
                report.setSend(true);
                
                // save content of printed report
                Transaction tx = HibernateUtil.getSession().beginTransaction();
                try {
                    new ReportExternalExportDAOImpl().insertReportExternalExport(report);
                    tx.commit();

                } catch (LIMSRuntimeException lre) {
                    tx.rollback();
                    LogEvent.logError("CommonReportPrintAction", "performAction()", Arrays.toString(lre.getStackTrace()));
                }
                
                //add to log history of print report
                try {
                    AuditTrailDAO auditDAO = new AuditTrailDAOImpl();
                    String sysUserId = currentUserId;                       
                    String tableName = "REPORT_EXTERNAL_EXPORT";
                    auditDAO.saveNewHistory(report, sysUserId, tableName);
                } catch (Exception e) {
                    LogEvent.logError("CommonReportPrintAction", "saveNewHistory()", e.toString());
                    throw new LIMSRuntimeException("Error in save history log report saveNewHistory()", e);
                }
            }
            
            //end           
			
		}

		if("patient".equals(request.getParameter("type"))) {
			trackReports( reportCreator, request.getParameter("report"), ReportType.PATIENT);
		}
		
		return mapping.findForward(forward);
	}

	private void trackReports(IReportCreator reportCreator, String reportName, ReportType reportType) {
		new ReportTrackingService().addReports(reportCreator.getReportedOrders(), reportType, reportName, currentUserId);
	}

	@Override
	protected String getPageSubtitleKey() {
		return "qaevent.add.title";
	}

	@Override
	protected String getPageTitleKey() {
		return "qaevent.add.title";
	}

	public String getReportPath() {
		if (reportPath == null) {
			reportPath = getServlet().getServletContext().getRealPath("") + "/WEB-INF/reports/";
		}
		return reportPath;
	}
	public String getImageUrl() {
		if (imageUrl == null) {
			imageUrl = getServlet().getServletContext().getRealPath("") + "\\tree_images\\Logo-Pasteur-Color.jpg";
		}
		return imageUrl;
	}
	
	/**
	 * nhuql.gv
	 * Set path for the image will display on the right of report
	 * @return
	 */
	public String getRightImageUrl() {
		if (rightImageUrl == null) {
			rightImageUrl = getServlet().getServletContext().getRealPath("") + "\\images\\pi_logo.png";
		}
		return rightImageUrl;
	}
}
