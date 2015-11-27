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
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.apdplat.extractor.html.HtmlFetcher" %>
<%@ page import="org.apdplat.extractor.html.impl.HtmlUnitHtmlFetcher" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String url = request.getParameter("url");
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
    String text = "";
    String htmlFragment = "";
    try{
        HtmlFetcher htmlFetcher = new HtmlUnitHtmlFetcher();

        text = Jsoup.parse(htmlFetcher.fetch(url)).text();
        if(StringUtils.isBlank(text)){
            htmlFragment = "获取网页内容失败请重新输入其他网页地址";
        }else{
            htmlFragment = AidReading.analyse(words, WordLinker.getValidDictionary(request.getParameter("dict")), column, false, null, Arrays.asList(text));
        }
    }catch (Exception e){
        htmlFragment = "获取网页内容失败请重新输入其他网页地址";
        e.printStackTrace();
    }
%>

<html>
<head>
   <title>网页辅助阅读</title>
    <script src="js/statistics.js"></script>
    <script type="text/javascript">
        function update(){
            var words_type = document.getElementById("words_type").value;
            var dict = document.getElementById("dict").value;
            var url = document.getElementById("url").value;
            var column = document.getElementById("column").value;

            if(url == ""){
                return;
            }
            location.href = "web-aid-reading.jsp?words_type="+words_type+"&dict="+dict+"&url="+url+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
        var display = true;
        function change(){
            var text_div = document.getElementById("text_div");
            var tip = document.getElementById("tip");
            if(display){
                text_div.style.display = "none";
                tip.innerText = "网页内容(点击显示)：";
            }else{
                text_div.style.display = "block";
                tip.innerText = "网页内容(点击隐藏)：";
            }
            display = !display;
        }
    </script>
</head>
<body>
    <jsp:include page="../common/head.jsp"/>

    <p>
        网页辅助阅读
    </p>

    <p>
        <font color="red">网页地址：</font><input onchange="update();" id="url" name="url" value="<%=url%>" size="150" maxlength="500"><br/>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="../select/dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="../select/words-select.jsp"/><br/>
    </p>
    <p>
        <font color="red"><span style="cursor: pointer" onclick="change();" id="tip">网页内容(点击隐藏)：</span></font><br/>
        <div id="text_div" style="display:block">
            <%=text%>
        </div>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
