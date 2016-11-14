<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ page
	import="us.mn.state.health.lims.common.action.IActionConstants,
                 us.mn.state.health.lims.common.formfields.FormFields,
                 us.mn.state.health.lims.common.formfields.FormFields.Field,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
                 us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
                 us.mn.state.health.lims.common.util.DateUtil,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.common.util.SystemConfiguration,
                 us.mn.state.health.lims.common.util.Versioning"%>
<%@ page
	import="us.mn.state.health.lims.patient.action.bean.PatientManagementInfo"%>


<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-tiles" prefix="tiles"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<script type="text/javascript"
	src="scripts/ajaxCalls.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="<%=basePath%>scripts/utilities.js?ver=<%=Versioning.getBuildNumber()%>"></script>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="patientProperties" name='<%=formName%>'
	property='patientProperties' type="PatientManagementInfo" />


<%!String basePath = "";
	boolean supportSTNumber = true;
	boolean supportAKA = true;
	boolean supportMothersName = true;
	boolean supportPatientType = true;
	boolean supportInsurance = true;
	boolean supportSubjectNumber = true;
	boolean subjectNumberRequired = true;
	boolean supportNationalID = true;
	boolean supportOccupation = true;
	boolean supportEmployerName = true;
	boolean supportCommune = true;
	boolean supportAddressDepartment = true;
	boolean supportMothersInitial = true;
	boolean patientRequired = true;
	boolean patientIDRequired = false;
	boolean patientNamesRequired = true;
	boolean patientAgeRequired = true;
	boolean patientGenderRequired = true;
	boolean useSingleNameField = false;
	boolean useCompactLayout = false;
	boolean useCityPicklist = false;
	boolean linkDistrictsToCities = false;
	boolean supportAddressWard = false;
	boolean supportAddressDistrict = false;
	boolean supportExternalID = false;
	boolean supportPatientClinicalDept = true;
	boolean supportPatientDiagnosis = false;
	boolean supportPatientBedNumber = false;
	boolean supportPatientRoomNumber = false;
	boolean supportPatientChartNumber = false;
	boolean patientAgeUnitRequired = true;%>
<%
    String path = request.getContextPath();
			basePath = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort() + path + "/";
			supportSTNumber = FormFields.getInstance().useField(Field.StNumber);
			supportAKA = FormFields.getInstance().useField(Field.AKA);
			supportMothersName = FormFields.getInstance().useField(
					Field.MothersName);
			supportPatientType = FormFields.getInstance().useField(
					Field.PatientType);
			supportInsurance = FormFields.getInstance().useField(
					Field.InsuranceNumber);
			supportSubjectNumber = FormFields.getInstance().useField(
					Field.SubjectNumber);
			subjectNumberRequired = FormFields.getInstance().useField(
					Field.SubjectNumberRequired);
			supportNationalID = FormFields.getInstance().useField(
					Field.NationalID);
			supportOccupation = FormFields.getInstance().useField(
					Field.Occupation);
			supportEmployerName = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_EMPLOYER_NAME);
			supportCommune = FormFields.getInstance().useField(
					Field.ADDRESS_COMMUNE);
			supportMothersInitial = FormFields.getInstance().useField(
					Field.MotherInitial);
			supportAddressDepartment = FormFields.getInstance().useField(
					Field.ADDRESS_DEPARTMENT);

			if ("SampleConfirmationEntryForm".equals(formName)) {
				patientIDRequired = FormFields.getInstance().useField(
						Field.PatientIDRequired_SampleConfirmation);
				patientRequired = FormFields.getInstance().useField(
						Field.PatientRequired_SampleConfirmation);
				patientAgeRequired = false;
				patientGenderRequired = false;
				patientAgeUnitRequired = false;
			} else {
				patientIDRequired = FormFields.getInstance().useField(
						Field.PatientIDRequired);
				patientRequired = FormFields.getInstance().useField(
						Field.PatientRequired);
				patientAgeRequired = true;
				patientGenderRequired = true;
				patientAgeUnitRequired = true;
			}

			patientNamesRequired = FormFields.getInstance().useField(
					Field.PatientNameRequired);
			useSingleNameField = FormFields.getInstance().useField(
					Field.SINGLE_NAME_FIELD);

			useCompactLayout = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_COMPACT_LAYOUT);
			useCityPicklist = FormFields.getInstance().useField(
					Field.PATIENT_CITY_PICKLIST);
			linkDistrictsToCities = FormFields.getInstance().useField(
					Field.LINK_DISTRICTS_TO_CITIES);
			supportAddressWard = FormFields.getInstance().useField(
					Field.ADDRESS_WARD);
			supportAddressDistrict = FormFields.getInstance().useField(
					Field.ADDRESS_DISTRICT);
			supportExternalID = FormFields.getInstance().useField(
					Field.EXTERNAL_ID);
			supportPatientClinicalDept = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_CLINICAL_DEPT);
			/*  supportPatientDiagnosis = FormFields.getInstance().useField(Field.SAMPLE_ENTRY_PATIENT_DIAGNOSIS); */
			supportPatientDiagnosis = true;
			supportPatientBedNumber = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_BED_NUMBER);
			supportPatientRoomNumber = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_ROOM_NUMBER);
			supportPatientChartNumber = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_MEDICAL_RECORD_CHART_NUMBER);
			// Dung add
			useSingleNameField = FormFields.getInstance().useField(
					Field.SINGLE_NAME_FIELD);
%>

<%
    if (useCityPicklist && linkDistrictsToCities) {
%>
<script type="text/javascript"
	src="scripts/cityDistrictMap.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<%
    }
%>

<script type="text/javascript">

var $jq = jQuery.noConflict();

document.onreadystatechange = function(){
    if(document.readyState === 'complete'){
    	var value = $jq("#receivedTime").val();
    	var currentTime = new Date();
    	var hour = currentTime.getHours();
    	var date = currentTime.getDate();
    	var month = currentTime.getMonth() + 1;
    	var year = currentTime.getYear() + 1900;
    	
    	if (date.toString().length == 1) {
    		date = '0' + date;
    	}
    	
    	if (month.toString().length == 1) {
    		month = '0' + month;
    	}
    	
    	if (hour.toString().length == 1) {
    		hour = '0' + hour;
    	}
    	
    	var minute = currentTime.getMinutes();
    	if (minute.toString().length == 1) {
    		minute = '0' + minute;
    	}
    	
    	if (value == '' || value == '00:00') {
    		$jq("#receivedTime").val(hour + ':' + minute);
    		$jq("#receivedDateForDisplay").val(date + '/' + month + '/' + year);
    	}
    	
    	var ageValue = $jq("#ageId").val();
    	
    	if (ageValue == '') {
    		$jq("#patientAgeUnitID").val("Năm");
    	}
    	
    	
    }
}

/*the prefix pt_ is being used for scoping.  Since this is being used as a tile there may be collisions with other
  tiles with simular names.  Only those elements that may cause confusion are being tagged, and we know which ones will collide
  because we can predicte the future */
  var useModalSampleEntry = true;
