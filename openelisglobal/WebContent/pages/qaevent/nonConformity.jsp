<%@page import="org.hamcrest.core.IsNot"%>
<%@page import="us.mn.state.health.lims.common.action.IActionConstants"%>
<%@page import="us.mn.state.health.lims.common.formfields.FormFields,
                us.mn.state.health.lims.common.formfields.FormFields.Field"%>

<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page import="us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,us.mn.state.health.lims.common.provider.validation.NonConformityRecordNumberValidationProvider,us.mn.state.health.lims.common.services.PhoneNumberService"%>
<%@ page import="us.mn.state.health.lims.common.util.DateUtil" %>
<%@ page import="us.mn.state.health.lims.common.util.StringUtil, us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="us.mn.state.health.lims.qaevent.valueholder.retroCI.QaEventItem" %>



<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />


<%! String basePath = "";
    IAccessionNumberValidator accessionNumberValidator;
    boolean useProject = FormFields.getInstance().useField(Field.Project);
    boolean useSiteList = FormFields.getInstance().useField(Field.NON_CONFORMITY_SITE_LIST);
    boolean useSubjectNo = FormFields.getInstance().useField(Field.QASubjectNumber);
    boolean useNationalID = FormFields.getInstance().useField(Field.NationalID);
    boolean useExternalID = FormFields.getInstance().useField(Field.EXTERNAL_ID);
    
	boolean isNonConformity = true;
%>
<%
    String path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
                    + "/";
    accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
    
%>

<link rel="stylesheet" href="css/jquery_ui/jquery.ui.all.css?ver=<%= Versioning.getBuildNumber() %>">
<link rel="stylesheet" href="css/customAutocomplete.css?ver=<%= Versioning.getBuildNumber() %>">

<script src="scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.menu.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ui/jquery.ui.autocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/customAutocomplete.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript" >

var requiredFields = new Array();
var dirty = false;

var confirmNewTypeMessage = "<bean:message key='nonConformant.confirm.newType.message'/>";

/* $jq(function() {
	var eventsTable = $('qaEventsTable');
	if (eventsTable === null) {
		return;
	}
	var fields = eventsTable.getElementsBySelector(".requiredField");
	fields.each(function(field) {
				if( !field.disabled){
					fieldValidator.addRequiredField(field.id);
				}  
			});
}); */

function siteListChanged(textValue){
	var siteList = $("site");
	//if the index is 0 it is a new entry, if it is not then the textValue may include the index value
	if( siteList.selectedIndex == 0 || siteList.options[siteList.selectedIndex].label != textValue){
		  $("newServiceName").value = textValue;
		  siteList.selectedIndex = 0;
	}
	
	$("serviceNew").value = !textValue.blank();
}

function /*void*/loadForm() {
	if (!$("searchId").value.empty()) {
		var form = document.forms[0];

		form.action = "NonConformity.do?labNo=" + $("searchId").value;

		form.submit();
	}
}

