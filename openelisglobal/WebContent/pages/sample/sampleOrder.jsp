<%@page import="us.mn.state.health.lims.common.action.IActionConstants"%>
<%@page import="us.mn.state.health.lims.common.formfields.FormFields"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.formfields.FormFields.Field,
                 us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
                 us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
                 us.mn.state.health.lims.common.services.PhoneNumberService,
                 us.mn.state.health.lims.common.util.ConfigurationProperties,
                 us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
                 us.mn.state.health.lims.common.util.StringUtil,
                 us.mn.state.health.lims.common.util.IdValuePair,
                 us.mn.state.health.lims.common.util.Versioning,
                 us.mn.state.health.lims.common.util.DateUtil"%>
<%@ page
	import="us.mn.state.health.lims.common.services.LocalizationService"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />
<!--bean:define id="entryDate" name="<%=formName%>" property="currentDate"/> -->

<%!String path = "";
	String basePath = "";
	boolean useCollectionDate = true;
	boolean useInitialSampleCondition = false;
	boolean useCollector = false;
	boolean autofillCollectionDate = true;
	boolean useReferralSiteList = false;
	boolean useReferralSiteCode = false;
	boolean useProviderInfo = false;
	boolean patientRequired = false;
	boolean trackPayment = false;
	boolean requesterLastNameRequired = false;
	boolean acceptExternalOrders = false;
	IAccessionNumberValidator accessionNumberValidator;
	boolean useModalSampleEntry = false;
	boolean useSubmitterNumber = false;
	boolean useSingleNameField = false;
	boolean useCompactLayout = false;
	boolean labNoUsedIfSpecimens = false;%>
<%
    path = request.getContextPath();
			basePath = request.getScheme() + "://" + request.getServerName()
					+ ":" + request.getServerPort() + path + "/";
			useCollectionDate = FormFields.getInstance().useField(
					Field.CollectionDate);
			useInitialSampleCondition = FormFields.getInstance().useField(
					Field.InitialSampleCondition);
			useCollector = FormFields.getInstance().useField(
					Field.SampleEntrySampleCollector);
			autofillCollectionDate = ConfigurationProperties.getInstance()
					.isPropertyValueEqual(Property.AUTOFILL_COLLECTION_DATE,
							"true");
			useReferralSiteList = FormFields.getInstance().useField(
					FormFields.Field.RequesterSiteList);
			useReferralSiteCode = FormFields.getInstance().useField(
					FormFields.Field.SampleEntryReferralSiteCode);
			useProviderInfo = FormFields.getInstance().useField(
					FormFields.Field.ProviderInfo);
			patientRequired = FormFields.getInstance().useField(
					FormFields.Field.PatientRequired);
			trackPayment = ConfigurationProperties.getInstance()
					.isPropertyValueEqual(Property.TRACK_PATIENT_PAYMENT,
							"true");
			accessionNumberValidator = new AccessionNumberValidatorFactory()
					.getValidator();
			requesterLastNameRequired = FormFields.getInstance().useField(
					Field.SampleEntryRequesterLastNameRequired);
			acceptExternalOrders = ConfigurationProperties.getInstance()
					.isPropertyValueEqual(Property.ACCEPT_EXTERNAL_ORDERS,
							"true");
			useModalSampleEntry = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_MODAL_VERSION);
			useSubmitterNumber = FormFields.getInstance().useField(
					Field.SUBMITTER_NUMBER);
			useSingleNameField = FormFields.getInstance().useField(
					Field.SINGLE_NAME_FIELD);
			useCompactLayout = FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_COMPACT_LAYOUT);
			labNoUsedIfSpecimens = FormFields.getInstance().useField(
					Field.LAB_NUMBER_USED_ONLY_IF_SPECIMENS);
			java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
			String clinicNA = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources()
					.getMessage(locale, "quick.entry.clinic.name.not.found");
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.jsp"></script>
<script type="text/javascript"
	src="scripts/jquery.asmselect.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="scripts/ajaxCalls.js?ver=<%=Versioning.getBuildNumber()%>"></script>
<script type="text/javascript"
	src="scripts/laborder.js?ver=<%=Versioning.getBuildNumber()%>"></script>

<link rel="stylesheet" type="text/css"
	href="css/jquery.asmselect.css?ver=<%=Versioning.getBuildNumber()%>" />
<style type="text/css">
</style>

