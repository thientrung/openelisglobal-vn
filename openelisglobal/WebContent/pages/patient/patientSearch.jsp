<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="us.mn.state.health.lims.common.action.IActionConstants,
			     us.mn.state.health.lims.common.formfields.FormFields,
			     us.mn.state.health.lims.common.formfields.FormFields.Field,
			     us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
			     us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
			     us.mn.state.health.lims.common.util.ConfigurationProperties.Property" %>
<%@ page import="us.mn.state.health.lims.common.util.*" %>


<%@ taglib uri="/tags/struts-bean"		prefix="bean" %>
<%@ taglib uri="/tags/struts-html"		prefix="html" %>
<%@ taglib uri="/tags/struts-logic"		prefix="logic" %>
<%@ taglib uri="/tags/labdev-view"		prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<bean:define id="formName"	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<bean:define id="localDBOnly" value='<%=Boolean.toString(ConfigurationProperties.getInstance().getPropertyValueLowerCase(Property.UseExternalPatientInfo).equals("false"))%>' />
<bean:define id="patientSearch" name='<%=formName%>' property='patientSearch' type="us.mn.state.health.lims.patient.action.bean.PatientSearch" />
<%!
	IAccessionNumberValidator accessionNumberValidator;
	boolean supportSTNumber = true;
	boolean supportMothersName = true;
	boolean supportSubjectNumber = true;
	boolean supportNationalID = true;
	boolean supportLabNumber = false;
	boolean useSingleNameField = false;
	boolean supportExternalID = false;
	String basePath = "";
	boolean accessNumber= true;
 %>

 <%
 	supportSTNumber = FormFields.getInstance().useField(Field.StNumber);
  	supportMothersName = FormFields.getInstance().useField(Field.MothersName);
  	supportSubjectNumber = FormFields.getInstance().useField(Field.SubjectNumber);
  	supportNationalID = FormFields.getInstance().useField(Field.NationalID);
  	supportLabNumber = FormFields.getInstance().useField(Field.SEARCH_PATIENT_WITH_LAB_NO);
 	accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
 	String path = request.getContextPath();
 	basePath = request.getScheme() + "://" + request.getServerName() + ":"	+ request.getServerPort() + path + "/";
	useSingleNameField = FormFields.getInstance().useField(Field.SINGLE_NAME_FIELD);
 	supportExternalID = FormFields.getInstance().useField(Field.EXTERNAL_ID);
 %>

<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" language="JavaScript1.2">

var supportSTNumber = <%= supportSTNumber %>;
var supportMothersName = <%= supportMothersName %>;
var supportSubjectNumber = <%= supportSubjectNumber %>;
var supportNationalID = <%= supportNationalID %>;
var supportLabNumber = <%= supportLabNumber %>;
var accessNumber = <%= accessNumber %>;
var patientSelectID;
var patientInfoHash = [];
var patientChangeListeners = [];
var localDB = '<%=localDBOnly%>' == "true";
var newSearchInfo = false;
var useSingleNameField = <%= useSingleNameField %>;
var supportExternalID = <%= supportExternalID %>;
//Dũng add
var criteria=null;
var valueSearch=null;
function searchPatients()
{
	$jq("#loadingModal").prop('hidden', false);
	criteria = $jq("#searchCriteria").val();
    var value = $jq("#searchValue").val();
    
    $jq("#searchAccessionNumber").val(value);
    var splitName;
    var lastName = "";
    var firstName = "";
    var STNumber = "";
    var subjectNumber = "";
    var nationalID = "";
    var externalID = "";
    var labNumber = "";
    var nameMinLength = useSingleNameField ? ((criteria == "1" && value.length > 2) || criteria != "1" ? true : false) : true;


	newSearchInfo = false;
    $jq("#resultsDiv").hide();
    $jq("#searchLabNumber").val('');
    if( criteria == 1){
    	if (!nameMinLength) {
    		alert("<%=StringUtil.getMessageForKey("patient.search.name.too.short")%>");
    		return;
    	} else {
        	firstName =  value.trim();
    	}
    }else if(criteria == 2){
        lastName = value.trim();
    }else if(criteria == 3){
        splitName = value.split(",");
        lastName = splitName[0].trim();
        firstName = splitName.size() == 2 ? splitName[1].trim() : "";
    }else if(criteria == 4){
        STNumber = value.trim();
        subjectNumber = value.trim();
        nationalID = value.trim();
        externalID = value.trim();
    }else if(criteria == 5){
        labNumber = value;
        $jq("#searchLabNumber").val(value);
    }

	patientSearch(lastName, firstName, STNumber, subjectNumber, nationalID, externalID, labNumber, "", false, processSearchSuccess);//1

}

