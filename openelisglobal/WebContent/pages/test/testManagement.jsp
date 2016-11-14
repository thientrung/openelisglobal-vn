<%@ page language="java"
         contentType="text/html; charset=utf-8"
         import="us.mn.state.health.lims.common.util.StringUtil"
        %>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>

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

<form>
    <script type="text/javascript">

        function submitAction(target) {
            var form = window.document.forms[0];
            form.action = target;
            form.submit();
        }


    </script>
    <br>
    <input type="button" value='<%= StringUtil.getMessageForKey("banner.menu.administration") %>'
           onclick="submitAction('MasterListsPage.do');"
           class="textButton"/> &rarr; <%=StringUtil.getMessageForKey( "configuration.test.management" )%>

    <ul>
        <li><input type="button" value='<%= StringUtil.getMessageForKey("configuration.test.rename") %>'
                   onclick="submitAction('TestRenameEntry.do');"
                   class="textButton"/><br>
            &nbsp;&nbsp;&nbsp;&nbsp;<%=StringUtil.getMessageForKey("configuration.test.rename.explain")%></li>
        <li><input type="button" value='<%= StringUtil.getMessageForKey("configuration.test.activate") %>'
                   onclick="submitAction('TestActivation.do');"
                   class="textButton"/><br>
            &nbsp;&nbsp;&nbsp;&nbsp;<%=StringUtil.getMessageForKey("configuration.test.activate.explain")%></li>
    </ul>


</form>