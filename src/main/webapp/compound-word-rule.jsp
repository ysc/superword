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
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    String word = request.getParameter("word");
    String all = request.getParameter("all");
    if(word == null && all == null){
        return;
    }
    String word_type = request.getAttribute("words_type").toString();
    Set<Word> words = (Set<Word>)application.getAttribute("words_"+word_type);
    String key = "compound_"+word_type;

    Map<Word, Map<Integer, List<Word>>> compound = (Map<Word, Map<Integer, List<Word>>>)application.getAttribute(key);
    if(compound == null){
        synchronized (this){
            if(compound == null){
                compound = CompoundWord.find(words, words);
                application.setAttribute(key, compound);
            }
        }
    }

    String htmlFragment = "";
    if("true".equals(all)){
        htmlFragment = HtmlFormatter.toHtmlForCompoundWord(compound, column);
    }else{
        if(word != null && word.length() > 3){
            Word w = new Word(word, "");

            Map<Integer, List<Word>> data = compound.get(w);
            if(data != null && data.size() > 0) {
                Map<Word, Map<Integer, List<Word>>> temp = new HashMap<Word, Map<Integer, List<Word>>>();
                temp.put(w, data);
                htmlFragment = HtmlFormatter.toHtmlForCompoundWord(temp, column);
            }else{
                htmlFragment = "<font color=\"red\">decomposition failed，please try other words level: </font>"+WordLinker.toLink(w.getWord());
            }
        }else{
            htmlFragment = "<font color=\"red\">the length of word must greater than three</font>";
        }
    }
%>
<html>
<head>
    <title>compound word analysis rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var words_type = document.getElementById("words_type").value;
            var column = document.getElementById("column").value;

            if(word == ""){
                return;
            }
            location.href = "compound-word-rule.jsp?all=false&word="+word+"&words_type="+words_type+"&column="+column;
        }
        function viewAllCompound(){
            var word = document.getElementById("word").value;
            var words_type = document.getElementById("words_type").value;
            var column = document.getElementById("column").value;

            location.href = "compound-word-rule.jsp?all=true&word="+word+"&words_type="+words_type+"&column="+column;
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
        ***compound word analysis rule:
        A compound word is the result of two words being joined.
    </p>
    <p>
        <font color="red">input word: </font><input onchange="update();" id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">words per line: </font><input onchange="viewAllCompound();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="select/words-select.jsp"/><br/>
    </p>
    <p>
        <a href="#1" onclick="update();"><font color="red">decomposition the input word</font></a><br/>
        <a href="#2" onclick="viewAllCompound();"><font color="red">view all compound word</font></a>
    </p>
    <%=htmlFragment%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
