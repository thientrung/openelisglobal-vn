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
 * Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.validator.GenericValidator;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.formfields.FormFields;
import us.mn.state.health.lims.common.formfields.FormFields.Field;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.PatientService;
import us.mn.state.health.lims.common.services.StatusService;
import us.mn.state.health.lims.common.services.StatusService.ExternalOrderStatus;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.common.util.XMLUtil;
import us.mn.state.health.lims.dataexchange.order.action.HL7OrderInterpreter;
import us.mn.state.health.lims.dataexchange.order.daoimpl.ElectronicOrderDAOImpl;
import us.mn.state.health.lims.dataexchange.order.valueholder.ElectronicOrder;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patienttype.dao.PatientTypeDAO;
import us.mn.state.health.lims.patienttype.daoimpl.PatientTypeDAOImpl;
import us.mn.state.health.lims.requester.daoimpl.SampleRequesterDAOImpl;
import us.mn.state.health.lims.requester.valueholder.SampleRequester;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleTestDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleTestDAOImpl;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSampleTest;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v251.group.OML_O21_OBSERVATION_REQUEST;
import ca.uhn.hl7v2.model.v251.group.OML_O21_ORDER_PRIOR;
import ca.uhn.hl7v2.model.v251.message.OML_O21;
import ca.uhn.hl7v2.model.v251.segment.OBR;
import ca.uhn.hl7v2.model.v251.segment.OBX;
import ca.uhn.hl7v2.model.v251.segment.ORC;
import ca.uhn.hl7v2.parser.DefaultXMLParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.parser.XMLParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

import com.mirth.connect.connectors.ws.DefaultAcceptMessage_DefaultAcceptMessagePort_Client;

public class LabOrderSearchProvider extends BaseQueryProvider {
    private TestDAO testDAO = new TestDAOImpl();

    private PanelDAO panelDAO = new PanelDAOImpl();

    private PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

    private TypeOfSampleTestDAO typeOfSampleTest = new TypeOfSampleTestDAOImpl();

    private Map<TypeOfSample, PanelTestLists> typeOfSampleMap;

    private Map<Panel, List<TypeOfSample>> panelSampleTypesMap;

    private Map<String, List<TestSampleType>> testNameTestSampleTypeMap;

    private static final String NOT_FOUND = "Not Found";

    private static final String CANCELED = "Canceled";

    private static final String REALIZED = "Realized";

    private static final String PROVIDER_FIRST_NAME = "firstName";

    private static final String PROVIDER_LAST_NAME = "lastName";

    private static final String PROVIDER_PHONE = "phone";

    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String externalOrderNumber = request.getParameter("orderNumber");

        StringBuilder xml = new StringBuilder();
        String result = VALID;

        HapiContext context = new DefaultHapiContext();
        Parser p = context.getGenericParser();
        DefaultAcceptMessage_DefaultAcceptMessagePort_Client clientAcceptMessage = new DefaultAcceptMessage_DefaultAcceptMessagePort_Client();

        String HL7str = "";
        try {
            String[] selectedTestIds = new String[IActionConstants.MAX_STRING_ARRAY_LENGTH];
            String[] selectedTestNames = new String[IActionConstants.MAX_STRING_ARRAY_LENGTH];
            String[] exchangeIds = new String[IActionConstants.MAX_STRING_ARRAY_LENGTH];
            String[] exchangeTests = new String[IActionConstants.MAX_STRING_ARRAY_LENGTH];
            List<String> info = new ArrayList<String>();
            Map<String, String> sampleItemMap = new HashMap<String, String>();
            Map<String, String> positionMap = new HashMap<String, String>();
            
            if (StringUtil.isNullorNill(externalOrderNumber)) {
            	result = INVALID;
            	LogEvent.logInfo("LabOrderSearchProvider", "processRequest", "externalOrderNumber entered is null or empty.");
            } else {
	            HL7str = clientAcceptMessage.getExternalLabOrder(externalOrderNumber);
                LogEvent.logInfo("LabOrderSearchProvider", "processRequest", "HL7str: " + HL7str);
	            // parse HL7 string
	            if (!StringUtil.isNullorNill(HL7str)) {
		            xml = parseHL7(HL7str, externalOrderNumber, selectedTestIds, selectedTestNames, exchangeIds, exchangeTests,
		                    info, sampleItemMap, positionMap);
	            } else {
	            	result = INVALID;
	                LogEvent.logInfo("LabOrderSearchProvider", "processRequest", "No return data from DataExchange-NTP:getAcceptMessage().");
	            }
	            // OML_O21 hapiMsg = (OML_O21) p.parse(HL7str);
	            // HL7OrderInterpreter hl7orderinterpreter = new HL7OrderInterpreter();
	            // List<InterpreterResults> interpreterResult = hl7orderinterpreter.interpret(hapiMsg);
	
	            // String result = createSearchResultXML(orderNumber, xml);
	            /* if(!result.equals(VALID)){ if(result.equals(NOT_FOUND)){ result =
	             * StringUtil.getMessageForKey("electronic.order.message.orderNotFound"); result += " " + orderNumber; }else
	             * if(result.equals(CANCELED)){ result = StringUtil.getMessageForKey("electronic.order.message.canceled");
	             * }else if(result.equals(REALIZED)){ result =
	             * StringUtil.getMessageForKey("electronic.order.message.realized"); } result += "\n\n" +
	             * StringUtil.getMessageForKey("electronic.order.message.suggestion"); xml.append("empty"); }
	             */
        	}

        } catch (Exception e) {
            e.printStackTrace();
        }