<script type="text/javascript">

	document.onreadystatechange = function(){
	    if(document.readyState === 'complete'){
	    	
	    	var value = $jq("#receivedTime").val();
	    	var currentTime = new Date();
	    	var hour = currentTime.getHours();
	    	if (hour.toString().length == 1) {
	    		hour = '0' + hour;
	    	}
	    	var minute = currentTime.getMinutes();
	    	if (minute.toString().length == 1) {
	    		minute = '0' + minute;
	    	}
	    	
	    	if (value == '' || value == '00:00') {
	    		$jq("#receivedTime").val(hour + ':' + minute);
	    	}
	    }
	}
	
    var useReferralSiteList = <%=useReferralSiteList%>;
    var useReferralSiteCode = <%=useReferralSiteCode%>;
    var useProjectName = <%=FormFields.getInstance().useField(Field.PROJECT_OR_NAME)%>;
    var useProject2Name = <%=FormFields.getInstance().useField(Field.PROJECT2_OR_NAME)%>;
    var zplData = "";
    //Dung add for validate In submitter number
    requiredFields.push("submitterNumber");
    function checkAccessionNumber(accessionNumber) {
        //check if empty
        if (!fieldIsEmptyById("labNo")) {
        	<%if (!labNoUsedIfSpecimens) {%>
            validateAccessionNumberOnServer(false, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
            <%} else {%>
            validateAccessionNumberOnServer(true, false, accessionNumber.id, accessionNumber.value, processAccessionSuccess, null);
            <%}%>
        }
        else {
             selectFieldErrorDisplay(false, $("labNo"));
        }

        setCorrectSave();
    }

    function processAccessionSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var success = false;
        var mg="";

    	<%if (!labNoUsedIfSpecimens) {%>
        if (message.firstChild.nodeValue == "valid") {
		<%} else {%>
		if (message.firstChild.nodeValue == "valid" || message.firstChild.nodeValue == "SAMPLE_NOT_FOUND"){
		<%}%>
            success = true;
        }
        //dung add
                <%if (!labNoUsedIfSpecimens) {%>
        if (message.firstChild.nodeValue == "valid") {
        <%} else {%>
        if (message.firstChild.nodeValue == "valid" || message.firstChild.nodeValue == "SAMPLE_NOT_IN_SYSTEM"){
        <%}%>
            success = false;
            mg="<bean:message key="sample.entry.invalid.accession.number.not.in.system"/>";
        }
        
        var labElement = formField.firstChild.nodeValue;
        selectFieldErrorDisplay(success, $(labElement));

        if (!success) {
        	<%if (!labNoUsedIfSpecimens) {%>
            alert(message.firstChild.nodeValue);
    		<%} else {%>
    		if(mg!=""){
    			 alert(mg);
    	         $(labElement).focus();
    		}else{
    			  alert( "<bean:message key="sample.entry.invalid.accession.number"/>" );
    	          $(labElement).focus();
    		}

    		<%}%>
    		
        }

        setCorrectSave();
    }

    function setCorrectSave(){
        if( window.setSave){
            setSave();
        }else if(window.setSaveButton){
            setSaveButton();
        }
    }

    function getNextAccessionNumber() {
        generateNextScanNumber(processScanSuccess);
    }

    function processScanSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var returnedData = formField.firstChild.nodeValue;

        var message = xhr.responseXML.getElementsByTagName("message").item(0);

        var success = message.firstChild.nodeValue == "valid";

        if (success) {
            $("labNo").value = returnedData;

        } else {
            alert("<%=StringUtil.getMessageForKey("error.accession.no.next")%>");
            $("labNo").value = "";
        }

        selectFieldErrorDisplay(success, $("labNo"));
        setValidIndicaterOnField(success, "labNo");

        setCorrectSave();
    }


    function siteListChanged(textValue) {
        var siteList = $("requesterId");

        //if the index is 0 it is a new entry, if it is not then the textValue may include the index value
        if (siteList.selectedIndex == 0 || siteList.options[siteList.selectedIndex].label != textValue) {
            $("newRequesterName").value = textValue;
        } else if (useReferralSiteCode) {
            getCodeForOrganization(siteList.options[siteList.selectedIndex].value, processCodeSuccess);
        }
    }

    function processCodeSuccess(xhr) {
        //alert(xhr.responseText);
        var code = xhr.responseXML.getElementsByTagName("code").item(0);
        var success = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue == "valid";

        if (success) {
            $jq("#requesterCodeId").val(code.getAttribute("value"));
        }
    }

    function testLocationCodeChanged(element) {
        if (element.length - 1 == element.selectedIndex) {
            $("testLocationCodeOtherId").show();
        } else {
            $("testLocationCodeOtherId").hide();
            $("testLocationCodeOtherId").value = "";
        }
    }

    function setOrderModified(){
        $jq("#orderModified").val("true");
        orderChanged = true;
        if( window.makeDirty ){ makeDirty(); }

        setCorrectSave();
    }

    function validateProjectIdOrName(field) {
    	if ($F(field) == '' || $F(field) == null) {
    		selectFieldErrorDisplay(true, field);
    		setSampleFieldValidity(true, field.id);
    		$(field.id + 'Display').innerHTML = "";
    		$(field.id + 'Other').value = "";
    		setCorrectSave();
     	} else {
     		new Ajax.Request (
               	'ajaxXML',  //url
                 	{//options
                  	method: 'get', //http method
                  	parameters: 'provider=ProjectIdOrNameValidationProvider&field=' + field.id + '&id=' + encodeURIComponent($F(field)),      //request parameters
                  	//indicator: 'throbbing'
                  	onSuccess:  processProjectIdOrNameSuccess,
                  	//onFailure:  processFailure
                 	}
              	);
     	}
    }

    function processProjectIdOrNameSuccess(xhr) {
    	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
    	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    	var success = false;
    	
    	if (message.substring(0,5) == "valid"){
    		success = true;
    	}
    	selectFieldErrorDisplay( success, $(formField));
    	setSampleFieldValidity( success, formField );
    	
    	if(success) {
    		$(formField + 'Display').innerHTML = message.substring(5);
    		$(formField + 'Other').value = message.substring(5);
    	} else {
    		$(formField + 'Display').innerHTML = "";
    		$(formField + 'Other').value = "";
    	}
    	setCorrectSave();
    }

    
    // if submitter number field is empty, set the field border to red
    function checkSubmitterNumberIfEmpty(field) {
		if (field.value !='') {
			selectFieldErrorDisplay(true, field);
			setSampleFieldValidity(true, field.id);
		} else {
			selectFieldErrorDisplay(false, field);
			setSampleFieldValidity(false, field.id);
		}
		//end
    }
    
    function validateOrganizationLocalAbbreviation() {
        new Ajax.Request (
               'ajaxXML',  //url
                 {//options
                  method: 'get', //http method
                  parameters: 'provider=OrganizationLocalAbbreviationValidationProvider&form=' + document.forms[0].name +
                  			  '&field=submitterNumber&id=' + encodeURIComponent($F("submitterNumber")),      //request parameters
                  //indicator: 'throbbing'
                  onSuccess:  processValidateOrganizationLocalAbbreviationSuccess,
                  //onFailure:  processFailure
                 }
              );
    }

    function processValidateOrganizationLocalAbbreviationSuccess(xhr) {
    	var formField = xhr.responseXML.getElementsByTagName("formfield").item(0).firstChild.nodeValue;
    	var message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
    	var success = false;
    	var field = $('submitterNumber');

    	if (message.substring(0,5) == "valid"){
    		success = true;
    	}
	/* 	selectFieldErrorDisplay(success, field);
		setSampleFieldValidity(success, field.id); */
		
    	if( !success ){
    		$('clinicName').innerHTML = '<%=clinicNA%>';
    	 	$(formField + 'Other').value = ''; 
    		//field.focus();
    		//add: if enter invalid submitter value, the textfield border will turn red
    		selectFieldErrorDisplay(false, field);
    		setSampleFieldValidity(false, field.id);
    	} else {
    		$('clinicName').innerHTML = message.substring(5);
    		$(formField + 'Other').value = message.substring(5);
    		// Remove the red textfield border when success
    		selectFieldErrorDisplay(true, field);
    		setSampleFieldValidity(true, field.id);
    	}
		setCorrectSave();
    }

    function sampleOrderValidateNumber(field, min) {
    	makeDirty();

    	if (field.value != null && field.value != '') {
    	    if (field.value < min || !field.value.match(/^\d+$/)) {
    			selectFieldErrorDisplay(false, field);
    			setSampleFieldValidity(false, field.id);
    			alert( "<bean:message key="quick.entry.invalid.number"/> " + min );
    			field.focus();
    			setCorrectSave();
    			return;
    	    }
    	} else {
    		field.value = '';
    	}
    	selectFieldErrorDisplay(true, field);
    	setSampleFieldValidity(true, field.id);
      	setCorrectSave();
    }
  //Keyboard shortcut for jump tab to patient fullname field
    function jumpTabShortcut(event) {
        var key = event.which ? event.which : event.keyCode;
        if (key == 113) { // "F2" key pressed
        	var fullName = $("firstNameID");
        	fullName.focus();
         	moveCursorToEnd(fullName);
         	setSave();
        }
    }
</script>


<!-- This define may not be needed, look at usages (not in any other jsp or js page may be radio buttons for ci LNSP-->
<!--bean:define id="orderTypeList" name='<%=formName%>' property="sampleOrderItems.orderTypes"  type="java.util.Collection"/> -->
<bean:define id="sampleOrderItem" name='<%=formName%>'
	property="sampleOrderItems"
	type="us.mn.state.health.lims.sample.bean.SampleOrderItem" />
<!--html:hidden property="currentDate" name="<%=formName%>" styleId="currentDate"/> -->
<html:hidden property="sampleOrderItems.newRequesterName"
	name='<%=formName%>' styleId="newRequesterName" />
<html:hidden property="sampleOrderItems.modified" name='<%=formName%>'
	styleId="orderModified" />



<div id=orderDisplay
	<%=acceptExternalOrders
					&& sampleOrderItem.getLabNo() == null
					? "style='display:block'"
					: ""%>>
	<table style="width: 100%; margin: 15px; background-color: inherit;">

		<tr>
			<td>
				<table style="background-color: inherit;" <%if (useCompactLayout) {%> style="width: 72%" <%}%>>
					<logic:empty name="<%=formName%>" property="sampleOrderItems.labNo">
						<tr>
							<td style="width: 25%;<%if (!useCompactLayout) {%>width:35%<%}%>">
								<%=StringUtil
						.getContextualMessageForKey("quick.entry.accession.number")%> : <span
								class="requiredlabel">*</span>
							</td>
							<%-- <% if (!useCompactLayout) { %>width:65%<% } %> --%>
							<td style=""25%;"><app:text name="<%=formName%>"
									property="sampleOrderItems.labNo"
									maxlength='<%=Integer.toString(accessionNumberValidator
						.getMaxAccessionLength())%>'
									onchange="setOrderModified();checkAccessionNumber(this);"
									styleClass="text" styleId="labNo" /> <%--           <bean:message key="sample.entry.scanner.instructions"/>
            <input type="button" value='<%=StringUtil.getMessageForKey("sample.entry.scanner.generate")%>'
                   onclick="setOrderModified();getNextAccessionNumber(); " class="textButton"> --%>
							</td>
							<%
							    if (useModalSampleEntry) {
							%>
							<td  width="15%" style="padding-left: 5px; padding-bottom: 8px;"><logic:equal
									value="false" name='<%=formName%>'
									property="sampleOrderItems.readOnly">
									<button data-toggle="modal" id="printMasterLabels"
										class="btn btn-default btn-block" name="printMasterLabels" type="button"
										onclick="setupPrintLabelsModal($('labNo'), null);if($('samplesSectionId'))showSection($('samplesSectionId'), 'samplesDisplay');$jq('#label-modal').modal('show');">
										<bean:message key="label.button.printMasterLabels" />
									</button>
								</logic:equal></td>
							<%
							    }
							%>
							<td width="45%"></td>
						</tr>
					</logic:empty>

					<tr>
						<%
						    if (FormFields.getInstance().useField(Field.onsetOfDate)) {
						%>
						<td><%=StringUtil
						.getContextualMessageForKey("quick.entry.onset.date")%>: <span
							style="font-size: xx-small;"><%=DateUtil.getDateUserPrompt()%>
						</span></td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.onsetOfDate"
								onblur="setOrderModified(); checkValidEntryDate(this, 'past', true);"
								onkeyup="addDateSlashes(this, event);" maxlength="10"
								styleClass="text required" styleId="onsetOfDate" /></td>
						<%
						    }
						%>
					</tr>

					<logic:notEmpty name="<%=formName%>"
						property="sampleOrderItems.labNo">
						<tr>
							<td style="<%if (!useCompactLayout) {%>width:35%<%}%>"></td>
							<td style="<%if (!useCompactLayout) {%>width:65%<%}%>"></td>
						</tr>
					</logic:notEmpty>
					<%
					    if (FormFields.getInstance().useField(
										Field.SampleEntryUseRequestDate)) {
					%>
					<tr>
						<td><bean:message key="sample.entry.requestDate" />: <span
							class="requiredlabel">*</span><span style="font-size: xx-small;"><%=DateUtil.getDateUserPrompt()%></span></td>
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.requestDate" styleId="requestDate"
								styleClass="required"
								onblur="setOrderModified();checkValidEntryDate(this, 'past', true)"
								onkeyup="addDateSlashes(this, event);" maxlength="10" />
					</tr>
					<%
					    }
					%>
					<tr>
						<td><%=StringUtil
					.getContextualMessageForKey("quick.entry.received.date")%> : <span
							class="requiredlabel">*</span> <span style="font-size: xx-small;"><%=DateUtil.getDateUserPrompt()%>
						</span></td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.receivedDateForDisplay"
								onblur="setOrderModified();checkValidEntryDate(this, 'past');"
								onkeyup="addDateSlashes(this, event);" maxlength="10"
								styleClass="text required" styleId="receivedDateForDisplay" /></td>
						<%
						    if (FormFields.getInstance().useField(
											Field.SampleEntryUseReceptionHour)) {
						%>
						<td><bean:message key="sample.receptionTime" />:</td>
						<td><html:text name="<%=formName%>"
								onkeyup="filterTimeKeys(this,event); jumpTabShortcut(event);"
								property="sampleOrderItems.receivedTime" styleId="receivedTime"
								styleClass="input-mini" maxlength="5"
								onblur="setOrderModified(); checkValidTime(this, true);"
								style="width:296px;" /></td>

						<%
						    }
						%>
					</tr>

					<%
					    if (FormFields.getInstance().useField(Field.PROJECT_OR_NAME)) {
					%>
					<tr>
						<td><bean:message key="humansampleone.projectNumber" />:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.projectIdOrName"
								onblur="this.value=this.value.toUpperCase();validateProjectIdOrName(this);"
								onchange="setOrderModified();" size="30" maxlength="50"
								styleClass="input-small" styleId="projectIdOrName"
								style="width:296px;" /> <%
     if (FormFields.getInstance().useField(Field.PROJECT2_OR_NAME)) {
 %>
						<td><bean:message key="humansampleone.project2Number" />:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.project2IdOrName"
								onblur="this.value=this.value.toUpperCase();validateProjectIdOrName(this);"
								onchange="setOrderModified();" size="30" maxlength="50"
								styleClass="input-small" styleId="project2IdOrName"
								style="width:296px;" />
							<div id="project2IdOrNameDisplay"
								style="display: inline; color: black;"></div> <html:hidden
								property="sampleOrderItems.project2IdOrNameOther"
								name="<%=formName%>" styleId="project2IdOrNameOther" /></td>
						<%
						    }
						%>
						<td>
							<div id="projectIdOrNameDisplay"
								style="display: inline-block; color: black;"></div>
							<html:hidden
								property="sampleOrderItems.projectIdOrNameOther"
								name="<%=formName%>" styleId="projectIdOrNameOther" />
						</td>

					</tr>
					<%
					    }
					%>

					<%
					    if (FormFields.getInstance().useField(
										Field.SampleEntryNextVisitDate)) {
					%>
					<tr>
						<td><bean:message key="sample.entry.nextVisit.date" />&nbsp;<span
							style="font-size: xx-small;"><bean:message
									key="sample.date.format" /></span>:</td>
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.nextVisitDate"
								onblur="setOrderModified();checkValidEntryDate(this, 'future', true)"
								onkeyup="addDateSlashes(this, event);" styleId="nextVisitDate"
								maxlength="10" /></td>
					</tr>
					<%
					    }
					%>

					<tr class="spacerRow">
						<td>&nbsp;</td>
					</tr>
					<%
					    if (FormFields.getInstance().useField(
										Field.SampleEntryRequestingSiteSampleId)) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.clientReference")%>:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.requesterSampleID" size="50"
								maxlength="50" onchange="setOrderModified();" /></td>
						<td style="width: 10%">&nbsp;</td>
						<td style="width: 45%">&nbsp;</td>
					</tr>
					<%
					    }
					%>
					<%
					    if (FormFields.getInstance().useField(
										Field.SAMPLE_ENTRY_USE_REFFERING_PATIENT_NUMBER)) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.referring.patientNumber")%>:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.referringPatientNumber"
								styleId="referringPatientNumber" size="50" maxlength="50"
								onchange="setOrderModified();" /></td>
						<td style="width: 10%">&nbsp;</td>
						<td style="width: 45%">&nbsp;</td>
					</tr>
					<%
					    }
					%>
					<%
					    if (useReferralSiteList) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.entry.project.siteName")%>: <%
						    if (FormFields.getInstance().useField(
												Field.SampleEntryReferralSiteNameRequired)) {
						%> <span class="requiredlabel">*</span> <%
     }
 %></td>
						<td colspan="3"><logic:equal value="false"
								name='<%=formName%>' property="sampleOrderItems.readOnly">
								<html:select styleId="requesterId" name="<%=formName%>"
									property="sampleOrderItems.referringSiteId"
									onchange="setOrderModified();siteListChanged(this);setCorrectSave();"
									onkeyup="capitalizeValue( this.value );">
									<option value=""></option>
									<html:optionsCollection name="<%=formName%>"
										property="sampleOrderItems.referringSiteList" label="value"
										value="id" />
								</html:select>
							</logic:equal> <logic:equal value="true" name='<%=formName%>'
								property="sampleOrderItems.readOnly">
								<html:text property="sampleOrderItems.referringSiteName"
									name="<%=formName%>" style="width:300px" />
							</logic:equal></td>
					</tr>
					<%
					    }
					%>
					<%
					    if (useReferralSiteCode) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.entry.referringSite.code")%>:
						</td>
						<td><html:text styleId="requesterCodeId" name="<%=formName%>"
								property="sampleOrderItems.referringSiteCode"
								onchange="setOrderModified();setCorrectSave();">
							</html:text></td>
					</tr>
					<%
					    }
					%>
					<%
					    if (ConfigurationProperties.getInstance().isPropertyValueEqual(
										Property.ORDER_PROGRAM, "true")) {
					%>
					<tr class="spacerRow">
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><bean:message key="label.program" />:</td>
						<td><html:select name="<%=formName%>"
								property="sampleOrderItems.program"
								onchange="setOrderModified();">
								<logic:iterate id="optionValue" name='<%=formName%>'
									property="sampleOrderItems.programList" type="IdValuePair">
									<option value='<%=optionValue.getId()%>'
										<%=optionValue.getId().equals(
								sampleOrderItem.getProgram())
								? "selected='selected'"
								: ""%>>
										<bean:write name="optionValue" property="value" />
									</option>
								</logic:iterate>
							</html:select></td>
					</tr>
					<%
					    }
					%>
					<tr class="spacerRow">
						<td>&nbsp;</td>
					</tr>
					<%
					    if (useProviderInfo) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.entry.provider.name")%>: <%
						    if (useCompactLayout) {
						%> <span style="font-size: x-small"><%=StringUtil
							.getContextualMessageForKey("humansampletwo.provider.addionalOrClinician")%></span>:
							<%
     }
 %> <%
     if (requesterLastNameRequired) {
 %> <span class="requiredlabel">*</span> <%
     }
 %></td>
						<td>
							<%
							    if (!useSingleNameField) {
							%> <html:text name="<%=formName%>"
								property="sampleOrderItems.providerLastName"
								styleId="providerLastNameID"
								onchange="setOrderModified();setCorrectSave();" size="30" /> <bean:message
								key="humansampleone.provider.firstName.short" />: <%
     }
 %> <html:text name="<%=formName%>"
								property="sampleOrderItems.providerFirstName"
								styleId="providerFirstNameID" onchange="setOrderModified();"
								size="30" />
						</td>

						<%
						    if (useSubmitterNumber) {
						%>
						<td width="18%"><span id="lb_providerFirstName"><bean:message
									key="humansampleone.organization.localAbbreviation" />: <span
								class="requiredlabel">*</span> </span></td>
						<td><html:text name="<%=formName%>"
								property="sampleOrderItems.submitterNumber"
								styleId="submitterNumber" styleClass="input-small"
								onblur="validateOrganizationLocalAbbreviation();"
								onchange="checkSubmitterNumberIfEmpty(this);setOrderModified();" size="25" style="width:296px;"
								maxlength="25" /></td>
						<%
						    }
						%>
					</tr>
					<tr>
						<%
						    if (FormFields.getInstance().useField(
												Field.SAMPLE_ENTRY_REQUESTER_WORK_PHONE_AND_EXT)) {
						%>
						<td><%=StringUtil
							.getContextualMessageForKey("humansampletwo.provider.workPhone")
							+ ": "%></td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.providerWorkPhone"
								styleId="providerWorkPhoneID" size="30" maxlength="30"
								styleClass="text"
								onchange="setOrderModified();validatePhoneNumber(this)" /></td>
						<%
						    }
						%>
						<%
						    if (FormFields.getInstance().useField(
												Field.SAMPLE_ENTRY_REQUESTER_WORK_PHONE_AND_EXT)) {
						%>
						<td><bean:message
								key="humansampletwo.provider.workPhone.extension" />:</td>
						<td><input id="providerWorkPhoneExt" type="text"
							class="input-mini" style="width: 296px;" maxlength="4"
							onchange="setOrderModified();sampleOrderValidateNumber(this, 0)"></td>
						<%
						    }
						%>
					</tr>
					<%
					    }
					%>
					<%
					    if (FormFields.getInstance().useField(Field.SampleEntryProviderFax)) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.entry.project.faxNumber")%>:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.providerFax" styleId="providerFaxID"
								size="20" styleClass="text" onchange="setOrderModified();" /></td>
					</tr>
					<%
					    }
					%>
					<%
					    if (FormFields.getInstance().useField(
										Field.SampleEntryProviderEmail)) {
					%>
					<tr>
						<td><%=StringUtil
						.getContextualMessageForKey("sample.entry.project.email")%>:</td>
						<td><app:text name="<%=formName%>"
								property="sampleOrderItems.providerEmail"
								styleId="providerEmailID" size="20" styleClass="text"
								onchange="setOrderModified();" /></td>
					</tr>
					<%
					    }
					%>
					<%
					    if (FormFields.getInstance().useField(
										Field.SampleEntryHealthFacilityAddress)) {
					%>
					<tr>
						<td><bean:message key="sample.entry.facility.address" />:</td>
					</tr>
					<tr>
						<td>&nbsp;&nbsp;<bean:message
								key="sample.entry.facility.street" />
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.facilityAddressStreet"
								styleClass="text" onchange="setOrderModified();" /></td>
					</tr>
					<tr>
						<td>&nbsp;&nbsp;<bean:message
								key="sample.entry.facility.commune" />:
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.facilityAddressCommune"
								styleClass="text" onchange="setOrderModified();" /></td>
					</tr>
					<tr>
						<td><bean:message key="sample.entry.facility.phone" />:
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.facilityPhone" styleClass="text"
								maxlength="17"
								onchange="setOrderModified(); validatePhoneNumber( this );" /></td>
					</tr>
					<tr>
						<td><bean:message key="sample.entry.facility.fax" />:
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.facilityFax" styleClass="text"
								onchange="setOrderModified();" /></td>
					</tr>
					<%
					    }
					%>
					<tr class="spacerRow">
						<td>&nbsp;</td>
					</tr>
					<%
					    if (trackPayment) {
					%>
					<tr>
						<td><%=StringUtil.getContextualMessageForKey("sample.entry.patientPayment")%>:</td>
						<td><logic:equal value="false" name="<%=formName%>"
								property="sampleOrderItems.readOnly">
								<html:select name="<%=formName%>" styleId="paymentOptionSelection"
									property="sampleOrderItems.paymentOptionSelection"
									onchange="setOrderModified();">
									<option value=''></option>
									<logic:iterate id="optionValue" name='<%=formName%>'
										property="sampleOrderItems.paymentOptions" type="IdValuePair">
										<option value='<%=optionValue.getId()%>'
											<%=optionValue.getId().equals(sampleOrderItem.getPaymentOptionSelection()) ? "selected='selected'" : ""%>>
											<bean:write name="optionValue" property="value" />
										</option>
									</logic:iterate>
								</html:select>
							</logic:equal> 
							<logic:equal value="true" name="<%=formName%>"
								property="sampleOrderItems.readOnly">
								<html:select name="<%=formName%>" styleId="paymentOptionSelection"
									property="sampleOrderItems.paymentOptionSelection" disabled='true'
									onchange="setOrderModified();">
									<option value=''></option>
									<logic:iterate id="optionValue" name='<%=formName%>'
										property="sampleOrderItems.paymentOptions" type="IdValuePair">
										<option value='<%=optionValue.getId()%>' 
											<%=optionValue.getId().equals(sampleOrderItem.getPaymentOptionSelection()) ? "selected='selected'" : ""%>>
											<bean:write name="optionValue" property="value" />
										</option>
									</logic:iterate>
								</html:select>
							</logic:equal></td>
						<td></td>
						<td>
							<div id="clinicName" style="display: inline; color: black;"></div>
							<html:hidden property="sampleOrderItems.submitterNumberOther"
								name="<%=formName%>" styleId="submitterNumberOther" />
						</td>
					</tr>
					<%
					    }
					%>
					<tr>
						<%
						    if (ConfigurationProperties.getInstance().isPropertyValueEqual(
											Property.USE_BILLING_REFERENCE_NUMBER, "true")) {
						%>
						<td><label for="billingReferenceNumber"> <%=LocalizationService
						.getLocalizedValueById(ConfigurationProperties
								.getInstance()
								.getPropertyValue(
										Property.BILLING_REFERENCE_NUMBER_LABEL))%>
						</label></td>
						<td><html:text name='<%=formName%>'
								property="sampleOrderItems.billingReferenceNumber"
								styleClass="text" styleId="billingReferenceNumber"
								onchange="setOrderModified();makeDirty()" /></td>
					</tr>
					<%
					    }
					%>
					<%
					    if (FormFields.getInstance().useField(Field.TEST_LOCATION_CODE)) {
					%>
					<tr>
						<td><bean:message key="sample.entry.sample.period" />:</td>
						<td><html:select name="<%=formName%>"
								property="sampleOrderItems.testLocationCode"
								onchange="setOrderModified(); testLocationCodeChanged( this )"
								styleId="testLocationCodeId">
								<option value=''></option>
								<logic:iterate id="optionValue" name='<%=formName%>'
									property="sampleOrderItems.testLocationCodeList"
									type="IdValuePair">
									<option value='<%=optionValue.getId()%>'
										<%=optionValue.getId().equals(
								sampleOrderItem.getTestLocationCode())
								? "selected='selected'"
								: ""%>>
										<bean:write name="optionValue" property="value" />
									</option>
								</logic:iterate>
							</html:select> &nbsp; <html:text name='<%=formName%>'
								property="sampleOrderItems.otherLocationCode"
								styleId="testLocationCodeOtherId" style='display:none' /></td>
					</tr>
					<%
					    }
					%>
					<tr class="spacerRow">
						<td>&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</div>

