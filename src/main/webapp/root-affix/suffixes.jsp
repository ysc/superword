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
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="java.util.UUID" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
  List<Suffix> suffixes = SuffixRule.getAllSuffixes();
  StringBuilder stringBuilder = new StringBuilder();
  stringBuilder.append("<table>\n");
  stringBuilder.append("<tr align=\"left\"><th>No.</th><th>Suffix</th><th>Chinese Meaning</th><th>English Oxford Meaning</th></tr>");

  String oxfordLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(WordLinker.Dictionary.OXFORD);

  int i=1;
  for(Suffix suffix : suffixes){
      String oxfordDefinitionURL = oxfordLinkPrefix+suffix.getSuffix()+"&word="+suffix.getSuffix()+"&dict="+ WordLinker.Dictionary.OXFORD.name();
      String oxfordDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+oxfordDefinitionURL+"', '"+suffix.getSuffix()+"');\">"+suffix.getSuffix()+"</a>";
    stringBuilder.append("<tr><td>")
            .append(i++)
            .append("</td><td>")
            .append("<a target=\"_blank\" href=\"suffix-rule.jsp?dict=ICIBA&words_type=SYLLABUS&strict=Y&column=6&suffixes=")
            .append(suffix.getSuffix())
            .append("\">")
            .append(suffix.getSuffix())
            .append("</a>")
            .append("</td><td>")
            .append(suffix.getDes().replace(";", ";<br/>"))
            .append("</td><td>")
            .append(oxfordDefinitionHtml)
            .append("</td></tr>\n");
  }
  stringBuilder.append("</table>\n");
%>

<html>
<head>
    <title>commonly used suffix</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
  <jsp:include page="../common/head.jsp"/>
  <h3>commonly used suffix</h3>
  <%=stringBuilder.toString()%>
  <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
