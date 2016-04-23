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

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apdplat.superword.freemarker.TemplateUtils" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.UserWord" %>
<%@ page import="org.apdplat.superword.tools.*" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="java.util.*" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User)request.getSession().getAttribute("user");
    
    String newWord = request.getParameter("new_word");
    if(StringUtils.isNotBlank(newWord)){
        if(user == null || StringUtils.isBlank(user.getUserName())){
            out.print("need_login");
            return;
        }
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

    Map<String, Object> data = new HashMap<>();
    data.put("word", word);
    data.put("servletContext", request.getContextPath());

    UserWord userWord = new UserWord();
    userWord.setDateTime(new Date());
    userWord.setUserName(user == null ? "anonymity" : user.getUserName());
    userWord.setWord(word);
    MySQLUtils.saveUserWordToDatabase(userWord);

    String file = application.getRealPath("/audio/oxford/"+word.toLowerCase()+".mp3");
    List<String> oxfordAudios = new ArrayList();
    if(Files.exists(Paths.get(file))){
        data.put("hasOxfordAudio", true);
        oxfordAudios.add(word.toLowerCase());
        int i=2;
        while(Files.exists(Paths.get(application.getRealPath("/audio/oxford/"+word.toLowerCase()+"_"+i+".mp3")))){
            oxfordAudios.add(word.toLowerCase()+"_"+i++);
        }
        data.put("oxfordAudios", oxfordAudios);
    }else{
        data.put("hasOxfordAudio", false);
    }

    file = application.getRealPath("/audio/webster/"+word.toLowerCase()+".mp3");
    List<String> websterAudios = new ArrayList();
    if(Files.exists(Paths.get(file))){
        data.put("hasWebsterAudio", true);
        websterAudios.add(word.toLowerCase());
        int i=2;
        while(Files.exists(Paths.get(application.getRealPath("/audio/webster/"+word.toLowerCase()+"_"+i+".mp3")))){
            websterAudios.add(word.toLowerCase()+"_"+i++);
        }
        data.put("websterAudios", websterAudios);
    }else{
        data.put("hasWebsterAudio", false);
    }

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

    data.put("icibaDefinitionHtml", icibaDefinitionHtml);
    data.put("youdaoDefinitionHtml", youdaoDefinitionHtml);
    data.put("icibaPronunciation", Pronunciation.getPronunciationString(Dictionary.ICIBA, word, " <font color=\"red\">|</font> "));
    data.put("icibaDefinition", Definition.getDefinitionString(Dictionary.ICIBA, word, "<br/>"));
    data.put("youdaoPronunciation", Pronunciation.getPronunciationString(Dictionary.YOUDAO, word, " <font color=\"red\">|</font> "));
    data.put("youdaoDefinition", Definition.getDefinitionString(Dictionary.YOUDAO, word, "<br/>"));
    data.put("oxfordDefinitionHtml", oxfordDefinitionHtml);
    data.put("websterDefinitionHtml", websterDefinitionHtml);
    data.put("oxfordPronunciation", Pronunciation.getPronunciationString(Dictionary.OXFORD, word, " <font color=\"red\">|</font> "));
    data.put("oxfordDefinition", OxfordPOS.highlight(Definition.getDefinitionString(Dictionary.OXFORD, word, "<br/>")));
    data.put("websterPronunciation", Pronunciation.getPronunciationString(Dictionary.WEBSTER, word, " <font color=\"red\">|</font> "));
    data.put("websterDefinition", WebsterPOS.highlight(Definition.getDefinitionString(Dictionary.WEBSTER, word, "<br/>")));
    data.put("otherDictionary", otherDictionary.toString());
    if(user != null && MySQLUtils.isMyNewWord(user.getUserName(), word)){
        data.put("isMyNewWord", true);
    }else{
        data.put("isMyNewWord", false);
    }
    data.put("wordLevels", WordSources.getLevels(word).toString());

    String html = TemplateUtils.getWordDefinition(data);
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
                    if(result.trim() == "need_login"){
                        location.href = "system/login.jsp";
                    }else {
                        $("#action_add_to_my_new_words").html(result);
                    }
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
    <%=html%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
