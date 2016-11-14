<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
	us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
    us.mn.state.health.lims.common.util.DateUtil,
	us.mn.state.health.lims.common.util.StringUtil"%>
<%@ page import="us.mn.state.health.lims.common.util.Versioning"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%!IAccessionNumberValidator accessionValidator;
	String basePath = "";%>

<%
	accessionValidator = new AccessionNumberValidatorFactory()
			.getValidator();
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName()
			+ ":" + request.getServerPort() + path + "/";
%>

<!-- Creates updated UI. Removing for current release 
<link rel="stylesheet" media="screen" type="text/css" href="<%=basePath%>css/bootstrap.min.css?ver=<%=Versioning.getBuildNumber()%>" />
<link rel="stylesheet" media="screen" type="text/css" href="<%=basePath%>css/openElisCore.css?ver=<%=Versioning.getBuildNumber()%>" />
-->

<script type="text/javascript"
	src="scripts/utilities.js?ver=<%=Versioning.getBuildNumber()%>"></script>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="reportType" name='<%=formName%>' property="reportType" />
<bean:define id="reportRequest" name='<%=formName%>'
	property="reportRequest" />
<bean:define id="instructions" name='<%=formName%>'
	property="instructions" />


<script type="text/javascript">
 function ChangeValueRadio(){
	 $jq('#commonRadio').val($jq('input[name=commoncheckbox]:checked').val()+'');
} 
function datePeriodUpdated(selectionElement){
	var selectedValue = selectionElement.options[selectionElement.selectedIndex].value;

    if( selectedValue == "custom"){
    	$("lowerMonth").disabled = false;
    	$("lowerYear").disabled = false;
    	$("upperMonth").disabled = false;
    	$("upperYear").disabled = false;
    }else{
    	clearDate( "lowerMonth");
    	clearDate( "lowerYear");
    	clearDate( "upperMonth");
    	clearDate( "upperYear");
    	
    	$("dateWarning").style.visibility = "hidden";
    }
}

function clearAllDates(){
		setDateWarning( false, "" );
}

function clearDate( id ){
	var element = $(id);
	element.value="";
	element.disabled = true;
	element.style.borderColor = "";	
}
function /*boolean*/ formCorrect(){
	var datePeriod = $("datePeriod");
	
	if( datePeriod ){
		var selectedValue = datePeriod.options[datePeriod.selectedIndex].value;
		if( selectedValue == "custom"){
			var hasMissingValue = false;
						
			if( missingValue("lowerYear")){ hasMissingValue = true;}
			if( missingValue("lowerMonth")){ hasMissingValue = true;}
			if( missingValue("upperYear")){ hasMissingValue = true;}
			if( missingValue("upperMonth")){ hasMissingValue = true;}
		
			if( hasMissingValue){ 
				$("dateWarning").innerHTML = "Dates are required for custom range";
				return false;
			}
			
			var lowerYearValue = $("lowerYear").value;
			var lowerMonthValue = $("lowerMonth").value;
			var upperYearValue = $("upperYear").value;
			var upperMonthValue = $("upperMonth").value;
			
			var low = getCompoundDate( lowerYearValue, lowerMonthValue);
			var upper = getCompoundDate(upperYearValue, upperMonthValue);
			
			if( low >= upper){
				setDateWarning( true, "Start date must be before end date" );
				return false;	
			}
			
			var yearDiff = parseInt(upperYearValue - lowerYearValue );
			
			if( yearDiff > 1 ){
			 	setDateWarning( true, "Maximum date span is 12 months" );
				return false;
			}else if( yearDiff == 1 ){
				var monthDiff = parseInt(upperMonthValue - lowerMonthValue );
				
				if( monthDiff >= 0){
					setDateWarning( true, "Maximum date span is 12 months"  );
					return false;
				}
			}
		}
	}

	return true;
}

function setDateWarning( on, warning ){
				$("lowerYear").style.borderColor = on ? "red" : "";	
				$("lowerMonth").style.borderColor = on ? "red" : "";	
				$("upperYear").style.borderColor = on ? "red" : "";	
				$("upperMonth").style.borderColor = on ? "red" : "";
				$("dateWarning").innerHTML = warning;
}

function /*int*/ getCompoundDate( year, month){
	month = month.length == 1 ? "0" + month : month;
	return parseInt( year + month); 

}

function /*boolean*/ missingValue( id ){
	if( $(id).value == ""){
		$(id).style.borderColor = "red";
		return true;
	}

	return false;
}

function onCancel(){
	var form = window.document.forms[0];
	form.action = "CancelReport.do";
	form.submit();
	return true;
}

