<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!

String allowEdits = "true";
//add: check allow edit or not
boolean isAllowEdits = true;
%>

<%
if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
   allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
   //add: if not allow edit will set field read only
   if (allowEdits == "true") {
      isAllowEdits = true;
   } else {
      isAllowEdits = false;
   }     
}

%>

<script language="JavaScript1.2">
function validateForm(form) {
 return validateProgramForm(form);
}
</script>

<table>
		<tr>
						<td class="label">
							<bean:message key="program.id"/>:
						</td>	
						<td> 
							<app:text name="<%=formName%>" property="id" allowEdits="false"/>
						</td>
		</tr>
		<tr>
						<td class="label">
							<bean:message key="program.programName"/>:<span class="requiredlabel">*</span>
						</td>	
						<td> 
							<html:text name="<%=formName%>" property="programName" />
						</td>
		 </tr>
         <tr>
						<td class="label">
							<bean:message key="program.code"/>:<span class="requiredlabel">*</span>
						</td>	
						<td>
							<html:text name="<%=formName%>" property="code" styleId="programCode" readonly="<%=!isAllowEdits%>"/>
						</td>
          </tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="programForm"/>

