<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName" value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="workplanType" name="<%=formName%>" property="workplanType" />
<bean:define id="responseAction" name='<%= formName %>' property="searchAction"  />

<%!
	String basePath = "";
%>
<%
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
			+ request.getServerPort() + path + "/";
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" language="JavaScript1.2">

var invalidElements = new Array();
var requiredFields = new Array("testName");

function doShowTests() {
	/* if ( (!$('receivedDate').value || $('receivedDate').value == "") &&
			(!$('resultDate').value || $('resultDate').value == "") ) {
		validateField($('receivedDate'));
		validateField($('resultDate'));
		alert('You should input one of the search dates (Received Date or Result Date).');
	} else { */
		window.location.href = '<%=responseAction%>' + '?type=' + '<%=workplanType %>' + '&selectedSearchID=' + $('testName').value + 
								'&receivedDate=' + $('receivedDate').value 
								+ '&startedDate=' + $('startedDate').value
								+ '&completedDate=' + $('completedDate').value;
	//}
	$('testName').focus();
}

function validateTest(){
	if ( fieldIsEmptyById( "testName" ) ) {
		setValidIndicaterOnField(false, "isValid");
	}
}

function /*boolean*/ handleEnterEvent(){
	if( !fieldIsEmptyById("testName")){
		doShowTests();
	}
	return false;
}

// Added by markaae.fr 2016-10-03 09:50AM
function /*void*/ updateFieldError(field) {
	if (field.value) {
		selectFieldErrorDisplay(true, $('receivedDate'));
		selectFieldErrorDisplay(true, $('resultDate'));
		setSampleFieldValidity(true, $('receivedDate').id);
		setSampleFieldValidity(true, $('resultDate').id);
	}
}

function /*void*/ validateField(field) {
	var isValid = false;
	if (field.value) {
		isValid = true;
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
	setSearch();
	
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
        setSearch();
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
	        setSearch();
	        return;
	        
	    } else {
	        //ajax call from utilities.js
	    	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	    }
    } catch (Exception) {
    	selectFieldErrorDisplay( false, $(date.id));
        setSampleFieldValidity( false, date.id );
        setSearch();
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

//disable or enable submit/search button based on validity of fields
function setSearch() {
	var validToSearch = isSearchEnabled() && requiredFieldsValid();
	//disable or enable submit/search button based on validity/visibility of fields
	$("searchWorkplanID").disabled = !validToSearch;
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

</script>

<div id="PatientPage" class="colorFill" style="display:inline" >
	<h2><bean:message key="sample.entry.search"/></h2>
	<table style="margin-left: auto; margin-right: auto; background-color: inherit;">
		<tr>
   		<%-- <% if ( workplanType.equals("test") || workplanType.equals("panel") ) { %> --%>
			<td>
				<%-- <bean:write name="<%=formName %>" property="searchLabel"/> --%>
				<bean:message key="sample.entry.show.message"/><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>
				<html:select name="<%=formName%>" property="selectedSearchID" styleId="testName" 
							 onchange="validateField(this);" onblur="setSearch();" style="margin-left:3px; margin-right:6px;">
					<app:optionsCollection name="<%=formName%>" property="searchTypes" label="value" value="id" />
				</html:select>
	   		</td>
	   		<%-- <td>
	   			<html:select name="<%=formName%>" property="selectedDateCriteria" styleId="searchCriteria"
	   						 onchange="validateField(this);" onblur="setSearch();" style="margin-left:3px;">
					<app:optionsCollection name="<%=formName%>" property="searchDateCriteria" label="value" value="id" />
				</html:select>
		       <span class="requiredlabel">*</span>:&nbsp;
			</td>
			--%>
			<td>
				<bean:message key="sample.receivedDate"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="receivedDate" onblur="checkValidEntryDate(this,'past',true);" maxlength="10"
						property="receivedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:6px; margin-right:3px;" />
		   	</td>
		   	<td>
				<bean:message key="result.test.beginDate"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="startedDate" onblur="checkValidEntryDate(this,'past',true);" maxlength="10"
						property="startedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:6px; margin-right:3px;" />
		   	</td>
		   	<td>
				<bean:message key="analysis.result.date"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="completedDate" onblur="checkValidEntryDate(this,'past',true);" maxlength="10"
						property="completedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:6px; margin-right:3px;" />
		   	</td>
		   	<td>
	            <input type="button"
		           name="searchWorkplanButton"
		           class="btn btn-default"
		           value="<%= StringUtil.getMessageForKey("label.patient.search")%>"
		           id="searchWorkplanID"
		           onclick="doShowTests();"
		           disabled="disabled"
		           style="margin-top: -9px!important;height:30px;margin-left:3px;"
		           class="btn btn-default"
		           disabled="false" />
		   	</td>
		</tr>
	</table>
	<br/>
	<h1>
		<bean:write name="<%=formName%>" property="testName"/>
	</h1>
</div>

<ajax:autocomplete
  source="testName"
  target="selectedTestID"
  baseUrl="ajaxAutocompleteXML"
  className="autocomplete"
  parameters="testName={testName},provider=TestAutocompleteProvider,fieldName=testName,idName=id"
  indicator="indicator"
  minimumCharacters="1"
  parser="new ResponseXmlToHtmlListParser()"
  />