function onPrint(){
	var typeFile= $jq('#commonRadio').val();
	if( formCorrect()){
		var form = window.document.forms[0];
		form.action = "ReportPrint.do?type=" + '<%=reportType%>' + "&report=" + '<%=reportRequest%>' + "&cacheBreaker="
					+ Math.random() + '&typeFile=' + typeFile;
			form.target = "_blank";
			form.submit();
			return false;
		}

		return true;
	}
</script>

<h2>
	<bean:write name="<%=formName%>" property="reportName" />
</h2>

<div class="oe-report">
	<html:hidden name='<%=formName%>' property="reportRequest" />

	<table id='tmpl1' style="width: 50%">
	<logic:equal name='<%=formName%>' property='noRequestSpecifications' value="false">

		<logic:equal name='<%=formName%>' property="useAccessionDirect" value="true">
			<tr>
				<td colspan='2'>
					<strong><%=StringUtil.getContextualMessageForKey("report.enter.labNumber.headline")%></strong>
				</td>
			</tr>
		</logic:equal>

		<tr>
			<logic:equal name='<%=formName%>' property="useAccessionDirect" value="true">
				<td>
					<span style="padding-left: 10px">
						<logic:equal name='<%=formName%>' property="useHighAccessionDirect" value="true">
								<%=StringUtil.getContextualMessageForKey("report.from")%>
						</logic:equal>
					</span>
				</td>
				<td>
					<html:text name='<%=formName%>' styleClass="input-medium"
						property="accessionDirect"
						maxlength='<%=Integer.toString(accessionValidator.getMaxAccessionLength())%>' />
				</td>
			</logic:equal>
				<logic:equal name='<%=formName%>' property="useHighAccessionDirect"
					value="true">
					<td>
						<span style="padding-left: 10px">
							<%=StringUtil.getContextualMessageForKey("report.to")%>
						</span>
					</td>
					<td>
						<html:text name='<%=formName%>' property="highAccessionDirect"
							styleClass="input-medium"
							maxlength='<%=Integer.toString(accessionValidator
								.getMaxAccessionLength())%>' />
					</td>
				</logic:equal>
		</tr>
		<tr>
			<td colspan='2'>
				<logic:equal name='<%=formName%>' property="usePatientNumberDirect" value="true">
					<div>
						<strong><%=StringUtil.getContextualMessageForKey("report.enter.subjectNumber")%></strong>
					</div>
				</logic:equal>
			</td>
		</tr>
		<tr>
			<logic:equal name='<%=formName%>' property="usePatientNumberDirect" value="true">
				<td style="width: 30%">
					<html:text styleClass="input-medium" name='<%=formName%>' property="patientNumberDirect" />
				</td>
			</logic:equal>

			<logic:equal name='<%=formName%>' property="useUpperPatientNumberDirect" value="true">
				<td style="width: 30%">
					<span style="padding-left: 10px"><%=StringUtil.getContextualMessageForKey("report.to")%></span>
					<html:text styleClass="input-medium" name='<%=formName%>' property="patientUpperNumberDirect" />
				</td>
			</logic:equal>
		</tr>
		<!-- Illness date -->
		<logic:equal name='<%=formName%>' property="useLowerIllnessDateRange" value="true">
		<tr>
			<logic:equal name='<%=formName%>' property="useLowerIllnessDateRange" value="true">
			<td>
				<span style="padding-left: 10px">
				<bean:message key="report.date.start.illness" />
				</span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="lowerIllnessDateRange"
					onkeyup="addDateSlashes(this, event);" maxlength="10" />
			</td>
			</logic:equal>
			<logic:equal name='<%=formName%>' property="useUpperIllnessDateRange" value="true">
			<td>
				<span style="padding-left: 10px">
					<bean:message key="report.date.end.illness" />
				</span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="upperIllnessDateRange" maxlength="10"
					onkeyup="addDateSlashes(this, event);" />
			</td>
			</logic:equal>
		</tr>
		</logic:equal>
		<logic:equal name='<%=formName%>' property="useLowerDateRange" value="true">
		<tr>
			<logic:equal name='<%=formName%>' property="useLowerDateRange" value="true">
				<td>
					<span style="padding-left: 10px"><bean:message
							key="report.date.start" /></span>
				</td>
				<td>
					<html:text styleClass="input-medium" name='<%=formName%>'
						property="lowerDateRange" onkeyup="addDateSlashes(this, event);"
						maxlength="10" />
				</td>
			</logic:equal>
				<logic:equal name='<%=formName%>' property="useUpperDateRange"
					value="true">
				<td>
					<span style="padding-left: 10px"><bean:message
							key="report.date.end" /></span>
				</td>
				<td>
					<html:text styleClass="input-medium" name='<%=formName%>'
						property="upperDateRange" maxlength="10"
						onkeyup="addDateSlashes(this, event);" />
				</td>
			</logic:equal>
		</tr>
		</logic:equal>
		<!-- Add new some date -->
		<!-- complete date -->
		<logic:equal name='<%=formName%>' property="useLowerCompleteDateRange" value="true">
		<tr>
			<logic:equal name='<%=formName%>' property="useLowerCompleteDateRange" value="true">
			<td>
				<span style="padding-left: 10px">
					<bean:message key="report.date.start.complete" />
				</span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="lowerCompleteDateRange"
					onkeyup="addDateSlashes(this, event);" maxlength="10" />
			</td>
			</logic:equal>
			<logic:equal name='<%=formName%>' property="useUpperCompleteDateRange" value="true">
			<td>
				<span style="padding-left: 10px">
					<bean:message key="report.date.end.complete" />
				</span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="upperCompleteDateRange" maxlength="10"
					onkeyup="addDateSlashes(this, event);" />
			</td>
			</logic:equal>
		</tr>
		</logic:equal>
		<!-- complete date -->
		<logic:equal name='<%=formName%>' property="useLowerResultDateRange" value="true">
		<tr>
			<logic:equal name='<%=formName%>' property="useLowerResultDateRange" value="true">
			<td>
				<span style="padding-left: 10px"><bean:message
						key="report.date.start.result" /></span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="lowerResultDateRange"
					onkeyup="addDateSlashes(this, event);" maxlength="10" />
			</td>
			</logic:equal>
			<logic:equal name='<%=formName%>' property="useUpperResultDateRange" value="true">
			<td>
				<span style="padding-left: 10px">
					<bean:message key="report.date.end.result" />
				</span>
			</td>
			<td>
				<html:text styleClass="input-medium" name='<%=formName%>'
					property="upperResultDateRange" maxlength="10"
					onkeyup="addDateSlashes(this, event);" />
			</td>
			</logic:equal>
			</td>
		</tr>
		</logic:equal>

		<logic:equal name='<%=formName%>' property="useLocationCode" value="true">
			<tr>
				<td>
					<span style="padding-left: 10px">
						<bean:message key="report.select.service.location" />
					</span>
				</td>
				<td>
					<html:select name="<%=formName%>" property="locationCode" styleClass="text">
						<app:optionsCollection name="<%=formName%>" 
							property="locationCodeList" 
							label="organizationName"
							value="id" />
					</html:select>
				</td>
			</tr>
		</logic:equal>

		<logic:equal name='<%=formName%>' property="useProjectCode" value="true">
			<tr>
				<td>
					<bean:message key="report.select.project" />
				</td>
				<td>
					<html:select name="<%=formName%>" property="projectCode" styleClass="text">
						<app:optionsCollection name="<%=formName%>"
							property="projectCodeList"
							label="localizedName"
							value="id" />
					</html:select>
				</td>
			</tr>
		</logic:equal>
		<logic:notEmpty name='<%=formName%>' property="listProjectDengue">
			<tr>
				<td style="min-width: 0px;">
					<bean:define id="listProjectDengue" name='<%=formName%>'
						property="listProjectDengue"
						type="us.mn.state.health.lims.reports.action.implementation.ParameterProjectDengue" />
				</td>
				<td>
					<span style="padding-left: 10px">
						<label for="listProjectDengue"> <%=listProjectDengue.getLabel()%></label>
						<html:select name="<%=formName%>"
							property="listProjectDengue.selection" styleClass="text"
							styleId="listProjectDengue" style="width: 388PX!important;">
							<app:optionsCollection name="<%=formName%>"
								property="listProjectDengue.list"
								label="value"
								value="id" />
						</html:select>
					</span>
				</td>
			</tr>

		</logic:notEmpty>
		<tr>
		<table>
			<logic:notEmpty name='<%=formName%>' property="monthList">
			<td>
				<bean:message key="report.month" />
				<html:select name="<%=formName%>" property="lowerMonth"
					styleClass="text" styleId="lowerMonth">
					<app:optionsCollection name="<%=formName%>" property="monthList"
						label="value" value="id" />
				</html:select>
			</td>
			</logic:notEmpty>
			<logic:notEmpty name='<%=formName%>' property="yearList">
			<td>
				<bean:message key="report.year" />
				<html:select name="<%=formName%>" property="lowerYear"
					styleClass="text" styleId="lowerYear">
					<app:optionsCollection name="<%=formName%>" property="yearList"
						label="value" value="id" />
				</html:select>
			</td>
			</logic:notEmpty>
		</table>
		</tr>
		<logic:equal name='<%=formName%>' property="usePredefinedDateRanges" value="true">
		<tr>
			<td>
				<bean:message key="report.select.date.period" />
			</td>
			<td>
				<html:select name="<%=formName%>" property="datePeriod"
					styleClass="text" onchange="datePeriodUpdated(this)"
					styleId="datePeriod">
					<option value='year'><%=StringUtil
								.getMessageForKey("report.select.date.period.year")%></option>
					<option value='months3'><%=StringUtil
								.getMessageForKey("report.select.date.period.months.3")%></option>
					<option value='months6'><%=StringUtil
								.getMessageForKey("report.select.date.period.months.6")%></option>
					<option value='months12'><%=StringUtil
								.getMessageForKey("report.select.date.period.months.12")%></option>
					<option value='custom'><%=StringUtil
								.getMessageForKey("report.selecte.date.period.custom")%></option>
				</html:select>
			</td>
			<td>
				<bean:message key="report.date.start" />
			</td>
			<td>
				<html:select name="<%=formName%>" property="lowerMonth"
					styleClass="text" disabled="true" styleId="lowerMonth"
					onchange="clearAllDates();" multiple="true">
					<app:optionsCollection name="<%=formName%>" property="monthList"
						label="value" value="id" />
				</html:select>
				<html:select name="<%=formName%>" property="lowerYear"
					styleClass="text" disabled="true" styleId="lowerYear"
					onchange="clearAllDates();" multiple="multiple">
					<app:optionsCollection name="<%=formName%>" property="yearList"
						label="value" value="id" />
				</html:select>
				<bean:message key="report.date.end" />
				<html:select name="<%=formName%>" property="upperMonth"
					styleClass="text" disabled="true" styleId="upperMonth"
					onchange="clearAllDates();">
					<app:optionsCollection name="<%=formName%>" property="monthList"
						label="value" value="id" />
				</html:select>
				<html:select name="<%=formName%>" property="upperYear"
					styleClass="text" disabled="true" styleId="upperYear"
					onchange="clearAllDates();">
					<app:optionsCollection name="<%=formName%>" property="yearList"
						label="value" value="id" />
				</html:select>
				<div id="dateWarning"></div>
			</td>
			<!-- </div> -->
		</tr>
		</logic:equal>
		<logic:notEmpty name='<%=formName%>' property="selectList">
			<tr>
			<table>
				<td>
					<bean:define id="selectList" name='<%=formName%>'
						property="selectList"
						type="us.mn.state.health.lims.reports.action.implementation.ReportSpecificationList" />
				</td>
				<td>
					<span style="padding-left: 10px">
						<label for="selectList"><%=selectList.getLabel()%></label>
						<html:select name="<%=formName%>"
							property="selectList.selection" styleClass="text"
							styleId="selectList" style="width: 388PX!important;">
							<app:optionsCollection name="<%=formName%>"
								property="selectList.list" label="value" value="id" />
						</html:select>
					</span>
				</td>
			</table>
			</tr>
		</logic:notEmpty>

		<logic:notEmpty name='<%=formName%>' property="testIsolationList">
			<tr>
			<table>
				<td>
					<bean:define id="testIsolationList" name='<%=formName%>'
						property="testIsolationList"
						type="us.mn.state.health.lims.reports.action.implementation.ParameterTestIsolationList" />
				</td>
				<td>
					<span style="padding-left: 10px">
						<label for="testIsolationList"><%=testIsolationList.getLabel()%>
						</label>
						<html:select name="<%=formName%>"
							property="testIsolationList.selection" styleClass="text"
							styleId="testIsolationList" style="width: 388PX!important;">
							<app:optionsCollection name="<%=formName%>"
								property="testIsolationList.list" label="value" value="id" />
						</html:select>
					</span>
				</td>
			</table>
			</tr>
		</logic:notEmpty>

		<logic:notEmpty name='<%=formName%>' property="testPCRList">
			<tr>
			<table>
				<td>
					<bean:define id="testPCRList" name='<%=formName%>'
						property="testPCRList"
						type="us.mn.state.health.lims.reports.action.implementation.ParameterTestPCRList" />
				</td>
				<td>
					<span style="padding-left: 10px">
						<label for="testPCRList"><%=testPCRList.getLabel()%></label>
						<html:select name="<%=formName%>"
							property="testPCRList.selection" styleClass="text"
							styleId="testPCRList" style="width: 388PX!important;">
							<app:optionsCollection name="<%=formName%>"
								property="testPCRList.list" label="value" value="id" />
						</html:select>
					</span>
				</td>
			</table>
			</tr>
		</logic:notEmpty>

		<logic:notEmpty name='<%=formName%>' property="selectListName">
			<tr>
			<table>
			<tr>
				<td>
					<bean:define id="selectListName" name='<%=formName%>'
						property="selectListName"
						type="us.mn.state.health.lims.reports.action.implementation.ReportSpecificationListName" />
				</td>
				<td>
					<span style="padding-left: 10px">
					<td>
						<label for="selectListName"><%=selectListName.getLabel()%></label>
					</td>
					<td>
						<html:select name="<%=formName%>"
							property="selectListName.selection" styleClass="text"
							styleId="selectList">
							<app:optionsCollection name="<%=formName%>"
								property="selectListName.list" label="value" value="id" />
						</html:select>
					</td>
					</span>
				</td>
			</tr>
			</table>
			</tr>
		</logic:notEmpty>


	</logic:equal>
	</table>
	<table id='tmpl2' style="width: 100%">
	<logic:notEmpty name='<%=formName%>' property="emergencySelectList">
		<tr>
			<td>
				<bean:define id="emergencySelectList" name='<%=formName%>'
					property="emergencySelectList"
					type="us.mn.state.health.lims.reports.action.implementation.ReportSpecificationList" />
			</td>
			<td>
				<span style="padding-left: 10px"><label for="emergencySelectList">
						<%=emergencySelectList.getLabel()%></label>
					<html:select name="<%=formName%>"
						property="emergencySelectList.selection" styleClass="text"
						styleId="selectList">
						<app:optionsCollection name="<%=formName%>"
							property="emergencySelectList.list"
							label="value" value="id" />
					</html:select>
				</span>
			</td>
		</tr>
	</logic:notEmpty>
	</table>
	<logic:notEmpty name='<%=formName%>' property="doctorSelectList">
		<div style="padding-left: 10px; margin: -23px 0px 0px 0px;">
			<bean:define id="doctorSelectList" name='<%=formName%>'
				property="doctorSelectList"
				type="us.mn.state.health.lims.reports.action.implementation.ReportSpecificationList" />
			<span style="padding-left: 10px"><label for="doctorSelectList">
					<%=doctorSelectList.getLabel()%></label> <html:select name="<%=formName%>"
					property="doctorSelectList.selection" styleClass="text"
					styleId="selectList">
					<app:optionsCollection name="<%=formName%>"
						property="doctorSelectList.list" label="value" value="id" />
				</html:select></span>
		</div>
	</logic:notEmpty>
	<logic:equal name='<%=formName%>' property="usePassword" value="true">
		<span style="padding-left: 10px">
				<%=StringUtil
					.getContextualMessageForKey("reports.parameter.password")%>:
		</span>
		<html:text name='<%=formName%>' styleClass="input-medium" property="password"/>
	</logic:equal>
	<!-- Dung add -->
	<div>
		<input type="hidden" id="commonRadio" value="pdf">
		<logic:equal name='<%=formName%>' property="usepdf" value="true">
			<div style="width: 170px; float: left;">
				<span style="padding-left: 10px">Xuất định dạnh pdf</span> <input
					type="radio" value="pdf" name="commoncheckbox" checked="checked"
					onchange="ChangeValueRadio();" />
				<%-- <html:text styleClass="input-medium"  property="pdf" onkeyup="" styleId="pdf" value="pdf"/> --%>
			</div>
		</logic:equal>
		<logic:equal name='<%=formName%>' property="useexcel" value="true">

			<div>
				<span style="padding-left: 10px">Xuất định dạnh excel</span> <input
					type="radio" value="excel" name="commoncheckbox"
					onchange="ChangeValueRadio();" />
			</div>
		</logic:equal>
	</div>
	<!-- end Dung add -->
	<logic:equal name='<%=formName%>' property="useHighAccessionDirect" value="true">
		<div>
			<span style="padding-left: 10px">
				<%=StringUtil.getContextualMessageForKey("report.enter.labNumber.detail")%>
			</span>
		</div>
	</logic:equal>
	<br />
	<div align="left" style="width: 80%">
		<logic:notEmpty name='<%=formName%>' property="instructions">
			<%=instructions%>
		</logic:notEmpty>
	</div>
	<div style="margin-left: 10px">
		<input type="button" class="btn" name="printNew" onclick="onPrint();"
			value="<%=StringUtil
					.getMessageForKey("label.button.print.new.window")%>">
	</div>

</div>