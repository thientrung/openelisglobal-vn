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
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.services.ResultLimitService;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.patient.dao.PatientDAO;
import us.mn.state.health.lims.patient.daoimpl.PatientDAOImpl;
import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.result.dao.ResultDAO;
import us.mn.state.health.lims.result.daoimpl.ResultDAOImpl;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.sample.dao.SampleDAO;
import us.mn.state.health.lims.sample.daoimpl.SampleDAOImpl;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.samplehuman.dao.SampleHumanDAO;
import us.mn.state.health.lims.samplehuman.daoimpl.SampleHumanDAOImpl;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;

/**
 * The ResultLimitValidationProvider class is used to validate via AJAX.
 */
public class ResultLimitValidationProvider extends BaseValidationProvider{

    public ResultLimitValidationProvider(){
        super();
    }

    public ResultLimitValidationProvider( AjaxServlet ajaxServlet ){
        this.ajaxServlet = ajaxServlet;
    }

    @Override
    public void processRequest( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        ResultDAO resultDAO = new ResultDAOImpl();
        TestResultDAO testResultDAO = new TestResultDAOImpl();
        TestDAO testDAO = new TestDAOImpl();
        PatientDAO patientDAO = new PatientDAOImpl();
        Result result = new Result();
        Test test = new Test();
        TestResult testResult = new TestResult();
        
        String queryResponse = VALID;
        String outOfRangeLow = "LOW";
        String outOfRangeHigh = "HIGH";
        String fieldId = request.getParameter( "fieldId" );
        String resultId = request.getParameter( "resultId" );
        String resultValue = request.getParameter( "resultValue" );
        String accessionNumber = request.getParameter( "accessionNumber" );
        
        if (!StringUtil.isNullorNill(resultId) && !StringUtil.isNullorNill(accessionNumber)) {
            result = resultDAO.getResultById(resultId);
            if (result != null) {
                TestResult tr = new TestResult();
                tr.setId(result.getTestResultId());
                testResult = testResultDAO.getTestResultById(tr);
                if (testResult != null) {
                    test = testDAO.getTestById(testResult.getTest());
                    //test.setId("248");    // for testing
                    test = testDAO.getTestById(test);
                    Patient patient = getPatientByAccessionNumber(accessionNumber);
                    if (test != null && patient != null) {
                        ResultLimit resultLimit = new ResultLimitService().getResultLimitForTestAndPatient(test, patient);
                        //System.out.println("ResultValue: " + resultValue);
                        if (resultLimit != null) {
                            //System.out.println("ResultLimit: " + resultLimit.getLowNormal() + " to " + resultLimit.getHighNormal());
                            if (Double.valueOf(resultValue) < resultLimit.getLowNormal()) {
                                queryResponse = INVALID + "-" + outOfRangeLow;
                            } else if (Double.valueOf(resultValue) > resultLimit.getHighNormal()) {
                                queryResponse = INVALID + "-" + outOfRangeHigh;
                            }
                        }
                    }
                }
            }
        }

        response.setCharacterEncoding( "UTF-8" );
        ajaxServlet.sendData( fieldId, queryResponse, request, response );
    }
    
    private Patient getPatientByAccessionNumber(String accessionNumber) {
        SampleDAO sampleDAO = new SampleDAOImpl();
        Sample sample = sampleDAO.getSampleByAccessionNumber(accessionNumber);

        if (sample != null && !GenericValidator.isBlankOrNull(sample.getId())) {
            SampleHumanDAO sampleHumanDAO = new SampleHumanDAOImpl();
            return sampleHumanDAO.getPatientForSample(sample);
        }

        return new Patient();
    }
    
}