function processSearchSuccess(xhr) {
	//alert( xhr.responseText );
	$jq("#loadingModal").prop('hidden', true);
	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
	var message = xhr.responseXML.getElementsByTagName("message").item(0);
	var table = $("searchResultTable");

	clearTable(table);
	clearPatientInfoCache();

	if ( message.firstChild.nodeValue == "valid" ) {
		$("noPatientFound").hide();
		$("searchResultsDiv").show();

		var resultNodes = formField.getElementsByTagName("result");
		for ( var i = 0; i < resultNodes.length; i++ ) {
			addPatientToSearch( table, resultNodes.item(i) );
		}
		if ( resultNodes.length == 1 && <%= String.valueOf(patientSearch.isLoadFromServerWithPatient()) %> ) {
			handleSelectedPatient();//2
		}
		
	} else {
		$("searchResultsDiv").hide();
		$("noPatientFound").show();
		selectPatient( null );
	}
}

function clearSearchResultTable() {
	var table = $("searchResultTable");
	clearTable(table);
	clearPatientInfoCache();
	
}

function clearTable(table){
	var rows = table.rows.length - 1;
	while( rows > 0 ){
		table.deleteRow( rows-- );
	}
}

function clearPatientInfoCache(){
	patientInfoHash = [];
}

function addPatientToSearch(table, result ){
	var patient = result.getElementsByTagName("patient")[0];

	var firstName = getValueFromXmlElement( patient, "first");
	var lastName = getValueFromXmlElement( patient, "last");
	var gender = getValueFromXmlElement( patient, "gender");
	var DOB = getValueFromXmlElement( patient, "dob");
	var stNumber = getValueFromXmlElement( patient, "ST");
	var subjectNumber = getValueFromXmlElement( patient, "subjectNumber");
	var nationalID = getValueFromXmlElement( patient, "nationalID");
	var externalID = getValueFromXmlElement( patient, "externalID");
	var accessionNumber = getValueFromXmlElement( result, "accessionNumber");
	var mother = getValueFromXmlElement( patient, "mother");
	var pk = getValueFromXmlElement( result, "id");
	var dataSourceName = getValueFromXmlElement( result, "dataSourceName");
	//Dung add
	var city = getValueFromXmlElement( patient, "city");
	var row = createRow( table, firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, externalID,accessionNumber, mother, pk, dataSourceName);
	addToPatientInfo( firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, externalID, mother, pk ,accessionNumber);

	if( row == 1 ){
		patientSelectID = pk;
		$("sel_1").checked = "true";
		selectPatient( pk );
	}
}

function getValueFromXmlElement( parent, tag ){
	var element = parent.getElementsByTagName( tag ).item(0);

	return element ? element.firstChild.nodeValue : "";
}

