package us.mn.state.health.lims.reports.action.implementation;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;

public class PIDenAggByCity extends Report implements IReportCreator {
	private String paramMenu;
	private String year;
	private String month;

	public PIDenAggByCity(String inParamMenu) {
		this.paramMenu = inParamMenu;
		this.isUseDataSource = false;
	}

	@Override
	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
		if (dynaForm.get("lowerYear") != null) {
			Object obYear = (Object) dynaForm.get("lowerYear");
			year = obYear.toString();
		}
		if (dynaForm.get("lowerMonth") != null) {
			Object obMonth = (Object) dynaForm.get("lowerMonth");
			month = obMonth.toString();
		}
		if (dynaForm.get("listProjectDengue") != null) {
    		ParameterProjectDengue selection = (ParameterProjectDengue) dynaForm.get("listProjectDengue");
            projectDengue = selection.getSelection();
        }
		createReportParameters();
	}

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();

	}

	@Override
	public JRDataSource getReportDataSource() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String reportFileName() {
		String reportFileName = "";
		if (ReportFileNameAndParams.Param_PIDenAggByCity.equals(paramMenu)) {
			reportFileName = ReportFileNameAndParams.Param_PIDenAggByCity;
		}
		return reportFileName;
	}

	@Override
	protected void createReportParameters() {
		super.createReportParameters();
		reportParameters.put("month", month);
		reportParameters.put("year", year);
	}
}
