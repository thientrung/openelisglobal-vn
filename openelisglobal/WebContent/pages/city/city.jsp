<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,
	us.mn.state.health.lims.common.formfields.FormFields,
	us.mn.state.health.lims.common.formfields.FormFields.Field,
	us.mn.state.health.lims.common.util.StringUtil" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<div id="sound"></div>

<bean:define id="formName" value='<%= (String) request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%!
String allowEdits = "true";
String path = "";
String basePath = "";
String errorNewLine = "";
String errorInvalidIsActive = "";

boolean useCityName = true;
%>
<%
path = request.getContextPath();
basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
 allowEdits = (String)request.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
}
useCityName = FormFields.getInstance().useField(Field.CityName);

java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
errorNewLine = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale, "error.dictionary.newlinecharacter");
errorInvalidIsActive = us.mn.state.health.lims.common.util.resources.ResourceLocator.getInstance().getMessageResources().getMessage(locale, "error.dictionary.invalidisactive");
%>

<script language="JavaScript1.2">
function validateForm(form) {
	var validated = validateCityForm(form);
    //validation for no new line characters and isActive input
    if (validated) {
      	var cityName = document.getElementById("cityName").value;
	    var cityIsActive = document.getElementById("isActive").value;
	    if (containsNewLine(cityName)) {
	       alert('<%=errorNewLine%>'); 
	       validated = false;
	    }
	    if (cityIsActive == 'Y' || cityIsActive == 'N') {
	    	//Do something here
	    } else {
	       alert('<%=errorInvalidIsActive%>'); 
	       validated = false;
	    }
    } 
   return validated;
}
</script>

<table>
		<% if (useCityName) { %>
		<tr>
				<td class="label">
					<bean:message key="city.cityName"/>:<span class="requiredlabel">*</span>
				</td>
				<td>
					<html:text name="<%=formName%>" property="cityName" styleId="cityName" />
				</td>
		</tr>
		<% } %>
        <tr>
				<td class="label">
					<bean:message key="city.isActive"/>:<span class="requiredlabel">*</span>
				</td>
				<td width="1">
					<html:text name="<%=formName%>" property="isActive" styleId="isActive" size="1" onblur="this.value=this.value.toUpperCase()"/>
				</td>
		</tr>
 		<tr>
		<td>&nbsp;</td>
		</tr>
</table>

<html:javascript formName="cityForm"/>
