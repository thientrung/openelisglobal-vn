<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.List,
	us.mn.state.health.lims.common.action.IActionConstants,
	java.util.ArrayList,
	java.text.DecimalFormat,
	org.apache.commons.validator.GenericValidator,
	us.mn.state.health.lims.inventory.form.InventoryKitItem,
	us.mn.state.health.lims.test.beanItems.TestResultItem,
	us.mn.state.health.lims.common.util.IdValuePair,
	us.mn.state.health.lims.common.formfields.FormFields,
	us.mn.state.health.lims.common.formfields.FormFields.Field,
	us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
	us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
	us.mn.state.health.lims.common.util.ConfigurationProperties,
	us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
	us.mn.state.health.lims.common.util.StringUtil,
    us.mn.state.health.lims.common.util.Versioning,
    us.mn.state.health.lims.common.exception.LIMSInvalidConfigurationException,
    us.mn.state.health.lims.common.util.DateUtil" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id='formName' value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<bean:define id="tests" name='<%=formName%>' property="testResult" />
<bean:size id="testCount" name="tests"/>
<bean:define id="inventory" name="<%=formName%>" property="inventoryItems" />

<bean:define id="pagingSearch" name='<%=formName%>' property="paging.searchTermToPage"  />

<bean:define id="logbookType" name="<%=formName%>" property="logbookType" />
 <bean:define id="testSectionsByName" name="<%=formName%>" property="testSectionsByName" />
<%!
	List<String> hivKits;
	List<String> syphilisKits;
	String basePath = "";
	String searchTerm = null;
	IAccessionNumberValidator accessionNumberValidator;
	boolean useSTNumber = true;
	boolean useNationalID = true;
	boolean useSubjectNumber = true;
	boolean useTechnicianName = true;
	boolean depersonalize = false;
	boolean ableToRefer = false;
	boolean compactHozSpace = false;
	boolean useInitialCondition = false;
	boolean failedValidationMarks = false;
	boolean noteRequired = false;
	boolean autofillTechBox = false;
    boolean useRejected = false;
 %>
<%
	hivKits = new ArrayList<String>();
	syphilisKits = new ArrayList<String>();

	for( InventoryKitItem item : (List<InventoryKitItem>)inventory ){
		if( item.getType().equals("HIV") ){
	hivKits.add(item.getInventoryLocationId());
		}else{
	syphilisKits.add( item.getInventoryLocationId());
		}
	}

	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
	+ request.getServerPort() + path + "/";

	searchTerm = request.getParameter("searchTerm");

    try{
	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
    }catch( LIMSInvalidConfigurationException e ){
        //no-op
    }
	useSTNumber = FormFields.getInstance().useField(Field.StNumber);
	useNationalID = FormFields.getInstance().useField(Field.NationalID);
	useSubjectNumber = FormFields.getInstance().useField(Field.SubjectNumber);
	useTechnicianName =  ConfigurationProperties.getInstance().isPropertyValueEqual(Property.resultTechnicianName, "true");
	useRejected =  ConfigurationProperties.getInstance().isPropertyValueEqual(Property.allowResultRejection, "true");

	depersonalize = FormFields.getInstance().useField(Field.DepersonalizedResults);
	ableToRefer = FormFields.getInstance().useField(Field.ResultsReferral);
	compactHozSpace = FormFields.getInstance().useField(Field.ValueHozSpaceOnResults);
	useInitialCondition = FormFields.getInstance().useField(Field.InitialSampleCondition);
	failedValidationMarks = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.failedValidationMarker, "true");
	noteRequired =  ConfigurationProperties.getInstance().isPropertyValueEqual(Property.notesRequiredForModifyResults, "true");
	autofillTechBox = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.autoFillTechNameBox, "true");

%>

<link rel="stylesheet" type="text/css" href="css/bootstrap_simple.css?ver=<%= Versioning.getBuildNumber() %>" />
<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/testResults.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/testReflex.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="scripts/overlibmws.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/jquery.ui.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/OEPaging.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/math-extend.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/multiselectUtils.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>" />


<script type="text/javascript" >

var invalidElements = new Array();
var requiredFields = new Array("testSectionsId", "searchReceivedDate");


<%if( ConfigurationProperties.getInstance().isPropertyValueEqual(Property.ALERT_FOR_INVALID_RESULTS, "true")){%>
       outOfValidRangeMsg = '<%= StringUtil.getMessageForKey("result.outOfValidRange.msg") %>';
<% }else{ %>
       outOfValidRangeMsg = null;
<% } %>

var compactHozSpace = '<%=compactHozSpace%>';
var dirty = false;

var pager = new OEPager('<%=formName%>', '<%= logbookType == "" ? "" : "&type=" + logbookType  %>');
pager.setCurrentPageNumber('<bean:write name="<%=formName%>" property="paging.currentPage"/>');

var pageSearch; //assigned in post load function

var pagingSearch = {};

