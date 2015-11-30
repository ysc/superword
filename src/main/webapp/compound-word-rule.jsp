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
<%@ page import="org.apdplat.superword.rule.CompoundWord" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Word word = new Word(request.getParameter("word"), "");
    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    Map<Word, Map<Integer, List<Word>>> compound = (Map<Word, Map<Integer, List<Word>>>)application.getAttribute("compound");
    if(compound == null){
        compound = CompoundWord.find(WordSources.getAll(), WordSources.getAll());
        application.setAttribute("compound", compound);
    }
    Map<Integer, List<Word>> data = compound.get(word);
    String htmlFragment = "";
    if(data != null && data.size() > 0) {
        Map<Word, Map<Integer, List<Word>>> temp = new HashMap<Word, Map<Integer, List<Word>>>();
        temp.put(word, data);
        htmlFragment = HtmlFormatter.toHtmlForCompoundWord(temp, WordLinker.getValidDictionary(request.getParameter("dict")));
    }else{
        htmlFragment = WordLinker.toLink(word.getWord(), WordLinker.getValidDictionary(request.getParameter("dict")));
    }
%>
<html>
<head>
    <title>复合词分析规则</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var dict = document.getElementById("dict").value;
            var column = document.getElementById("column").value;

            if(word == ""){
                return;
            }
            location.href = "compound-word-rule.jsp?word="+word+"&dict="+dict+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body id="back-to-top">
    <jsp:include page="common/head.jsp"/>
    <p>
        ***用法说明:
        复合词分析规则，判断一个词是不是复合词就看它是不是由2个或2个以上现有词简单拼装在一起形成的词
    </p>
    <p>
        <font color="red">输入单词：</font><input onchange="update();" id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="select/dictionary-select.jsp"/><br/>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
    </p>
    <%=htmlFragment%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