function createRow(table, firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, externalID,accessionNumber, mother, pk,  dataSourceName){

		var row = table.rows.length;

		var newRow = table.insertRow(row);

		newRow.id = "_" + row;

		var cellCounter = -1;

		var selectionCell = newRow.insertCell(++cellCounter);
		selectionCell.innerHTML = getSelectionHtml( row, pk );

		if (lastName == "UNKNOWN_") {
			var spanCell = newRow.insertCell(++cellCounter);
			spanCell.setAttribute("colspan", "100%");
			spanCell.innerHTML = '<%=StringUtil.getMessageForKey("label.patient.unknown")%>';
		} else if (nonNullString(lastName) == "" && nonNullString(firstName) == "" &&
				   nonNullString(gender) == "" && nonNullString(DOB) == "" &&
				   nonNullString(stNumber) == "" && nonNullString(subjectNumber) == "" &&
				   nonNullString(nationalID) == "" && nonNullString(externalID) == "" &&
				   nonNullString(mother) == "") {
			var spanCell = newRow.insertCell(++cellCounter);
			spanCell.setAttribute("colspan", "100%");
			spanCell.innerHTML = '<%=StringUtil.getMessageForKey("label.patient.no.displayable.data")%>';			
		} else {
			if( !localDB){
				var dataSourceCell = newRow.insertCell(++cellCounter);
				dataSourceCell.innerHTML = nonNullString( dataSourceName );
			}
			if (!useSingleNameField)
				var lastNameCell = newRow.insertCell(++cellCounter);
			var firstNameCell = newRow.insertCell(++cellCounter);
			var genderCell = newRow.insertCell(++cellCounter);
			var dobCell = newRow.insertCell(++cellCounter);
			var motherCell = supportMothersName ? newRow.insertCell(++cellCounter) : null;
			var stCell = supportSTNumber ? newRow.insertCell(++cellCounter) : null;
			var subjectNumberCell = supportSubjectNumber ? newRow.insertCell(++cellCounter) : null;
			var externalCell = supportExternalID ? newRow.insertCell(++cellCounter) : null;
			var nationalCell = supportNationalID ? newRow.insertCell(++cellCounter) : null;
			var accessionNumberCell = accessNumber ? newRow.insertCell(++cellCounter) : null;
			if (!useSingleNameField)
				lastNameCell.innerHTML = nonNullString( lastName );
			firstNameCell.innerHTML = nonNullString( firstName );
			genderCell.innerHTML = nonNullString( gender );
			if( supportSTNumber){stCell.innerHTML = nonNullString( stNumber );}
			if( supportSubjectNumber){subjectNumberCell.innerHTML = nonNullString( subjectNumber );}
			if( supportExternalID){externalCell.innerHTML = nonNullString( externalID );}
			if( supportNationalID){nationalCell.innerHTML = nonNullString( nationalID );}
			if( accessNumber){accessionNumberCell.innerHTML = nonNullString( accessionNumber );}
			dobCell.innerHTML = nonNullString( DOB );
			if(supportMothersName){motherCell.innerHTML = nonNullString( mother );}
		}

		return row;
}

function getSelectionHtml( row, key){
	return "<input name='selPatient' id='sel_" + row + "' value='" + key + "' onclick='selectPatient(this.value)' type='radio'>";
}

function /*String*/ nonNullString( target ){
	return target == "null" ? "" : target;
}

function addToPatientInfo( firstName, lastName, gender, DOB, stNumber, subjectNumber, nationalID, externalID, mother, pk ,accessionNumber){
	var info = [];
	info["first"] = nonNullString( firstName );
	info["last"] = nonNullString( lastName );
	info["gender"] = nonNullString( gender );
	info["DOB"] = nonNullString( DOB );
	info["ST"] = nonNullString( stNumber );
	info["subjectNumber"] = nonNullString( subjectNumber );
	info["external"] = nonNullString( externalID );
	info["national"] = nonNullString( nationalID );
	info["mother"] = nonNullString( mother );
	info["accessionNumber"] = nonNullString( accessionNumber );
	patientInfoHash[pk] = info;
	
}


function selectPatient( patientID ){
    var i;
	if( patientID ){
		patientSelectID = patientID;

		var info = patientInfoHash[patientID];

		for(i = 0; i < patientChangeListeners.length; i++){
			patientChangeListeners[i](info["first"],info["last"],info["gender"],info["DOB"],info["ST"],info["subjectNumber"],info["national"],info["external"],info["mother"], patientID,info["accessionNumber"]);
			$jq("#searchAccessionNumber").val(info["accessionNumber"]);
		}

	}else{
		for(i = 0; i < patientChangeListeners.length; i++){
			patientChangeListeners[i]("","","","","","","","","", null);
		}
	}
	
}

function /*void*/ addPatientChangedListener( listener ){
	patientChangeListeners.push( listener );
}


function /*void*/ handleEnterEvent( ){
		
		if( newSearchInfo ){
			searchPatients();
		}
		return false;
}

function /*void*/ dirtySearchInfo(e){ 
	var code = e ? e.which : window.event.keyCode;
	if( code != 13 ){
		newSearchInfo = true; 
	}
}