<%
	for( IdValuePair pair : (List<IdValuePair>)pagingSearch){
		out.print( "pagingSearch[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
	}
%>


$jq(document).ready( function() {
			var searchTerm = '<%=searchTerm%>';
            loadMultiSelects();
			$jq("select[multiple]").asmSelect({
					removeLabel: "X"
				});

			$jq("select[multiple]").change(function(e, data) {
				handleMultiSelectChange( e, data );
				});

			pageSearch = new OEPageSearch( $("searchNotFound"), compactHozSpace == "true" ? "tr" : "td", pager );

			if( searchTerm != "null" ){
				 pageSearch.highlightSearch( searchTerm, false );
			}
			
            $jq('#modal_ok').on('click',function(e){
                addReflexToTests( '<%= StringUtil.getMessageForKey("button.label.edit")%>' );
                e.preventDefault();
                $jq('#reflexSelect').modal('hide');
			});
            
            loadPagedReflexSelections('<%= StringUtil.getMessageForKey("button.label.edit")%>');
            $jq(".asmContainer").css("display","inline-block");
            disableRejectedResults();
            showCachedRejectionReasonRows();
			});


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

function toggleKitDisplay( button ){
	if( button.value == "+" ){
		$(kitView).show();
		button.value = "-";
	}else{
		$(kitView).hide();
		button.value = "+";
	}
}

//Added by markaae.fr 2016-10-04 10:52AM
function /*void*/ updateFieldError(field) {
	if (field.value) {
		selectFieldErrorDisplay(true, $(field.id));
		setSampleFieldValidity(true, field.id);
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
	$jq('#saveButtonId').attr('disabled', !isValid);
	selectFieldErrorDisplay( isValid, $(formField));
	setSampleFieldValidity( isValid, formField );
	if (formField == "searchReceivedDate") {
    	setSearch();
    }
	
	if( message == '<%=IActionConstants.INVALID_TO_LARGE%>' ){
		alert( "<bean:message key="error.date.inFuture"/>" );
	}else if( message == '<%=IActionConstants.INVALID_TO_SMALL%>' ){
		alert( "<bean:message key="error.date.inPast"/>" );
	}
	
	if (!isValid) {
		$(formField).focus();
	} else {
		autofill($(formField));
	}
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
        if (date.id == "searchReceivedDate") {
        	setSearch();
        }
        autofill(date);
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
	        if (date.id == "searchReceivedDate") {
	        	setSearch();
	        }
	        return;
	        
	    } else {
	        //ajax call from utilities.js
	    	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	    }
    } catch (Exception) {
    	$jq('#saveButtonId').attr('disabled', true);
    	selectFieldErrorDisplay( false, $(date.id));
        setSampleFieldValidity( false, date.id );
        if (date.id == "searchReceivedDate") {
        	setSearch();
        }
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
	$("searchResultsID").disabled = !validToSearch;
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

//nhuql.gv ADD
function hiddenButtonSave(source) {
	  var inputs = document.getElementsByTagName("input");
	  var cbs = []; //will contain all checkboxes  
	  var checked = []; //will contain all checked checkboxes  
	  for (var i = 0; i < inputs.length; i++) {  
	      if (inputs[i].type == "checkbox") {  
	          cbs.push(inputs[i]);  
	          if (inputs[i].checked) {  
	              checked.push(inputs[i]);  
	          }  
	      }  
	  }  
	  var nbCbs = cbs.length; //number of checkboxes  
	  var nbChecked = checked.length; //number of checked checkboxes  
      if(nbChecked > 0){
    	  document.getElementById("saveButtonId").disabled = false; 
      }else{
    	  document.getElementById("saveButtonId").disabled = true; 
      }
}
//END nhuql.gv ADD

function markUpdated( index, userChoiceReflex, siblingReflexKey ) {
	if( userChoiceReflex ){
		var siblingId = siblingReflexKey != 'null' ? $(siblingReflexKey).value : null;
		showUserReflexChoices( index, $("resultId_" + index).value, siblingId );
	}
	// Check null
	if( $("modified_" + index) != null ) {
		$("modified_" + index).value = "true";
	}

	makeDirty();

    $jq("#saveButtonId").removeAttr("disabled");
}

function updateLogValue(element, index) {
	var logField = $("log_" + index );

	if( logField ){
		var logValue = Math.baseLog(element.value).toFixed(2);

		if( isNaN(logValue) ){
			logField.value = "--";
		}else{
			logField.value = logValue;
		}
	}
}

function /*void*/ setRadioValue(index, value) {
	$("results_" + index).value = value;
}

function  /*void*/ setMyCancelAction(form, action, validate, parameters) {
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function /*void*/ autofill( sourceElement ) {
	if (sourceElement.id == 'autofillBeginDate') {
		var techBoxes = $$(".startedDate");
	} else if (sourceElement.id == 'autofillcompletedDate') {
		var techBoxes = $$(".completedDate");
	} else {
		var techBoxes = $$(".techName");	
	}
	var boxCount = techBoxes.length,
	    value = sourceElement.value,
	    i = 0;
	
	for( ; i < boxCount; ++i){
		if (!techBoxes[i].disabled) {
			techBoxes[i].value = value;
			var index = parseInt(techBoxes[i].name.match((/\d+/g)));
			markUpdated(index);
			$jq("#modified_" + index).val("true");
		}
	}	
}

function validateForm() {
	return true;
}

function handleReferralCheckChange(checkbox,  index) {
	var referralReason = $( "referralReasonId_" + index );
	referralReason.value = 0;
	referralReason.disabled = !checkbox.checked;
    $("shadowReferred_" + index).value = checkbox.checked;
}

function /*void*/ handleReferralReasonChange(select, index ) {
	if( select.value == 0 ){
		$( "referralId_" + index ).checked = false;
		select.disabled = true;
	}
}

//this overrides the form in utilities.jsp
function  /*void*/ savePage() {
	var href = window.top.location.href;
	var urlParams = parseURLParams(href);
	var accessionNumber = "undefined";
    var patientID = "undefined";

	$jq( "#saveButtonId" ).prop("disabled",true);
	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	//Dũng add
    valueSearch = $jq("#searchValue").val();
    criteria = $jq("#searchCriteria").val();
	var form = window.document.forms[0];
	// Added by Mark 2016.06.28 05:45PM
	// Get accessionNumber and patientID from request if not in StatusResults page
	if (('<%=formName%>'.sub('Form','')) != ("<bean:message key="status.results"/>")) { 
		accessionNumber = urlParams.accessionNumber;
	    patientID = urlParams.patientID;
	}    
	// End of Modification
	form.action = '<%=formName%>'.sub('Form','') + "Update.do" + "?accessionNumber=" + accessionNumber + "&patientID=" + patientID  + '<%= logbookType == "" ? "" : "&type=" + logbookType  %>' +"&criteria="+criteria+ "&valueSearch="+valueSearch;
	form.submit();
}

//Dung add new
function parseURLParams(url) {
    var queryStart = url.indexOf("?") + 1,
        queryEnd   = url.indexOf("#") + 1 || url.length + 1,
        query = url.slice(queryStart, queryEnd - 1),
        pairs = query.replace(/\+/g, " ").split("&"),
        parms = {}, i, n, v, nv;

    if (query === url || query === "") {
        return;
    }

    for (i = 0; i < pairs.length; i++) {
        nv = pairs[i].split("=");
        n = decodeURIComponent(nv[0]);
        v = decodeURIComponent(nv[1]);

        if (!parms.hasOwnProperty(n)) {
            parms[n] = [];
        }

        parms[n].push(nv.length === 2 ? v : null);
    }

    return parms;
}
//end dung add new

function updateReflexChild( group) {
	
 	var reflexGroup = $$(".reflexGroup_" + group);
	var childReflex = $$(".childReflex_" + group);
 	var i, childId, rowId, resultIds = "", values="", requestString = "";
 	
 	if( childReflex ){
 		childId = childReflex[0].id.split("_")[1];
 		
		for( i = 0; i < reflexGroup.length; i++ ){
			if( childReflex[0] != reflexGroup[i]){
				rowId = reflexGroup[i].id.split("_")[1];
				resultIds += "," + $("hiddenResultId_" + rowId).value;
				values += "," + reflexGroup[i].value;
			}
		}
		requestString +=   "results=" +resultIds.slice(1) + "&values=" + values.slice(1) + "&childRow=" + childId;

		new Ajax.Request (
                      'ajaxQueryXML',  //url
                      {//options
                      method: 'get', //http method
                      parameters: 'provider=TestReflexCD4Provider&' + requestString,
                      indicator: 'throbbing',
                      onSuccess:  processTestReflexCD4Success,
                      onFailure:  processTestReflexCD4Failure
                           }
                          );
 	}
}

function processTestReflexCD4Failure() {
	alert("failed");
}

function processTestReflexCD4Success(parameters) {
    var xhr = parameters.xhr;
	//alert( xhr.responseText );
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var childRow, value;


	if (message.firstChild.nodeValue == "valid"){
		childRow = formField.getElementsByTagName("childRow").item(0).childNodes[0].nodeValue;
		value = formField.getElementsByTagName("value").item(0).childNodes[0].nodeValue;
		
		if( value && value.length > 0){
			$("results_" + childRow).value = value;
		}
	}
}

function submitTestSectionSelect() {
	var testSectionNameIdHash = [];
	<%	for( IdValuePair pair : (List<IdValuePair>) testSectionsByName){
			out.print( "testSectionNameIdHash[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
		}
	%>
	window.location.href = "LogbookResults.do?testSectionId=" + $('testSectionsId').value + "&type=" + testSectionNameIdHash[$('testSectionsId').value] +
							'&searchReceivedDate=' + $('searchReceivedDate').value;
} 

var showForceWarning = true;
function forceTechApproval(checkbox, index ){
	if( $jq(checkbox).attr('checked')){
		if( showForceWarning){
			alert( "<%= StringUtil.getContextualMessageForKey("result.forceAccept.warning")%>" );
			showForceWarning = false;
		}
		showNote( index );
	}else{
		hideNote( index);
	}
}

function processDateCallbackEvaluation(xhr) {

    //alert(xhr.responseText);
    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var formFieldId = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
    var givenDate = $jq("#" + formFieldId).val();
    var isValid = message == "valid";

    if( !isValid ){
        if( message == 'invalid_value_to_large' ){
            alert( "<bean:message key="error.date.inFuture"/>" );
        }else if( message == 'invalid_value_to_small' ){
            alert( "<bean:message key="error.date.inPast"/>" );
        }else if( message == "invalid"){
            alert( givenDate + " " + "<%=StringUtil.getMessageForKey("errors.date", "" )%>");
        }
    }

    updateFieldValidity(isValid, formFieldId);
}

function updateShadowResult(source, index){
  $jq("#shadowResult_" + index).val(source.value);
}

function /*void*/ handleEnterEvent() {
	savePage();
}

</script>

<logic:equal  name="<%=formName %>" property="displayTestSections" value="true">
<div id="searchDiv" class="colorFill"  >
<div id="PatientPage" class="colorFill" style="display:inline" >
<h2><bean:message key="sample.entry.search"/></h2>
	<table style="margin-left: auto; margin-right: auto;">
		<tr bgcolor="white">
			<td>
				<%= StringUtil.getMessageForKey("workplan.unit.types") %><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>
				<html:select name='<%= formName %>' property="testSectionId" styleId="testSectionsId" 
								onchange="validateField(this);" onblur="setSearch();" style="margin-left:3px; margin-right:6px;" >
					<app:optionsCollection name="<%=formName%>" property="testSections" label="value" value="id" />
				</html:select>
	   		</td>
	   		<!-- Added by markaae.fr 2016/10/04 10:13AM -->
			<td>
				<bean:message key="sample.receivedDate"/><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="searchReceivedDate" onblur="checkValidEntryDate(this,'past',false);" maxlength="10"
						property="searchReceivedDate" onkeyup="updateFieldError(this);addDateSlashes(this, event);" style="width:100px; margin-left:3px; margin-right:6px;" />
		   	</td>
		   	<td>
	            <input type="button"
		           name="searchResultsButton"
		           class="btn btn-default"
		           value="<%= StringUtil.getMessageForKey("label.patient.search")%>"
		           id="searchResultsID"
		           onclick="submitTestSectionSelect();"
		           disabled="disabled"
		           style="margin-top: -9px!important;height:30px;margin-left:3px;"
		           class="btn btn-default"
		           disabled="false" />
		   	</td>	   			
			<!-- End of Code Addition -->	   		
		</tr>
	</table>
	<br/>
	<h1>
		
	</h1>
</div>
</div>
</logic:equal>

<!-- Modal popup-->
<div id="reflexSelect" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="headerLabel"></h3>
    </div>
    <div class="modal-body">
        <input type="hidden" id="testRow" />
        <input type="hidden" id="targetIds" />
        <input type="hidden" id="serverResponse" />
        <p ><input style='vertical-align:text-bottom' id='selectAll' type='checkbox' onchange='modalSelectAll(this);' >&nbsp;&nbsp;&nbsp;<b><%=StringUtil.getMessageForKey("label.button.checkAll")%></b></p><hr>
    </div>
    <div class="modal-footer">
        <button id="modal_ok" class="btn btn-primary" disabled="disabled">OK</button>
        <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
    </div>
</div>

<logic:notEmpty name="<%=formName%>" property="logbookType" >
	<html:hidden name="<%=formName%>" property="logbookType" />
</logic:notEmpty>

<logic:notEqual name="testCount" value="0">
<logic:equal name="<%=formName%>" property="displayTestKit" value="true">
	<hr style="width:100%" />
    <input type="button" onclick="toggleKitDisplay(this)" value="+">
	<bean:message key="inventory.testKits"/>
	<div id="kitView" style="display: none;" class="colorFill" >
		<tiles:insert attribute="testKitInfo" />
		<br/>
		<hr style="width:100%" />
	</div>
</logic:equal>

<logic:equal  name='<%=formName%>' property="singlePatient" value="true">
<% if(!depersonalize){ %>        
<table style="width:100%" >
	<tr>
		
		<th style="width:20%">
			<bean:message key="person.lastName" />
		</th>
		<th style="width:20%">
			<bean:message key="person.firstName" />
		</th>
		<th style="width:10%">
			<bean:message key="patient.gender" />
		</th>
		<th style="width:15%">
			<bean:message key="patient.birthDate" />
		</th>
		<% if(useSTNumber){ %>
		<th style="width:15%">
			<bean:message key="patient.ST.number" />
		</th>
		<% } %>
		<% if(useNationalID){ %>
		<th style="width:20%">
			<%= StringUtil.getContextualMessageForKey("patient.NationalID") %>
		</th>
		<% } %>
		<% if(useSubjectNumber){ %>
		<th style="width:20%">
			<bean:message key="patient.subject.number" />
		</th>
		<% } %>
	</tr>
	<tr>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="lastName" />
		</td>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="firstName" />
		</td>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="gender" />
		</td>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="dob" />
		</td>
		<% if(useSTNumber){ %>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="st" />
		</td>
		<% } %>
		<% if(useNationalID){ %>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="nationalId" />
		</td>
		<% } %>
		<% if(useSubjectNumber){ %>
		<td style="text-align:center">
			<bean:write name="<%=formName%>" property="subjectNumber" />
		</td>
		<% } %>
	</tr>
</table>
<% } %>
<br/>
</logic:equal>

<div  style="width:100%" >
<logic:notEqual name="<%=formName%>" property="paging.totalPages" value="0">
	<html:hidden styleId="currentPageID" name="<%=formName%>" property="paging.currentPage"/>
	<bean:define id="total" name="<%=formName%>" property="paging.totalPages"/>
	<bean:define id="currentPage" name="<%=formName%>" property="paging.currentPage"/>
	<bean:define id="currentPageTotal" name="<%=formName%>" property="paging.currentPageTotal"/>
	<bean:define id="totalItems" name="<%=formName%>" property="paging.totalItems"/>

	<%if( "1".equals(currentPage)) {%>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.previous") %>' style="width:100px;" disabled="disabled" >
	<% } else { %>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.previous") %>' style="width:100px;" onclick="pager.pageBack();" />
	<% } %>
	<%if( total.equals(currentPage)) {%>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.next") %>' style="width:100px;" disabled="disabled" />
	<% }else{ %>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.next") %>' style="width:100px;" onclick="pager.pageFoward();"   />
	<% } %>

	&nbsp;
		<bean:write name="<%=formName%>" property="paging.currentPage"/> <bean:message key="list.of"/>
		<bean:write name="<%=formName%>" property="paging.totalPages"/>&nbsp;<bean:message key="list.pages"/>
		
	&nbsp; | &nbsp; 
	<bean:write name="<%=formName%>" property="paging.currentPageTotal"/>
	<bean:message key="list.of"/>
	<bean:write name="<%=formName%>" property="paging.totalItems"/>&nbsp;<bean:message key="list.items"/>
	
	<div class='textcontent' style="float: right" >
	<span style="visibility: hidden" id="searchNotFound"><em><%= StringUtil.getMessageForKey("search.term.notFound") %></em></span>
	<%=StringUtil.getContextualMessageForKey("resultsentry.accessionNumber")%> : &nbsp;
	<input type="text"
	       id="labnoSearch"
	       maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>' />
	<input type="button"  class="btn btn-default" onclick="pageSearch.doLabNoSearch($(labnoSearch))" value='<%= StringUtil.getMessageForKey("label.button.search") %>' style="margin-top: -9px!important;height:30px;margin-left:3px;">
	</div>
</logic:notEqual>

<div style="float: right" >
<img src="./images/nonconforming.gif" /> = <bean:message key="result.nonconforming.item"/>&nbsp;&nbsp;&nbsp;&nbsp;
<% if(failedValidationMarks){ %> 
<img src="./images/validation-rejected.gif" /> = <bean:message key="result.validation.failed"/>&nbsp;&nbsp;&nbsp;&nbsp;
<% } %>
</div>

</div>

<Table style="width:100%" border="0" cellspacing="0" >
	<!-- header -->
	<tr >
		<th style="text-align: center">
	  		<bean:message key="row.label"/>
		</th>
		<% if( !compactHozSpace ){ %>
		<th style="text-align: left">
			<%=StringUtil.getContextualMessageForKey("result.sample.id")%>
		</th>
		<logic:equal name="<%=formName %>" property="singlePatient" value="false">
			<th style="text-align: left">
				<bean:message key="result.sample.patient.summary"/>
			</th>
		</logic:equal>
		<% } %>

		<th style="text-align: center">
			<bean:message key="result.test.beginDate"/><br/>
			<!-- Trung Add -->
			<input type="text" size="10" style="width:75px;" maxlength="10" id="autofillBeginDate"
				onkeyup="addDateSlashes(this, event);"
				onblur="checkValidEntryDate(this, 'past', true);"/>
			<%-- <%=DateUtil.getDateUserPrompt()%> --%>
		</th>
		<th style="text-align: center; width: 110px;">
			<bean:message key="analysis.result.date"/><br/>
			<input type="text" size="10" style="width:75px;" maxlength="10" id="autofillcompletedDate" 
				onkeyup="addDateSlashes(this, event);"
				onblur="checkValidEntryDate(this, 'past', true);" />
		</th>
		<logic:equal name="<%=formName%>" property="displayTestMethod" value="true">
			<th style="width: 72px; padding-right: 10px; text-align: center">
				<bean:message key="result.method.auto"/>
			</th>
		</logic:equal>
		<th style="text-align: center">
			<bean:message key="result.test"/>
		</th>
		<th style="width:16px">&nbsp;</th>
		<th style="width: 56px; padding-right: 10px; text-align: center"><%= StringUtil.getContextualMessageForKey("result.forceAccept.header") %></th>
		<th style="width:165px; text-align: center">
			<bean:message key="result.result"/>
		</th>
		<% if( ableToRefer ){ %>
		<th style="text-align: left">
			<bean:message key="referral.referandreason"/>
		</th>
		<% } %>
		<% if( useTechnicianName ){ %>
		<th style="text-align: center;">
			<bean:message key="result.technician"/>
			<span class="requiredlabel">*</span>
			<% if(autofillTechBox){ %>
			<bean:message key="result.autofill"/>: </br><input type="text" size='10em' onchange="autofill( this )">
			<% } %>
		</th>
		<% }%>
        <% if( useRejected ){ %>
        <th style="text-align: center">
            <bean:message key="result.rejected"/>&nbsp;
        </th>
        <% }%>
		<th style="text-align: center">
			<bean:message key="result.notes"/>
		</th>
	</tr>
	<!-- body -->
	<logic:iterate id="testResult" name="<%=formName%>"  property="testResult" indexId="index" type="TestResultItem">
	<bean:define id="currentPage" name="<%=formName%>" property="paging.currentPage"/>
	<logic:equal name="testResult" property="isGroupSeparator" value="true">
	<tr>
		<td colspan="10"><hr/></td>
	</tr>
	<tr>
		<th >
			<bean:message key="sample.receivedDate"/> <br/>
			<bean:write name="testResult" property="receivedDate"/>
		</th>
		<th >
			<%=StringUtil.getContextualMessageForKey("resultsentry.accessionNumber")%><br/>
			<bean:write name="testResult" property="accessionNumber"/>
		</th>
		<th colspan="8" ></th>
	</tr>
	</logic:equal>
	<logic:equal name="testResult" property="isGroupSeparator" value="false">
		<bean:define id="lowerBound" name="testResult" property="lowerNormalRange" />
		<bean:define id="upperBound" name="testResult" property="upperNormalRange" />
		<bean:define id="lowerAbnormalBound" name="testResult" property="lowerAbnormalRange" />
		<bean:define id="upperAbnormalBound" name="testResult" property="upperAbnormalRange" />
        <bean:define id="significantDigits" name="testResult" property="significantDigits" />
		<bean:define id="rowColor" value='<%=(testResult.getSampleGroupingNumber() % 2 == 0) ? "evenRow" : "oddRow" %>' />
		<bean:define id="readOnly" value='<%=testResult.isReadOnly() ? "disabled=\'true\'" : "" %>' />
		<bean:define id="accessionNumber" name="testResult" property="accessionNumber"/>
	
	<% 
	boolean enableRowFields = false;
    if (testResult.getStatusUserSession().getHasAssign().equals("Y")) {
	    if (testResult.getStatusUserSession().getHasComplete().equals("Y")) {
        	enableRowFields = true;
	    } else {
	    	if ( !StringUtil.isNullorNill(testResult.getResultValue()) && !testResult.getResultValue().equals("0") ) {
	        	enableRowFields = true;
	    	}
	    }
    }    
   %>	
   <logic:equal name="testResult" property="statusUserSession.hasView" value="Y">
   <% if( compactHozSpace ){ %>
   <logic:equal  name="testResult" property="showSampleDetails" value="true">
		<tr class='<%= rowColor %>Head <%= accessionNumber%>' >
			<td colspan="10" class='InterstitialHead' >
			    <%=StringUtil.getContextualMessageForKey("resultsentry.accessionNumber")%> : &nbsp;
				<b><bean:write name="testResult" property="accessionNumber"/> -
				<bean:write name="testResult" property="sequenceNumber"/>
				<logic:notEmpty  name="testResult" property="emergency">
					<logic:equal name="testResult" property="emergency" 
						value='<%=StringUtil.getContextualMessageForKey("vn.hse1.emergency")%>'>
						<span style="color: blue;">(<bean:write name="testResult" property="emergency"/>)</span>&nbsp;
					</logic:equal>
					<logic:notEqual name="testResult" property="emergency" 
						value='<%=StringUtil.getContextualMessageForKey("vn.hse1.emergency")%>'>
						<span>(<bean:write name="testResult" property="emergency"/>)</span>&nbsp;
					</logic:notEqual>
				</logic:notEmpty>
				</b>
				<%=StringUtil.getContextualMessageForKey("project.browse.title")%>: &nbsp;
				<b><bean:write name="testResult" property="projectName"/></b>
				<% if(useInitialCondition){ %>
					&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="sample.entry.sample.condition" />:
					<b><bean:write name="testResult" property="initialSampleCondition" /></b>
				<% } %>
				&nbsp;&nbsp;&nbsp;&nbsp;<bean:message  key="sample.entry.sample.type"/>:
				<b><bean:write  name="testResult" property="sampleType"/></b>
		<logic:equal  name="<%=formName %>" property="singlePatient" value="false">
		    <% if( !depersonalize){ %>
				<logic:equal  name="testResult" property="showSampleDetails" value="true">
					<br/>
					<bean:message key="result.sample.patient.summary"/> : &nbsp;
					<b><bean:write name="testResult" property="patientName"/> &nbsp;
					<bean:write name="testResult" property="patientInfo"/></b>
				</logic:equal>
			<% } %>	
		</logic:equal>
		</td>
		</tr>
	</logic:equal>
    <% } %>
	
	<tr class='<%= rowColor %>'  id='<%="row_" + index %>'>
			<html:hidden name="testResult" property="isModified"  indexed="true" styleId='<%="modified_" + index%>' />
			<html:hidden name="testResult" property="analysisId"  indexed="true" styleId='<%="analysisId_" + index%>' />
			<html:hidden name="testResult" property="resultId"  indexed="true" styleId='<%="hiddenResultId_" + index%>'/>
			<html:hidden name="testResult" property="testId"  indexed="true" styleId='<%="testId_" + index%>'/>
			<html:hidden name="testResult" property="technicianSignatureId" indexed="true" />
			<html:hidden name="testResult" property="testKitId" indexed="true" />
			<html:hidden name="testResult" property="resultLimitId" indexed="true" />
			<html:hidden name="testResult" property="resultType" indexed="true" styleId='<%="resultType_" + index%>' />
			<html:hidden name="testResult" property="valid" indexed="true"  styleId='<%="valid_" + index %>'/>
			<html:hidden name="testResult" property="referralId" indexed="true" />
            <html:hidden name="testResult" property="referralCanceled" indexed="true" />
            <html:hidden name="testResult" property="considerRejectReason" styleId='<%="considerRejectReason_" + index %>' indexed="true" />
            <html:hidden name="testResult" property="hasQualifiedResult" indexed="true" styleId='<%="hasQualifiedResult_" + index %>' />
            <html:hidden name="testResult" property="shadowResultValue" indexed="true" styleId='<%="shadowResult_" + index%>' />
            <logic:equal name="testResult" property="userChoiceReflex" value="true">
                <html:hidden name="testResult" property="reflexJSONResult"  styleId='<%="reflexServerResultId_" + index%>'  styleClass="reflexJSONResult" indexed="true"/>
            </logic:equal>
			<logic:notEmpty name="testResult" property="thisReflexKey">
					<input type="hidden" id='<%= testResult.getThisReflexKey() %>' value='<%= index %>' />
			</logic:notEmpty>
		
		<td class="ruled" id="'<%="rowNum_" + index %>'" style="text-align:center;">
    		<%= (index + 1) + ((Integer.parseInt(currentPage.toString()) - 1) * IActionConstants.PAGING_SIZE) %>
   		</td>	
		 <% if( !compactHozSpace ){ %>
	     <td class='<%= accessionNumber%>'>
			<logic:equal  name="testResult" property="showSampleDetails" value="true">
				<bean:write name="testResult" property="accessionNumber"/> -
				<bean:write name="testResult" property="sequenceNumber"/>
			</logic:equal>
		</td>
		<logic:equal  name="<%=formName %>" property="singlePatient" value="false">
			<td >
				<logic:equal  name="testResult" property="showSampleDetails" value="true">
					<bean:write name="testResult" property="patientName"/><br/>
					<bean:write name="testResult" property="patientInfo"/>
				</logic:equal>
			</td>
		</logic:equal>
		<% } %>
		<!-- date cell -->
		<td class="ruled" style="text-align:center;">
			<html:text name="testResult"
                       property="startedDate"
                       indexed="true"
                       size="10"
                       style="width:75px;"
                       maxlength="10"
                       tabindex='-1'
                       onchange='<%="markUpdated(" + index + ");checkValidDate(this, processDateCallbackEvaluation, \'past\', false)" %>'
                       onblur="checkValidEntryDate(this, 'past', true);"
                       onkeyup="addDateSlashes(this, event);"
                       styleId='<%="startedDate_" + index%>'
                       disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'
                       styleClass='<%= GenericValidator.isBlankOrNull(testResult.getTechnicianSignatureId()) ? "startedDate" : "" %>'/>
		</td>
		<!-- Trung Add -->
		<td class="ruled" style="text-align:center;">
			<html:text name="testResult"
                       property="completedDate"
                       indexed="true"
                       size="10"
                       style="width:75px;"
                       maxlength="10"
                       tabindex='-1'
                       onchange='<%="markUpdated(" + index + ");checkValidDate(this, processDateCallbackEvaluation, \'past\', false)" %>'
                       onblur="checkValidEntryDate(this, 'past', true);"
                       onkeyup="addDateSlashes(this, event);"
                       styleId='<%="completedDate_" + index%>'
                       disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'
                       styleClass='<%= GenericValidator.isBlankOrNull(testResult.getTechnicianSignatureId()) ? "completedDate" : "" %>'/>
			<%-- <input type="text" size="10" style="width:75px;" maxlength="10" id="newColumn"
				onkeyup="addDateSlashes(this, event);"
				class='<%= GenericValidator.isBlankOrNull(testResult.getTechnicianSignatureId()) ? "completedDate" : "" %>'/> --%>
		</td>
		<logic:equal  name="<%=formName%>" property="displayTestMethod" value="true">
			<td class="ruled" style='text-align: center'>
				<html:checkbox name="testResult"
							property="analysisMethod"
							indexed="true"
							tabindex='-1'
							onchange='<%="markUpdated(" + index + ");"%>'
                       		disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'/>
			</td>
		</logic:equal>
		<!-- results -->
		<logic:equal name="testResult" property="resultDisplayType" value="HIV">
			<td style="vertical-align:top" class="ruled">
				<html:hidden name="testResult" property="testMethod" indexed="true"/>
				<bean:write name="testResult" property="testName"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<bean:message key="inventory.testKit"/>
				<html:select name="testResult"
							 property="testKitInventoryId"
							 value='<%=testResult.getTestKitInventoryId()%>'
							 indexed="true"
							 tabindex='-1'
							 onchange='<%="markUpdated(" + index + ");"%>' >
					<logic:iterate id="id" indexId="index" collection="<%=hivKits%>">
						<option value='<%=id%>'  <%if(id.equals(testResult.getTestKitInventoryId())) out.print("selected");%>  >
								<%=id%>
							</option>
					</logic:iterate>
					<logic:equal name="testResult" property="testKitInactive" value="true">
						<option value='<%=testResult.getTestKitInventoryId()%>' selected ><%=testResult.getTestKitInventoryId()%></option>
					</logic:equal>
				</html:select>
			</td>
		</logic:equal>
		<logic:equal name="testResult" property="resultDisplayType" value="SYPHILIS">
			<td style="vertical-align:middle; text-align: center" class="ruled">
				<html:hidden name="testResult" property="testMethod" indexed="true"/>
				<bean:write name="testResult" property="testName"/>
				<logic:greaterThan name="testResult" property="reflexStep" value="0">
				&nbsp;--&nbsp;
				<bean:message key="reflexTest.step" />&nbsp;<bean:write name="testResult" property="reflexStep"/>
				</logic:greaterThan>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<bean:message key="inventory.testKit"/>
				<html:select name="testResult"
							 indexed="true"
							 tabindex='-1'
							 property="testKitInventoryId"
							 value='<%=testResult.getTestKitInventoryId()%>'
							 onchange='<%="markUpdated(" + index + ");"%>' >
					<logic:iterate id="id" indexId="index" collection="<%=syphilisKits%>">
							<option value='<%=id%>'  <%if(id.equals(testResult.getTestKitInventoryId())) out.print("selected");%>  >
								<%=id%>
							</option>
					</logic:iterate>
					<logic:equal name="testResult" property="testKitInactive" value="true">
						<option value='<%=testResult.getTestKitInventoryId()%>' selected ><%=testResult.getTestKitInventoryId()%></option>
					</logic:equal>
				</html:select>
			</td>
		</logic:equal>
		<logic:notEqual name="testResult" property="resultDisplayType" value="HIV"><logic:notEqual name="testResult" property="resultDisplayType" value="SYPHILIS">
			<td style="vertical-align:middle" class="ruled">
                <%= testResult.getTestName() %>
                <br/>
				<logic:notEmpty  name="testResult" property="normalRange" >
					<bean:write name="testResult" property="normalRange"/>&nbsp;
				</logic:notEmpty>
				<bean:write name="testResult" property="unitsOfMeasure"/>
			</td>
		</logic:notEqual></logic:notEqual>

		<td class="ruled" style='vertical-align: middle'>
		<% if( failedValidationMarks){ %>
		<logic:equal name="testResult" property="failedValidation" value="true">
			<img src="./images/validation-rejected.gif" />
		</logic:equal>
		<% } %>
		<logic:equal name="testResult" property="nonconforming" value="true">
			<img src="./images/nonconforming.gif" />
		</logic:equal>
		</td>
		<!-- force acceptance -->
		<td class="ruled" style='text-align: center'>
			<html:checkbox name="testResult"
							property="forceTechApproval"
							indexed="true"
							tabindex='-1'
							onchange='<%="markUpdated(" + index + "); " %>'
                       		disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'/>
		</td>
		<!-- result cell -->
		<td id='<%="cell_" + index %>' class="ruled" >
			<logic:equal name="testResult" property="resultType" value="N">
			    <input type="text" 
			           name='<%="testResult[" + index + "].resultValue" %>' 
			           size="6" 
			           value='<%= testResult.getResultValue() %>' 
			           id='<%= "results_" + index %>'
                       <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                       <%=!enableRowFields ? "disabled='disabled'" : ""%>
			           style='<%=!(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? 
			                   "background: " + (testResult.isValid() ? testResult.isNormal() ? "#ffffff" : "#ffffa0" : "#ffa0a0") :
			                       "background: #eeeeee"%>'
			           title='<%= (testResult.isValid() ? testResult.isNormal() ? "" : StringUtil.getMessageForKey("result.value.abnormal") : StringUtil.getMessageForKey("result.value.invalid")) %>' 
					   class='<%= (testResult.isReflexGroup() ? "reflexGroup_" + testResult.getReflexParentGroup()  : "")  +  (testResult.isChildReflex() ? " childReflex_" + testResult.getReflexParentGroup() : "") %> ' 
					   onchange='<%="validateResults( this," + index + "," + lowerBound + "," + upperBound + "," + lowerAbnormalBound + "," + upperAbnormalBound + "," + significantDigits +", \"XXXX\" );" +
						               "markUpdated(" + index + "); " +
						                (testResult.isReflexGroup() && !testResult.isChildReflex() ? "updateReflexChild(" + testResult.getReflexParentGroup()  +  " ); " : "") 
						                   + 
						                ( testResult.isDisplayResultAsLog() ? " updateLogValue(this, " + index + ");" : "" ) +
						                  " updateShadowResult(this, " + index + ");"%>'/>
			</logic:equal>
			<logic:equal name="testResult" property="resultType" value="A">
				<app:text name="testResult"
						  indexed="true"
						  property="resultValue"
						  size="20"
                          disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6)  || !enableRowFields%>'
						  style='<%=!(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? 
                               "background: " + (testResult.isValid() ? testResult.isNormal() ? "#ffffff" : "#ffffa0" : "#ffa0a0") :
                                   "background: #eeeeee"%>'
						  title='<%= (testResult.isValid() ? testResult.isNormal() ? "" : StringUtil.getMessageForKey("result.value.abnormal") : StringUtil.getMessageForKey("result.value.invalid")) %>' 
						  styleId='<%="results_" + index %>'
						  onchange='<%="markUpdated(" + index + ");"  +
						  			   ( testResult.isDisplayResultAsLog() ? " updateLogValue(this, " + index + ");" : "" ) +
						                " updateShadowResult(this, " + index + ");"%>'/>
			</logic:equal>
			<logic:equal name="testResult" property="resultType" value="R">
				<!-- text results -->
				<app:textarea name="testResult"
						  indexed="true"
						  property="resultValue"
						  rows="2"
                       	  disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'/>
						  style='<%="background: " + (testResult.isValid() ? testResult.isNormal() ? "#ffffff" : "#ffffa0" : "#ffa0a0") %>'
						  title='<%= (testResult.isValid() ? testResult.isNormal() ? "" : StringUtil.getMessageForKey("result.value.abnormal") : StringUtil.getMessageForKey("result.value.invalid")) %>'
						  styleId='<%="results_" + index %>'
						  onkeyup='<%="value = value.substr(0, 200); markUpdated(" + index + ");"  +
						                " updateShadowResult(this, " + index + ");"%>'
						  />
			</logic:equal>
			<% if("D".equals(testResult.getResultType())  ){ %>
			<!-- dictionary results -->
			<select name="<%="testResult[" + index + "].resultValue" %>"
			        onchange="<%="markUpdated(" + index + ", " + testResult.isUserChoiceReflex() +  ", \'" + testResult.getSiblingReflexKey() + "\');"   +				               
						               (testResult.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + testResult.getQualifiedDictionaryId() + ", 'D');" :"") +
						                 " updateShadowResult(this, " + index + ");"%>"
			        id='<%="resultId_" + index%>'
                    <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                    <%=!enableRowFields ? "disabled='disabled'" : ""%>
                     >
					<option value="0"></option>
					<logic:iterate id="optionValue" name="testResult" property="dictionaryResults" type="IdValuePair" >
						<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(testResult.getResultValue())) out.print("selected"); %>  >
							<bean:write name="optionValue" property="value"/>
						</option>
					</logic:iterate>
			</select><br/>
			<input type="text" 
			           name='<%="testResult[" + index + "].qualifiedResultValue" %>' 
			           value='<%= testResult.getQualifiedResultValue() %>' 
			           id='<%= "qualifiedDict_" + index %>'
                       <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                       <%=!enableRowFields ? "disabled='disabled'" : ""%>
					   style = '<%= "display:" + ((testResult.isHasQualifiedResult() || 
					           (testResult.getQualifiedDictionaryId() != null 
					           && testResult.getResultValue() != "" 
					           && !"0".equals(testResult.getResultValue()) 
					           && testResult.getQualifiedDictionaryId().indexOf(testResult.getResultValue()) != -1)) ? "inline" : "none") %>'
					   onchange='<%="markUpdated(" + index + ");$jq(\"#hasQualifiedResult_" + index + "\").val(\"true\");"%>'
					    />
			<% } %>
			<logic:equal name="testResult" property="resultType" value="M">
			<!-- multiple results -->
			<select name="<%="testResult[" + index + "].multiSelectResultValues" %>"
					id='<%="resultId_" + index + "_0"%>'
                    class="<%=testResult.isUserChoiceReflex() ? "userSelection" : "" %>"
					multiple="multiple"
                    <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                    <%=!enableRowFields ? "disabled='disabled'" : ""%>
				 	title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
				 	onchange='<%="markUpdated(" + index + "); "  +
				               ((noteRequired && testResult.getMultiSelectResultValues() != null && testResult.getMultiSelectResultValues().length() > 2 ) ? "showNewNote( " + index + ");" : "") +
				               (testResult.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + testResult.getQualifiedDictionaryId() + ", \"M\" );" :"")%>' >
					<logic:iterate id="optionValue" name="testResult" property="dictionaryResults" type="IdValuePair" >
						<option value='<%=optionValue.getId()%>' >
							<bean:write name="optionValue" property="value"/>
						</option>
					</logic:iterate>
				</select>
				<html:hidden name="testResult" property="multiSelectResultValues" indexed="true" styleId='<%="multiresultId_" + index%>' styleClass="multiSelectValues"  />
                <input type="text"
                   name='<%="testResult[" + index + "].qualifiedResultValue" %>'
                   value='<%= testResult.getQualifiedResultValue() %>'
                   id='<%= "qualifiedDict_" + index %>'
                   style = '<%= "display:" + ( testResult.isHasQualifiedResult() ? "inline" : "none") %>'
                   <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                   <%=!enableRowFields ? "disabled='disabled'" : ""%>
                   onchange='<%="markUpdated(" + index + ");" %>'
                />
			</logic:equal>
            <logic:equal name="testResult" property="resultType" value="C">
                <!-- cascading multiple results -->
                <div id='<%="cascadingMulti_" + index + "_0"%>' class='<%="cascadingMulti_" + index %>' >
                <input type="hidden" id='<%="divCount_" + index %>' value="0" >
                <select name="<%="testResult[" + index + "].multiSelectResultValues" %>"
                        id='<%="resultId_" + index + "_0"%>'
                        class="<%=testResult.isUserChoiceReflex() ? "userSelection" : "" %>"
                        multiple="multiple"
                    	<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                    	<%=!enableRowFields ? "disabled='disabled'" : ""%>
                        title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
                        onchange='<%="markUpdated(" + index + "); "  +
						               ((noteRequired && testResult.getMultiSelectResultValues() != null && testResult.getMultiSelectResultValues().length() > 2 ) ? "showNewNote( " + index + ");" : "") +
						               (testResult.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + testResult.getQualifiedDictionaryId() + ", \"M\" );" :"")%>' >
                    <logic:iterate id="optionValue" name="testResult" property="dictionaryResults" type="IdValuePair" >
                        <option value='<%=optionValue.getId()%>' >
                            <bean:write name="optionValue" property="value"/>
                        </option>
                    </logic:iterate>
                </select>
                <input class='<%="addMultiSelect" + index%>' type="button" value="+" onclick='<%="addNewMultiSelect(" + index + ", this);" + (noteRequired ? "showNewNote( " + index + ");" : "" ) %>'/>
                <input class='<%="removeMultiSelect" + index%>' type="button" value="-" onclick='<%="removeMultiSelect(\"target\");" + (noteRequired ? "showNewNote( " + index + ");" : "" )%>' style="display: none" />
                <html:hidden name="testResult" property="multiSelectResultValues" indexed="true" styleId='<%="multiresultId_" + index%>' styleClass="multiSelectValues"  />
                <input type="text"
                       name='<%="testResult[" + index + "].qualifiedResultValue" %>'
                       value='<%= testResult.getQualifiedResultValue() %>'
                       id='<%= "qualifiedDict_" + index %>'
                       style = '<%= "display:" + ( testResult.isHasQualifiedResult() ? "inline" : "none") %>'
                       <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                       <%=!enableRowFields ? "disabled='disabled'" : ""%>
                       onchange='<%="markUpdated(" + index + ");" %>'
                        />

                </div>
            </logic:equal>
			<% if( testResult.isDisplayResultAsLog()){ %>
						<br/><input type='text'
								    id='<%= "log_" + index %>'
									disabled='disabled'
									style="color:black"
									value='<% try{
												Double value = Math.log10(Double.parseDouble(testResult.getResultValue()));
												DecimalFormat twoDForm = new DecimalFormat("##.##");
												out.print(Double.valueOf(twoDForm.format(value)));
												}catch(Exception e){
													out.print("--");} %>'
									size='6' /> log
					<% } %>
           <%--  <bean:write name="testResult" property="unitsOfMeasure"/> --%>
		</td>
		<!-- End result cell -->
		<% if( ableToRefer ){ %>
		<td style="white-space: nowrap" class="ruled">
            <html:hidden name="testResult" property="referralId" indexed='true'/>
            <html:hidden name="testResult" property="shadowReferredOut" indexed="true" styleId='<%="shadowReferred_" + index %>' />
		<% if(GenericValidator.isBlankOrNull(testResult.getReferralId()) || testResult.isReferralCanceled()){  %>
			<html:checkbox name="testResult"
						   property="referredOut"
						   indexed="true"
						   styleId='<%="referralId_" + index %>'
						   onchange='<%="markUpdated(" + index + "); handleReferralCheckChange( this, " + index + ")" %>'/>
		<% } else {%>
			<html:checkbox name="testResult"
						   property="referredOut"
						   indexed="true"
						   disabled="true" />
		<% } %>
			<select name="<%="testResult[" + index + "].referralReasonId" %>"
			        id='<%="referralReasonId_" + index%>'
					onchange='<%="markUpdated(" + index + "); handleReferralReasonChange( this, " + index + ")" %>'
			        <% out.print(testResult.isShadowReferredOut() && "0".equals(testResult.getReferralReasonId()) ? "" : "disabled='true'"); %> >
					<option value='0' >
					   <logic:equal name="testResult" property="referralCanceled" value="true"  >
					   		<bean:message key="referral.canceled" />
					   </logic:equal>
					</option>
			<logic:iterate id="optionValue" name='<%=formName %>' property="referralReasons" type="IdValuePair" >
					<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(testResult.getReferralReasonId())) out.print("selected"); %>  >
							<bean:write name="optionValue" property="value"/>
					</option>
			</logic:iterate>
			</select>
		</td>
		<% } %>
		<% if( useTechnicianName){ %>
		<td style="text-align: center" class="ruled">
			<app:text name="testResult"
					   styleId='<%="technicianSig_" + index %>'
					   styleClass='<%= GenericValidator.isBlankOrNull(testResult.getTechnicianSignatureId()) ? "techName" : "" %>'
					   property="technician"
					   size="10em"
                       maxlength="20"
                       tabindex="-1"
					   onchange='<%="markUpdated(" + index + ");"%>'
                       disabled='<%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields%>'
                       />
                       
		</td>
		<% } %>
		<% if( useRejected){ %> 
			<td class="ruled" style='text-align: center'>
			<html:hidden name="testResult" property="shadowRejected" indexed="true" styleId='<%="shadowRejected_" + index %>' />
			
			<input type="hidden" id='<%="isRejected_" + index %>' value='<%= testResult.isRejected() %>'/>
	                <html:checkbox name="testResult"
	                    styleId='<%="rejected_" + index%>' 
	                    property="rejected"
	                    indexed="true"
	                    tabindex='-1'
	                    onchange='<%="markUpdated(" + index + "); showHideRejectionReasons(" + index + ", \'" + StringUtil.getContextualMessageForKey( "result.delete.confirm" ) + "\' );" %>' />
	   		</td>
		<% } %>
		<td style="text-align:center" class="ruled">
						 	<img src="./images/note-add.gif"
						 	     onclick='<%= "showHideNotes( " + index + ", this);" %>'
						 	     id='<%="showHideButton_" + index %>'
						 	     <%=((!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) || !enableRowFields) ? "disabled=true" : ""%>
						    />
            <input type="hidden" name="hideShowFlag" value="hidden" id='<%="hideShow_" + index %>'>
		</td>
	</tr>
	<tr id='<%="rejectReasonRow_" + index %>'
        class='<%= rowColor %>'
        style='<%= ("true".equals(testResult.getConsiderRejectReason()) ? "" : "display: none;") %>'>
        <td colspan="4"></td>
        <td colspan="6" style="text-align:right" >
               <select name="<%="testResult[" + index + "].rejectReasonId"%>"
                    id='<%="rejectReasonId_" + index%>'
                    <%=(!StringUtil.isNullorNill(testResult.getAnalysisStatusId()) && Integer.valueOf(testResult.getAnalysisStatusId()) == 6) ? "disabled='disabled'" : ""%>
                    <%=!enableRowFields ? "disabled='disabled'" : ""%>
                    <logic:iterate id="optionValue" name="<%=formName %>" property="rejectReasons" type="IdValuePair" >
                        <option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(testResult.getRejectReasonId())) out.print("selected"); %>  >
                            <bean:write name="optionValue" property="value"/>
                        </option>
                    </logic:iterate>
            </select><br/>
       </td>
    </tr>   
	<logic:notEmpty name="testResult" property="pastNotes">
		<tr class='<%= rowColor %>' >
			<td colspan="2" style="text-align:right;vertical-align:top"><bean:message key="label.prior.note" />: </td>
			<td colspan="8" style="text-align:left">
				<%= testResult.getPastNotes() %>
			</td>
		</tr>
	</logic:notEmpty>
	<tr id='<%="noteRow_" + index %>'
		class='<%= rowColor %>'
		style="display: none;">
		<!-- Commented by Mark 2016/06/14 04:07 VST -->
		<%-- <td colspan="4" style="vertical-align:top;text-align:right"><% if(noteRequired &&
														 !(GenericValidator.isBlankOrNull(testResult.getMultiSelectResultValues()) && 
														   GenericValidator.isBlankOrNull(testResult.getResultValue()))){ %>
													  <bean:message key="note.required.result.change"/>		
													<% } else {%>
													<bean:message key="note.note"/>
													<% } %>
													:</td> --%>
		<!-- End of Comment -->											
		<td colspan="4" style="vertical-align:top;text-align:right"><bean:message key="note.note"/>:</td>
		<td colspan="6" style="text-align:left" >
			<html:textarea styleId='<%="note_" + index %>'
						   onchange='<%="markUpdated(" + index + ");"%>'
					   	   name="testResult"
			           	   property="note"
			           	   indexed="true"
			           	   cols="100"
			           	   style="resize:none;"
			           	   rows="6" 
			           	   disabled='<%=!enableRowFields%>'/>
		</td>
	</tr>
	</logic:equal>
	</logic:equal>
	</logic:iterate>
