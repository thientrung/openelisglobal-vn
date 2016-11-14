<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
			us.mn.state.health.lims.common.formfields.FormFields,
			us.mn.state.health.lims.common.util.*,
            us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
	        us.mn.state.health.lims.sample.util.AccessionNumberUtil,
	        us.mn.state.health.lims.sample.bean.SampleEditItem"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>
<%@ taglib uri="/tags/globalOpenELIS" prefix="global"%>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="idSeparator"
	value='<%=SystemConfiguration.getInstance().getDefaultIdSeparator()%>' />
<bean:define id="accessionFormat"
	value='<%=ConfigurationProperties.getInstance().getPropertyValue(
					Property.AccessionFormat)%>' />
<bean:define id="genericDomain" value='' />
<bean:define id="accessionNumber" name="<%=formName%>"
	property="accessionNumber" />
<bean:define id="newAccessionNumber" name="<%=formName%>"
	property="newAccessionNumber" />
<bean:define id="cancelableResults" name="<%=formName%>"
	property="ableToCancelResults" type="java.lang.Boolean" />
<bean:define id="isEditable" name="<%=formName%>" property="isEditable"
	type="java.lang.Boolean" />
<bean:define id="maxLabels" value='<%= SystemConfiguration.getInstance().getMaxNumberOfLabels() %>' />

<%!
	String basePath = "";
	String currentSampleItemId = "";
	int editableAccession = 0;
	int nonEditableAccession = 0;
	int maxAccessionLength = 0;
	boolean useCollectionDate = true;
	boolean useSTNumber = false;
	boolean useMothersName = false;
	boolean patientRequired = false;
	boolean supportExternalID = false;
	boolean useModalSampleEntry = false;
	boolean useRejectionInModalSampleEntry = false;
	boolean useSpecimenLabels = false;%>
<%
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName()
			+ ":" + request.getServerPort() + path + "/";
	editableAccession = AccessionNumberUtil.getChangeableLength();
	nonEditableAccession = AccessionNumberUtil.getInvarientLength();
	maxAccessionLength = editableAccession + nonEditableAccession;
	useCollectionDate = FormFields.getInstance().useField(
			FormFields.Field.CollectionDate);
	useSTNumber = FormFields.getInstance().useField(
			FormFields.Field.StNumber);
	useMothersName = FormFields.getInstance().useField(
			FormFields.Field.MothersName);
	patientRequired = FormFields.getInstance().useField(
			FormFields.Field.PatientRequired);
	supportExternalID = FormFields.getInstance().useField(
			FormFields.Field.EXTERNAL_ID);
	useModalSampleEntry = FormFields.getInstance().useField(
			FormFields.Field.SAMPLE_ENTRY_MODAL_VERSION);
	useRejectionInModalSampleEntry = FormFields.getInstance().useField(
			FormFields.Field.SAMPLE_ENTRY_REJECTION_IN_MODAL_VERSION);
	useSpecimenLabels = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.USE_SPECIMEN_LABELS, "true");
%>

<script
	src="scripts/ui/jquery.ui.core.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/ui/jquery.ui.widget.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/ui/jquery.ui.button.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/ui/jquery.ui.menu.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/ui/jquery.ui.position.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/ui/jquery.ui.autocomplete.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script
	src="scripts/customAutocomplete.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="<%=basePath%>scripts/utilities.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="<%=basePath%>scripts/ajaxCalls.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="<%=basePath%>scripts/jquery.selectlist.dev.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<link rel="stylesheet"
	href="css/jquery_ui/jquery.ui.all.css?ver=<%=Versioning.getBuildNumber()%>">
<link rel="stylesheet"
	href="css/customAutocomplete.css?ver=<%=Versioning.getBuildNumber()%>">

<script type="text/javascript">

var requiredFields = new Array("submitterNumber");

var checkedCount = 0;
var currentSampleType;
var sampleIdStart = 0;
var orderChanged = false;
var invalidSampleElements = [];
var useSampleRejection = <%=useRejectionInModalSampleEntry%>;
var usePatientInfoModal = false;

//This handles the case where sampleAdd.jsp tile is not used.  Will be overridden in sampleAdd.jsp
function samplesHaveBeenAdded(){ return false;}

$jq(document).ready( function() {
    if( !<%=isEditable%>) {
        $jq(":input").prop("readOnly", true);
        $jq(".patientSearch").prop("readOnly", false);
    }
});

$jq(function() {
   	var maxAccessionNumber = $("maxAccessionNumber").value;
	var lastDash = maxAccessionNumber.split('-')[1];
   	sampleIdStart = parseInt(lastDash) + 1;

   	if (<%=useModalSampleEntry%>) {
		$jq("#addSampleButton").attr("disabled", false);
	}
});

