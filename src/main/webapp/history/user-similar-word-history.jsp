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

<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.superword.model.UserSimilarWord" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String userName = (String) session.getAttribute("userName");
    List<UserSimilarWord> userSimilarWords = MySQLUtils.getHistoryUserSimilarWordsFromDatabase(userName);
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table>");
    htmlFragment.append("<tr><th>序号</th><th>单词</th><th>时间</th></tr>");
    int i = 1;
    for (UserSimilarWord userSimilarWord : userSimilarWords) {
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append("<a target=\"_blank\" href=\"../similar-word-rule.jsp?count=10&dict=ICIBA&words_type=CET4&word=")
                .append(userSimilarWord.getSimilarWord())
                .append("\">")
                .append(userSimilarWord.getSimilarWord())
                .append("</a>")
                .append("</td><td>")
                .append(userSimilarWord.getDateTimeString())
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <title>用户相似单词分析记录</title>
</head>
<body>
<jsp:include page="../common/head.jsp"/>
<p>用户 <%=userName%> 相似单词分析记录</p>
<%=htmlFragment%>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
