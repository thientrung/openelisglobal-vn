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
package us.mn.state.health.lims.audittrail.action.workers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.address.dao.AddressPartDAO;
import us.mn.state.health.lims.address.dao.PersonAddressDAO;
import us.mn.state.health.lims.address.daoimpl.AddressPartDAOImpl;
import us.mn.state.health.lims.address.daoimpl.PersonAddressDAOImpl;
import us.mn.state.health.lims.address.valueholder.AddressPart;
import us.mn.state.health.lims.address.valueholder.PersonAddress;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.SampleOrderService;
import us.mn.state.health.lims.common.services.historyservices.AnalysisHistoryService;
import us.mn.state.health.lims.common.services.historyservices.HistoryService;
import us.mn.state.health.lims.common.services.historyservices.NoteHistoryService;
import us.mn.state.health.lims.common.services.historyservices.OrderHistoryService;
import us.mn.state.health.lims.common.services.historyservices.PatientHistoryHistoryService;
import us.mn.state.health.lims.common.services.historyservices.PatientHistoryService;
import us.mn.state.health.lims.common.services.historyservices.QaHistoryService;
import us.mn.state.health.lims.common.services.historyservices.ReportHistoryService;
import us.mn.state.health.lims.common.services.historyservices.ResultHistoryService;
import us.mn.state.health.lims.common.services.historyservices.SampleHistoryService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.patient.util.PatientUtil;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.patienttype.dao.PatientPatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientPatientTypeDAOImpl;
import us.mn.state.health.lims.patienttype.valueholder.PatientType;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;

public class AuditTrailViewWorker {

	private AnalysisDAO analysisDAO = new AnalysisDAOImpl();
	private ResultDAO resultDAO = new ResultDAOImpl();
	private String accessionNumber = null;
	private Sample sample;
    private static PersonAddressDAO addressDAO = new PersonAddressDAOImpl();
    private static String ADDRESS_PART_VILLAGE_ID;
    private static String ADDRESS_PART_COMMUNE_ID;
    private static String ADDRESS_PART_DEPT_ID;
    private static String ADDRESS_PART_WARD_ID;
    private static String ADDRESS_PART_DISTRICT_ID;

    static {
        AddressPartDAO addressPartDAO = new AddressPartDAOImpl();
        List<AddressPart> partList = addressPartDAO.getAll();

        for (AddressPart addressPart : partList) {
            if ("department".equals(addressPart.getPartName())) {
                ADDRESS_PART_DEPT_ID = addressPart.getId();
            } else if ("commune".equals(addressPart.getPartName())) {
                ADDRESS_PART_COMMUNE_ID = addressPart.getId();
            } else if ("village".equals(addressPart.getPartName())) {
                ADDRESS_PART_VILLAGE_ID = addressPart.getId();
            } else if ("ward".equals(addressPart.getPartName())) {
                ADDRESS_PART_WARD_ID = addressPart.getId();
            } else if ("district".equals(addressPart.getPartName())) {
                ADDRESS_PART_DISTRICT_ID = addressPart.getId();
            }
        }
    }

	public AuditTrailViewWorker(String accessionNumber) {
		this.accessionNumber = accessionNumber;
		sample = null;
	}

	public List<AuditTrailItem> getAuditTrail() throws IllegalStateException {
		if (GenericValidator.isBlankOrNull(accessionNumber)) {
			throw new IllegalStateException("AuditTrialViewWorker is not initialized");
		}

        getSample();

        List<AuditTrailItem> items = new ArrayList<AuditTrailItem>();

		if (sample != null) {
			items.addAll(addOrders());	
			items.addAll(addSamples());	
			items.addAll(addTestsAndResults());
			items.addAll(addReports());
			items.addAll(addPatientHistory());
			items.addAll(addNotes());
			items.addAll(addQAEvents());
		}
		
		sortItemsByTime(items);
		return items;
	}

    public SampleOrderItem getSampleOrderSnapshot() {
        if (GenericValidator.isBlankOrNull(accessionNumber)) {
            throw new IllegalStateException("AuditTrialViewWorker is not initialized");
        }
        SampleOrderService orderService = new SampleOrderService( accessionNumber, true );
        
        return orderService.getSampleOrderItem();
    }

    public PatientManagementInfo getPatientSnapshot() {
        if (GenericValidator.isBlankOrNull(accessionNumber)) {
            throw new IllegalStateException("AuditTrialViewWorker is not initialized");
        }
        getSample();
        
        if (sample != null) {
            //PatientService patientService = new PatientService( sample );
            //return new PatientManagementBridge().getPatientManagementInfoFor( patientService.getPatient(), true );
            return patientManagementInfo(sample, true);
        } else {
            return new PatientManagementInfo();
        }
    }
    
	public List<AuditTrailItem> getPatientHistoryAuditTrail() throws IllegalStateException{
		if (GenericValidator.isBlankOrNull(accessionNumber)) {
			throw new IllegalStateException("AuditTrialViewWorker is not initialized");
		}
        getSample();

        List<AuditTrailItem> items = new ArrayList<AuditTrailItem>();
		if (sample != null) {
			items.addAll(addPatientHistory());
		}
		
		return items;
	}
	
