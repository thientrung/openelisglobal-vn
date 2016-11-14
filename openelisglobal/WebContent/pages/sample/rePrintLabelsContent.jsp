
<%@ page language="java"
    contentType="text/html; charset=utf-8"
    import="java.util.Calendar,
            java.util.Date,
            java.util.List,
            us.mn.state.health.lims.common.action.IActionConstants,
            us.mn.state.health.lims.common.provider.reports.SampleLabelPrintProvider,
            us.mn.state.health.lims.common.provider.validation.AccessionNumberValidatorFactory,
            us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator,
            us.mn.state.health.lims.sample.util.AccessionNumberUtil,
            us.mn.state.health.lims.common.util.ConfigurationProperties,
            us.mn.state.health.lims.common.util.ConfigurationProperties.Property,
            us.mn.state.health.lims.common.util.Versioning" %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<bean:define id="formName" value='<%= (String)request.getAttribute(IActionConstants.FORM_NAME) %>' />

<%
    String basePath = "";
    boolean useSpecimenLabels = false;

    String path = request.getContextPath();
    basePath = request.getScheme() + "://" + request.getServerName() + ":"  + request.getServerPort() + path + "/";
    
    java.util.Locale locale = (java.util.Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
    
    IAccessionNumberValidator accessionNumberValidator = new AccessionNumberValidatorFactory().getValidator();
    
    List<String> printerNames = null;
    SampleLabelPrintProvider sampLblPrnProvider = new SampleLabelPrintProvider();
    if (sampLblPrnProvider != null) {
        printerNames = sampLblPrnProvider.getAllSystemPrintServiceNames();          
    }

    Calendar cal = Calendar.getInstance();
    int currentYear = cal.get(Calendar.YEAR);
    useSpecimenLabels = ConfigurationProperties.getInstance().isPropertyValueEqual(Property.USE_SPECIMEN_LABELS, "true");
%>

<script type="text/javascript" src="<%=basePath%>scripts/utilities.js?ver=<%= Versioning.getBuildNumber() %>" ></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.core.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.widget.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.button.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ui/jquery.ui.position.js?ver=<%= Versioning.getBuildNumber() %>"></script>
<script type="text/javascript" src="<%=basePath%>scripts/ajaxCalls.js?ver=<%= Versioning.getBuildNumber() %>"></script>

<script type="text/javascript">
var invalidPrintElements = new Array();

function isPrintFieldValid(fieldname)
{
    return $jq.inArray(fieldname, invalidPrintElements) == -1;
}

function setPrintFieldInvalid(field)
{
    if( $jq.inArray(field, invalidPrintElements) == -1 )
    {
        invalidPrintElements.push(field);
    }
}

function setPrintFieldValid(field)
{
    var removeIndex = $jq.inArray(field, invalidPrintElements);
    if( removeIndex != -1 )
    {
        for( var i = removeIndex + 1; i < invalidPrintElements.length; i++ )
        {
            invalidPrintElements[i - 1] = invalidPrintElements[i];
        }

        invalidPrintElements.length--;
    }
}

function isPrintEnabled()
{
    return checkRequiredLabelFields();
}

function checkRequiredLabelFields() {
    if (($jq.trim($jq('#masterLabels').val()) < 1
        <% if (useSpecimenLabels) { %>
         && $jq.trim($jq('#itemLabels').val()) < 1
        <% } %>
       ))
        return false;
    else
        return true;
}

function validateNumber(field) {
    makeDirty();

    if (field.value != null && field.value != '') {
        if (field.value < 1 || field.value > 6 || !field.value.match(/^\d+$/)) {
            selectFieldErrorDisplay(false, field);
            setPrintFieldInvalid(field.id);
            alert( "<bean:message key="quick.entry.invalid.maxsix"/>" );
            field.focus();
        } else {
        	selectFieldErrorDisplay(true, field);
            setPrintFieldValid(field.id);
        }
    } else {
        field.value = '';
        selectFieldErrorDisplay(true, field);
        setPrintFieldValid(field.id);
    }
    setSave();
    if ($jq("#re-label-modal").is(":visible")) $jq("#re-label-modal").data("modalUpdated", true);
}
</script>

<table style="border-spacing:6px;border-collapse:separate">
    <tr> 
        <td>
            <bean:message key="sample.label.print.printerName"/>:<span class="requiredlabel">*</span>
        </td>
        <td>
            <%if(printerNames == null) {%>
            <bean:message key="sample.label.print.printer.input.required"/>         
            <%} else {%>
            <select name="rePrinterName" id="rePrinterName">
                <%for (int i = 0; i < printerNames.size(); i++) {%>                             
                <option value="<%= (String) printerNames.get(i)%>">
                    <%= printerNames.get(i)%>                                                                   
                </option>
                <%}%>
            </select>
            <%}%>                 
        </td>
    </tr>
    <tr> 
        <td>
            <bean:message key="sample.label.print.master.labels"/>:<span class="requiredlabel">*</span>
        </td>
        <td>
            <input style="width:25px" id="reMasterLabels" type="text" name="masterLabels" maxlength="1" value="3"
                onchange="validateNumber(this)"/>
        </td>
    </tr>
    <% if (useSpecimenLabels) { %>
    <tr> 
        <td>
            <bean:message key="sample.label.print.item.labels"/>:<span class="requiredlabel">*</span>
        </td>
        <td>
            <input style="width:25px" id="reItemLabels" type="text" name="itemLabels" maxlength="1" value="1"
                onchange="validateNumber(this)"/>
        </td>
    </tr>
    <% } %>
</table>
