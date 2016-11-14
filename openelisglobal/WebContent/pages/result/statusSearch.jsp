<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
				 us.mn.state.health.lims.common.formfields.FormFields,
				 us.mn.state.health.lims.common.formfields.FormFields.Field,
				 us.mn.state.health.lims.common.util.DateUtil,
                 us.mn.state.health.lims.common.util.StringUtil,
				 us.mn.state.health.lims.common.util.Versioning"  %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />

<%!
	boolean useCollectionDate = true;
	boolean useSampleStatus = false;
 %>
<%
	useCollectionDate = FormFields.getInstance().useField(Field.CollectionDate);
	useSampleStatus = FormFields.getInstance().useField(Field.SearchSampleStatus);
	
	String basePath = "";
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
 
<script type="text/javascript" language="JavaScript1.2">

var useModalSampleEntry = true;
var newSearchInfo = false;
var invalidElements = new Array();
var requiredFields = new Array();
var isPrimarySearchEnabled = true;
var isSecondarySearchEnabled = true;
var isAccessionNumberFromValid = true;


$jq(document).ready(function () {
	/*if (isPrimarySearchEnabled && isSecondarySearchEnabled) {
		enableAllSearchFields();
	}*/
	// Initialize "Save" button status
	//setSave();
});

//Added by markaae.fr 2016-10-04 04:03PM
function /*void*/ validateField(field) {
	var isValid = false;
	if (field.value) {
		isValid = true;
		isAccessionNumberFromValid = true;
	} else {
		isAccessionNumberFromValid = false;
		alert("Please input a value for 'Accession Number From'.");
	}
	selectFieldErrorDisplay(isValid, $(field.id));
	setSampleFieldValidity(isValid, field.id);
}

function  /*void*/ processValidateEntryDateSuccess(xhr) {
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var isValid = message == "<%=IActionConstants.VALID%>";

	//utilities.js
	selectFieldErrorDisplay( isValid, $(formField));
	setSampleFieldValidity( isValid, formField );
	//setSearch();
	
	if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
		alert( "<bean:message key="error.date.inFuture"/>" );
	}else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
		alert( "<bean:message key="error.date.inPast"/>" );
	}
	
	if (!isValid) $(formField).focus();
}

//check date enter is valid
function checkValidEntryDate(date, dateRange, blankAllowed) {
	var isNumeric = true;
	if (date.value.indexOf("/") > 0 && date.value.length <= 6) {
		var dateSplit = date.value.split("/");
		var newDate = new Date(dateSplit[1] + "/" + dateSplit[0]);
		if (newDate != "Invalid Date") {
			var yyyy = new Date().getFullYear();
			var mm = (newDate.getMonth()+1).toString(); // getMonth() is zero-based
			var dd  = newDate.getDate().toString();
			date.value = (dd[1]?dd:"0"+dd[0]) + "/" + (mm[1]?mm:"0"+mm[0]) + "/" + yyyy;
		}
	}

	if((!date.value || date.value == "") && !blankAllowed){
        isNumeric = false;
    } else if ((!date.value || date.value == "") && blankAllowed) {
    	selectFieldErrorDisplay( true, $(date.id));
        setSampleFieldValidity( true, date.id );
        //setSearch();
        return;
    }
    
    if( !dateRange || dateRange == ""){
        dateRange = 'past';
    }
	
    // Check if date value is numeric
    try {
	    var dateSplit = date.value.split("/");
	    if (isNotaNumber(dateSplit[0]) || isNotaNumber(dateSplit[1]) || isNotaNumber(dateSplit[2]) || !isNumeric) {
	    	selectFieldErrorDisplay( false, $(date.id));
	        setSampleFieldValidity( false, date.id );
	        //setSearch();
	        return;
	        
	    } else {
	        //ajax call from utilities.js
	    	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	    }
    } catch (Exception) {
    	selectFieldErrorDisplay( false, $(date.id));
        setSampleFieldValidity( false, date.id );
        //setSearch();
        return;
    }
}

// Check date enter is number
function /*boolean*/ isNotaNumber(str) {
	var regex = /^[a-zA-Z]+$/;
	// loop through every character
	for(var i=0; i < str.length; i++) {
		// check if the i-th character is not a number
		if(isNaN(str[i]) || regex.test(str)) {
			return true;
		}
	}
	// if the loop has finished and no letters have been found, return false
	return false;
}

function setSampleFieldValidity(valid, fieldId) {
	if (valid) {
		setFieldValid(fieldId);
	} else {
		setFieldInvalid(fieldId);
	}
}

function setFieldInvalid(field) {
    if ( $jq.inArray(field, invalidElements) == -1 ) {
        invalidElements.push(field);
    }
}

function setFieldValid(field) {
    var removeIndex = $jq.inArray(field, invalidElements);
    if ( removeIndex != -1 ) {
    	invalidElements.splice(removeIndex, 1);
    }
}

function setFieldRequired(field) {
    if ( $jq.inArray(field, requiredFields) == -1 ) {
    	requiredFields.push(field);
    }
}