<script type="text/javascript">

	// Modified by Mark 2016.09.07 03:36PM
	// Display location value
	<%if (useSubmitterNumber) {%>
		validateOrganizationLocalAbbreviation();
	<%}%>
		// Display project name
	<%if (FormFields.getInstance().useField(Field.PROJECT_OR_NAME)) {%>
		validateProjectIdOrName($("projectIdOrName"));
	<%}%>
	// End of Modification

	$jq(document).ready(function () {
		if (useProjectName) {
			new AjaxJspTag.Autocomplete(
				"ajaxAutocompleteXML", {
					source: "projectIdOrName",
					target: "projectIdOrNameOther",
					minimumCharacters: "1",
					className: "autocomplete",
					parameters: "projectName={projectIdOrName},provider=ProjectAutocompleteProvider,fieldName=projectName,idName=id"
				});
	    }
	    if (useProject2Name) {
			new AjaxJspTag.Autocomplete(
				"ajaxAutocompleteXML", {
					source: "project2IdOrName",
					target: "project2IdOrNameOther",
					minimumCharacters: "1",
					className: "autocomplete",
					parameters: "projectName={project2IdOrName},provider=ProjectAutocompleteProvider,fieldName=projectName,idName=id"
				});
		}
	    new AjaxJspTag.Autocomplete(
	    		"ajaxAutocompleteXML", {
	    		source: "submitterNumber",
	    		target: "submitterNumberOther",
	    		minimumCharacters: "1",
	    		className: "autocomplete",
	    		parameters: "organizationName={submitterNumber},provider=OrganizationAutocompleteProvider,fieldName=organizationName,idName=id"
	    	});
		
	    var dropdown = $jq("select#requesterId");
	    autoCompleteWidth = dropdown.width() + 66 + 'px';
	    clearNonMatching = false;
	    capitialize = true;
	    // Actually executes autocomplete
	    dropdown.combobox();
	    // invalidLabID = '<bean:message key="error.site.invalid"/>'; // Alert if value is typed that's not on list. FIX - add bad message icon
	    maxRepMsg = '<bean:message key="sample.entry.project.siteMaxMsg"/>';
	
	    resultCallBack = function (textValue) {
	        siteListChanged(textValue);
	        setOrderModified();
	        setCorrectSave();
	    };
	
	    <%if (FormFields.getInstance().useField(Field.TEST_LOCATION_CODE)) {%>
	        showTestLocationCode();
	    <%}%>
	    
	 	// Modified by Mark 2016.09.07 03:36PM
		// Display location value
		<%if (useSubmitterNumber) {%>
			validateOrganizationLocalAbbreviation();
		<%}%>
			// Display project name
		<%if (FormFields.getInstance().useField(Field.PROJECT_OR_NAME)) {%>
			validateProjectIdOrName($("projectIdOrName"));
		<%}%>
		// End of Modification
	});
	
	<%if (FormFields.getInstance().useField(
					Field.SAMPLE_ENTRY_REQUESTER_WORK_PHONE_AND_EXT)) {%>
		if ($("providerWorkPhoneID").value.indexOf(" ") != -1) {
			var phone = $("providerWorkPhoneID").value.substring(0, $("providerWorkPhoneID").value.indexOf(" "));
			var ext = $("providerWorkPhoneID").value.substring($("providerWorkPhoneID").value.indexOf(" ") + 1);
			$("providerWorkPhoneID").value = phone;
			$("providerWorkPhoneExt").value = ext;
		}
	<%}%>

    <%if (FormFields.getInstance().useField(Field.TEST_LOCATION_CODE)) {%>
	   function showTestLocationCode(){
           if(( $jq("#testLocationCodeId option").length -1 ) == $jq("#testLocationCodeId option:selected").index() ){
               $jq("#testLocationCodeOtherId").show();
           }
	   }
    <%}%>

</script>

