<%@ page language="java" contentType="text/html; charset=utf-8"
	import="us.mn.state.health.lims.common.action.IActionConstants,org.apache.struts.Globals"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>


<%
	String saveDisabled = (String) request.getSession().getAttribute(IActionConstants.SAVE_DISABLED);
%>

	<table border="0" cellpadding="0" cellspacing="4" width="0 auto;" align="center">
		<tbody valign="middle">
			<tr>
				<td align="center">
					<html:button onclick="validateAndTransfer();"
						property="validatetransfer"
						style="display:none;"
						styleId="validateTransferButtonId"
						styleClass="btn" 
						disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
						<bean:message key="label.button.validate.transfer" />
					</html:button>
				</td>
				<td width="4%"></td>
				<td align="center">
					<html:button onclick="savePage();"
						property="save"
						styleId="saveButtonId"
						styleClass="btn"
						disabled="<%=Boolean.valueOf(saveDisabled).booleanValue()%>">
						<bean:message key="label.button.save" />
					</html:button>
				</td>
				<td width="4%"></td>
				<td align="center">
					<html:button
						onclick="setMyCancelAction(window.document.forms[0], 'Cancel', 'no', '');"
						property="cancel"
						styleId="cancelButtonId"
						styleClass="btn">
						<bean:message key="label.button.cancel" />
					</html:button>
				</td>
			</tr>
		</tbody>
	</table>

<script type="text/javascript">

<%if( (request.getAttribute(IActionConstants.FWD_SUCCESS) != null &&
      ((Boolean)request.getAttribute(IActionConstants.FWD_SUCCESS))) || (request.getAttribute(IActionConstants.FWD_SUCCESS_NUMBER) != null &&
    	      ((Boolean)request.getAttribute(IActionConstants.FWD_SUCCESS_NUMBER)) ) ) { %>
if( typeof(showSuccessMessage) != 'undefined' ){
	showSuccessMessage( true );
}
<% } %>

</script>