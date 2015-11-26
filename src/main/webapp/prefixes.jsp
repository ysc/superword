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

<%@ page import="org.apdplat.superword.model.Prefix" %>
<%@ page import="org.apdplat.superword.rule.PrefixRule" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
  List<Prefix> prefixes = PrefixRule.getAllPrefixes();
  StringBuilder stringBuilder = new StringBuilder();
  stringBuilder.append("<table>\n");
  stringBuilder.append("<tr><th>序号</th><th>前缀</th><th>含义</th><th>英文牛津含义</th></tr>");
  int i=1;
  for(Prefix prefix : prefixes){
    stringBuilder.append("<tr><td>")
            .append(i++)
            .append("</td><td>")
            .append("<a target=\"_blank\" href=\"prefix-rule.jsp?dict=ICIBA&words_type=SYLLABUS&strict=Y&column=6&prefixes=")
            .append(prefix.getPrefix())
            .append("\">")
            .append(prefix.getPrefix())
            .append("</a>")
            .append("</td><td>")
            .append(prefix.getDes().replace(";", ";<br/>"))
            .append("</td></tr>\n");
  }
  stringBuilder.append("</table>\n");
%>

<html>
<head>
    <title>常见前缀</title>
</head>
<body>
  <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>
  <%=stringBuilder.toString()%>
  <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
