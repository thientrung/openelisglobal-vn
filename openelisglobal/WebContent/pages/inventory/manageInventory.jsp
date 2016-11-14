<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
				us.mn.state.health.lims.inventory.form.InventoryKitItem,
				us.mn.state.health.lims.common.util.DateUtil,
				us.mn.state.health.lims.common.util.IdValuePair,
				us.mn.state.health.lims.common.util.StringUtil,
				us.mn.state.health.lims.common.util.Versioning" %>
<%@ page import="java.util.List" %>


<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax" %>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="kitSources" name="<%=formName %>" property="sources" type="List<IdValuePair>" />
<bean:define id="kitTypeList" name="<%=formName %>" property="kitTypes" type="List<String>" />
<bean:size id="currentKitCount" name="<%=formName %>" property="inventoryItems"/>

<%
String basePath = "";
String path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript" language="JavaScript1.2">

var invalidElements = new Array();
var requiredFields = new Array();

var dirty = false;
var nextKitValue = 	<%= currentKitCount%> + 1;
var errorColor = "#ff0000";
var DatePattern = /^((((0?[1-9]|[12]\d|3[01])[\.\-\/](0?[13578]|1[02])[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|[12]\d|30)[\.\-\/](0?[13456789]|1[012])[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|1\d|2[0-8])[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]\d)?\d{2}))|(29[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))|(((0[1-9]|[12]\d|3[01])(0[13578]|1[02])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|[12]\d|30)(0[13456789]|1[012])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|1\d|2[0-8])02((1[6-9]|[2-9]\d)?\d{2}))|(2902((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00))))$/;

$jq(document).ready(function () {
	// Initialize "Save" button status
	setSave();
});

// Added by Mark 2016.07.04 09:55AM
// Validate a date entry
function checkValidEntryDate(date, dateRange, blankAllowed) {
	var isNumeric = true;
	if(date.value.indexOf("/") > 0 && date.value.length <= 6) {
		var dateSplit = date.value.split("/");
		var newDate = new Date(dateSplit[1] + "/" + dateSplit[0]);
		if(newDate != "Invalid Date") {
			var yyyy = new Date().getFullYear();
			var mm = (newDate.getMonth()+1).toString(); // getMonth() is zero-based
			var dd  = newDate.getDate().toString();
			date.value = (dd[1]?dd:"0"+dd[0]) + "/" + (mm[1]?mm:"0"+mm[0]) + "/" + yyyy;
		}
	}

 if((!date.value || date.value == "") && !blankAllowed) {
     isNumeric = false;
 } else if ((!date.value || date.value == "") && blankAllowed) {
 	 selectFieldErrorDisplay( true, date );
     setSampleFieldValidity( true, date.id );
     setSave();
     return;
 }
 
 if( !dateRange || dateRange == "") {
     dateRange = 'past';
 }

 // Check if date value is numeric
 try {
	 var dateSplit = date.value.split("/");
	 if (isNotaNumber(dateSplit[0]) || isNotaNumber(dateSplit[1]) || isNotaNumber(dateSplit[2]) || !isNumeric) {
	 	 selectFieldErrorDisplay( false, date );
	     setSampleFieldValidity( false, date.id );
         setSave();
	     return;
	     
	 } else {
	     //ajax call from utilites.js
	 	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	 }
 } catch(Exception) {
	 selectFieldErrorDisplay( false, date );
     setSampleFieldValidity( false, date.id );
     setSave();
     return;
 }
}

function  /*void*/ processValidateEntryDateSuccess(xhr) {
    //alert(xhr.responseText);
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

	var isValid = message == "<%=IActionConstants.VALID%>";

	//utilites.js
	selectFieldErrorDisplay( isValid, $(formField) );
	setSampleFieldValidity( isValid, formField );
	setSave();
	
	if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
		alert( "<bean:message key="error.date.inFuture"/>" );
	}else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
		alert( "<bean:message key="error.date.inPast"/>" );
	}
	
	if (!isValid) $(formField).focus();
}

function selectFieldErrorDisplay(ok, field) {
    if (ok) {
        clearFieldErrorDisplay(field);
    } else {
        setFieldErrorDisplay(field);
    }
}

function setFieldErrorDisplay(field) {
    if (field.className.search("error") == -1) {
        field.className += " error";
    }
}

function clearFieldErrorDisplay(field) {
	if (field)
		field.className = field.className.replace(/(?:^|\s)error(?!\S)/, '');
}

