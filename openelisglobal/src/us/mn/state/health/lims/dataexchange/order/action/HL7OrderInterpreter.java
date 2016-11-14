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
package us.mn.state.health.lims.dataexchange.order.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.services.ITestIdentityService;
import us.mn.state.health.lims.common.services.TestIdentityService;
import us.mn.state.health.lims.common.util.DateUtil;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CX;
import ca.uhn.hl7v2.model.v251.group.OML_O21_OBSERVATION_REQUEST;
import ca.uhn.hl7v2.model.v251.group.OML_O21_ORDER_PRIOR;
import ca.uhn.hl7v2.model.v251.message.OML_O21;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.ORC;
import ca.uhn.hl7v2.model.v251.segment.PID;

public class HL7OrderInterpreter implements IOrderInterpreter{

	public enum IdentityType{
		GUID("GU"),
		ST_NUMBER("ST"),
		NATIONAL_ID("NA"),
		OB_NUMBER("OB"),
		PC_NUMBER("PC");

		private String tag;

		private IdentityType(String tag){
			this.tag = tag;
		}

		public String getIdentifier(){
			return tag;
		}
	}

	public enum Gender{
		MALE("M"), 
		FEMALE("F");

		private String tag;

		private Gender(String tag){
			this.tag = tag;
		}

		public String getIdentifier(){
			return tag;
		}

	}
	
	public enum ServiceIdentifier{
		PANEL("P"),
		TEST("T");
		
		private String tag;
		ServiceIdentifier(String tag){
			this.tag = tag;
		}
		
		public String getIdentifier(){
			return tag;
		}
	}
	
	private String labOrderNumber;
	private OrderType orderType;
	private OML_O21 orderMessage;
	private MessagePatient patient;
	private List<InterpreterResults> results = new ArrayList<InterpreterResults>();
	private List<String> unsupportedTests = new ArrayList<String>();
	private List<String> unsupportedPanels = new ArrayList<String>();
	private ITestIdentityService testIdentityService;

	@Override
	public List<InterpreterResults> interpret(Message orderMessage){
		this.orderMessage = (OML_O21)orderMessage;
//		try{
//			System.out.println(this.orderMessage.printStructure());
//			LogEvent.logError( "Debugging", "hl7", this.orderMessage.printStructure());
//		}catch(HL7Exception e1){
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try{
			patient = createPatientFromHL7();
			extractOrderInformation();
		}catch(HL7Exception e){
			e.printStackTrace();
			return buildResultList(true);
		}
		return buildResultList(false);
	}
	
	private void extractOrderInformation() throws HL7Exception{
		ORC orcSegment = (ORC)orderMessage.getORDER().get("ORC");

		labOrderNumber =  orcSegment.getPlacerOrderNumber().encode();
		
		if(OrderType.REQUEST.getIdentifier().equals(orcSegment.getOrderControl().getValue())){
			orderType = OrderType.REQUEST;
		}else if(OrderType.CANCEL.getIdentifier().equals(orcSegment.getOrderControl().getValue())){
			orderType = OrderType.CANCEL;
		}else{
			orderType = OrderType.UNKNOWN;
		}

	}

	private MessagePatient createPatientFromHL7() throws HL7Exception{

		MessagePatient patient = new MessagePatient();
		
		PID pid = (PID)orderMessage.getPATIENT().get("PID");
		
		CX[] patientIdentities = pid.getPatientIdentifierList();
		for(CX identity : patientIdentities){
			String value = identity.getCx1_IDNumber().getValue();
			String code = identity.getIdentifierTypeCode().getValue();

			if(IdentityType.GUID.getIdentifier().equals(code)){
				patient.setGuid(value);
			}else if(IdentityType.ST_NUMBER.getIdentifier().equals(code)){
				patient.setStNumber(value);
			}else if(IdentityType.NATIONAL_ID.getIdentifier().equals(code)){
				patient.setNationalId(value);
			}else if(IdentityType.OB_NUMBER.getIdentifier().equals(code)){
				patient.setObNumber(value);
			}else if(IdentityType.PC_NUMBER.getIdentifier().equals(code)){
				patient.setPcNumber(value);
			}
		}

		if( Gender.MALE.getIdentifier().equals(pid.getAdministrativeSex().getValue())){
			patient.setGender("M");
		}else if( Gender.FEMALE.getIdentifier().equals(pid.getAdministrativeSex().getValue())){
			patient.setGender("F");
		}

		setDOB(patient, pid);

		patient.setLastName(pid.getPatientName(0).getFamilyName().getSurname().getValue());
		patient.setFirstName(pid.getPatientName(0).getGivenName().getValue());
		patient.setAddressStreet(pid.getPatientAddress(0).getStreetAddress().getStreetOrMailingAddress().getValue());
		patient.setAddressVillage(pid.getPatientAddress(0).getCity().getValue());
		patient.setAddressDepartment(pid.getPatientAddress(0).getStateOrProvince().getValue());

		return patient;
	}

