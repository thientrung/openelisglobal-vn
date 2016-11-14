package us.mn.state.health.lims.reports.action.implementation;

import java.util.HashMap;

import net.sf.jasperreports.engine.JRDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import vi.mn.state.health.lims.report.common.ReportFileNameAndParams;

public class PIProcessing extends Report implements IReportCreator {
	private String paramMenu;
	
	public PIProcessing(String inParamMenu) {
        this.paramMenu = inParamMenu;
        this.isUseDataSource = false;
    }
	
	@Override
	public void initializeReport(BaseActionForm dynaForm) {
		super.initializeReport();
		if (dynaForm.get("accessionDirect") != null) {
			Object obYear = (Object) dynaForm.get("accessionDirect");
			startAccessionNumber = obYear.toString();
		}
		if (dynaForm.get("highAccessionDirect") != null) {
			Object obMonth = (Object) dynaForm.get("highAccessionDirect");
			endAccessionNumber = obMonth.toString();
		}
		try {
			if (dynaForm.get("lowerDateRange") != null) {
				Object startDate = (Object) dynaForm.get("lowerDateRange");
				//startReceivedDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate.toString());
				startReceivedDate = startDate.toString();
			}
			if (dynaForm.get("upperDateRange") != null) {
				Object endDate = (Object) dynaForm.get("upperDateRange");
				//endReceivedDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate.toString());
				endReceivedDate = endDate.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		if (ReportFileNameAndParams.Param_PIProcessing.equals(paramMenu)) {
			reportFileName = ReportFileNameAndParams.Report_PIProcessing;
        }
        return reportFileName;
	}

}
