<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date, java.util.List,
	org.apache.struts.Globals,
	us.mn.state.health.lims.common.util.SystemConfiguration,
	us.mn.state.health.lims.common.action.IActionConstants,
	java.util.Collection,
	java.util.ArrayList,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.inventory.form.InventoryKitItem,
	us.mn.state.health.lims.test.beanItems.TestResultItem,
	us.mn.state.health.lims.common.util.IdValuePair,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.ConfigurationProperties,
	us.mn.state.health.lims.common.util.ConfigurationProperties.Property" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="workplanType"	value='<%=(String) request.getParameter("type")%>' />
<bean:define id="tests" name="<%=formName%>" property="workplanTests" />
<bean:size id="testCount" name="tests" />
<bean:define id="testSectionsByName" name="<%=formName%>" property="testSectionsByName" />

<%!
	boolean showAccessionNumber = false;
	boolean displayNoTest = false;
	String currentAccessionNumber = "";
	int rowColorIndex = 2;
%>
<%
	String basePath = "";
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
	+ request.getServerPort() + path + "/";
	currentAccessionNumber = "";

%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript" >

var invalidElementsTestSections = new Array();
var requiredFieldsTestSections = new Array("testSectionsId");


function  /*void*/ setMyCancelAction(form, action, validate, parameters) {
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function disableEnableTest(checkbox, index) {
	if (checkbox.checked) {
		$("row_" + index).style.background = "#cccccc";
	}else {
		checkbox.checked = "";
		$("row_" + index).style.backgroundColor = "";
	}
}

function submitTestSectionSelect() {
	/* if ( (!$('receivedDateTestSections').value || $('receivedDateTestSections').value == "") &&
			(!$('resultDateTestSections').value || $('resultDateTestSections').value == "") ) {
		validateFieldTestSections($('receivedDateTestSections'));
		validateFieldTestSections($('resultDateTestSections'));
		alert('You should input one of the search dates (Received Date or Result Date).');
		
	} else { */
		var testSectionNameIdHash = [];
		<%	for( IdValuePair pair : (List<IdValuePair>) testSectionsByName){
				out.print( "testSectionNameIdHash[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
			}
		%>
		window.location.href = "WorkPlanByTestSection.do?testSectionId=" + $('testSectionsId').value + "&type=" + "<%=workplanType %>" +
								'&receivedDate=' + $('receivedDateTestSections').value +
								'&completedDate=' + $('resultDateTestSections').value +
								'&startedDate=' + $('startedDateTestSections').value;
	//}
}

function printWorkplan() {
	var form = window.document.forms[0];
	form.action = "PrintWorkplanReport.do" + "?type=" + "<%=workplanType %>";
	form.target = "_blank";
	form.submit();
}

//Added by markaae.fr 2016-10-03 09:50AM
function /*void*/ updateFieldErrorTestSections(field) {
	if (field.value) {
		selectFieldErrorDisplay(true, $('receivedDateTestSections'));
		selectFieldErrorDisplay(true, $('resultDateTestSections'));
		setSampleFieldValidityTestSections(true, $('receivedDateTestSections').id);
		setSampleFieldValidityTestSections(true, $('resultDateTestSections').id);
	}
}

function /*void*/ validateFieldTestSections(field) {
	var isValid = false;
	if (field.value) {
		isValid = true;
	}
	selectFieldErrorDisplay(isValid, $(field.id));
	setSampleFieldValidityTestSections(isValid, field.id);
}

function  /*void*/ processValidateEntryDateSuccessTestSections(xhr) {
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var isValid = message == "<%=IActionConstants.VALID%>";

	//utilities.js
	selectFieldErrorDisplay( isValid, $(formField));
	setSampleFieldValidityTestSections( isValid, formField );
	setSearchTestSections();
	
	if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
		alert( "<bean:message key="error.date.inFuture"/>" );
	}else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
		alert( "<bean:message key="error.date.inPast"/>" );
	}
	
	if (!isValid) $(formField).focus();
}