        ajaxServlet.sendData(xml.toString(), result, request, response);
    }

    /**
     * @author markaae.fr
     * 
     * @param strHL7
     * @param externalOrderNumber
     * @param testIDs
     * @param testNames
     * @param idExchange
     * @param testExchange
     * @param info
     *            : obtain patient's information
     * @return StringBuilder
     */
    private StringBuilder parseHL7(String strHL7, String externalOrderNumber, String[] testIDs, String[] testNames,
            String[] idExchange, String[] testExchange, List<String> info, Map<String, String> map,
            Map<String, String> position_map) {
        //System.out.println("HL7: " + strHL7);
        String result1 = strHL7.replace("\n", "\r");
        // String result1 = strHL7;
        StringBuilder xmlResult = new StringBuilder();

        DictionaryDAO dictDAO = new DictionaryDAOImpl();
        PatientTypeDAO pTypeDAO = new PatientTypeDAOImpl();
        SampleRequesterDAOImpl sampleRequesterDAO = new SampleRequesterDAOImpl();
        TestDAO testDao = new TestDAOImpl();
        TypeOfSampleDAO tosDAO = new TypeOfSampleDAOImpl();
        TypeOfSample tosModel = new TypeOfSample();
        Dictionary dict = new Dictionary();
        SampleRequester sampReq = new SampleRequester();

        try {
            //PipeParser pipeParser = new PipeParser();
            //pipeParser.setValidationContext(new NoValidation()); 
            HapiContext context = new DefaultHapiContext();
            context.setValidationContext(new NoValidation());
            Parser pipeParser = context.getGenericParser();
            // parse the message string into a Message object
            Message message = pipeParser.parse(result1);
            // instantiate an XML parser
            XMLParser xmlParser = new DefaultXMLParser();

            // encode message in XML
            String ackMessageInXML = xmlParser.encode(message);
            // System.out.println(ackMessageInXML);

            // From the 10th test, there is error of XML, we need to replace "OML_O21.OBR%" to "OBR"
            // we need to replace "OML_O21.OBX%" to "OBX"
            int count = 10;
            while (ackMessageInXML.contains("OML_O21.OBR")) {
                ackMessageInXML = ackMessageInXML.replace(("OML_O21.OBR" + String.valueOf(count)), "OBR");
                ackMessageInXML = ackMessageInXML.replace(("OML_O21.OBX" + String.valueOf(count)), "OBX");
                count = count + 1;
            }
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(ackMessageInXML));

            // parse the HL7 string into Document object
            Document doc = db.parse(is);
            // Start of XML result data
            xmlResult.append("<result>");

            // PID|1||16^^^^GU||NGUYEN THI THAO||19650610|F|||88 Lang Ha - Q. Dong Da - Ha Noi||^^^^^^^^^^^^^|||S
            // PID|1||5^^^^GU||VÃ…Â¨ THÃ¡Â»Å  Ã„ï¿½OÃƒâ‚¬N||1976|F|viÃƒÂªm ruÃ¡Â»â„¢t thÃ¡Â»Â«a||XÃƒÂ£ HÃƒÂ¹ng ThÃ¡ÂºÂ¯ng, HuyÃ¡Â»â€¡n TiÃƒÂªn
            // LÃƒÂ£ng|||||NgoÃ¡ÂºÂ¡i,A6,15,ViÃ¡Â»â€¡n phÃƒÂ­,BÃƒâ„¢I NGÃ¡Â»Å’C SANH
            // Get name and birth date, gender, address, diagnosis
            //System.out.println("BEGIN: PID ===>");
            NodeList nodes_pid = doc.getElementsByTagName("PID");
            if (nodes_pid.getLength() > 0) {
                // Start of patient
                xmlResult.append("<patient>");
            }

            for (int ii = 0; ii < nodes_pid.getLength(); ii++) {
                Element element = (Element) nodes_pid.item(ii);
                String exchangeName = "";
                String exchangeAddress = "";
                String exchangDiagnosis = "";
                String exchangeBirth = "";
                String exchangeGender = "";
                String exchangePID = "";
                String exchangeWard = "";
                String exchangeDistrict = "";
                String exchangeCity = "";
                String exchangePatientType = "";
                String exchangeChartNumber = "";
                String exchangeSpeciesName = "";
                String exchangePatientAge = "";
                String exchangePatientAgeUnit = "";
                // Patient Name
                NodeList patientName = element.getElementsByTagName("PID.5");
                for (int k = 0; k < patientName.getLength(); k++) {
                    Element name_element = (Element) patientName.item(k);
                    NodeList name_node = name_element.getElementsByTagName("XPN.1");
                    if (name_node.getLength() > 0) {
                        exchangeName = getStringByTagXMl(name_node, "FN.1");
                    }
                }
                XMLUtil.appendKeyValue("first", exchangeName, xmlResult);
                //System.out.println("Name: " + exchange_name);
                info.add(!StringUtil.isNullorNill(exchangeName) ? exchangeName.trim() : "");

                // Gender
                NodeList genders = element.getElementsByTagName("PID.8");
                if (genders.getLength() > 0) {
                    exchangeGender = genders.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("gender", exchangeGender, xmlResult);
                //System.out.println("Gender: " + exchange_gender);
                info.add(!StringUtil.isNullorNill(exchangeGender) ? exchangeGender.trim() : "");

                // Birth Date
                NodeList birthDate = element.getElementsByTagName("PID.7");
                if (birthDate.getLength() > 0) {
                    exchangeBirth = getStringByTagXMl(birthDate, "TS.1").trim();
                }
                XMLUtil.appendKeyValue("dob", exchangeBirth, xmlResult);
                //System.out.println("Date of Birth: " + exchange_birth);
                info.add(!StringUtil.isNullorNill(exchangeBirth) ? exchangeBirth.trim() : "");

                // Patient ID
                NodeList pid = element.getElementsByTagName("PID.4");
                if (pid.getLength() > 0) {
                    exchangePID = pid.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("externalID", exchangePID, xmlResult);
                //System.out.println("Patient ID: " + exchange_pid);
                info.add(!StringUtil.isNullorNill(exchangePID) ? exchangePID.trim() : "");

                // Address
                NodeList address = element.getElementsByTagName("PID.11");
                for (int k = 0; k < address.getLength(); k++) {
                    Element address_element = (Element) address.item(k);
                    NodeList address_node = address_element.getElementsByTagName("XAD.1");
                    if (address_node.getLength() > 0) {
                        exchangeAddress = getStringByTagXMl(address_node, "SAD.1");
                    }
                }
                XMLUtil.appendKeyValue("streetAddress", exchangeAddress, xmlResult);
                //System.out.println("Address: " + exchange_address);
                info.add(!StringUtil.isNullorNill(exchangeAddress) ? exchangeAddress.trim() : "");

                // Ward
                NodeList ward = element.getElementsByTagName("PID.12");
                if (ward.getLength() > 0) {
                    exchangeWard = ward.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("ward", exchangeWard, xmlResult);
                //System.out.println("Ward: " + exchange_ward);
                info.add(!StringUtil.isNullorNill(exchangeWard) ? exchangeWard.trim() : "");

                // District
                NodeList district = element.getElementsByTagName("PID.13");
                if (district.getLength() > 0) {
                    exchangeDistrict = district.item(0).getTextContent().trim();
                }
                if (!StringUtil.isNullorNill(exchangeDistrict)) {
                	//exchangeDistrict = "Huyện Bình Đại";	// testing
                    dict.setDictEntry(exchangeDistrict);
                    XMLUtil.appendKeyValue("districtID", dictDAO.getDictionaryEntrysByNameAndCategoryDescription(exchangeDistrict, "districts").getId(), xmlResult);
                }
                XMLUtil.appendKeyValue("district", exchangeDistrict, xmlResult);
                //System.out.println("District: " + exchange_district);
                info.add(!StringUtil.isNullorNill(exchangeDistrict) ? exchangeDistrict.trim() : "");

                // City
                NodeList city = element.getElementsByTagName("PID.14");
                if (city.getLength() > 0) {
                    exchangeCity = city.item(0).getTextContent().trim();
                }
                if (!StringUtil.isNullorNill(exchangeCity)) {
                	//exchangeCity = "Bến Tre";	// testing
                    dict.setDictEntry(exchangeCity);
                    XMLUtil.appendKeyValue("cityID", dictDAO.getDictionaryEntrysByNameAndCategoryDescription(exchangeCity, "cities").getId(), xmlResult);
                }
                XMLUtil.appendKeyValue("city", exchangeCity, xmlResult);
                //System.out.println("City: " + exchange_city);
                info.add(!StringUtil.isNullorNill(exchangeCity) ? exchangeCity.trim() : "");
                
                // Patient Age
                NodeList patientAge = element.getElementsByTagName("PID.6");
                if (patientAge.getLength() > 0) {
                    exchangePatientAge = patientAge.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("patientAge", exchangePatientAge, xmlResult);
                
                // Patient Age Unit
                NodeList patientAgeUnit = element.getElementsByTagName("PID.10");
                if (patientAgeUnit.getLength() > 0) {
                    exchangePatientAgeUnit = patientAgeUnit.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("patientAgeUnit", exchangePatientAgeUnit, xmlResult);

                // Chart Number
                NodeList chartNumber = element.getElementsByTagName("PID.15");
                if (chartNumber.getLength() > 0) {
                    exchangeChartNumber = chartNumber.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("chartNumber", exchangeChartNumber, xmlResult);
                
                // Species Name
                NodeList speciesName = element.getElementsByTagName("PID.2");
                if (speciesName.getLength() > 0) {
                    exchangeSpeciesName = speciesName.item(0).getTextContent();
                }
                XMLUtil.appendKeyValue("speciesName", exchangeSpeciesName, xmlResult);
                
                // Diagnosis
                NodeList dignosis = element.getElementsByTagName("PID.9");
                for (int k = 0; k < dignosis.getLength(); k++) {
                    Element dignosis_element = (Element) dignosis.item(k);
                    NodeList dignosis_node = dignosis_element.getElementsByTagName("XPN.1");
                    if (dignosis_node.getLength() > 0) {
                        exchangDiagnosis = getStringByTagXMl(dignosis_node, "FN.1");
                    }
                }
                XMLUtil.appendKeyValue("diagnosis", exchangDiagnosis, xmlResult);
                //System.out.println("Diagnosis: " + exchange_dignosis);
                info.add(!StringUtil.isNullorNill(exchangDiagnosis) ? exchangDiagnosis.trim() : "");

                // Add more info: department^bedNumber^roomNumber^paymentType^providerFirstName
                //^projectName^submitterNumber^orderUrgency
                String[] exchange_info = new String[] { "", "", "", "", "", "", "", "", "" };
                NodeList more_info = element.getElementsByTagName("PID.16");
                if (more_info.getLength() > 0) {
                    exchange_info = more_info.item(0).getTextContent().split(",");
                    for (int k = 0; k < exchange_info.length; k++) {
                    	exchange_info[k] = exchange_info[k].trim();
                        switch (k) {
                            case 0:
                                if (!StringUtil.isNullorNill(exchange_info[k])) {
                                    // for testing
                                    //exchange_info[k] = "Ngoáº¡i TH";
                                    dict.setDictEntry(exchange_info[k]);
                                    XMLUtil.appendKeyValue("departmentID", dictDAO.getDictionaryEntrysByNameAndCategoryDescription(exchange_info[k], "patientClinicalDept").getId(), xmlResult);
                                }
                                XMLUtil.appendKeyValue("department", exchange_info[k], xmlResult);
                                break;
                            case 1:
                                XMLUtil.appendKeyValue("roomnumber", exchange_info[k], xmlResult);
                                break;
                            case 2:
                                XMLUtil.appendKeyValue("bednumber", exchange_info[k], xmlResult);
                                break;
                            case 3:
                            	//exchange_info[k] = "Viện phí";	// testing
                                if (!StringUtil.isNullorNill(exchange_info[k].trim())) {
                                    dict.setDictEntry(exchange_info[k].trim());
                                    XMLUtil.appendKeyValue("paymentID", dictDAO.getDictionaryEntrysByNameAndCategoryDescription(exchange_info[k].trim(), "patientPayment").getId(), xmlResult);
                                }
                                XMLUtil.appendKeyValue("payment", exchange_info[k], xmlResult);
                                break;
                            case 4:
                                XMLUtil.appendKeyValue("requester", exchange_info[k], xmlResult);
                                break;
                            case 5:
                                XMLUtil.appendKeyValue("project", exchange_info[k], xmlResult);
                                break;
                            case 6:
                            	/*if (!StringUtil.isNullorNill(exchange_info[k])) {
                                    sampReq = sampleRequesterDAO.readOld(sampleId, requesterTypeId);
                                    XMLUtil.appendKeyValue("organizationId", dictDAO.getDictionaryEntrysByNameAndCategoryDescription(exchange_info[k], "patientPayment").getId(), xmlResult);
                                }*/
                                XMLUtil.appendKeyValue("organization", exchange_info[k], xmlResult);
                                break;
                            case 7:    
                                // Patient Type
                            	/*exchange_info[k] = "SXH Dengue (A)";
                                if (!StringUtil.isNullorNill(exchange_info[k])) {
                                    PatientType pType = new PatientType();
                                    pType.setDescription(exchange_info[k]);
                                    XMLUtil.appendKeyValue("patientTypeID", pTypeDAO.getPatientTypeByDescription(pType).getType(), xmlResult);
                                }
                                XMLUtil.appendKeyValue("patientType", exchange_info[k], xmlResult);
                                */
                                XMLUtil.appendKeyValue("patientTypeID", exchange_info[k], xmlResult);
                                break;
                            case 8:
                                XMLUtil.appendKeyValue("orderUrgency", exchange_info[k], xmlResult);
                                break;
                            default:
                                break;
                        }
                        //System.out.println("Add more: " + exchange_info[k]);
                        info.add(!StringUtil.isNullorNill(exchange_info[k]) ? exchange_info[k].trim() : "");
                    }
                }
            }
            // End of patient
            xmlResult.append("</patient>");
            //System.out.println("END: PID <===");

            // OBR
            //System.out.println("BEGIN: OBR ===>");
            NodeList nodes = doc.getElementsByTagName("OBR");
            //System.out.println("BEGIN: OBX ===>");
            NodeList nodes_obx = doc.getElementsByTagName("OBX");

            String initialSample = "";
            boolean sampleChanged = false;
            int k = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                //System.out.println("  BEGIN: TEST " + i);
                TypeOfSample tos = new TypeOfSample();
                String specimen_type = "";
                String check = "";
                Element element_obx = (Element) nodes_obx.item(i);
                NodeList specimen_nodes = element_obx.getElementsByTagName("OBX.5");
                NodeList sample_source_nodes = element_obx.getElementsByTagName("OBX.6");
                NodeList collection_date_nodes = element_obx.getElementsByTagName("OBX.7");
                NodeList collection_time_nodes = element_obx.getElementsByTagName("OBX.8");
                if (specimen_nodes.getLength() > 0) {
                    specimen_type = specimen_nodes.item(0).getTextContent();
                    String typeOfSample = specimen_type;
                    tosModel.setDescription(specimen_type);
                    tos = tosDAO.getTypeOfSampleByDescriptionAndDomain(tosModel, true);
                    specimen_type = (tos == null ? specimen_type : tos.getId());

                    if (StringUtil.isNullorNill(typeOfSample) || !initialSample.equals(typeOfSample)) {
                        // End of Sample (check if not initial/first sample item)
                        if (!StringUtil.isNullorNill(initialSample)) {
                            xmlResult.append("</sampleItem>");
                        }
                        // Start of sample
                        xmlResult.append("<sampleItem>");
                        initialSample = typeOfSample;
                        sampleChanged = true;
                        // Type of Sample ID
                        XMLUtil.appendKeyValue("typeOfSampleID", specimen_type, xmlResult);
                        //System.out.println("TypeofSample ID: " + specimen_type);
                        // Type of Sample Name
                        XMLUtil.appendKeyValue("typeOfSample", typeOfSample, xmlResult);
                        //System.out.println("Type of Sample: " + typeOfSample);
                        
                        String sampleSource = "";
                        String collectionDate = "";
                        String collectionTime = "";
                        if (sample_source_nodes.getLength() > 0) {
	                        // Sample Source
                        	sampleSource = sample_source_nodes.item(0).getTextContent();
	                        XMLUtil.appendKeyValue("sampleSource", sampleSource, xmlResult);
                        }
                        if (collection_date_nodes.getLength() > 0) {
	                        // Sample Collection Date
                        	collectionDate = collection_date_nodes.item(0).getTextContent();
                        	XMLUtil.appendKeyValue("collectionDate", collectionDate, xmlResult);
                        }
                        if (collection_time_nodes.getLength() > 0) {
	                        // Sample Collection Time
                        	collectionTime = collection_time_nodes.item(0).getTextContent();
	                        XMLUtil.appendKeyValue("collectionTime", collectionTime, xmlResult);
                        }
                    } else {
                        sampleChanged = false;
                    }

                    check = map.put(specimen_type, tos == null ? "other" : tos.getDescription());
                    if (StringUtil.isNullorNill(check) && map.size() > 1) {
                        k++;
                        position_map.put(specimen_type, String.valueOf(k));
                    }
                    if (k == 0) {
                        position_map.put(specimen_type, String.valueOf(0));
                    }
                }
                // OBR
                Element element = (Element) nodes.item(i);
                NodeList idExchange_nodes = element.getElementsByTagName("OBR.2");
                NodeList analysisId_nodes = element.getElementsByTagName("OBR.3");
                if (idExchange_nodes.getLength() > 0 && analysisId_nodes.getLength() > 0) {
                    // Start of Test
                    xmlResult.append("<test>");
                    // Test ID
                    String id = getStringByTagXMl(idExchange_nodes, "EI.1").trim();
                    XMLUtil.appendKeyValue("testID", id, xmlResult);
                    //System.out.println("Test ID: " + id);
                    // For testing only (trapped exceeding test data) not in DB
                    /*if (Integer.valueOf(id) > 362) {
                        int twoDigits = Integer.valueOf(id) % 100;
                        id = String.valueOf(200 + twoDigits);
                    }*/
                    // Test Name
                    Test test = new Test();
                    if (!StringUtil.isNullorNill(id)) {
                        test.setId(id);
                        XMLUtil.appendKeyValue("testName", testDao.getTestById(test).getLocalizedName(), xmlResult);
                        // System.out.println("Test Name: " + testDao.getTestById(test).getDescription());
                    }
                    // Analysis ID
                    String analysisId = getStringByTagXMl(analysisId_nodes, "EI.1").trim();
                    XMLUtil.appendKeyValue("externalAnalysisID", analysisId, xmlResult);
                    //System.out.println("External Analysis ID: " + analysisId);

                    if (!StringUtil.isNullorNill(id) && !StringUtil.isNullorNill(analysisId) && test != null) {
                        int position = position_map.get(specimen_type) == null ? k : Integer.parseInt(position_map
                                .get(specimen_type));
                        if (StringUtil.isNullorNill(idExchange[position])) {
                            idExchange[position] = "";
                            testExchange[position] = "";
                            testNames[position] = "";
                            testIDs[position] = "";
                        }
                        idExchange[position] += ";" + id;
                        testExchange[position] += ";" + analysisId;
                        testNames[position] += ";" + testDao.getTestById(test).getDescription();
                        testIDs[position] += ";" + id;
                    }
                    // End of test
                    xmlResult.append("</test>");
                    //System.out.println("  END: TEST " + i);
                }
            }
            xmlResult.append("</sampleItem>");
            xmlResult.append("</result>");

        } catch (Exception e) {
            LogEvent.logError("LabOrderSearchProvider", "parseHL7", "ERROR parse HL7: " + e.getMessage());

        } finally {
            //System.out.println("Result:");
            //System.out.println("TestID[]: " + testIDs.toString());
            //System.out.println("TestName[]: " + testIDs.toString());
            //System.out.println("Type[]: " + map.toString());
            //System.out.println("TypePosition[]: " + position_map.toString());
        }
        LogEvent.logInfo("LabOrderSearchProvider", "parseHL7", "xmlResult: " + xmlResult);
        return xmlResult;
    }

    public static String getCharacterDataFromElement(Element e) {
        org.w3c.dom.Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    public static String getStringByTagXMl(NodeList test_nodes, String element) {
        Element test_lines = (Element) test_nodes.item(0);
        NodeList test_node = test_lines.getElementsByTagName(element);
        if (test_node.getLength() == 0) {
            return "";
        }
        Element test_line = (Element) test_node.item(0);
        return getCharacterDataFromElement(test_line);
    }

    private String createSearchResultXML(String orderNumber, StringBuilder xml) {
        String success = VALID;

        List<ElectronicOrder> eOrders = new ElectronicOrderDAOImpl().getElectronicOrdersByExternalId(orderNumber);
        if (eOrders.isEmpty()) {
            return NOT_FOUND;
        }

        ElectronicOrder eOrder = eOrders.get(eOrders.size() - 1);
        ExternalOrderStatus eOrderStatus = StatusService.getInstance()
                .getExternalOrderStatusForID(eOrder.getStatusId());

        if (eOrderStatus == ExternalOrderStatus.Cancelled) {
            return CANCELED;
        } else if (eOrderStatus == ExternalOrderStatus.Realized) {
            return REALIZED;
        }

        String patientGuid = getPatientGuid(eOrder);
        createOrderXML(eOrder.getData(), patientGuid, xml);

        return success;
    }

    private String getPatientGuid(ElectronicOrder eOrder) {
        // PatientService patientService = new PatientService(eOrder.getPatient());
        PatientDAO patientDAO = new PatientDAOImpl();
        PatientService patientService = new PatientService(patientDAO.getData(eOrder.getPatient().getId()));
        return patientService.getGUID();
    }

    private void createOrderXML(String orderMessage, String patientGuid, StringBuilder xml) {
        List<Request> tests = new ArrayList<Request>();
        List<Request> panels = new ArrayList<Request>();
        Map<String, String> requesterValuesMap = new HashMap<String, String>();

        getTestsAndPanels(tests, panels, orderMessage, requesterValuesMap);
        createMaps(tests, panels);
        xml.append("<order>");
        addRequester(xml, requesterValuesMap);
        addPatientGuid(xml, patientGuid);
        addSampleTypes(xml);
        addCrossPanels(xml);
        addCrosstests(xml);
        addAlerts(xml, patientGuid);
        xml.append("</order>");
    }

    private void addRequester(StringBuilder xml, Map<String, String> requesterValuesMap) {
        xml.append("<requester>");
        XMLUtil.appendKeyValue(PROVIDER_FIRST_NAME, requesterValuesMap.get(PROVIDER_FIRST_NAME), xml);
        XMLUtil.appendKeyValue(PROVIDER_LAST_NAME, requesterValuesMap.get(PROVIDER_LAST_NAME), xml);
        XMLUtil.appendKeyValue(PROVIDER_PHONE, requesterValuesMap.get(PROVIDER_PHONE), xml);
        xml.append("</requester>");
    }

    private void getTestsAndPanels(List<Request> tests, List<Request> panels, String orderMessage,
            Map<String, String> requesterValuesMap) {
        HapiContext context = new DefaultHapiContext();
        Parser p = context.getGenericParser();
        try {
            OML_O21 hapiMsg = (OML_O21) p.parse(orderMessage);

            ORC commonOrderSegment = hapiMsg.getORDER().getORC();

            requesterValuesMap.put(PROVIDER_PHONE, commonOrderSegment.getCallBackPhoneNumber(0)
                    .getXtn12_UnformattedTelephoneNumber().getValue());
            requesterValuesMap.put(PROVIDER_LAST_NAME, commonOrderSegment.getOrderingProvider(0).getFamilyName()
                    .getSurname().getValue());
            requesterValuesMap.put(PROVIDER_FIRST_NAME, commonOrderSegment.getOrderingProvider(0).getGivenName()
                    .getValue());

            OML_O21_OBSERVATION_REQUEST orderRequest = hapiMsg.getORDER().getOBSERVATION_REQUEST();

            addToTestOrPanel(tests, panels, orderRequest.getOBR(), orderRequest.getOBSERVATION().getOBX());

            List<OML_O21_ORDER_PRIOR> priorOrders = orderRequest.getPRIOR_RESULT().getORDER_PRIORAll();
            for (OML_O21_ORDER_PRIOR priorOrder : priorOrders) {
                addToTestOrPanel(tests, panels, priorOrder.getOBR(), priorOrder.getOBSERVATION_PRIOR().getOBX());
            }

        } catch (HL7Exception e) {
            e.printStackTrace();
        }
    }

    private void addToTestOrPanel(List<Request> tests, List<Request> panels, OBR obr, OBX obx) {
        if (!StringUtil.isNullorNill(obr.getUniversalServiceIdentifier().getIdentifier().getValue())
                && obr.getUniversalServiceIdentifier().getIdentifier().getValue()
                        .startsWith(HL7OrderInterpreter.ServiceIdentifier.PANEL.getIdentifier() + "-")) {
            panels.add(new Request(obr.getUniversalServiceIdentifier().getText().getValue(), obx.getObservationValue(0)
                    .getData().toString()));
        } else {
            tests.add(new Request(obr.getUniversalServiceIdentifier().getText().getValue(), obx.getObservationValue(0)
                    .getData().toString()));
        }
    }

    private void createMaps(List<Request> testRequests, List<Request> panelNames) {
        typeOfSampleMap = new HashMap<TypeOfSample, PanelTestLists>();
        panelSampleTypesMap = new HashMap<Panel, List<TypeOfSample>>();
        testNameTestSampleTypeMap = new HashMap<String, List<TestSampleType>>();

        createMapsForTests(testRequests);

        createMapsForPanels(panelNames);
    }

    private void addPatientGuid(StringBuilder xml, String patientGuid) {
        xml.append("<patient>");
        XMLUtil.appendKeyValue("guid", patientGuid, xml);
        xml.append("</patient>");
    }

    private void createMapsForTests(List<Request> testRequests) {
        for (Request testRequest : testRequests) {
            if (!StringUtil.isNullorNill(testRequest.getName())) {
                List<Test> tests = testDAO.getActiveTestByName(testRequest.getName());

                // If there is only one sample type we don't care what was requested
                boolean hasSingleSampleType = tests.size() == 1;
                Test singleTest = tests.get(0);
                TypeOfSample singleSampleType = TypeOfSampleUtil.getTypeOfSampleForTest(singleTest.getId());

                if (tests.size() > 1) {
                    if (!GenericValidator.isBlankOrNull(testRequest.getSampleType())) {
                        for (Test test : tests) {
                            TypeOfSample typeOfSample = TypeOfSampleUtil.getTypeOfSampleForTest(test.getId());
                            if (typeOfSample.getDescription().equals(testRequest.getSampleType())) {
                                hasSingleSampleType = true;
                                singleSampleType = typeOfSample;
                                singleTest = test;
                                break;
                            }
                        }
                    }

                    if (!hasSingleSampleType) {
                        List<TestSampleType> testSampleTypeList = testNameTestSampleTypeMap.get(testRequest.getName());

                        if (testSampleTypeList == null) {
                            testSampleTypeList = new ArrayList<TestSampleType>();
                            testNameTestSampleTypeMap.put(testRequest.getName(), testSampleTypeList);
                        }

                        for (Test test : tests) {
                            testSampleTypeList.add(new TestSampleType(test, TypeOfSampleUtil
                                    .getTypeOfSampleForTest(test.getId())));
                        }
                    }
                }

                if (hasSingleSampleType) {
                    PanelTestLists panelTestLists = typeOfSampleMap.get(singleSampleType);
                    if (panelTestLists == null) {
                        panelTestLists = new PanelTestLists();
                        typeOfSampleMap.put(singleSampleType, panelTestLists);
                    }

                    panelTestLists.addTest(singleTest);
                }
            }
        }
    }

    private void createMapsForPanels(List<Request> panelRequests) {
        for (Request panelRequest : panelRequests) {
            Panel panel = panelDAO.getPanelByName(panelRequest.getName());

            if (panel != null) {
                List<TypeOfSample> typeOfSamples = TypeOfSampleUtil.getTypeOfSampleForPanelId(panel.getId());
                boolean hasSingleSampleType = typeOfSamples.size() == 1;
                TypeOfSample singleTypeOfSample = typeOfSamples.get(0);

                if (!GenericValidator.isBlankOrNull(panelRequest.getSampleType())) {
                    if (typeOfSamples.size() > 1) {
                        for (TypeOfSample typeOfSample : typeOfSamples) {
                            if (typeOfSample.getDescription().equals(panelRequest.getSampleType())) {
                                hasSingleSampleType = true;
                                singleTypeOfSample = typeOfSample;
                                break;
                            }
                        }
                    }
                }

                if (hasSingleSampleType) {
                    PanelTestLists panelTestLists = typeOfSampleMap.get(singleTypeOfSample);
                    if (panelTestLists == null) {
                        panelTestLists = new PanelTestLists();
                        typeOfSampleMap.put(singleTypeOfSample, panelTestLists);
                    }

                    panelTestLists.addPanel(panel);
                } else {
                    panelSampleTypesMap.put(panel, typeOfSamples);
                }
            }
        }
    }

    private void addSampleTypes(StringBuilder xml) {
        xml.append("<sampleTypes>");
        for (TypeOfSample typeOfSample : typeOfSampleMap.keySet()) {
            addSampleType(xml, typeOfSample, typeOfSampleMap.get(typeOfSample));
        }
        xml.append("</sampleTypes>");
    }

    private void addSampleType(StringBuilder xml, TypeOfSample typeOfSample, PanelTestLists panelTestLists) {
        xml.append("<sampleType>");
        XMLUtil.appendKeyValue("id", typeOfSample.getId(), xml);
        XMLUtil.appendKeyValue("name", typeOfSample.getLocalizedName(), xml);
        addPanels(xml, panelTestLists.getPanels(), typeOfSample.getId());
        addTests(xml, "tests", panelTestLists.getTests());
        xml.append("</sampleType>");
    }

    private void addPanels(StringBuilder xml, List<Panel> panels, String sampleTypeId) {
        xml.append("<panels>");
        for (Panel panel : panels) {
            xml.append("<panel>");
            XMLUtil.appendKeyValue("id", panel.getId(), xml);
            XMLUtil.appendKeyValue("name", panel.getLocalizedName(), xml);
            addPanelTests(xml, panel.getId(), sampleTypeId);
            xml.append("</panel>");
        }
        xml.append("</panels>");
    }

    private void addPanelTests(StringBuilder xml, String panelId, String sampleTypeId) {
        List<Test> panelTests = getTestsForPanelAndType(panelId, sampleTypeId);
        addTests(xml, "panelTests", panelTests);
    }

    private List<Test> getTestsForPanelAndType(String panelId, String sampleTypeId) {
        List<TypeOfSampleTest> sampleTestList = typeOfSampleTest.getTypeOfSampleTestsForSampleType(sampleTypeId);
        List<Integer> testList = new ArrayList<Integer>();
        for (TypeOfSampleTest typeTest : sampleTestList) {
            testList.add(Integer.parseInt(typeTest.getTestId()));
        }

        List<PanelItem> panelList = panelItemDAO.getPanelItemsForPanelAndItemList(panelId, testList);
        List<Test> tests = new ArrayList<Test>();
        for (PanelItem item : panelList) {
            tests.add(item.getTest());
        }

        return tests;
    }

    private void addTests(StringBuilder xml, String parent, List<Test> tests) {
        xml.append("<");
        xml.append(parent);
        xml.append(">");
        for (Test test : tests) {
            xml.append("<test>");
            XMLUtil.appendKeyValue("id", test.getId(), xml);
            XMLUtil.appendKeyValue("name", test.getLocalizedName(), xml);
            xml.append("</test>");
        }
        xml.append("</");
        xml.append(parent);
        xml.append(">");
    }

    private void addCrossPanels(StringBuilder xml) {
        xml.append("<crosspanels>");
        for (Panel panel : panelSampleTypesMap.keySet()) {
            addCrosspanel(xml, panel, panelSampleTypesMap.get(panel));
        }

        xml.append("</crosspanels>");
    }

    private void addCrosspanel(StringBuilder xml, Panel panel, List<TypeOfSample> typeOfSampleList) {
        xml.append("<crosspanel>");
        XMLUtil.appendKeyValue("name", panel.getLocalizedName(), xml);
        XMLUtil.appendKeyValue("id", panel.getId(), xml);
        addPanelCrosssampletypes(panel.getId(), xml, typeOfSampleList);
        xml.append("</crosspanel>");
    }

    private void addPanelCrosssampletypes(String panelId, StringBuilder xml, List<TypeOfSample> typeOfSampleList) {
        xml.append("<crosssampletypes>");
        for (TypeOfSample typeOfSample : typeOfSampleList) {
            addCrosspanelTypeOfSample(panelId, xml, typeOfSample);
        }
        xml.append("</crosssampletypes>");
    }

    private void addCrosspanelTypeOfSample(String panelId, StringBuilder xml, TypeOfSample typeOfSample) {
        xml.append("<crosssampletype>");
        XMLUtil.appendKeyValue("id", typeOfSample.getId(), xml);
        XMLUtil.appendKeyValue("name", typeOfSample.getLocalizedName(), xml);
        if (FormFields.getInstance().useField(Field.SAMPLE_ENTRY_MODAL_VERSION)) {
            addPanelTests(xml, panelId, typeOfSample.getId());
        }
        xml.append("</crosssampletype>");
    }

    private void addCrosstests(StringBuilder xml) {
        xml.append("<crosstests>");
        for (String testName : testNameTestSampleTypeMap.keySet()) {
            addCrosstestForTestName(xml, testName, testNameTestSampleTypeMap.get(testName));
        }
        xml.append("</crosstests>");

    }

    private void addCrosstestForTestName(StringBuilder xml, String testName, List<TestSampleType> list) {
        xml.append("<crosstest>");
        XMLUtil.appendKeyValue("name", testName, xml);
        xml.append("<crosssampletypes>");
        for (TestSampleType testSampleType : list) {
            addTestCrosssampleType(xml, testSampleType);
        }
        xml.append("</crosssampletypes>");
        xml.append("</crosstest>");
    }

    private void addTestCrosssampleType(StringBuilder xml, TestSampleType testSampleType) {
        xml.append("<crosssampletype>");
        XMLUtil.appendKeyValue("id", testSampleType.getSampleType().getId(), xml);
        XMLUtil.appendKeyValue("name", testSampleType.getSampleType().getLocalizedName(), xml);
        XMLUtil.appendKeyValue("testid", testSampleType.getTest().getId(), xml);
        xml.append("</crosssampletype>");
    }

    private void addAlerts(StringBuilder xml, String patientGuid) {
        PatientService patientService = new PatientService(patientGuid);
        if (GenericValidator.isBlankOrNull(patientService.getDOB())
                || GenericValidator.isBlankOrNull(patientService.getGender())) {
            XMLUtil.appendKeyValue("user_alert",
                    StringUtil.getMessageForKey("electroinic.order.warning.missingPatientInfo"), xml);
        }
    }

    public class PanelTestLists {
        private List<Test> tests = new ArrayList<Test>();

        private List<Panel> panels = new ArrayList<Panel>();

        public List<Test> getTests() {
            return tests;
        }

        public List<Panel> getPanels() {
            return panels;
        }

        public void addPanel(Panel panel) {
            if (panel != null) {
                panels.add(panel);
            }
        }

        public void addTest(Test test) {
            if (test != null) {
                tests.add(test);
            }
        }

    }

    public class TestSampleType {
        private Test test;

        private TypeOfSample sampleType;

        public TestSampleType(Test test, TypeOfSample sampleType) {
            this.test = test;
            this.sampleType = sampleType;
        }

        public Test getTest() {
            return test;
        }

        public TypeOfSample getSampleType() {
            return sampleType;
        }
    }

    private class Request {
        private String name;

        private String sampleType;

        public Request(String name, String sampleType) {
            this.name = name;
            this.sampleType = sampleType;
        }

        public String getSampleType() {
            return sampleType;
        }

        public String getName() {
            return name;
        }

    }
}