function setFieldNotRequired(field) {
    var removeIndex = $jq.inArray(field, requiredFields);
    if ( removeIndex != -1 ) {
    	requiredFields.splice(removeIndex, 1);
    }
}

//disable or enable submit/search button based on validity of fields
function setSearch() {
	var validToSearch = isSearchEnabled() && requiredFieldsValid();
	//disable or enable submit/search button based on validity/visibility of fields
	$("searchResultButton").disabled = !validToSearch;
}

function /*bool*/ isSearchEnabled() {
	return invalidElements.length == 0;
}

function /*bool*/ requiredFieldsValid(){
    for(var i = 0; i < requiredFields.length; ++i){
        // check if required field exists
        if (!$jq('#' + requiredFields[i]).length)
        	return false;
        if ($jq('#' + requiredFields[i]).is(':input')) {
    		// check for empty input values
        	if ($jq.trim($jq('#' + requiredFields[i]).val()).length === 0)
            	return false;
        } else {
			// check for empty spans/divs
			if ($jq.trim($jq('#' + requiredFields[i]).text()).length === 0)
				return false;
		}
    }
    
    return true;
}
// End of Code Addition

function doShowTests() {
	/*if (isPrimarySearchEnabled && !isSecondarySearchEnabled) {
		validateField($('accessionNumberFrom'));
	}*/
	//if (isAccessionNumberFromValid) {
		newSearchInfo = false;
		var form = document.forms[0];

		form.action = "StatusResults.do"; 
		form.submit();
	//}
}

function /*boolean*/ handleEnterEvent() {
	if( newSearchInfo ){
		doShowTests();
	}
	return false;
}

function /*void*/ dirtySearchInfo(e) { 
	var code = e ? e.which : window.event.keyCode;
	if( code != 13 ){
		newSearchInfo = true; 
	}
}

function /*void*/ enableDisableSearchFields(field) { 
	if (field.id == "accessionNumberFrom" || field.id == "accessionNumberTo" || field.id == "selectedResultStatus") {
		if (!(!field.value || field.value == "")) {
			disableSecondarySearchFields();
		} else {
			enableAllSearchFields();
		}
	} else {
		if (!(!field.value || field.value == "")) {
			disablePrimarySearchFields();
		} else {
			enableAllSearchFields();
		}
	}
}

//Enable all search parameters
function /*void*/ enableAllSearchFields() {
	isPrimarySearchEnabled = true;
	isSecondarySearchEnabled = true;
	// Set Test Name as required field
	setFieldRequired($('selectedTest'));
	setFieldNotRequired($('accessionNumberFrom'));
	$('accessionNumberFrom').disabled = false;
	$('accessionNumberTo').disabled = false;
	$('selectedResultStatus').disabled = false;
	$('resultDate').disabled = false;
	$('collectionDate').disabled = false;
	$('recievedDate').disabled = false;
	$('selectedTest').disabled = false;
	$('selectedAnalysisStatus').disabled = false;
	//$('selectedSampleStatus').disabled = false;
}

// Disable primary search parameters
function /*void*/ disablePrimarySearchFields() {
	isPrimarySearchEnabled = false;
	isSecondarySearchEnabled = true;
	// Set Test Name as required field
	setFieldRequired($('selectedTest'));
	setFieldNotRequired($('accessionNumberFrom'));
	// Disable primary search parameters
	$('accessionNumberFrom').disabled = true;
	$('accessionNumberTo').disabled = true;
	$('selectedResultStatus').disabled = true;
	// Enable secondary search parameters
	$('resultDate').disabled = false;
	$('collectionDate').disabled = false;
	$('recievedDate').disabled = false;
	$('selectedTest').disabled = false;
	$('selectedAnalysisStatus').disabled = false;
	//$('selectedSampleStatus').disabled = false;
}

//Disable secondary search parameters
function /*void*/ disableSecondarySearchFields() {
	isPrimarySearchEnabled = true;
	isSecondarySearchEnabled = false;
	// Set Accession Number From as required field
	setFieldRequired($('accessionNumberFrom'));
	setFieldNotRequired($('selectedTest'));
	// Enable primary search parameters
	$('accessionNumberFrom').disabled = false;
	$('accessionNumberTo').disabled = false;
	$('selectedResultStatus').disabled = false;
	// Disable secondary search parameters
	$('resultDate').disabled = true;
	$('collectionDate').disabled = true;
	$('recievedDate').disabled = true;
	$('selectedTest').disabled = true;
	$('selectedAnalysisStatus').disabled = true;
	//$('selectedSampleStatus').disabled = true;
}

</script>