//check date enter is valid
function checkValidEntryDateTestSections(date, dateRange, blankAllowed) {
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
    	setSampleFieldValidityTestSections( true, date.id );
        setSearchTestSections();
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
	    	setSampleFieldValidityTestSections( false, date.id );
	        setSearchTestSections();
	        return;
	        
	    } else {
	        //ajax call from utilities.js
	    	isValidDate( date.value, processValidateEntryDateSuccessTestSections, date.id, dateRange );
	    }
    } catch (Exception) {
    	selectFieldErrorDisplay( false, $(date.id));
    	setSampleFieldValidityTestSections( false, date.id );
        setSearchTestSections();
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

function setSampleFieldValidityTestSections(valid, fieldId) {
	if (valid) {
		setFieldValidTestSections(fieldId);
	} else {
		setFieldInvalidTestSections(fieldId);
	}
}

function setFieldInvalidTestSections(field) {
    if ( $jq.inArray(field, invalidElementsTestSections) == -1 ) {
        invalidElementsTestSections.push(field);
    }
}

function setFieldValidTestSections(field) {
    var removeIndex = $jq.inArray(field, invalidElementsTestSections);
    if ( removeIndex != -1 ) {
    	invalidElementsTestSections.splice(removeIndex, 1);
    }
}

//disable or enable submit/search button based on validity of fields
function setSearchTestSections() {
	var validToSearch = isSearchEnabledTestSections() && requiredFieldsTestSectionsValid();
	//disable or enable submit/search button based on validity/visibility of fields
	$("searchWorkplanIDTestSections").disabled = !validToSearch;
}

function /*bool*/ isSearchEnabledTestSections() {
	return invalidElementsTestSections.length == 0;
}

function /*bool*/ requiredFieldsTestSectionsValid(){
    for(var i = 0; i < requiredFieldsTestSections.length; ++i){
        // check if required field exists
        if (!$jq('#' + requiredFieldsTestSections[i]).length)
        	return false;
        if ($jq('#' + requiredFieldsTestSections[i]).is(':input')) {
    		// check for empty input values
        	if ($jq.trim($jq('#' + requiredFieldsTestSections[i]).val()).length === 0)
            	return false;
        } else {
			// check for empty spans/divs
			if ($jq.trim($jq('#' + requiredFieldsTestSections[i]).text()).length === 0)
				return false;
		}
    }
    
    return true;
}
// End of Code Addition

</script>

<% if( !workplanType.equals("test") && !workplanType.equals("panel") ){ %>
<div id="searchDiv" class="colorFill"  >
<div id="PatientPage" class="colorFill" style="display:inline" >
<h2><bean:message key="sample.entry.search"/></h2>
	<table style="margin-left: auto; margin-right: auto; background-color: inherit;">
		<tr>
			<td>
				<%-- <bean:write name="<%=formName %>" property="searchLabel"/> --%>
				<bean:message key="test.section.message"/><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>
				<html:select name='<%= formName %>' property="testSectionId" styleId="testSectionsId" 
							onchange="validateFieldTestSections(this);" onblur="setSearchTestSections();" style="margin-left:3px; margin-right:6px;" >
					<app:optionsCollection name="<%=formName%>" property="testSections" label="value" value="id" />
				</html:select>
	   		</td>
	   		<td>
				<bean:message key="sample.receivedDate"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="receivedDateTestSections" onblur="checkValidEntryDateTestSections(this,'past',true);" maxlength="10"
						property="receivedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:3px; margin-right:6px;" />
		   	</td>
		   	<td>
				<bean:message key="result.test.beginDate"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="startedDateTestSections" onblur="checkValidEntryDate(this,'past',true);" maxlength="10"
						property="startedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:6px; margin-right:3px;" />
		   	</td>
		   	<td>
				<bean:message key="analysis.result.date"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="resultDateTestSections" onblur="checkValidEntryDateTestSections(this,'past',true);" maxlength="10"
						property="completedDate" onkeyup="addDateSlashes(this, event);" style="width:100px; margin-left:3px; margin-right:3px;" />
		   	</td>
		   	<td>
	            <input type="button"
		           name="searchWorkplanTestSectionButton"
		           class="btn btn-default"
		           value="<%= StringUtil.getMessageForKey("label.patient.search")%>"
		           id="searchWorkplanIDTestSections"
		           onclick="submitTestSectionSelect();"
		           disabled="disabled"
		           style="margin-top: -9px!important;height:30px;margin-left:3px;"
		           class="btn btn-default"
		           disabled="false" />
		   	</td>
		</tr>
	</table>
	<br/>
	<h1>
		
	</h1>
</div>
</div>
<% }%>

<br/>
<logic:notEqual name="testCount" value="0">
<bean:size name='<%= formName %>' property="workplanTests" id="size" />
<html:button property="print" styleId="print"  onclick="printWorkplan();" styleClass="btn btn-default" >
	<bean:message key="workplan.print"/>
</html:button>
<br/><br/>
<bean:message key="label.total.tests"/> = <bean:write name="size"/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="./images/nonconforming.gif" /> = <bean:message key="result.nonconforming.item"/>
<br/><br/>
<Table width="100%" border="0" cellspacing="0">
	<tr>
		<th width="5%" style="text-align: center;">
			<bean:message key="label.button.remove"/>
		</th>
		<th width="2%" style="text-align: center;">
			<bean:message key="row.label"/>
		</th>
		<% if( workplanType.equals("test") ){ %>
			<th width="3%">&nbsp;</th>
		<% } %>
    	<th width="8%" style="text-align: center;">
    		<%= StringUtil.getContextualMessageForKey("quick.entry.accession.number") %>
		</th>
		<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true")){ %>
		<th width="5%" style="text-align: center;">
			<%= StringUtil.getContextualMessageForKey("patient.subject.number") %>
		</th>	
		<% } %>
		<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "true")){ %>
	    <th width="5%" style="text-align: center;">
	    	<bean:message key="sample.entry.nextVisit.date"/>
	    </th>
	    <% } %>
		<% if( !workplanType.equals("test") ){ %>
		<th width="3%">&nbsp;</th>
		<th width="30%" style="text-align: center;">
			<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "Haiti LNSP")){ %>
                <bean:message key="sample.entry.project.patient.and.testName"/>
	        <% } else { %>
	  		   <bean:message key="sample.entry.project.testName"/>
	  		<% } %>  
		</th>
		<% } %>
		<th width="10%" style="text-align: center;">
	  		<bean:message key="sample.receivedDate"/>&nbsp;&nbsp;
		</th>
		<!-- Added by Trung -->
		<th width="10%" style="text-align: center;">
	  		<bean:message key="result.test.beginDate"/>&nbsp;&nbsp;
		</th>
		<th width="10%" style="text-align: center;">
	  		<bean:message key="analysis.result.date"/>&nbsp;&nbsp;
		</th>
		<th width="13%" style="text-align: center;">
	  		<bean:message key="order.urgency"/>&nbsp;&nbsp;
		</th>
		<th width="15%" style="text-align: center;">
	  		<bean:message key="project.browse.title"/>&nbsp;&nbsp;
		</th>
  	</tr>

	<logic:iterate id="workplanTests" name="<%=formName%>"  property="workplanTests" indexId="index" type="TestResultItem">
			<% showAccessionNumber = !workplanTests.getAccessionNumber().equals(currentAccessionNumber);
				   if( showAccessionNumber ){
					currentAccessionNumber = workplanTests.getAccessionNumber();
					rowColorIndex++; } %>
     		<tr id='<%="row_" + index %>' class='<%=(rowColorIndex % 2 == 0) ? "evenRow" : "oddRow" %>'  >
     			<td id='<%="cell_" + index %>'  style="text-align: center;">
     			<% if (!workplanTests.isServingAsTestGroupIdentifier()) { %>
					<html:checkbox name="workplanTests"
						   property="notIncludedInWorkplan"
						   styleId='<%="includedCheck_" + index %>'
						   styleClass="includedCheck"
						   indexed="true"
						   onclick='<%="disableEnableTest(this," + index + ");" %>' />
			    <% } %>
				</td>
				<td style="text-align:center;">
		    		<%= index + 1 %>
		   		</td>
				<% if( workplanType.equals("test") ){ %>
				<td>
					<logic:equal name="workplanTests" property="nonconforming" value="true">
						<img src="./images/nonconforming.gif" />
					</logic:equal>
				</td>	
				<% } %>
	    		<td style="text-align: center;">
	      		<% if( showAccessionNumber ){%>
	      			<bean:write name="workplanTests" property="accessionNumber"/>
				<% } %>
	    		</td>
	    		<% if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.SUBJECT_ON_WORKPLAN, "true")){ %>
	    		<td>
	    			<% if(showAccessionNumber ){ %>
	    			<bean:write name="workplanTests" property="patientInfo"/>
	    			<% } %>
	    		</td>
	    		<% } %>
	    			<% if(ConfigurationProperties.getInstance().isPropertyValueEqual(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "true")){ %>
	    			<td>
	    			<% if(showAccessionNumber ){ %>
		    			<bean:write name="workplanTests" property="nextVisitDate"/>
		    		<% } %>	
	    			</td>
	    		<% } %>
	    		<% if( !workplanType.equals("test") ){ %>
	    		<td>
		    		<logic:equal name="workplanTests" property="nonconforming" value="true">
						<img src="./images/nonconforming.gif" />
					</logic:equal>
				</td>
				<td>
					<%=workplanTests.getTestName() %> 
				</td>
				<% } %>
	    		<td style="text-align: center;">
	      			<bean:write name="workplanTests" property="receivedDate"/>
	    		</td>
	    		<!-- Added By trungtt.kd -->
	    		<td style="text-align: center;">
	      			<bean:write name="workplanTests" property="startedDate"/>
	    		</td>
	    		<td style="text-align: center;">
	      			<bean:write name="workplanTests" property="completedDate"/>
	    		</td>
	    		<td style="text-align: center;">
	      			<bean:write name="workplanTests" property="emergency"/>
	    		</td>
	    		<td style="text-align: center;">
	      			<bean:write name="workplanTests" property="projectName"/>
	    		</td>
      		</tr>
  	</logic:iterate>
	<tr>
	    <td colspan="8"><hr/></td>
    </tr>
	<tr>
		<td colspan="3">
      		<html:button property="print" styleId="print"  onclick="printWorkplan();"  styleClass="btn btn-default">
				<bean:message key="workplan.print"/>
			</html:button>
		</td>
	</tr>
</Table>
</logic:notEqual>

<% if( workplanType.equals("test") || workplanType.equals("panel") ){ %>
	<logic:equal name="testCount"  value="0">
		<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
	</logic:equal>
<% } else { %>
	<logic:equal name="testCount"  value="0">
		<logic:notEmpty name="<%=formName %>" property="testSectionId">
		<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
		</logic:notEmpty>
	</logic:equal>
<% } %>


