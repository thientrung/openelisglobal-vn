package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.JgM03_BM_Patient;

public interface JgM03BMPatientDAO {
	public List<JgM03_BM_Patient> getAllJgM03BMPatient(String accessNumberFrom,
			String accessNumberTo, int testId, String organizationName);

	public List<JgM03_BM_Patient> getAllJgM03BMPatientDate(String dateFrom,
			String DateTo, int testId, String organizationName);

	public List<JgM03_BM_Patient> getAllJgM03BMPatientNumDate(
			String accessNumberFrom, String accessNumberTo, String datefrom,
			String dateTo, int testId, String organizationName);

	public List<JgM03_BM_Patient> getListData(String accessNumberFrom,
			String accessNumberTo, int testId, String lowerIllnessDateRange,
			String upperIllnessDateRange, String lowerDateRange,
			String upperDateRange, String lowerResultDateRange,
			String upperResultDateRange,String organizationId) throws LIMSRuntimeException;
}
