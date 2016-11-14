package us.mn.state.health.lims.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.analysisexchange.valueholder.AnalysisExchange;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.daoimpl.ResultLimitDAOImpl;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.testmapping.dao.TestMappingDAO;
import us.mn.state.health.lims.testmapping.daoimpl.TestMappingDAOImpl;

/**
 * Connect database SqlServer and execute update store
 * @author markaae.fr
 *
 */
public class SqlConnectUtil {

    // DAOs
    //private static final AnalysisDAO analysisDAO = new AnalysisDAOImpl();
    private static final DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
    /*private static final ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
    private static final PersonDAO personDAO = new PersonDAOImpl();
    private static final PersonAddressDAO personAddressDAO = new PersonAddressDAOImpl();
    private static final PatientPatientTypeDAO patientPatientTypeDAO = new PatientPatientTypeDAOImpl();
    private static final PatientTypeDAOImpl patientTypeDAO = new PatientTypeDAOImpl();*/
    private static final ResultDAO resultDAO = new ResultDAOImpl();
    /*private static final SampleDAO sampleDAO = new SampleDAOImpl();
    private static final SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
    private static final SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();
    private static final SampleRequesterDAOImpl sampleRequesterDAO = new SampleRequesterDAOImpl();*/
    private static final TestMappingDAO testMappingDAO = new TestMappingDAOImpl();
    
