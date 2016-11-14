/*
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
 */

package us.mn.state.health.lims.common.services;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.organization.dao.OrganizationDAO;
import us.mn.state.health.lims.organization.daoimpl.OrganizationDAOImpl;
import us.mn.state.health.lims.organization.valueholder.Organization;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.project.dao.ProjectDAO;
import us.mn.state.health.lims.project.daoimpl.ProjectDAOImpl;
import us.mn.state.health.lims.project.valueholder.Project;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleproject.dao.SampleProjectDAO;
import us.mn.state.health.lims.sampleproject.daoimpl.SampleProjectDAOImpl;
import us.mn.state.health.lims.sampleproject.valueholder.SampleProject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static us.mn.state.health.lims.common.services.ObservationHistoryService.ObservationType;
import static us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory.ValueType;

/**
 */
public class SampleOrderService{
    private static final SampleDAO sampleDAO = new SampleDAOImpl();
    private static final OrganizationDAO orgDAO = new OrganizationDAOImpl();
    private static final ProjectDAO projDAO = new ProjectDAOImpl();
	private static final SampleProjectDAO sampleProjectDAO = new SampleProjectDAOImpl();
    private boolean needRequesterList = FormFields.getInstance().useField( FormFields.Field.RequesterSiteList );
    private boolean needPaymentOptions = ConfigurationProperties.getInstance().isPropertyValueEqual( ConfigurationProperties.Property.TRACK_PATIENT_PAYMENT, "true" );
    private boolean needClinicalDeptOptions = FormFields.getInstance().useField( FormFields.Field.SAMPLE_ENTRY_PATIENT_CLINICAL_DEPT );
    private boolean needTestLocationCode = FormFields.getInstance().useField( FormFields.Field.TEST_LOCATION_CODE );
    private SampleOrderItem sampleOrder;
    private Sample sample;
    private boolean readOnly = false;


    public SampleOrderService(){
        sample = null;
    }

    public SampleOrderService( Sample sample ){
        this.sample = sample;
    }

    public SampleOrderService( SampleOrderItem sampleOrder ){
        this.sampleOrder = sampleOrder;
    }

    public SampleOrderService(String accessionNumber, boolean readOnly){
        this.sample = sampleDAO.getSampleByAccessionNumber( accessionNumber );
        this.readOnly = readOnly;
    }


    private SampleOrderItem getBaseSampleOrderItem(){
        SampleOrderItem orderItems = new SampleOrderItem();

        String dateAsText = DateUtil.getCurrentDateAsText();

        orderItems.setReceivedDateForDisplay( dateAsText );
        orderItems.setRequestDate( dateAsText );

        if( needRequesterList ){
            orderItems.setReferringSiteList( DisplayListService.getFreshList( DisplayListService.ListType.SAMPLE_PATIENT_REFERRING_CLINIC ) );
        }

        if( needPaymentOptions ){
            orderItems.setPaymentOptions( DisplayListService.getList( DisplayListService.ListType.SAMPLE_PATIENT_PAYMENT_OPTIONS ) );
        }

        if( needClinicalDeptOptions ){
            orderItems.setClinicalDeptOptions( DisplayListService.getDictionaryListByCategory( "patientClinicalDept" ) );
        }

        if( needTestLocationCode){
            orderItems.setTestLocationCodeList( DisplayListService.getList( DisplayListService.ListType.TEST_LOCATION_CODE) );
        }

        if( ConfigurationProperties.getInstance().isPropertyValueEqual( ConfigurationProperties.Property.ORDER_PROGRAM, "true" )){
            orderItems.setProgramList( DisplayListService.getList( DisplayListService.ListType.PROGRAM ) );
        }

        return orderItems;
    }