function enableSearchButton(eventCode){
	
    var valueElem = $jq("#searchValue");
    var criteriaElem  = $jq('#searchCriteria');
    var searchButton = $jq("#searchButton");
    var nameMinLength = useSingleNameField ? ((criteriaElem.val() == "1" && valueElem.val().length > 2) || criteriaElem.val() != "1" ? true : false) : true;
  //Dũng add
	if(eventCode==0){
		if(valueElem.val() && criteriaElem.val()!="0" && valueElem.val() != '<%=StringUtil.getMessageForKey("label.select.search.here")%>' && nameMinLength)
			searchButton.attr("enable", "enable");
		else
			searchButton.attr("disabled", "disabled");
	}
    if( valueElem.val() && criteriaElem.val() != "0" && valueElem.val() != '<%=StringUtil.getMessageForKey("label.select.search.here")%>' && nameMinLength){
        searchButton.removeAttr("disabled");
        if( eventCode == 13 ){
            searchButton.click();
        }
    }else{
        searchButton.attr("disabled", "disabled");
    }

    if(criteriaElem.val() == "5" ){
        valueElem.attr("maxlength","<%= Integer.toString(accessionNumberValidator.getMaxAccessionLength()) %>");
    }else{
        valueElem.attr("maxlength","120");
    }
}

function handleSelectedPatient(){//3
    var accessionNumber = "";
    if($jq("#searchCriteria").val() == 5){//lab number
        accessionNumber = $jq("#searchValue").val();
    }

    $("searchResultsDiv").style.display = "none";
    var form = document.forms[0];
    //Dũng add
    valueSearch=$jq("#searchValue").val();
    form.action = '<%=formName%>'.sub('Form','') + ".do?accessionNumber=" + accessionNumber + "&patientID=" + patientSelectID+"&criteria="+criteria+"&valueSearch="+valueSearch;
    if( !(typeof requestType === 'undefined') ){
        form.action += "&type=" + requestType;
    }
    $jq("#accessionNumber").val(accessionNumber);
    $jq("#valuetextSearch").val(patientSelectID);
    form.submit();

}

function firstClick(){
    var searchValue = $jq("#searchValue");
    searchValue.val("");
    searchValue.removeAttr("onkeydown");
}

function messageRestore(element ){
    if( !element.value  ){
        element.maxlength = 120;
        element.value = '<%=StringUtil.getMessageForKey("label.select.search.here")%>';
        element.onkeydown = firstClick;
        setCaretPosition(element, 0);
    }
}

function cursorAtFront(element){

    if( element.onkeydown){
    	if($jq("#"+element.id).val()=="" || $jq("#"+element.id).val()==null)
        setCaretPosition( element, 0);
    }
}

function setCaretPosition(ctrl, pos){
    if(ctrl.setSelectionRange){
        ctrl.focus();
        ctrl.setSelectionRange(pos,pos);
    } else if (ctrl.createTextRange) {
        var range = ctrl.createTextRange();
        range.collapse(true);
        range.moveEnd('character', pos);
        range.moveStart('character', pos);
        range.select();
    }
}
</script>

