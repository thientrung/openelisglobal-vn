<%@page import="us.mn.state.health.lims.systemusermodule.valueholder.RoleModule"%>
<%@page import="us.mn.state.health.lims.analyzerimport.analyzerreaders.SysmexReader"%>
<%@page import="us.mn.state.health.lims.systemusermodule.daoimpl.RoleModuleDAOImpl"%>
<%@page import="us.mn.state.health.lims.systemmodule.daoimpl.SystemModuleDAOImpl"%>
<%@page import="us.mn.state.health.lims.systemmodule.dao.SystemModuleDAO"%>
<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.provider.validation.PasswordValidationFactory,
    us.mn.state.health.lims.common.util.Versioning,
	us.mn.state.health.lims.role.action.bean.DisplayRole,
	us.mn.state.health.lims.systemmodule.valueholder.SystemModule,
	us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!

String allowEdits = "true";
String basePath = "";
String tab = "&nbsp;&nbsp;&nbsp;&nbsp";
String currentTab = "";
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);

String path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":"
			+ request.getServerPort() + path + "/";
}
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>

<script type="text/javascript" src="<%=basePath%>scripts/CollapsibleLists.js"></script>

<script language="JavaScript1.2">

$jq(document).ready( function() {
	var input = $jq( ":input" );
	document.getElementsByName("save")[0].disabled = true;
	input.change( function( objEvent ){
		document.getElementsByName("save")[0].disabled = false;
	} );
	//CollapsibleLists.apply();
	
});

function validateForm(form) {
	return checkRequiredElements( true );
}
 
function /*boolean*/ checkRequiredElements( ){
 	var elements = $$(".required");
 	var ok = true;
 	
 	for( var i = 0; i < elements.length; ++i){
 		if( elements[i].value.blank() ){
 			elements[i].style.borderColor = "red";
 			ok = false;
 		}
 	}
 
 	if($("password1").value != $("password2").value ){
 		$("password1").style.borderColor = "red";
 		$("password2").style.borderColor = "red";

 		ok = false;
  	}
 	
 	return ok; 
}
 
function handlePassword1( password1 ){
	var password2 = $("password2");
	password2.value = "";
	password2.style.borderColor = "";
	password1.style.borderColor = "";
} 

function handlePassword2( password2 ){
 	var password1 = $("password1");
 	
 	if( !password1.value.blank() && !password2.value.blank() && password1.value != password2.value ){
 		password2.style.borderColor = "red";
		password1.style.borderColor = "red";
 		alert( "<%= StringUtil.getMessageForKey("errors.password.match")%>");
 	}else{
 		password2.style.borderColor = "";
		password1.style.borderColor = "";
 	}
}

