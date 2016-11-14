 /**
 * @(#) DistrictAutocompleteProvider.java 01-00 Aug 17, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update Aug 17, 2016
 */
package us.mn.state.health.lims.common.provider.autocomplete;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.dictionary.valueholder.Dictionary;
import us.mn.state.health.lims.district.dao.DistrictDAO;
import us.mn.state.health.lims.district.daoimpl.DistrictDAOImpl;

/**
 * Detail description of processing of this class.
 *
 * @author trungtt.kd
 */
public class DistrictAutocompleteProvider extends BaseAutocompleteProvider {
    
    /**
    * @see org.ajaxtags.demo.servlet.BaseAjaxServlet
    *  #getXmlContent(javax.servlet.http.HttpServletRequest,
    *  javax.servlet.http.HttpServletResponse)
    */
   public List<Dictionary> processRequest(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
       
       String districtName = request.getParameter("districtName");
       DistrictDAO distDAO = new DistrictDAOImpl();
       List<Dictionary> list = distDAO.getDistrictsEntyByName(districtName);
       
       return list;
   }

}
