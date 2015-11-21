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

<%@ page import="org.apdplat.superword.model.Suffix" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.superword.rule.SuffixRule" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
  List<Suffix> suffixes = SuffixRule.getAllSuffixes();
  StringBuilder stringBuilder = new StringBuilder();
  stringBuilder.append("<table>\n");
  stringBuilder.append("<tr><th>序号</th><th>后缀</th><th>含义</th></tr>");
  int i=1;
  for(Suffix suffix : suffixes){
    stringBuilder.append("<tr><td>")
            .append(i++)
            .append("</td><td>")
            .append("<a target=\"_blank\" href=\"suffix-rule.jsp?dict=ICIBA&words_type=SYLLABUS&strict=Y&column=6&suffixes=")
            .append(suffix.getSuffix())
            .append("\">")
            .append(suffix.getSuffix())
            .append("</a>")
            .append("</td><td>")
            .append(suffix.getDes())
            .append("</td></tr>\n");
  }
  stringBuilder.append("</table>\n");
%>

<html>
<head>
    <title>常见后缀</title>
</head>
<body>
  <%=stringBuilder.toString()%>
  <p><a href="index.jsp">主页</a></p>
</body>
</html>
