/**
 * @(#) DataExchangeImpl.java 01-00 May 3, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 3, 2016
 */
package vi.mn.state.health.lims.dataexchange.daoimpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;

import org.apache.commons.lang.NumberUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Transaction;

import us.mn.state.health.lims.address.dao.PersonAddressDAO;
import us.mn.state.health.lims.address.daoimpl.PersonAddressDAOImpl;
import us.mn.state.health.lims.address.valueholder.PersonAddress;
import us.mn.state.health.lims.analysis.dao.AnalysisDAO;
import us.mn.state.health.lims.analysis.daoimpl.AnalysisDAOImpl;
import us.mn.state.health.lims.analysis.valueholder.Analysis;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.connection.PostgreSQLConnector;
import us.mn.state.health.lims.common.daoimpl.BaseDAOImpl;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.login.dao.LoginDAO;
import us.mn.state.health.lims.login.daoimpl.LoginDAOImpl;
import us.mn.state.health.lims.login.valueholder.Login;
import us.mn.state.health.lims.observationhistory.dao.ObservationHistoryDAO;
import us.mn.state.health.lims.observationhistory.daoimpl.ObservationHistoryDAOImpl;
import us.mn.state.health.lims.observationhistory.valueholder.ObservationHistory;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.person.dao.PersonDAO;
import us.mn.state.health.lims.person.daoimpl.PersonDAOImpl;
import us.mn.state.health.lims.person.valueholder.Person;
import us.mn.state.health.lims.provider.dao.ProviderDAO;
import us.mn.state.health.lims.provider.daoimpl.ProviderDAOImpl;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.samplehuman.valueholder.SampleHuman;
import us.mn.state.health.lims.sampleitem.dao.SampleItemDAO;
import us.mn.state.health.lims.sampleitem.daoimpl.SampleItemDAOImpl;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.systemuser.dao.SystemUserDAO;
import us.mn.state.health.lims.systemuser.daoimpl.SystemUserDAOImpl;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.ValidateDataWebService;
import vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO;
import vi.mn.state.health.lims.dataexchange.dao.IParameterTestWebservice;
import vi.mn.state.health.lims.dataexchange.dto.AimUserDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultDTO;
import vi.mn.state.health.lims.dataexchange.dto.TestResultParameterDTO;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;
import vi.mn.state.health.lims.dataexchange.valueholder.TestResultVO;
import vi.mn.state.health.lims.etor.data.dao.EtorDataMappingDAO;
import vi.mn.state.health.lims.etor.data.daoimpl.EtorDataMappingDAOImpl;
import vi.mn.state.health.lims.etor.data.valueholder.EtorDataMapping;

/**
 * Detail description of processing of this class.
 * 
 * @author thonghh, markaae.fr
 */