var supportSTNumber = <%=supportSTNumber%>;
var supportAKA = <%=supportAKA%>;
var supportMothersName = <%=supportMothersName%>;
var supportPatientType = <%=supportPatientType%>;
var supportInsurance = <%=supportInsurance%>;
var supportPatientChartNumber = <%=supportPatientChartNumber%>;
var supportSubjectNumber = <%=supportSubjectNumber%>;
var subjectNumberRequired = <%=subjectNumberRequired%>;
var supportNationalID = <%=supportNationalID%>;
var supportMothersInitial = <%=supportMothersInitial%>;
var supportCommune = <%=supportCommune%>;
var supportCity =true;
<%-- var supportCity = <%= FormFields.getInstance().useField(Field.ADDRESS_VILLAGE) %>; --%>
var supportOccupation = <%=supportOccupation%>;
var supportEmployerName = <%=supportEmployerName%>;
var supportAddressDepartment = <%=supportAddressDepartment%>;
var patientRequired = <%=patientRequired%>;
var patientIDRequired = <%=patientIDRequired%>;
var patientNamesRequired = <%=patientNamesRequired%>;
var patientAgeRequired = <%=patientAgeRequired%>;
var patientGenderRequired = <%=patientGenderRequired%>;
var supportEducation = <%=FormFields.getInstance().useField(Field.PatientEducation)%>;
var supportPatientNationality = <%=FormFields.getInstance().useField(
					Field.PatientNationality)%>;
var supportMaritialStatus = <%=FormFields.getInstance().useField(
					Field.PatientMarriageStatus)%>;
var supportHealthRegion = <%=FormFields.getInstance().useField(
					Field.PatientHealthRegion)%>;
var supportHealthDistrict = <%=FormFields.getInstance().useField(
					Field.PatientHealthDistrict)%>;
var useSingleNameField = <%=useSingleNameField%>;
var linkDistrictsToCities = <%=linkDistrictsToCities%>;
var supportAddressWard = <%=supportAddressWard%>;
var supportAddressDistrict = <%=supportAddressDistrict%>;
var originalDistrictList = null;
var supportExternalID = <%=supportExternalID%>;
var supportPatientClinicalDept = <%= supportPatientClinicalDept %>;
//var supportPatientClinicalDept = true;
var supportPatientBedNumber = false;
var supportPatientRoomNumber = false;
var pt_invalidElements = [];
var pt_requiredFields = [];

var patientAgeUnitRequired =<%=patientAgeUnitRequired%>;
/*if( patientAgeRequired){
    pt_requiredFields.push("dateOfBirthID");
}*/

if( patientGenderRequired){
    pt_requiredFields.push("genderID");
}
if( patientNamesRequired){
    pt_requiredFields.push("firstNameID"); 
    if (!useSingleNameField) pt_requiredFields.push("lastNameID"); 
}
if (patientAgeUnitRequired){
	pt_requiredFields.push("patientAgeUnitID");
}

var pt_requiredOneOfFields = [];

/*if( patientIDRequired){
    pt_requiredOneOfFields.push("nationalID") ;
    pt_requiredOneOfFields.push("patientGUID_ID") ;
    if (supportSTNumber) {
        pt_requiredOneOfFields.push("ST_ID");
    } else if (supportSubjectNumber && subjectNumberRequired){
        pt_requiredOneOfFields = new Array("subjectNumberID");
    } else if (supportExternalID){
        pt_requiredOneOfFields.push("externalID");
    }
}*/

var updateStatus = "add";
var patientInfoChangeListeners = [];
var dirty = false;

function  /*bool*/ pt_isFieldValid(fieldname)
{
    return pt_invalidElements.indexOf(fieldname) == -1;
}


function  /*void*/ pt_setFieldInvalid(field)
{
    if( pt_invalidElements.indexOf(field) == -1 )
    {
        pt_invalidElements.push(field);
    }
}

function  /*void*/ pt_setFieldValid(field)
{
    var removeIndex = pt_invalidElements.indexOf( field );
    if( removeIndex != -1 )
    {
        for( var i = removeIndex + 1; i < pt_invalidElements.length; i++ )
        {
            pt_invalidElements[i - 1] = pt_invalidElements[i];
        }

        pt_invalidElements.length--;
    }
}

function  /*void*/ pt_setFieldValidity( valid, fieldName ){
    if (useModalSampleEntry)
        fieldName = $($jq('[name="' + fieldName + '"]').attr('id'));

    if( valid ){
        pt_setFieldValid( fieldName );
    }else{
        pt_setFieldInvalid( fieldName );
    }
}
function /*boolean*/ patientFormValid(){
    if ( patientRequired || !pt_patientRequiredFieldsAllEmpty()) {
        return pt_invalidElements.length == 0 && pt_requiredFieldsValid();
    } else {
        return true;
    }
}

function pt_patientRequiredFieldsAllEmpty() {
    var i;

    for(i = 0; i < pt_requiredFields.length; ++i ){
        if( !$(pt_requiredFields[i]).value.blank() ){
            return false;
        }
    }
    
    for(i = 0; i < pt_requiredOneOfFields.length; ++i ){
        if( !($(pt_requiredOneOfFields[i]).value.blank()) ){
            return false;
        }
    }
    return true;
}

function /*void*/ pt_setSave()
{
    if( window.setSave ){
        setSave();
    }else{
        $("saveButtonId").disabled = !patientFormValid();
    }
}

function /*boolean*/ pt_isSaveEnabled()
{
    return !$("saveButtonId").disabled;
}

function  /*void*/ setMyCancelAction(form, action, validate, parameters)
{

    //first turn off any further validation
    setAction(window.document.forms[0], 'Cancel', 'no', '');
}

function  /*void*/ pt_requiredFieldsValid(){
    var i;
    for( i = 0; i < pt_requiredFields.length; ++i ){
        if( $(pt_requiredFields[i]).value.blank() ){
            return false;
        }
    }

    if( pt_requiredOneOfFields.length == 0){
        return true;
    }

    for( i = 0; i < pt_requiredOneOfFields.length; ++i ){
        if( !($(pt_requiredOneOfFields[i]).value.blank()) ){
            return true;
        }
    }

    return false;
}

function  /*string*/ pt_requiredFieldsValidMessage()
{
    var hasError = false;
    var returnMessage = "";
    var oneOfMembers = "";
    var requiredField = "";
    var i;

    for( i = 0; i < pt_requiredFields.length; ++i ){
        if( $(pt_requiredFields[i]).value.blank() ){
            hasError = true;
            requiredField += " : " + pt_requiredFields[i];
        }
    }

    for( i = 0; i < pt_requiredOneOfFields.length; ++i ){
        if( !pt_requiredOneOfFields[i].value.blank() ){
            oneOfFound = true;
            break;
        }

        oneOfMemebers += " : " + pt_requiredOneOfFields[i];
    }

    if( !oneOFound ){
        hasError = true;
    }

    if( hasError )
    {
        if( !requiredField.blank() ){
            returnMessage = "Please enter the following patient values  " + requiredField;
        }
        if( !oneOfMembers.blank() ){
            returnMessage = "One of the following must have a value " + onOfMemebers;
        }
    }else{
        returnMessage = "valid";
    }

    return returnMessage;
}

function  /*void*/ processValidateDateSuccess(xhr){

    //alert(xhr.responseText);
    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;

    var isValid = message == "<%=IActionConstants.VALID%>";

    setValidIndicaterOnField(isValid, formField);
    pt_setFieldValidity( isValid, formField );


    if( isValid ){
        <%if (!FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_AGE_VALUE_AND_UNITS)) {%>
        updatePatientAge( $("dateOfBirthID") );
        <%}%>
    }else if( message == "<%=IActionConstants.INVALID_TO_LARGE%>" ){
        alert( '<bean:message key="error.date.birthInPast" />' );
    }
    
    pt_setSave();
}

