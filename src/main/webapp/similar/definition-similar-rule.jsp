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
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.UserSimilarWord" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.DefinitionSimilarRule" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="java.util.*" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String word = request.getParameter("word");
    int count = 100;
    try{
        count = Integer.parseInt(request.getParameter("count"));
    }catch (Exception e){
        //none
    }
    if(count > 100){
        count = 100;
    }
    String wordsType = request.getParameter("words_type");
    Dictionary dictionary = WordLinker.getValidDictionary(request.getParameter("dictionary"));
    String htmlFragment = "";
    if(word != null && !"".equals(word.trim())){
        User user = (User)session.getAttribute("user");
        UserSimilarWord userSimilarWord = new UserSimilarWord();
        userSimilarWord.setSimilarWord(word);
        userSimilarWord.setDateTime(new Date());
        userSimilarWord.setUserName(user == null ? "anonymity" : user.getUserName());
        MySQLUtils.saveUserSimilarWordToDatabase(userSimilarWord);

        Set<Word> words = (Set<Word>)application.getAttribute("words_"+wordsType);
        String wordDefinition = MySQLUtils.getWordDefinition(word, dictionary.name());
        if(StringUtils.isBlank(wordDefinition)){
            htmlFragment = "We don't have the definition of the specified word, so the similarity can't be calculated. ";
        }else {
            if(dictionary == Dictionary.ICIBA || dictionary == Dictionary.YOUDAO) {
                if(user == null || !"ysc".equals(user.getUserName())){
                    out.println("You don't have the permission to access the functionality.<a href=\"definition-similar-rule.jsp?dictionary=WEBSTER&word="+word+"&count="+count+"&words_type="+request.getAttribute("words_type")+"\"> Return</a>");
                    return;
                }
            }
            List<DefinitionSimilarRule.Result> results = DefinitionSimilarRule.run(dictionary, words, wordDefinition, count);
            Map<String, Object> data = new HashMap<>();
            data.put("results", results);
            data.put("word", word);
            data.put("dictionary", dictionary.name());
            data.put("count", count);
            data.put("words_type", request.getAttribute("words_type"));

            htmlFragment = TemplateUtils.getDefinitionSimilarResult(data);
        }
    }
%>
<html>
<head>
    <title>definition similarity rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var count = document.getElementById("count").value;
            var words_type = document.getElementById("words_type").value;
            var dictionary = document.getElementById("dictionary").value;
            if(word == ""){
                return;
            }
            location.href = "definition-similar-rule.jsp?dictionary="+dictionary+"&word="+word+"&count="+count+"&words_type="+words_type;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
        // 异步加载下拉框
        $(document).ready(function(){
            $.get("../select/words-select.jsp?words_type=<%=wordsType%>", function (data, status) {
                $("#wordsLevel").html(data);
            });
            $.get("../select/dictionary-select-for-symbol.jsp?dictionary=<%=dictionary.name()%>", function (data, status) {
                $("#dictionaries").html(data);
            });
        });
    </script>
</head>
<body id="top">
    <jsp:include page="../common/head.jsp"/>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
