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

<%@ page import="org.apdplat.superword.tools.AidReading" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.net.URLDecoder" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String text = request.getParameter("text");
    text = URLDecoder.decode(text, "utf-8");
    String words_type = request.getParameter("words_type");
    if(words_type == null){
        words_type = "ALL";
    }
    request.setAttribute("words_type", words_type.trim());
    String key = "words_"+words_type;
    Set<Word> words = (Set<Word>)session.getAttribute(key);
    if(words == null){
        if("ALL".equals(words_type.trim())){
            words = WordSources.getAll();
        }else if("SYLLABUS".equals(words_type.trim())){
            words = WordSources.getSyllabusVocabulary();
        }else{
            String resource = "/word_"+words_type+".txt";
            words = WordSources.get(resource);
        }
        session.setAttribute(key, words);
    }
    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    String htmlFragment = AidReading.analyse(words, WordLinker.getValidDictionary(request.getParameter("dict")), column, false, null, Arrays.asList(text));
%>

<html>
<head>
   <title>网页辅助阅读</title>

    <script type="text/javascript">
        function update(){
            var text = document.getElementById("text").value;
            if(text == ""){
                return;
            }
            text = encodeURIComponent(text);
            document.getElementById("text").value = text;
            document.getElementById("form").submit();
        }
        var display = true;
        function change(){
            var text_div = document.getElementById("text_div");
            var tip = document.getElementById("tip");
            if(display){
                text_div.style.display = "none";
                tip.innerText = "显示文本：";
            }else{
                text_div.style.display = "block";
                tip.innerText = "隐藏文本：";
            }
            display = !display;
        }
    </script>
</head>
<body>
    <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>

    <p>
        文本辅助阅读
    </p>

    <form method="post" id="form" action="text-aid-reading.jsp">
        <p>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"/><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/><br/>
        </p>
        <font color="red"><span style="cursor: pointer" onclick="change();" id="tip">隐藏文本：</span></font>
        <div id="text_div" style="display:block">
            <textarea id="text" name="text" rows="13" cols="100"  maxlength="10000"><%=text%></textarea><br/>
            <span style="cursor: pointer" onclick="update();"><font color="red">确定分析文本</font></span>
        </div>
    </form>
    <%=htmlFragment%>
    <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
