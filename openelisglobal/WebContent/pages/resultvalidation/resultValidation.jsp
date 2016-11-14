<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="org.apache.commons.validator.GenericValidator, us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
	us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
	us.mn.state.health.lims.common.util.IdValuePair,
	us.mn.state.health.lims.common.util.StringUtil,
	us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.typeoftestresult.valueholder.TypeOfTestResult.ResultType,
    java.text.DecimalFormat,
	java.util.List,
	us.mn.state.health.lims.resultvalidation.bean.AnalysisItem,
	us.mn.state.health.lims.common.util.ConfigurationProperties,
	us.mn.state.health.lims.common.util.ConfigurationProperties.Property" %>

<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax" %>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="testSection"	value='<%=request.getParameter("type")%>' />
<bean:define id="testName"	value='<%=request.getParameter("test")%>' />
<bean:define id="results" name="<%=formName%>" property="resultList" />
<bean:define id="pagingSearch" name='<%=formName%>' property="paging.searchTermToPage" type="List<IdValuePair>" /> 
<bean:define id="testSectionsByName" name='<%=formName%>' property="testSectionsByName" />
<bean:size id="resultCount" name="results" />

<%!
	boolean showAccessionNumber = false;
	String currentAccessionNumber = "";
	int rowColorIndex = 2;
	IAccessionNumberValidator accessionNumberValidator;
	String searchTerm = null;
	boolean showTestSectionSelect = false;
%>
<%
	String basePath;
	String path = request.getContextPath();
	basePath = request.getScheme() + "://" + request.getServerName() + ":"
	+ request.getServerPort() + path + "/";
	currentAccessionNumber="";
	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
	searchTerm = request.getParameter("searchTerm");
	showTestSectionSelect = !ConfigurationProperties.getInstance().isPropertyValueEqual(Property.configurationName, "CI RetroCI");
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/math-extend.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="scripts/OEPaging.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="scripts/jquery.asmselect.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<link rel="stylesheet" type="text/css" href="css/jquery.asmselect.css?ver=<%= Versioning.getBuildNumber() %>" />
<script type="text/javascript" src="<%=basePath%>scripts/testReflex.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/multiselectUtils.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="scripts/testSectionTestMap.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript" >
var invalidElements = new Array();
var requiredFields = new Array("sectionId", "receivedDate");
var originalTestList = null;
var newSearchInfo = false;
var dirty = false;
var pager = new OEPager('<%=formName%>', '<%= testSection == "" ? "" : "&type=" + testSection  %>' + '<%= "&test=" + testName  %>');
pager.setCurrentPageNumber('<bean:write name="<%=formName%>" property="paging.currentPage"/>');

var pageSearch; //assigned in post load function

var pagingSearch = {};