<div id="PatientPage" class="colorFill" style="display:inline" >

	<h2><bean:message key="sample.entry.search"/></h2>
	<table style="margin-left:20px; margin-right:20px; margin-top:20px; background-color: inherit;">
		<!-- Accession Number & Result Status (Search Criteria) -->
		<tr align="left">
			<td colspan="10">
				<%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%><!-- <span class="requiredlabel">*</span> -->:&nbsp;
				<html:text name="<%=formName%>" property="accessionNumberFrom" styleId="accessionNumberFrom" maxlength="10"
						   style="margin-left:6px;margin-right:6px;" />
				<bean:message key="quick.entry.accession.number.thru"/>
				<html:text name="<%=formName%>" property="accessionNumberTo" styleId="accessionNumberTo" maxlength="10"
						   style="margin-left:6px;margin-right:6px;" />
				<%-- <bean:message key="analysis.result.status"/>:&nbsp;
				<html:select  name="<%=formName%>" property="selectedResultStatus" onchange="dirtySearchInfo(event);"
							   styleId="selectedResultStatus" style="margin-left:6px;margin-right:6px;" >
					<html:optionsCollection name="<%=formName%>" property="resultStatusSelections" label="description" value="id" />
				</html:select> --%>
			</td>
		</tr>
		<tr>
			<td colspan="5"><h1></h1></td>
		</tr>
		<tr align="left">
			<%-- <td >
				<% if(useCollectionDate){ %>  <%= StringUtil.getContextualMessageForKey("sample.collectionDate")  %>:&nbsp;<br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span> <% } %>
			</td>
			<td >
				<% if(useCollectionDate){ %> <html:text name="<%=formName%>" property="collectionDate" onkeyup="dirtySearchInfo(event); addDateSlashes(this, event);" 
														styleId="collectionDate" onblur="checkValidEntryDate(this, 'past', true);" maxlength="10" style="width:100px; margin-left:5px; margin-right:5px;" /> <%} %>
			</td> --%>
			
			<td >
				<bean:message key="sample.receivedDate"/>:&nbsp;<%-- <br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span> --%>
			</td>
			<td>
				<html:text name="<%=formName%>" property="recievedDate" onkeyup="dirtySearchInfo(event); addDateSlashes(this, event);"
						   styleId="recievedDate" onblur="checkValidEntryDate(this, 'past', true);" maxlength="10" style="width:100px; margin-left:5px; margin-right:5px;" />
			</td>
			<td >
				<bean:message key="result.test.beginDate"/>:&nbsp;<%-- <br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span> --%>
			</td>
			<td>
				<html:text name="<%=formName%>" property="startedDate" onkeyup="dirtySearchInfo(event); addDateSlashes(this, event);"
						   styleId="startedDate" onblur="checkValidEntryDate(this, 'past', true);" maxlength="10" style="width:100px; margin-left:5px; margin-right:5px;" />
			</td>
			<td >
				<bean:message key="analysis.result.date"/>:&nbsp;<%-- <br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span> --%>
			</td>
			<td>
				<html:text name="<%=formName%>" property="completedDate" onkeyup="dirtySearchInfo(event); addDateSlashes(this, event);"
						   styleId="completedDate" onblur="checkValidEntryDate(this, 'past', true);" maxlength="10" style="width:100px; margin-left:5px; margin-right:5px;" />
			</td>
			
			<td >
				<bean:message key="test.testName"/><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>
					<html:select  name="<%=formName%>" property="selectedTest" styleId="selectedTest" onchange="dirtySearchInfo(event);" style="margin-left:5px; margin-right:5px;">
						<html:optionsCollection name="<%=formName%>"  property="testSelections" label="value" value="id"/>
					</html:select>
			</td>
			
			<td >
				<%-- <%= StringUtil.getContextualMessageForKey("analysis.status") %> --%>
				<%= StringUtil.getContextualMessageForKey("analysis.result.status") %>
			</td>
			<td>
					<%-- <html:select  name="<%=formName%>" property="selectedAnalysisStatus" styleId="selectedAnalysisStatus" onchange="dirtySearchInfo(event);" >
						<html:optionsCollection name="<%=formName%>" property="analysisStatusSelections" label="description" value="id" />
					</html:select> --%>
					<html:select name="<%=formName%>" property="selectedResultStatus" onchange="dirtySearchInfo(event);"
							     styleId="selectedResultStatus" style="margin-left:6px;margin-right:6px;" >
							<html:optionsCollection name="<%=formName%>" property="resultStatusSelections" label="description" value="id" />
					</html:select>
			</td>
			
			<% if( useSampleStatus ){ %>
			<td >
				<bean:message key="sample.status"/>
			</td>
			<% } %>	
			<% if( useSampleStatus ){ %>
			<td>
					<html:select  name="<%=formName%>" property="selectedSampleStatus" styleId="selectedSampleStatus" onchange="dirtySearchInfo(event);">
						<html:optionsCollection name="<%=formName%>" property="sampleStatusSelections" label="description" value="id" />
					</html:select>
			</td>
			<% } %>
		</tr>
	</table>
	
	<table align="center" style="margin-top:10px; margin-bottom:10px;">
		<tr>
			<td>
				<html:button property="searchButton" styleId="searchResultButton" onclick="doShowTests()" styleClass="btn btn-default">
					<bean:message key="resultsentry.status.search"/>
				</html:button>
			</td>
		</tr>
	</table>
	
</div>
