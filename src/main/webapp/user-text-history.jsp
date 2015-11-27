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

<%@ page import="org.apdplat.superword.model.UserText" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String userName = (String) session.getAttribute("userName");
    userName = userName == null ? "ysc" : userName;

    List<UserText> userTexts = MySQLUtils.getHistoryUseTextsFromDatabase(userName);
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table>");
    htmlFragment.append("<tr><th>序号</th><th>分析文本</th><th>时间</th></tr>");
    int i = 1;
    for (UserText userText : userTexts) {
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append("<a target=\"_blank\" href=\"text-aid-reading.jsp?words_type=CET4&dict=ICIBA&column=6&id=")
                .append(userText.getId())
                .append("\">")
                .append(userText.getText().substring(0, 100))
                .append("...</a>")
                .append("</td><td>")
                .append(userText.getDateTimeString())
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <script src="js/statistics.js"></script>
    <title>用户文本分析记录</title>
</head>
<body>
<jsp:include page="head.jsp"/>
<p>用户 <%=userName%> 文本分析记录</p>
<%=htmlFragment%>
<jsp:include page="bottom.jsp"/>
</body>
</html>
