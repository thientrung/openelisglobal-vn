package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.ReportResultHema;

public interface ReportResultHemaDAO {
	public List<ReportResultHema> getListReportData() throws LIMSRuntimeException;
}
