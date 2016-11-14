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
package us.mn.state.health.lims.reports.action.implementation;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.common.util.DateUtil;
import us.mn.state.health.lims.common.util.StringUtil;
import us.mn.state.health.lims.reports.action.implementation.reportBeans.ErrorMessages;

import java.sql.Date;
import java.util.HashMap;

public abstract class IndicatorReport extends Report {

	protected String lowerDateRange;
	protected String upperDateRange;
	protected Date lowDate;
	protected Date highDate;
	
	public void setRequestParameters(BaseActionForm dynaForm) {
        new ReportSpecificationParameters( ReportSpecificationParameters.Parameter.DATE_RANGE, getNameForReportRequest(), null ).setRequestParameters( dynaForm );
	}

	protected void createReportParameters(){
		super.createReportParameters();

		reportParameters.put("startDate", lowerDateRange);
		reportParameters.put("stopDate", upperDateRange);
		reportParameters.put("siteId", ConfigurationProperties.getInstance().getPropertyValue(Property.SiteCode));
		reportParameters.put("directorName", ConfigurationProperties.getInstance().getPropertyValue(Property.labDirectorName));
		reportParameters.put("labName1", getLabNameLine1());
		reportParameters.put("labName2", getLabNameLine2());
		reportParameters.put("reportTitle", getNameForReport());
		if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "CI LNSP")){
            reportParameters.put("headerName", "CILNSPHeader.jasper");
        }else{
            reportParameters.put("headerName", "GeneralHeader.jasper");
        }
	}
	
	protected void setDateRange(BaseActionForm dynaForm) {
		errorFound = false;
		lowerDateRange = dynaForm.getString("lowerDateRange");
		upperDateRange = dynaForm.getString("upperDateRange");

		if (GenericValidator.isBlankOrNull(lowerDateRange)) {
			errorFound = true;
			ErrorMessages msgs = new ErrorMessages();
			msgs.setMsgLine1(StringUtil.getMessageForKey("report.error.message.noPrintableItems"));
			errorMsgs.add(msgs);
		}

		if (GenericValidator.isBlankOrNull(upperDateRange)) {
			upperDateRange = lowerDateRange;
		}

		try {
			lowDate = DateUtil.convertStringDateToSqlDate(lowerDateRange);
			highDate = DateUtil.convertStringDateToSqlDate(upperDateRange);
		} catch (LIMSRuntimeException re) {
			errorFound = true;
			ErrorMessages msgs = new ErrorMessages();
			msgs.setMsgLine1(StringUtil.getMessageForKey("report.error.message.date.format"));
			errorMsgs.add(msgs);
		}
	}
	
	protected void setDateRange(HashMap<String, String> hashmap) {
		errorFound = false;
		lowerDateRange = hashmap.get("lowerDateRange");
		upperDateRange = hashmap.get("upperDateRange");

		if (GenericValidator.isBlankOrNull(lowerDateRange)) {
			errorFound = true;
			ErrorMessages msgs = new ErrorMessages();
			msgs.setMsgLine1(StringUtil.getMessageForKey("report.error.message.noPrintableItems"));
			errorMsgs.add(msgs);
		}

		if (GenericValidator.isBlankOrNull(upperDateRange))
			upperDateRange = lowerDateRange;

		try {
			lowDate = DateUtil.convertStringDateToSqlDate(lowerDateRange);
			highDate = DateUtil.convertStringDateToSqlDate(upperDateRange);
		} catch (LIMSRuntimeException re) {
			errorFound = true;
			ErrorMessages msgs = new ErrorMessages();
			msgs.setMsgLine1(StringUtil.getMessageForKey("report.error.message.date.format"));
			errorMsgs.add(msgs);
		}
	}

	abstract protected String getNameForReportRequest();
	abstract protected String getNameForReport();
	abstract protected String getLabNameLine1();
	abstract protected String getLabNameLine2();

}
