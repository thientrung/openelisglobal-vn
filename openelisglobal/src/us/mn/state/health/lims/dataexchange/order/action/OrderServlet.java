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

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.hoh.hapi.server.HohServlet;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Group;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.datatype.CWE;
import ca.uhn.hl7v2.model.v251.message.ACK;
import ca.uhn.hl7v2.model.v251.message.OML_O21;
import ca.uhn.hl7v2.model.v251.segment.ERR;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import us.mn.state.health.lims.dataexchange.order.action.IOrderInterpreter.InterpreterResults;
import us.mn.state.health.lims.dataexchange.order.action.OrderWorker.OrderResult;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OrderServlet extends HohServlet{

	private static final long serialVersionUID = -2572093053734971596L;

	
	@Override
	public void init(ServletConfig theConfig) throws ServletException{
		setApplication(new OrderApplication());
	}

	/**
	 * The application does the actual processing
	 */
	private class OrderApplication implements ReceivingApplication
	{
		/**
		 * processMessage is fired each time a new message arrives.
		 * 
		 * @param message
		 *            The message which was received
		 * @param theMetadata
		 *            A map containing additional information about the message,
		 *            where it came from, etc.
		 */
		public Message processMessage(Message message, Map<String, Object> theMetadata) throws ReceivingApplicationException, HL7Exception{
			//LogEvent.logFatal("OrderServlet", "processMessage", message.encode());
			// System.out.println("Received message:\n" + message.printStructure());

			OrderWorker worker = new OrderWorker(message);

			worker.setInterpreter(new HL7OrderInterpreter());
			worker.setExistanceChecker(new DBOrderExistanceChecker());
			worker.setPersister(new DBOrderPersister());

			OrderResult orderResult = worker.handleOrderRequest();

			ACK response = null;
			try{
				OML_O21 omlMessage = (OML_O21)message;

				if(orderResult == OrderResult.OK){

					response = (ACK)message.generateACK();

				}else if(orderResult == OrderResult.NON_CANCELABLE_ORDER || orderResult == OrderResult.DUPLICATE_ORDER){
					response = (ACK)omlMessage.generateACK(AcknowledgmentCode.CR, null);
					ERR err = createNewERRSegment("207", "Application internal error", orderResult.toString() + " : "
							+ worker.getExistanceCheckResult().toString(), response);
					response.insertERR(err, 0);
				}else if(orderResult == OrderResult.MESSAGE_ERROR){
					response = (ACK)omlMessage.generateACK(AcknowledgmentCode.CR, null);

					List<InterpreterResults> interpreterResults = worker.getMessageErrors();
					int errorCnt = 0;
					ERR err = null;
					for(InterpreterResults result : interpreterResults){
						switch(result){
						case MISSING_ORDER_NUMBER:
						case MISSING_PATIENT_GUID:
						case MISSING_PATIENT_DOB:
						case MISSING_PATIENT_GENDER:
						case MISSING_PATIENT_IDENTIFIER:
						case MISSING_TESTS:
							err = createNewERRSegment("101", "Required field missing", result.toString(), response);
							response.insertERR(err, errorCnt++);
							break;
						case UNSUPPORTED_TESTS:
							StringBuilder testResponseBuilder = new StringBuilder(result.toString());
							List<String> unsupportedTests = worker.getUnsupportedTests();
							if(!unsupportedTests.isEmpty()){
								testResponseBuilder.append("[");
								testResponseBuilder.append(unsupportedTests.get(0));
								for(int i = 1; i < unsupportedTests.size(); i++){
									testResponseBuilder.append(",");
									testResponseBuilder.append(unsupportedTests.get(i));
								}

								testResponseBuilder.append("]");
							}

							err = createNewERRSegment("207", "Application internal error", testResponseBuilder.toString(), response);
							response.insertERR(err, errorCnt++);
							break;
						case UNSUPPORTED_PANELS:
							StringBuilder panelResponseBuilder = new StringBuilder(result.toString());
							List<String> unsupportedPanels = worker.getUnsupportedPanels();
							if(!unsupportedPanels.isEmpty()){
								panelResponseBuilder.append("[");
								panelResponseBuilder.append(unsupportedPanels.get(0));
								for(int i = 1; i < unsupportedPanels.size(); i++){
									panelResponseBuilder.append(",");
									panelResponseBuilder.append(unsupportedPanels.get(i));
								}

								panelResponseBuilder.append("]");
							}

							err = createNewERRSegment("207", "Application internal error", panelResponseBuilder.toString(), response);
							response.insertERR(err, errorCnt++);
							break;
						case UNKNOWN_REQUEST_TYPE:
						case OTHER_THAN_PANEL_OR_TEST_REQUESTED:
							err = createNewERRSegment("103", "Table value not found", result.toString(), response);
							response.insertERR(err, errorCnt++);
							break;
						case INTERPRET_ERROR:
							err = createNewERRSegment("207", "Application internal error", "Unexpected internal error", response);
							response.insertERR(err, errorCnt++);
						}

					}
				}else{
					response = (ACK)omlMessage.generateACK(AcknowledgmentCode.AE, new HL7Exception("Unknown result thrown"));
				}

			}catch(IOException e){
				throw new ReceivingApplicationException(e);
			}
		
			return response;
		}

		private ERR createNewERRSegment(String HL70357Identifier, String HL70357Msg, String detail, ACK response) throws DataTypeException{
			ERR err = new ERR((Group)response.getParent(), response.getModelClassFactory());
			CWE cwe = err.getHL7ErrorCode();
			cwe.getIdentifier().setValue(HL70357Identifier);
			cwe.getText().setValue(HL70357Msg);
			cwe.getOriginalText().setValue(detail);
			err.getSeverity().setValue("E");
			return err;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean canProcess(Message theMessage){
			return true;
		}

	}
}
