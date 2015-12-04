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
<%@ page import="org.apdplat.superword.tools.Definition" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.UserWord" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String word = request.getParameter("word");
    if(word == null){
        word = "fantastic";
    }
    word = word.trim();

    User user = (User)request.getSession().getAttribute("user");

    UserWord userWord = new UserWord();
    userWord.setDateTime(new Date());
    userWord.setUserName(user==null?"anonymity":user.getUserName());
    userWord.setWord(word);
    MySQLUtils.saveUserWordToDatabase(userWord);

    StringBuilder definitionHtmls = new StringBuilder();

    StringBuilder otherDictionary = new StringBuilder();
    for(Dictionary dictionary : Dictionary.values()){
        if(dictionary == Dictionary.ICIBA
                || dictionary == Dictionary.YOUDAO
                || dictionary == Dictionary.OXFORD
                || dictionary == Dictionary.WEBSTER){
            continue;
        }
        String definitionURL = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(dictionary)+word+"&word="+word+"&dict="+dictionary.name();
        String definitionHtml = "<a href=\"#"+ UUID.randomUUID()+"\" onclick=\"openWindow('"+definitionURL+"', '"+word+"');\">"+dictionary.getDes()+"</a>";
        otherDictionary.append(definitionHtml).append(" | ");
    }
    otherDictionary.setLength(otherDictionary.length() - 3);

    String icibaLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.ICIBA);
    String icibaDefinitionURL = icibaLinkPrefix+word+"&word="+word+"&dict="+Dictionary.ICIBA.name();
    String icibaDefinitionHtml = "<a href=\"#"+ UUID.randomUUID()+"\" onclick=\"openWindow('"+icibaDefinitionURL+"', '"+word+"');\">爱词霸解释</a>";

    String youdaoLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.YOUDAO);
    String youdaoDefinitionURL = youdaoLinkPrefix+word+"&word="+word+"&dict="+Dictionary.YOUDAO.name();
    String youdaoDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+youdaoDefinitionURL+"', '"+word+"');\">有道解释</a>";

    String oxfordLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.OXFORD);
    String oxfordDefinitionURL = oxfordLinkPrefix+word+"&word="+word+"&dict="+Dictionary.OXFORD.name();
    String oxfordDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+oxfordDefinitionURL+"', '"+word+"');\">牛津解释</a>";

    String websterLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.WEBSTER);
    String websterDefinitionURL = websterLinkPrefix+word+"&word="+word+"&dict="+Dictionary.WEBSTER.name();
    String websterDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+websterDefinitionURL+"', '"+word+"');\">韦氏解释</a>";

    definitionHtmls.append("<table border=\"1\">");

    definitionHtmls.append("<tr><td>")
            .append(icibaDefinitionHtml)
            .append("</td><td>")
            .append(youdaoDefinitionHtml)
            .append("</td></tr>")
            .append("<tr><td ondblclick=\"querySelectionWord();\">")
            .append(Definition.getDefinitionString(Dictionary.ICIBA, word, "<br/>"))
            .append("</td><td ondblclick=\"querySelectionWord();\">")
            .append(Definition.getDefinitionString(Dictionary.YOUDAO, word, "<br/>"))
            .append("</td></tr>");

    definitionHtmls.append("<tr><td>")
            .append(oxfordDefinitionHtml)
            .append("</td><td>")
            .append(websterDefinitionHtml)
            .append("</td></tr>")
            .append("<tr><td ondblclick=\"querySelectionWord();\">")
            .append(Definition.getDefinitionString(Dictionary.OXFORD, word, "<br/>"))
            .append("</td><td ondblclick=\"querySelectionWord();\">")
            .append(Definition.getDefinitionString(Dictionary.WEBSTER, word, "<br/>"))
            .append("</td></tr>");

    definitionHtmls.append("</table>")
            .append("<br/><br/>");

    definitionHtmls.append("<font color=\"red\">其他英文词典解释: </font>").append(otherDictionary.toString());
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
        <font color="red">输入单词：</font><input onchange="query();" id="word" name="word" value="<%=word%>" size="50" maxlength="50"/>
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