function  /*void*/ checkValidAgeDate(dateElement)
{
    if( dateElement && !dateElement.value.blank() ){
		if(dateElement.value.indexOf("/") > 0 && dateElement.value.length <= 6){
			var dateSplit = dateElement.value.split("/");
			var newDate = new Date(dateSplit[1] + "/" + dateSplit[0]);
			if(newDate != "Invalid Date"){
				var yyyy = new Date().getFullYear();
				var mm = (newDate.getMonth()+1).toString(); // getMonth() is zero-based
				var dd  = newDate.getDate().toString();
				dateElement.value = (dd[1]?dd:"0"+dd[0]) + "/" + (mm[1]?mm:"0"+mm[0]) + "/" + yyyy;
			}
		}
        isValidDate( dateElement.value, processValidateDateSuccess, dateElement.name, "past" );
    }else{
        setValidIndicaterOnField(dateElement.value.blank(), dateElement.name);
        pt_setFieldValidity( dateElement.value.blank(),  dateElement.name);
        pt_setSave();
        <%-- <% if (!FormFields.getInstance().useField(Field.SAMPLE_ENTRY_PATIENT_AGE_VALUE_AND_UNITS)) { %>
        $("ageId").value = null;
        <% } %> --%>
    }
}


function  /*void*/ updatePatientAge( DOB )
{
    var date = String( DOB.value );

    var datePattern = '<%=SystemConfiguration.getInstance()
					.getPatternForDateLocale()%>';
    var splitPattern = datePattern.split("/");
    var dayIndex = 0;
    var monthIndex = 1;
    var yearIndex = 2;

    for( var i = 0; i < 3; i++ ){
        if(splitPattern[i] == "DD"){
            dayIndex = i;
        }else if(splitPattern[i] == "MM" ){
            monthIndex = i;
        }else if(splitPattern[i] == "YYYY" ){
            yearIndex = i;
        }
    }


    var splitDOB = date.split("/");
    var monthDOB = splitDOB[monthIndex];
    var dayDOB = splitDOB[dayIndex];
    var yearDOB = splitDOB[yearIndex];

    var today = new Date();

    var adjustment = 0;

    if( !monthDOB.match( /^\d+$/ ) ){
        monthDOB = "01";
    }

    if( !dayDOB.match( /^\d+$/ ) ){
        dayDOB = "01";
    }

    //months start at 0, January is month 0
    var monthToday = today.getMonth() + 1;

    if( monthToday < monthDOB ||
        (monthToday == monthDOB && today.getDate() < dayDOB  ))
        {
            adjustment = -1;
        }

    var calculatedAge = today.getFullYear() - yearDOB + adjustment;

    var age = document.getElementById("age");
    age.value = calculatedAge;

    setValidIndicaterOnField( true, $("age").name);
    pt_setFieldValid( $("age").name );
}

function /*void*/ handleAgeChange( age )
{
    if( pt_checkValidAge( age ) )
    {
        pt_updateDOB( age );
        setValidIndicaterOnField( true, $("dateOfBirthID").name);
        pt_setFieldValid( $("dateOfBirthID").name );
    }

    pt_setSave();
}

function  /*bool*/ pt_checkValidAge( age )
{
    var valid = age.value.blank();

    if( !valid ){
        var regEx = new RegExp("^\\s*\\d{1,2}\\s*$");
        valid =  regEx.test(age.value);
    }

    setValidIndicaterOnField(  valid , age.name );
    pt_setFieldValidity( valid, age.name );

    return valid;
}

function  /*void*/ pt_updateDOB( age )
{
    if( age.value.blank() ){
        $("dateOfBirthID").value = null;
    }else{
        var today = new Date();

        var day = "xx";
        var month = "xx";
        var year = today.getFullYear() - age.value;

        var datePattern = '<%=SystemConfiguration.getInstance()
					.getPatternForDateLocale()%>';
        var splitPattern = datePattern.split("/");

        var DOB = "";

        for( var i = 0; i < 3; i++ ){
            if(splitPattern[i] == "DD"){
                DOB = DOB + day + "/";
            }else if(splitPattern[i] == "MM" ){
                DOB = DOB + month + "/";
            }else if(splitPattern[i] == "YYYY" ){
                DOB = DOB + year + "/";
            }
        }

        $("dateOfBirthID").value = DOB.substring(0, DOB.length - 1 );
    }
}

function  /*void*/ getDetailedPatientInfo()
{
    $("patientPK_ID").value = patientSelectID;

    new Ajax.Request (
                       'ajaxQueryXML',  //url
                        {//options
                          method: 'get', //http method
                          parameters: "provider=PatientSearchPopulateProvider&personKey=" + patientSelectID,
                          onSuccess:  processSearchPopulateSuccess,
                          onFailure:  processSearchPopulateFailure
                         }
                          );
}

function  /*void*/ setUpdateStatus( newStatus )
{
    if( updateStatus != newStatus )
    {
        updateStatus = newStatus;
        document.getElementById("processingStatus").value = newStatus;
    }
}

function  /*void*/ processSearchPopulateSuccess(xhr)
{

    setUpdateStatus("noAction");
    //alert(xhr.responseText);
    var response = xhr.responseXML.getElementsByTagName("formfield").item(0);

    var nationalIDValue = getXMLValue(response, "nationalID");
    var STValue = getXMLValue(response, "ST_ID");
    var subjectNumberValue = getXMLValue(response, "subjectNumber");
    var lastNameValue = getXMLValue(response, "lastName");
    var firstNameValue = getXMLValue(response, "firstName");
    var akaValue = getXMLValue(response, "aka");
    var motherValue = getXMLValue(response, "mother");
    var motherInitialValue = getXMLValue(response, "motherInitial");
    var streetValue = getXMLValue(response, "street");
    var cityValue = getXMLValue(response, "city");
    var communeValue = getXMLValue(response, "commune");
    var dobValue = getXMLValue(response, "dob");
    var genderValue = getSelectIndexFor( "genderID", getXMLValue(response, "gender"));
    var patientTypeValue = getSelectIndexFor( "patientTypeID", getXMLValue(response, "patientType"));
    var insuranceValue = getXMLValue(response, "insurance");
    var chartNumberValue = getXMLValue(response, "chartNumber");
    var occupationValue = getXMLValue(response, "occupation");
    var employerNameValue = getXMLValue(response, "employerName");
    var patientUpdatedValue = getXMLValue(response, "patientUpdated");
    var personUpdatedValue = getXMLValue(response, "personUpdated");
    var addressDepartment = getXMLValue( response, "addressDept" );
    var addressWard = getXMLValue( response, "addressWard" );
    var addressDistrict = getXMLValue( response, "addressDistrict" );
    var education = getSelectIndexFor( "educationID", getXMLValue(response, "education"));
    var nationality = getSelectIndexFor( "nationalityID", getXMLValue(response, "nationality"));
    var otherNationality = getXMLValue( response, "otherNationality");
    var maritialStatus = getSelectIndexFor( "maritialStatusID", getXMLValue(response, "maritialStatus"));
    var healthRegion = getSelectIndexByTextFor( "healthRegionID", getXMLValue(response, "healthRegion"));
    var healthDistrict = getXMLValue(response, "healthDistrict");
    var guid = getXMLValue( response, "guid");
    var externalIDValue = getXMLValue(response, "externalID");
    var age= getXMLValue(response, "age");
    var ageUnit= getXMLValue(response, "ageUnit");
    var bedNumber=getXMLValue(response, "bedNumber");
    var roomNumber=getXMLValue(response, "roomNumber");
    var diagnosis=getXMLValue(response, "diagnosis");
    var depId=getXMLValue(response, "depId");
    var depName=getXMLValue(response, "depName");
	var speciesName=getXMLValue(response, "speciesName");
    var patientAgeUnitValue = getSelectIndexFor("patientAgeUnitID", getXMLValue(response, "patientAgeUnit"));
    setPatientInfo( nationalIDValue,
					STValue,
					subjectNumberValue,
					lastNameValue,
					firstNameValue,
					akaValue,
					motherValue,
					streetValue,
					cityValue,
					dobValue,
					genderValue,
					patientTypeValue,
					insuranceValue,
					chartNumberValue,
					occupationValue,
					employerNameValue,
					patientUpdatedValue,
					personUpdatedValue,
					motherInitialValue,
					communeValue,
					addressDepartment,
					addressWard,
					addressDistrict,
					education,
					nationality,
					otherNationality,
					maritialStatus,
					healthRegion,
					healthDistrict,
					guid,
					externalIDValue,
					age,
					ageUnit,
					bedNumber,
					roomNumber,
					diagnosis,
					depId,depName,
					speciesName);
}

