package vi.mn.state.health.lims.report.dao;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.report.valueholder.PIDengueAggregate;;

public interface ReportPIDAO {
	public List<PIDengueAggregate> getDengueAggregateRecord(String month,String year);
	
	 //new added
    public List getListOfSatisfiedAccessionNumber(String fromAccessionNumber, String toAccessionNumber, String fromStartedIllnessDate, String toStartedIllnessDate, String fromRecievedDate, String toRecievedDate, String fromResultDate, String toResultDate, String testId, String organizationID) throws LIMSRuntimeException;
}
