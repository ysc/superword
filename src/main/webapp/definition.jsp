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
<%@ page import="java.util.UUID" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.UserWord" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.*" %>

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
    String icibaDefinitionHtml = "<a href=\"#"+ UUID.randomUUID()+"\" onclick=\"openWindow('"+icibaDefinitionURL+"', '"+word+"');\">iCIBA definition</a>";

    String youdaoLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.YOUDAO);
    String youdaoDefinitionURL = youdaoLinkPrefix+word+"&word="+word+"&dict="+Dictionary.YOUDAO.name();
    String youdaoDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+youdaoDefinitionURL+"', '"+word+"');\">Youdao definition</a>";

    String oxfordLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.OXFORD);
    String oxfordDefinitionURL = oxfordLinkPrefix+word+"&word="+word+"&dict="+Dictionary.OXFORD.name();
    String oxfordDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+oxfordDefinitionURL+"', '"+word+"');\">Oxford definition</a>";

    String websterLinkPrefix = WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(Dictionary.WEBSTER);
    String websterDefinitionURL = websterLinkPrefix+word+"&word="+word+"&dict="+Dictionary.WEBSTER.name();
    String websterDefinitionHtml = "<a href=\"#" + UUID.randomUUID()+"\" onclick=\"openWindow('"+websterDefinitionURL+"', '"+word+"');\">Webster's definition</a>";

    definitionHtmls.append("<table border=\"1\">");

    definitionHtmls.append("<tr><td>")
            .append(icibaDefinitionHtml)
            .append("</td><td>")
            .append(youdaoDefinitionHtml)
            .append("</td></tr>")
            .append("<tr><td ondblclick=\"querySelectionWord();\">")
            .append("phonetic symbol: <br/>")
            .append(Pronunciation.getPronunciationString(Dictionary.ICIBA, word, " <font color=\"red\">|</font> "))
            .append("<br/><br/>")
            .append("definition(Chinese): <br/>")
            .append(Definition.getDefinitionString(Dictionary.ICIBA, word, "<br/>"))
            .append("</td><td ondblclick=\"querySelectionWord();\">")
            .append("phonetic symbol: <br/>")
            .append(Pronunciation.getPronunciationString(Dictionary.YOUDAO, word, " <font color=\"red\">|</font> "))
            .append("<br/><br/>")
            .append("definition(Chinese): <br/>")
            .append(Definition.getDefinitionString(Dictionary.YOUDAO, word, "<br/>"))
            .append("</td></tr>");

    definitionHtmls.append("<tr><td>")
            .append(oxfordDefinitionHtml)
            .append("</td><td>")
            .append(websterDefinitionHtml)
            .append("</td></tr>")
            .append("<tr><td>")
            .append("<tr><td ondblclick=\"querySelectionWord();\">")
            .append("phonetic symbol: <br/>")
            .append(Pronunciation.getPronunciationString(Dictionary.OXFORD, word, " <font color=\"red\">|</font> "))
            .append("<br/><br/>")
            .append("definition: <br/>")
            .append(OxfordPOS.highlight(Definition.getDefinitionString(Dictionary.OXFORD, word, "<br/>")))
            .append("</td><td ondblclick=\"querySelectionWord();\">")
            .append("phonetic symbol: <br/>")
            .append(Pronunciation.getPronunciationString(Dictionary.WEBSTER, word, " <font color=\"red\">|</font> "))
            .append("<br/><br/>")
            .append("definition: <br/>")
            .append(WebsterPOS.highlight(Definition.getDefinitionString(Dictionary.WEBSTER, word, "<br/>")))
            .append("</td></tr>");

    definitionHtmls.append("</table>")
            .append("<br/><br/>");

    definitionHtmls.append("<font color=\"red\">Other English Dictionaries's definition: </font>").append(otherDictionary.toString());
%>

<html>
<head>
    <title>word definition</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        var lock = false;
        function query() {
            var word = document.getElementById("word").value;
            if (word == "") {
                document.getElementById("tip").innerText = "Please input the word which you want to query.";
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
        <font color="red">Input Word：</font><input onchange="query();" id="word" name="word" value="<%=word%>" size="50" maxlength="50"/>
        <button type="button" onclick="query();">Query definition</button><br/>
    </p>
    <script type="text/javascript">
        document.getElementById('word').select();
        document.getElementById('word').focus();
    </script>

    <%=definitionHtmls.toString()%>
    <br/>
    <a target="_blank" href="<%=request.getContextPath()%>/root-affix/root_affix_rule.jsp?dict=ICIBA&word=<%=word%>&column=6&strict=N"><font color="red">analyze roots and affix</font></a><br/>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/spell-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS"><font color="red">similar spelling</font></a><br/>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/definition-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS&dictionary=WEBSTER"><font color="red">similar definition</font></a><br/>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/pronunciation-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS&dictionary=ICIBA"><font color="red">similar pronunciation</font></a><br/>
    <br/>
    <a target="_blank" href="pos.jsp">Comparison of part of speech symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a><br/>
    <a target="_blank" href="symbol.jsp">Comparison of phonetic symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
