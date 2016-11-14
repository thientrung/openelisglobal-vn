<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
				 us.mn.state.health.lims.common.util.SystemConfiguration,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
			     us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
			     us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			     us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.util.Versioning,
			     us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<%--
  ~ The contents of this file are subject to the Mozilla Public License
  ~ Version 1.1 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://www.mozilla.org/MPL/
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing rights and limitations under
  ~ the License.
  ~
  ~ The Original Code is OpenELIS code.
  ~
  ~ Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
  --%>

<bean:define id="formName"		value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="accessionFormat" value='<%=ConfigurationProperties.getInstance().getPropertyValue(Property.AccessionFormat)%>' />

<%!
	String basePath = "";
	IAccessionNumberValidator accessionNumberValidator;
 %>
<%
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
			+ request.getServerPort() + path + "/";

	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript" language="JavaScript1.2">

var searchedFlag = false;

$jq(document).ready( function() {
	$("searchAccessionID").value = <%=request.getParameter("accessionNumber")%> != null ? '<%=request.getParameter("accessionNumber")%>' : '';
});

function validateEntrySize( elementValue ){
	$("retrieveTestsID").disabled = (elementValue.length == 0);
}


function doShowTests(){

	var form = document.forms[0];

	form.action = '<%=formName%>'.sub('Form','') + ".do?accessionNumber="  + $("searchAccessionID").value;

	form.submit();
}

function /*void*/ handleEnterEvent(  ){
	if( $("searchAccessionID").value.length > 0 && !searchedFlag){
		searchedFlag = true;
		doShowTests();
	}
	return false;
}



</script>


<div id="PatientPage" class="colorFill" style="display:inline" >

	<h2><bean:message key="sample.entry.search"/></h2>
	<table width="100%" style="background-color: inherit;">
	<tr >
		<td width="20%"></td>
		<td width="20%" align="right" >
			<%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>:&nbsp;
		</td>
		<td width="5%">
			<input name="searchAccession"
					style="margin: auto;"
			       size="20"
			       id="searchAccessionID"
			       maxlength="<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength()) %>"
			       onkeyup="validateEntrySize( this.value );"
			       onblur="validateEntrySize( this.value );"
			       class="text input-medium"
			       type="text">
		</td>
		<td width="1%"></td>
		<td width="14%">
			<html:button styleClass="btn btn-default" property="retrieveTestsButton" styleId="retrieveTestsID"  onclick="doShowTests();" disabled="true" >
			<%= StringUtil.getContextualMessageForKey("resultsentry.accession.search") %>
			</html:button>
		</td>
		<td width="40%"></td>
	</tr>
	</table>
	<br/>
		<logic:notEqual name="<%=formName %>" property="firstName" value="">
		<table width="100%;">
			<tr><th style="text-align: center; " colspan="9"><%= StringUtil.getContextualMessageForKey("sampletracking.patientInfo") %></th></tr>
			<tr>
				<td width= 5%></td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="patient.epiFullName"/>:&nbsp;
				</td>
				<td style="text-align: left; width= 20%;">
					<b><bean:write name="<%=formName%>" property="firstName" /></b>
				</td>
				<td style="text-align: left; width= 15%;">
					<bean:message key="patient.birthDate"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%; font-weight: bold;">
					<bean:write name="<%=formName%>" property="dob" />
				</td>
				<td style="text-align: left; width= 5%; ">
					<bean:message key="patient.age"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%; font-weight: bold;"><bean:write name="<%=formName%>" property="age" /></td>
				<td width= 15%;></td>
			</tr>
			<tr>
				<td></td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="person.streetAddress"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 20%;">
					<b><bean:write name="<%=formName%>" property="address" /></b>
				</td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="patient.gender"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<logic:equal name="<%=formName %>" property="gender" value="M">
						<b><%= StringUtil.getContextualMessageForKey("gender.male") %></b>
					</logic:equal>
					<logic:equal name="<%=formName %>" property="gender" value="F">
						<b><%= StringUtil.getContextualMessageForKey("gender.female") %></b>
					</logic:equal>
					<logic:notEqual name="<%=formName %>" property="gender" value="M">
						<logic:notEqual name="<%=formName %>" property="gender" value="F">
							<logic:notEmpty name="<%=formName%>" property="firstName">
								<b><%= StringUtil.getContextualMessageForKey("gender.unknown") %></b>
							</logic:notEmpty>
						</logic:notEqual>
					</logic:notEqual>
				</td>
				<td style="text-align: left; ">
					<bean:message key="patient.externalId"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<b><bean:write name="<%=formName%>" property="externalId" /></b>
				</td>
			</tr>
			<tr>
				<td></td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="person.department"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 20%; font-weight: bold;">
					<b><bean:write name="<%=formName%>" property="department" /></b>
				</td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="patient.patient.diagnosis"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<b><bean:write name="<%=formName%>" property="diagnosis" /></b>
				</td>
				<td style="text-align: left; width= 5%; ">
					<bean:message key="patient.chartNumber"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<b><bean:write name="<%=formName%>" property="chartNumber" /></b>
				</td>
			</tr>
			<tr>
				<td></td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="label.jasper.submitter"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 20%; font-weight: bold;">
					<b><bean:write name="<%=formName%>" property="organization" /></b>
				</td>
				<td style="text-align: left; width= 5%;">
					<bean:message key="project.browse.title"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<b><bean:write name="<%=formName%>" property="projectName" /></b>
				</td>
				<td style="text-align: left; width= 5%; ">
					<bean:message key="sample.receivedDate"/>&nbsp;:&nbsp;
				</td>
				<td style="text-align: left; width= 15%;">
					<b><bean:write name="<%=formName%>" property="receivedDate" /></b>
				</td>
			</tr>
		</table>
		</logic:notEqual>
	<br/>

</div>



