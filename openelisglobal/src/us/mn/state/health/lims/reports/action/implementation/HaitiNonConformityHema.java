package us.mn.state.health.lims.reports.action.implementation;

import java.util.HashMap;

import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;

public class HaitiNonConformityHema extends NonConformityHema implements IReportCreator{

	public HaitiNonConformityHema(){
		
	}
	
	@Override
	protected String getHeaderName() {
		return "GeneralHeader.jasper";
	}
	
	@Override
    protected void createReportParameters() throws IllegalStateException{
        super.createReportParameters();
        reportParameters.put( "supportStudy", "false" );
        reportParameters.put( "supportService", "false" );
        reportParameters.put( "supportSiteSubject", "false" );
        reportParameters.put( "labName1", ConfigurationProperties.getInstance().getPropertyValue( Property.SiteName ) );
        reportParameters.put( "labName2", "" );
    }

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
	}

}