// Added by Mark 2016.06.24 04:04PM
// Set focus on onsetOfDate onPageLoad
function prePageOnLoad() {
	var onsetDate = $("onsetOfDate");
 	onsetDate.focus();
 	moveCursorToEnd(onsetDate);

 	setSave();
}

// Set cursor to the end of string input
function moveCursorToEnd(field) {
	if (typeof field.selectionStart == "number") {
 		field.selectionStart = field.selectionEnd = field.value.length;
 	} else if (typeof field.createTextRange != "undefined") {
 		field.focus();
     	var range = field.createTextRange();
     	range.collapse(false);
     	range.select();
	}
}
//End of Modification

function setSampleFieldValidity( valid, fieldName ){

    if( valid )
    {
        setSampleFieldValid(fieldName);
    }
    else
    {
        setSampleFieldInvalid(fieldName);
    }
}

function setSampleFieldInvalid(field)
{
    if( invalidSampleElements.indexOf(field) == -1 )
    {
        invalidSampleElements.push(field);
    }
}

function setSampleFieldValid(field)
{
    var removeIndex = invalidSampleElements.indexOf( field );
    if( removeIndex != -1 )
    {
        invalidSampleElements.splice( removeIndex,1);
    }
}

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function /*void*/ addRemoveRequest( checkbox ){
	checkedCount = Math.max(checkedCount + (checkbox.checked ? 1 : -1), 0 );

	if( typeof(showSuccessMessage) != 'undefinded' ){
		showSuccessMessage(false); //refers to last save
	}

	setSaveButton();

}


// Adds warning when leaving page if tests are checked
function formWarning(){
	var newAccession = $("newAccessionNumber").value;
	var accessionChanged = newAccession.length > 1 && newAccession != "<%=accessionNumber%>"; 

  	if ( checkedCount > 0 || accessionChanged || samplesHaveBeenAdded()) {
    	return "<bean:message key="banner.menu.dataLossWarning"/>";
	}
}
window.onbeforeunload = formWarning;