	private PatientManagementInfo patientManagementInfo(Sample sample, boolean readOnly) {
        try {
            PatientManagementInfo patientManagementInfo = new PatientManagementInfo();
            // get sample human
            Patient patient = new Patient();
            SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
            patient = sampleHumanDAO.getPatientForSample(sample);
            
            // set information for patient
            patientManagementInfo.setFirstName(patient.getPerson()
                    .getFirstName());
            patientManagementInfo.setBirthDateForDisplay(patient
                    .getBirthDateForDisplay());
            patientManagementInfo.setStreetAddress(patient.getPerson()
                    .getStreetAddress());
            patientManagementInfo.setGender(patient.getGender());
            patientManagementInfo.setExternalId(patient.getExternalId());
            patientManagementInfo.setChartNumber(patient.getChartNumber());
            patientManagementInfo.setAddressWard(getAddress(
                    patient.getPerson(), ADDRESS_PART_WARD_ID));
            patientManagementInfo.setAddressDistrict(getAddress(
                    patient.getPerson(), ADDRESS_PART_DISTRICT_ID));
            patientManagementInfo.setCity(getAddress(patient.getPerson(),
                    ADDRESS_PART_VILLAGE_ID));
            //In case do not use department, then comment the following code
            /*Get Department for the patient from observation history*/
            /*if (patient.getId() != null) {
                ObservationHistory observationHistory = new ObservationHistory();
                ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
                   observationHistory = observationHistoryDAO
                           .getObservationHistoriesByPatientIdAndTypeOnlyOne(
                                   patient.getId(), "9");
                   if (observationHistory != null) {
                       // used the dept_id [observationhistory.getValue() for dropdown type]
                       patientManagementInfo.setAddressDepartment(observationHistory.getValue());
                   }
            }*/
            
            patientManagementInfo.setPatientType(getPatientType(patient));
             /*Get age unit for the patient from observation history*/
            if (patient.getId() != null) {
                ObservationHistory observationHistory = new ObservationHistory();
                ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
                   observationHistory = observationHistoryDAO
                           .getObservationHistoriesByPatientIdAndTypeOnlyOne(
                                   patient.getId(), "11");
                   if (observationHistory != null) {
                       patientManagementInfo.setPatientAgeUnit(observationHistory.getValue());
                   }
            }
            /*End get*/
            // set patient readOnly property
            patientManagementInfo.setReadOnly(readOnly);
            
            return patientManagementInfo;
        } catch (LIMSRuntimeException ex) {
            LogEvent.logError("patientManagementInfo",
                    "patientManagementInfo()", ex.getMessage());
            return null;
        }
    }
	
	private String getAddress(Person person, String addressPartId) {
        if (GenericValidator.isBlankOrNull(addressPartId)) {
            return "";
        }
        PersonAddress address = addressDAO.getByPersonIdAndPartId(person.getId(), addressPartId);

        return address != null ? address.getValue() : "";
    }

	private String getPatientType(Patient patient) {
        PatientPatientTypeDAO patientPatientTypeDAO = new PatientPatientTypeDAOImpl();
        PatientType patientType = patientPatientTypeDAO.getPatientTypeForPatient(patient.getId());

        return patientType != null ? patientType.getType() : null;
    }
	
    private void getSample(){
        if( sample == null ){
            sample = new SampleDAOImpl().getSampleByAccessionNumber(accessionNumber);
        }
    }

    private Collection<AuditTrailItem> addReports() {
		List<AuditTrailItem> items = new ArrayList<AuditTrailItem>();
		
		if (sample != null) {
			HistoryService historyService = new ReportHistoryService(sample,accessionNumber);
			items.addAll(historyService.getAuditTrailItems());

			//sortItemsByTime(items);
		}
		
		for( AuditTrailItem auditTrailItem : items){
			auditTrailItem.setClassName("reportAudit");
			setAttributeNewIfInsert(auditTrailItem);
		}
		return items;
	}

	private Collection<AuditTrailItem> addSamples() {
		List<AuditTrailItem> sampleItems = new ArrayList<AuditTrailItem>();
		if (sample != null) {
			HistoryService historyService = new SampleHistoryService(sample);
			sampleItems.addAll(historyService.getAuditTrailItems());

			//sortItems(sampleItems);
			
			for( AuditTrailItem auditTrailItem : sampleItems){
				auditTrailItem.setClassName("sampleAudit");
				setAttributeNewIfInsert(auditTrailItem);
			}
		}
		
		return sampleItems;
	}

	private Collection<AuditTrailItem> addOrders() {
		List<AuditTrailItem> orderItems = new ArrayList<AuditTrailItem>();
		if (sample != null) {
			HistoryService historyService = new OrderHistoryService(sample);
			orderItems.addAll(historyService.getAuditTrailItems());

			//sortItems(orderItems);
			
			for( AuditTrailItem auditTrailItem : orderItems){
				auditTrailItem.setClassName("orderAudit");
				setAttributeNewIfInsert(auditTrailItem);
			}
		}
		
		return orderItems;
	}

