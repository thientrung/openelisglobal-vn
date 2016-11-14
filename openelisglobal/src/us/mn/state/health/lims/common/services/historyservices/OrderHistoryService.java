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

import us.mn.state.health.lims.audittrail.action.workers.AuditTrailItem;
import us.mn.state.health.lims.audittrail.valueholder.History;
import us.mn.state.health.lims.common.services.PersonService;
import us.mn.state.health.lims.common.services.RequesterService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.referencetables.dao.ReferenceTablesDAO;
import us.mn.state.health.lims.referencetables.daoimpl.ReferenceTablesDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.common.util.DateUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryService extends HistoryService {
	private static final String SAMPLE_TABLE_ID;
    private static final String SAMPLE_REQUESTER_TABLE_ID;

	private static final String ACCESSION_ATTRIBUTE = "accessionNumber";
	private static final String STATUS_ATTRIBUTE = "status";
    private static final String ORGANIZATION_ATTRIBUTE = "requestingOrganization";
    private static final String PROVIDER_ATTRIBUTE = "requestingProvider";
    private static final String ONSET_OF_DATE_ATTRIBUTE = "onsetOfDate";
    private static final String RECEIVED_DATE_ATTRIBUTE = "receivedDate";
    private static final String RECEIVED_TIME_ATTRIBUTE = "receivedTime";
	
	static {
		ReferenceTablesDAO tableDAO = new ReferenceTablesDAOImpl();
		SAMPLE_TABLE_ID = tableDAO.getReferenceTableByName("SAMPLE").getId();
        SAMPLE_REQUESTER_TABLE_ID = tableDAO.getReferenceTableByName( "SAMPLE_REQUESTER" ).getId();
	}

    private String currentProviderRequestLinkId;

    private String currentOrganizationRequesterLinkId;
    private SampleRequester requester;

    public OrderHistoryService(Sample sample) {
		setUpForOrder( sample );
	}
	
	@SuppressWarnings("unchecked")
	private void setUpForOrder(Sample sample) {
		identifier = sample.getAccessionNumber();

        attributeToIdentifierMap = new HashMap<String, String>();
        attributeToIdentifierMap.put(ORGANIZATION_ATTRIBUTE, StringUtil.getMessageForKey( "organization.organization" ));
        attributeToIdentifierMap.put(PROVIDER_ATTRIBUTE, StringUtil.getContextualMessageForKey( "nonconformity.provider.label" ));
        attributeToIdentifierMap.put(ACCESSION_ATTRIBUTE, StringUtil.getContextualMessageForKey( "quick.entry.accession.number" ));
        attributeToIdentifierMap.put(STATUS_ATTRIBUTE, StringUtil.getContextualMessageForKey( "status" ));
        attributeToIdentifierMap.put(ONSET_OF_DATE_ATTRIBUTE, StringUtil.getContextualMessageForKey( "quick.entry.onset.date" ));
        attributeToIdentifierMap.put(RECEIVED_DATE_ATTRIBUTE, StringUtil.getContextualMessageForKey( "quick.entry.received.date" ));
        attributeToIdentifierMap.put(RECEIVED_TIME_ATTRIBUTE, StringUtil.getContextualMessageForKey( "quick.entry.received.date" ));

        History searchHistory = new History();
		searchHistory.setReferenceId(sample.getId());
		searchHistory.setReferenceTable(SAMPLE_TABLE_ID);
		historyList = auditTrailDAO.getHistoryByRefIdAndRefTableId(searchHistory);

        newValueMap = new HashMap<String, String>();
        addReferrerHistory( sample, searchHistory );


		newValueMap.put(STATUS_ATTRIBUTE, StatusService.getInstance().getStatusNameFromId(sample.getStatusId()));
		newValueMap.put(ACCESSION_ATTRIBUTE, sample.getAccessionNumber());
		
        if (sample.getOnsetOfDate() != null) {
            String onsetDate = sample.getOnsetOfDate().toString().split(" ")[0];
            Timestamp ts = DateUtil.convertStringDateToTimestampWithPattern(onsetDate, "yyyy-MM-dd");
            onsetDate = DateUtil.convertTimestampToStringDate(ts);
            newValueMap.put(ONSET_OF_DATE_ATTRIBUTE, onsetDate);
        } else {
            newValueMap.put(ONSET_OF_DATE_ATTRIBUTE, "");
        }
        
		newValueMap.put(RECEIVED_DATE_ATTRIBUTE, sample.getReceivedDate() != null ? sample.getReceivedDateForDisplay() + " "
	        + sample.getReceived24HourTimeForDisplay() : "");

	}

    private void addReferrerHistory( Sample sample,  History searchHistory ){
        RequesterService requesterService = new RequesterService( sample.getId() );

        requester = requesterService.getSampleRequesterByType( RequesterService.Requester.ORGANIZATION , false);
        if( requester != null ){
            searchHistory.setReferenceId( requester.getId() );
            searchHistory.setReferenceTable( SAMPLE_REQUESTER_TABLE_ID );
            List<History> list = auditTrailDAO.getHistoryByRefIdAndRefTableId( searchHistory );
            historyList.addAll( list );
            newValueMap.put( ORGANIZATION_ATTRIBUTE, requesterService.getReferringSiteName() );
            currentOrganizationRequesterLinkId = requester.getId();
        }

        requester = requesterService.getSampleRequesterByType( RequesterService.Requester.PERSON , false);
        if( requester != null){
                searchHistory.setReferenceId( requester.getId() );
                searchHistory.setReferenceTable( SAMPLE_REQUESTER_TABLE_ID);
                List<History> list = auditTrailDAO.getHistoryByRefIdAndRefTableId( searchHistory );
                historyList.addAll( list );
            newValueMap.put(PROVIDER_ATTRIBUTE, requesterService.getRequesterLastFirstName());
                currentProviderRequestLinkId = requester.getId();
        }
    }

    @Override
	protected void addInsertion(History history, List<AuditTrailItem> items) {
        AuditTrailItem item = getCoreTrail( history );
        if( history.getReferenceTable().equals( SAMPLE_REQUESTER_TABLE_ID)){
             if( history.getReferenceId( ).equals( currentProviderRequestLinkId )){
                 item.setIdentifier( StringUtil.getMessageForKey( "provider.browse.title" ) );
                 item.setNewValue( newValueMap.get(PROVIDER_ATTRIBUTE) );
             }else if( history.getReferenceId().equals( currentOrganizationRequesterLinkId )){
                 item.setIdentifier( StringUtil.getMessageForKey( "organization.organization" ) );
                 item.setNewValue( newValueMap.get(ORGANIZATION_ATTRIBUTE) );
             }
        } else if (history.getReferenceTable().equals( SAMPLE_TABLE_ID )) {
            // When reference table of history is not sample requester, organization
            // And It equals sample table id (1)
            // We set identifier with accession_attribute again.
            item.setIdentifier( attributeToIdentifierMap.get( ACCESSION_ATTRIBUTE ));
            item.setNewValue( newValueMap.get( ACCESSION_ATTRIBUTE ));
        }
        if (!StringUtil.isNullorNill(item.getNewValue())) {
            items.add( item );
        }
	}

	@Override
	protected void getObservableChanges(History history, Map<String, String> changeMap, String changes) {
		String status = extractStatus(changes);
		if (status != null) {
			changeMap.put(STATUS_ATTRIBUTE, status);
		}
		simpleChange(changeMap, changes, ACCESSION_ATTRIBUTE);

        if ( history.getReferenceTable().equals( SAMPLE_REQUESTER_TABLE_ID )) {
            if( history.getReferenceId( ).equals( currentProviderRequestLinkId )){
                String value = extractSimple(changes, "requesterId");
                if (value != null) {
                    PersonService personService = new PersonService( new PersonDAOImpl().getPersonById( value ) );
                    changeMap.put( PROVIDER_ATTRIBUTE, personService.getLastFirstName() != null ? personService.getLastFirstName() : "");
                }
            } else if ( history.getReferenceId().equals( currentOrganizationRequesterLinkId )){
                String value = extractSimple(changes, "requesterId");

                if (value != null) {
                    changeMap.put(ORGANIZATION_ATTRIBUTE, new OrganizationDAOImpl().getOrganizationById( value ).getOrganizationName());
                }
            }
        }
        // When onsetDate, receivedDate, receivedTime of sample were changed
        if ( history.getReferenceTable().equals( SAMPLE_TABLE_ID )) {
            String value = extractSimple(changes, "onsetOfDate");
            if (value != null) {
                String date[] = value.split(" ");
                Timestamp ts = DateUtil.convertStringDateToTimestampWithPattern(date[0], "yyyy-MM-dd");
                String onsetDate = DateUtil.convertTimestampToStringDate(ts);
                changeMap.put(ONSET_OF_DATE_ATTRIBUTE, onsetDate);
            }
            value = extractSimple(changes, "receivedDateForDisplay");
            if (!StringUtil.isNullorNill(value)) {
                value += extractSimple(changes, "receivedTimeForDisplay") != null ? 
                        (" " + extractSimple(changes, "receivedTimeForDisplay")) : "";  
                changeMap.put(RECEIVED_DATE_ATTRIBUTE, value);
            } else {
                value = extractSimple(changes, "receivedTimeForDisplay");
                if (!StringUtil.isNullorNill(value)) {
                    changeMap.put(RECEIVED_TIME_ATTRIBUTE, value);
                }
            }
        }

	}
	@Override
	protected String getObjectName() {
		return StringUtil.getMessageForKey("auditTrail.order");
	}

	@Override
	protected boolean showAttribute() {
		return false;
	}
}