function /*void*/ savePage(){
	if( samplesHaveBeenAdded() && !sampleAddValid( false )){
		alert('<%=StringUtil
					.getMessageForKey("warning.sample.missing.test")%>');
		return;
	}


    if( $jq(".testWithResult:checked").size() > 0 &&
        !confirm("<%=StringUtil.getMessageForKey("test.modify.save.warning")%>") ) {
            return;
    }
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	
	<%if (!useModalSampleEntry) {%>
	loadSamples(); //in addSample tile
	<%} else {%>
	loadXml(); //in sampleAddModal tile

	// Clear any forced placeholder values before form submission
	$jq('input[placeholder]').each(function() {
		var input = $jq(this);
		if (input.val() == input.attr('placeholder')) {
			input.val('');
		}
	});
	<%}%>

	<%if (FormFields.getInstance().useField(
					FormFields.Field.SAMPLE_ENTRY_REQUESTER_WORK_PHONE_AND_EXT)) {%>
	// Merge requester work phone and extension (space separated) before submission
	if ($('providerWorkPhoneExt').value.length > 0)
		$('providerWorkPhoneID').value = $('providerWorkPhoneID').value + " " + $('providerWorkPhoneExt').value;
	<%}%>

	var form = document.forms[0];
	if ($jq("#searchCriteria").val() != "") {
		form.action = "SampleEditUpdate.do?criteria="+$jq("#searchCriteria").val();
	} else {
	    form.action = "SampleEditUpdate.do";
	}
	form.submit();
}

function checkEditedAccessionNumber(changeElement){
	var accessionNumber;
	clearFieldErrorDisplay( changeElement );

	$("newAccessionNumber").value = "";
	
	if( changeElement.value.length == 0){
		updateSampleItemNumbers( "<%=accessionNumber%>" );
		setSaveButton();
		return;
	}
	
	if( changeElement.value.length != <%=editableAccession%>){
		setFieldErrorDisplay( changeElement );
		setSaveButton();
		alert("<%=StringUtil
					.getMessageForKey("sample.entry.invalid.accession.number.length")%>");
		return;
	}
	
	accessionNumber = "<%=((String) accessionNumber).substring(0,
					nonEditableAccession)%>" + changeElement.value;
	
	if( accessionNumber == "<%=accessionNumber%>"){
		updateSampleItemNumbers( accessionNumber );
		setSaveButton();
		return;
	}
	
	validateAccessionNumberOnServer(true, false, changeElement.id, accessionNumber, processEditAccessionSuccess, null);
}

function processEditAccessionSuccess(xhr)
{
	//alert( xhr.responseText );
	var accessionNumberUpdate;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;

	if (message == "SAMPLE_FOUND"){
		setFieldErrorDisplay( $(formField) );
		setSaveButton();
		alert('<%=StringUtil
					.getMessageForKey("errors.may_not_reuse_accession_number")%>');
		return;
	}
	
	if( message == "SAMPLE_NOT_FOUND"){
		accessionNumberUpdate = "<%=((String) accessionNumber).substring(0,
					nonEditableAccession)%>" + $(formField).value;
		updateSampleItemNumbers( accessionNumberUpdate );
		$("newAccessionNumber").value = accessionNumberUpdate;
		setSaveButton();
		return;
	}
	
	setFieldErrorDisplay( $(formField) );
	setSaveButton();
	alert(message);
}

function updateSampleItemNumbers(newAccessionNumber){
		var i, itemNumbers, currentValue, lastDash = 0;
		itemNumbers = $$('span.itemNumber');
		
		for( i = 0; i < itemNumbers.length; i++){
			if(itemNumbers[i].firstChild != undefined){
				currentValue = itemNumbers[i].firstChild.nodeValue;
				lastDash = currentValue.lastIndexOf('-');
				itemNumbers[i].firstChild.nodeValue = newAccessionNumber + currentValue.substring(lastDash);
			}
		}
}

function checkChangeExternalId (externalId) {
	$jq("#sampleItemChanged_" + externalId.id.split("_")[1]).val(true);
}

function /*void*/ calculateDayDifference(date) {
	var onsetDateField = $("onsetOfDate");
	var onSetDate = null;
	if (onsetDateField != null && onsetDateField.value) {
		var onSetDateSplit = onsetDateField.value.split("/");
		onSetDate = new Date(onSetDateSplit[1] + "/" + onSetDateSplit[0] + "/" + onSetDateSplit[2]);
	}

	if (date == null) {
		$jq("input[id^='collectionDate_']").each(function(){
			displayDayDifference(this, onSetDate, onsetDateField);
	    });
	} else {
		displayDayDifference(date, onSetDate, date);
	}
}

function /*void*/ displayDayDifference(collectionDateField, onSetDate, modifiedField) {
	if ( (collectionDateField != null && collectionDateField.value) && 
			(onSetDate != null && onSetDate) && modifiedField != null ) {
		var rowNum = collectionDateField.id.split("_")[1];
		var collDateSplit = collectionDateField.value.split("/");
		var collectionDate = new Date(collDateSplit[1] + "/" + collDateSplit[0] + "/" + collDateSplit[2]);
		
		if (collectionDate.getTime() >= onSetDate.getTime()) {
			var timeDiff = Math.abs(collectionDate.getTime() - onSetDate.getTime());
			var diffDays = Math.ceil(timeDiff / (	1000 * 3600 * 24)); 
			$("dayDifference_" + rowNum).value = diffDays + 1;
			var onsetDateField = $("onsetOfDate");
			selectFieldErrorDisplay(true, onsetDateField);
			selectFieldErrorDisplay(true, collectionDateField);
	        setSampleFieldValidity(true, onsetDateField.id);
	        setSampleFieldValidity(true, collectionDateField.id);
	        setSaveButton();
		} else {
			$("dayDifference_" + rowNum).value = "";
	        alert( '<bean:message key="day.difference.error.message"/>' );
			selectFieldErrorDisplay(false, modifiedField);
	        setSampleFieldValidity(false, modifiedField.id);
	        setSaveButton();
		}
	}	
}

function checkValidEntryDate(date, dateRange, blankAllowed) {
	var isNumeric = true;
    $jq("#sampleItemChanged_" + date.id.split("_")[1]).val(true);
    if(date.value.indexOf("/") > 0 && date.value.length <= 6){
        var dateSplit = date.value.split("/");
        var newDate = new Date(dateSplit[1] + "/" + dateSplit[0]);
        if(newDate != "Invalid Date"){
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
        setSaveButton();
        return;
    }
    
    if( !dateRange || dateRange == ""){
        dateRange = 'past';
    }
    
    // Added by Mark 2016.06.29 11:31AM
    // Check if date value is numeric
    try {
	    var dateSplit = date.value.split("/");
	    if (isNotaNumber(dateSplit[0]) || isNotaNumber(dateSplit[1]) || isNotaNumber(dateSplit[2])  || !isNumeric) {
	    	selectFieldErrorDisplay( false, $(date.id));
	        setSampleFieldValidity( false, date.id );
	        setSaveButton();
	        return;
	        
	    } else {
	        //ajax call from utilites.js
	    	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	    }
    }catch(Exception){
    	selectFieldErrorDisplay( false, $(date.id));
        setSampleFieldValidity( false, date.id );
        setSaveButton();
        return;
    }
    // End of Modification
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

function  /*void*/ processValidateEntryDateSuccess(xhr){

    //alert(xhr.responseText);
    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

    var isValid = message == "<%=IActionConstants.VALID%>";

    //utilities.js
    selectFieldErrorDisplay( isValid, $(formField));
    setSaveButton();

    if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
        alert( '<bean:message key="error.date.inFuture"/>' );
    }else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
        alert( '<bean:message key="error.date.inPast"/>' );
    }

	var colldateChecker = ($(formField).id).indexOf('_');
    if ($(formField).id == "onsetOfDate") {
    	calculateDayDifference(null);
    } else if (colldateChecker > -1) {
    	var collDatePrefix = ($(formField).id).split('_')[0];
    	if (collDatePrefix == "collectionDate") {
        	calculateDayDifference($(formField));
    	}
    }
}

function checkValidTime(time, blankAllowed)
{
    var lowRangeRegEx = new RegExp("^[0-1]{0,1}\\d:[0-5]\\d$");
    var highRangeRegEx = new RegExp("^2[0-3]:[0-5]\\d$");

    $jq("#sampleItemChanged_" + time.id.split("_")[1]).val(true);
    if (time.value.blank() && blankAllowed == true) {
        clearFieldErrorDisplay(time);
        setSaveButton();
        return;
    }

    if( lowRangeRegEx.test(time.value) ||
            highRangeRegEx.test(time.value) )
    {
        if( time.value.length == 4 )
        {
            time.value = "0" + time.value;
        }
        clearFieldErrorDisplay(time);
    }
    else
    {
        setFieldErrorDisplay(time);
 //       setSampleFieldInvalid(time.name);
    }

    setSaveButton();
}

//all methods here either overwrite methods in tiles or all called after they are loaded
var dirty=false;
function makeDirty(){
    dirty=true;
    if( typeof(showSuccessMessage) != 'undefined' ){
        showSuccessMessage(false); //refers to last save
    }
    // Adds warning when leaving page if content has been entered into makeDirty form fields
    function formWarning(){
        return "<bean:message key="banner.menu.dataLossWarning"/>";
    }
    window.onbeforeunload = formWarning;
}

function setupRePrintLabelsModal() {
    $('reMasterLabels').value = '3';
    <% if (useSpecimenLabels) { %>
    $('reItemLabels').value = $("samplesAddedTable").rows.length - 1;
    <% } %>
    setSave();
}
</script>

<hr />

<logic:equal name="<%=formName%>" property="noSampleFound" value="false">
	<DIV id="patientInfo" class='textcontent'>
		<bean:message key="sample.entry.patient" />
		:&nbsp;
		<bean:write name="<%=formName%>" property="patientName" />
		&nbsp;
		<bean:write name="<%=formName%>" property="dob" />
		&nbsp;
		<bean:write name="<%=formName%>" property="gender" />
		&nbsp;
		<bean:write name="<%=formName%>" property="nationalId" />
	</DIV>
	<hr />
	<br />
	<html:hidden name="<%=formName%>" property="accessionNumber" />
	<html:hidden name="<%=formName%>" property="newAccessionNumber"
		styleId="newAccessionNumber" />
	<html:hidden name="<%=formName%>" property="isEditable" />
	<html:hidden name="<%=formName%>" property="maxAccessionNumber"
		styleId="maxAccessionNumber" />
	<logic:equal name='<%=formName%>' property="isEditable" value="true">
		<h1><%=StringUtil
							.getContextualMessageForKey("banner.menu.sampleEdit")%></h1>
		<div id="accessionEditDiv" class="TableMatch" style="background-color:inherit;">
		  <div style="width: 15%; float: left; padding-top: 5px; text-align: center;">
			<b><%=StringUtil
							.getContextualMessageForKey("batchresultsentry.browse.accessionNumber")%>:</b>
			<bean:write name="<%=formName%>" property="accessionNumber" />
		  </div>
		  <div style="width: 20%; float: left;">
			<logic:equal
                value="false" name='<%=formName%>'
                property="sampleOrderItems.readOnly">
                <button data-toggle="modal" id="rePrintMasterLabels"
                    class="btn btn-default btn-block" name="printMasterLabels" type="button"
                    onclick="setupRePrintLabelsModal();if($('samplesSectionId'))showSection($('samplesSectionId'), 'samplesDisplay');$jq('#re-label-modal').modal('show');">
                    <bean:message key="label.button.rePrintMasterLabels" />
                </button>
            </logic:equal>
          </div>
			<br />
			<br />
			<hr />
		</div>
	</logic:equal>
	<bean:define id="confirmationSample" name='<%=formName%>'
		property="isConfirmationSample" type="java.lang.Boolean" />
	<div id="sampleOrder" class="colorFill">
		<%
			if (confirmationSample) {
		%>
		<tiles:insert attribute="sampleConfirmationOrder" />
		<%
			} else {
		%>
		<tiles:insert attribute="sampleOrder" />
		<%
			}
		%>
	</div>

	<logic:equal name='<%=formName%>' property="isEditable" value="true">
		<h1><%=StringUtil.getContextualMessageForKey("sample.edit.tests")%></h1>
	</logic:equal>
	<table style="width: 100%; margin: 0px auto;" id="sampleItem">
		<%-- width:<%=useModalSampleEntry ? "80" : "60"%>% --%>
		<caption>
			<div style="margin-bottom: 10px;">
				<bean:message key="sample.edit.existing.tests" />
			</div>
			<span style="color: red"><small><small><%=cancelableResults ? StringUtil
						.getMessageForKey("test.modify.static.warning") : ""%></small></small></span>
		</caption>
		<tr>
			<th><%=StringUtil
						.getContextualMessageForKey("quick.entry.accession.number")%></th>
			<th><bean:message key="sample.entry.sample.type" /></th>
			<th><bean:message key="sample.entry.sample.order" /></th>
			<%
				if (useCollectionDate) {
			%>
			<th><bean:message key="sample.collectionDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>
			</th>
			<th><bean:message key="sample.entry.sample.daydifference"/></th>
			<th><bean:message key="sample.collectionTime" /></th>
			<%
				}
			%>
			
			<logic:equal name='<%=formName%>' property="isEditable" value="true">
				<th style="width: 16px"><bean:message
						key="sample.edit.remove.sample" /></th>
			</logic:equal>
			<th><bean:message key="test.testName" /></th>
			<th style="width:16px; padding-left:5px; padding-right:5px;"><bean:message key="test.has.result" /></th>
			<logic:equal name='<%=formName%>' property="isEditable" value="true">
				<th style="width:16px; padding-left:5px; padding-right:5px;"><bean:message
						key="sample.edit.remove.tests" /></th>
			</logic:equal>
			<logic:equal name='<%=formName%>' property="isEditable" value="false">
				<th><bean:message key="analysis.status" /></th>
			</logic:equal>

		</tr>
		<logic:iterate id="existingTests" name="<%=formName%>"
			property="existingTests" indexId="index"
			type="us.mn.state.health.lims.sample.bean.SampleEditItem">
			<% currentSampleItemId = existingTests.getSampleItemId();%>
			<html:hidden property="sampleItemChanged" name="existingTests"
				styleId='<%="sampleItemChanged_" + index%>'
				styleClass="sampleItemChanged" indexed="true" />
			<logic:notEmpty name="existingTests" property="accessionNumber">
			<tr>
				<td class="centerTable"><html:hidden name="existingTests"
						property="analysisId" indexed="true" /> <span class="itemNumber"><bean:write
							name="existingTests" property="accessionNumber" /></span></td>
				<td class="centerTable"><bean:write name="existingTests"
						property="sampleType" /></td>
				<td class="centerTable">
					<%
						if (existingTests.getAccessionNumber() != null || index == 0) {
					%> 
					<app:text
						name='existingTests' property='sampleItemExternalId'
						onblur="makeDirty();"
						onchange="checkChangeExternalId(this);"
						onkeypress='return event.charCode >= 48 && event.charCode <= 57'
						styleId='<%="sampleItemExternalId_" + index%>' indexed="true"
						styleClass="text input-mini"
						style="margin:auto;float:inherit;width=60px;" /></td>
					<%
					 	}
					%>
				<%
					if (useCollectionDate) {
				%>
				<td class="centerTable">
					<%
						if (existingTests.getCollectionDate() != null) {
					%> <html:text
						name='existingTests' property='collectionDate' maxlength='10'
						size='12' onkeyup="addDateSlashes(this, event);"
						onblur="checkValidEntryDate(this, 'past', true); makeDirty();"
						styleId='<%="collectionDate_" + index%>' indexed="true"
						styleClass="text input-small"
						style=" margin:auto; width: 100px;float: inherit;" />
					<%-- <%=isEditable ==true ? "" : " readOnly"%> --%> <%
				 	}
				 %>
				</td>
				<!-- Added by markaae.fr 2016-10-18 02:50PM (Day Difference between collection_date and onset_date) -->
		      	<td class="centerTable">
		      		<% if( existingTests.getCollectionDate() != null ){%>
						<app:text name="<%=formName%>" 
							  property="sampleItemDayDifference" 
			  				  onchange="makeDirty();"
							  styleClass="text input-mini" 
			  				  styleId='<%="dayDifference_" + index%>'
			  				  style=" margin:auto; float:inherit;"
			  				  disabled='true'
			  				   />
				  	<%	}	%>
				</td>
				<td class="centerTable">
					<%
						if (existingTests.getCollectionDate() != null) {
					%> <html:text
						name='existingTests' property='collectionTime' maxlength='10'
						size='12' onkeyup='filterTimeKeys(this, event);'
						onblur='checkValidTime(this, true);'
						styleId='<%="collectionTime_" + index%>'
						styleClass='text input-mini' style="float: inherit; margin:auto;"
						indexed="true" /> <%
					 	}
					 %>
				</td>
				<%
					}
				%>
				<logic:equal name='<%=formName%>' property="isEditable" value="true">
					<td class="centerTable">
						<%
							if (existingTests.getAccessionNumber() != null) {
											if (existingTests.isCanRemoveSample()) {
						%> <html:checkbox
								name='existingTests' property='removeSample' indexed='true'
								onchange="addRemoveRequest(this);" /> <%
						 	} else {
						 %> <html:checkbox
								name='existingTests' property='removeSample' indexed='true'
								disabled="true" /> <%
						 	}
	 				}
				 %>
					</td>
				</logic:equal>

				<!-- Modified by markaae.fr	2016/10/26 02:19PM -->
				<td colspan="3">
				<%	if ( existingTests.getAccessionNumber() != null ) { %>
					<div class="display-tests-main test-container hide" style="display:block; min-height:0px; max-height:110px;">
						<table>
							<logic:iterate id="testNameList" name="<%=formName%>"
										   property="testNameList" indexId="index"
										   type="us.mn.state.health.lims.sample.bean.SampleEditItem">
								<logic:equal name="testNameList" property="sampleItemId" value="<%=currentSampleItemId%>">
								<tr>
									<td class="centerTable" width="350px;">
										<bean:write name="testNameList" property="testName" />
									</td>
									<td class="centerTable" style="padding-left:50px;"><%=testNameList.isHasResults() ? "X" : ""%>
									</td>
									<logic:equal name='<%=formName%>' property="isEditable" value="true">
										<td class="centerTable" style="padding-left:50px;">
											<%
												if (testNameList.isCanCancel()) {
											%> <input type="checkbox"
											name='<%="testNameList[" + index + "].canceled"%>' value="on"
											onchange="addRemoveRequest(this);"
													<%=testNameList.isHasResults()
														? "class='testWithResult'"
														: ""%>>
											<%
												} else {
											%> <html:checkbox name='testNameList' property='canceled'
												indexed='true' disabled="true" /> <%
											 	}
											 %>
										</td>
									</logic:equal>
									<logic:equal name='<%=formName%>' property="isEditable"
										value="false">
										<td class="centerTable"><bean:write name='testNameList'
												property="status" /></td>
									</logic:equal>
								</tr>
								</logic:equal>
							</logic:iterate>
						</table>
					</div>
				<%	} %>
				</td>
				<!-- End of Modification 2016/10/26 02:19PM -->
			</tr>
			</logic:notEmpty>
		</logic:iterate>
	</table>
	<hr />
	<br />
	<logic:equal name='<%=formName%>' property="isEditable" value="true">
		<table id="availableTestTable" style="width:100%">
			<caption>
				<bean:message key="sample.edit.available.tests" />
			</caption>
			<tr>
				<th style="width: 20%"><%=StringUtil.getContextualMessageForKey("quick.entry.accession.number")%></th>
				<th style="width: 20%"><bean:message key="sample.entry.sample.type" /></th>
				<th style="width: 60%"><bean:message key="sample.entry.assignAvailableTests" /></th>
				<%-- <th><bean:message key="test.testName" /></th> --%>
			</tr>
			<logic:iterate id="possibleTests" name="<%=formName%>"
				property="possibleTests" indexId="index" type="SampleEditItem">
				<logic:notEmpty name="possibleTests" property="sampleType">
				<tr id="<bean:write name="possibleTests"	property="sampleItemId" />">
					<td class="centerTable">
						<html:hidden name="possibleTests" property="testId" indexed="true" />
						<html:hidden name="possibleTests" property="sampleItemId" indexed="true" />
						<span class="itemNumber">
							<bean:write name="possibleTests" property="accessionNumber" />
						</span>
					</td>
					<td class="centerTable">
						<bean:write name="possibleTests" property="sampleType" />
					</td>
					
					<td class="centerTable">
					<table style="width:100%;">
						<tr>
							<td id="assignBtnContainer_<bean:write name="possibleTests" property="sampleItemId" />" style="width:100%; text-align:center;">
								<button data-toggle="modal"
									class="btn btn-default"
									style="width: 200px;"
									onclick='loadPanelsAndTestsAvailableForSampleType("<bean:write name="possibleTests" property="sampleTypeId"/>",
									<bean:write name="possibleTests" property="sampleItemId" />)'
									id="assign_tmpl"
									class="btn btn-default">
									<bean:message key="sample.entry.assignAvailableTests"/>
								</button>
							</td>
							<td style="width:50%; text-align:left;">
								<input id="testIds_<bean:write name="possibleTests"	property="sampleItemId" />" type="hidden">
								<input id="externalAnalysisIds_<bean:write name="possibleTests"	property="sampleItemId" />" type="hidden">
								<input id="panelIds_<bean:write name="possibleTests" property="sampleItemId" />" type="hidden">
								<div id="tests_<bean:write name="possibleTests" property="sampleItemId" />"
									style="margin:0 auto; max-width:500px; min-height:0px;"
									class="display-tests-main test-container hide"></div>
							</td>
						</tr>
					</table>
					</td>
					<%-- <td class="centerTable"><html:checkbox name="possibleTests"
							property="add" indexed="true" onchange="addRemoveRequest(this);" />
					</td>
					<td class="centerTable">&nbsp; <bean:write
							name="possibleTests" property="testName" />
					</td> --%>
				</tr>
				</logic:notEmpty>
			</logic:iterate>
			<logic:iterate id="possibleTests" name="<%=formName%>"
				property="possibleTests" indexId="index" type="SampleEditItem">
				<input type="hidden" class='possibleTest' sample-item='<bean:write name="possibleTests" property="sampleItemId" />'
				test-id='<bean:write name="possibleTests" property="testId" />'
				test-name='<bean:write name="possibleTests" property="testName" />' />
			</logic:iterate>
		</table>

		<hr>
		<h1>
			<bean:message key="sample.entry.addSample" />
		</h1>

		<div id="samplesDisplay" class="colorFill">
			<%
				if (useModalSampleEntry) {
			%>
			<div id="sampleAddModal">
				<tiles:insert attribute="addSampleModal" />
			</div>
			<%
				} else {
			%>
			<tiles:insert attribute="addSample" />
			<br />
			<%
				}
			%>
		</div>
	</logic:equal>