function /*void*/ selectChildren(selection, childList) {
	var roleId = selection.id.split("_")[1];
	if ((selection.id).indexOf("role") != -1) {
		if (selection.checked == true)
			$jq("input[id^='module_" + roleId + "_']").prop("checked", true);
		else
			$jq("input[id^='module_" + roleId + "_']").prop("checked", false);
	}
	
	$jq("input[id='role_" + roleId + "']").prop("checked", selection.checked);
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

function /*void*/ requiredFieldUpdated( field){
	field.style.borderColor = field.value.blank() ? "red" : "";
}

function /*void*/ isModuleClicked(item, initLoad) {
	var roleId = item.id.split("_")[1];
	var moduleId = item.id.split("_")[2];
	if (moduleId == <%=IActionConstants.MODIFY_ORDER_MODULE_ID%>) {
		$jq("input[id='module_" + roleId + "_" + <%=IActionConstants.MODIFY_ORDER_SEARCH_MODULE_ID%> + "']").prop("checked", item.checked);
	}
	if (moduleId == <%=IActionConstants.MODIFY_FULLORDER_MODULE_ID%>) {
		$jq("input[id='module_" + roleId + "_" + <%=IActionConstants.MODIFY_FULLORDER_SEARCH_MODULE_ID%> + "']").prop("checked", item.checked);
	}
	if ((item.id).indexOf("module") != -1) {
		if (item.checked == true) {
			if (areAllChecked(roleId)) {
				$jq("input[id='role_" + roleId + "']").prop("checked", true);
			}
		} else {
			$jq("input[id='role_" + roleId + "']").prop("checked", false);
		}
	}
}

function /*boolean*/ areAllChecked(roleId) {
    var numUnchecked = $jq("#roleModule_" + roleId).find("input[type='checkbox']:not(:checked)").length;
    return numUnchecked > 0 ? false : true;
}

</script>
<html:hidden name="<%=formName %>"  property="systemUserId"/>
<html:hidden name="<%=formName %>"  property="loginUserId"/>
<html:hidden name="<%=formName %>"  property="systemUserLastupdated"/>
<table >
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.login.name"/> <span class="requiredlabel">*</span>
						</td>
						<td>
							<app:text name="<%=formName%>" property="userLoginName" styleClass='required' onchange="requiredFieldUpdated( this); makeDirty(); "/>
						</td>
		</tr>
		<tr>
			<td colspan="2">
				<!-- %=PasswordValidationFactory.getPasswordValidator().getInstructions() %-->
				<bean:message key="login.changePassEight.message" />
			</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.password"/> <span class="requiredlabel">*</span>
						</td>
						<td>
							<html:password name="<%=formName%>" 
										   property="userPassword1" 
										   styleId="password1" 
										   styleClass='required'
										   onchange="handlePassword1(this);  makeDirty();"/>
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.repeat.password" /> <span class="requiredlabel">*</span>
						</td>
						<td>
							<html:password name="<%=formName%>" 
							               property="userPassword2"  
							               styleId="password2" 
							               styleClass='required'
							               onchange="handlePassword2(this); makeDirty();" />
						</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="person.firstName" /> <span class="requiredlabel">*</span>
						</td>
						<td>
							<app:text name="<%=formName%>" 
							          property="userFirstName" 
							          styleClass='required' 
							          onchange="requiredFieldUpdated( this); makeDirty();"/>
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="person.lastName" /> <span class="requiredlabel">*</span>
						</td>
						<td>
							<app:text name="<%=formName%>" 
							          property="userLastName" 
							          styleClass='required' onchange="requiredFieldUpdated( this); makeDirty();"/>
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.password.expired.date" /> <span class="requiredlabel">*</span>
						</td>
						<td>
							<app:text name="<%=formName%>" 
							          property="expirationDate"
							          onkeyup="addDateSlashes(this, event);"
							          onchange="makeDirty();"
							          maxlength="10" />
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.timeout" /> <span class="requiredlabel">*</span>
						</td>
						<td>
							<html:text name="<%=formName%>" property="timeout" onchange="makeDirty();" />
						</td>
						<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.account.locked" />
						</td>
						<td>
							<html:radio name="<%=formName %>" property="accountLocked" value="Y" onchange="makeDirty();">Y</html:radio>
							<html:radio name="<%=formName %>" property="accountLocked" value="N" onchange="makeDirty();">N</html:radio>
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="login.account.disabled" />
						</td>
						<td>
							<html:radio name="<%=formName %>" property="accountDisabled" value="Y" onchange="makeDirty();">Y</html:radio>
							<html:radio name="<%=formName %>" property="accountDisabled" value="N" onchange="makeDirty();">N</html:radio>
						</td>
		</tr>
		<tr>
						<td class="label" style="vertical-align: inherit;" >
							<bean:message key="systemuser.isActive" />
						</td>
						<td>
							<html:radio name="<%=formName %>" property="accountActive" value="Y" onchange="makeDirty();">Y</html:radio>
							<html:radio name="<%=formName %>" property="accountActive" value="N" onchange="makeDirty();">N</html:radio>
						</td>
		</tr><tr>
			<td>&nbsp;</td>
		</tr>
</table>
<hr/>
<table>
	<tr>
		<td class="label" width="50%">
			<bean:message key="systemuserrole.roles" />
		</td>
	</tr>
	<logic:iterate  name="<%=formName%>" property="roles" id="role" type="DisplayRole" >
	<tr>
	<td>
		<% currentTab = "";
		   while(role.getNestingLevel() > 0){ currentTab += tab; role.setNestingLevel( role.getNestingLevel() - 1);} %>
       <%=currentTab%>    	
		<ul class="collapsibleList" style="list-style-type: none; margin-left:-1px;">
			<li>
				<html:multibox name="<%=formName %>"
							   property="selectedRoles"
							   styleId='<%="role_" + role.getRoleId() %>'
							   onclick='<%="selectChildren(this, " + role.getRoleId() + ");makeDirty();" %>' >
					<bean:write name="role" property="roleId" />
				</html:multibox>
				<bean:write name="role" property="roleName" />
				
				<% if (role.getSystemModuleList().size() > 0 ) { %>   
				
		        <ul id='<%="roleModule_" + role.getRoleId() %>' style="list-style-type:none; margin-left:15px;">
					<logic:iterate  name="<%=formName%>" property="modules" id="module" type="SystemModule" >
			        	<% for (int i=0; i < role.getSystemModuleList().size(); i++) { 
				        		if ((role.getSystemModuleList().get(i).getId()).equalsIgnoreCase(module.getId())) { 
				        			if ((role.getSystemModuleList().get(i).getId()).equalsIgnoreCase(String.valueOf(IActionConstants.INVENTORY_MODULE_ID)) ||
				        					(role.getSystemModuleList().get(i).getId()).equalsIgnoreCase(String.valueOf(IActionConstants.NONCOMFORMITY_MODULE_ID)) ||
				        						(role.getSystemModuleList().get(i).getId()).equalsIgnoreCase(String.valueOf(IActionConstants.MODIFY_ORDER_SEARCH_MODULE_ID)) ||
				        							(role.getSystemModuleList().get(i).getId()).equalsIgnoreCase(String.valueOf(IActionConstants.MODIFY_FULLORDER_SEARCH_MODULE_ID))) { %>
				        				<li style="display:none;">
						        	<% } else {%>
						        		<li>
						        	<% } %>
						        		<html:multibox name="<%=formName %>"
													   property="selectedModules"
													   styleId='<%="module_" + role.getRoleId() + "_" + role.getSystemModuleList().get(i).getId() %>'
													   onclick='isModuleClicked(this, false);' >
											<bean:write name="module" property="id" />
										</html:multibox>
										<bean:write name="module" property="description" />
									</li>
						<% 		}
			        		}%>
					</logic:iterate>
				</ul>
				<% } %>
			</li>
		</ul>
	</td>
	</tr>
	</logic:iterate>
	<tr>
		<td>&nbsp;
			
		</td>
	</tr>

</table>