<input type="hidden" id="searchLabNumber">
<div id="PatientPage" class="colorFill patientSearch" style="display:inline;" >

	<h2><bean:message key="sample.entry.search"/></h2>
    <logic:present property="warning" name="<%=formName%>" >
        <center><h3 class="important-text"><bean:message key="order.modify.search.warning" /></h3></center>
    </logic:present>
    <table width="100%" style="background-color:inherit;">
      <tr>
        <td width="45%">
          <select id="searchCriteria"  style="float:right; margin:auto;" onchange="enableSearchButton()">
	        <%
	        	String ciriteria=request.getAttribute(IActionConstants.CIRITERIA)==null?""
	        			:request.getAttribute(IActionConstants.CIRITERIA).toString();
	            for(IdValuePair pair : patientSearch.getSearchCriteria()){
	            	if(ciriteria.equals(pair.getId())){
	            		out.print("<option value=\"" + pair.getId() +"\" selected>" + pair.getValue() + "</option>");
	            	}else{
	                out.print("<option value=\"" + pair.getId() +"\">" + pair.getValue() + "</option>");
	            	}
	            }
	        %>
	       </select>
        </td>
        
        <td width="20%">
			<input size="35"
	           maxlength="120"
	           style="margin:auto;"
	           id="searchValue"
	           class="text patientSearch"
	           <% 
	           		String valueSearch = request.getParameter(IActionConstants.VALUE_SEARCH) == null ? "" :
	           		 new String(request.getParameter(IActionConstants.VALUE_SEARCH).getBytes("iso-8859-1"),"UTF-8");
	           		if (!valueSearch.equals("")) {
	           			out.print("value='" + valueSearch + "'");
	           		} else {
	           			out.print("value='"+StringUtil.getMessageForKey("label.select.search.here")+"'");
	           		}
	           %>
	           type="text"
	           onclick="cursorAtFront(this)"
	           onkeydown='firstClick();'
	           onkeyup="messageRestore(this);enableSearchButton(event.which);"
	           onfocus="enableSearchButton(event.which);"
	           onchange="enableSearchButton(event.which);"
	            />
        </td>
        <td width="1%"></td>
        <td width="34%">
           <input type="button"
	           name="searchButton"
	           class="btn btn-default"
	           value="<%= StringUtil.getMessageForKey("label.patient.search")%>"
	           id="searchButton"
	           onclick="searchPatients()"
	           disabled="disabled"
	           style="margin-left:3px; float:left;"
	           class="btn btn-default"/>
        </td>
      </tr>
      <tr id="loadingModal" hidden="true"><td colspan="4" widht=100% style="text-align: center;"><%= StringUtil.getMessageForKey("search.loading")%><img src="<%=basePath%>images/loading.gif" /></td></tr>
      
    </table>
<br/>
	<div id="noPatientFound" align="center" style="clear: both; display: none" >
		<h1><bean:message key="patient.search.not.found"/></h1>
	</div>
	<div id="searchResultsDiv" class="colorFill" style="clear: both; display: none; margin-top:5px;" >
		<% if( localDBOnly.equals("false")){ %>
		<table id="searchResultTable" style="width:100%">
			<tr>
				<th width="2%"></th>
				<th width="10%" >
					<bean:message key="patient.data.source" />
				</th>
		<% } else { %>
		<table id="searchResultTable" width="100%">
			<tr>
				<th width="2%"></th>
		<% } %>
				<% if (!useSingleNameField) { %>
				<th width="18%">
					<bean:message key="patient.epiLastName"/>
				</th>
				<% } %>
				<th width="15%">
				<% if (useSingleNameField) { %>
					<bean:message key="patient.epiFullName"/>
				<% } else { %>
					<bean:message key="patient.epiFirstName"/>
				<% } %>
				</th>
				<th width="5%">
					<bean:message key="patient.gender"/>
				</th>
				<th width="11%">
					<bean:message key="patient.birthDate"/>
				</th>
				<% if( supportMothersName ){ %>
				<th width="20%">
					<bean:message key="patient.mother.name"/>
				</th>
				<% } %>
				<% if(supportSTNumber){ %>
				<th width="12%">
					<bean:message key="patient.ST.number"/>
				</th>
				<% } %>
				<% if(supportExternalID){ %>
				<th width="12%">
					<bean:message key="patient.externalId"/>
				</th>
				<% } %>
				<% if(supportSubjectNumber){ %>
				<th width="12%">
					<bean:message key="patient.subject.number"/>
				</th>
				<% } %>
				<% if(supportNationalID){ %>
				<th width="12%">
                    <%=StringUtil.getContextualMessageForKey("patient.NationalID") %>
                </th>
                <% } %>
                <% if(accessNumber){ %>
				<th width="12%">
                    <%=StringUtil.getContextualMessageForKey("batchresultsentry.browse.accessionNumber") %>
                </th>
                <% } %>
			</tr>
		</table>
		<br/>
        <% if( patientSearch.getSelectedPatientActionButtonText() != null){ %>
            <input type="button"]
            		class="btn btn-default"
                   value="<%= patientSearch.getSelectedPatientActionButtonText()%>"
                   id="selectPatientButtonID"
                   onclick="handleSelectedPatient()">
        <% }%>
		</div>
	</div>

