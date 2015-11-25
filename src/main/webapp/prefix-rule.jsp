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

<%@ page import="org.apdplat.superword.model.Prefix" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.PrefixRule" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String prefixes = request.getParameter("prefixes");
    String htmlFragment = "";
    int column = 10;
    if(prefixes != null && !"".equals(prefixes.trim())){
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
        List<Prefix> prefixList = new ArrayList<Prefix>();
        Map<String, Prefix> map = (Map<String, Prefix>)application.getAttribute("all_prefix");
        if(map == null){
            map = new ConcurrentHashMap<String, Prefix>();
            for(Prefix prefix : PrefixRule.getAllPrefixes()){
                map.put(prefix.getPrefix().replace("-", ""), prefix);
            }
            application.setAttribute("all_prefix", map);
        }
        for(String prefix : prefixes.trim().split(",")){
            prefixList.add(new Prefix(prefix, map.get(prefix.replace("-", ""))==null?"":map.get(prefix.replace("-", "")).getDes()));
        }
        TreeMap<Prefix, List<Word>> data = PrefixRule.findByPrefix(words, prefixList, "N".equalsIgnoreCase(request.getParameter("strict")) ? false : true);
        for(Map.Entry<Prefix, List<Word>> entry : data.entrySet()){
            if(entry.getValue().size() > 500) {
                entry.setValue(entry.getValue().subList(0, 500));
            }
        }
        try{
            column = Integer.parseInt(request.getParameter("column"));
        }catch (Exception e){}
        htmlFragment = HtmlFormatter.toHtmlTableFragmentForRootAffix(PrefixRule.convert(data), column, WordLinker.getValidDictionary(request.getParameter("dict")));
    }
%>
<html>
<head>
    <title>前缀规则</title>
    <script type="text/javascript">
        function submit(){
            var prefixes = document.getElementById("prefixes").value;
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;
            var strict = document.getElementById("strict").value;
            var column = document.getElementById("column").value;

            if(prefixes == ""){
                return;
            }
            location.href = "prefix-rule.jsp?prefixes="+prefixes+"&dict="+dict+"&words_type="+words_type+"&strict="+strict+"&column="+column;
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
        前缀规则，从指定的英文单词的集合中找出符合前缀规则的单词，
        如：pro或者pre，多个前缀可用逗号分隔，如：anti,counter,de,dis,il,im,in,ir,mis,non,un
    </p>
    <p>
        <font color="red">输入前缀：</font><input onchange="submit();" id="prefixes" name="prefixes" value="<%=prefixes==null?"":prefixes%>" size="50" maxlength="50"><br/>
        <font color="red">每行词数：</font><input onchange="submit();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">严格匹配：</font>
        <jsp:include page="strict-select.jsp"/><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/>
    </p>
    <%=htmlFragment%>
    <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