</Table>
<logic:notEqual name="<%=formName%>" property="paging.totalPages" value="0">
	<html:hidden styleId="currentPageID" name="<%=formName%>" property="paging.currentPage"/>
	<bean:define id="total" name="<%=formName%>" property="paging.totalPages"/>
	<bean:define id="currentPage" name="<%=formName%>" property="paging.currentPage"/>
	<bean:define id="currentPageTotal" name="<%=formName%>" property="paging.currentPageTotal"/>
	<bean:define id="totalItems" name="<%=formName%>" property="paging.totalItems"/>

	<%if( "1".equals(currentPage)) {%>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.previous") %>' style="width:100px;" disabled="disabled" >
	<% } else { %>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.previous") %>' style="width:100px;" onclick="pager.pageBack();" />
	<% } %>
	<%if( total.equals(currentPage)) {%>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.next") %>' style="width:100px;" disabled="disabled" />
	<% }else{ %>
		<input type="button" value='<%=StringUtil.getMessageForKey("label.button.next") %>' style="width:100px;" onclick="pager.pageFoward();"   />
	<% } %>

	&nbsp;
		<bean:write name="<%=formName%>" property="paging.currentPage"/> <bean:message key="list.of"/>
		<bean:write name="<%=formName%>" property="paging.totalPages"/>&nbsp;<bean:message key="list.pages"/>
		
	&nbsp; | &nbsp; 
	<bean:write name="<%=formName%>" property="paging.currentPageTotal"/>
	<bean:message key="list.of"/>
	<bean:write name="<%=formName%>" property="paging.totalItems"/>&nbsp;<bean:message key="list.items"/>
</logic:notEqual>

</logic:notEqual>


<logic:equal  name="<%=formName %>" property="displayTestSections" value="true">
	<logic:equal name="testCount"  value="0">
		<logic:notEqual name="<%=formName %>" property="testSectionId" value="0">
		<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
		</logic:notEqual>
	</logic:equal>
</logic:equal>

<logic:notEqual  name="<%=formName %>" property="displayTestSections" value="true">
	<logic:equal name="testCount"  value="0">
	<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
	</logic:equal>
</logic:notEqual>
