package us.mn.state.health.lims.reports.action.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.SystemConfiguration;
import us.mn.state.health.lims.reports.action.implementation.Report.DateRange;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;
import vi.mn.state.health.lims.report.dao.ReportPIDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportPIDAOImpl;
import vi.mn.state.health.lims.report.valueholder.PIDengueAggregate;;

public class PIDengueMonthlyAggregate extends Report implements IReportCreator {

	private String month;
	private String year;
	private String paramMenu;
	
	public PIDengueMonthlyAggregate(String inParamMenu) {
        this.paramMenu = inParamMenu;
		this.isUseDataSource = false;
    }
	
	@Override
	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
        try {
        	month = dynaForm.getString("lowerMonth");
        	year = dynaForm.getString("lowerYear");
        	if (dynaForm.get("listProjectDengue") != null) {
        		ParameterProjectDengue selection = (ParameterProjectDengue) dynaForm.get("listProjectDengue");
                projectDengue = selection.getSelection();
            }
        	
        	setMonth(Integer.parseInt(month));
        	setYear(Integer.parseInt(year));
        	createReportParameters();
            // listReportData = jgM03BMPatientDAO.getAllJgM03BMPatientDate(lowDateStr, highDateStr);
        } catch (Exception e) {
            e.printStackTrace();

        }
	}

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
	}

	@Override
	public JRDataSource getReportDataSource() throws IllegalStateException {
        return null;
	}

	@Override
	protected String reportFileName() {
		String reportFileName = "";
		if (ReportFileNameAndParams.Param_DengueMonthlyAggregate.equals(paramMenu)) {
			reportFileName = ReportFileNameAndParams.Report_DengueMonthlyAggregate;
        }
        return reportFileName;
	}

}
