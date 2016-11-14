<%@page import="us.mn.state.health.lims.dataexchange.order.action.HL7OrderInterpreter.Gender"%>
<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.action.IActionConstants"
        %>
<%@ page import="us.mn.state.health.lims.common.util.IdValuePair" %>
<%@ page import="us.mn.state.health.lims.common.util.StringUtil" %>
<%@ page import="us.mn.state.health.lims.common.util.Versioning" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%--
  ~ The contents of this file are subject to the Mozilla Public License
  ~ Version 1.1 (the "License"); you may not use this file except in
  ~ compliance with the License. You may obtain a copy of the License at
  ~ http://www.mozilla.org/MPL/
  ~
  ~ Software distributed under the License is distributed on an "AS IS"
  ~ basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing rights and limitations under
  ~ the License.
  ~
  ~ The Original Code is OpenELIS code.
  ~
  ~ Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
  --%>

<script type="text/javascript" src="scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>'/>
<bean:define id="testList" name='<%=formName%>' property="testList" type="java.util.List"/>

<%!
    int testCount = 0;
    int columnCount = 0;
    int columns = 3;
%>
<%
    columnCount = 0;
    testCount = 0;
%>
<form>
<script type="text/javascript">

	var saveClicked = false;
	
    if (!$jq) {
        var $jq = jQuery.noConflict();
    }

    function makeDirty(){
        function formWarning(){
            return "<bean:message key="banner.menu.dataLossWarning"/>";
        }
        window.onbeforeunload = formWarning;
    }

    function submitAction(target) {
        var form = window.document.forms[0];
        form.action = target;
        form.submit();
    }

    function setForEditing(testId, name) {
        $jq("#editDiv").show();
        $jq("#testName").text(name);
        $jq(".error").each(function (index, value) {
            value.value = "";
            $jq(value).removeClass("error");
            $jq(value).removeClass("confirmation");
        });
        $jq("#testId").val(testId);
        $jq(".test").each(function () {
            var element = $jq(this);
            element.prop("disabled", "disabled");
            element.addClass("disabled-text-button");
        });
        getTestNames(testId, testNameSuccess);
    }

    function testNameSuccess(xhr) {
        //alert(xhr.responseText);
        var formField = xhr.responseXML.getElementsByTagName("formfield").item(0);
        var message = xhr.responseXML.getElementsByTagName("message").item(0);
        var response;

        if (message.firstChild.nodeValue == "valid") {
            response = JSON.parse(formField.firstChild.nodeValue);
            $jq("#nameEnglish").text(response["name"]["english"]);
            $jq("#nameFrench").text(response["name"]["french"]);
            $jq("#nameVietnamese").text(response["name"]["vietnamese"]);
            $jq("#reportNameEnglish").text(response["reportingName"]["english"]);
            $jq("#reportNameFrench").text(response["reportingName"]["french"]);
            $jq("#reportNameVietnamese").text(response["reportingName"]["vietnamese"]);
            $jq(".required").each(function () {
                $jq(this).val("");
            });
            $jq("#testUnit").val(response["unitOfMeasure"]);
            
            if (response["resultLimitSize"] > 0 && $('tableTestNormalRange') == null) {
            	
                $jq('.testNormalRangeWrapper').css('display','block');
                var table = document.createElement('table');
                table.setAttribute("id", "tableTestNormalRange");
                
                for (var i = 0; i < response["resultLimitSize"]; i++) {
                	
                	var gender = response["resultLimit"]["resultLimitItem_" + i]["gender"];
                    var tr = document.createElement('tr');   
                    var td1 = document.createElement('td'); var td2 = document.createElement('td'); var td3 = document.createElement('td');
                    var td4 = document.createElement('td'); var td5 = document.createElement('td'); var td0 = document.createElement('td');

                    td1.setAttribute("style", "padding-left:20px; padding-right:10px;");
                    td2.setAttribute("style", "padding-left:14px; padding-right:19px;");
                    td4.setAttribute("style", "padding-left:14px; padding-right:16px;");
                    
                    var textlabel0;
					if (!(gender == " ") && !(gender == "") && gender != null) {
	                    td0.setAttribute("style", "padding-left:10px;");
	                    var 
						textlabel0 = document.createTextNode( '<%=StringUtil.getMessageForKey("patient.gender")%>' + ' (' + gender + ')' );
                	} else {
                		textlabel0 = document.createTextNode('');
                        td0.setAttribute("style", "padding-left:15px; padding-right:35px;");
                	}
                    
                    var textlabel1;
					if (response["resultLimit"]["resultLimitItem_" + i]["minAge"] == 0 && response["resultLimit"]["resultLimitItem_" + i]["maxAge"] == null) {
                		textlabel1 = document.createTextNode('');
                        td1.setAttribute("style", "padding-left:35px; padding-right:55px;");
                	} else {
						textlabel1 = document.createTextNode('<%=StringUtil.getMessageForKey("patient.gender")%>' + ' (' + response["resultLimit"]["resultLimitItem_" + i]["minAge"] + "-" + response["resultLimit"]["resultLimitItem_" + i]["maxAge"] + ')');
                	}
                    
					var minValue = response["resultLimit"]["resultLimitItem_" + i]["lowNormal"];
					var maxValue = response["resultLimit"]["resultLimitItem_" + i]["highNormal"];
					
                    var textlabel2 = document.createTextNode('<%=StringUtil.getMessageForKey("test.edit.minimum.value")%>' + ':');
                    var minVal = document.createElement("input");
                    //minVal.setAttribute("style", "width:60px;");
                    minVal.setAttribute('id', 'minVal_' + response["resultLimit"]["resultLimitItem_" + i]["id"]);
                    minVal.setAttribute('type', 'text');
                    minVal.setAttribute('value', minValue == null ? "" : minValue);
                    var textlabel3 = document.createTextNode('<%=StringUtil.getMessageForKey("test.edit.maximum.value")%>' + ':');
                    var maxVal = document.createElement("input");
                    //maxVal.setAttribute("style", "width:60px;");
                    maxVal.setAttribute('id', 'maxVal_' + response["resultLimit"]["resultLimitItem_" + i]["id"]);
                    maxVal.setAttribute('type', 'text');
                    maxVal.setAttribute('value', maxValue == null ? "" : maxValue);


                    td0.appendChild(textlabel0); td1.appendChild(textlabel1);
                    td2.appendChild(textlabel2); td3.appendChild(minVal);
                    td4.appendChild(textlabel3); td5.appendChild(maxVal);
                    
                    tr.appendChild(td0); tr.appendChild(td1); tr.appendChild(td2); tr.appendChild(td3); tr.appendChild(td4); tr.appendChild(td5);
                    table.appendChild(tr);
                }          
                $('testNormalRangeTable').appendChild(table);
                
                <%-- <tr>
	                <td style="padding-right: 20px"><bean:message key="label.french"/>:</td>
	                <td id="nameFrench" style="padding-left: 10px"></td>
	                <td><span class="requiredlabel">*</span>&nbsp;<html:text property="nameFrench" name="<%=formName%>" size="40"
	                                                                   styleClass="required" onchange="handleInput(this);"/>
	                </td>
	                <td id="reportNameFrench" style="padding-left: 10px"></td>
	                <td><span class="requiredlabel">*</span>&nbsp;<html:text property="reportNameFrench" name="<%=formName%>"
	                                                                   size="40" styleClass="required" onchange="handleInput(this);"/>
	                </td>
	            </tr> --%>
                
            } else {
                $jq('.testNormalRangeWrapper').css('display','none');
            }
        }

        window.onbeforeunload = null;
    }

    function confirmValues() {
        var hasError = false;
        $jq(".required").each(function () {
            var input = $jq(this);
            if (!input.val() || input.val().strip().length == 0) {
                input.addClass("error");
                hasError = true;
            }
        });

        if (hasError) {
            alert('<%=StringUtil.getMessageForKey("error.all.required")%>');
        } else {
            $jq(".required").each(function () {
                var element = $jq(this);
                element.prop("readonly", true);
                element.addClass("confirmation");
            });
            $jq(".requiredlabel").each(function () {
                $jq(this).hide();
            });
            
            // Added testUnit and testNormalRange (disable fields upon save)
            $jq("#testUnit").prop("readonly", true);
            $jq("#testUnit").addClass("confirmation");
            $jq("input[id^='minVal_']").each(function(){
            	// enable minValue inputs
    			var rowNumber = this.id.split("_");
    			var resultLimitId = rowNumber[1];
            	var minValElement = $jq(this);
            	minValElement.prop("readonly", true);
            	minValElement.addClass("confirmation");
            	// enable maxValue inputs
    			var maxValElement = $jq("#maxVal_" + resultLimitId);
    			maxValElement.prop("readonly", true);
    			maxValElement.addClass("confirmation");
    	    });
            
            $jq("#editButtons").hide();
            $jq("#confirmationButtons").show();
            $jq("#action").text('<%=StringUtil.getMessageForKey("label.confirmation")%>');
        }
    }

    function rejectConfirmation() {
        $jq(".required").each(function () {
            var element = $jq(this);
            element.removeProp("readonly");
            element.removeClass("confirmation");
        });
        $jq(".requiredlabel").each(function () {
            $jq(this).show();
        });

     	// Added testUnit and testNormalRange (enable fields upon reject)
        $jq("#testUnit").removeProp("readonly", true);
        $jq("#testUnit").removeClass("confirmation");
        $jq("input[id^='minVal_']").each(function(){
        	// enable minValue inputs
			var rowNumber = this.id.split("_");
			var resultLimitId = rowNumber[1];
        	var minValElement = $jq(this);
        	minValElement.removeProp("readonly", true);
        	minValElement.removeClass("confirmation");
        	// enable maxValue inputs
			var maxValElement = $jq("#maxVal_" + resultLimitId);
			maxValElement.removeProp("readonly", true);
			maxValElement.removeClass("confirmation");
	    });
        
        $jq("#editButtons").show();
        $jq("#confirmationButtons").hide();
        $jq("#action").text('<%=StringUtil.getMessageForKey("label.button.edit")%>');
    }

    function cancel() {
        $jq("#editDiv").hide();
        $jq("#testId").val("");
        $jq(".test").each(function () {
            var element = $jq(this);
            element.removeProp("disabled");
            element.removeClass("disabled-text-button");
        });
        window.onbeforeunload = null;
    }

    function handleInput(element) {
        $jq(element).removeClass("error");
        makeDirty();
    }

    function savePage() {
    	var testResultLimitList = [];
    	$jq("input[id^='minVal_']").each(function(){
			var rowNumber = this.id.split("_");
			var resultLimitId = rowNumber[1];
			var resultLimitMinValue = this.value;
			var resultLimitMaxValue = $("maxVal_" + resultLimitId).value;
			testResultLimitList.push({
	    	    key:   resultLimitId,
	    	    value: resultLimitMinValue + "-" + resultLimitMaxValue
	    	})
	    });
    	// Convert Array to String, then save to hidden HTML input
    	var strTestResultLimits = "";
    	for (i=0; i < testResultLimitList.length; i++) {
    		strTestResultLimits += testResultLimitList[i].key + "_";
    		strTestResultLimits += testResultLimitList[i].value;
    		strTestResultLimits += ";";
    	}
    	strTestResultLimits = strTestResultLimits.slice(0, -1);
    	$("selectedResultLimit").value = strTestResultLimits;
    	//alert("strTestResultLimits: " + strTestResultLimits);
    	/* if (!saveClicked) {
    		saveClicked = true;
	    	for (i=0; i < testResultLimitList.length; i++) {
	    	     $jq('#selectedResultLimit')
	    	         .append($jq("<option></option>")
	    	                    .attr("value", testResultLimitList[i].key)
	    	                    .text(testResultLimitList[i].value)); 
	    	}
    	} */
        
    	window.onbeforeunload = null; // Added to flag that formWarning alert isn't needed.
        var form = window.document.forms[0];
        form.action = "TestRenameUpdate.do";
        form.submit();
    }
