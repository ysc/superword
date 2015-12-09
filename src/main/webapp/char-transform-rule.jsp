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
<%@ page import="org.apdplat.superword.model.CharMap" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.CharTransformRule" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String word = request.getParameter("word");
    if(StringUtils.isBlank(word)) {
        return;
    }

    Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

    Map<Word, Map<CharMap, List<Word>>> data = CharTransformRule.transforms(words, new Word(word, ""));

    String htmlFragment = CharTransformRule.toHtmlFragmentForWord(data);
%>

<html>
<head>
    <title>character transform rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>

    <script type="text/javascript">
        var lock = false;
        function update(){
            if(lock){
                return;
            }
            lock = true;
            var word = document.getElementById("word").value;
            if(word == ""){
                return;
            }
            document.getElementById("form").submit();
        }
    </script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>

    <h3>
        character transform rule
    </h3>

    <form method="post" id="form" action="char-transform-rule.jsp">
        <p>
            <font color="red">input word: </font><input onchange="update();" id="word" name="word" value="<%=word%>" size="50" maxlength="50"/><br/>
            <font color="red">select words level: </font>
            <jsp:include page="select/words-select.jsp"/><br/><br/>
            <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
        </p>
    </form>
    <%=htmlFragment%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