    /**
     * If this service is created with the default no parameter constructor then this will
     * generate a SampleOrderItem populated with the appropriate lists but no other values.
     *
     * @return The SampleOrderItem
     */
    public SampleOrderItem getSampleOrderItem(){
        sampleOrder = getBaseSampleOrderItem();

        if( sample != null ){
            SampleService sampleService = new SampleService( sample );
            sampleOrder.setSampleId( sample.getId() );
            sampleOrder.setLabNo( sampleService.getAccessionNumber() );
            sampleOrder.setReceivedDateForDisplay( sampleService.getReceivedDateForDisplay() );
            sampleOrder.setReceivedTime( sampleService.getReceived24HourTimeForDisplay( ) );

            sampleOrder.setRequestDate( ObservationHistoryService.getValueForSample( ObservationType.REQUEST_DATE, sample.getId() ) );
            sampleOrder.setReferringPatientNumber( ObservationHistoryService.getValueForSample( ObservationType.REFERRERS_PATIENT_ID, sample.getId() ) );
            sampleOrder.setNextVisitDate( ObservationHistoryService.getValueForSample( ObservationType.NEXT_VISIT_DATE, sample.getId() )  );
            sampleOrder.setPaymentOptionSelection( ObservationHistoryService.getRawValueForSample( ObservationType.PAYMENT_STATUS, sample.getId() ) );
            sampleOrder.setTestLocationCode( ObservationHistoryService.getRawValueForSample( ObservationType.TEST_LOCATION_CODE, sample.getId() ) );
            sampleOrder.setOtherLocationCode( ObservationHistoryService.getValueForSample( ObservationType.TEST_LOCATION_CODE_OTHER, sample.getId() ) );
            sampleOrder.setBillingReferenceNumber( ObservationHistoryService.getValueForSample( ObservationType.BILLING_REFERENCE_NUMBER, sample.getId() ) );
            sampleOrder.setOrderUrgency( ObservationHistoryService.getValueForSample( ObservationType.ORDER_URGENCY, sample.getId() ) );
            sampleOrder.setPatientDiagnosis( ObservationHistoryService.getValueForSample( ObservationType.PATIENT_DIAGNOSIS, sample.getId() ) );
            sampleOrder.setPatientBedNumber( ObservationHistoryService.getValueForSample( ObservationType.PATIENT_BED_NUMBER, sample.getId() ) );
            sampleOrder.setPatientRoomNumber( ObservationHistoryService.getValueForSample( ObservationType.PATIENT_ROOM_NUMBER, sample.getId() ) );
            sampleOrder.setClinicalDeptOptionSelection( ObservationHistoryService.getRawValueForSample( ObservationType.PATIENT_CLINICAL_DEPT_ID, sample.getId() ) );
            sampleOrder.setPatientAgeValue( ObservationHistoryService.getValueForSample( ObservationType.PATIENT_AGE_VALUE, sample.getId() ) );
            sampleOrder.setPatientAgeUnits( ObservationHistoryService.getValueForSample( ObservationType.PATIENT_AGE_UNITS, sample.getId() ) );
            sampleOrder.setProgram( ObservationHistoryService.getRawValueForSample( ObservationType.PROGRAM, sample.getId() ) );

            RequesterService requesterService = new RequesterService( sample.getId() );
            sampleOrder.setProviderFirstName( requesterService.getRequesterFirstName() );
            sampleOrder.setProviderLastName( requesterService.getRequesterLastName() );
            sampleOrder.setProviderWorkPhone( requesterService.getWorkPhone() );
            sampleOrder.setProviderFax( requesterService.getFax() );
            sampleOrder.setProviderEmail( requesterService.getEmail() );
            sampleOrder.setReferringSiteId( requesterService.getReferringSiteId() );
            sampleOrder.setReferringSiteCode( requesterService.getReferringSiteCode() );
            sampleOrder.setReferringSiteName( requesterService.getReferringSiteName() );
            if (FormFields.getInstance().useField(FormFields.Field.SUBMITTER_NUMBER) && requesterService.getOrganization() != null)
            	sampleOrder.setSubmitterNumber( requesterService.getOrganization().getOrganizationLocalAbbreviation() );

            if (FormFields.getInstance().useField(FormFields.Field.PROJECT_OR_NAME) ||
            	FormFields.getInstance().useField(FormFields.Field.PROJECT2_OR_NAME))
            	setSampleProjects();
            //Dung add show ngay khoi benh
            sampleOrder.setReadOnly( readOnly );
            if(sample.getOnsetOfDate()!=null){
	            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
	            sampleOrder.setOnsetOfDate(simpleDateFormat.format(sample.getOnsetOfDate()));
            }
            
            sampleOrder.setSpeciesName(ObservationHistoryService.getValueForSample(ObservationType.SPECIES_NAME, sample.getId()));
        }

        return sampleOrder;
    }

