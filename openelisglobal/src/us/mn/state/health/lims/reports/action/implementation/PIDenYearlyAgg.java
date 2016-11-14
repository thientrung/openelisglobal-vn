package us.mn.state.health.lims.reports.action.implementation;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;

public class PIDenYearlyAgg extends Report implements IReportCreator {
	private String paramMenu;
	private String year;
	private String projectDengue;

	public PIDenYearlyAgg(String inParamMenu) {
		this.paramMenu = inParamMenu;
		this.isUseDataSource = false;
	}

	@Override
	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
		if (dynaForm.get("lowerYear") != null) {
			Object testIsolationList = (Object) dynaForm.get("lowerYear");
			year = testIsolationList.toString();
		}
		if (dynaForm.get("listProjectDengue") != null) {
			ParameterProjectDengue testIsolationList = (ParameterProjectDengue) dynaForm
					.get("listProjectDengue");
			projectDengue = testIsolationList.getSelection();
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
		if (ReportFileNameAndParams.Param_PIDenYearlyAgg.equals(paramMenu)) {
			reportFileName = ReportFileNameAndParams.Param_PIDenYearlyAgg;
		}
		return reportFileName;
	}

	@Override
	protected void createReportParameters() {
		super.createReportParameters();
		reportParameters.put("projectName", projectDengue);
		reportParameters.put("year", year);
	}
}
