<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.formfields.FormFields.Field,
			us.mn.state.health.lims.common.formfields.FormFields,
			us.mn.state.health.lims.common.action.IActionConstants,
			us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
			us.mn.state.health.lims.common.util.StringUtil,
		    us.mn.state.health.lims.common.util.Versioning"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/struts-tiles"     prefix="tiles" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"		value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="genericDomain" value='' />

<%!
	java.util.Locale locale = null;
	IAccessionNumberValidator accessionValidator;
	String formName;
 %>
<%
	String basePath = "";
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
	accessionValidator = new AccessionNumberValidatorFactory().getValidator();
	formName = (String)request.getAttribute(IActionConstants.FORM_NAME);
	locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
%>

<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.menu.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.autocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/jquery.selectlist.dev.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/customAutocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/laborder.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript">

	var requiredFields = new Array("submitterNumber");

	// $jq is now an alias to the jQuery function; creating the new alias is optional.
	var $jq = jQuery.noConflict();
	 
    $jq(document).ready( function() {
    	$jq(".current input").each( function(index,elem){ $jq(elem).attr('readonly', true) });
    	$jq(".current .spacerRow").each( function(index, elem){ $jq(elem).hide() });
    });

	function submit(){
		var form = window.document.forms[0];
		form.action = "AuditTrailReport.do";
		form.submit();
		
		return false;
	}
	
	function setSampleFieldValidity(valid, fieldId) {
		// To catch setSampleFieldValidity call from common->sampleOrder.jsp
	}

</script>

<h1 class="page-title"><bean:message key="reports.auditTrail" /></h1>

<form class="form-horizontal">

	<div class="row-fluid">
	    <div class="span12">
	
		<%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%>: 
		<html:text name='<%= formName %>'
				   styleClass="input-medium" 
	    	       property="accessionNumberSearch"
	        	   maxlength="<%= Integer.toString(accessionValidator.getMaxAccessionLength()) %>" />
		<input class="btn" type="button" onclick="submit();" value='<%=StringUtil.getMessageForKey("label.button.view") %>'>
		</div>
	</div>
</form>
	
	<hr>
		
	<logic:notEmpty name='<%= formName %>' property="accessionNumber" >
	
		<logic:empty name='<%= formName %>' property="log" >
		<div class="row-fluid">
		    <div class="span6">
		    	<em><bean:message key="sample.edit.sample.notFound" /></em>
		    </div>
		</div>
		</logic:empty>
		
		<logic:notEmpty name='<%= formName %>' property="log" >
		
		<div class="row-fluid order-details">
		    <div class="span12">
		        <span class="order-number"><bean:write name='<%= formName %>' property="accessionNumber"  /></span> 
		        <bean:message key="reports.auditTrail.creation" />: <span id="dateCreated"></span>
		        <bean:message key="reports.auditTrail.days" />: <span id="daysInSystem"></span>
		    </div>
		</div>
	    <div class="current" >
            <h2><bean:message key="order.information" /></h2>
			<tiles:insert attribute="sampleOrder" />
    		<tiles:useAttribute name="displayOrderItemsInPatientManagement" scope="request" />
            <tiles:insert attribute="patientInfo" />
        </div>
		<div class="row-fluid">
			<div class="span12">		
				<div id="loading" class="loading-note"><img src="<%=basePath%>/images/indicator.gif" /><bean:message key="loading" /></div>
				<table class="table table-small table-hover table-bordered table-striped" id="advancedTable">
					<thead>
				    	<tr id="rowHeader">
						    <th>ID</th>
							<th><span><bean:message key="reports.auditTrail.time"/></span></th>
						    <th><span><bean:message key="reports.auditTrail.item"/></span></th>
							<th><span><bean:message key="reports.auditTrail.action"/></span></th>
						    <th><span><bean:message key="reports.auditTrail.identifier"/></span></th>
							<th><span><bean:message key="reports.auditTrail.user"/></span></th>
							<th><span><bean:message key="reports.auditTrail.old.value"/></span></th>
							<th><span><bean:message key="reports.auditTrail.new.value"/></span></th>
						</tr>
					</thead>
					<tbody>
					<logic:iterate id="log" indexId="rowIndex" name='<%= formName %>' property="log" type="us.mn.state.health.lims.audittrail.action.workers.AuditTrailItem">
						<tr class="<%=log.getItem()%>">
							<td><%=rowIndex%></td>
							<td class="time-stamp"><%= log.getDate() + " " +  log.getTime()%></td>
							<td class="item-cell"><%=log.getItem()%></td>
							<td><%=log.getAttribute()%></td>
							<td class="id-number"><%= log.getIdentifier() %></td>
							<td><%= log.getUser() %></td>
							<td><%=log.getOldValue()%></td>
							<td><%=log.getNewValue()%></td>
						</tr>
					</logic:iterate>
					</tbody>
				</table>
				
				<div id="showOptions" class="show-table-options">
					<button class="reset-sort btn btn-mini" disabled="disabled"><i class="icon-refresh"></i> <%=StringUtil.getMessageForKey("label.button.reset") %></button>
					<label> <%=StringUtil.getMessageForKey("label.pagination.show") %> :
				        <select id="filterByType">
				        <!--  Options for filter are added via filterByType jquery function -->
				            <option value=""><%=StringUtil.getMessageForKey("label.button.checkAll") %></option>
				        </select>
				    </label>
				</div>
						
			</div>
		</div>
	
		</logic:notEmpty>
				
	</logic:notEmpty>
	
<logic:notEmpty name='<%= formName %>' property="log" >
	<script type="text/javascript" src="<%=basePath%>scripts/oe.datatables.functions.js?ver=<%= Versioning.getBuildNumber() %>" params="locale='<%=locale%>'"></script>
</logic:notEmpty>