function /*string*/ getXMLValue( response, key )
{
    var field = response.getElementsByTagName(key).item(0);

    if( field != null )
    {
         return field.firstChild.nodeValue;
    }
    else
    {
        return undefined;
    }
}

function  /*void*/ processSearchPopulateFailure(xhr) {
        //alert(xhr.responseText); // do something nice for the user
}

function unselectPatient() {
    $jq("input[id^='sel_']").each(function(){
        $jq(this).prop('checked', false);
    });
}

function  /*void*/ clearPatientInfo(){
    setPatientInfo();
}

function /*void*/ clearErrors(){

    for( var i = 0; i < pt_invalidElements.length; ++i ){
        setValidIndicaterOnField( true, $(pt_invalidElements[i]).name );
    }

    pt_invalidElements = [];

}

function  /*void*/ setPatientInfo(nationalID, ST_ID, subjectNumber, lastName, firstName, aka, mother, street, city, dob, gender,
		patientType, insurance, chartNumber, occupation, employerName, patientUpdated, personUpdated, motherInitial, commune, addressDept,
		addressWard, addressDistrict, educationId, nationalId, 
		nationalOther, maritialStatusId, healthRegionId, healthDistrictId, guid, externalID,
		age, ageUnit, bedNumber, roomNumber, diagnosis, depId, depName, speciesName) {

    clearErrors();

    if ( supportNationalID) { $("nationalID").value = nationalID == undefined ? "" : nationalID; }
    if(supportSTNumber){ $("ST_ID").value = ST_ID == undefined ? "" : ST_ID; }
    if(supportSubjectNumber){ $("subjectNumberID").value = subjectNumber == undefined ? "" : subjectNumber; }
    if (!useSingleNameField) {
	    $("lastNameID").value = lastName == undefined ? "" : lastName;
	    $("firstNameID").value = firstName == undefined ? "" : firstName;
    } else {
        $("firstNameID").value = firstName == undefined ? "" : firstName;
        $("firstNameID").value += lastName == undefined || lastName.length < 1 ? "" : " " + lastName;
    }
    if(supportAKA){$("akaID").value = aka == undefined ? "" : aka; }
    if(supportMothersName){$("motherID").value = mother == undefined ? "" : mother; }
    if(supportMothersInitial){$("motherInitialID").value = (motherInitial == undefined ? "" : motherInitial); }
    $("streetID").value = street == undefined ? "" : street;
    if(supportCity){$("cityID").value = city == undefined ? "" : city; }
    //Dung comment
/*  if(supportCommune){$("communeID").value = commune == undefined ? "" : commune; } */
    if(supportInsurance){$("insuranceID").value = insurance == undefined ? "" : insurance; }
    if(supportPatientChartNumber){$("chartNumberID").value = chartNumber == undefined ? "" : chartNumber; }
    if(supportOccupation){$("occupationID").value = occupation == undefined ? "" : occupation; }
    if(supportEmployerName){$("employerNameID").value = employerName == undefined ? "" : employerName; }
    $("patientLastUpdated").value = patientUpdated == undefined ? "" : patientUpdated;
    $("personLastUpdated").value = personUpdated == undefined ? "" : personUpdated;
    $("patientGUID_ID").value = guid == undefined ? "" : guid;
    if(supportExternalID){$("externalID").value = externalID == undefined ? "" : externalID; }
    if(supportPatientNationality){
        $("nationalityID").selectedIndex = nationalId == undefined ? 0 : nationalId; 
        $("nationalityOtherId").value = nationalOther == undefined ? "" : nationalOther;}
    if( supportEducation){ $("educationID").selectedIndex =  educationId == undefined ? 0 : educationId;}
    if( supportMaritialStatus){ $("maritialStatusID").selectedIndex = maritialStatusId == undefined ? 0 : maritialStatusId;}
    if( supportHealthRegion){ 
        var healthRegion = $("healthRegionID"); 
        healthRegion.selectedIndex = healthRegionId == undefined ? 0 : healthRegionId;
        $("shadowHealthRegion").value = healthRegion.options[healthRegion.selectedIndex].label;}
    if( supportHealthDistrict){
        if($("healthRegionID").selectedIndex != 0){
            getDistrictsForRegion( $("healthRegionID").value, healthDistrictId, healthDistrictSuccess, null);
        } 
    }
    
    if(supportAddressDepartment){
        var deptMessage = $("deptMessage");
        deptMessage.innerText = deptMessage.textContent = "";
        //for historic reasons, we switched from text to dropdown
        if( addressDept == undefined ){
            $("patientClinicalDeptId").value = 0;
        }else if( isNaN( addressDept) ){
            $("patientClinicalDeptId").value = 0;
            deptMessage.textContent = "<%=StringUtil
					.getMessageForKey("patient.address.dept.entry.msg")%>" + " " + addressDept;
        }else{
            $("patientClinicalDeptId").value = addressDept;
        }
    }
	// For Clinical Department (dropdown type field)
	if (supportPatientClinicalDept) {
		$("departmentID").value = depId == undefined ? "" : depId;
	}

    if(supportAddressWard){$("wardID").value = addressWard == undefined ? "" : addressWard; }
    if(supportAddressDistrict){$("districtID").value = addressDistrict == undefined ? 0 : addressDistrict; }

    if (dob == undefined) {
        $("dateOfBirthID").value = "";
        <%if (!FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_PATIENT_AGE_VALUE_AND_UNITS)) {%>
        $("age").value = "";
        <%}%>
    } else {
        var dobElement = $("dateOfBirthID");
        dobElement.value = dob;
        checkValidAgeDate(dobElement);
    }

    document.getElementById("genderID").selectedIndex = gender == undefined ? 0 : gender;
    //document.getElementById("patientAgeUnitID").selectedIndex = patientAgeUnit == undefined ? 0 : patientAgeUnit;
    if(supportPatientType){document.getElementById("patientTypeID").selectedIndex = patientType == undefined ? 0 : patientType; }

    // run this b/c dynamically populating the fields does not constitute an onchange event to populate the patmgmt tile
    // this is the fx called by the onchange event if manually changing the fields
    updatePatientEditStatus();
    //Dung add age
    
    $("ageId").value = age == undefined ? "" : age;
    $("patientAgeUnitID").value = ageUnit == undefined ? "" : ageUnit;
    /* var selectObj = document.getElementById("patientAgeUnits");
    if (ageUnit == undefined) {
        selectObj.selectedIndex = 0;
	} else {
	for (var i = 0; i < selectObj.options.length; i++) {
	      	if (selectObj.options[i].text== ageUnit) {
	          	selectObj.options[i].selected = true;
	      	}
	  	}
	} */
     
    $("patientDiagnosis").value = diagnosis == undefined ? "" : diagnosis;
    if (supportAddressDepartment) {
		$("patientClinicalDeptId").value = addressDept == undefined ? 0 : addressDept;
		$("patientClinicalDept").value = addressDept == undefined ? "" : addressDept;
	}
	if (supportPatientBedNumber) {	
		$("patientBedNumber").value = bedNumber == undefined ? "" : bedNumber;	
	}
	if (supportPatientRoomNumber) {	
		$("patientRoomNumber").value = roomNumber == undefined ? "" : roomNumber;	
	}
	$("speciesNameId").value = speciesName == undefined ? "" : speciesName;
}

