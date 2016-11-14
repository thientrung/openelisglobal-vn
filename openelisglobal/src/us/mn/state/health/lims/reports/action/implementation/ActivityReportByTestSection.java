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

package us.mn.state.health.lims.reports.action.implementation;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.ResultService;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.ActivityReportBean;
import us.mn.state.health.lims.result.valueholder.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 */
public class ActivityReportByTestSection extends ActivityReport implements IReportCreator, IReportParameterSetter {
    private String unitName;

    @Override
    public void setRequestParameters( BaseActionForm dynaForm ){
        new ReportSpecificationParameters( ReportSpecificationParameters.Parameter.DATE_RANGE,
                StringUtil.getMessageForKey( "report.activity.report.base" ) + " " + StringUtil.getMessageForKey( "report.by.unit" ),
                StringUtil.getMessageForKey( "report.instruction.all.fields" ) ).setRequestParameters( dynaForm );
        new ReportSpecificationList( DisplayListService.getList( DisplayListService.ListType.TEST_SECTION ),
                                     StringUtil.getMessageForKey( "workplan.unit.types" ) ).setRequestParameters( dynaForm );
    }

    @Override
    protected String getActivityLabel(){
        return StringUtil.getMessageForKey( "report.unit" ) + ": " + unitName;
    }

    @Override
    protected void buildReportContent( ReportSpecificationList unitSelection ){
        unitName = unitSelection.getSelectionAsName();
        createReportParameters();

        List<Result> resultList = ResultService.getResultsInTimePeriodInTestSection( dateRange.getLowDate(), dateRange.getHighDate(), unitSelection.getSelection() );
        ArrayList<ActivityReportBean> rawResults = new ArrayList<ActivityReportBean>( resultList.size() );
        testsResults = new ArrayList<ActivityReportBean>( );


        String currentAnalysisId = "-1";
        for( Result result : resultList){
            if( result.getAnalysis() != null){
                if( !currentAnalysisId.equals( result.getAnalysis().getId())){
                    rawResults.add( createActivityReportBean( result, true ) );
                    currentAnalysisId = result.getAnalysis().getId();
                }
            }
        }

        Collections.sort( rawResults, new Comparator<ActivityReportBean>(){
            @Override
            public int compare( ActivityReportBean o1, ActivityReportBean o2 ){
                return o1.getAccessionNumber().compareTo( o2.getAccessionNumber() );
            }
        } );

        String currentAccessionNumber = "";
        for( ActivityReportBean item : rawResults){
            if( !currentAccessionNumber.equals( item.getAccessionNumber() )){
                testsResults.add( createIdentityActivityBean( item, true ));
                currentAccessionNumber = item.getAccessionNumber();
            }
            testsResults.add( item );
        }

    }

	@Override
	public void initializeReport(HashMap<String, String> hashmap) {
		super.initializeReport();
	}

}
