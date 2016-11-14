/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.common.services.historyservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import us.mn.state.health.lims.audittrail.action.workers.AuditTrailItem;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.ReportTrackingService;
import us.mn.state.health.lims.common.services.ReportTrackingService.ReportType;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dataexchange.aggregatereporting.daoimpl.ReportExternalExportDAOImpl;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.reports.valueholder.DocumentTrack;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.dataexchange.aggregatereporting.dao.ReportExternalExportDAO;
import us.mn.state.health.lims.dataexchange.aggregatereporting.valueholder.ReportExternalExport;

public class ReportHistoryService extends HistoryService {
	private static String REPORT_TABLE_ID;
	
	static {
		ReferenceTablesDAO tableDAO = new ReferenceTablesDAOImpl();
		REPORT_TABLE_ID = tableDAO.getReferenceTableByName("REPORT_EXTERNAL_EXPORT").getId();
	}
	
	public ReportHistoryService(Sample sample, String accessionNumber) {
		setUpForReport(sample,accessionNumber);
	}
	
	/*@SuppressWarnings("unchecked")
	private void setUpForReport(Sample sample) {
		List<DocumentTrack> documentList = new ReportTrackingService().getReportsForSample(sample, ReportType.PATIENT);

		historyList = new ArrayList<History>();
		for (DocumentTrack docTrack : documentList) {
			History searchHistory = new History();
			searchHistory.setReferenceId(docTrack.getId());
			searchHistory.setReferenceTable(REPORT_TABLE_ID);
			historyList.addAll(auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory));
		}
	}
*/
	@SuppressWarnings("unchecked")
    private void setUpForReport(Sample sample, String accessionNumber) {
	    ReportExternalExportDAO reportEExportDAO = new ReportExternalExportDAOImpl();
        List<ReportExternalExport> reportList = reportEExportDAO.getReportsByAccessionNumber(accessionNumber);
        
        historyList = new ArrayList<History>();
        for (ReportExternalExport reportEE : reportList) {
            History searchHistory = new History();
            searchHistory.setReferenceId(reportEE.getId());
            searchHistory.setReferenceTable(REPORT_TABLE_ID);
            historyList.addAll(auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory));
        }
    }
	
	/*@Override
	protected void addInsertion(History history, List<AuditTrailItem> items) {
		identifier = new ReportTrackingService().getDocumentForId(history.getReferenceId()).getDocumentName();
		AuditTrailItem item = getCoreTrail(history);
		items.add(item);
	}*/
	
	@Override
    protected void addInsertion(History history, List<AuditTrailItem> items) {
	    ReportExternalExportDAO reportEExportDAO = new ReportExternalExportDAOImpl();
	    ReportExternalExport reportEExport = reportEExportDAO.readReportExternalExport(history.getReferenceId());
	    
	    String data = reportEExport.getData();
	    // cut string to get report name
	    try {
	        String cutDataToGetReportName = data.substring(data.lastIndexOf(":") +1);
	        String getSubstringForReportName = StringUtils.substringBefore(cutDataToGetReportName,";");
	        identifier = getSubstringForReportName;
	    } catch(Exception e) {
	        LogEvent.logError("ReportHistoryService","addInsertion()",e.toString());
	        throw new LIMSRuntimeException("Error in addInsertion()", e);
	    }
	    
	    //end
        
        AuditTrailItem item = getCoreTrail(history);
        items.add(item);
    }

	@Override
	protected void getObservableChanges(History history, Map<String, String> changeMap, String changes) {
		String status = extractStatus(changes);
		if (status != null) {
			changeMap.put(STATUS_ATTRIBUTE, status);
		}
		String value = extractSimple(changes, "value");
		if (value != null) {
			changeMap.put(VALUE_ATTRIBUTE, value);
		}
	}
	
	@Override
	protected String getObjectName() {
		return StringUtil.getMessageForKey("banner.menu.reports");
	}

}
