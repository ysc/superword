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

<%@ page import="org.apdplat.superword.model.Suffix" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.SuffixRule" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="org.apdplat.superword.rule.CompoundWord" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String dict = request.getParameter("dict");
    if(dict != null){
        WordLinker.dictionary = dict;
    }

    String words_type = request.getParameter("words_type");
    if(words_type == null){
        words_type = "all";
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
    Map<Word, Map<Integer, List<Word>>> data = CompoundWord.find(words, words);
    String htmlFragment = HtmlFormatter.toHtmlForCompoundWord(data);
%>
<html>
<head>
    <title>复合词规则</title>
    <script type="text/javascript">
        function submit(){
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;

            location.href = "suffix-rule.jsp?dict="+dict+"&words_type="+words_type;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                submit();
            }
        }
    </script>
</head>
<body>
    <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>
    <p>
        ***用法说明:
        复合词规则，由2个或2个以上现有词简单拼装在一起形成的词
    </p>
    <p>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/>
    </p>
    <p></p>
    <p><a href="#" onclick="submit();">提交</a></p>
    <%=htmlFragment%>
    <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
