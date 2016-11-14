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
package us.mn.state.health.lims.test.action;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import us.mn.state.health.lims.common.action.BaseAction;
import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.hibernate.HibernateUtil;
import us.mn.state.health.lims.localization.daoimpl.LocalizationDAOImpl;
import us.mn.state.health.lims.localization.valueholder.Localization;
import us.mn.state.health.lims.resultlimits.dao.ResultLimitDAO;
import us.mn.state.health.lims.resultlimits.daoimpl.ResultLimitDAOImpl;
import us.mn.state.health.lims.resultlimits.valueholder.ResultLimit;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.unitofmeasure.dao.UnitOfMeasureDAO;
import us.mn.state.health.lims.unitofmeasure.daoimpl.UnitOfMeasureDAOImpl;
import us.mn.state.health.lims.unitofmeasure.valueholder.UnitOfMeasure;

/**
 * @author diane benz
 *         <p/>
 *         To change this generated comment edit the template variable "typecomment":
 *         Window>Preferences>Java>Templates. To enable and disable the creation of type
 *         comments go to Window>Preferences>Java>Code Generation.
 */
public class TestRenameUpdate extends BaseAction{

    protected ActionForward performAction( ActionMapping mapping,
                                           ActionForm form, HttpServletRequest request,
                                           HttpServletResponse response ) throws Exception{
        String forward = FWD_SUCCESS;

        BaseActionForm dynaForm = ( BaseActionForm ) form;
        String testId = dynaForm.getString( "testId" );
        String nameEnglish = decodeString(dynaForm.getString( "nameEnglish" ));        
        String nameFrench = decodeString(dynaForm.getString( "nameFrench" ));
        String nameVietnamese = decodeString(dynaForm.getString( "nameVietnamese" ));
        String reportNameEnglish = decodeString(dynaForm.getString( "reportNameEnglish" ));
        String reportNameFrench = decodeString(dynaForm.getString( "reportNameFrench" ));
        String reportNameVietnamese = decodeString(dynaForm.getString( "reportNameVietnamese" ));
        
        // Added by markaae.fr 2016-10-13 03:38PM
        String testUnit = dynaForm.getString( "testUnit" );
        String strResultLimits = dynaForm.getString( "selectedResultLimit" );
        List<IdValuePair> resultLimitList = new ArrayList<IdValuePair>();
        resultLimitList = createResultLimitList(strResultLimits);
        // End of Modification
        
        
        String userId = getSysUserId( request );

        updateTestNames( testId, nameEnglish, nameFrench, nameVietnamese, reportNameEnglish, reportNameFrench, reportNameVietnamese, 
                            userId, testUnit, resultLimitList );

        return mapping.findForward( forward );
    }
    
    private static List<IdValuePair> createResultLimitList(String strResultLimits) {
        ArrayList<IdValuePair> resultLimits = new ArrayList<IdValuePair>();

        //Split into kvpObject
        String[] arrKVPObject = strResultLimits.split(";");
        
        for (int i=0; i < arrKVPObject.length; i++) {
            //Split into key and value
            String[] arrKeyValObject = arrKVPObject[i].split("_");

            /*for (int j=0; j < arrKeyValObject.length; j++) {
                //Split into minimum value and maximum value
                String[] minMaxValueObj = arrKeyValObject[j].split("-");*/
                
                resultLimits.add( new IdValuePair(arrKeyValObject[0], arrKeyValObject[1]) );
                Collections.sort(resultLimits, new Comparator<IdValuePair>() {
                    @Override
                    public int compare( IdValuePair o1, IdValuePair o2 ){
                        return o1.getValue().compareTo( o2.getValue() );
                    }
                } );
            //}
        }

        return resultLimits;
    }
    
    private void updateTestNames( String testId, String nameEnglish, String nameFrench, String nameVietnamese,
            String reportNameVietnamese, String reportNameEnglish, String reportNameFrench, String userId, 
            String testUnit, List<IdValuePair> resultLimitList) {
        
        UnitOfMeasureDAO unitOfMeasureDAO = new UnitOfMeasureDAOImpl();
        ResultLimitDAO resultLimitDAO = new ResultLimitDAOImpl();
        Test test = new TestService( testId ).getTest();
        
        if (test != null) {
            Localization name = test.getLocalizedTestName();
            Localization reportingName = test.getLocalizedReportingName();
            name.setEnglish( nameEnglish.trim() );
            name.setFrench( nameFrench.trim() );
            name.setVietnamese( nameVietnamese.trim());
            name.setSysUserId( userId );
            reportingName.setEnglish( reportNameEnglish.trim() );
            reportingName.setFrench( reportNameFrench.trim() );
            reportingName.setVietnamese( reportNameVietnamese.trim() );
            reportingName.setSysUserId( userId );

            Transaction tx = HibernateUtil.getSession().beginTransaction();
            try {
                new LocalizationDAOImpl().updateData( name );
                new LocalizationDAOImpl().updateData( reportingName );
                
                // update test unitOfMeasure (both name & description)
                if (test.getUnitOfMeasure() != null) {
                    UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
                    unitOfMeasure.setSysUserId(userId);
                    unitOfMeasure.setId(test.getUnitOfMeasure().getId());
                    unitOfMeasure.setUnitOfMeasureName(testUnit);
                    unitOfMeasure.setDescription(testUnit);
                    unitOfMeasureDAO.updateDataWithoutDuplicateCheck(unitOfMeasure);
                } else {
                    LogEvent.logWarn("TestRenameUpdate", "updateTestNames", "Unit of measure of test[" + test.getId() + "] is null.");
                }
                
                // update test resultLimits (minVal & maxVal)
                for (IdValuePair kvpObj : resultLimitList) {
                    ResultLimit resultLimit = new ResultLimit();
                    resultLimit.setSysUserId(userId);
                    resultLimit.setId(kvpObj.getId());
                    resultLimit.setTestId(test.getId());
                    // Set Test Result Type => NUMERIC ("4")
                    resultLimit.setResultTypeId(IActionConstants.STR_TEST_RESULT_TYPE_ID);
                    // Split kVP value to (minVal & maxVal)
                    String[] normalRange = kvpObj.getValue().split("-");
                    String minimumValue = normalRange[0];
                    String maximumValue = normalRange[1];
                    resultLimit.setLowNormal(Double.parseDouble(minimumValue));
                    resultLimit.setHighNormal(Double.parseDouble(maximumValue));
                    resultLimitDAO.updateDataTestEdit(resultLimit);
                }
                tx.commit();
                
            } catch (HibernateException e) {
                tx.rollback();
                
            } finally {
                HibernateUtil.closeSession();
            }
        }
        //Refresh test names
        DisplayListService.getFreshList( DisplayListService.ListType.ALL_TESTS );
        DisplayListService.getFreshList( DisplayListService.ListType.ORDERABLE_TESTS );
    }

    protected String getPageTitleKey(){
        return "";
    }

    protected String getPageSubtitleKey(){
        return "";
    }
    
    private String decodeString(String value) {
    	String item = value;
    	byte[] bytes = item.getBytes(StandardCharsets.ISO_8859_1);
    	item = new String(bytes, StandardCharsets.UTF_8);
        
    	return item;
    }

}