</logic:equal>
<logic:equal name="<%=formName%>" property="noSampleFound" value="true">
	<bean:message key="sample.edit.sample.notFound" />
</logic:equal>
<!-- Label Printing Modal -->
<div id="re-label-modal" class="sample-modal modal hide fade" style="position: fixed;">
    <div class="modal-header">
        <button type="button"
                class="close"
                onclick="cancelPrint2()"
                data-dismiss="modal">&times;</button>
        <h3 style="padding-left: 5px;"><bean:message key="sample.label.print.title" /></h3>
    </div>
    <div class="modal-body">
        <tiles:insert attribute="rePrintLabelsModal"/>
    </div>
    <div class="modal-footer">
        <button id="rePrintButton"
            class="btn btn-primary btn-large"
            disabled="disabled"
            data-dismiss="modal"
            onclick="submitRePrintJob();"
        ><bean:message key="sample.label.print.button"/></button>
        <button class="btn btn-small"
                onclick="cancelPrint2()"
                data-dismiss="modal"
                id="cancel-printLabels"><bean:message key="label.button.cancel" /></button>
    </div>
</div>
<script type="text/javascript">
function submitRePrintJob() {
    new Ajax.Request (
        'ajaxReportsXML',  //url
        {//options
            method: 'get', //http method
            parameters: 'provider=SampleLabelPrintProvider&labelAccessionNumber=' + <%= request.getParameter("accessionNumber") %> +
                        '&printerName=' + escape($F('rePrinterName')) + 
                        '&masterLabels=' + $F('masterLabels') + 
                        '&itemLabels=' + ($('itemLabels') ? $F('itemLabels') : ''),      //request parameters
            //indicator: 'throbbing'
            onSuccess:  processPrintSuccess,
            //onFailure:  processFailure
        }
    );
}

