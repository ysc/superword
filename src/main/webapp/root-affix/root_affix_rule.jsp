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
<%@ page import="org.apdplat.superword.rule.RootAffixRule" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Word word = new Word(request.getParameter("word"), "");
    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    boolean strict = "Y".equalsIgnoreCase(request.getParameter("strict"));
    Map<Word, List<Word>> rootAffix = (Map<Word, List<Word>>)application.getAttribute("rootAffix_"+strict);
    if(rootAffix == null){
        rootAffix = RootAffixRule.getWord(WordSources.getAll(), strict);
        application.setAttribute("rootAffix_"+strict, rootAffix);
    }
    List<Word> data = rootAffix.get(word);
    String htmlFragment = "";
    if(data != null && data.size() > 0){
        Map<Word, List<Word>> temp = new HashMap<Word, List<Word>>();
        temp.put(word, data);
        htmlFragment = HtmlFormatter.toHtmlTableFragmentForIndependentWord(temp, column, Integer.MAX_VALUE, WordLinker.getValidDictionary(request.getParameter("dict"))).get(0);
    }else{
        htmlFragment = WordLinker.toLink(word.getWord(), WordLinker.getValidDictionary(request.getParameter("dict")));
    }
%>
<html>
<head>
    <title>词根词缀分析规则</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var dict = document.getElementById("dict").value;
            var column = document.getElementById("column").value;
            var strict = document.getElementById("strict").value;

            if(word == ""){
                return;
            }
            location.href = "root_affix_rule.jsp?word="+word+"&dict="+dict+"&column="+column+"&strict="+strict;
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
    <jsp:include page="../common/head.jsp"/>
    <p>
        ***用法说明:
        词根词缀分析规则，分析单词可能拥有的所有前缀、后缀和词根
    </p>
    <p>
        <font color="red">输入单词：</font><input onchange="update();" id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">严格匹配：</font>
        <jsp:include page="../select/strict-select.jsp"/><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="../select/dictionary-select.jsp"/><br/>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
