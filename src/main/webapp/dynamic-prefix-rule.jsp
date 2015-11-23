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
<%@ page import="java.util.Set" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="org.apdplat.superword.model.Prefix" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.superword.rule.DynamicPrefixRule" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String dict = request.getParameter("dict");
    if(dict != null){
        WordLinker.dictionary = dict;
    }
    String prefixes = request.getParameter("prefixes");
    String htmlFragment = "";
    if(prefixes != null && !"".equals(prefixes.trim()) && prefixes.contains("-")){
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

        List<Prefix> prefixList = new ArrayList<Prefix>();
        for(String prefix : prefixes.trim() .split("-")){
            prefixList.add(new Prefix(prefix, ""));
        }
        List<Word> data = DynamicPrefixRule.findByPrefix(words, prefixList);
        if(data.size() > 500){
            data = data.subList(0, 500);
        }
        htmlFragment = DynamicPrefixRule.toHtmlFragment(data, prefixList);
    }
%>
<html>
<head>
    <title>动态前缀规则</title>
    <script type="text/javascript">
        function submit(){
            var prefixes = document.getElementById("prefixes").value;
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;
            if(prefixes == ""){
                return;
            }
            location.href = "dynamic-prefix-rule.jsp?prefixes="+prefixes+"&dict="+dict+"&words_type="+words_type;
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
        动态前缀规则，比如规则为：m-imm，
        表示单词集合中，有两个词分别以m和imm开始，
        且除了前缀外，其他部分都相同
    </p>
    <p>
        <font color="red">输入动态前缀：</font><input id="prefixes" name="prefixes" value="<%=prefixes==null?"":prefixes%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/>
    </p>
    <p></p>
    <p><a href="#" onclick="submit();">提交</a></p>
    <%=htmlFragment%>
    <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
