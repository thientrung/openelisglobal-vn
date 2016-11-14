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


public class CityDistrictMapProvider extends BaseValidationProvider {

    DictionaryDAO dictionaryDAO = new DictionaryDAOImpl();
    DistrictDAO districtDAO = new DistrictDAOImpl();
    Dictionary dictionary = new Dictionary();
    
	public CityDistrictMapProvider() {
		super();
	}

	public CityDistrictMapProvider(AjaxServlet ajaxServlet) {
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
		String cityId = (String) paramMap.get("cityId");
		String result = getAllCityDistricts(cityId);

        ajaxServlet.sendData(result, request, response);
	}

	public String getAllCityDistricts(String targetId) throws LIMSRuntimeException {
	    String strJSON = "";

		// Check if cityId is null or empty
		if ((!StringUtil.isNullorNill(targetId))) {
		    
            // Check if getAllDistricts "0" or in getDictionary "non-zero"
		    if (!StringUtil.isNullorNill(targetId) && !targetId.equalsIgnoreCase("0")) {
		        // Get all districts data by city_id
	            List<String> listDistricts = new ArrayList();
		        listDistricts = districtDAO.getAllDistrictsByCity(targetId); // cityId
		        for (String districtId : listDistricts) {
		            StringBuffer s = new StringBuffer();
	                s.append(districtId);
	                s.append(",");
	                strJSON = (s.substring(0, s.lastIndexOf(","))).toString();
	            }
		    } else {
                // Get all districts data
	            List<District> listDistricts = new ArrayList<District>();
                listDistricts = districtDAO.getAllDistricts();  // get all row in District table
                strJSON = createCustomJSONMessage(listDistricts);
		    }
		}
		
		return strJSON;
	}
	
	public String createCustomJSONMessage(List<District> listDistricts) {
        StringBuffer s = new StringBuffer();
	    String cityId = "";
        String districtId = "";
        boolean isInitLoad = true;
        boolean isNewCity = false;
        
        if (listDistricts.size() > 0) {
            // Start of message
            s.append("{");
            
            for (District district : listDistricts) {
               
                if (StringUtil.isNullorNill(cityId)) {
                    cityId = district.getCity().getId();
                
                } else {
                    // Get initial or new cityId
                    if (!cityId.equals(district.getCity().getId()) && district != null && district.getCity() != null && 
                                !StringUtil.isNullorNill(district.getCity().getId()) ) {
                        cityId = district.getCity().getId();
                        isNewCity = true;
                    } else {
                        isNewCity = false;
                        s.append(",");
                    }
                }
                
                // Append City
                dictionary.setId(cityId);
                dictionaryDAO.getData(dictionary);
                if (isInitLoad || isNewCity) {
                    if (!isInitLoad) {
                        s.append("],");
                    }
                    s.append("\"" + dictionary.getDictEntry() + "\":[");
                    isInitLoad = false;
                }
                
                // Append District
                districtId = district.getDistrictEntry().getId();
                dictionary.setId(districtId);
                dictionaryDAO.getData(dictionary);
                s.append("\"" + dictionary.getDictEntry() + "\"");
            }
            
            s.append("]}");
            // End of message
        }	    
        
        return s.toString();
	}

}