@SuppressWarnings("deprecation")
@WebService
public class DataExchangeDAOImpl extends BaseDAOImpl implements DataExchangeDAO, IActionConstants,
        IParameterTestWebservice {
	
    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getTotalSpecimensByUser(int)
     */
    @Override
    public int getTotalSpecimensByUser(int userId) {
        String sql = "SELECT count(*)  FROM etor_data_mapping e where e.etor_user_id = :userId";
        int count = 0;
        try {
            SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
            query.setInteger("userId", userId);
            count = ((BigInteger) query.uniqueResult()).intValue();
            closeSession();
        } catch (HibernateException e) {
            LogEvent.logError("DataExchangeDAOImpl", "getTotalSpecimensByUser()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getTotalSpecimensByUser()", e);
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getTotalSpecimensByUserAndStatus(int, int)
     */
    @Override
    public int getTotalSpecimensByUserAndStatus(int userId, int status) {
        String sql = "SELECT count(*)  FROM etor_data_mapping e where e.etor_user_id = :userId and e.lis_status = :status";
        int count = 0;
        try {
            SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
            query.setInteger("userId", userId);
            query.setInteger("status", status);
            count = ((BigInteger) query.uniqueResult()).intValue();
            closeSession();
        } catch (HibernateException e) {
            LogEvent.logError("DataExchangeDAOImpl", "getTotalSpecimensByUserAndStatus()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getTotalSpecimensByUserAndStatus()", e);
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getAllRequiredFields()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getAllRequiredFields() {
        List<Object[]> structureFields;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT DISTINCT i.table_name, i.column_name, i.data_type, i.is_identity, i.is_nullable, i.character_maximum_length  ");
            sql.append(" FROM information_schema.columns i  ");
            sql.append(" WHERE (i.table_name = 'person' AND  i.column_name='first_name') ");
            sql.append(" OR (i.table_name = 'patient' AND  i.column_name='external_id') ");
            sql.append(" OR (i.table_name = 'patient' AND  i.column_name='birth_date') ");
            sql.append(" OR (i.table_name = 'person' AND  i.column_name='age') ");
            sql.append(" OR (i.table_name = 'patient' AND  i.column_name='gender') ");
            sql.append(" OR (i.table_name = 'observation_history' AND  i.column_name='value') ");
            sql.append(" OR (i.table_name = 'person' AND  i.column_name='street_address') ");
            sql.append(" OR (i.table_name = 'person_address' AND  i.column_name='value') ");
            sql.append(" OR (i.table_name = 'dictionary' AND  i.column_name='dict_entry') ");
            sql.append(" OR (i.table_name = 'sample_item' AND  i.column_name='collection_date') ");
            sql.append(" OR (i.table_name = 'sample' AND  i.column_name='entered_date') ");
            sql.append(" OR (i.table_name = 'sample_item' AND  i.column_name='collector') ");
            sql.append(" OR (i.table_name = 'sample' AND  i.column_name='transmission_date') ");
            sql.append(" OR (i.table_name = 'result' AND  i.column_name='value') ");
            sql.append(" OR (i.table_name = 'type_of_sample' AND  i.column_name='description') ");

            SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql.toString());
            structureFields = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getAllRequiredFields()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getAllRequiredFields()", e);
        }
        return structureFields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getGenders()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getGenders() {
        List<Object[]> genders;
        try {
            String sql = "SELECT id, gender_type FROM gender";
            Query query = HibernateUtil.getSession().createSQLQuery(sql);
            genders = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getGenders()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getGenders()", e);
        }
        return genders;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getCities()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getCities() {
        List<Object[]> cities;
        try {
            String sql = "SELECT d.id, d.dict_entry FROM dictionary d, dictionary_category dc  WHERE d.dictionary_category_id = dc.id AND dc.id = 239";
            Query query = HibernateUtil.getSession().createSQLQuery(sql);
            cities = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getCities()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getCities()", e);
        }
        return cities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getDistricts()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getDistricts() {
        List<Object[]> districts;
        try {
            String sql = "SELECT d.id, d.dict_entry FROM dictionary d, dictionary_category dc  WHERE d.dictionary_category_id = dc.id AND dc.id = 240";
            Query query = HibernateUtil.getSession().createSQLQuery(sql);
            districts = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getDistricts()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getDistricts()", e);
        }
        return districts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getSampleTypes()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getSampleTypes() {
        List<Object[]> sampleTypes;
        try {
            String sql = "SELECT id, description FROM type_of_sample";
            Query query = HibernateUtil.getSession().createSQLQuery(sql);
            sampleTypes = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getSampleTypes()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getSampleTypes()", e);
        }
        return sampleTypes;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#addNewOrder(vi.mn.state.health.lims.dataexchange.valueholder
     * .OrderVO)
     */
    @Override
    public String addNewOrder(OrderVO orderVO) throws LIMSRuntimeException {
        Map<String, String> errors = new HashMap<String, String>();
        errors = ValidateDataWebService.validateOrderData(orderVO);
        if (errors.size() > 0) {
            String errorReturn = "";
            for (Entry<String, String> value : errors.entrySet()) {
                errorReturn += value.getValue() + "\n\br";
            }
            return errorReturn;
        }
        Transaction tx = HibernateUtil.getSession().beginTransaction();
        try {
            orderVO.getSample().setId(addSample(orderVO));
            // add doctor
            orderVO.getProvider().setSelectedPersonId(addPersion(orderVO.getProvider().getPerson()));
            orderVO.getProvider().setId(addProvider(orderVO));
            // add patient
            orderVO.getPatient().getPersonPatient().setId(addPersion(orderVO.getPatient().getPerson()));
            orderVO.getPatient().setId(addPatient(orderVO));
            // add observation for sample
            addPatientObservationHistory(orderVO);
            // Add Stress,ward,district,city for patient
            addPersionAddresForpatient(orderVO);
            // add sample human
            addSampleHuman(orderVO);
            sampleItem(orderVO);
            tx.commit();
            return FWD_SUCCESS;
        } catch (LIMSRuntimeException lre) {
            tx.rollback();
            LogEvent.logError("addNewOrder()", "Add new order for web Service", lre.toString());
            return FWD_FAIL;
        }
    }

    /**
     * Add New sample get data from web service
     */
    private String addSample(OrderVO orderVO) {
        SampleDAO sampleDAO = new SampleDAOImpl();
        // add new sample
        return sampleDAO.insertDataSampleWithAccessionNumber(orderVO.getSample());
    }

    /*
     * (non-Javadoc)
     * 
     * @see vi.mn.state.health.lims.dataexchange.dao.DataExchangeDAO#getTests()
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<Object[]> getTests() {
        List<Object[]> tests;
        try {
            String sql = "SELECT id, description FROM test where test.test_section_id=7";
            Query query = HibernateUtil.getSession().createSQLQuery(sql);
            tests = query.list();
            closeSession();
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "getTests()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getTests()", e);
        }
        return tests;
    }

    private String addPersion(Person person) {
        PersonDAO personDAO = new PersonDAOImpl();
        return personDAO.insertDataWS(person);
    }

    private String addProvider(OrderVO orderVO) {
        ProviderDAO providerDAO = new ProviderDAOImpl();
        return providerDAO.insertDataWS(orderVO.getProvider());
    }

    private String addPatient(OrderVO orderVO) {
    	String patientId="";
		PatientDAO patientDAO = new PatientDAOImpl();
		if(StringUtil.isNullorNill(orderVO.getPatient().getId())){
			patientId= patientDAO.insertDataWS(orderVO.getPatient());
		}else{
			//patientDAO.updateData(orderVO.getPatient());
			patientId= orderVO.getPatient().getId();
		}
		return patientId;
	}

    /**
	 * 
	 */
    private String addPatientObservationHistory(OrderVO orderVO) {
		ObservationHistoryDAO observationHistoryDAO = new ObservationHistoryDAOImpl();
		if (!orderVO.getDiagnosis().equals("")
				&& orderVO.getDiagnosis() != null) {
			ObservationHistory observationHistory = new ObservationHistory();
			observationHistory.setSysUserId(orderVO.getEtorId());
			observationHistory
					.setObservationHistoryTypeId(OBSERVATION_HISTORY_TYPE_DIAGNOSIS_ID);
			observationHistory.setPatientId(orderVO.getPatient().getId());
			observationHistory.setSampleId(orderVO.getSample().getId());
			observationHistory.setValueType(OBSERVATION_VALUE_TYPE);
			observationHistory.setValue(orderVO.getDiagnosis());
			observationHistoryDAO.insertDataWS(observationHistory);
		}
		if (orderVO.getAge() != null && !orderVO.getAge().equals("")) {
			observationHistoryDAO = new ObservationHistoryDAOImpl();
			ObservationHistory observationHistory = new ObservationHistory();
			observationHistory.setSysUserId(orderVO.getEtorId());
			observationHistory
					.setObservationHistoryTypeId(OBSERVATION_HISTORY_TYPE_AGE_ID);
			observationHistory.setPatientId(orderVO.getPatient().getId());
			observationHistory.setSampleId(orderVO.getSample().getId());
			observationHistory.setValueType(OBSERVATION_VALUE_TYPE);
			observationHistory.setValue(orderVO.getAge());

			observationHistoryDAO.insertDataWS(observationHistory);
		}
		return FWD_SUCCESS;
	}

	/**
	 * Add Stress,ward,district,city for patient
	 */
	private String addPersionAddresForpatient(OrderVO orderVO) {
		PersonAddressDAO personAddressDAO = new PersonAddressDAOImpl();
		PersonAddress personAddress = new PersonAddress();
		if (orderVO.getCity() != null && !orderVO.getCity().equals("")) {
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_CITY);
			personAddress.setType(TYPE_TEXT);
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getCity());
			personAddressDAO.insert(personAddress);
		}
		if (orderVO.getDistrict() != null && !orderVO.getDistrict().equals("")) {
			personAddressDAO = new PersonAddressDAOImpl();
			personAddress = new PersonAddress();
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_DISTRICT);
			personAddress.setType(TYPE_DICTIONARY);
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getDistrict());
			personAddressDAO.insert(personAddress);
		}
		if (orderVO.getWard() != null && !orderVO.getWard().equals("")) {
			personAddressDAO = new PersonAddressDAOImpl();
			personAddress = new PersonAddress();
			personAddress.setSysUserId(orderVO.getEtorId());
			personAddress.setAddressPartId(ADDRESS_PART_WARD);
			personAddress.setType("T");
			personAddress.setPersonId(orderVO.getPatient().getPersonPatient()
					.getId());
			personAddress.setValue(orderVO.getWard());
			personAddressDAO.insert(personAddress);
		}
		return "";
	}

    private boolean addSampleHuman(OrderVO orderVO) {
        SampleHuman sampleHuman = new SampleHuman();
        sampleHuman.setSysUserId(orderVO.getEtorId());
        sampleHuman.setSampleId(orderVO.getSample().getId());
        sampleHuman.setProviderId(orderVO.getProvider().getId());
        sampleHuman.setPatientId(orderVO.getPatient().getId());
        SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
        return sampleHumanDAO.insertData(sampleHuman);
    }

    private String sampleItem(OrderVO orderVO) {
        // get all list test
        int i = 0;
        for (TypeOfSample type : orderVO.getTypeOfSample()) {
            i++;
            // TypeOfSampleDAO typeOfSample=new TypeOfSampleDAOImpl();
            // add new all sample of
            SampleItem firstSampleItem = new SampleItem();
            firstSampleItem.setSysUserId(orderVO.getEtorId());
            // sampleItem.setTypeOfSample(new
            // TypeOfSample(typeOfSample.getTypeOfSampleById(type.getId())));
            firstSampleItem.setSample(orderVO.getSample());
            firstSampleItem.setTypeOfSample(orderVO.getTypeOfSample().get(0));
            firstSampleItem.setSortOrder(String.valueOf(i));
            firstSampleItem.setLastupdated(new Timestamp(new java.util.Date().getTime()));
            firstSampleItem.setStatusId(SAMPLE_ITEM_STATUS_ID);
            SampleItemDAO sampleItemDAO = new SampleItemDAOImpl();

            firstSampleItem.setId(sampleItemDAO.insertDataWS(firstSampleItem));
            orderVO.getLiSampleItem().add(firstSampleItem);
            // add data etor
            //addEtorDataMapping(orderVO, sampleItem);

            addAnalysis(orderVO, type, firstSampleItem, true);
            // add analysis for LIS add result
            // sampleItem.setId(sampleItemDAO.insertDataWS(sampleItem));
           
			try {
				 SampleItem secondSampleItem = (SampleItem) firstSampleItem.clone();
				 secondSampleItem.setSortOrder(String.valueOf(Integer.valueOf(firstSampleItem.getSortOrder()).intValue() + 1));
				 secondSampleItem.setId(sampleItemDAO.insertDataWS(secondSampleItem));
				 addAnalysis(orderVO, type, secondSampleItem, false);
				 addEtorDataMapping(orderVO, secondSampleItem);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
           
        }
        return FWD_SUCCESS;
    }

    private String addAnalysis(OrderVO orderVO, TypeOfSample typeOfSample, SampleItem sampleItem, boolean flag) {
        // get test from SET of order web service
        orderVO.getLiTest().clear();
        
        
        for (Entry<String, String> key : orderVO.getMapTest().entrySet()) {
            // if type of sample equal type of sample of test the
            if (typeOfSample.getId().equals(key.getValue())) {
                Test test = new Test();
                TestDAO testDAO = new TestDAOImpl();
                test = testDAO.getTestById(key.getKey());
                orderVO.getLiTest().add(test);
            }
        }
        
        // add analysis when have test and sample item
        for (Test test : orderVO.getLiTest()) {
            Analysis analysis = new Analysis();
            analysis.setSysUserId(orderVO.getEtorId());
            analysis.setSampleItem(sampleItem);
            analysis.setTestSection(orderVO.getTestSection());
            analysis.setTest(test);
            analysis.setAnalysisType(IActionConstants.ANALYSIS_TYPE_MANUAL);
            if (flag == true) {
                analysis.setStatusId(ANALYSIS_STATUS_ID);
            } else {
                analysis.setStatusId(ANALYSIS_STATUS_ID_YET);
            }
            analysis.setSampleTypeName(typeOfSample.getDescription());
            AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            analysis.setId(analysisDAO.insertDataWS(analysis, false));
            // if flag = true then add result from etor else not have result
            if (flag) {
                // if have result child get rchild
                Result resultchild = new Result();
                // get result child
                if (orderVO.getLiTestResultVO().size() >= 2) {
                    for (TestResultVO testResultVO : orderVO.getLiTestResultVO()) {
                        if (NumberUtils.stringToInt(testResultVO.getResult()) == 0
                                && testResultVO.getTestId().equals(test.getId())) {
                            resultchild.setAnalysis(analysis);
                            resultchild.setSysUserId(orderVO.getEtorId());
                            resultchild.setIsReportable(IS_REPORTABLE);
                            resultchild.setResultType(TYPE_A);
                            resultchild.setValue(testResultVO.getResult());
                            resultchild.setSortOrder(SORT_ORDER);
                            resultchild.setSignificantDigits(SIGN_IF_CANTDIGIT);
                        }
                    }
                }

                // check if have result then insert resource
                Result result = new Result();
                ResultDAO resultDAO = new ResultDAOImpl();
                for (TestResultVO testResultVO : orderVO.getLiTestResultVO()) {
                    if (test.getId().equals(testResultVO.getTestId())
                            && NumberUtils.stringToInt(testResultVO.getResult()) > 0) {

                        TestResultDAO testResultDAO = new TestResultDAOImpl();
                        TestResult testResult = new TestResult();
                        // check if test result is true

                        testResult.setId(testResultVO.getResult());
                        testResult = testResultDAO.getTestResultById(testResult);
                        // if test reslut not null then save test result with
                        // analysis
                        if (testResult.getValue() != null) {
                            result.setSysUserId(orderVO.getEtorId());
                            result.setAnalysis(analysis);
                            result.setIsReportable(IS_REPORTABLE);
                            result.setResultType(testResult.getTestResultType());
                            result.setValue(testResult.getValue());
                            result.setSortOrder(SORT_ORDER);
                            result.setTestResultId(testResult.getId());
                            result.setSignificantDigits(SIGN_IF_CANTDIGIT);
                            result.setId(resultDAO.insertDataWS(result));
                        }
                    }// end if
                    if (test.getId().equals(testResultVO.getTestId())
                            && NumberUtils.stringToInt(testResultVO.getResult()) == 0) {
                        resultchild.setParentResult(result);
                        resultDAO.insertDataWS(resultchild);
                        resultchild = new Result();
                    }
                }
            }
        }

        return FWD_SUCCESS;
    }

    /**
     * @param orderVO
     * @param sampleItem
     *            success if insert data success
     */
	private boolean addEtorDataMapping(OrderVO orderVO, SampleItem sampleItem) {
		EtorDataMappingDAO etorDataMappingDAO = new EtorDataMappingDAOImpl();
		EtorDataMapping etorDataMapping = new EtorDataMapping();
		etorDataMapping.setSample(orderVO.getSample());
		etorDataMapping.setSampleItem(sampleItem);
		etorDataMapping.setLisStatus(Integer.parseInt(ANALYSIS_STATUS_ID_YET));
		etorDataMapping.setEtorStatus(Integer.parseInt(ANALYSIS_STATUS_ID));
		etorDataMapping.setEtorUserId(orderVO.getEtorId());
		return etorDataMappingDAO.insertData(etorDataMapping);
	}

    @SuppressWarnings("unchecked")
	@Override
    public Object[] getIdForUsername(String username) {
        Object[] result = null;
        try {
            String sql = "select id, org_id from etor_user where username = :username  limit 1";
            SQLQuery query = HibernateUtil.getSession().createSQLQuery(sql);
            query.setString("username", username);
            List<Object[]> etorUsers = query.list();
            if (etorUsers != null) {
                result = etorUsers.get(0);
            }
            closeSession();
        } catch (HibernateException e) {
            LogEvent.logError("DataExchangeDAOImpl", "getIdForUsername()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl getIdForUsername()", e);
        }
        return result;
    }
    
    /**
     * Validate User by username and password
     * 
     * @param aimUserDTO
     * @return String json (username, full_name, isValid)
     */
    @Override
    public String validateAIMLogin(AimUserDTO aimUserDTO) {
        String result = "";
        try {
            Login login = new Login();
            login.setLoginName(aimUserDTO.getUsername().trim());
            login.setPassword(aimUserDTO.getPassword());
            LoginDAO loginDao = new LoginDAOImpl();
            Login userInfo = loginDao.getUserProfile(login.getLoginName());
            String isValid = "true";
            if ( userInfo == null ) {
                //Error: User profile not existing.
                isValid = "false";
            } else {
                if ( userInfo.getAccountDisabled().equalsIgnoreCase(YES) ) {
                    //Error: Account is disabled.
                    isValid = "false";
                }
                if ( userInfo.getAccountLocked().equalsIgnoreCase(YES) ) {
                    //Error: Account is locked.
                    isValid = "false";
                }
                if ( userInfo.getPasswordExpiredDayNo() <= 0 ) {
                    //Error: Password has expired.
                    isValid = "false";
                }
            }
            Login resLogin = loginDao.getValidateLogin(login);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", aimUserDTO.getUsername());
            if (resLogin == null) {
                isValid = "false";
                jsonObject.put("fullname", "");
            } else {
                SystemUserDAO systemUserDao = new SystemUserDAOImpl();
                SystemUser systemUser = systemUserDao.getDataForLoginUser(resLogin.getLoginName());
                jsonObject.put("fullname", systemUser.getNameForDisplay());
            }
            jsonObject.put("isValid", isValid);
            result = jsonObject.toString();
            
        } catch (HibernateException e) {
            LogEvent.logError("DataExchangeDAOImpl", "validateAIMLogin()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl validateAIMLogin()", e);
        } catch (JSONException e) {
            LogEvent.logError("DataExchangeDAOImpl", "validateAIMLogin()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl validateAIMLogin()", e);
        }
        
        return result;
    }

    /**
     * Add AIM Test Result to openELIS
     * 
     * @param testResultParameterDTO
     * @return String "success" or "fail"
     */
    @Override
    public String addAIMTestResult(TestResultParameterDTO testResultParameterDTO) {
        String result = FWD_SUCCESS;
        try {
        	// Set Default return "success"
        	JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", result);
            result = jsonObject.toString();
            
    		result = insertAIMTestResults(testResultParameterDTO.getAccessNumber(), testResultParameterDTO.getTestId(),
						    				testResultParameterDTO.getMainResult(), testResultParameterDTO.getSubResult(),
						    				  testResultParameterDTO.getBeginDate());
            
        } catch (HibernateException e) {
            LogEvent.logError("DataExchangeDAOImpl", "addAIMTestResult()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl addAIMTestResult()", e);
            
        } catch (JSONException e) {
            LogEvent.logError("DataExchangeDAOImpl", "addAIMTestResult()", e.toString());
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl addAIMTestResult()", e);
        }
        
        return result;
    }
    
    /**
     * Insert AIM Test Result
     * 
     * @param accessionNumber
     * @param testId
     * @param mainResult
     * @param subResult
     * @param beginDate
     * @return String "success" or "fail"
     */
    public String insertAIMTestResults(String accessionNumber, String testId, String mainResult, String subResult, String beginDate) {
        String result = FWD_FAIL;
        String errorMsg = "";
        
        try {
        	// Getting the current date
        	Calendar calendar = Calendar.getInstance();
        	Date currentDate = calendar.getTime();
        	java.sql.Date sqlBeginDate = new java.sql.Date(currentDate.getTime());
        	
        	if (!StringUtil.isNullorNill(beginDate.trim())) {
        		sqlBeginDate = DateUtil.convertStringDateToSqlDateAIM(beginDate);
        	}
            
        	JSONObject jsonObject = new JSONObject();
        	//Get all results of the accession number
            ResultDAO resultDao = new ResultDAOImpl();
            List<BigDecimal> listResultId = resultDao.getListofResultId(accessionNumber, testId);
            
            //Get test result
        	TestResultDAO testResultDAO = new TestResultDAOImpl();
        	TestResultDTO testResultDTO = testResultDAO.getTestResultByResult(accessionNumber, mainResult, testId);
            AnalysisDAO analysisDAO = new AnalysisDAOImpl();
            
            //Check if there are existing results for the accession number
            if (!listResultId.isEmpty() && (listResultId.size() > 0) && (testResultDTO != null)) {
            	
            	boolean updateSuccess = true; 
            	if (testResultDTO != null) {
                     Analysis newAnalysis = analysisDAO.getAnalysisByIdAIM(testResultDTO.getAnalysisId());
                 
	            	//Update the existing result value if old value is 0 (for mainResult) or empty (for subResult)
	            	for (BigDecimal resultId : listResultId) {
	            		Result retResult = resultDao.getResultByIdAIM(String.valueOf(resultId));
	
	        			//(for type="N") Check if results were deleted (mainResult="" or null or "null")
	        			if (StringUtil.isNullorNill(String.valueOf(retResult.getValue())) && !retResult.getResultType().equalsIgnoreCase("A")) {
	                        newAnalysis.setCompletedDate(sqlBeginDate);
	        				retResult.setAnalysis(newAnalysis);
	        				//Update the old mainResult value "" or null or "null"
	                        resultDao.updateDataAIM(retResult, mainResult, testResultDTO.getSysUserId());
	                        
	        			//(for type="D") Check if results were deleted (mainResult=0, subResult="")
	        			} else if (retResult.getValue().equalsIgnoreCase("0") && retResult.getResultType().equalsIgnoreCase("D")) {
	                        newAnalysis.setCompletedDate(sqlBeginDate);
	        				retResult.setAnalysis(newAnalysis);
	        				//Update the old mainResult value "0"
	    					resultDao.updateDataAIM(retResult, mainResult, testResultDTO.getSysUserId());
	    					
	    				//(for type="A")	
	        			} else if (retResult.getResultType().equalsIgnoreCase("A")) {
	                        newAnalysis.setCompletedDate(sqlBeginDate);
	        				retResult.setAnalysis(newAnalysis);
	        				//Update the old subResult value
	    					resultDao.updateDataAIM(retResult, subResult, testResultDTO.getSysUserId());
	    					
	            		} else {
	            			//Return error message when result is already existing for the access number
	                    	errorMsg = "There is already an existing result. Please delete it before adding a new one.";
	                    	updateSuccess = false;
	                    	break;
	            		}
	                }
            	}
            	if (updateSuccess) {
                	result = FWD_SUCCESS;
            	}
            	
            } else {
            	//No existing result for the accession number, new result data are inserted (mainResult, subResult)
                if (testResultDTO != null) {
                    Analysis newAnalysis = analysisDAO.getAnalysisByIdAIM(testResultDTO.getAnalysisId());
                    newAnalysis.setCompletedDate(sqlBeginDate);
                    //Insert Main Result
                    Result resParent = new Result();
                    resParent.setAnalysis(newAnalysis);
                    resParent.setSortOrder(String.valueOf(0));
                    resParent.setIsReportable("N");
                    resParent.setResultType(testResultDTO.getTestResultType());
                    resParent.setValue(mainResult);
                    TestResult tr = new TestResult();
                    tr.setId(testResultDTO.getTestResultId());
                    resParent.setTestResult(testResultDAO.getTestResultById(tr));
                    resParent.setLastupdated(new Timestamp(new Date().getTime()));
                    resParent.setMinNormal(Double.valueOf(0));
                    resParent.setMaxNormal(Double.valueOf(0));
                    resParent.setSignificantDigits(-1);
                    resParent.setGrouping(0);
                    resParent.setSysUserId(testResultDTO.getSysUserId());
                    String insertParentResult = resultDao.insertDataWS(resParent);
                    
                    if ((subResult != null) && (insertParentResult != null)) {
                        //Insert Sub Result
                        Result resChild = new Result();
                        resChild.setAnalysis(newAnalysis);
                        resChild.setSortOrder(String.valueOf(0));
                        resChild.setIsReportable("N");
                        resChild.setResultType("A");
                        resChild.setValue(subResult);
                        resChild.setLastupdated(new Timestamp(new Date().getTime()));
                        resChild.setMinNormal(Double.valueOf(0));
                        resChild.setMaxNormal(Double.valueOf(0));
                        resChild.setParentResult(resultDao.getResultById(insertParentResult));
                        resChild.setSignificantDigits(0);
                        resChild.setGrouping(0);
                        resChild.setSysUserId(testResultDTO.getSysUserId());
                        resultDao.insertData(resChild);
                    }
                    result = FWD_SUCCESS;
                    
                } else {
                	errorMsg = "No test result found.";
                }
            }
            jsonObject.put("message", result);
            if (result.equalsIgnoreCase("fail")) {
            	jsonObject.put("error", errorMsg);
            }
            result = jsonObject.toString();
            
            closeSession();
            
        } catch (JSONException e) {
            LogEvent.logError("DataExchangeDAOImpl", "insertAIMTestResults()", Arrays.toString(e.getStackTrace()));
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl insertAIMTestResults()", e);
            
        } catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "insertAIMTestResults()", Arrays.toString(e.getStackTrace()));
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl insertAIMTestResults()", e);
        }
        
        return result;
    }
    
    public String insertTestAndResultDAO (String accessionNumber, String testId, String mainResult, String subResult, String beginDate, int instrumentId) {
        String result = FWD_FAIL;
        
        try {
        	Connection conn = new PostgreSQLConnector().getConnection();

			String testIdsForInserting = "";
			int countSampItem = 0;
			
			int typeOfSample = 52;
			
			if (instrumentId == 9) {
				typeOfSample = 9;
			}
			LogEvent.logInfo("DataExchangeDAOImpl", "insertTestAndResultDAO()", " start for accession number " + accessionNumber);
			// Get sample_item_id that has test Id already
			String sqlValidateSampItem = "SELECT count(*) from validate_sample_item_test WHERE accession_number ='" + accessionNumber + "' AND typeosamp_id::TEXT = '" + typeOfSample + "'" ;
			LogEvent.logInfo("DataExchangeDAOImpl", "insertTestAndResultDAO()", sqlValidateSampItem);
			PreparedStatement prpValidateSampItem = conn.prepareStatement(sqlValidateSampItem);
			ResultSet rsValidateSampItem = prpValidateSampItem.executeQuery();
			rsValidateSampItem.next();
			countSampItem = rsValidateSampItem.getInt(1);
			rsValidateSampItem.close();

			// If there doesn't exist, insert new sample_item and all tests
			if(countSampItem == 0){
				CallableStatement csInsertTest = conn.prepareCall("{call auto_insert(?,?,?)}");
				csInsertTest.setString(1, accessionNumber);
				csInsertTest.setString(2, testId);
				csInsertTest.setInt(3, typeOfSample);
				LogEvent.logInfo("DataExchangeDAOImpl", "insertTestAndResultDAO()", csInsertTest.toString());
				csInsertTest.execute();
			}

			// If there exists:
			// Get the max of sample_item_id
			// Check that does sample_item_id contain tests? If yes, do nothing. If no, insert tests into that sample_item_id
			else{
				String testIdsFromDb = "";
				String sampItemIdsFromDb = "";
				String sampItemId = "";
				String sqlGetMaxSampItem = "SELECT * from validate_sample_item_test WHERE accession_number ='" + accessionNumber  + "' AND typeosamp_id::TEXT = '" + typeOfSample + "'";
				
				PreparedStatement prpGetMaxSampItem = conn.prepareStatement(sqlGetMaxSampItem);
				ResultSet rsGetMaxSampItem = prpGetMaxSampItem.executeQuery();
				while(rsGetMaxSampItem.next()){
					testIdsFromDb += String.valueOf(rsGetMaxSampItem.getInt("test_id"));
					testIdsFromDb += ",";
					sampItemId = String.valueOf(rsGetMaxSampItem.getInt("samp_item_id"));
					sampItemIdsFromDb += sampItemId;
					sampItemIdsFromDb += ",";
				}
				rsGetMaxSampItem.close();

				testIdsFromDb = testIdsFromDb.substring(0, testIdsFromDb.length() - 1);
				sampItemIdsFromDb = sampItemIdsFromDb.substring(0, sampItemIdsFromDb.length() - 1);
				String[] arrTestId = testIdsFromDb.split(",");
				String[] arrSampItemId = sampItemIdsFromDb.split(",");

				//Get testIds belong to max sample_item_id that has type_of_sample is blood
				String testIdsOfSampItem = "";
				for(int j = arrSampItemId.length - 1; j >= 0; j--){
					if(arrSampItemId[j].equals(sampItemId)){
						testIdsOfSampItem += arrTestId[j];
						testIdsOfSampItem += ",";
					}
				}

				testIdsOfSampItem = testIdsOfSampItem.substring(0, testIdsOfSampItem.length() - 1);
				String[] arrTestIdsOfSampItem = testIdsOfSampItem.split(",");
				String newTestId = testId;
				boolean isExisted = false;
				for(int h = 0; h < arrTestIdsOfSampItem.length ; h++){
					String existedTestId = arrTestIdsOfSampItem[h];
					if(existedTestId.equals(newTestId)){
						isExisted = true;
						break;
					}
				}

				if(!isExisted){
					testIdsForInserting += newTestId;
					testIdsForInserting += ",";
				}
					
				if(!StringUtil.isNullorNill(testIdsForInserting)){
					testIdsForInserting = testIdsForInserting.substring(0, testIdsForInserting.length() - 1);

					CallableStatement csInsertTest = conn.prepareCall("{call auto_insert_test(?,?)}");
					csInsertTest.setString(1, sampItemId);
					csInsertTest.setString(2, testIdsForInserting);
					csInsertTest.execute();
					LogEvent.logInfo("DataExchangeDAOImpl", "insertTestAndResultDAO()", csInsertTest.toString());
				}
			}
				
			CallableStatement cs = null;

			cs = conn.prepareCall("{call sp_result(?,?,?,?,?)}");
			cs.setString(1, accessionNumber);
			cs.setInt(2, Integer.parseInt(testId));
			cs.setString(3, mainResult);
			cs.setString(4, subResult);
			cs.setInt(5, instrumentId);
			cs.execute();
			LogEvent.logInfo("DataExchangeDAOImpl", "insertTestAndResultDAO()", cs.toString());
			if (cs != null) {
				cs.close();
			}
			
			conn.close();
			result = FWD_SUCCESS;
        	
        }catch (Exception e) {
            LogEvent.logError("DataExchangeDAOImpl", "insertTestAndResultDAO()", Arrays.toString(e.getStackTrace()));
            throw new LIMSRuntimeException("Error in DataExchangeDAOImpl insertTestAndResultDAO()", e);
        }
        return result;
    }
}
