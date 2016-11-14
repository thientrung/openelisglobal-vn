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
package us.mn.state.health.lims.common.provider.query;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.json.simple.JSONObject;

import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.localization.valueholder.Localization;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.daoimpl.ResultLimitDAOImpl;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.test.valueholder.Test;

public class TestNamesProvider extends BaseQueryProvider {

	protected AjaxServlet ajaxServlet = null;

    private ResultLimitDAO resultLimitDAO = new ResultLimitDAOImpl();

    @Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String testId = request.getParameter("testId");

        String jResult;
        JSONObject jsonResult = new JSONObject();
        String jString;

		if (GenericValidator.isBlankOrNull(testId) ){
			jResult = INVALID;
			jString = "Internal error, please contact Admin and file bug report";
		} else {
            jResult = createJsonTestNames( testId, jsonResult );
            StringWriter out = new StringWriter();
            try{
                jsonResult.writeJSONString( out );
                jString = out.toString();
            }catch( IOException e ){
                e.printStackTrace();
                jResult = INVALID;
                jString = "Internal error, please contact Admin and file bug report";
            }catch( IllegalStateException e ){
                e.printStackTrace();
                jResult = INVALID;
                jString = "Internal error, please contact Admin and file bug report";
            }
        }
		ajaxServlet.sendData(jString, jResult, request, response);

	}

    @SuppressWarnings("unchecked")
    private String createJsonTestNames( String testId, JSONObject jsonResult )throws IllegalStateException{

        if( GenericValidator.isBlankOrNull( testId ) ){
            throw new IllegalStateException( "TestNamesProvider testId was blank.  It must have a value" );
        }
        Test test = new TestService( testId ).getTest();
        
        if( test != null){
            List<ResultLimit> resultLimits = resultLimitDAO.getAllResultLimitsForTest(test);
            if (!resultLimits.isEmpty()) {

                jsonResult.put("resultLimitSize", resultLimits.size());
                JSONObject resultLimitItemObject = new JSONObject();
                for (int x = 0; x < resultLimits.size() ; x++) {
                    JSONObject normalRangeObject = new JSONObject();
                    normalRangeObject.put("minAge", resultLimits.get(x).getMinAge());
                    normalRangeObject.put("maxAge", resultLimits.get(x).getMaxAge());
                    normalRangeObject.put("gender", resultLimits.get(x).getGender());
                    normalRangeObject.put("lowNormal", resultLimits.get(x).getLowNormal());
                    normalRangeObject.put("highNormal", resultLimits.get(x).getHighNormal());
                    normalRangeObject.put("lowValid", resultLimits.get(x).getLowValid());
                    normalRangeObject.put("highValid", resultLimits.get(x).getHighValid());
                    normalRangeObject.put("id", resultLimits.get(x).getId());

                    resultLimitItemObject.put("resultLimitItem_" + x, normalRangeObject);
                }
                jsonResult.put("resultLimit", resultLimitItemObject);
            }

            Localization nameLocalization = test.getLocalizedTestName();
            Localization reportNameLocalization = test.getLocalizedReportingName();

            JSONObject nameObject = new JSONObject();
            nameObject.put("english", nameLocalization.getEnglish());
            nameObject.put("french", nameLocalization.getFrench());
            nameObject.put("vietnamese", nameLocalization.getVietnamese());
            jsonResult.put("name", nameObject);

            JSONObject reportingNameObject = new JSONObject();
            reportingNameObject.put("english", reportNameLocalization.getEnglish());
            reportingNameObject.put("french", reportNameLocalization.getFrench());
            reportingNameObject.put("vietnamese", reportNameLocalization.getVietnamese());
            jsonResult.put("reportingName", reportingNameObject);
            if (test.getUnitOfMeasure() != null) {
                jsonResult.put("unitOfMeasure", test.getUnitOfMeasure().getName());
            }

            return VALID;
        }

        return INVALID;
    }

	@Override
	public void setServlet(AjaxServlet as) {
		this.ajaxServlet = as;
	}

	@Override
	public AjaxServlet getServlet() {
		return this.ajaxServlet;
	}

}