	private void setAttributeNewIfInsert(AuditTrailItem auditTrailItem) {
		if (auditTrailItem.getAction().equals("I")) {
			auditTrailItem.setAttribute(StringUtil.getMessageForKey("auditTrail.action.new"));
		}
		if (auditTrailItem.getAttribute().equals("status")) {
            auditTrailItem.setAttribute(StringUtil.getMessageForKey("auditTrail.action.status"));
        }
		if (auditTrailItem.getNewValue() != null) {
	        if (auditTrailItem.getNewValue().equals("SampleCanceled")) {
	            auditTrailItem.setNewValue(StringUtil.getMessageForKey("sample.status.canceled"));
	        } else if (auditTrailItem.getNewValue().equals("SampleEntered")) {
	            auditTrailItem.setNewValue(StringUtil.getMessageForKey("sample.status.entered"));
	        }
		}

	}

	private List<AuditTrailItem> addTestsAndResults() {
		List<AuditTrailItem> items = new ArrayList<AuditTrailItem>();

		List<Analysis> analysisList = analysisDAO.getAnalysesBySampleId(sample.getId());

		for (Analysis analysis : analysisList) {
			List<Result> resultList = resultDAO.getResultsByAnalysis(analysis);
			HistoryService historyService = new AnalysisHistoryService(analysis);
			List<AuditTrailItem> resultItems = historyService.getAuditTrailItems();
			items.addAll(resultItems);
			
			for (Result result : resultList) {
				historyService = new ResultHistoryService(result, analysis);
				resultItems = historyService.getAuditTrailItems();
				
				items.addAll(resultItems);
			}
		}

		//sortItems(items);
		for( AuditTrailItem auditTrailItem : items){
			auditTrailItem.setClassName("testResultAudit");
			setAttributeNewIfInsert(auditTrailItem);
		}
		return items;
	}

	private Collection<AuditTrailItem> addPatientHistory() {	
		List<AuditTrailItem> items = new ArrayList<AuditTrailItem>();
		HistoryService historyService;
		Patient patient = PatientUtil.getPatientForSample(sample);
		if( patient != null) {
			historyService = new PatientHistoryService(patient);
			items.addAll(historyService.getAuditTrailItems());
		}
		
//		historyService = new HistoryService(sample, HistoryType.PERSON);
//		items.addAll(historyService.getAuditTrailItems());
		historyService = new PatientHistoryHistoryService(sample);
		items.addAll(historyService.getAuditTrailItems());
		
		//sortItems(items);
		for( AuditTrailItem auditTrailItem : items){
			auditTrailItem.setClassName("patientHistoryAudit");
			setAttributeNewIfInsert(auditTrailItem);
		}
		if (patient.getPerson().getId().equals(String.valueOf(IActionConstants.UNKNOWN_PERSON_ID))) {
		    items = new ArrayList<AuditTrailItem>();
		}
		
		return items;
	}
	
	private Collection<AuditTrailItem> addNotes() {
		List<AuditTrailItem> notes = new ArrayList<AuditTrailItem>();
		if (sample != null) {
			HistoryService historyService = new NoteHistoryService(sample);
			notes.addAll(historyService.getAuditTrailItems());

			//sortItems(notes);
			
			for( AuditTrailItem auditTrailItem : notes){
				auditTrailItem.setClassName("noteAudit");
				setAttributeNewIfInsert(auditTrailItem);
			}
		}
		
		return notes;
	}
	
	private Collection<AuditTrailItem> addQAEvents() {
		List<AuditTrailItem> qaEvents = new ArrayList<AuditTrailItem>();
		if (sample != null) {
			QaHistoryService qaService = new QaHistoryService(sample);
			qaEvents = qaService.getAuditTrailItems();
			
			for( AuditTrailItem auditTrailItem : qaEvents){
				auditTrailItem.setClassName("qaEvent");
				setAttributeNewIfInsert(auditTrailItem);
			}
		}
		
		return qaEvents;
	}

	private void sortItems(List<AuditTrailItem> items) {
		Collections.sort(items, new Comparator<AuditTrailItem>() {
			@Override
			public int compare(AuditTrailItem o1, AuditTrailItem o2) {
				int sort = o1.getIdentifier().compareTo(o2.getIdentifier());
				if (sort != 0) {
					return sort;
				}

				sort = o1.getTimeStamp().compareTo(o2.getTimeStamp());
				if (sort != 0) {
					return sort;
				}
				
				return o1.getAction().compareTo(o2.getAction());
			}
		});
	}
	
	private void sortItemsByTime(List<AuditTrailItem> items) {
		Collections.sort(items, new Comparator<AuditTrailItem>() {
			@Override
			public int compare(AuditTrailItem o1, AuditTrailItem o2) {
				return o1.getTimeStamp().compareTo(o2.getTimeStamp());
			}
		});
	}
}