    public SampleOrderPersistenceArtifacts getPersistenceArtifacts( Sample sample, String currentUserId ){
        if( sampleOrder == null ){
            throw new IllegalStateException( "SampleOrderService.getPersistenceArtifacts have used the SampleOrderItem constructor" );
        }

        SampleOrderPersistenceArtifacts artifacts = new SampleOrderPersistenceArtifacts();

        if( sampleOrder.getModified() ){
            createSampleArtifacts( sample, currentUserId, artifacts );
            createProviderArtifacts(sampleOrder, currentUserId, artifacts);
        }

        return artifacts;
    }


    private void createSampleArtifacts( Sample sample, String currentUserId, SampleOrderPersistenceArtifacts artifacts ){
        if( sample == null ){
            sample = new Sample();
            sample.setId( sampleOrder.getSampleId() );
            sampleDAO.getData( sample );
            sample.setSysUserId( currentUserId );
        }

        String receivedDate = sampleOrder.getReceivedDateForDisplay();

        if( !GenericValidator.isBlankOrNull( sampleOrder.getReceivedTime() ) ){
            receivedDate += " " + sampleOrder.getReceivedTime();
        }else{
            receivedDate += " 00:00";
        }

        //TODO check if the receivedDate has changed, if it has then the change has to propagate through the analysis
        //   if (useReceiveDateForCollectionDate) {
        //       collectionDateFromRecieveDate = receivedDateForDisplay;
        //   }

        sample.setReceivedTimestamp( DateUtil.convertStringDateToTimestamp( receivedDate ));
        artifacts.setSample( sample );
    }

    private void createProviderArtifacts( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts  ){
        RequesterService requesterService = new RequesterService( sampleOrder.getSampleId() );
        createPersonProviderArtifacts( sampleOrder, currentUserId, artifacts, requesterService );
        createObservationHistoryArtifacts( sampleOrder, currentUserId, artifacts );
        createOrganizationProviderArtifacts( sampleOrder, currentUserId, artifacts, requesterService );
        if (FormFields.getInstance().useField(FormFields.Field.PROJECT_OR_NAME) ||
            FormFields.getInstance().useField(FormFields.Field.PROJECT2_OR_NAME))
        	createSampleProjectArtifacts(sampleOrder, currentUserId, artifacts);
    }

    private void createPersonProviderArtifacts( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts, RequesterService requesterService ){
        Person providerPerson = requesterService.getPerson();

        if( namesDiffer( providerPerson, sampleOrder)){
            providerPerson = null;
        }

        if( providerPerson == null){
            providerPerson = new Person();
            SampleRequester samplePersonRequester =requesterService.getSampleRequesterByType( RequesterService.Requester.PERSON, true );
            samplePersonRequester.setSysUserId( currentUserId );
            artifacts.setSamplePersonRequester( samplePersonRequester );
        }

        providerPerson.setFirstName( sampleOrder.getProviderFirstName() );
        providerPerson.setLastName( sampleOrder.getProviderLastName() );
        providerPerson.setWorkPhone( sampleOrder.getProviderWorkPhone() );
        providerPerson.setFax( sampleOrder.getProviderFax() );
        providerPerson.setEmail( sampleOrder.getProviderEmail() );
        providerPerson.setSysUserId( currentUserId );

        artifacts.setProviderPerson( providerPerson );
    }

    private boolean namesDiffer( Person providerPerson, SampleOrderItem sampleOrder ){
        return providerPerson == null ||
                StringUtil.compareWithNulls( providerPerson.getFirstName(), sampleOrder.getProviderFirstName() ) != 0 ||
                StringUtil.compareWithNulls( providerPerson.getLastName(), sampleOrder.getProviderLastName() ) != 0;

    }

