<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"
    import="java.util.Date,
	java.util.Hashtable,
	us.mn.state.health.lims.dictionary.valueholder.Dictionary,
	us.mn.state.health.lims.common.action.IActionConstants"%>
    <%@ taglib uri="/tags/struts-bean" prefix="bean" %>
	<%@ taglib uri="/tags/struts-html" prefix="html" %>
	<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
	<%@ taglib uri="/tags/labdev-view" prefix="app" %>

<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />
<table width="100%" border=2">
	<tr>
	   <th>
	     <bean:message key="label.form.select"/>
	   </th>
	   	   <th>
	   	  <bean:message key="district.browse.title"/>
	   </th>
	   <th>
	      <bean:message key="district.cityName"/>
	   </th>
	</tr>
	<logic:iterate id="dist" indexId="ctr" name="<%=formName%>" property="menuList" type="us.mn.state.health.lims.district.valueholder.District">
	<bean:define id="distID" name="dist" property="id"/>
	  <tr>	
	   <td class="textcontent">
	      <html:multibox name="<%=formName%>" property="selectedIDs" onclick="output()">
	         <bean:write name="distID" />
	      </html:multibox>
     
   	   </td>	 
	   <td class="textcontent">
   	      <app:write name="dist" property="districtEntry.dictEntry" />
	      &nbsp;
       </td>
       <td class="textcontent">
   	      <app:write name="dist" property="city.dictEntry" />
	      &nbsp;
       </td>       
     </tr>
	</logic:iterate>
</table>