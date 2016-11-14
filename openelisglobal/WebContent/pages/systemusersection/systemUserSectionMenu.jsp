<%@ page language="java"
	contentType="text/html; charset=utf-8"
	import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.systemuser.valueholder.SystemUser,
	us.mn.state.health.lims.test.valueholder.TestSection,
	us.mn.state.health.lims.common.action.IActionConstants" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />


<table width="100%" border=2" id="sUserSection">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.system.user.id"/> / <bean:message key="systemuser.name"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.test.section.id"/> / <bean:message key="test.testSectionName"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.view"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.assign"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.complete"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.release"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.cancel"/>
	   </th>
	   <th>
	   	  <bean:message key="systemusersection.has.admin"/>
	   </th>		   	   
	</tr>
	<logic:iterate id="systemUserSection" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection">
	<bean:define id="systemUserSectionId" name="systemUserSection" property="id"/>
	  <tr>
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs" onclick="output();">
	         <bean:write name="systemUserSectionId" />
	      </html:multibox>
   	   </td>
	   <td class="textcontent" style="text-align: left!important;">
 			<logic:notEmpty name="systemUserSection" property="systemUser">
	   	  		<bean:write name="systemUserSection" property="systemUser.id"/>
	   	  		&nbsp;&nbsp;
	   	  		<bean:write name="systemUserSection" property="systemUser.lastName"/>, 
	   	  		<bean:write name="systemUserSection" property="systemUser.firstName"/> 	   	  		
		   	</logic:notEmpty>
		   	 &nbsp;
	   </td>
	   <td class="textcontent" style="text-align: left!important;">
			<logic:notEmpty name="systemUserSection" property="testSection">
	   	  		<bean:write name="systemUserSection" property="testSection.id"/>
	   	  		&nbsp;&nbsp;
	   	  		<bean:write name="systemUserSection" property="testSection.testSectionName"/>	   	  		
		   	</logic:notEmpty>
		   	 &nbsp;	   
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="hasView"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="hasAssign"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="hasComplete"/>
	   </td>
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="hasRelease"/>
	   </td>	   	   	   	   
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="hasCancel"/>
	   </td>	   	   	   	   
	   <td class="textcontent">
	   	  <bean:write name="systemUserSection" property="isAdmin"/>
	   </td>
       </tr>
	</logic:iterate>
</table>