function setMyCancelAction() {
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function onChangeSearchNumber(searchField) {
	var searchButton = $("searchButtonId");
	if (searchField.value === "") {
		searchButton.disable();
	} else {
	    validateAccessionNumberOnServer( true, true, searchField.id, searchField.value, processAccessionSuccess);
	}
}

function processAccessionSuccess(xhr)
{
    //alert(xhr.responseText);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var success = message.firstChild.nodeValue == "valid";

    var searchButton = $("searchButtonId");

	if( !success ){
		alert( message.firstChild.nodeValue );
        searchButton.disable();
	}else {
        searchButton.enable();
        searchButton.focus();
    }
}


/**
 * make the text of the blank option for sample type say "all types"
 */
function tweekSampleTypeOptions() {
	var eventsTable = $('qaEventsTable');
	if (eventsTable === null) {
		return;
	}
	var fields = eventsTable.getElementsBySelector(".typeOfSample");
	fields.each(function(field) {
				field.options[1].text = "<bean:message key='nonConformant.allSampleTypesText'/>";
			});
}

// Initialize row fields enable/disable status
function /*void*/ initializeRowFields() {
	var eventsTable = $('qaEventsTable');
	if (eventsTable == null)
		return;
	var rows = eventsTable.getElementsBySelector("TR.qaEventRow");
	rows.detect(function(row) {
		// Initialize the enable/disable status of each row fields
		var qaEvent = row.getElementsBySelector("#qaEvent" + row.id.split("_")[1])[0];
		var sampleType = row.getElementsBySelector("#sampleType" + row.id.split("_")[1])[0];
		var section = row.getElementsBySelector("#section" + row.id.split("_")[1])[0];
		var author = row.getElementsBySelector("#author" + row.id.split("_")[1])[0];
		var note = row.getElementsBySelector("#note" + row.id.split("_")[1])[0];
		var rowcheckBox = row.getElementsBySelector(".qaEventEnable")[0];
		qaEvent.enable(); qaEvent.removeClassName("readOnly");
		sampleType.enable(); sampleType.removeClassName("readOnly");
		section.enable(); section.removeClassName("readOnly");
		author.enable(); author.removeClassName("readOnly");
		note.enable(); note.removeClassName("readOnly");
		
		// Check if row has data or value, then disable/enable
		if (checkRowHasData(row)) {
			rowcheckBox.enable();
		} else {
			rowcheckBox.disable();
		}
	});
}

function addRow() {
	var eventsTable = $('qaEventsTable');
	if (eventsTable == null)
		return;
	var rows = eventsTable.getElementsBySelector("TR.qaEventRow");
	// display all rows up to the first unused one
	var row = rows.detect(function(row) {
		row.show(); // show each row as we go everyone
		var idFieldValue = row.getElementsBySelector(".id")[0].getValue();
		if (idFieldValue == "NEW") {
			enableRow(row, idFieldValue);
		}
		return idFieldValue == "";
	});
	if (row == null) {
		row = rows[rows.length - 1];
	}
	// we have an empty row
	row.show();
	var idFields = row.getElementsBySelector(".id");
	$(idFields[0]).value = "NEW";
	enableRow(row, "NEW");
	
	addToRequiredFields(row.getElementsBySelector("#qaEvent" + row.id.split("_")[1])[0]);
	addToRequiredFields(row.getElementsBySelector("#sampleType" + row.id.split("_")[1])[0]);
	setSave();
}

function /*bool*/ checkRowHasData(row) {
	var qaEventValue = row.getElementsBySelector("#qaEvent" + row.id.split("_")[1])[0].getValue();
	var sampleTypeValue = row.getElementsBySelector("#sampleType" + row.id.split("_")[1])[0].getValue();
	var sectionValue = row.getElementsBySelector("#section" + row.id.split("_")[1])[0].getValue();
	var authorValue = row.getElementsBySelector("#author" + row.id.split("_")[1])[0].getValue();
	var noteValue = row.getElementsBySelector("#note" + row.id.split("_")[1])[0].getValue();
	
	if (!(qaEventValue == 0) || !(sampleTypeValue == 0) || !(sectionValue == 0) || 
			!authorValue.blank() || !noteValue.blank()) {
		return true;
	}
	return false;	
}

function enableRow(row, newIndicator) {
	var isNew = newIndicator == "NEW";
	var elements = row.getElementsBySelector(".qaEventElement");

	elements.each(function(field) {
		field = $(field);
		if( isNew || field.id.startsWith("note")){
			field.enable();
			field.removeClassName("readOnly");
		}
	});
/* 	elements = row.getElementsBySelector(".requiredField");
	elements.each(function(field) {
		fieldValidator.addRequiredField(field.id);
	});
 */
}

function areNewTypesOfSamples() {
	var eventsTable = $('qaEventsTable');
	var fields = eventsTable.getElementsBySelector(".typeOfSample");
	if ($("sampleItemsTypeOfSampleIds").value == "") {
		return false; // we don't worry about sample types when there aren't any at all
	}

    return fields.detect(function (field) {
        var ids = $("sampleItemsTypeOfSampleIds").value;
        var val = field.value;
        return (val !== null && val !== "0" && ids.indexOf("," + val + ",") == -1);
    }) != null;
}

function /*boolean*/ handleEnterEvent(){
	loadForm();
	return false;
}

function validateRecordNumber( recordElement){
	if( recordElement.value != ""){
		validateNonConformityRecordNumberOnServer( recordElement, recordNumberSuccess);
	}
	
	clearFieldErrorDisplay( recordElement);
}

function recordNumberSuccess( xhr){
    //alert(xhr.responseText);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var success = message.firstChild.nodeValue == "Record not Found";
	var fieldElement = $(formField);

	selectFieldErrorDisplay( success, fieldElement);
    fieldValidator.setFieldValidity(success, fieldElement.id);

	if( !success ){
		alert( message.firstChild.nodeValue );
	}
	
	setSave();
}

function checkValidTime( timeElement ){
	var valid = !timeElement.value || checkTime(timeElement.value);
	selectFieldErrorDisplay( valid, timeElement);
	fieldValidator.setFieldValidity(valid, timeElement.id);
	setSave();
}

function setSave(){
	var saveButton = $("saveButtonId");
	var validToSave = fieldValidator.isAllValid() && isRequireFieldsEmpty();

	//all this crap is to make sure there is not an enabled new row that has no value for the required field then none of the 
	//other fields have values		
	if( validToSave){
		$jq("#qaEventsTable tbody tr").each( function(rowIndex, rowElement){
			var jqRow = $jq(rowElement);
			if(validToSave && jqRow.is(":visible")){
				//if row is visible and the required field is blank make sure no other field has a value
				if( !(jqRow.find(".qaEventEnable").is(":checked") ) && requiredSelectionsNotDone( jqRow ) ){
					jqRow.find(".qaEventElement").each( function(index, element){
						var cellValue = $jq(element).val();
						if(!(cellValue.length == 0 || cellValue == "0")){
							validToSave = false;
						}
					});
				}
			}
		});
	}

	if(saveButton){
		saveButton.disabled = !validToSave;
	}
	return;
}

function /*void*/ updateRequiredFields(field) {
	// For removeCheckbox
	if (field.id.startsWith("remove")) {
		var row = field.parentNode.parentNode;
		var elements = row.getElementsBySelector(".qaEventElement");
		var qaEventField = elements[0];
		var sampleTypeField = elements[1];
		if (field.checked) {
			// Delete qaEvent and sampleType from requiredFields
			deleteFromRequiredFields(qaEventField);
			deleteFromRequiredFields(sampleTypeField);
		} else {
			// Check if row->qaEvent has a value
			if (!(qaEventField.value == 0)) {
				deleteFromRequiredFields(qaEventField);
			} else {
				addToRequiredFields(qaEventField);
			}
			// Check if row->sampleType has a value
			if (!(sampleTypeField.value == 0)) {
				deleteFromRequiredFields(sampleTypeField);
			} else {
				addToRequiredFields(sampleTypeField);
			}
		}
		setSave();
		return;
	} 
	// For refusalReason & sampleType
	if (field.id.startsWith("qaEvent") || field.id.startsWith("sampleType")) {
		var row = field.parentNode.parentNode;
		var removeCheckbox = row.getElementsBySelector(".qaEventEnable");
		if (!(removeCheckbox[0].checked)) {
			if (field.value == 0) {
				addToRequiredFields(field);
			} else {
				deleteFromRequiredFields(field);
			}
		}
	}
	setSave();
}

function /*void*/ deleteFromRequiredFields(field) {
    var removeIndex = $jq.inArray(field, requiredFields);
	if (!(removeIndex == -1)) {
		requiredFields.splice(field.id.split(field.id)[1], 1);
	}
}

function /*void*/ addToRequiredFields(field) {
	if ($jq.inArray(field, requiredFields) == -1) {
		requiredFields.push(field);
	}
}

function /*bool*/ isRequireFieldsEmpty() {
	if (requiredFields.length > 0) {
		return false;
	}
    for(var i = 0; i < requiredFields.length; ++i){
        // Check if there are empty required fields
        if (requiredFields[i].value.blank()) {
        	return false;
        }
    }
    return true;
}

function requiredSelectionsNotDone( jqRow ){
    var done = true;

    jqRow.find(".requiredField").each( function(index, element){
        if( element.value == 0 ){
            done = false;
            return;
        }
    });

    return !done;
}

function validatePhoneNumber( phoneElement){
    validatePhoneNumberOnServer( phoneElement, processPhoneSuccess);
}

function  processPhoneSuccess(xhr){
    //alert(xhr.responseText);

    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = false;

    if (message.firstChild.nodeValue == "valid"){
        success = true;
    }
    var labElement = formField.firstChild.nodeValue;
    selectFieldErrorDisplay( success, $(labElement));
    fieldValidator.setFieldValidity(success, labElement);

    if( !success ){
        alert( message.firstChild.nodeValue );
    }

    setSave();
}

</script>


<div align="center">
	<%= StringUtil.getContextualMessageForKey("quick.entry.accession.number") %>
	:
	<input type="text" name="searchNumber"
		maxlength='<%=Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>'
		value="" onchange="onChangeSearchNumber(this)" id="searchId" style="margin-top: 10px; float:inherit;"
		class="text input-medium">
	&nbsp;
	<input type="button" id="searchButtonId" class="btn btn-default"
		value='<%=StringUtil.getMessageForKey("label.button.search")%>'
		onclick="loadForm();" disabled="disabled" style="padding-top: 4px;padding-bottom: 4px;">
</div>
<hr />
<bean:define id="readOnly" name='<%=formName%>' property="readOnly"	type="java.lang.Boolean" />
<logic:notEmpty name='<%=formName%>' property="labNo">
	<html:hidden name='<%=formName%>' property="sampleId" />
	<html:hidden name='<%=formName%>' property="patientId" />
	<html:hidden name='<%=formName%>' styleId="sampleItemsTypeOfSampleIds" property="sampleItemsTypeOfSampleIds" />
	<table >
			<tr>
			<td width="width: 200px;">
				<%=StringUtil.getContextualMessageForKey("resultsentry.accessionNumber") %> :
			</td>
			<td width="352px;">
				<bean:write name="<%=formName%>" property="labNo" />
			</td>
						<td >
				<%= StringUtil.getContextualMessageForKey("nonconformity.date") %>&nbsp;
                <span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span>
				:
			</td>
			<td width="225px;">
				<html:text name='<%=formName %>' 
				           styleId="date1" 
				           property="date"
					       maxlength="10"
                       	   onkeyup="addDateSlashes(this, event);"
					       onchange="checkValidDate(this);" />
			<% if( FormFields.getInstance().useField(Field.QATimeWithDate)){ %>
				<%= StringUtil.getContextualMessageForKey("nonconformity.time") %>
				:
				<html:text name='<%=formName %>' 
				           styleId="time" 
				           property="time"
					       maxlength="5" 
					       onchange="checkValidTime(this);" />
			<% } %>
			</td>
		</tr>
		<tr>
		</tr>
		<tr>
			<html:hidden name='<%=formName%>' property="project" />
			<% if ( useProject ) { %>
			<td >
				<bean:message key="label.study" />
				:
			</td>
			<logic:equal name='<%=formName%>' property="project" value="">
				<td>
					<html:select name="<%=formName%>" property="projectId"
						onchange='makeDirty();'>
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>" property="projects"
							label="localizedName" value="id" />
					</html:select>
				</td>
			</logic:equal>
			<logic:notEqual name='<%=formName%>' property="project" value="">
				<td>
					<bean:write name="<%=formName%>" property="project" />
				</td>
			</logic:notEqual>
			<% } %>
		</tr>
		<% if ( useSubjectNo ) { %>
		<tr>
			<td >
				<bean:message key="patient.subject.number" />
				:
			</td>
			<html:hidden name='<%=formName%>' styleId="subjectNew"
				property="subjectNew" />
			<logic:equal name='<%=formName%>' property="subjectNo" value="">
				<td>
					<html:text name='<%=formName %>' property="subjectNo"
						onchange="makeDirty();$('subjectNew').value = true;" />
				</td>
			</logic:equal>
			<logic:notEqual name='<%=formName%>' property="subjectNo" value="">
				<td>
					<bean:write name="<%=formName%>" property="subjectNo" />
				</td>
			</logic:notEqual>
		</tr>
		<% } %>
		<% if ( FormFields.getInstance().useField(Field.StNumber) ) { %>
		<tr>
			<td >
				<bean:message key="patient.ST.number" /> :
			</td>
			<html:hidden name='<%=formName%>' property="newSTNumber" styleId="newSTNumber"/>
			<logic:equal name='<%=formName%>' property="STNumber" value="">
				<td>
					<html:text name='<%=formName %>' property="STNumber"
						onchange="makeDirty();$('newSTNumber').value = true;" />
				</td>
			</logic:equal>
			<logic:notEqual name='<%=formName%>' property="STNumber" value="">
				<td>
					<bean:write name="<%=formName%>" property="STNumber" />
				</td>
			</logic:notEqual>
		</tr>
		<% } %>
		<% if ( useExternalID ) { %>
		<tr>
			<td >
			    <%=StringUtil.getContextualMessageForKey("patient.externalId") %> :
			</td>
			<html:hidden name='<%=formName%>' property="externalIdNew" styleId="externalIdNew"/>
			<%-- <logic:equal name='<%=formName%>' property="externalId" value=""> --%>
				<td>
					<html:text name='<%=formName %>' property="externalId" styleClass="text"
						onchange="makeDirty();$('externalIdNew').value = true;" size="70" />
				</td>
			<%-- </logic:equal>
			<logic:notEqual name='<%=formName%>' property="externalId" value="">
				<td>
					<bean:write name="<%=formName%>" property="externalId" />
				</td>
			</logic:notEqual> --%>
		<% } %>
		<% if ( !isNonConformity ) { %>
			<td >
			    <%=StringUtil.getContextualMessageForKey("patient.NationalID") %>
				:
			</td>
			<html:hidden name='<%=formName%>'	property="nationalIdNew" styleId="nationalIdNew"/>
			<logic:equal name='<%=formName%>' property="nationalId" value="">
				<td>
					<html:text name='<%=formName %>' property="nationalId"
						onchange="makeDirty();$('nationalIdNew').value = true;" />
				</td>
			</logic:equal>
			<logic:notEqual name='<%=formName%>' property="nationalId" value="">
				<td>
					<bean:write name="<%=formName%>" property="nationalId" />
				</td>
			</logic:notEqual>	
		</tr>
		<% } %>
		<tr>
		<html:hidden name='<%=formName%>' styleId="serviceNew" property="serviceNew" />
			<html:hidden name='<%=formName%>' styleId="newServiceName" property="newServiceName" />
			<logic:equal name='<%=formName%>' property="service" value="">
			<% if ( !isNonConformity ) { %>
				<% if( useSiteList ){ %>
						<td><%= StringUtil.getContextualMessageForKey("sample.entry.project.siteName") %>
						<td>
							<html:select styleId="site"
									     name="<%=formName%>"
									     property="service"
									     onchange="makeDirty();$('serviceNew').value = true;">
								<option value=""></option>
								<html:optionsCollection name="<%=formName%>" property="siteList" label="value" value="id" />
						   	</html:select>
						</td>
				<% } else { %>
						<td ><bean:message key="patient.project.service" />:</td>
				  		<td><html:text name='<%=formName %>' property="service" onchange="makeDirty();$('serviceNew').value = true;"/></td>
				<% }%>
			<% }%>
			</logic:equal>
				<logic:notEqual name='<%=formName%>' property="service" value="">
						
							<td ><%= StringUtil.getContextualMessageForKey("sample.entry.project.siteName") %>:</td>
							<td>
								<bean:write name='<%=formName%>' property="service" />
							</td>
				</logic:notEqual>
				<% if ( !isNonConformity ) { %>
					<%  if (FormFields.getInstance().useField(Field.QA_FULL_PROVIDER_INFO )) { %>
	        			<% if( FormFields.getInstance().useField( Field.QA_REQUESTER_SAMPLE_ID )) { %>
								<td><bean:message key="sample.clientReference" />:</td>
									<td >
									<logic:equal name='<%=formName%>' property="requesterSampleID" value="">
										<app:text name="<%=formName%>"
												  property="requesterSampleID"
												  size="30"
											  	onchange="makeDirty();"/>
									</logic:equal>		  	
									<logic:notEqual name='<%=formName%>' property="requesterSampleID" value="">					
										<bean:write name="<%=formName%>" property="requesterSampleID" />
									</logic:notEqual>
									</td>
								<td colspan="2">&nbsp;</td>
		        		<% } %>
		        	<% } %>
	        	<% } %>
			</tr>
		<html:hidden name='<%=formName%>' styleId="doctorNew" property="doctorNew" />
		<%  if (FormFields.getInstance().useField(Field.QA_FULL_PROVIDER_INFO )) { %>
				<% if ( !isNonConformity ) { %>
					<tr>
						<td><%= StringUtil.getContextualMessageForKey("nonconformity.provider.label") %></td>
					</tr>
				<% } %>
				<tr>
					<%-- <logic:equal name='<%=formName%>' property="providerLastName" value=""> --%>
						<td align="left"><bean:message key="patient.epiFullName"/> :</td>
						<td >
						<html:text name="<%=formName%>"
								  property="providerFirstName"
							      styleId="providerFirstNameID"
							      onchange="makeDirty();$('doctorNew').value = true;"
							      size="30" />
						</td>		      
					<%-- </logic:equal>
					<logic:notEqual name='<%=formName%>' property="providerLastName" value="">
						<td align="left"><bean:message key="person.name" /> :</td>
						<td><bean:write name="<%=formName%>" property="providerLastName" />,&nbsp;<bean:write name="<%=formName%>" property="providerFirstName" /></td>
					</logic:notEqual> --%>
					<td align="left">
						<bean:message  key="person.streetAddress.street" /> :
					</td>
					<td>
					<%-- <logic:equal name='<%=formName%>' property="providerStreetAddress" value=""> --%>
						<app:text name='<%=formName%>'
					  			  property="providerStreetAddress"
					  			  styleClass="text"
					  			  size="70" />
					<%-- </logic:equal>
					<logic:notEqual name='<%=formName%>' property="providerStreetAddress" value="">
						<bean:write name="<%=formName%>" property="providerStreetAddress" />
					</logic:notEqual> --%>
					</td>		      
				</tr>
			<% if( FormFields.getInstance().useField(Field.ADDRESS_VILLAGE )) { %>
				<tr>
					<td align="left">
					    <%= StringUtil.getContextualMessageForKey("person.town") %> :
					</td>
					<td>
					<logic:equal name='<%=formName%>' property="providerCity" value="">
						<app:text name='<%=formName%>'
								  property="providerCity"
								  styleClass="text"
								  size="30" />
					</logic:equal>
					<logic:notEqual name='<%=formName%>' property="providerCity" value="">
						<bean:write name="<%=formName%>" property="providerCity" />
					</logic:notEqual>			  
					</td>
				</tr>
			<% } %>
			<% if( FormFields.getInstance().useField(Field.ADDRESS_COMMUNE)){ %>
			<tr>
				<td align="left">
					<bean:message  key="person.commune" /> :
				</td>
				<td>
				<%-- <logic:equal name='<%=formName%>' property="providerCommune" value=""> --%>
					<app:text name='<%=formName%>'
							  property="providerCommune"
							  styleClass="text"
							  size="30" />
				<%-- </logic:equal>
				<logic:notEqual name='<%=formName%>' property="providerCommune" value="">
					<bean:write name="<%=formName%>" property="providerCommune" />
				</logic:notEqual> --%>
				</td>
			<% } %>
			<% if ( !isNonConformity ) { %>
				<td align="left">
					<bean:message key="person.phone" />&nbsp;
					(<%= PhoneNumberService.getPhoneFormat() %>)
				</td>
				<td>
				<logic:equal name='<%=formName%>' property="providerWorkPhone" value="">
					<app:text name="<%=formName%>"
					          property="providerWorkPhone"
						      styleId="providerWorkPhoneID"
						      size="30"
						      maxlength="35"
						      styleClass="text"
						      onchange="validatePhoneNumber(this);makeDirty();$('doctorNew').value = true;" />
				    <div id="providerWorkPhoneMessage" class="blank" ></div>
				</logic:equal>    
				<logic:notEqual name='<%=formName%>' property="providerWorkPhone" value="">
					<bean:write name="<%=formName%>" property="providerWorkPhone" />
				</logic:notEqual>
				</td>
			<% } %>
			</tr>
			<% if( FormFields.getInstance().useField(Field.ADDRESS_DEPARTMENT )){ %>
			<tr>
				<td align="left">
					<bean:message  key="person.department" />:
				</td>
				<td>
				<logic:equal name='<%=formName%>' property="providerDepartment" value="">
					<html:select name='<%=formName%>'
								 property="providerDepartment"
							     styleId="departmentID" >
					<option value="0" ></option>
					<html:optionsCollection name="<%=formName %>" property="departments" label="value" value="id" />
					</html:select>
				</logic:equal>	
				<logic:notEqual name='<%=formName%>' property="providerDepartment" value="">
					<bean:write name="<%=formName%>" property="providerDepartment" />
				</logic:notEqual>
				</td>
			</tr>
			<% } %>
		<% } else { %>
			<tr>
				<td ><bean:message key="sample.entry.project.doctor" />:</td>
				<logic:equal name='<%=formName%>' property="doctor" value="">
					<td>
						<html:text name='<%=formName %>' property="doctor"
							onchange="makeDirty();$('doctorNew').value = true;" />
					</td>
				</logic:equal>
				<logic:notEqual name='<%=formName%>' property="doctor" value="">
					<td >
						<bean:write name="<%=formName%>" property="doctor" />
					</td>
				</logic:notEqual>
			</tr>
		<% } %>
	</table>
	
	<hr />
	<table style="width:100%" id="qaEventsTable">
		<thead>
		<tr>
			<th style="display: none"></th>
			<% if( FormFields.getInstance().useField(Field.QA_DOCUMENT_NUMBER)){ %>
				<th style="width:100px">
					<bean:message key="nonConformity.document.number" /><br> <%= NonConformityRecordNumberValidationProvider.getDocumentNumberFormat() %>  :
				</th>
			<% } %>
			<th style="width:22%">
				<bean:message key="label.refusal.reason" /><span class="requiredlabel">*</span>
			</th>
			<th style="width:16%">
				<bean:message key="label.sampleType" /><span class="requiredlabel">*</span>
			</th>
			<th style="width:11%">
				<%=StringUtil.getContextualMessageForKey("nonconformity.section") %>
			</th>
			<th style="width:13%">
				<%=StringUtil.getContextualMessageForKey("label.biologist") %>
			</th>
			<th style="width:28%;">
				<%= StringUtil.getContextualMessageForKey("nonconformity.note") %>
			</th>
			<th style="width:5%">
				<bean:message key="label.remove" />
			</th>
		</tr>
		</thead>
		<tbody>
		<logic:iterate id="qaEvents" name="<%=formName%>" property="qaEvents"
			indexId="index" type="QaEventItem">
			<tr id="qaEvent_<%=index%>" style="display: none;" class="qaEventRow">
			<% if( FormFields.getInstance().useField(Field.QA_DOCUMENT_NUMBER)){ %>
				<td>
					<html:text 
						styleId='<%="record" + index%>'
						styleClass="readOnly qaEventElement" 
						name='qaEvents'
						property = 'recordNumber'
						indexed = 'true'
						size = '12'
						onchange="makeDirty();validateRecordNumber(this)" />		 
				</td>
			<% } %>
				<td style="display: none">
					<html:hidden styleId='<%="id" + index%>' styleClass="id" name="qaEvents"
						property="id" indexed="true" onchange='makeDirty();' />
				</td>
				<td>
					<html:select styleId='<%="qaEvent" + index%>' 
								 name="qaEvents"
						         styleClass="readOnly qaEventElement requiredField" 
						         property="qaEvent"
						         indexed="true" 
						         disabled = "true"						    
						         style="width: 99%" 
						         onchange='makeDirty(); updateRequiredFields(this);'>
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>"
							property="qaEventTypes" label="value" value="id" />
					</html:select>
				</td>
				<td>
                    <html:select styleId='<%="sampleType" + index%>' name="qaEvents"
						styleClass="readOnly qaEventElement typeOfSample requiredField" disabled="false"
						property="sampleType" onchange='makeDirty(); updateRequiredFields(this);' indexed="true"
						style="width: 99%">
                        <option value="0"></option>
                        <option value="-1"  <%= (qaEvents.getSampleType() != null && qaEvents.getSampleType().equals("-1")) ? "selected='selected'" : ""%> ></option>
						<html:optionsCollection name="<%=formName%>"
							property="typeOfSamples" label="value" value="id" />
					</html:select>
				</td>
				<td>
					<html:select name="qaEvents" styleId='<%="section" + index%>'
						styleClass="readOnly qaEventElement" disabled="false"
						property="section" indexed="true" style="width: 99%"
						onchange='makeDirty();'>
						<option ></option>
						<html:optionsCollection name="<%=formName%>" property="sections"
							label="localizedName" value="nameKey" />
					</html:select>
				</td>
				<td>
					<html:text styleId='<%="author" + index%>' name="qaEvents"
						styleClass="readOnly qaEventElement" disabled="false"
						property="authorizer" indexed="true" onchange='makeDirty();' />
				</td>
				<td>
					<html:text styleId='<%="note" + index%>'
					           name="qaEvents"
							   styleClass="readOnly qaEventElement" 
							   disabled="false"
							   property="note"
							   indexed="true" 
							   onchange='makeDirty();'
							   style="width: 99%" />
				</td>
				<td>
					<html:checkbox styleId='<%="remove" + index%>' name="qaEvents"
						styleClass="qaEventEnable" property="remove" indexed="true"
						onclick='makeDirty(); updateRequiredFields(this);' style="margin-left: 40px;margin-top: -7px;"/>
				</td>
			</tr>
		</logic:iterate>
		</tbody>
	</table>
	<input type="button" id="addButtonId"  class="btn btn-default"
		value='<%=StringUtil.getMessageForKey("label.button.add")%>'
		onclick="addRow()" />
	<hr />
	<html:hidden name='<%=formName%>' styleId="commentNew"
		property="commentNew" value="" />
	<center><bean:message key="label.comments" />:
	<div style="width: 50%">
		<html:textarea name="<%=formName %>" property="comment"
			onchange="makeDirty();$('commentNew').value = true;"
			disabled='<%=readOnly%>' style="resize:none;" rows="6" />
	</div></center>
</logic:notEmpty>

<script type="text/javascript" >
//all methods here either overwrite methods in tiles or all called after they are loaded

function makeDirty() {
	dirty = true;

	if (typeof (showSuccessMessage) != 'undefinded') {
		showSuccessMessage(false);
	}
	// Adds warning when leaving page if content has been entered into makeDirty form fields
	function formWarning(){ 
    return "<bean:message key="banner.menu.dataLossWarning"/>";
	}
	window.onbeforeunload = formWarning;
	
	setSave();
}

function savePage() {
	if (areNewTypesOfSamples() && !confirm(confirmNewTypeMessage)) {
		return false;
	}
	
	var form = window.document.forms[0];
  
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	form.action = "NonConformityUpdate.do";
	form.submit();
}

addRow(); // call once at the beginning to make the table of Sample QA Events look right.
initializeRowFields();
tweekSampleTypeOptions();

<% if( FormFields.getInstance().useField(Field.NON_CONFORMITY_SITE_LIST_USER_ADDABLE)){ %>
// Moving autocomplete to end - needs to be at bottom for IE to trigger properly
$jq(document).ready( function() {
     	var dropdown = $jq( "select#site" );
        autoCompleteWidth = dropdown.width() + 66 + 'px';
        clearNonMatching = false;
        capitialize = true;
        dropdown.combobox();
       // invalidLabID = '<bean:message key="error.site.invalid"/>'; // Alert if value is typed that's not on list. FIX - add badmessage icon
        maxRepMsg = '<bean:message key="sample.entry.project.siteMaxMsg"/>'; 
        
        resultCallBack = function( textValue) {
  				siteListChanged(textValue);
  				makeDirty();
  				setSave();
				};
});
<%  } %>

</script>
