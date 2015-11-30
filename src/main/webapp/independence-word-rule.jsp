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

<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.IndependentWordRule" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String words_type = request.getParameter("words_type");
    if(words_type == null){
        words_type = "ALL";
    }
    request.setAttribute("words_type", words_type.trim());
    String key = "words_"+words_type;
    Set<Word> words = (Set<Word>)application.getAttribute(key);
    if(words == null){
        if("ALL".equals(words_type.trim())){
            words = WordSources.getAll();
        }else if("SYLLABUS".equals(words_type.trim())){
            words = WordSources.getSyllabusVocabulary();
        }else{
            String resource = "/word_"+words_type+".txt";
            words = WordSources.get(resource);
        }
        application.setAttribute(key, words);
    }
    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    List<String> data = IndependentWordRule.getIndependentWord(words, WordLinker.getValidDictionary(request.getParameter("dict")));
    String htmlFragment = "words("+data.size()+"):\n"+HtmlFormatter.toHtmlTableFragment(data, column);
%>

<html>
<head>
    <title>独立词规则</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var words_type = document.getElementById("words_type").value;
            var dict = document.getElementById("dict").value;
            var column = document.getElementById("column").value;

            if(words_type == ""){
                return;
            }
            location.href = "independence-word-rule.jsp?words_type="+words_type+"&dict="+dict+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>

    <p>
        ***用法说明:
        独立词规则，没有任何前缀、后缀和词根的词，这类词比较少,需要重点记忆
    </p>

    <p>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="select/dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="select/words-select.jsp"/><br/>
    </p>
    <%=htmlFragment%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
