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
<%@ page import="org.apdplat.superword.model.UserWord" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.QQUser" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User)session.getAttribute("user");
    String displayName = user.getUserName();
    if(user instanceof QQUser){
        displayName = ((QQUser)user).getNickname();
    }
    List<UserWord> userWords = MySQLUtils.getHistoryUserWordsFromDatabase(user.getUserName());
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table>");
    htmlFragment.append("<tr><th>序号</th><th>单词</th><th>所有词典定义</th><th>时间</th></tr>");
    int i = 1;
    for (UserWord userWord : userWords) {
        String word = userWord.getWord();
        String definitionURL = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.valueOf(userWord.getDictionary()))+word+"&word="+word+"&dict="+Dictionary.valueOf(userWord.getDictionary()).name();
        String definitionHtml = "<span style=\"cursor:pointer;color:red\" onclick=\"viewDefinition('"+definitionURL+"', '"+word+"');\">"+userWord.getWord()+"("+Dictionary.valueOf(userWord.getDictionary()).getDes()+")"+"</span>";
        StringBuilder all = new StringBuilder();
        for(Dictionary dictionary : Dictionary.values()){
            String url = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(dictionary)+word+"&word="+word+"&dict="+dictionary.name();
            String html = "<span style=\"cursor:pointer;color:red\" onclick=\"viewDefinition('"+url+"', '"+word+"');\">"+dictionary.getDes()+"</span>";
            all.append(html).append(" | ");
        }
        all.setLength(all.length()-3);
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append(definitionHtml)
                .append("</td><td>")
                .append(all.toString())
                .append("</td><td>")
                .append(userWord.getDateTimeString())
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <title>用户查词记录</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function viewDefinition(url, word){
            window.open(url, word, 'width=1200,height=600');
        }
    </script>
</head>
<body id="top">
<jsp:include page="../common/head.jsp"/>
<p>用户 <%=displayName%> 查词记录</p>

<%=htmlFragment%>

<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
