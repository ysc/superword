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
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.rule.RootRule" %>
<%@ page import="org.apdplat.superword.tools.HtmlFormatter" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String roots = request.getParameter("roots");
    String htmlFragment = "";
    int column = 10;
    if(roots != null && !"".equals(roots.trim())){
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
        Map<String, Word> map = (Map<String, Word>)application.getAttribute("all_root");
        if(map == null){
            map = new ConcurrentHashMap<String, Word>();
            for(Word root : RootRule.getAllRoots()){
                map.put(root.getWord().replace("-", ""), root);
            }
            application.setAttribute("all_root", map);
        }
        List<Word> rootList = new ArrayList<Word>();
        for(String root : roots.trim().split(",")){
            rootList.add(new Word(root, map.get(root.replace("-", ""))==null?"":map.get(root.replace("-", "")).getMeaning()));
        }
        TreeMap<Word, List<Word>> data = RootRule.findByRoot(words, rootList);
        for(Map.Entry<Word, List<Word>> entry : data.entrySet()){
            if(entry.getValue().size() > 500) {
                entry.setValue(entry.getValue().subList(0, 500));
            }
        }
        try{
            column = Integer.parseInt(request.getParameter("column"));
        }catch (Exception e){}
        htmlFragment = HtmlFormatter.toHtmlTableFragmentForRootAffix(data, column, WordLinker.getValidDictionary(request.getParameter("dict")));
    }
%>
<html>
<head>
    <title>词根规则</title>
    <script src="js/statistics.js"></script>
    <script type="text/javascript">
        function update(){
            var roots = document.getElementById("roots").value;
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;
            var column = document.getElementById("column").value;

            if(roots == ""){
                return;
            }
            location.href = "root-rule.jsp?roots="+roots+"&dict="+dict+"&words_type="+words_type+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body>
    <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>
    <p>
        ***用法说明:
        词根规则，从指定的英文单词的集合中找出符合词根规则的单词，
        如：son或者spir，多个后缀可用逗号分隔，如：spect,spic
    </p>
    <p>
        <font color="red">输入词根：</font><input onchange="update();" id="roots" name="roots" value="<%=roots==null?"":roots%>" size="50" maxlength="50"><br/>
        <font color="red">每行词数：</font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/>
    </p>
    <%=htmlFragment%>
    <p><a target="_blank" href="index.jsp">主页</a></p>
</body>
</html>
