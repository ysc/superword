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
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User)request.getSession().getAttribute("user");
    
    String newWord = request.getParameter("new_word");
    if(StringUtils.isNotBlank(newWord)
            && user != null
            && StringUtils.isNotBlank(user.getUserName())){
        MyNewWord myNewWord = new MyNewWord();
        myNewWord.setWord(newWord);
        myNewWord.setDateTime(new Date());
        myNewWord.setUserName(user.getUserName());
        MySQLUtils.saveMyNewWordsToDatabase(myNewWord);
        out.println(newWord+" has been added to <a href=\"history/my-new-words-book.jsp\">my new words book</a>");
        return;
    }
    
    String word = request.getParameter("word");
    if(StringUtils.isBlank(word)){
        word = "fantastic";
    }
    word = word.trim();

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
            .append("<br/>");

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
        var loc = false;
        function addToMyNewWordsBook(word){
            if (loc) {
                return;
            }
            loc = true;
            $.ajax({
                url: "definition.jsp?new_word="+word, 
                success: function(result){
                    $("#action_add_to_my_new_words").html(result);
                }
            });
        }
        function instant(prefix){
            $.ajax({
                url: "word.instant?prefix="+prefix,
                success: function(result){
                    $("#instant_tip").html(result);
                }
            });
        }
    </script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>
    <p>
        <font color="red"><span id="tip"></span></font><br/>
        <font color="red">Input Word：</font><input onkeyup="instant(this.value);" onchange="query();" id="word" name="word" value="<%=word%>" size="50" maxlength="50" autocomplete="off"/>
        <span style="cursor: pointer" onclick="query();"><font color="red">Submit</font></span><br/>
        <div id="instant_tip"></div>
    </p>
    <script type="text/javascript">
        document.getElementById('word').select();
        document.getElementById('word').focus();
    </script>
    <%
        if(user != null){
            if(MySQLUtils.isMyNewWord(user.getUserName(), word)){
    %>
    <%=word%> has been added to <a href="history/my-new-words-book.jsp">my new words book</a><br/><br/>
    <%
            }else{
    %>
    <span id="action_add_to_my_new_words"><span onclick="addToMyNewWordsBook('<%=word%>');" style="cursor:pointer"><font color="red">add to my new words book</font></span></span><br/><br/>
    <%
            }
        }
    %>
    <font color="red">Word Level: <%=WordSources.getLevels(word)%></font><br/><br/>
    <a target="_blank" href="<%=request.getContextPath()%>/char-transform-rule.jsp?word=<%=word%>&words_type=SYLLABUS">transform character</a> <font color="red"> | </font>
    <a target="_blank" href="<%=request.getContextPath()%>/root-affix/root_affix_rule.jsp?dict=ICIBA&word=<%=word%>&column=6&strict=N">analyze roots and affix</a> <font color="red"> | </font>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/spell-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS">similar spelling</a> <font color="red"> | </font>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/definition-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS&dictionary=WEBSTER">similar definition</a> <font color="red"> | </font>
    <a target="_blank" href="<%=request.getContextPath()%>/similar/pronunciation-similar-rule.jsp?word=<%=word%>&count=100&words_type=SYLLABUS&dictionary=ICIBA">similar pronunciation</a><br/>
    <br/>
    <%=definitionHtmls.toString()%>
    <br/><br/>
    <a target="_blank" href="pos.jsp">Comparison of part of speech symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a><br/>
    <a target="_blank" href="symbol.jsp">Comparison of phonetic symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