</script>

<html:hidden property="testId" name="<%=formName%>" styleId="testId"/>
<html:hidden property="selectedResultLimit" name="<%=formName%>" styleId="selectedResultLimit"/>
<input type="button" value='<%= StringUtil.getMessageForKey("banner.menu.administration") %>'
       onclick="submitAction('MasterListsPage.do');"
       class="textButton"/> &rarr;
<input type="button" value='<%= StringUtil.getMessageForKey("configuration.test.management") %>'
       onclick="submitAction('TestManagementConfigMenu.do');"
       class="textButton"/>&rarr;
<%=StringUtil.getMessageForKey( "label.testName" )%>
<br><br>

<div id="editDiv" style="display: none">
    <h1 id="action"><bean:message key="label.button.edit"/></h1>

    <h2><%=StringUtil.getMessageForKey( "sample.entry.test" )%>:&nbsp;<span id="testName"></span></h2>
    <br>
    <table>
        <tr>
            <th>&nbsp;</th>
            <th colspan="2" style="text-align: center"><bean:message key="test.testName"/></th>
            <th colspan="2" style="text-align: center"><bean:message key="test.testName.reporting"/></th>
        </tr>
        <tr>
            <td></td>
            <td style="text-align: center"><bean:message key="label.current"/></td>
            <td style="text-align: center"><bean:message key="label.new"/></td>
            <td style="text-align: center"><bean:message key="label.current"/></td>
            <td style="text-align: center"><bean:message key="label.new"/></td>
        </tr>
        <%-- <tr>
            <td style="padding-right: 20px"><bean:message key="label.english"/>:</td>
            <td id="nameEnglish" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="nameEnglish" name="<%=formName%>" size="40"
                                                               styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
            <td id="reportNameEnglish" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="reportNameEnglish" name="<%=formName%>"
                                                               size="40" styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
            
        </tr>
        <tr>
            <td style="padding-right: 20px"><bean:message key="label.french"/>:</td>
            <td id="nameFrench" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="nameFrench" name="<%=formName%>" size="40"
                                                               styleClass="required" onchange="handleInput(this);"/>
            </td>
            
            <td id="reportNameFrench" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="reportNameFrench" name="<%=formName%>"
                                                               size="40" styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
        </tr> --%>
        <tr>
            <td style="padding-right: 20px"><bean:message key="label.vietnamese"/>:</td>
            <td id="nameVietnamese" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="nameVietnamese" name="<%=formName%>" size="40"
                                                               styleClass="required" onchange="handleInput(this);"/>
            </td>
            
            <td id="reportNameVietnamese" style="padding-left: 10px"></td>
            <td><span class="requiredlabel">*</span>&nbsp;<html:text property="reportNameVietnamese" name="<%=formName%>"
                                                               size="40" styleClass="required"
                                                               onchange="handleInput(this);"/>
            </td>
        </tr>
        
        <tr>
        	<td colspan="5"><hr/></td>
        </tr>
        <tr>
            <td>
            	<bean:message key="test.testUnit"/><!-- <span class="requiredlabel">*</span> -->:&nbsp;
            </td>
            <td>&nbsp;</td>
            <td style="padding-left:10px;">
            	<html:text name="<%=formName%>" property="testUnit" styleId="testUnit" size="20" />
            </td>
        </tr>
        
        <tr class="testNormalRangeWrapper">
        	<tr>
				<th colspan="5">
	   				<bean:message key="test.testNormalRange"/><%-- <span class="requiredlabel">*</span> :&nbsp;--%>
	   			</th>
   			</tr>
        	<tr><td>&nbsp;</td></tr>
   			<tr>
	   			<td id="testNormalRangeTable" colspan="5">
		    	</td>
	    	</tr>
	    </tr>
        
        <tr>
            <td></td>
            <td colspan="4" style="text-align: center">
                <div style="text-align: center" id="editButtons">
                    <input type="button" value='<%=StringUtil.getMessageForKey("label.button.save")%>'
                        onclick="confirmValues();"/>
                    <input type="button" value='<%=StringUtil.getMessageForKey("label.button.cancel")%>'
                        onclick='cancel()'/>
                </div>
                
                <div style="text-align: center; display: none;" id="confirmationButtons">
                    <input type="button" value='<%=StringUtil.getMessageForKey("label.button.accept")%>'
                        onclick="savePage();"/>
                    <input type="button" value='<%=StringUtil.getMessageForKey("label.button.reject")%>'
                        onclick='rejectConfirmation();'/>
                </div>
            </td>
        </tr>
    </table>
    <br><br>
</div>

<table>
    <% while(testCount < testList.size()){%>
    <tr>
        <td><input type="button" value='<%= ((IdValuePair)testList.get(testCount)).getValue() %>'
                   onclick="setForEditing( '<%= ((IdValuePair)testList.get(testCount)).getId() + "', '" + ((IdValuePair)testList.get(testCount)).getValue() %>');"
                   class="textButton test"/>
            <%
                testCount++;
                columnCount = 1;
            %></td>
        <% while(testCount < testList.size() && ( columnCount < columns )){%>
        <td><input type="button" value='<%= ((IdValuePair)testList.get(testCount)).getValue() %>'
                   onclick="setForEditing( '<%= ((IdValuePair)testList.get(testCount)).getId() + "', '" + ((IdValuePair)testList.get(testCount)).getValue() %>' );"
                   class="textButton test"/>
            <%
                testCount++;
                columnCount++;
            %></td>
        <% } %>

    </tr>
    <% } %>
</table>

<br>
<input type="button" value='<%= StringUtil.getMessageForKey("label.button.finished") %>'
       onclick="submitAction('TestManagementConfigMenu.do');"/>
</form>