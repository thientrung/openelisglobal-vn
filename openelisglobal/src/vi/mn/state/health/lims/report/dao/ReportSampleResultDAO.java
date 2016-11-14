package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.ReportSampleResult;

public interface ReportSampleResultDAO {
	public List<ReportSampleResult> getAllSamplePersion(String accessNumber) throws LIMSRuntimeException;
}
