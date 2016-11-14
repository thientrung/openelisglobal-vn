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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.common.provider.validation;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.servlet.validation.AjaxServlet;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.dictionary.dao.DictionaryDAO;
import us.mn.state.health.lims.dictionary.daoimpl.DictionaryDAOImpl;
import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.daoimpl.DistrictDAOImpl;
import us.mn.state.health.lims.district.valueholder.District;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.dao.TestSectionDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;


public class TestSectionTestMapProvider extends BaseValidationProvider {

    TestSectionDAO testSectionDAO = new TestSectionDAOImpl();
    TestDAO testDAO = new TestDAOImpl();
    TestSection testSection = new TestSection();
    
	public TestSectionTestMapProvider() {
		super();
	}

	public TestSectionTestMapProvider(AjaxServlet ajaxServlet) {
		this.ajaxServlet = ajaxServlet;
	}

	public void processRequest(HttpServletRequest request,
			                    HttpServletResponse response) throws ServletException, IOException {
	    
	    // Need to use 'URLDecoder.decode' since calling 'request.getParameter' directly does not handle the encoding properly
        String decodedRequest = URLDecoder.decode(request.getQueryString(), "UTF-8");
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        for (String paramPair : decodedRequest.split("&")) {
           String[] pairs = paramPair.split("=", 2);
           paramMap.put(pairs[0], pairs[1]);
        }
		// Get id from request
		String testSectionId = (String) paramMap.get("testSectionId");
		String result = getAllTestSectionTests(testSectionId);

        ajaxServlet.sendData(result, request, response);
	}

	public String getAllTestSectionTests(String targetId) throws LIMSRuntimeException {
	    String strJSON = "";

		// Check if testSectionId is null or empty
		if ((!StringUtil.isNullorNill(targetId))) {
		    
            // Check if getAllTests "0" or in getTestSection "non-zero"
		    if (!StringUtil.isNullorNill(targetId) && !targetId.equalsIgnoreCase("0")) {
		        // Get all tests data by testSectionId
	            List<String> listTests = new ArrayList();
	            listTests = testDAO.getTestIdsByTestSection(targetId); // testSectionId
		        for (String testId : listTests) {
		            StringBuffer s = new StringBuffer();
	                s.append(testId);
	                s.append(",");
	                strJSON = (s.substring(0, s.lastIndexOf(","))).toString();
	            }
		    } else {
                // Get all tests data
	            List<Test> listTests = new ArrayList<Test>();
	            listTests = testDAO.getAllActiveTests(false);  // get all active row in Test table
                strJSON = createCustomJSONMessage(listTests);
		    }
		}
		
		return strJSON;
	}
	
	public String createCustomJSONMessage(List<Test> listTests) {
        StringBuffer s = new StringBuffer();
	    String testSectionId = "";
        String testId = "";
        boolean isInitLoad = true;
        boolean isNewTestSection = false;
        
        if (listTests.size() > 0) {
            // Start of message
            s.append("{");
            
            for (Test test : listTests) {
               
                if (StringUtil.isNullorNill(testSectionId)) {
                    testSectionId = test.getTestSection().getId();
                
                } else {
                    // Get initial or new testSectionId
                    if (!testSectionId.equals(test.getTestSection().getId()) && test != null && test.getTestSection() != null && 
                                !StringUtil.isNullorNill(test.getTestSection().getId()) ) {
                        testSectionId = test.getTestSection().getId();
                        isNewTestSection = true;
                    } else {
                        isNewTestSection = false;
                        s.append(",");
                    }
                }
                
                // Append Test Section
                testSection.setId(testSectionId);
                testSectionDAO.getData(testSection);
                if (isInitLoad || isNewTestSection) {
                    if (!isInitLoad) {
                        s.append("],");
                    }
                    s.append("\"" + testSection.getId() + "\":[");
                    isInitLoad = false;
                }
                
                // Append Test
                testId = test.getId();
                test.setId(testId);
                testDAO.getData(test);
                s.append("\"" + test.getName() + "\"");
            }
            
            s.append("]}");
            // End of message
        }	    
        
        return s.toString();
	}

}