    private static Log log = LogFactory.getLog(SqlConnectUtil.class);
//	private static Logger log = Logger.getLogger(SqlConnectUtil.class);
    
    
	public static void transferTestResult(Analysis analysis, AnalysisExchange analysisExchange, String accessionNumber) throws SQLException {
		ResultLimitDAO resultLimitDAO = new ResultLimitDAOImpl();
		SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
		Connection conn = null;
		
	    try {
			conn = getMSSQLConnection();
			
			if (conn != null) {
			    List<Result> listResult = resultDAO.getResultsByAnalysis(analysis);
			    Result analysisResult = null;
                String resultValue = "";
                String childResultValue = "";
                String normalRange = "";
                String outOfRange = "1";
                
                Sample sample = analysis.getSampleItem().getSample();
			    Patient patient = sampleHumanDAO.getPatientForSample(sample);
			    String patientAge = patient.getPerson().getAge();
			    String patientGender = patient.getGender();
			    
			    if (listResult.size() > 0) {
			        analysisResult = listResult.get(0);
    			    if (analysisResult != null) {
    			        resultValue = analysisResult.getValue();
    			    }
                    if (listResult.size() > 1) {
                        List<Result> listChildResult = resultDAO.getChildResults(analysisResult.getId());
                        Result childResult = listChildResult.get(0);
                        childResultValue = childResult.getValue();
                    }
			    }
			    if (analysisResult.getResultType().equals("D")) {
			        resultValue = dictionaryDAO.getDictionaryById(analysisResult.getValue()).getDictEntry();
			    }
			    
			    List<ResultLimit> listResultLimit = resultLimitDAO.getAllResultLimitsForTest(analysis.getTest());
			    if (listResultLimit.size() > 0) {
			    	for (ResultLimit resultLimit : listResultLimit) {
					    if ( resultLimit != null && ((patientGender.equals(resultLimit.getGender()) || StringUtil.isNullorNill(resultLimit.getGender().trim())) &&
					    		((Double.parseDouble(patientAge) >= resultLimit.getMinAge() && Double.parseDouble(patientAge) <= resultLimit.getMaxAge()) || 
					    			(resultLimit.getMinAge() == 0 && resultLimit.getMaxAge() == Double.POSITIVE_INFINITY))) ) {
					    	normalRange = String.valueOf(resultLimit.getLowNormal()) + " ~ " + String.valueOf(resultLimit.getHighNormal());
					    	
					    	if ( (!StringUtil.isNullorNill(childResultValue) && (Double.parseDouble(childResultValue) >= resultLimit.getLowNormal() && 
						    		Double.parseDouble(childResultValue) <= resultLimit.getHighNormal())) ||
						    			(!StringUtil.isNullorNill(resultValue) && (Double.parseDouble(resultValue) >= resultLimit.getLowNormal() && 
									    		Double.parseDouble(resultValue) <= resultLimit.getHighNormal())) ) {
						    	outOfRange = "0";
						    }
					    	break;
					    }
			    	}
			    }
			    
			    us.mn.state.health.lims.testmapping.valueholder.TestMapping testMapping = testMappingDAO.getTestMappingByInternalTestId(analysis.getTest().getId());

			    String sql = "EXEC his.dbo.DataExchange_Update ?,?,?,?,?,?,?,?,?,?,?"; // Update result using external_test_id, external_analysis_id, and assigned_code
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setEscapeProcessing(true);
                ps.setQueryTimeout(30000);
                
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "##### DataExchange->Update Parameters: #####");

                String[] splitAssignedCode = analysisExchange.getAssignedCode().split("@");
                ps.setString(1, splitAssignedCode[0]);       // assigned_code
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "assigned_code: " + analysisExchange.getAssignedCode());
                
            	/*if (!StringUtil.isNullorNill(childResultValue)) {
                    ps.setString(2, resultValue + "-" + childResultValue); // result
                    LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "result: " + resultValue + "-" + childResultValue);
                } else {*/
                    ps.setString(2, resultValue); // result
                    LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "result: " + resultValue);
                //}
                ps.setString(3, analysisExchange.getExternalAnalysisId()); // external_analysis_id
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "external_analysis_id: " + analysisExchange.getExternalAnalysisId());
                
                if (testMapping != null) {
                    ps.setString(4, testMapping.getExternalTestId());      // external_test_id
                    LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "external_test_id: " + testMapping.getExternalTestId());
                }
                ps.setString(5, normalRange); // normal_range
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "normal_range: " + "1");
                ps.setString(6, outOfRange); // out_of_range
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "out_of_range: " + "1");
                ps.setString(7, analysisExchange.getMedicalNumber()); // medical_number
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "medical_number: " + analysisExchange.getMedicalNumber());
                ps.setString(8, childResultValue); // sub_result
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "sub_result: " + childResultValue);
                ps.setString(9, analysisExchange.getExchangedBy()); // exchanged_by
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "exchanged_by: " + analysisExchange.getExchangedBy());
                ps.setString(10, "1"); // upd
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "upd: " + "1");
                ps.setString(11, accessionNumber); // accession_number
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "accession_number: " + accessionNumber);
                
                int sqlReturn = ps.executeUpdate();
                if (sqlReturn > 0) {
                    LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil transferTestResult()", "HIS data updated successfully.");
                }
			    
                //String sql = "EXEC dbo.DataExchange_Insert ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";  //added two(2) parameters (project,organization)
			    //PreparedStatement ps = conn.prepareStatement(sql);
                //ps.setEscapeProcessing(true);
                //ps.setQueryTimeout(30000);
                
			    /*Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);
				SampleItem sampleItem = sampleItemDAO.getSampleItemsBySampleId(sample.getId()).get(0);
				Patient patient = sampleHumanDAO.getPatientForSample(sample);
				// Patient Information
				ps.setString(1, patient.getPerson().getFirstName()); // full_name
				ps.setString(2, patient.getBirthDateForDisplay()); // birth_date
				
                ps.setString(3, observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "10").getValue()); // age
				//if (analysisResult != null)
				//    ps.setString(3, analysisResult.getValue()); // age (used as test_result_value for now)
                ps.setString(4, patient.getGender()); // gender
                ps.setString(5, observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "5").getValue()); // emergency
                ObservationHistory deptObsHistory = observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "9");
                if (deptObsHistory != null)
                	ps.setString(6, dictionaryDAO.getDictionaryById(deptObsHistory.getValue()).getDictEntry()); // clinical_department
                else 
                	ps.setString(6, null); // clinical_department
                ps.setString(7, observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "6").getValue()); //diagnosis
                ObservationHistory bedObsHistory = observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "7");
                if (bedObsHistory != null)
                    ps.setInt(8, Integer.valueOf(bedObsHistory.getValue())); // bed_number
                else 
                    ps.setInt(8, 0); // bed_number
                
                ObservationHistory roomObsHistory = observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "8");
                if (deptObsHistory != null)
                    ps.setString(9, roomObsHistory.getValue()); // room_number
                else 
                    ps.setString(9, null); // room_number
                SampleRequester sampleRequester = sampleRequesterDAO.readOld(Long.parseLong(sample.getId()), Long.parseLong("2"));
                ps.setString(10, personDAO.getPersonById(String.valueOf(sampleRequester.getRequesterId())).getFirstName()); // requestor
                String patientTypeId = patientPatientTypeDAO.getPatientPatientTypeForPatient(patient.getId()).getPatientTypeId();
                ps.setString(11, patientTypeDAO.readPatientType(patientTypeId).getDescription()); // patient_type
                String paymentDictionaryId = observationHistoryDAO.getObservationHistoriesBySampleIdAndType(sample.getId(), "2").getValue();
                ps.setString(12, dictionaryDAO.getDictionaryById(paymentDictionaryId).getDictEntry()); // payment_type
                ps.setString(13, patient.getPerson().getStreetAddress()); // street_address
                ps.setString(14, personAddressDAO.getByPersonIdAndPartId(patient.getPerson().getId(), "8").getValue()); // ward
                String districtDictionaryId = personAddressDAO.getByPersonIdAndPartId(patient.getPerson().getId(), "7").getValue();
                ps.setString(15, dictionaryDAO.getDictionaryById(districtDictionaryId).getDictEntry()); // district
                ps.setString(16, dictionaryDAO.getDictionaryById(patient.getPerson().getCity()).getDictEntry()); // city
                //ps.setString(17, "A19901021"); //assigned_code
                ps.setString(17, analysisExchange.getAssignedCode());
                ps.setString(18, patient.getChartNumber()); // chart_number
                ps.setString(19, "IN1234"); // insurance_number
                ps.setString(20, DateUtil.convertTimestampToStringDate(sampleItem.getCollectionDate())); //collection_date
                ps.setString(21, sampleItem.getCollector()); // collector
                if (testMapping != null)
                   ps.setString(22, testMapping.getExternalTestId()); // external_test_id
                
                ps.setString(23, sampleItem.getTypeOfSampleId()); // type_of_sample_id
                ps.setString(24, patient.getId()); // patient_id
                ps.setString(25, analysisResult.getId()); // result_id
                ps.setString(26, analysis.getId()); // analysis_id
                //ps.setString(27, "1"); // project
                //ps.setString(28, "99"); // organization
				//ResultSet rs = ps.executeQuery();*/
			}
			
		} catch (Exception e) {
            LogEvent.logError("SqlConnectUtil", "SqlConnectUtil transferTestResult()", e.toString());
            
		} finally {
			conn.close();
		}
	}
	
	public static void updateExternalOrderFlag(Analysis analysis, AnalysisExchange analysisExchange) throws SQLException {
        Connection conn = null;
        try {
            conn = getMSSQLConnection();
            
            if (conn != null) {
                us.mn.state.health.lims.testmapping.valueholder.TestMapping testMapping = testMappingDAO.getTestMappingByInternalTestId(analysis.getTest().getId());

                // Update Flag 
                String sqlUpdateFlag = "EXEC dbo.DataExchange_UpdateFlag ?,?,?,?,?"; // Update result using orderNumber, externalAnalysisId, externalTestId
                PreparedStatement psUpdateFlag = conn.prepareStatement(sqlUpdateFlag);
                psUpdateFlag.setEscapeProcessing(true);
                psUpdateFlag.setQueryTimeout(30000);
                
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "##### DataExchange->UpdateFlag: #####");
                String[] splitAssignedCode = analysisExchange.getAssignedCode().split("@");
                psUpdateFlag.setString(1, splitAssignedCode[0]);       // assigned_code
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "assigned_code: " + analysisExchange.getAssignedCode());
                if (testMapping != null) {
                    psUpdateFlag.setString(2, testMapping.getExternalTestId());      // external_test_id
                    LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "external_test_id: " + testMapping.getExternalTestId());
                }
                psUpdateFlag.setString(3, analysisExchange.getExternalAnalysisId()); // external_analysis_id
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "external_analysis_id: " + analysisExchange.getExternalAnalysisId());
                // Added by markaae.fr 2016-11-04
                psUpdateFlag.setString(4, analysisExchange.getMedicalNumber()); // medical_number
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "medical_number: " + analysisExchange.getMedicalNumber());
                psUpdateFlag.setString(5, "1"); // blood_taken
                LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "blood_taken: " + "1");
                
                int sqlReturn = psUpdateFlag.executeUpdate();
                if (sqlReturn > 0) {
                	LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", "HIS data FLAG updated successfully.");
                }
            }
            
        } catch (Exception e) {
            LogEvent.logError("SqlConnectUtil", "SqlConnectUtil updateExternalOrderFlag()", e.toString());
            
        } finally {
            conn.close();
        }
	}
	
	public static Connection getMSSQLConnection() {
        SystemConfiguration config = SystemConfiguration.getInstance();
        Connection conn = null;
        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            LogEvent.logError("SqlConnectUtil", "SqlConnectUtil getMSSQLConnection()", Arrays.toString(e1.getStackTrace()));
        }
        
        String url = config.getDataExchangeSQLServerURL();
        String user = config.getDataExchangeSQLServerUser();
        String password = config.getDataExchangeSQLServerPassword(); 
        LogEvent.logInfo("SqlConnectUtil", "SqlConnectUtil getMSSQLConnection()", "server[URL]: " + url);
        
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            LogEvent.logError("SqlConnectUtil", "SqlConnectUtil getMSSQLConnection()", e.toString());
        }
        
        return conn;
    }
	
	/*public static boolean setValueByAnalysisId(String xml) throws SAXException, IOException, ParserConfigurationException, SQLException {
		boolean isSuccessful = false;
		SystemConfiguration config = SystemConfiguration.getInstance();
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String url = config.getDataExchangeSQLServerURL();
		//String user = config.getUserName();
		//String password = config.getPassword();
		
		//log.info("Connection string is: URL: " + url + ", USERNAME: " + user + ", PASSWORD: " + password);
		try {
//			conn = DriverManager.getConnection(url, user, password);
//			Statement sta = conn.createStatement();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
			NodeList test_nodes = doc.getElementsByTagName("Test");
			if (test_nodes.getLength() > 0) {
				for (int i =0; i< test_nodes.getLength(); i++) {
					Element element = (Element)test_nodes.item(i);
					//System.out.println(element.getElementsByTagName("loinc").item(0).getTextContent());
					//System.out.println(element.getElementsByTagName("testUnit").item(0).getTextContent());
					//System.out.println(element.getElementsByTagName("flag").item(0).getTextContent());
					NodeList externalId_nodes = element.getElementsByTagName("externalTestId");
					if (externalId_nodes.getLength() > 0) {
						String requestTestId = element.getElementsByTagName("requestTestId").item(0).getTextContent();
						// Add for National Lung
						String chartNumber = element.getElementsByTagName("chartNumber").item(0).getTextContent();
						if (chartNumber.contains("/"))
						{
							chartNumber = chartNumber.substring(0,chartNumber.indexOf("/"));
						}
						String externalTestId = element.getElementsByTagName("externalTestId").item(0).getTextContent();
						//String completedDate = element.getElementsByTagName("completedDate").item(0).getTextContent();
						Timestamp completedDate = new Timestamp(System.currentTimeMillis());
						String normalEmergency = element.getElementsByTagName("normalEmergency").item(0).getTextContent();
						String typeOfSampleId = element.getElementsByTagName("typeOfSampleId").item(0).getTextContent();
							if (!StringUtil.isNullorNill(requestTestId)) {
								NodeList value_nodes = element.getElementsByTagName("result");
								if (value_nodes.getLength() > 0) {
									String value = element.getElementsByTagName("result").item(0).getTextContent();
									String sql = "EXEC dbo.sp_LIS_result '" 
										+ chartNumber + "','" 
										+ externalTestId + "','" 
										+ value + "','" 
										+ completedDate + "'," 
										+ normalEmergency + "," 
										+ typeOfSampleId + ",'"
										+ requestTestId.trim() + "'";
									log.info("SQL query export data to HIS: " + sql);
									if (conn != null) {
										//District 4 Hospital
										//String sql = "EXEC dbo.setValueByAnalysisId " + requestTestId.trim() + "," + (value != ""?value:"");
										//National Lung Hospital
										//String sql = "EXEC [sp_LIS_Result] '" 
//										String sql = "EXEC dbo.sp_LIS_result '" 
//														+ chartNumber + "','" 
//														+ externalTestId + "','" 
//														+ value + "','" 
//														+ completedDate + "'," 
//														+ normalEmergency + "," 
//														+ typeOfSampleId + ",'"
//														+ requestTestId.trim() + "'";
										log.info("SQL query export data to HIS: " + sql);
//										sta.addBatch(sql);
									}
								}
							}
					}
				}
			}
//			int[] result = sta.executeBatch();
//			if (result.length > 0) {
//				isSuccessful = true;
//				System.out.println("Table update successfully...");
//			}
		} catch (Exception e) {
			System.out.println(e.toString());
			log.info("Error update results for HIS: " + e.getStackTrace());
		}
		finally {
			if (conn != null) {
				conn.close();
			} else {
				log.info("Unable to connect to HIS server");
			}
		}
		
		return isSuccessful;
	}*/
	
	/**
	 @author thachnn
	 * call store from other system and update value with exchange test
	 * @param xml
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SQLException
	 */
	/*public static void updateResultToHIS(String xml) throws SQLException {
		SystemConfiguration config = SystemConfiguration.getInstance();
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e1) {
			log.info("Error updateResultToHIS: " + e1.toString());
		}
		
		String url = config.getOracleURL();
		String user = config.getOracleUserName();
		String password = config.getOraclePassword();
		String[] parameter = SystemConfiguration.getInstance().getParameter();
		String[] parameter_type = SystemConfiguration.getInstance().getParameterType();
		String storeName = SystemConfiguration.getInstance().getStoreName();
		int number = parameter.length;
		try {
			conn = DriverManager.getConnection(url, user, password);
			CallableStatement sta = null;
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
			NodeList test_nodes = doc.getElementsByTagName("Test");
				for (int i =0; i< test_nodes.getLength(); i++) {
					Element element = (Element)test_nodes.item(i);
					String[] nodes_value = new String[number];
					for (int k = 0; k < number; k++) {
						NodeList nodes = element.getElementsByTagName(parameter[k].trim());
						nodes_value[k] = nodes.getLength() > 0 ? nodes.item(0).getTextContent() : ""; 
					}
							if (!StringUtil.isNullorNill(nodes_value[0]) && !StringUtil.isNullorNill(nodes_value[1])) {
								if (conn != null) {
									sta = conn.prepareCall(storeName);
									for (int k = 0; k < number; k++) {
										switch (Integer.valueOf(parameter_type[k])) {
										case 1: sta.setBigDecimal(k+1, new BigDecimal(nodes_value[k]));
											break;
										case 2: sta.setString(k+1, nodes_value[k]);
											break;
										default:
											break;
										}
									}
									sta.executeUpdate();
								}
							}
				}
			
		} catch (Exception e) {
			log.info("Error updateResultToHIS SQL: " + e.getMessage());
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}*/
	
}