	private void setDOB(MessagePatient patient, PID pid) throws HL7Exception{
		String dob = pid.getDateTimeOfBirth().encode();
		
		if( dob.length() >= 4){
			String year = null;	
			String month = DateUtil.AMBIGUOUS_DATE_SEGMENT;
			String date = DateUtil.AMBIGUOUS_DATE_SEGMENT;
		
			year = dob.substring(0, 4);
			
			if( dob.length() >= 6){
				month = dob.substring(4, 6);
			}
			
			if( dob.length() >= 8){
				date = dob.substring(6, 8);
			}	
			
			patient.setDisplayDOB(date + "/" + month + "/" + year);
		}
	}

	private List<InterpreterResults> buildResultList(boolean exceptionThrown){
		results = new ArrayList<InterpreterResults>();
		
		if(exceptionThrown){
			results.add(InterpreterResults.INTERPRET_ERROR);
		}else{
			if( orderType == OrderType.UNKNOWN){
				results.add(InterpreterResults.UNKNOWN_REQUEST_TYPE);
			}
			
			if(GenericValidator.isBlankOrNull(getReferringOrderNumber())){
				results.add(InterpreterResults.MISSING_ORDER_NUMBER);
			}

			if(orderType == OrderType.REQUEST){
				if(GenericValidator.isBlankOrNull(getMessagePatient().getGuid())){
					results.add(InterpreterResults.MISSING_PATIENT_GUID);
				}

//These are being commented out until we get confirmation on the desired policy.  Either the request should be rejected or the user should be required to
// fill the missing information in at the time of sample entry.  Commenting these out supports the latter				
//				if(GenericValidator.isBlankOrNull(getMessagePatient().getGender())){
//					results.add(InterpreterResults.MISSING_PATIENT_GENDER);
//				}
//
//				if(getMessagePatient().getDob() == null){
//					results.add(InterpreterResults.MISSING_PATIENT_DOB);
//				}

				if(getMessagePatient().getNationalId() == null &&
						getMessagePatient().getObNumber() == null &&
						getMessagePatient().getPcNumber() == null &&
						getMessagePatient().getStNumber() == null){
					results.add(InterpreterResults.MISSING_PATIENT_IDENTIFIER);
				}

				try{
					OML_O21_OBSERVATION_REQUEST orderRequest = orderMessage.getORDERAll().get(0).getOBSERVATION_REQUEST();
					checkOBR( orderRequest.getOBR());
					List<OML_O21_ORDER_PRIOR> priorOrders = orderRequest.getPRIOR_RESULT().getORDER_PRIORAll();
					for(OML_O21_ORDER_PRIOR priorOrder : priorOrders){
						checkOBR(priorOrder.getOBR());
					}
					
				}catch(HL7Exception e){
					e.printStackTrace();
					results.add(InterpreterResults.INTERPRET_ERROR);
				}
			}
		}

		if(results.isEmpty()){
			results.add(InterpreterResults.OK);
		}

		return results;
	}

	private void checkOBR( OBR obr) throws HL7Exception{
		if( obr.isEmpty() ){
			results.add(InterpreterResults.MISSING_TESTS);
		}else{
			String name = obr.getUniversalServiceIdentifier().getText().getValue();
			String identifier = obr.getUniversalServiceIdentifier().getIdentifier().getValue();
			if( identifier.startsWith(ServiceIdentifier.TEST.getIdentifier() + "-")){
				if(!getTestIdentityService().doesTestExist(name)){
					if( !results.contains(InterpreterResults.UNSUPPORTED_TESTS)){
						results.add(InterpreterResults.UNSUPPORTED_TESTS);
					}
					unsupportedTests.add( name );
				}
			}else if( identifier.startsWith(ServiceIdentifier.PANEL.getIdentifier() + "-")){
				if(!getTestIdentityService().doesPanelExist(name)){
					if( !results.contains(InterpreterResults.UNSUPPORTED_PANELS)){
						results.add(InterpreterResults.UNSUPPORTED_PANELS);
					}
					unsupportedPanels.add( name );
				}
			}else{
				results.add(InterpreterResults.OTHER_THAN_PANEL_OR_TEST_REQUESTED);
			}
		}
	}

	@Override
	public String getReferringOrderNumber(){
		return labOrderNumber;
	}

	@Override
	public String getMessage(){
		if(orderMessage == null){
			return null;
		}

		try{
			return orderMessage.encode();
		}catch(HL7Exception e){
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public MessagePatient getMessagePatient(){
		return patient;
	}

	@Override
	public List<InterpreterResults> getResultStatus(){
		return results;
	}

	@Override
	public OrderType getOrderType(){
		return orderType;
	}

	@Override
	public List<String> getUnsupportedTests(){
		return unsupportedTests;
	}

	@Override
	public List<String> getUnsupportedPanels(){
		return unsupportedPanels;
	}

	private ITestIdentityService getTestIdentityService(){
		if( testIdentityService == null){
			testIdentityService = TestIdentityService.getInstance();
		}
		
		return testIdentityService;
	}

	public void setTestIdentityService(ITestIdentityService testIdentityService){
		this.testIdentityService = testIdentityService;
	}

}
