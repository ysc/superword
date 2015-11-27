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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String userName = (String) session.getAttribute("userName");
    userName = userName==null?"anonymity":userName;

    List<UserWord> userWords = MySQLUtils.getHistoryUserWordsFromDatabase(userName);
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table>");
    htmlFragment.append("<tr><th>序号</th><th>单词</th><th>词典</th><th>时间</th></tr>");
    int i = 1;
    for (UserWord userWord : userWords) {
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append(WordLinker.toLink(userWord.getWord(), WordLinker.getValidDictionary(request.getParameter("dict"))))
                .append("</td><td>")
                .append(WordLinker.toLink(userWord.getWord(), WordLinker.getValidDictionary(userWord.getDictionary())))
                .append("(")
                .append(userWord.getDictionary())
                .append(")")
                .append("</td><td>")
                .append(userWord.getDateTimeString())
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <title>用户查词记录</title>
    <script src="js/statistics.js"></script>
    <script type="text/javascript">
        function update(){
            var dict = document.getElementById("dict").value;

            if(dict == ""){
                return;
            }
            location.href = "user-word-history.jsp?dict="+dict;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body>
<jsp:include page="../common/head.jsp"/>
<p>用户 <%=userName%> 查词记录</p>
<p>
    <font color="red">选择词典：</font>
    <jsp:include page="../select/dictionary-select.jsp"/><br/>
</p>
<%=htmlFragment%>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