function  /*void*/ updatePatientEditStatus() {
    if (updateStatus == "noAction") {
        setUpdateStatus("update");
    }

    for(var i = 0; i < patientInfoChangeListeners.length; i++){
            patientInfoChangeListeners[i]($("firstNameID").value,
                                          useSingleNameField ? null : $("lastNameID").value,
                                          $("genderID").value,
                                          $("patientAgeUnitID").value,
                                          $("dateOfBirthID").value,
                                          supportSTNumber ? $("ST_ID").value : "",
                                          supportSubjectNumber ? $("subjectNumberID").value : "",
                                          supportNationalID ? $("nationalID").value : "",
                                          supportMothersName ? $("motherID").value : null,
                                          $("patientPK_ID").value,
                                          supportExternalID ? $("externalID").value : "");

        }

    makeDirty();

    pt_setSave();
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

function  /*void*/  addPatient(){
    clearPatientInfo();
    unselectPatient();
    clearErrors();
    if(supportSTNumber){$("ST_ID").disabled = false;}
    if(supportSubjectNumber){$("subjectNumberID").disabled = false;}
    if(supportNationalID){$("nationalID").disabled = false;}
    setUpdateStatus( "add" );
    
    for(var i = 0; i < patientInfoChangeListeners.length; i++){
            patientInfoChangeListeners[i]("", "", "", "", "", "", "", "", "", "");
        }
}

function  /*void*/ savePage()
{
    window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
    var form = window.document.forms[0];
    form.action = "PatientManagementUpdate.do";
    form.submit();
}

function /*void*/ addPatientInfoChangedListener( listener ){
    patientInfoChangeListeners.push( listener );
}

function clearDeptMessage(){
    $("deptMessage").innerText = deptMessage.textContent = "";
}

function updateHealthDistrict( regionElement){
    $("shadowHealthRegion").value = regionElement.options[regionElement.selectedIndex].text;
    getDistrictsForRegion( regionElement.value, "", healthDistrictSuccess, null);
}

function healthDistrictSuccess( xhr ){
    //alert(xhr.responseText);

    var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    var districts = xhr.responseXML.getElementsByTagName("formfield").item(0).childNodes[0].childNodes;
    var selected = xhr.responseXML.getElementsByTagName("formfield").item(0).childNodes[1];
    var isValid = message == "<%=IActionConstants.VALID%>";
    var healthDistrict = $("healthDistrictID");
    var i = 0;

    healthDistrict.disabled = "";
    if( isValid ){
        healthDistrict.options.length = 0;
        healthDistrict.options[0] = new Option('', '');
        for( ;i < districts.length; ++i){
            healthDistrict.options[i + 1] = new Option(districts[i].attributes.getNamedItem("value").value, districts[i].attributes.getNamedItem("value").value);
        }
    }
    
    if( selected){
        healthDistrict.selectedIndex = getSelectIndexFor( "healthDistrictID", selected.childNodes[0].nodeValue);
    }
}

function pt_validatePhoneNumber( phoneElement){
    validatePhoneNumberOnServer( phoneElement, pt_processPhoneSuccess);
}

function  pt_processPhoneSuccess(xhr){
    //alert(xhr.responseText);

    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var success = false;

    if (message.firstChild.nodeValue == "valid"){
        success = true;
    }
    var labElement = formField.firstChild.nodeValue;

    setValidIndicaterOnField(success, labElement);
    pt_setFieldValidity( success, labElement );

    if( !success ){
        alert( message.firstChild.nodeValue );
    }

    pt_setSave();
}

function validateSubjectNumber( el, numberType ){

    validateSubjectNumberOnServer( el.value, numberType, el.id, processSubjectNumberSuccess );
}

function  processSubjectNumberSuccess(xhr){
    //alert(xhr.responseText);
    var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
    var message = xhr.responseXML.getElementsByTagName("message").item(0);
    var messageParts = message.firstChild.nodeValue.split("#");
    var valid = messageParts[0] == "valid";
    var warning = messageParts[0] == "warning";
    var fail = messageParts[0] == "fail";
    var success = valid || warning;
    var labElement = formField.firstChild.nodeValue;

    setValidIndicaterOnField(success, labElement);
    pt_setFieldValidity( success, labElement );

    if( warning || fail ){
        alert( messageParts[1] );
    }

    pt_setSave();
}
</script>
<nested:hidden name='<%=formName%>'
	property="patientProperties.currentDate" styleId="currentDate" />
<div id="PatientPage" style="display: inline">
	<nested:hidden property="patientProperties.patientLastUpdated"
		name='<%=formName%>' styleId="patientLastUpdated" />
	<nested:hidden property="patientProperties.personLastUpdated"
		name='<%=formName%>' styleId="personLastUpdated" />
	<nested:hidden property="patientSearch.selectedPatientActionButtonText"
		name='<%=formName%>' styleId="searchAccessionNumber" />


	<nested:hidden name='<%=formName%>'
		property="patientProperties.patientProcessingStatus"
		styleId="processingStatus" value="add" />
	<nested:hidden name='<%=formName%>'
		property="patientProperties.patientPK" styleId="patientPK_ID" />
	<nested:hidden name='<%=formName%>' property="patientProperties.guid"
		styleId="patientGUID_ID" />
	<logic:equal value="false" name="<%=formName%>"
		property="patientProperties.readOnly">
		<%--  <br />
        <div class="patientSearch" style="clear: both;">
            <hr style="width: 100%" />
            <input type="button"
                value='<%=StringUtil.getMessageForKey("patient.new")%>'
                onclick="addPatient()">
        </div> --%>
	</logic:equal>
	<div id="PatientDetail">
		<h2>
			<bean:message key="patient.information" />
		</h2>
		<table style="width: 80%" border="0">
			<%
			    if (FormFields.getInstance().useField(
								Field.SAMPLE_ENTRY_ORDER_URGENCY)) {
			%>
			<logic:equal name="displayOrderItemsInPatientManagement" value="true">
				<tr>
					<td align="center" colspan="100%"><bean:message
							key="patient.normal" />: <nested:radio name="<%=formName%>"
							property="sampleOrderItems.orderUrgency" style="margin:0"
							styleId="orderUrgency" onchange="setOrderModified();"
							value='<%=StringUtil
							.getContextualMessageForKey("patient.normal")%>' />
						<bean:message key="patient.emergency" />: <nested:radio
							name="<%=formName%>" property="sampleOrderItems.orderUrgency"
							style="margin:0" styleId="orderUrgency"
							onchange="setOrderModified();"
							value='<%=StringUtil
							.getContextualMessageForKey("patient.emergency")%>' />
					</td>
				</tr>
			</logic:equal>
			<%
			    }
			%>
			<tr>
				<%
				    if (!useSingleNameField) {
				%>
				<td style="width: 170px"><bean:message key="patient.name" /></td>
				<td style="text-align: left; width: 15%;" colspan="1"><bean:message
						key="patient.epiLastName" /> : <%
				    if (patientNamesRequired) {
				%> <span class="requiredlabel">*</span> <%
     }
 %></td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.lastName" styleClass="text" size="60"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="lastNameID" /></td>
				<td style="text-align: left; width: 15%"><bean:message
						key="patient.epiFirstName" /> : <%
				    if (patientNamesRequired) {
				%> <span class="requiredlabel">*</span> <%
     }
 %></td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.firstName" styleClass="text" size="40"
						onchange="checkFullName(this);updatePatientEditStatus();setOrderModified();"
						styleId="firstNameID" /></td>
				<%
				    } else {
				%>
				<td style="text-align: left; width: 170px;">
					<!-- full name --> <bean:message key="patient.epiFullName" /> : <%
     if (patientNamesRequired) {
 %> <span class="requiredlabel">*</span> <%
     }
 %>
				</td>
				<td colspan="5"><nested:text name='<%=formName%>'
						property="patientProperties.firstName"
						styleClass="text input-xxlarge" size="40"
						onchange="checkFullName(this);updatePatientEditStatus();setOrderModified();"
						styleId="firstNameID" /></td>
				<%
				    }
				%>
			</tr>
			<tr>
				<td style="text-align: left;" colspan="1"><bean:message
						key="patient.birthDate" />&nbsp;<%=DateUtil.getDateUserPrompt()%>:
					<%
				    if (patientAgeRequired) {
				%> <!--  span class="requiredlabel">*</span --> <%
     }
 %></td>
				<td colspan="1" width="330px"><nested:text name='<%=formName%>'
						property="patientProperties.birthDateForDisplay"
						styleClass="text input-small" size="20" maxlength="10"
						onkeyup="addDateSlashes(this,event);"
						onblur="checkValidEntryDate(this, 'past', true); updatePatientEditStatus(); setOrderModified();"
						styleId="dateOfBirthID" />
					<div id="patientProperties.birthDateForDisplayMessage"
						class="blank"></div> <%
     if (FormFields.getInstance().useField(
 					Field.SAMPLE_ENTRY_PATIENT_AGE_VALUE_AND_UNITS)) {
 %> <span id="age_label"><bean:message key="patient.age" />:</span> <span
					id="age_text"> <html:text name="<%=formName%>"
							property="sampleOrderItems.patientAgeValue" size="3"
							maxlength="4"
							onchange="sampleOrderValidateNumber( this, 1 ); updatePatientEditStatus();"
							styleClass="text input-mini" styleId="ageId" /> <logic:equal
							value="false" name="<%=formName%>"
							property="patientProperties.readOnly">
							<nested:select name='<%=formName%>'
								property="patientProperties.patientAgeUnit"
								onchange="updatePatientEditStatus();setOrderModified();"
								styleId="patientAgeUnitID" styleClass="input-small">
								
								<nested:optionsCollection name='<%=formName%>'
									property="patientProperties.patientAgeUnits" label="value"
									value="id" />
							</nested:select>
						</logic:equal> <logic:equal value="true" name="<%=formName%>"
							property="patientProperties.readOnly">
							<html:text property="patientProperties.patientAgeUnit"
								name="<%=formName%>" style="margin-left:3px;margin-right:3px;" />
						</logic:equal>
				</span> <%
     } else {
 %> <span id="age_label" style="text-align: left;"> <bean:message
							key="patient.age" />:
				</span> <span id="age_text"> <html:text
							property="patientProperties.age" name="<%=formName%>" size="3"
							maxlength="3"
							onchange="handleAgeChange( this ); updatePatientEditStatus();setOrderModified();"
							styleClass="text input-mini" styleId="age" />
						<div id="patientProperties.ageMessage" class="blank"></div>
				</span> <%
     }
 %></td>
				<td style="text-align: left;" colspan="1" width="145px;"><bean:message
						key="patient.gender" />: <%
				    if (patientGenderRequired) {
				%> <span class="requiredlabel">*</span> <%
     }
 %></td>
				<td><logic:equal value="false" name="<%=formName%>"
						property="patientProperties.readOnly">
						<nested:select name='<%=formName%>'
							property="patientProperties.gender"
							onchange="updatePatientEditStatus();setOrderModified();"
							styleId="genderID">
							<option value=" "></option>
							<nested:optionsCollection name='<%=formName%>'
								property="patientProperties.genders" label="value" value="id" />
						</nested:select>
					</logic:equal> <logic:equal value="true" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:text property="patientProperties.gender"
							name="<%=formName%>" />
					</logic:equal></td>
			</tr>
			<!-- comment -->
			<tr class="spacerRow">
				<td colspan="1">&nbsp;</td>
			</tr>
			<%
			    if (supportAKA) {
			%>
			<tr>
				<td></td>
				<td style="text-align: left;"><bean:message key="patient.aka" />
				</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.aka"
						onchange="updatePatientEditStatus();" styleId="akaID"
						styleClass="text" size="60" /></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (supportMothersName) {
			%>
			<tr>
				<td></td>
				<td style="text-align: left;"><bean:message
						key="patient.mother.name" /></td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.mothersName"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="motherID" styleClass="text" size="60" /></td>
			</tr>
			<%
			    }
						if (supportMothersInitial) {
			%>
			<tr>
				<td></td>
				<td style="text-align: left;"><bean:message
						key="patient.mother.initial" /></td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.mothersInitial"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="motherInitialID" styleClass="text" size="1" maxlength="1" /></td>
			</tr>
			<%
			    }
			%>
			<tr class="spacerRow">
				<td colspan="1">&nbsp;</td>
			</tr>
			<tr>
				<%
				    if (!useCompactLayout) {
				%>
				<td><bean:message key="person.streetAddress" /></td>
				<td style="text-align: left;"><bean:message
						key="person.streetAddress.street" />:</td>
				<%
				    } else {
				%>
				<td style="text-align: left;" colspan="1"><bean:message
						key="person.streetAddress.street" />:</td>
				<%
				    }
				%>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.streetAddress"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="streetID" styleClass="text" size="70"
						style="width:296px; " /></td>
				<%
				    if (!useCompactLayout) {
				%>
			</tr>
			<%
			    }
			%>
			<%
			    if (supportCommune) {
			%>
			<%
			    if (!useCompactLayout) {
			%>
			<tr>
				<td></td>
				<%
				    }
				%>
				<td style="text-align: left;" colspan="1"><bean:message
						key="person.address.ward" />:</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.addressWard"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="wardID" styleClass="text" style="width:296px;" /></td>


			</tr>
			<%
			    }
			%>
			<%
				supportPatientClinicalDept = true;
			    if (supportPatientClinicalDept) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="person.department" />:</td>
				<td><logic:equal value="false" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:select name='<%=formName%>'
							property="patientProperties.addressDepartment"
							onchange="updatePatientEditStatus();clearDeptMessage();setOrderModified();"
							styleId="departmentID">
							<option value="0"></option>
							<html:optionsCollection name="<%=formName%>"
								property="patientProperties.addressDepartments"
								label="dictEntry" value="id" />
						</html:select>
						<br>
						<span id="deptMessage"></span>
					</logic:equal> <logic:equal value="true" name="<%=formName%>"
						property="patientProperties.readOnly">
						<%-- <html:text property="patientProperties.addressDepartment"
							name="<%=formName%>" /> --%>
						<html:select name='<%=formName%>'
							property="patientProperties.addressDepartment" disabled='true'
							onchange="updatePatientEditStatus();clearDeptMessage();"
							styleId="departmentID">
							<option value="0"></option>
							<html:optionsCollection name="<%=formName%>"
								property="patientProperties.addressDepartments"
								label="dictEntry" value="id" />
						</html:select>
					</logic:equal></td>
				<td></td>
				<td></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.ADDRESS_VILLAGE)) {
			%>
			<tr>
				<td></td>
				<td style="text-align: left;"><%=StringUtil.getContextualMessageForKey("person.town")%>:
				</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.city"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="cityID" styleClass="text" size="20" /></td>
			</tr>
			<%
			    }
			%>
			
			<%
			    if (FormFields.getInstance().useField(Field.ADDRESS_WARD)) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="person.commune" />:</td>
				<td>
					<%
					    if (!useCityPicklist) {
					%> <nested:text name='<%=formName%>'
						property="patientProperties.commune"
						onchange="updatePatientEditStatus();" styleId="communeID"
						styleClass="text" size="30" /> <%
     } else {
 %> <logic:equal value="false" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:select name='<%=formName%>'
							property="patientProperties.city"
							onchange="updatePatientEditStatus();if(linkDistrictsToCities)updateCityDistrictOptions('cityID', 'districtID');setOrderModified();"
							styleId="cityID">
							<option value="0"></option>
							<html:optionsCollection name="<%=formName%>"
								property="patientProperties.cities" label="dictEntry" value="id" />
						</html:select>
					</logic:equal> <logic:equal value="true" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:text name="<%=formName%>" property="patientProperties.city"
							styleId="cityID" />
					</logic:equal> <%
     }
 %>
				</td>
				<%
				    if (!useCompactLayout) {
				%>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.ADDRESS_DISTRICT)) {
			%>
			<%
			    if (!useCompactLayout) {
			%>
			<tr>
				<td></td>
				<%
				    }
				%>
				<td style="text-align: left;"><%=StringUtil
							.getContextualMessageForKey("person.address.district")%>:
				</td>
				<td><logic:equal value="false" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:select name='<%=formName%>'
							property="patientProperties.addressDistrict"
							onchange="updatePatientEditStatus();setOrderModified();"
							styleId="districtID">
							<option value="0"></option>
							<html:optionsCollection name="<%=formName%>"
								property="patientProperties.districts" label="dictEntry"
								value="id" />
						</html:select>
					</logic:equal> <logic:equal value="true" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:text name="<%=formName%>"
							property="patientProperties.addressDistrict" styleId="districtID" />
					</logic:equal></td>
			</tr>
			<%
			    }
			%>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PatientPhone)) {
			%>
			<tr>
				<td>&nbsp;</td>
				<td style="text-align: left;"><%=StringUtil.getContextualMessageForKey("person.phone")%>:</td>
				<td><html:text styleId="patientPhone" name='<%=formName%>'
						property="patientProperties.phone" maxlength="35"
						onchange="pt_validatePhoneNumber( this );setOrderModified();" /></td>
			</tr>
			<%
			    }
			%>
			<tr class="spacerRow">
				<td>&nbsp;</td>
			</tr>
			<%
			    if (FormFields.getInstance().useField(Field.PatientHealthRegion)) {
			%>
			<tr>
				<td>&nbsp;</td>
				<td style="text-align: left;"><bean:message
						key="person.health.region" />:</td>
				<td><nested:hidden name='<%=formName%>'
						property="patientProperties.healthRegion"
						styleId="shadowHealthRegion" /> <html:select name='<%=formName%>'
						property="patientProperties.healthRegion"
						onchange="updateHealthDistrict( this );setOrderModified();"
						styleId="healthRegionID">
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>"
							property="patientProperties.healthRegions" label="value"
							value="id" />
					</html:select></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PatientHealthDistrict)) {
			%>
			<tr>
				<td>&nbsp;</td>
				<td style="text-align: left;"><bean:message
						key="person.health.district" />:</td>
				<td><html:select name='<%=formName%>'
						property="patientProperties.healthDistrict"
						onchange="setOrderModified();setOrderModified();"
						styleId="healthDistrictID" disabled="true">
						<option value="0"></option>

					</html:select></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (supportAddressDepartment || supportPatientDiagnosis) {
			%>
			<%-- <logic:equal name="displayOrderItemsInPatientManagement" value="true"> --%>
			<tr>
				<%
				    if (supportAddressDepartment) {
				%>
				<td style="text-align: left;"><bean:message
						key="patient.department" />:</td>
				<td><app:text name="<%=formName%>"
						property="sampleOrderItems.patientClinicalDept"
						onchange="makeDirty();setOrderModified();"
						styleClass="text input-medium" styleId="patientClinicalDept"
						style="width:296px;" /> <html:hidden
						property="sampleOrderItems.patientClinicalDeptId"
						name="<%=formName%>" styleId="patientClinicalDeptId" /></td>
				<%
				    }
								if (supportPatientDiagnosis) {
				%><!-- if (supportPatientDiagnosis) { -->
				<td style="text-align: left; width: 9.8%;"><bean:message
						key="patient.diagnosis" />:</td>
				<td><html:text name='<%=formName%>'
						property="sampleOrderItems.patientDiagnosis"
						onchange="setOrderModified();" styleId="patientDiagnosis"
						styleClass="text input-medium" style="width:296px;" /></td>
				<%
				    }
				%>

				<%
				    if (supportPatientType) {
				%>
				<%
				    if (!useCompactLayout) {
				%>
				<td style="text-align: left;"><bean:message
						key="patienttype.type" />:</td>
				<%
				    } else {
				%>
				<td style="text-align: left;" width="180px;"><bean:message
						key="patienttype.type" />:</td>
				<%
				    }
				%>
				<td><logic:equal value="false" name="<%=formName%>"
						property="patientProperties.readOnly">
						<nested:select name='<%=formName%>'
							property="patientProperties.patientType"
							onchange="updatePatientEditStatus();setOrderModified();"
							styleId="patientTypeID">
							<option value="0"></option>
							<nested:optionsCollection name='<%=formName%>'
								property="patientProperties.patientTypes" label="description"
								value="type" />
						</nested:select>
					</logic:equal> <logic:equal value="true" name="<%=formName%>"
						property="patientProperties.readOnly">
						<html:text name="<%=formName%>"
							property="patientProperties.patientType" styleId="patientTypeID"
							onchange="setOrderModified();" />
					</logic:equal></td>
				<%
				    }
				%>
			</tr>
			<%--     </logic:equal> --%>
			<%
			    }
			%>
			<%
			    if (supportPatientBedNumber || supportPatientRoomNumber) {
			%>
			<logic:equal name="displayOrderItemsInPatientManagement" value="true">
				<tr>
					<%
					    if (supportPatientBedNumber) {
					%>
					<td style="text-align: left;"><bean:message
							key="patient.bedNumber" />:</td>
					<td><html:text name='<%=formName%>'
							property="sampleOrderItems.patientBedNumber"
							styleId="patientBedNumber" styleClass="text input-small"
							onchange="setOrderModified();" style="width:296px;" /></td>
					<%
					    }
						if (supportPatientRoomNumber) {
					%>
					<td style="text-align: left;"><bean:message
							key="patient.roomNumber" />:</td>
					<td><html:text name='<%=formName%>'
							property="sampleOrderItems.patientRoomNumber"
							styleId="patientRoomNumber" styleClass="text input-small"
							onchange="setOrderModified();" style="width:296px;" /></td>
					<%
					    }
					%>

				</tr>
			</logic:equal>
			<%
			    }
			%>
			<%
			    if (supportInsurance || supportPatientType
								|| supportPatientChartNumber) {
			%>
			<tr>
				<%
				    if (supportInsurance) {
				%>
				<td style="text-align: left;" width="170px;"><bean:message
						key="patient.insuranceNumber" />:</td>
				<td width="330px"><nested:text name='<%=formName%>'
						property="patientProperties.insuranceNumber"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="insuranceID" styleClass="text input-medium"
						style="width:296px;" /></td>
				<%
				    }
				%>


			</tr>

			<tr>
				<%
				    if (!supportSubjectNumber) {
				%>
				<td><%=StringUtil
							.getContextualMessageForKey("patient.externalId")%>
					<%
					    if (patientIDRequired) {
					%> <!-- span class="requiredlabel">*</span --> <%
     }
 %></td>
				<%
				    }
				%>
				<%
				    if (supportSTNumber) {
				%>
				<td style="text-align: left;"><bean:message
						key="patient.ST.number" />:</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.STnumber"
						onchange="validateSubjectNumber(this, 'STnumber');updatePatientEditStatus();setOrderModified();"
						styleId="ST_ID" styleClass="text" style="width:296px;" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<%
				    }
				%>
				<%
				    if (supportSubjectNumber) {
				%>
				<td>&nbsp;</td>
				<td style="text-align: left;"><bean:message
						key="patient.subject.number" />: <%
				    if (subjectNumberRequired) {
				%> <span class="requiredlabel">*</span> <%
     }
 %></td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.subjectNumber"
						onchange="validateSubjectNumber(this, 'subjectNumber');updatePatientEditStatus();setOrderModified();"
						styleId="subjectNumberID" styleClass="text" size="60" /></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<%
				    }
				%>
				<%
				    if (supportExternalID) {
				%>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.externalId"
						onchange="validateSubjectNumber(this, 'externalId');updatePatientEditStatus();setOrderModified();"
						styleId="externalID" styleClass="text" style="width:296px;" /></td>
				<%
				    }
				%>
				<%
				    if (supportNationalID) {
				%>
				<td style="text-align: left;"><%=StringUtil
							.getContextualMessageForKey("patient.NationalID")%>:

				</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.nationalId"
						onchange="validateSubjectNumber(this, 'nationalId');updatePatientEditStatus();setOrderModified();"
						styleId="nationalID" styleClass="text" style="width:296px;" /></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			<%
			    }
			%>
			<%
			    }
			%>
			<%
			    if (supportPatientChartNumber) {
			%>
			<td style="text-align: left;"><bean:message
					key="patient.chartNumber" />:</td>
			<td><nested:text name='<%=formName%>'
					property="patientProperties.chartNumber"
					onchange="updatePatientEditStatus();setOrderModified();"
					styleId="chartNumberID" styleClass="text input-medium"
					style="width:296px;" /></td>
			<%
			    }
			%>
			<%
			    if (supportOccupation || supportEmployerName) {
			%>
			<tr>


				<%
				    if (!useCompactLayout) {
				%>
				<td style="text-align: left;"></td>
				<%
				    }
				%>
				<%
				    if (supportOccupation) {
				%>
				<%
				    if (!useCompactLayout) {
				%>
				<td style="text-align: left;"><bean:message
						key="patient.occupation" />:</td>
				<%
				    } else {
				%>
				<td style="text-align: left;"><bean:message
						key="patient.occupation" />:</td>
				<%
				    }
				%>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.occupation"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="occupationID" styleClass="text" style="width:296px;" /></td>
				<%
				    }
				%>
			</tr>
			<tr>
				<%
				    if (supportEmployerName) {
				%>
				<td style="text-align: left;"><bean:message
						key="patient.employerName" />:</td>
				<td><nested:text name='<%=formName%>'
						property="patientProperties.employerName"
						onchange="updatePatientEditStatus();setOrderModified();"
						styleId="employerNameID" styleClass="text" style="width:296px;" /></td>
				<%
				    }
				%>
				<td></td>
				<td></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PatientEducation)) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="patient.education" />:</td>
				<td><html:select name='<%=formName%>'
						property="patientProperties.education" styleId="educationID">
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>"
							property="patientProperties.educationList" label="value"
							value="value" />
					</html:select></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PatientMarriageStatus)) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="patient.maritialStatus" />:</td>
				<td><html:select name='<%=formName%>'
						property="patientProperties.maritialStatus"
						styleId="maritialStatusID">
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>"
							property="patientProperties.maritialList" label="value"
							value="value" />
					</html:select></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PatientNationality)) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="patient.nationality" />:</td>
				<td><html:select name='<%=formName%>'
						property="patientProperties.nationality" styleId="nationalityID">
						<option value="0"></option>
						<html:optionsCollection name="<%=formName%>"
							property="patientProperties.nationalityList" label="value"
							value="value" />
					</html:select></td>
				<td><bean:message key="specify" />:</td>
				<td><html:text name='<%=formName%>'
						property="patientProperties.otherNationality"
						styleId="nationalityOtherId" /></td>
			</tr>
			<%
			    }
			%>
			<%
			    if (FormFields.getInstance().useField(Field.PATIENT_SPECIES_NAME)) {
			%>
			<tr>
				<td style="text-align: left;"><bean:message
						key="patient.speciesName" />:</td>
				<td><html:text name='<%=formName%>'
						property="sampleOrderItems.speciesName" styleId="speciesNameId" /></td>
			</tr>
			<%
			    }
			%>

		</table>
		<table>

		</table>
	</div>
