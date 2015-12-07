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

<%@ page import="org.apdplat.superword.tools.*" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
   <title>音标符号列表</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>

    <h3>音标符号列表</h3>
    <table border="1">
        <tr><td>牛津词典</td><td>韦氏词典</td><td>爱词霸</td><td>有道词典</td></tr>
        <tr>
            <td valign="top">
                <ol>
                <%
                    for(OxfordSymbol item : OxfordSymbol.values()){
                %>
                    <li><a target="_blank" href="symbol-search.jsp?symbol=<%=item.name()%>&dictionary=OXFORD&words_type=primary_school"><%=item.name()%></a></li>
                <%
                    }
                %>
                <ol>
            </td>
            <td valign="top">
                <ol>
                <%
                    for(WebsterSymbol item : WebsterSymbol.values()){
                %>
                    <li><a target="_blank" href="symbol-search.jsp?symbol=<%=item.name()%>&dictionary=WEBSTER&words_type=primary_school"><%=item.name()%></a></li>
                <%
                    }
                %>
                <ol>
            </td>
            <td valign="top">
                <ol>
                    <%
                        for(IcibaSymbol item : IcibaSymbol.values()){
                    %>
                    <li><a target="_blank" href="symbol-search.jsp?symbol=<%=item.name()%>&dictionary=ICIBA&words_type=primary_school"><%=item.name()%></a></li>
                    <%
                        }
                    %>
                </ol>
            </td>
            <td valign="top">
                <ol>
                    <%
                        for(YoudaoSymbol item : YoudaoSymbol.values()){
                    %>
                    <li><a target="_blank" href="symbol-search.jsp?symbol=<%=item.name()%>&dictionary=YOUDAO&words_type=primary_school"><%=item.name()%></a></li>
                    <%
                        }
                    %>
                </ol>
            </td>
        </tr>
    </table>

    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
