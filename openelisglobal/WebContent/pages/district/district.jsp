<%@ page language="java" contentType="text/html; charset=utf-8"
	import="java.util.Date,
	us.mn.state.health.lims.common.action.IActionConstants"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/labdev-view" prefix="app"%>
<%@ taglib uri="/tags/sourceforge-ajax" prefix="ajax"%>

<div id="sound"></div>

<bean:define id="formName"
	value='<%=(String) request.getAttribute(IActionConstants.FORM_NAME)%>' />



<%!String allowEdits = "true";
	//bugzilla 1494
	String errorNewLine = "";%>

<%
    if (request.getAttribute(IActionConstants.ALLOW_EDITS_KEY) != null) {
				allowEdits = (String) request
						.getAttribute(IActionConstants.ALLOW_EDITS_KEY);
			}
			//bugzilla 1494
			java.util.Locale locale = (java.util.Locale) request.getSession()
					.getAttribute(org.apache.struts.Globals.LOCALE_KEY);
			errorNewLine = us.mn.state.health.lims.common.util.resources.ResourceLocator
					.getInstance().getMessageResources()
					.getMessage(locale, "error.dictionary.newlinecharacter");
%>

<script language="JavaScript1.2">

function validateForm(form) {
	return validateDistrictForm(form)
}

</script>
<%-- <html:hidden property="dirtyFormFields" name="<%=formName%>" styleId="dirtyFormFields"/> --%>
<table>
	<tr>
		<td class="label"><bean:message key="district.name" />:<span
			class="requiredlabel">*</span></td>
		<td><app:text name="<%=formName%>"
				property="districtName"
				onkeydown="return (event.keyCode!=13)" /></td>

	</tr>
	<tr>
		<td class="label"><bean:message key="district.cityName" />:<span
			class="requiredlabel">*</span></td>
		<td>
			<html:select name="<%=formName%>" property="selectedCityId">
			<app:optionsCollection name="<%=formName%>"
				property="cities"
				label="dictEntry"
				value="id" />
			</html:select>		
		</td>
				
				
			<%-- <app:text name="<%=formName%>"
				property="city.dictEntry" /> --%>
		<td></td>
	</tr>

	<tr>
		<td>&nbsp;</td>
	</tr>
</table>

<html:javascript formName="districtForm"/>