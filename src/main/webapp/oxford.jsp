<%--
  ~ APDPlat - Application Product Development Platform
  ~ Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
  ~
  ~  This program is free software: you can redistribute it and/or modify
  ~  it under the terms of the GNU General Public License as published by
  ~  the Free Software Foundation, either version 3 of the License, or
  ~  (at your option) any later version.
  ~
  ~  This program is distributed in the hope that it will be useful,
  ~  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~  GNU General Public License for more details.
  ~
  ~  You should have received a copy of the GNU General Public License
  ~  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ page import="org.apdplat.superword.tools.OxfordPOS" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
   <title>牛津词典词性列表</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>

    <h3>牛津词典词性列表</h3>
    <table>
        <tr><td>
        <ol ondblclick="querySelectionWord();">
        <%
            for(OxfordPOS item : OxfordPOS.values()){
        %>
            <li><%=item.name().replace("_", " ")%></li>
        <%
            }
        %>
        <ol>
        </td></tr>
    </table>

    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