function processPrintSuccess(xhr) {
    var message = xhr.responseXML.getElementsByTagName("message")[0];
    //alert("I am in parseMessage and this is message, formfield " + message + " " + formfield);
    var msg = message.firstChild.nodeValue;
    var alertMsg = '';
    if (msg.substring(0, 7) == "success") {
        alertMsg = "<bean:message key="print.success"/>";
        zplData = msg;
        showZplModal();
        if ($jq("#labelSimulation").hasClass("hide")) $jq("#labelSimulation").removeClass("hide");
    } else {
        if (!$jq("#labelSimulation").hasClass("hide")) $jq("#labelSimulation").addClass("hide");
        switch (msg) {
            case "failMaxLabels":
                alertMsg = '<bean:message key="errors.labelprint.exceeded.maxnumber" arg0="<%=maxLabels%>"/>';
                break;
            case "failPrinter":
                alertMsg = '<bean:message key="errors.labelprint.no.printer"/>';
                break;
            case "fail":
                alertMsg = '<bean:message key="errors.labelprint.general.error"/>';
                break;
        }
    }
    $jq("#successMsg").text(alertMsg);
    showSuccessMessage(true);
}

function showZplModal() {
    if (zplData.length && zplData != "null") {
        var segs = zplData.split(";;");
        var masterHtml = '';
        var masterZpl = '';
        var itemHtml = '';
        var itemZpl = '';
        for (var i = 1; i < segs.length; i += 2) {
            var masterSeg = segs[i];
        
            if (masterSeg.length) {
                var masterBits = pullDataFromZpl(masterSeg);
                if (!$jq.isEmptyObject(masterBits)) {
                    masterHtml += '<div id="masterBarcode' + i + '"></div><div id="masterText' + i + '"></div>';
                }
                masterZpl += masterSeg + "<br/>";
            }
            if (segs[i + 1].length) {
                var itemSegs = segs[i + 1].split("::");
                for (var j = 0; j < itemSegs.length; j++) {
                    var itemBits = pullDataFromZpl(itemSegs[j]);
                    if (!$jq.isEmptyObject(itemBits)) {
                        itemHtml += '<div id="itemBarcode' + i + '-' + j + '"></div><div id="itemText' + i + '-' + j + '"></div>';
                    }
                    itemZpl += itemSegs[j] + "<br/>";
                }
            }
        }
        $jq("#masterStuff").empty().html(masterHtml);
        $jq("#itemStuff").empty().html(itemHtml);
        for (var i = 1; i < segs.length; i += 2) {
            var masterSeg = segs[i];
        
            if (masterSeg.length) {
                var masterBits = pullDataFromZpl(masterSeg);
                if (!$jq.isEmptyObject(masterBits)) {
                    $jq("#masterBarcode" + i).barcode(masterBits.accNo, "code128", {showHRI:false});
                    $jq("#masterText" + i).empty().html(masterBits.theRest);
                }
            }
            if (segs[i + 1].length) {
                var itemSegs = segs[i + 1].split("::");
                for (var j = 0; j < itemSegs.length; j++) {
                    var itemBits = pullDataFromZpl(itemSegs[j]);
                    if (!$jq.isEmptyObject(itemBits)) {
                        $jq("#itemBarcode" + i + '-' + j).barcode(itemBits.accNo, "code128", {showHRI:false});
                        $jq("#itemText" + i + '-' + j).empty().html(itemBits.theRest);
                    }
                }
            }
        }
        $jq("div[id^='itemText'], div[id^='masterText']").css("margin-left", "20px");
        $jq("div[id^='itemBarcode'], div[id^='masterBarcode']").css("padding-top", "15px");
        $jq("#masterZpl").empty().html(masterZpl);
        $jq("#itemZpl").empty().html(itemZpl);
        $jq("#zpl-modal").modal('show');
    }
}

function cancelPrint2() {
	$jq("#re-label-modal").modal('hide');
    setSave();
}

function setSave() {
	//disable or enable print button based on validity/visibility of fields
    $('rePrintButton').disabled = !isPrintEnabled();
    
	setSaveButton();
}

function setSaveButton(){
    var newAccession = $("newAccessionNumber").value;
    var accessionChanged = newAccession.length > 1 && newAccession != "<%=accessionNumber%>";
	var sampleItemChanged = $jq(".sampleItemChanged[value='true']").length > 0;
	var sampleAddIsValid = typeof (sampleAddValid) != 'undefined' ? sampleAddValid(false)
			: true;
	var sampleConfirmationIsValid = typeof (sampleConfirmationValid) != 'undefined' ? sampleConfirmationValid()
			: true;

	$("saveButtonId").disabled = errorsOnForm()
			|| !sampleAddIsValid
			|| !sampleConfirmationIsValid
			|| (checkedCount == 0 && !accessionChanged
					&& !samplesHaveBeenAdded() && !orderChanged && !sampleItemChanged);
}

</script>