//disable or enable save button based on validity of fields
function setSave() {
	var validToSave = isSaveEnabled() && requiredFieldsValid();
	//disable or enable save button based on validity/visibility of fields
    if($("saveButtonId") != null) {
    	$("saveButtonId").disabled = !validToSave;
	}
}
 
function  /*bool*/ requiredFieldsValid(){
	return requiredFields.length == 0;
	/*var kits = $$("input.kitName");
	for(var i = 0; i < kits.length; i++) {
		if( kits[i].value == null || kits[i].value == "" ) {
			return false;
		}
	}
	var orgId = $$("input.organizationId");
	for(var i = 0; i < orgId.length; i++) {
		if( orgId[i].value == null || orgId[i].value == "" ) {
			return false;
		}
	}
	return true;*/
}

function isSaveEnabled()  {
    return invalidElements.length == 0;
}

function /*void*/ updateRequiredFields(field, index) {
	// For removeCheckbox
	if (field.id.startsWith("removeButton")) {
		// Delete kitName and source from requiredFields
		deleteFromRequiredFields($("kitName_" + index));
		//deleteFromRequiredFields($("organizationId_" + index));
		setSave();
		return;
	} 
	// For kitName & source
	if (field.id.startsWith("kitName")) {
		if (field.value == 0 || field.value == "") {
			addToRequiredFields(field);
		} else {
			deleteFromRequiredFields(field);
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

function setSampleFieldValidity( valid, fieldId ) {
	if( valid ) {
		setFieldValid(fieldId);
	} else {
		setFieldInvalid(fieldId);
	}
}

function setFieldInvalid(field) {
	if ( invalidElements.indexOf(field) == -1 ) {
		invalidElements.push(field);
	}
}

function setFieldValid(field) {
 	var removeIndex = invalidElements.indexOf( field );
	if ( removeIndex != -1 ) {
		for( var i = removeIndex + 1; i < invalidElements.length; i++ ) {
			invalidElements[i - 1] = invalidElements[i];
		}
		invalidElements.length--;
	}
}

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
// End of Modification

function validateDate( id ) {
	id.value = id.value.strip();

    if (((id.value.match(DatePattern)) && (id.value!=='')) || (id.value=='')) {
    	id.style.borderColor = "";
    	return true;
    } else {
    	id.style.borderColor  = errorColor;
    	return false;
    }
}

function checkIfEmpty( field ) {
	field.style.borderColor = field.value ? "" :errorColor;
	setSave();
}

function /*void*/ showInactiveKits( sendingButton ){
	sendingButton.hide();
	$('hideInactiveButton').show();
	$('inactiveInventoryItems').show();
}

function /*void*/ hideInactiveKits( sendingButton ){
    sendingButton.hide();
	$('showInactiveButton').show();
	$('inactiveInventoryItems').hide();
}

function /*void*/ deactivateTestKit( index ){
	$("activeRow_" + index).hide();
	$("inactiveRow_" + index).show();
	$("isActive_" + index ).value = false;
	setModifiedFlag(index);
}

function /*void*/ reactivateTestKit( index ){
	$("activeRow_" + index).show();
	$("inactiveRow_" + index).hide();
	$("isActive_" + index ).value = true;
	setModifiedFlag(index);
}

function /*void*/ setModifiedFlag( index ){
	$("isModified_" + index ).value = true;
	makeDirty();
}

function /*void*/ addNewTestKit(){
	var TEST_KIT_PROTOTYPE_ID = '<input name="inventoryLocationId" style="width:100%" id="inventoryLocationId_' + nextKitValue + '" size="3" value="" disabled="disabled" type="text">';
	var TEST_KIT_PROTOTYPE_NAME = '<input name="kitName" style="width:100%" id="kitName_' + nextKitValue + '" value="" type="text" class="kitName" onchange="updateRequiredFields(this,' + nextKitValue + ')" onblur="checkIfEmpty(this);">';
	var TEST_KIT_PROTOTYPE_TYPE = '<select name="type" style="width:100%" id="type_' + nextKitValue + '" class="kitType" > \
									<option value="" ></option> ' +
									'<%
										for( String kitTypeValue : kitTypeList ){
										out.print("<option value=\"" + kitTypeValue + "\">" + kitTypeValue +"</option>" );
										}
									%>'
									+ '</select>';									
	var TEST_KIT_PROTOTYPE_RECEIVE = '<input id="receiveDate_' + nextKitValue + '" name="receiveDate" class="receiveDate" style="width:100%" size="12" maxlength="10" value="" type="text" onkeyup="addDateSlashes(this, event);" onblur="checkValidEntryDate(this, \'past\', true);">';
	var TEST_KIT_PROTOTYPE_EXPIRE = '<input id="expirationDate_' + nextKitValue + '" name="expirationDate" class="expirationDate" style="width:100%" size="12" maxlength="10" value="" type="text" onkeyup="addDateSlashes(this, event);" onblur="checkValidEntryDate(this, \'past\', true);">';
	var TEST_KIT_PROTOTYPE_LOT = '<input id="lotNumber_' + nextKitValue + '" style="width:100%" name="lotNumber" class="lotNumber" size="6" value="" type="text">';
	var TEST_KIT_PROTOTYPE_SOURCE = '<select id="organizationId_' + nextKitValue + '" style="width:100%" name="organizationId" class="organizationId" onchange="updateRequiredFields(this,' + nextKitValue + ')" > \
									' +
									'<%
										for( IdValuePair pair : kitSources ){
										out.print("<option value=\"" + pair.getId() + "\">" + pair.getValue() +"</option>" );
										}
									%>'
									+ '</select>';
	var TEST_KIT_PROTOTYPE_REMOVE = '<input name="removeButton" id="removeButton_' + nextKitValue + '" style="width:100%" value="' + '<bean:message key="label.button.remove"/>' + '" onclick="updateRequiredFields(this, #); removeTestKit(#);" class="textButton" type="button">';

	var testKitTable = $("testKitTable");
	var rows = testKitTable.rows;
	var origionalRowCount = rows.length;
	var newRow = testKitTable.insertRow( origionalRowCount );
	//N.B. assigning innerHTML to a row does not work for IE
	newRow.insertCell(0).innerHTML = TEST_KIT_PROTOTYPE_ID;
	newRow.insertCell(1).innerHTML = TEST_KIT_PROTOTYPE_NAME;
	newRow.insertCell(2).innerHTML = TEST_KIT_PROTOTYPE_TYPE;
	newRow.insertCell(3).innerHTML = TEST_KIT_PROTOTYPE_RECEIVE;
	newRow.insertCell(4).innerHTML = TEST_KIT_PROTOTYPE_EXPIRE;
	newRow.insertCell(5).innerHTML = TEST_KIT_PROTOTYPE_LOT;
	newRow.insertCell(6).innerHTML = TEST_KIT_PROTOTYPE_SOURCE;
	newRow.insertCell(7).innerHTML = TEST_KIT_PROTOTYPE_REMOVE.replace(/\#/g, nextKitValue);

	newRow.id = "addedKit_" + nextKitValue;
	newRow.className = "addedKit";

	addToRequiredFields($("kitName_" + nextKitValue));
	//addToRequiredFields($("organizationId_" + nextKitValue));
	
	nextKitValue++;
	makeDirty();
	//alert( newRow.innerHTML );
}

function /*void*/ removeTestKit( index ){
	var testKitTable = $("testKitTable");
	var row = $("addedKit_" + index);
	testKitTable.deleteRow( row.rowIndex );
}


function /*void*/ createNewKitXML(){
	var xml = "<?xml version='1.0' encoding='utf-8'?><newKits>";
	var newKitRows = $$('tr.addedKit');
	var rowsLength = newKitRows.length;
    var i;

	for( var rowIndex = 0; rowIndex < rowsLength; ++rowIndex ){

		xml += "<kit ";

		var row = newKitRows[rowIndex];
		var cells = row.getElementsByTagName("input");
		var cellsLength = cells.length;

		for( i = 1; i < cellsLength; ++i ){ //the first cell is the kit id #

			xml += cells[i].className + "=\"";
			xml += cells[i].value;
			xml += "\" ";
		}

		cells = row.getElementsByTagName("select");
		cellsLength = cells.length;

		for( i = 0; i < cellsLength; ++i ){

			xml += cells[i].className + "=\"";
			xml += cells[i].value;
			xml += "\" ";
		}

		xml += " />";
	}

	xml += "</newKits>";

	$('newKits').value = xml;
}

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function  /*void*/ savePage()
{
	var receiveDates, expirationDates,i;
	var kits = $$("input.kitName");
		
	for(i = 0; i < kits.length; ++i) {
		if( kits[i].value == null || kits[i].value == "" ) {
			alert( "<bean:message key="inventory.add.error.noname"/>" );
			return;
		}
	}

	receiveDates = $$("input.receiveDate");

	for(i = 0; i < receiveDates.length; ++i) {
		if( !validateDate(receiveDates[i]) ){
			alert( "<bean:message key="inventory.add.error.receivedDate"/>" );
			return;
		}
	}
	
	expirationDates = $$("input.expirationDate");
	
	for(i = 0; i < expirationDates.length; ++i) {
		if( !validateDate(expirationDates[i]) ){
			alert( "<bean:message key="inventory.add.error.expirationDate"/>" );
			return;
		}
	}
	
	createNewKitXML();
  
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	var form = window.document.forms[0];
	form.action = "ManageInventoryUpdate.do";
	form.submit();
}

function /*void*/ makeDirty(){
	dirty=true;
	if( typeof(showSuccessMessage) != 'undefinded' ){
		showSuccessMessage(false); //refers to last save
	}
	// Adds warning when leaving page if content has been entered into makeDirty form fields
	function formWarning(){ 
    return "<bean:message key="banner.menu.dataLossWarning"/>";
	}
	window.onbeforeunload = formWarning;
}

</script>

<div id="PatientPage" class="colorFill" style="display:inline" >
	<logic:present name="<%=formName%>" property="inventoryItems" >
	<html:hidden name="<%=formName%>"  property="newKitsXML" styleId="newKits" />
	<table id="testKitTable" class="table">
	<tr >
		<th style="width:5%">
			<bean:message key="inventory.testKit.id"/>
		</th>
		<th style="width:15%">
			<bean:message key="inventory.testKit.name"/>
			<span class="requiredlabel">*</span>
		</th>
		<th style="width:10%">
  			<bean:message key="inventory.testKit.type"/>
		</th>
		<th style="width:10%">
			<bean:message key="inventory.testKit.receiveDate"/><br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span>
		</th>
		<th style="width:10%">
			<bean:message key="inventory.testKit.expiration"/><br><span style="font-size: xx-small; "><%=DateUtil.getDateUserPrompt()%></span>
		</th>
		<th style="width:10%">
			<bean:message key="inventory.testKit.lot"/>
		</th>
		<th style="width:20%">
			<bean:message key="inventory.testKit.source"/>
			<span class="requiredlabel">*</span>
		</th>
		<th style="width:10%">
		</th>
	</tr>
	<logic:iterate id="inventoryItems"  name="<%=formName%>" property="inventoryItems" indexId="index" type="InventoryKitItem" >
		<tr <% if(!inventoryItems.getIsActive()){out.print("style=\"display:none\"");} %> id='<%="activeRow_" + index %>' >
			<td >
				<html:hidden indexed="true" name="inventoryItems" property="isActive" styleId='<%= "isActive_" + index %>'/>
				<html:hidden indexed="true" name="inventoryItems" property="isModified" styleId='<%= "isModified_" + index %>'/>
				<html:text style="width:100%" indexed="true" name="inventoryItems" property="inventoryLocationId" styleId='<%= "inventoryLocationId_" + index %>' disabled="true" size="3" />
			</td>
			<td>
			<html:text  indexed="true"
						style="width:100%"
			            name="inventoryItems"
			            property="kitName"
			            styleId='<%= "kitName_" + index %>'
			            styleClass="kitName"
			            onchange='<%="setModifiedFlag(" + index + ");" %>'
			            onblur="checkIfEmpty(this);"/>
			</td>
			<td >
				<html:select indexed="true"
							 style="width:100%"
			            	 styleId='<%= "type_" + index %>'
							 name="inventoryItems"
							 property="type"  value='<%=inventoryItems.getType()%>'
							 onchange= '<%=" setModifiedFlag(" + index + ");" %>' >
				    <option value="" ></option>
					<html:options name="<%=formName%>" property="kitTypes" />
				</html:select>
			</td>
			<td >
				<html:text  indexed="true"
							style="width:100%"
							name="inventoryItems"
							property="receiveDate"
			            	styleId='<%= "receiveDate_" + index %>'
							styleClass="receiveDate"
							onkeyup="addDateSlashes(this, event);"
							maxlength="10"
							size="12"
							onchange='<%="setModifiedFlag(" + index + "); " %>'
							onblur="checkValidEntryDate(this, 'past', true);" />
			</td>
			<td >
				<html:text  indexed="true"
							style="width:100%"
				            name="inventoryItems"
				            property="expirationDate"
			            	styleId='<%= "expirationDate_" + index %>'
				            styleClass="expirationDate"
							onkeyup="addDateSlashes(this, event);"
							maxlength="10"
				            size="12"
				            onchange='<%="setModifiedFlag(" + index + ");" %>'
				            onblur="checkValidEntryDate(this, 'past', true);" />
			</td>
			<td >
				<html:text  indexed="true"
			            	styleId='<%= "lotNumber_" + index %>'
							style="width:100%"
				            name="inventoryItems"
				            property="lotNumber"
				            size="6"
				            onchange='<%="setModifiedFlag(" + index + ");" %>'/>
			</td>
			<td >
						<html:select indexed="true"
							 style="width:100%"
			            	 styleId='<%= "organizationId_" + index %>'
				             name="inventoryItems"
				             property="organizationId"
				             value='<%=inventoryItems.getOrganizationId()%>'
				             onchange='<%="setModifiedFlag(" + index + ");" %>'>

							<html:optionsCollection name="<%=formName%>" property="sources" value="id" label="value"/>
						</html:select>
			</td>
			<td >
                <input type="button"
					   style="width:100%"
                       value='<%= StringUtil.getMessageForKey("label.button.deactivate")%>'
                       onclick='<%= "deactivateTestKit(" + index + ");"%>'
                       class="textButton">
			</td>
		</tr>
	</logic:iterate>
	</table>
	<div id="inactiveInventoryItems" style="display:none" >
	<hr>

	<table class="table">
	<tr>
		<th colspan="8"  align="left"><bean:message key="invnetory.testKit.inactiveKits"/></th>
	</tr>
	<logic:iterate id="item"  name="<%=formName%>" property="inventoryItems" indexId="index" type="InventoryKitItem" >
		<tr <% if(item.getIsActive()){out.print("style=\"display:none\"");} %> id='<%="inactiveRow_" + index %>' >
			<td style="width:5%">
				<html:text name="item" style="width:100%" styleId='<%="inventoryLocationId_" + index %>' property="inventoryLocationId" disabled="true" size="3" />
			</td>
			<td>
				<html:text name="item" style="width:100%" styleId='<%="kitName_" + index %>' property="kitName" disabled="true" />
			</td>
			<td>
				<html:text name="item" style="width:100%" styleId='<%="type_" + index %>' property="type" disabled="true" size="12" />
			</td>
			<td>
				<html:text name="item" style="width:100%" styleId='<%="receiveDate_" + index %>' property="receiveDate" disabled="true" size="12" />
			</td>
			<td>
				<html:text name="item" style="width:100%" styleId='<%="expirationDate_" + index %>' property="expirationDate" disabled="true" size="12" />
			</td>
			<td style="width:10%">
				<html:text name="item" style="width:100%" styleId='<%="lotNumber_" + index %>' property="lotNumber" disabled="true" size="6" />
			</td>
			<td style="width:20%">
				<html:text name="item" style="width:100%" styleId='<%="source_" + index %>' property="source" disabled="true" />
			</td>
			<td style="width:10%">
                    <input type="button"
                     	   style="width:100%" 
                           value='<%= StringUtil.getMessageForKey("label.button.reactivate")%>'
                           onclick='<%= "reactivateTestKit(" + index + ");"%>'
                           class="textButton">
			</td>
		</tr>
	</logic:iterate>
	</table>
	</div>
	<br/>
        <input type="button"
                   value='<%= StringUtil.getMessageForKey("inventory.testKit.showAll")%>'
                   onclick="showInactiveKits( this );"
                   class="textButton"
                   id="showInactiveButton">
        <input type="button"
               value='<%= StringUtil.getMessageForKey("inventory.testKit.hideInactive")%>'
               onclick="hideInactiveKits( this );"
               class="textButton"
               style="display:none"
               id="hideInactiveButton">
        <input type="button"
               value='<%= StringUtil.getMessageForKey("inventory.testKit.add")%>'
               onclick="addNewTestKit();"
               class="textButton">
</logic:present>
<logic:notPresent name="<%=formName%>" property="inventoryItems" >
	<bean:message key="inventory.testKit.none"/>
</logic:notPresent>


</div>
