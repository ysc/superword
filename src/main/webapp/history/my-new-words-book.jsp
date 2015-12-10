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
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.QQUser" %>
<%@ page import="java.util.UUID" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User)session.getAttribute("user");

    String word = request.getParameter("word");
    if("true".equals(request.getParameter("delete"))
            && user != null
            && StringUtils.isNotBlank(user.getUserName())
            && StringUtils.isNotBlank(word)){
        MySQLUtils.deleteMyNewWord(user.getUserName(), word);
    }

    String displayName = user.getUserName();
    if(user instanceof QQUser){
        displayName = ((QQUser)user).getNickname();
    }
    List<MyNewWord> myNewWords = MySQLUtils.getMyNewWordsFromDatabase(user.getUserName());
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table border=\"1\" ondblclick=\"querySelectionWord();\">");
    htmlFragment.append("<tr><th>No.</th><th>Word</th><th>Chinese Meaning</th><th>English Meaning</th><th>Time</th><th>Delete</th></tr>");
    int i = 1;
    for (MyNewWord myNewWord : myNewWords) {
        String w = myNewWord.getWord();
        String englishMeaning = MySQLUtils.getWordDefinition(w, Dictionary.WEBSTER.name());
        if(StringUtils.isBlank(englishMeaning)){
            englishMeaning = MySQLUtils.getWordDefinition(w, Dictionary.OXFORD.name());
        }
        String chineseMeaning = MySQLUtils.getWordDefinition(w, Dictionary.YOUDAO.name());
        if(StringUtils.isBlank(chineseMeaning)){
            chineseMeaning = MySQLUtils.getWordDefinition(w, Dictionary.ICIBA.name());
        }
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append("<a  href=\"#")
                .append(UUID.randomUUID())
                .append("\" onclick=\"queryWord('")
                .append(w)
                .append("');\">")
                .append(w)
                .append("</a>")
                .append("</td><td>")
                .append(chineseMeaning)
                .append("</td><td>")
                .append(englishMeaning)
                .append("</td><td>")
                .append(myNewWord.getDateTimeString())
                .append("</td><td>")
                .append("<a  href=\"my-new-words-book.jsp?delete=true&word=")
                .append(w)
                .append("\">")
                .append("delete")
                .append("</a>")
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <title>my new words book</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
<jsp:include page="../common/head.jsp"/>
<h3>user <%=displayName%>'s new words book</h3>

<%=htmlFragment%>

<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
