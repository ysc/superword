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

<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="java.util.UUID" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String word = request.getParameter("word");
    if(word == null){
        word = "fantastic";
    }
    StringBuilder definitionHtmls = new StringBuilder();
    definitionHtmls.append("<table>")
                   .append("<tr><td>序号</td><td>词典</td><td>单词</td></tr>");
    int i=1;
    for(Dictionary dictionary : Dictionary.values()){
        String definitionURL = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(dictionary)+word+"&word="+word+"&dict="+dictionary.name();
        String definitionHtml = "<a href=\"#"+ UUID.randomUUID()+"\" onclick=\"viewDefinition('"+definitionURL+"', '"+word+"');\">"+word+"</a>";
        definitionHtmls.append("<tr><td>").append(i++).append("</td><td>").append(dictionary.getDes()).append("</td><td>").append(definitionHtml).append("</td></tr>");
    }
    definitionHtmls.append("</table>");
%>

<html>
<head>
    <title>定义查询</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        var lock = false;
        function query() {
            var word = document.getElementById("word").value;
            if (word == "") {
                document.getElementById("tip").innerText = "请输入查询定义的单词";
                return;
            }
            if (lock) {
                return;
            }
            lock = true;
            location.href = "definition.jsp?word="+word;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                query();
            }
        }
    </script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>
    <p>
        <font color="red"><span id="tip"></span></font><br/>
        <font color="red">输入单词：</font><input id="word" name="word" value="<%=word%>" size="50" maxlength="50"/><br/><br/>
        <button type="button" onclick="query();">查询定义</button><br/>
    </p>
    <script type="text/javascript">
        document.getElementById('word').select();
        document.getElementById('word').focus();
    </script>

    <%=definitionHtmls.toString()%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
