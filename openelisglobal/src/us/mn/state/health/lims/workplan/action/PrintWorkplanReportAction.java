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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.workplan.action;


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRProperties;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.connection.PostgreSQLConnector;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.workplan.reports.ElisaWorkplanReport;
import us.mn.state.health.lims.workplan.reports.IWorkplanReport;
import us.mn.state.health.lims.workplan.reports.TestSectionWorkplanReport;
import us.mn.state.health.lims.workplan.reports.TestWorkplanReport;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class PrintWorkplanReportAction extends BaseAction {
	
	private final TestDAO testDAO = new TestDAOImpl();
	private static us.mn.state.health.lims.workplan.reports.IWorkplanReport workplanReport;
	private String reportPath;
			
	protected ActionForward performAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		//no forward necessary if successful, returning stream
		String forward = FWD_FAIL;
		ActionError error = null;
		ActionMessages errors = new ActionMessages();
		
		BaseActionForm dynaForm = (BaseActionForm) form;
		
		request.getSession().setAttribute(SAVE_DISABLED, "true");
		String type = request.getParameter("type");
		String workplanType = dynaForm.getString("workplanType");
		String workplanName, testSectionId, startedDate, completedDate, receivedDate;
		String panelId = "";
		String testId = "";
		
		if (workplanType.equals("test")) {
			String testID = (String) dynaForm.get("testTypeID");
			workplanName = getTestTypeName(testID);
			testId = dynaForm.getString("selectedSearchID");
		} else {
			workplanType = Character.toUpperCase(workplanType.charAt(0))+ workplanType.substring(1);
			workplanName = dynaForm.getString("testName");
			panelId = dynaForm.getString("selectedSearchID");
		}
		testSectionId = dynaForm.getString("testSectionId");
		receivedDate = dynaForm.getString("receivedDate");
		startedDate = dynaForm.getString("startedDate");
		completedDate = dynaForm.getString("completedDate");
		
		//get workplan report based on testName
		workplanReport = getWorkplanReport(workplanType, workplanName, testSectionId, testId, panelId, receivedDate, startedDate, completedDate);
		workplanReport.setReportPath(getReportPath());
				
		//set jasper report parameters
		HashMap<String, ?> parameterMap = workplanReport.getParameters();
						
		//prepare report
		List<?> workplanRows = workplanReport.prepareRows(dynaForm);
				
		//set Jasper report file name
		String reportFileName = "WorkPlan_Report";//workplanReport.getFileName();
		String reportFile = this.getServlet().getServletConfig().getServletContext().getRealPath("WEB-INF/reports/" + reportFileName + ".jasper");
						
	    try {
	        	
	        byte[] bytes = null;
			 
			//JRDataSource dataSource = createReportDataSource(workplanRows);
//			JRProperties.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");
//			JRProperties.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
//			JRProperties.setProperty("net.sf.jasperreports.default.pdf.embedded", "false");
			
			PostgreSQLConnector postgreSQLConnector = new PostgreSQLConnector();
			Connection con = postgreSQLConnector.getConnection();
			bytes = JasperRunManager.runReportToPdf(reportFile, parameterMap, con);

			//bytes = JasperRunManager.runReportToPdf(reportFile, parameterMap, dataSource);
			ServletOutputStream servletOutputStream = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/pdf");
			response.setContentLength(bytes.length);

			servletOutputStream.write(bytes, 0, bytes.length);
			servletOutputStream.flush();
			servletOutputStream.close();	
	
	    }
	    catch (JRException jre) {
	    	LogEvent.logError("PrintWorkplanReportAction","processRequest()", jre.toString());
	    	error = new ActionError("error.jasper", null, null);
	    } catch (Exception e) {
	    	LogEvent.logError("PrintWorkplanReportAction","processRequest()", e.toString());
	    	e.printStackTrace();
	    	error = new ActionError("error.jasper", null, null);
	    }
	    
	    if (error != null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, error);
			request.setAttribute(Globals.ERROR_KEY, errors);
			forward = FWD_FAIL;
      	}
	    
	    return mapping.findForward(forward);
		
	}

	private String getName(String workplanType) {
		String name = StringUtil.getContextualMessageForKey("test.section." + workplanType);
		
		if( name == null){
			name = workplanType;
		}
		return name;
	}
	
	private JRDataSource createReportDataSource(List<?> includedTests)
	{
		JRBeanCollectionDataSource dataSource;
		dataSource = new JRBeanCollectionDataSource(includedTests);

		return dataSource;
	}
	
		
	@Override
	protected String getPageSubtitleKey() {
		return "workplan.title";
	}

	@Override
	protected String getPageTitleKey() {
		return "workplan.title";
	}

	private String getTestTypeName(String id) {
		return TestService.getUserLocalizedTestName( id );
	}
	
	public IWorkplanReport getWorkplanReport(String testType, String name,
			String testSectionId, String testId, String panelId, String receivedDate, String startedDate, String completedDate) {
	      
    	IWorkplanReport workplan;
    	
    	if ("test".equals(testType)) {
    		//workplan = new TestWorkplanReport(name);
    		workplan = new TestSectionWorkplanReport(name, testSectionId, testId, panelId, receivedDate, startedDate, completedDate);
    	}else if ("Serology".equals(testType)) {
    		workplan = new ElisaWorkplanReport(name);
    	}else {
    		workplan = new TestSectionWorkplanReport(name, testSectionId, testId, panelId, receivedDate, startedDate, completedDate); 
    	}
       	
    	return workplan;
    }
	
	public String getReportPath() {
	    if (reportPath == null) {
	        reportPath = getServlet().getServletContext().getRealPath("") + File.separator + "WEB-INF" + File.separator + "reports" + File.separator;
	    }
        return reportPath;
    }

	
}
	