<%
	for( IdValuePair pair : ((List<IdValuePair>)pagingSearch)){
		out.print( "pagingSearch[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
	}
%>

$jq(document).ready( function() {
	var searchTerm = '<%=searchTerm%>';

	mapTestSectionTest();
	var newSelect = document.createElement("select");
	$jq("#testNameId option").each(function(){
		var newOption = new Option($jq(this).text(), $jq(this).val());
		newSelect.add(newOption);
	});
	originalTestList = newSelect.options;
	
    loadMultiSelects();
    $jq("select[multiple]").asmSelect({
        removeLabel: "X"
    });

    $jq("select[multiple]").change(function(e, data) {
        handleMultiSelectChange( e, data );
    });
    pageSearch = new OEPageSearch( $("searchNotFound"), "td", pager );
	
	if( searchTerm != "null" ){
		pageSearch.highlightSearch( searchTerm, false );
	}
    $jq(".asmContainer").css("display","inline-block");
    $jq('#validateTransferButtonId').css('display','inline-block');
    setSearch();
});


function removeReflexesFor(){
    //no-op
}

function showUserReflexChoices(){
    //no-op
}

function  /*void*/ setMyCancelAction(form, action, validate, parameters) {
	//first turn off any further validation
	setAction(window.document.forms[0], 'Cancel', 'no', '');
}
// check if all child checkboxes are checked or not
function isCheckedAllOrNon(classField,groupingNumber){
	var flag =true;
	$$("input[class^='"+classField+" "+classField+"_"+groupingNumber+"']").each( function(item){
		if(item.checked == false) {
			flag= false;
			return false;
		}
	});
	
	return flag;
}
// check checkAll checkbox for all analysis results of each accession number
// this case happen when display results have many results belong to many accession numbers
function isCheckedAllIndividual(classField){
	var flag =true;
	$$("input[id^='"+classField+"']").each( function(item){
		if(item.checked == false) {
			flag= false;
			return false;
		}
	});
	
	return flag;
}

function isCheckedAll(classField,arrayGroupingNumber){
	var flag=true;
	for (var i = 0; i < arrayGroupingNumber.length; i++) {
		var number=arrayGroupingNumber[i].getAttribute("id").split("_")[1];
		$$(classField + number).each( function(item){
			if(item.checked == false) {
				flag= false;
			// add "return false" to break the loop, if using "break" will cause error
				return false;
			}
		});
	}
	
	return flag;
}

function /*void*/ enableDisableCheckboxes( matchedElement, groupingNumber ){
	$(matchedElement).checked = false;
	$(name="selectAllReject").checked = false;
	$(name="selectAllAccept").checked = false;
	if($("sampleAccepted_" + groupingNumber)!= null)
	$("sampleAccepted_" + groupingNumber).checked = false;
	if($("sampleRejected_" + groupingNumber)!=null)
	$("sampleRejected_" + groupingNumber).checked = false;
	//Dung 2016.07.07
	//Checkbox check selectAllAccept when click child checkbox
	
	var tmp= false;
	tmp = isCheckedAllOrNon("accepted",groupingNumber);
	if (tmp==true) {
		if ($("sampleAccepted_" + groupingNumber)!= null) {
		   $("sampleAccepted_" + groupingNumber).checked = true;
		}
	} else { 
		if($("sampleAccepted_" + groupingNumber)!= null) {
			$("sampleAccepted_" + groupingNumber).checked = false;
		}
	}
	var tmpAllAccept = false;
	tmpAllAccept = isCheckedAllIndividual("sampleAccepted_");
	if (tmpAllAccept==true) {
		$(name="selectAllAccept").checked = true;
	} else { 
		$(name="selectAllAccept").checked = false;
	}
	
	//Checkbox check selectAllReject when click child checkbox
	tmp = isCheckedAllOrNon("rejected",groupingNumber);
	if (tmp==true) {
		if($("sampleRejected_" + groupingNumber)!=null) {
			$("sampleRejected_" + groupingNumber).checked = true;
		}
	} else {
		if($("sampleRejected_" + groupingNumber)!=null) {
			$("sampleRejected_" + groupingNumber).checked = false;
		}
	}
	var tmpAllReject = false;
	tmpAllReject = isCheckedAllIndividual("sampleRejected_");
	if (tmpAllReject==true) {
		$(name="selectAllReject").checked = true;
	} else { 
		$(name="selectAllReject").checked = false;
	}
}

function /*void*/ acceptSample(element, groupingNumber ){
	$$(".accepted_" + groupingNumber).each( function(item){
		item.checked = element.checked;
	});
	$$(".rejected_" + groupingNumber).each( function(item){
		item.checked = false;
	});
	
	$("sampleRejected_" + groupingNumber).checked = false;
	$("selectAllReject").checked = false;
	var tmp=false;
	tmp = isCheckedAllIndividual("sampleAccepted_");
	if (tmp==true) {
		$(name="selectAllAccept").checked = true;
	} else { 
		$(name="selectAllAccept").checked = false;
	}
	/* if (element.checked) {
		tmp= isCheckedAll(".rejected_",$jq("[id^='sampleRejected_']"));
		if (tmp) {
        	$("selectAllReject").checked = true;
		} else {
			$("selectAllReject").checked = false;
		}
    } else {
    	tmp= isCheckedAll(".accepted_",$jq("[id^='sampleAccepted_']"));
    	if (tmp) {
        	$("selectAllAccept").checked = true;
    	} else {
    		$("selectAllAccept").checked = false;
    	}
    } */
}

function /*void*/ rejectSample(element, groupingNumber ){
	$$(".accepted_" + groupingNumber).each( function(item){
		item.checked = false;
	});
	$$(".rejected_" + groupingNumber).each( function(item){
		item.checked = element.checked;
	});
	
	$("sampleAccepted_" + groupingNumber).checked = false;
	$("selectAllAccept").checked = false;
	var tmp=false;
	tmp = isCheckedAllIndividual("sampleRejected_");
	if (tmp==true) {
		$(name="selectAllReject").checked = true;
	} else { 
		$(name="selectAllReject").checked = false;
	}
	/* if (element.checked) {
		tmp= isCheckedAll(".accepted_",$jq("[id^='sampleAccepted_']"));
		if (tmp) {
        	$("selectAllAccept").checked = true;
		} else {
			$("selectAllAccept").checked = false;
		}
    } else {
    	tmp= isCheckedAll(".rejected_",$jq("[id^='sampleRejected_']"));
    	if (tmp) {
        	$("selectAllReject").checked = true;
    	} else {
    		$("selectAllReject").checked = false;
    	}
    } */
}

function /*void*/ markUpdated(){
	$("saveButtonId").disabled = false;
	$("validateTransferButtonId").disabled = false;
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

function validateAndTransfer() {
	//alert("Validated and transfered!");
    if( !confirm("<%=StringUtil.getMessageForKey("validation.save.message")%>")){
        return;
    }
 	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	var form = window.document.forms[0];
	form.action = "ResultValidationTransfer.do" + '<%= "?type=" + testSection + "&test=" + testName %>';
	form.submit();
}

function savePage() {
    if( !confirm("<%=StringUtil.getMessageForKey("validation.save.message")%>")){
        return;
    }

    window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
	var form = window.document.forms[0];
	form.action = "ResultValidationSave.do" + '<%= "?type=" + testSection + "&test=" + testName %>';
	form.submit();
}
var sectionId="";
<%-- function submitTestSectionSelect(element) {
	sectionId = element.value;
	var testSectionNameIdHash = [];

	<%
		for( IdValuePair pair : (List<IdValuePair>) testSectionsByName){
			out.print( "testSectionNameIdHash[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
		}
	%>
	window.location.href = "ResultValidationSave.do?testSectionId=" + sectionId + "&test=&type=" + testSectionNameIdHash[sectionId] + "&receivedDate=" + receivedDate;
	//window.location.href = "ResultValidationSave.do?accessionNumber=1670000011";
} --%>

function /*void*/ dirtySearchInfo(e){ 
	var code = e ? e.which : window.event.keyCode;
	if( code != 13 ){
		newSearchInfo = true; 
	}
	setSearch();
}

function doShowTests() {
	newSearchInfo = false;
	var receivedDate = $("receivedDate").value;
	//get Id of selected section, test
	var sectionId = $("sectionId").value;
	var testNameId = $("testNameId").value;
	var testSectionNameIdHash = [];

	<%
		for( IdValuePair pair : (List<IdValuePair>) testSectionsByName){
			out.print( "testSectionNameIdHash[\'" + pair.getId()+ "\'] = \'" + pair.getValue() +"\';\n");
		}
	%>
	window.location.href = "ResultValidation.do?testSectionId="+sectionId + "&test=" + testNameId +"&type="+ testSectionNameIdHash[sectionId]+ "&receivedDate=" + receivedDate;
}

function toggleSelectAll( element ) {
    var index, item, checkboxes, matchedCheckboxes;

	if (element.id == "selectAllAccept" ) {
		checkboxes = $$(".accepted");
		checkboxes = [].concat.apply($$(".t_accepted"),checkboxes);
		matchedCheckboxes = $$(".rejected");
		matchedCheckboxes = [].concat.apply($$(".t_rejected"),matchedCheckboxes);
		
	} else if (element.id == "selectAllReject" ) {
		checkboxes = $$(".rejected");
		checkboxes=[].concat.apply($$(".t_rejected"),checkboxes);
		matchedCheckboxes = $$(".accepted");
		matchedCheckboxes = [].concat.apply($$(".t_accepted"),matchedCheckboxes);
	}

	if (element.checked == true ) {
		for (index = 0; index < checkboxes.length; ++index) {
			if (checkboxes[index].getAttribute("disabled") != "disabled") {
				  item = checkboxes[index];
				  item.checked = true;
			}
		}
		for (index = 0; index < matchedCheckboxes.length; ++index) {
			if (matchedCheckboxes[index].getAttribute("disabled") != "disabled") {
				  item = matchedCheckboxes[index];
				  item.checked = false;
			}
		}
		
	} else if (element.checked == false ) {
		for (index = 0; index < checkboxes.length; ++index) {
			if (checkboxes[index].getAttribute("disabled") != "disabled") {
				item = checkboxes[index];
			  	item.checked = false;
			}  
		}
	}
}

function updateLogValue(element, index ){
	var logField = $("log_" + index );

	if( logField ){
		var logValue = Math.baseLog(element.value).toFixed(2);

		if( isNaN(logValue) ){
			jQuery(logField).html("--");
		}else{
			jQuery(logField).html(logValue);
		}
	}
}

function trim(element, significantDigits){
    if( isNaN(significantDigits) || isNaN(element.value) ){
        return;
    }

    element.value = round(element.value, significantDigits);
}

function updateReflexChild( group){
 	var reflexGroup = $$(".reflexGroup_" + group);
	var childReflex = $$(".childReflex_" + group);
 	var i, childId, rowId, resultIds = "", values="", requestString = "";

 	if( childReflex ){
 		childId = childReflex[0].id.split("_")[1];
 		
		for( i = 0; i < reflexGroup.length; i++ ){
			if( childReflex[0] != reflexGroup[i]){
				rowId = reflexGroup[i].id.split("_")[1];
				resultIds += "," + $("resultIdValue_" + rowId).value;
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

function /*void*/ processTestReflexCD4Failure(){
	alert("failed");
}

function /*void*/ processTestReflexCD4Success(xhr)
{
	//alert( xhr.responseText );
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var childRow, value;

	if (message.firstChild.nodeValue == "valid"){
		childRow = formField.getElementsByTagName("childRow").item(0).childNodes[0].nodeValue;
		value = formField.getElementsByTagName("value").item(0).childNodes[0].nodeValue;
		
		if( value && value.length > 0){
			$("resultId_" + childRow).value = value;
		}
	}
}

function  /*void*/ processValidateEntryDateSuccess(xhr) {
	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
	var isValid = message == "<%=IActionConstants.VALID%>";

	//utilites.js
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
	        //ajax call from utilites.js
	    	isValidDate( date.value, processValidateEntryDateSuccess, date.id, dateRange );
	    }
    } catch (Exception) {
    	selectFieldErrorDisplay( false, $(date.id));
        setSampleFieldValidity( false, date.id );
        setSearch();
        return;
    }
}

//check date enter is number
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
	$("retrieveTestsID").disabled = !validToSearch;
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

//It is not clear why this is needed but what it prevents is the browser trying to do a submit on 'enter'  Other pages handle this
//differently.  Overrides formTemplate.jsp handleEnterEvent
function /*boolean*/ handleEnterEvent(){
	if( newSearchInfo ){
		doShowTests();
	}
	return false;
}

//Tien add: enable enter press to submit function
var pressEnter = false;
$jq(function() {
  $jq(document).keyup(function(evt) {
    if (evt.keyCode == 13) {
    	pressEnter = false;
    }
  }).keydown(function(evt) {
    	if (evt.keyCode == 13) {
    		pressEnter = true;
      		// press enter to enable submit
      		doShowTests();
    	}
  	});
}); 
//end

function validateResultValue(field, accessionNumber) {
	if (field != null) {
		var rowNumber = field.id.split("_");
		var resultId = $("resultIdValue_" + rowNumber[1]).value;
		validateResultLimit(field.id, resultId, field.value, accessionNumber, processValidateResultSuccess, processValidateResultFailure);
	} else {
		$jq("input[id^='resultId_']").each(function(){
			var rowNumber = this.id.split("_");
			var resultId = $("resultIdValue_" + rowNumber[1]).value;
			validateResultLimit(this.id, resultId, this.value, accessionNumber, processValidateResultSuccess, processValidateResultFailure);
	    });
	}
}

function processValidateResultFailure(){
	alert("Failed to validate result.");
}

function processValidateResultSuccess(xhr){
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);

	var splitMessage = message.firstChild.nodeValue.split("-");
	if (splitMessage[0] == "invalid") {
		var resultField = formField.firstChild.nodeValue;
		if (splitMessage[1] == "LOW") {
			$(resultField).style.color = "#0000ff";
		} else {
			$(resultField).style.color = "#ee0000";
		}
	} else {
		var resultField = formField.firstChild.nodeValue;
		$(resultField).style.color = "#5F5F5F";
	}
}

</script>

<% if( showTestSectionSelect ){ %>
<div id="searchDiv" class="colorFill"  >
<div id="PatientPage" class="colorFill" style="display:inline" >
<h2><bean:message key="sample.entry.search"/></h2>
	<table style="margin-left: auto; margin-right: auto;">
		<tr>
			<td>
				<%= StringUtil.getMessageForKey("workplan.unit.types") %><span class="requiredlabel">*</span>:&nbsp;
			</td>
			<td>			
				<html:select name='<%= formName %>' property="testSectionId" styleId="sectionId" style="margin-right:5px;"
				    onchange="dirtySearchInfo(event);updateTestSectionTestOptions('sectionId', 'testNameId');"
				    onblur="setSearch();">
					<app:optionsCollection name="<%=formName%>" property="testSections" label="value" value="id" />
				</html:select>
		   	</td>
		   
		   	<td>
				<%= StringUtil.getMessageForKey("test.testName") %><!-- <span class="requiredlabel">*</span> -->:&nbsp;
			</td>
			<td>
				<html:select name="<%=formName%>" property="selectedTestId" styleId="testNameId" onchange="dirtySearchInfo(event);" style="margin-right:5px;" >
					<app:optionsCollection name="<%=formName%>" property="testNames" label="value" value="id" />
				</html:select>
	   		</td>
		   	
		   	<td>
				<%= StringUtil.getMessageForKey("sample.receivedDate") %><span class="requiredlabel">*</span>:&nbsp;
			</td >
			<td>			
				<app:text name="<%=formName%>" styleId="receivedDate" onblur="checkValidEntryDate(this, 'past', true); makeDirty();" property="receivedDate" 
						  onkeyup="dirtySearchInfo(event);addDateSlashes(this, event);" style="margin-right:5px; width:100px;" maxlength="10" 
						  value='<%=request.getParameter("receivedDate")==null?"": request.getParameter("receivedDate").toString() %>' />
		   	</td>
		   	<td>
		   	    <html:button property="retrieveTestsButton" styleClass="btn btn-default" styleId="retrieveTestsID"  onclick="doShowTests();" disabled="false" style="margin-top: -9px!important;height:30px;margin-left:3px;" >
		           <%= StringUtil.getContextualMessageForKey("resultsentry.testsection.search") %>
	            </html:button>
		   	</td>
		</tr>
	<%-- 	<tr>
			<td width="50%" align="right" >
				<%= StringUtil.getMessageForKey("sample.receivedDate") %>
			</td>
			<td>			
				<app:text name="<%=formName%>" styleId="receivedDate" onblur="checkValidEntryDate(this, 'past');" property="receivedDate" onkeyup="addDateSlashes(this, event);" value='<%=request.getParameter("receivedDate")==null?"": request.getParameter("receivedDate").toString() %>' maxlength="10" />
		   	</td>
		</tr> --%>
	</table>
	<br/>

	<%-- <html:button property="retrieveTestsButton" styleId="retrieveTestsID"  onclick="doShowTests();" disabled="false" >
		<%= StringUtil.getContextualMessageForKey("resultsentry.accession.search") %>
	</html:button> --%>
	<h1>
		
	</h1>
</div>
</div>
	<% }%>
<logic:notEqual name="resultCount" value="0">
<div  style="width:100%" >
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
	
	<span style="float : right" >
	<span style="visibility: hidden" id="searchNotFound"><em><%= StringUtil.getMessageForKey("search.term.notFound") %></em></span>
	<%=StringUtil.getContextualMessageForKey("result.sample.id")%> : &nbsp;
	<input type="text"
	       id="labnoSearch"
	       maxlength='<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength())%>' />
	<input type="button" class="btn btn-default" onclick="pageSearch.doLabNoSearch($(labnoSearch))" value='<%= StringUtil.getMessageForKey("label.button.search") %>' style="margin-top: -9px!important;height:30px;margin-left:3px;">
	</span>
</div>
</logic:notEqual>
<html:hidden name="<%=formName%>"  property="testSection" value="<%=testSection%>" />
<html:hidden name="<%=formName%>"  property="testName" value="<%=testName%>" />
<logic:notEqual name="resultCount" value="0">
<Table style="width:100%" >
    <tr>
		<th colspan="3" style="background-color: white;width:15%;">
			<img src="./images/nonconforming.gif" /> = <%= StringUtil.getContextualMessageForKey("result.nonconforming.item")%>
		</th>
		<th style="text-align:center;width:3%;" style="background-color: white">&nbsp;
				<bean:message key="validation.accept.all" />
			<input type="checkbox"
				name="selectAllAccept"
				value="on"
				onclick="toggleSelectAll(this);"
				onchange="markUpdated(); makeDirty();"
				id="selectAllAccept"
				class="accepted acceptAll">
		</th>
		<th  style="text-align:center;width:3%;" style="background-color: white">&nbsp;
		<bean:message key="validation.reject.all" />
			<input type="checkbox"
					name="selectAllReject"
					value="on"
					onclick="toggleSelectAll(this);"
					onchange="markUpdated(); makeDirty();"
					id="selectAllReject"
					class=" rejected rejectAll">
		</th>
		<th style="background-color: white;width:5%;">&nbsp;</th>
  	</tr>
</Table>
</logic:notEqual>
<Table style="width:100%" >
	<logic:notEqual name="resultCount" value="0">
	<tr>
	    <td colspan="9"><hr/></td>
    </tr>    
	<tr>
		<th>
	  		<bean:message key="row.label"/>
		</th>
    	<th>
	  		<bean:message key="quick.entry.accession.number.CI"/>
		</th>
		<th>
	  		<bean:message key="sample.entry.project.testName"/>
		</th>
		<th>
			<bean:message key="analyzer.results.result"/>
		</th>
		<th>
	  		<bean:message key="validation.normal.range.unit"/>
		</th>
		<th>
	  		<bean:message key="validation.out.of.range.flag"/>
		</th>
		<th style="text-align:center">
			<bean:message key="validation.accept" />
		</th>
		<th style="text-align:center">
			<bean:message key="validation.reject" />
		</th>
		<th>
			<bean:message key="result.notes"/>
		</th>
  	</tr>

	<logic:iterate id="resultList" name="<%=formName%>"  property="resultList" indexId="index" type="AnalysisItem">
			<bean:define id="currentPage" name="<%=formName%>" property="paging.currentPage"/>
			 <% showAccessionNumber = !resultList.getAccessionNumber().equals(currentAccessionNumber);
				   if( showAccessionNumber ){
					currentAccessionNumber = resultList.getAccessionNumber();
					rowColorIndex++;}  %>
			<html:hidden name="resultList" property="sampleGroupingNumber" indexed="true" />
			<html:hidden name="resultList" property="noteId" indexed="true" />
			<html:hidden name="resultList" property="resultId"  indexed="true" styleId='<%="resultIdValue_" + index%>'/>
            <html:hidden name="resultList" property="hasQualifiedResult" indexed="true" styleId='<%="hasQualifiedResult_" + index %>' />

			<%  boolean enableRowFields = false;
				if ( resultList.getStatusUserSession().getHasRelease().equals("Y") ) {
				    enableRowFields = true;
				}
			%>
			<%
		    if (enableRowFields) {
		         
			   if( resultList.isMultipleResultForSample() && showAccessionNumber ){ 
			     showAccessionNumber = false; %>
			<tr class='<%=(rowColorIndex % 2 == 0) ? "evenRow" : "oddRow" %>'  >
				<td id="'<%="rowNum_" + index %>'" style="text-align:center;">
	      			<%= (index + 1) + ((Integer.parseInt(currentPage.toString()) - 1) * IActionConstants.VALIDATION_PAGING_SIZE) %>
	    		</td>
				<td class='<%= currentAccessionNumber %>' style="text-align:center;">
	      			<bean:write name="resultList" property="accessionNumber"/>
	    		</td>
	    		<td colspan="4"></td>
	    		<td style="text-align:center">
					<html:checkbox styleId='<%="sampleAccepted_" + resultList.getSampleGroupingNumber() %>'
								   name="resultList"
								   property="isAccepted"
								   styleClass="t_accepted"
								   indexed="true"
								   onchange="markUpdated(); makeDirty();" 
								   onclick='<%="acceptSample( this, \'" + resultList.getSampleGroupingNumber() + "\');" %>' 
								   disabled='<%=!enableRowFields%>' />
				</td>
				<td style="text-align:center">
					<html:checkbox styleId='<%="sampleRejected_" + resultList.getSampleGroupingNumber() %>'
								   name="resultList"
								   property="isRejected"
								   styleClass="t_rejected"
								   indexed="true"
								   onchange="markUpdated(); makeDirty();"
								   onclick='<%="rejectSample( this, \'" + resultList.getSampleGroupingNumber() + "\');" %>' 
								   disabled='<%=!enableRowFields%>' />
				</td>
				<td>&nbsp;</td>
			</tr>
			<% } %>
     		<tr id='<%="row_" + index %>' class='<%=(rowColorIndex % 2 == 0) ? "evenRow" : "oddRow" %>'  >
				<td id="'<%="rowNum_" + index %>'" style="text-align:center;">
	      			<%= (index + 1) + ((Integer.parseInt(currentPage.toString()) - 1) * IActionConstants.VALIDATION_PAGING_SIZE) %>
	    		</td>
				<% if( showAccessionNumber ){%>
	    		<td class='<%= currentAccessionNumber %>' style="text-align:center;" >
	      			<bean:write name="resultList" property="accessionNumber"/>
	    		</td>
	    		<% }else{ %>
	    			<td></td>
	    		<% } %>
				<td style="text-align:center;">
					<bean:write name="resultList" property="testName"/>
					<% if( resultList.isNonconforming()){ %>
						<img src="./images/nonconforming.gif" />
					<% } %>
				</td>
				<td>
					<% if( ResultType.NUMERIC.matches(resultList.getResultType())){%>
						<% if( resultList.isReadOnly() ){%>
							<div
								class='results-readonly <%= (resultList.getIsHighlighted() ? "invalidHighlight " : " ") + (resultList.isReflexGroup() ? "reflexGroup_" + resultList.getSampleGroupingNumber()  : "")  +  
							              (resultList.isChildReflex() ? " childReflex_" + resultList.getSampleGroupingNumber(): "") %> '
							    id='<%= "resultId_" + index %>'
                            	disabled="disabled"
								name='<%="resultList[" + index + "].result" %>' >
							<%= resultList.getResult() %>
							</div>
						<% }else{ %>
	    					<input type="text" 
					           name='<%="resultList[" + index + "].result" %>' 
					           size="6" 
					           value='<%= resultList.getResult() %>'
					           <%= resultList.isWithInRange() ? "style='color:#5F5F5F;font-weight:bold;'" : (resultList.isOutOfRangeLow() ? "style='color:#0000ff;font-weight:bold;'" : "style='color:#ee0000;font-weight:bold;'") %>
			           		   onblur="validateResultValue(this, <%= currentAccessionNumber %>);" 
					           <%=!enableRowFields ? "disabled='disabled'" : ""%>
					           id='<%= "resultId_" + index %>'
							   class='<%= (resultList.getIsHighlighted() ? "invalidHighlight " : " ") + (resultList.isReflexGroup() ? "reflexGroup_" + resultList.getSampleGroupingNumber()  : "")  +  
							              (resultList.isChildReflex() ? " childReflex_" + resultList.getSampleGroupingNumber(): "") %> ' 
							   style="font-weight:bold;"
							   onchange='<%= "markUpdated(); makeDirty(); updateLogValue(this, " + index + "); trim(this, " + resultList.getSignificantDigits() + ");" +
								                (resultList.isReflexGroup() && !resultList.isChildReflex() ? "updateReflexChild(" + resultList.getSampleGroupingNumber()  +  " ); " : "")  %>'/>
	    				<% } %>
						<%-- <bean:write name="resultList" property="units"/> --%>
					<% }else if( ResultType.DICTIONARY.matches(resultList.getResultType())){ %>
						<select name="<%="resultList[" + index + "].result" %>" 
						        id='<%="resultId_" + index%>'
					           <%=!enableRowFields ? "disabled='disabled'" : ""%>
						        onchange= '<%= "markUpdated(); makeDirty();" +
						         (resultList.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + resultList.getQualifiedDictionaryId() : "" ) + ");"  %>' 
						         <%=!enableRowFields ? "disabled='disabled'" : ""%>  >
								<logic:iterate id="optionValue" name="resultList" property="dictionaryResults" type="IdValuePair" >
									<option value='<%=optionValue.getId()%>'  <%if(optionValue.getId().equals(resultList.getResult())) out.print("selected"); %>  >
										<bean:write name="optionValue" property="value"/>
									</option>
								</logic:iterate>
						</select>
						<input type="text" 
			           			name='<%="resultList[" + index + "].qualifiedResultValue" %>' 
			           			value='<%= resultList.getQualifiedResultValue() %>' 
			           			id='<%= "qualifiedDict_" + index %>'
			           			style = '<%= "display:" + (resultList.isHasQualifiedResult() ? "inline" : "none") %>'
					   			<%= resultList.isReadOnly() ? "disabled='disabled'" : ""%>
					   			<%=!enableRowFields ? "disabled='disabled'" : ""%> />
                    <%-- <bean:write name="resultList" property="units"/> --%>
					<% }else  if( ResultType.MULTISELECT.matches(resultList.getResultType())){%>
                    <!-- multiple results -->
                    <select name="<%="resultList[" + index + "].multiSelectResultValues" %>"
                            id='<%="resultId_" + index + "_0"%>'
                            multiple="multiple"
                            <%=resultList.isReadOnly()? "disabled=\'disabled\'" : "" %>
                            title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
                            onchange='<%="markUpdated(" + index + "); "  +
						               ((!GenericValidator.isBlankOrNull(resultList.getMultiSelectResultValues())) ? "showNote( " + index + ");" : "") +
						               (resultList.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + resultList.getQualifiedDictionaryId() + ", \"M\" );" :"")%>'
			                <%=!enableRowFields ? "disabled='disabled'" : ""%> >
                        <logic:iterate id="optionValue" name="resultList" property="dictionaryResults" type="IdValuePair" >
                            <option value='<%=optionValue.getId()%>'
                                    <%if(StringUtil.textInCommaSeperatedValues(optionValue.getId(), resultList.getMultiSelectResultValues())) out.print("selected"); %>  >
                                <bean:write name="optionValue" property="value"/>
                            </option>
                        </logic:iterate>
                    </select>
                    <html:hidden name="resultList" property="multiSelectResultValues" indexed="true" styleId='<%="multiresultId_" + index%>' styleClass="multiSelectValues"  />
                    <input type="text"
                           name='<%="resultList[" + index + "].qualifiedResultValue" %>'
                           value='<%= resultList.getQualifiedResultValue() %>'
                           id='<%= "qualifiedDict_" + index %>'
                           style = '<%= "display:" + ( resultList.isHasQualifiedResult() ? "inline" : "none") %>'
                            <%= resultList.isReadOnly() ? "disabled='disabled'" : ""%>
                           onchange='<%="markUpdated(" + index + ");" %>'
                           <%=!enableRowFields ? "disabled='disabled'" : ""%>
                            />
                    <%-- <bean:write name="resultList" property="units"/> --%>
                    <%}else  if( ResultType.CASCADING_MULTISELECT.matches(resultList.getResultType())){%>
                    <!-- cascading multiple results -->
                    <div id='<%="cascadingMulti_" + index + "_0"%>' class='<%="cascadingMulti_" + index %>' >
                    <input type="hidden" id='<%="divCount_" + index %>' value="0" >
                    <select name="<%="resultList[" + index + "].multiSelectResultValues" %>"
                            id='<%="resultId_" + index + "_0"%>'
                            multiple="multiple"
                            <%=resultList.isReadOnly()? "disabled=\'disabled\'" : "" %>
                            title='<%= StringUtil.getMessageForKey("result.multiple_select")%>'
                            onchange='<%="markUpdated(" + index + "); "  +
						               ((!GenericValidator.isBlankOrNull(resultList.getMultiSelectResultValues())) ? "showNote( " + index + ");" : "") +
						               (resultList.getQualifiedDictionaryId() != null ? "showQuanitiy( this, "+ index + ", " + resultList.getQualifiedDictionaryId() + ", \"M\" );" :"")%>' 
						    <%=!enableRowFields ? "disabled='disabled'" : ""%> >
                        <logic:iterate id="optionValue" name="resultList" property="dictionaryResults" type="IdValuePair" >
                            <option value='<%=optionValue.getId()%>' >
                                <bean:write name="optionValue" property="value"/>
                            </option>
                        </logic:iterate>
                    </select>
                        <input class='<%="addMultiSelect" + index%>' type="button" value="+" onclick='<%="addNewMultiSelect(" + index + ", this);showNewNote( " + index + ");"%>'/>
                        <input class='<%="removeMultiSelect" + index%>' type="button" value="-" onclick='<%="removeMultiSelect(\"target\");showNewNote( " + index + ");"%>' style="display: none" />
                        <html:hidden name="resultList" property="multiSelectResultValues" indexed="true" styleId='<%="multiresultId_" + index%>'  styleClass="multiSelectValues" />
                    <input type="text"
                           name='<%="resultList[" + index + "].qualifiedResultValue" %>'
                           value='<%= resultList.getQualifiedResultValue() %>'
                           id='<%= "qualifiedDict_" + index %>'
                           style = '<%= "display:" + ( resultList.isHasQualifiedResult() ? "inline" : "none") %>'
                            <%= resultList.isReadOnly() ? "disabled='disabled'" : ""%>
                           onchange='<%="markUpdated(" + index + ");" %>'
                           disabled='<%=!enableRowFields%>'
                            />
                    <%-- <bean:write name="resultList" property="units"/> --%>
                     </div>
                    <% }else if( ResultType.ALPHA.matches(resultList.getResultType())){%>
                    <app:text name="resultList"
                              indexed="true"
                              property="result"
                              size="6"
                              allowEdits='<%= !resultList.isReadOnly() %>'
                              styleId='<%="resultId_" + index %>'
                              onchange='<%="markUpdated(); makeDirty(); updateLogValue(this, " + index + ");" %>'
                              disabled='<%=!enableRowFields%>' />
                    <%-- <bean:write name="resultList" property="units"/> --%>

                    <%}else if( ResultType.REMARK.matches(resultList.getResultType())){ %>
						<app:textarea name="resultList"
								  indexed="true"
								  property="result"
								  rows="2"
								  allowEdits='<%= !resultList.isReadOnly() %>'
								  styleId='<%="resultId_" + index %>'
								  onkeyup='value = value.substr(0, 200); markUpdated();'
					           	  disabled='<%=!enableRowFields%>'
								  />
						<%-- <bean:write name="resultList" property="units"/> --%>
			 			<br/>200 char max
					<%}%>
					<% if(resultList.isDisplayResultAsLog()){ %>
						<br/>
						<div id='<%= "log_" + index %>'
								class='results-readonly'>
								<% try{
												Double value = Math.log10(Double.parseDouble(resultList.getResult()));
												DecimalFormat twoDForm = new DecimalFormat("##.##");
												out.print(Double.valueOf(twoDForm.format(value)));
												}catch(Exception e){
													out.print("--");} %>		
						</div> log
					<% } %>
				</td>
				<td style="text-align:center;">
					<bean:write name="resultList" property="units"/>
				</td>
				<td style="text-align:center;">
					<% if( !resultList.isWithInRange()){ 
							if (resultList.isOutOfRangeLow()) { %>
								<%= IActionConstants.OUT_OF_RANGE_LOW %>
					<%		} else { %>
								<%= IActionConstants.OUT_OF_RANGE_HIGH %>
					<% 		}
					 } %>
				</td>
				<% if(resultList.isShowAcceptReject()){ %>
				<td style="text-align:center">
					<html:checkbox styleId='<%="accepted_" + index %>'
								   name="resultList"
								   property="isAccepted"
								   styleClass='<%= "accepted accepted_" + resultList.getSampleGroupingNumber() %>'
								   indexed="true"
								   onchange="markUpdated(); makeDirty();"
								   onclick='<%="enableDisableCheckboxes(\'rejected_" + index + "\', \'" + resultList.getSampleGroupingNumber() + "\');" %>'
								   disabled='<%=!enableRowFields%>' />
				</td>
				<td style="text-align:center">
					<html:checkbox styleId='<%="rejected_" + index %>'
								   name="resultList"
								   property="isRejected"
								   styleClass='<%= "rejected rejected_" + resultList.getSampleGroupingNumber() %>'
								   indexed="true"
								   onchange="markUpdated(); makeDirty();"
								   onclick='<%="enableDisableCheckboxes(\'accepted_" + index + "\', \'" + resultList.getSampleGroupingNumber() + "\');" %>' 
								   disabled='<%=!enableRowFields%>' />
				</td>
				<% }else{ %>
				<td><bean:message key="label.computed"/></td><td><bean:message key="label.computed"/></td>
				<% } %>
				<td style="text-align:center">
					<% if( !resultList.isReadOnly()){ %>
				    	<logic:empty name="resultList" property="note">
						 	<img src="./images/note-add.gif"
						 	     onclick='<%= "showHideNotes( " + index + ", this);" %>'
						 	     id='<%="showHideButton_" + index %>'
						 	     <%=!enableRowFields ? "disabled=true" : ""%>
						    />
						 </logic:empty>
						 <logic:notEmpty name="resultList" property="note">
						 	<img src="./images/note-edit.gif"
						 	     onclick='<%= "showHideNotes( " + index + ", this);" %>'
						 	     id='<%="showHideButton_" + index %>'
						 	     <%=!enableRowFields ? "disabled=true" : ""%>
						    />
						 </logic:notEmpty>
                    <input type="hidden" value="hidden" id='<%="hideShow_" + index %>' >
					<% } %>
				</td>
      		</tr>
      		<logic:notEmpty name="resultList" property="pastNotes">
			<tr  >
				<td colspan="2" style="text-align:right;vertical-align:top"><bean:message key="label.prior.note" />: </td>
				<td colspan="6" style="text-align:left">
				<%= resultList.getPastNotes() %>
				</td>
			</tr>
			</logic:notEmpty>
      		<tr id='<%="noteRow_" + index %>'
				style="display: none;">
				<td colspan="2" style="text-align:right;vertical-align:top;"><bean:message key="note.note"/>:</td>
				<td colspan="6" style="text-align:left" >
					<html:textarea styleId='<%="note_" + index %>'
								   onchange='<%="markUpdated(" + index + ");"%>'
							   	   name="resultList"
					           	   property="note"
					           	   indexed="true"
					           	   cols="100"
					           	   rows="6"
					           	   style="resize:none;"
					           	   disabled='<%=!enableRowFields%>' />
				</td>
			</tr>
		<% } %>
  	</logic:iterate>
	<tr>
	    <td colspan="8"><hr/></td>
    </tr>
  	</logic:notEqual>
  	
	<logic:equal  name="<%=formName %>" property="displayTestSections" value="true">
		<logic:equal name="resultCount"  value="0">
					<h2><%= StringUtil.getContextualMessageForKey("result.noTestsFound") %></h2>
		</logic:equal>
	</logic:equal>
  	
</Table>

   <logic:notEqual name="resultCount" value="0">
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
	