</div>

<script type="text/javascript">
$jq(document).ready(function () {
	mapCityDistrict();
	if (linkDistrictsToCities) {
        var newSelect = document.createElement("select");
        $jq("#districtID option").each(function(){
            var newOption = new Option($jq(this).text(), $jq(this).val());
            newSelect.add(newOption);
        });
        originalDistrictList = newSelect.options;
    }
    if (supportAddressDepartment && $('patientClinicalDeptId')) {
        new AjaxJspTag.Autocomplete(
                "ajaxAutocompleteXML", {
                    source: "patientClinicalDept",
                    target: "patientClinicalDeptId",
                    minimumCharacters: "1",
                    className: "autocomplete",
                    parameters: "dictionaryEntry={patientClinicalDept},dictionaryCategory=patientClinicalDept," + 
                                "provider=DictionaryAutocompleteProvider,fieldName=dictEntryDisplayValue,idName=id"
                });         
    }
});

//overrides method of same name in patientSearch
function selectedPatientChangedForManagement(firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, externalID, mother, pk ){
    if( pk ){
        getDetailedPatientInfo();
        $("patientPK_ID").value = pk;
    }else{
        clearPatientInfo();
        setUpdateStatus("add");
    }
}

var registered = false;

function registerPatientChangedForManagement(){
    if( !registered ){
        if (typeof addPatientChangedListener == typeof Function) {  // to avoid a JS error when this tile is included on the Audit Trail page
	        addPatientChangedListener( selectedPatientChangedForManagement );
	        registered = true;
	    }
	}
}

function checkFullName(field) {
    //Check if field is blank or empty
    if (field.value == null || field.value == "" || field.value.length == 0) {
        selectFieldErrorDisplay(false, field);
    } else {
        selectFieldErrorDisplay(true, field);
    }
}

registerPatientChangedForManagement();

</script>