    private void createObservationHistoryArtifacts( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts ){
        List<ObservationHistory> observations = new ArrayList<ObservationHistory>(  );
        PatientService patientService = new PatientService( artifacts.getSample() );
        String patientId = patientService.getPatient().getId();

        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.REFERRERS_PATIENT_ID, sampleOrder.getReferringPatientNumber(), ValueType.LITERAL );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.NEXT_VISIT_DATE, sampleOrder.getNextVisitDate(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.TEST_LOCATION_CODE, sampleOrder.getTestLocationCode(), ValueType.DICTIONARY  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.TEST_LOCATION_CODE_OTHER, sampleOrder.getOtherLocationCode(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PAYMENT_STATUS, sampleOrder.getPaymentOptionSelection(), ValueType.DICTIONARY  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.REQUEST_DATE, sampleOrder.getRequestDate(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PROGRAM, sampleOrder.getProgram(), ValueType.DICTIONARY  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.BILLING_REFERENCE_NUMBER, sampleOrder.getBillingReferenceNumber(), ValueType.LITERAL);
        
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.ORDER_URGENCY, sampleOrder.getOrderUrgency(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_DIAGNOSIS, sampleOrder.getPatientDiagnosis(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_BED_NUMBER, sampleOrder.getPatientBedNumber(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_ROOM_NUMBER, sampleOrder.getPatientRoomNumber(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_CLINICAL_DEPT_ID, sampleOrder.getClinicalDeptOptionSelection(), ValueType.DICTIONARY  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_AGE_VALUE, sampleOrder.getPatientAgeValue(), ValueType.LITERAL  );
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.PATIENT_AGE_UNITS, sampleOrder.getPatientAgeUnits(), ValueType.LITERAL  );
        
        //add to get observation for species
        createOrUpdateObservation(  currentUserId, observations, patientId, ObservationType.SPECIES_NAME, sampleOrder.getSpeciesName(), ValueType.LITERAL  );
        
        artifacts.setObservations( observations );
    }

    private void createOrUpdateObservation(  String currentUserId, List<ObservationHistory> observations,
                                             String patientId, ObservationType observationType, String value,
                                             ValueType valueType){
        ObservationHistory observation = ObservationHistoryService.getObservationForSample( observationType, sampleOrder.getSampleId() );
        if(observation == null && !GenericValidator.isBlankOrNull( value )){
            observation = new ObservationHistory(  );
            observation.setSampleId( sampleOrder.getSampleId() );
            observation.setPatientId( patientId );
            observation.setObservationHistoryTypeId( ObservationHistoryService.getObservationTypeIdForType( observationType ) );
            observation.setValueType( valueType.getCode() );
        }

        if( observation != null){
            observation.setValue( value );
            observation.setSysUserId( currentUserId );
            observations.add( observation );
        }
    }

    private void createOrganizationProviderArtifacts( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts, RequesterService requesterService ){
        /*
        These are the possible cases
        1. No organizationRequester
            a. No referring site id or code or new organization name -- nothing to do
            b. New organization name -- create a new sample requester and organization they will need to be bound after the organization is saved
            b. A referring site id -- create a new SampleRequester using the referring site id
                i. no referring code or -- nothing to do to organization.  We don't want to delete an existing code
                ii referring code differs from what is in the database -- update the organization
                iii. referring code is the same as in the database -- nothing to do with the organization
        2. Existing organizationRequester
            a. No referring site id or  new organization name -- delete requester
            b. New Organization -- create a new organization they will need to be bound after the organization is saved
            c. A referring site id which is the same as in organizationRequester
               i. referring code is the same as in the organization or referring code is blank -- nothing to do
               ii referring code is different from organization -- update organization
            d. A different site id for that in the requester -- update the requester
                i. referring code is the same as in the organization or referring code is blank -- nothing to do
               ii referring code is different from organization -- update organization
         */
        SampleRequester orgRequester = requesterService.getSampleRequesterByType( RequesterService.Requester.ORGANIZATION, false );

        if( orgRequester == null){
            handleNoExistingOrganizationRequester( sampleOrder, currentUserId, artifacts );
        }else{
            handleExistingOrganizationRequester( sampleOrder, currentUserId,artifacts, orgRequester );
        }
    }

    private void handleNoExistingOrganizationRequester( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts ){
        SampleRequester orgRequester;
        String submitterNumber = null;
        
        if (!GenericValidator.isBlankOrNull(sampleOrder.getSubmitterNumber()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getSubmitterNumber()))
            submitterNumber = sampleOrder.getSubmitterNumber();
        else if (!GenericValidator.isBlankOrNull(sampleOrder.getSubmitterNumberOther()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getSubmitterNumberOther()))
            submitterNumber = sampleOrder.getSubmitterNumberOther();
        
        if( GenericValidator.isBlankOrNull( sampleOrder.getReferringSiteId() ) &&
        	GenericValidator.isBlankOrNull( sampleOrder.getNewRequesterName() ) &&
        	GenericValidator.isBlankOrNull( submitterNumber )){
            return;
        }

        orgRequester = new SampleRequester();
        if (!GenericValidator.isBlankOrNull( sampleOrder.getReferringSiteId()))
	        orgRequester.setRequesterId(Long.parseLong( sampleOrder.getReferringSiteId()) ); //may be overridden latter
        orgRequester.setSampleId( Long.parseLong( sampleOrder.getSampleId() ) );
        orgRequester.setRequesterTypeId( RequesterService.Requester.ORGANIZATION.getId() );
        orgRequester.setSysUserId( currentUserId );
        if (FormFields.getInstance().useField( Field.SUBMITTER_NUMBER ) && !GenericValidator.isBlankOrNull(submitterNumber)) {
			Organization organization = new Organization();
			organization.setOrganizationLocalAbbreviation(submitterNumber);
			orgRequester.setRequesterId(Long.parseLong(orgDAO.getOrganizationByLocalAbbreviation(organization, true).getId()));
        }
        artifacts.setSampleOrganizationRequester( orgRequester );

        //Either there is an existing org else a new org
        Organization org;
        if( GenericValidator.isBlankOrNull( sampleOrder.getNewRequesterName() )){
            org = orgDAO.getOrganizationById( sampleOrder.getReferringSiteId() );
            //all of these are reasons to have nothing to do with the organization
            if( GenericValidator.isBlankOrNull( sampleOrder.getReferringSiteCode() ) ||
                    org == null || sampleOrder.getReferringSiteCode().equals( org.getCode() )){
                return;
            }
        }else{
            org = new Organization();
            org.setIsActive("Y");
            org.setMlsSentinelLabFlag( "N" );
        }

        org.setOrganizationName( sampleOrder.getNewRequesterName() );
        org.setCode( sampleOrder.getReferringSiteCode() );
        org.setSysUserId( currentUserId );
        artifacts.setProviderOrganization( org );
    }

    private void handleExistingOrganizationRequester( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts, SampleRequester orgRequester ){
        String submitterNumber = null;
        
        if (!GenericValidator.isBlankOrNull(sampleOrder.getSubmitterNumber()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getSubmitterNumber()))
            submitterNumber = sampleOrder.getSubmitterNumber();
        else if (!GenericValidator.isBlankOrNull(sampleOrder.getSubmitterNumberOther()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getSubmitterNumberOther()))
            submitterNumber = sampleOrder.getSubmitterNumberOther();
        
        if( (GenericValidator.isBlankOrNull( sampleOrder.getReferringSiteId() ) &&
        		GenericValidator.isBlankOrNull( sampleOrder.getNewRequesterName() )) ||
        		(FormFields.getInstance().useField(Field.SUBMITTER_NUMBER) &&
        		 GenericValidator.isBlankOrNull( submitterNumber )) ){
            artifacts.setDeletableSampleOrganizationRequester( orgRequester );
            return;
        }

        Organization org;
        if( !GenericValidator.isBlankOrNull( sampleOrder.getNewRequesterName() ) ){
            org = new Organization();
            org.setIsActive( "Y" );
            org.setMlsSentinelLabFlag( "N" );
            org.setOrganizationName( sampleOrder.getNewRequesterName() );
            org.setCode( sampleOrder.getReferringSiteCode() );
            org.setSysUserId( currentUserId );
            artifacts.setProviderOrganization( org );
            orgRequester.setSysUserId( currentUserId );
            artifacts.setSampleOrganizationRequester( orgRequester );
        }else{
            // cannot use submitterNumber to replace organizationId when getting organization, they're different
            org = orgDAO.getOrganizationBySubmitterNumber(submitterNumber);
            if (FormFields.getInstance().useField(Field.SUBMITTER_NUMBER) && !GenericValidator.isBlankOrNull(submitterNumber)) {
    			Organization organization = new Organization();
    			organization.setOrganizationLocalAbbreviation(submitterNumber);
            	if (!String.valueOf(submitterNumber).equals(orgDAO.getOrganizationByLocalAbbreviation(organization, true).getId()) &&
            		org != null) {
                    orgRequester.setRequesterId(orgDAO.getOrganizationByLocalAbbreviation(organization, true).getId());
                    orgRequester.setSysUserId(currentUserId);
                    artifacts.setSampleOrganizationRequester(orgRequester);
            	}
            	return;
            }
            if( String.valueOf( submitterNumber ).equals( sampleOrder.getReferringSiteId() ) ){
                if( org == null ||
                        sampleOrder.getReferringSiteCode() == null ||
                        sampleOrder.getReferringSiteCode().equals( org.getCode() ) ){
                    return;
                }

                updateExistingOrganizationCode( sampleOrder, currentUserId, artifacts, org );
            }else{
                orgRequester.setRequesterId( sampleOrder.getReferringSiteId() );
                orgRequester.setSysUserId( currentUserId );
                artifacts.setSampleOrganizationRequester( orgRequester );

                updateExistingOrganizationCode( sampleOrder, currentUserId, artifacts, org );
            }
        }
    }

    private void updateExistingOrganizationCode( SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts, Organization org ){
        if(sampleOrder.getReferringSiteCode() != null && !sampleOrder.getReferringSiteCode().equals( org.getCode() )){
            org.setCode( sampleOrder.getReferringSiteCode() );
            org.setSysUserId( currentUserId );
            artifacts.setProviderOrganization( org );
        }
    }

    private void setSampleProjects() {
		SampleProject sampleProject = new SampleProject();
    	List<SampleProject> sampleProjects = sampleProjectDAO.getSampleProjectListBySampleId(sample.getId());

		if (sampleProjects != null && sampleProjects.size() > 0) {
			sampleProject = sampleProjects.get(0);
			sampleOrder.setProjectIdOrName(sampleProject.getProject().getId());
			if (sampleProjects.size() > 1) {
				sampleProject = sampleProjects.get(1);
				sampleOrder.setProject2IdOrName(sampleProject.getProject().getId());
			}
		}
	}

    private void createSampleProjectArtifacts(SampleOrderItem sampleOrder, String currentUserId, SampleOrderPersistenceArtifacts artifacts) {
    	List<SampleProject> existingSampleProjects = sampleProjectDAO.getSampleProjectListBySampleId(sampleOrder.getSampleId());
    	Map<String, SampleProject> existingProjectMap = new HashMap<String, SampleProject>();
    	List<String> submittedProjectIds = new ArrayList<String>();
    	List<String> updatableSampleProjectIds = new ArrayList<String>();
    	List<SampleProject> newList = new ArrayList<SampleProject>();
    	List<SampleProject> deleteList = new ArrayList<SampleProject>();

		if (existingSampleProjects != null && existingSampleProjects.size() > 0) {
			existingProjectMap.put(existingSampleProjects.get(0).getProject().getId(), existingSampleProjects.get(0));
			if (existingSampleProjects.size() > 1)
				existingProjectMap.put(existingSampleProjects.get(1).getProject().getId(), existingSampleProjects.get(1));
		}

		if (!GenericValidator.isBlankOrNull(sampleOrder.getProjectIdOrName()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getProjectIdOrName()))
			submittedProjectIds.add(sampleOrder.getProjectIdOrName());
		else if (!GenericValidator.isBlankOrNull(sampleOrder.getProjectIdOrNameOther()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getProjectIdOrNameOther()))
			submittedProjectIds.add(sampleOrder.getProjectIdOrNameOther());

		if (!GenericValidator.isBlankOrNull(sampleOrder.getProject2IdOrName()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getProject2IdOrName()))
			submittedProjectIds.add(sampleOrder.getProject2IdOrName());
		else if (!GenericValidator.isBlankOrNull(sampleOrder.getProject2IdOrNameOther()) && org.apache.commons.lang.StringUtils.isNumeric(sampleOrder.getProject2IdOrNameOther()))
			submittedProjectIds.add(sampleOrder.getProject2IdOrNameOther());

		for (String id : existingProjectMap.keySet()) {
			if (!submittedProjectIds.contains(id))
				updatableSampleProjectIds.add(existingProjectMap.get(id).getId());
		}

		for (String id : submittedProjectIds) {
			if (!existingProjectMap.keySet().contains(id)) {
				if (updatableSampleProjectIds.size() > 0) {
					newList.add(updateSampleProjectWithProjectId(updatableSampleProjectIds.get(0), id, currentUserId));
					updatableSampleProjectIds.remove(0);
				} else {
					newList.add(newSampleProjectWithProjectId(id, sampleOrder.getSampleId(), currentUserId));
				}
			}
		}

		for (String id : updatableSampleProjectIds) {
			for (String projId : existingProjectMap.keySet()) {
				if (existingProjectMap.get(projId).getId().equals(id))
					deleteList.add(existingProjectMap.get(projId));
			}
		}

		if (deleteList.size() > 0)
			artifacts.setDeletableSampleProjects(deleteList.toArray(new SampleProject[deleteList.size()]));
		if (newList.size() > 0)
			artifacts.setSampleProjects(newList.toArray(new SampleProject[newList.size()]));		
    }

    private SampleProject updateSampleProjectWithProjectId(String sampProjId, String projId, String currentUserId) {
    	SampleProjectDAO sampProjDAO = new SampleProjectDAOImpl();
		Project project = new Project();
		project.setId(projId);
		projDAO.getData(project);

		SampleProject sampleProject = new SampleProject();
		sampleProject.setId(sampProjId);
		sampProjDAO.getData(sampleProject);
		sampleProject.setProject(project);
		sampleProject.setSysUserId(currentUserId);
		return sampleProject;
    }

    private SampleProject newSampleProjectWithProjectId(String id, String sampId, String currentUserId) {
		Project project = new Project();
		project.setId(id);
		projDAO.getData(project);

		Sample newSample = new Sample();
		newSample.setId(sampId);
		sampleDAO.getData(newSample);

		SampleProject sampleProject = new SampleProject();
		sampleProject.setProject(project);
		sampleProject.setSample(newSample);
		sampleProject.setSysUserId(currentUserId);
		return sampleProject;
    }


    public class SampleOrderPersistenceArtifacts{
        private Sample sample;
        private Person providerPerson;
        private Organization providerOrganization;
        private SampleRequester deletableSampleOrganizationRequester;
        private List<ObservationHistory> observations = new ArrayList<ObservationHistory>();
        private SampleRequester sampleOrganizationRequester;
        private SampleRequester samplePersonRequester;
        private SampleProject[] deletableSampleProjects = null;
        private SampleProject[] sampleProjects = null;

        public Sample getSample(){
            return sample;
        }

        public void setSample( Sample sample ){
            this.sample = sample;
        }

        public Person getProviderPerson(){
            return providerPerson;
        }

        public void setProviderPerson( Person providerPerson ){
            this.providerPerson = providerPerson;
        }

        public Organization getProviderOrganization(){
            return providerOrganization;
        }

        public void setProviderOrganization( Organization providerOrganization ){
            this.providerOrganization = providerOrganization;
        }


        public List<ObservationHistory> getObservations(){
            return observations;
        }

        public void setObservations( List<ObservationHistory> observations ){
            this.observations = observations;
        }

        public SampleRequester getSampleOrganizationRequester(){
            return sampleOrganizationRequester;
        }

        public void setSampleOrganizationRequester( SampleRequester sampleRequester ){
            this.sampleOrganizationRequester = sampleRequester;
        }

        public SampleRequester getDeletableSampleOrganizationRequester(){
            return deletableSampleOrganizationRequester;
        }

        public void setDeletableSampleOrganizationRequester( SampleRequester deletableSampleOrganizationRequester ){
            this.deletableSampleOrganizationRequester = deletableSampleOrganizationRequester;
        }

        public SampleRequester getSamplePersonRequester(){
            return samplePersonRequester;
        }

        public void setSamplePersonRequester( SampleRequester samplePersonRequester ){
            this.samplePersonRequester = samplePersonRequester;
        }

        public SampleProject[] getDeletableSampleProjects() {
        	if (this.deletableSampleProjects == null)
        		return new SampleProject[0];
        	else {
        		SampleProject[] copy = new SampleProject[this.deletableSampleProjects.length];
        		System.arraycopy(this.deletableSampleProjects, 0, copy, 0, copy.length);
        		return copy;
        	}
        }

        public void setDeletableSampleProjects(SampleProject[] deletableSampleProjects) {
        	this.deletableSampleProjects = new SampleProject[deletableSampleProjects.length];
        	System.arraycopy(deletableSampleProjects, 0, this.deletableSampleProjects, 0, deletableSampleProjects.length);
        }

        public SampleProject[] getSampleProjects() {
        	if (this.sampleProjects == null)
        		return new SampleProject[0];
        	else {
        		SampleProject[] copy = new SampleProject[this.sampleProjects.length];
        		System.arraycopy(this.sampleProjects, 0, copy, 0, copy.length);
        		return copy;
        	}
        }

        public void setSampleProjects(SampleProject[] sampleProjects) {
        	this.sampleProjects = new SampleProject[sampleProjects.length];
        	System.arraycopy(sampleProjects, 0, this.sampleProjects, 0, sampleProjects.length);
        }
    }
}