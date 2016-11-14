package us.mn.state.health.lims.reports.action.implementation;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.StringUtil;
import vi.mn.state.health.lims.report.dao.ReportResultHemaDAO;
import vi.mn.state.health.lims.report.daoimpl.ReportResultHemaDAOImpl;
import vi.mn.state.health.lims.report.valueholder.ReportResultHema;

public abstract class NonConformityHema extends Report implements IReportCreator {

	private String lowDateStr;
	private String highDateStr;
	private DateRange dateRange;
	private List<ReportResultHema> reportItems;
	private ReportResultHemaDAO reportResultHemaDAO = new ReportResultHemaDAOImpl();
	
	@Override
	protected void createReportParameters() throws IllegalStateException {
		super.createReportParameters();
		String nonConformity = StringUtil.getContextualMessageForKey("banner.menu.nonconformity");
		reportParameters.put("status", nonConformity);
		reportParameters.put("reportTitle", nonConformity);
		reportParameters.put("reportPeriod", StringUtil.getContextualMessageForKey("banner.menu.nonconformity") + "  " + dateRange.toString());
	    reportParameters.put("supervisorSignature", ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SIGNATURES_ON_NONCONFORMITY_REPORTS, "true"));
		if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "CI LNSP")){
			reportParameters.put("headerName", "CILNSPHeader.jasper");	
		} else {
			reportParameters.put("headerName", getHeaderName());
		}
	}
	
	@Override
	public void initializeReport(BaseActionForm dynaForm) {
        super.initializeReport();
        lowDateStr = dynaForm.getString("lowerDateRange");
        highDateStr = dynaForm.getString("upperDateRange");
        dateRange = new DateRange(lowDateStr, highDateStr);
        
        createReportParameters();
        errorFound = !validateSubmitParameters();
        if ( errorFound ) {
            return;
        }
        reportItems = new ArrayList<ReportResultHema>();

        createReportItems();
        if ( this.reportItems.size() == 0 ) {
            add1LineErrorMessage("report.error.message.noPrintableItems");
        }
	}

	/**
    *
    */
   private void createReportItems() {
	   reportItems = reportResultHemaDAO.getListReportData();
   }

	@Override
	public JRDataSource getReportDataSource() throws IllegalStateException {
       return errorFound ? new JRBeanCollectionDataSource(errorMsgs) : new JRBeanCollectionDataSource(reportItems);
	}

	   /**
    * check everything
    */
   private boolean validateSubmitParameters() {
       return (dateRange.validateHighLowDate("report.error.message.date.received.missing") );
   }


   @Override
	protected String reportFileName(){
		return "D2_Hema_Logbook";
	}

   protected abstract String getHeaderName();

}
