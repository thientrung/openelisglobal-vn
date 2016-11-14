<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.test.valueholder.TestSection,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!
	String allowEdits = "true";
%>
<%
	if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
	 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
	}
%>

<script language="JavaScript1.2">
$jq(document).ready(function () {
	//set property checkOrUncheck hidden
	$jq("#checkOrUncheck").prop("type", "hidden");
	//when user is admin, will disable View, Assign, Cancel, Release and Complete in Edit until uncheck hasAdmin checkbox
    isAdminChange();
});

function validateForm(form) {
	return validateSystemUserSectionForm(form);
}

function isAdminChange() {
	 // user is admin will have all right on View, Assign, Complete, Release and Cancel
	 // but user has all right on View, Assign, Complete, Release and Cancel may not e admin

	/* if (initLoad && ($jq("#hasView").val() == "Y" && $jq("#hasAssign").val() == "Y" &&
			$jq("#hasComplete").val() == "Y" && $jq("#hasRelease").val() == "Y" &&
				$jq("#hasCancel").val() == "Y")) {
		//$jq("#hasAdmin").prop("checked", true);
	}
	
	if (initLoad && (item.checked == true)) {
		$jq("#hasAdmin").prop("checked", true);
<<<<<<< .mine
	}
	
	
	
	if (item.checked == false) {
		alert(item.checked);
		$jq("#hasAdmin").prop("checked", false);
		//$jq("#hasAdmin").val("N");
		//$jq("#hasAdmin").val("false");
		//$jq("#hasAdmin").val("off");
		//document.getElementById("hasAdmin").setAttribute("checked", false);		
	}

	if (item.checked == true) {
||||||| .r923
	}
	
	if (item.checked == true) {
=======
	}  */
	item = document.getElementById("hasAdmin");
	if ($jq("#hasAdmin").prop("checked")) {
>>>>>>> .r992
		$jq("#hasView").val("Y");
		$jq("#hasAssign").val("Y");
		$jq("#hasComplete").val("Y");
		$jq("#hasRelease").val("Y");
		$jq("#hasCancel").val("Y");
		$jq("#hasView").prop("disabled", true);
		$jq("#hasAssign").prop("disabled", true);
		$jq("#hasComplete").prop("disabled", true);
		$jq("#hasRelease").prop("disabled", true);
		$jq("#hasCancel").prop("disabled", true);
		
	} else {
		$jq("#hasView").prop("disabled", false);
		$jq("#hasAssign").prop("disabled", false);
		$jq("#hasComplete").prop("disabled", false);
		$jq("#hasRelease").prop("disabled", false);
		$jq("#hasCancel").prop("disabled", false);
	}
	//set value for property checkOrUncheck, when checkbox is checked, checkOrUncheck will have "true" value
	$jq("#checkOrUncheck").val(item.checked);
}

</script>

<table>
		<tr>
			<td class="label" style="vertical-align: inherit;height: 41px">
				<bean:message key="systemusersection.id"/>:
			</td>	
			<td> 
				<app:text name="<%=formName%>" property="id" allowEdits="false"/>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.system.user.id"/>:<span class="requiredlabel">*</span>
			</td>	
			<td> 
				<html:select name="<%=formName%>" property="systemUserId">
		   	  		<app:optionsCollection name="<%=formName%>" property="systemusers" label="nameForDisplay" value="id" allowEdits="true"/>
                        	</html:select>						
			</td>
		 </tr>
         <tr>
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.test.section.id"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:select name="<%=formName%>" property="testSectionId">
		   	  		<app:optionsCollection name="<%=formName%>" property="testsections" label="testSectionName" value="id" allowEdits="true"/>
                        	</html:select>						
			</td>
        </tr>
        <tr>
			<td>&nbsp;</td>
		</tr>
        <tr>						
			<td class="label" style="vertical-align: inherit;height: 41px">
				<bean:message key="systemusersection.has.admin"/>:<!-- <span class="requiredlabel">*</span> -->
			</td>	
			<td>
				<html:checkbox name="<%=formName%>" property="hasAdmin" onclick="isAdminChange();" styleId="hasAdmin" />
			</td>
			<!-- add to get value of checkbox-->
			<td>
				<html:text name="<%=formName%>" property="checkOrUncheck" styleId="checkOrUncheck"/>
			</td>
        </tr>
        <tr>
			<td>&nbsp;</td>
		</tr>
        <tr>						
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.has.view"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:text name="<%=formName%>" property="hasView" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="hasView"/>
			</td>
        </tr>
        <tr>						
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.has.assign"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:text name="<%=formName%>" property="hasAssign" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="hasAssign"/>
			</td>	
        </tr>
        <tr>								    
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.has.complete"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:text name="<%=formName%>" property="hasComplete" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="hasComplete"/>
			</td>
        </tr>
        <tr>									
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.has.release"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:text name="<%=formName%>" property="hasRelease" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="hasRelease"/>
			</td>																											
          </tr>
        <tr>									
			<td class="label" style="vertical-align: inherit;">
				<bean:message key="systemusersection.has.cancel"/>:<span class="requiredlabel">*</span>
			</td>	
			<td>
				<html:text name="<%=formName%>" property="hasCancel" size="1" maxlength="1" onblur="this.value=this.value.toUpperCase()" styleId="hasCancel"/>
			</td>																											
          </tr>          
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="systemUserSectionForm"/>