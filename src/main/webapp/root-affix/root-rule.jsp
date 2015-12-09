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
<%@ page import="java.util.*" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String roots = request.getParameter("roots");
    String htmlFragment = "";
    int column = 10;
    if(roots != null && !"".equals(roots.trim())){
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
        Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));
        TreeMap<Word, List<Word>> data = RootRule.findByRoot(words, rootList);
        for(Map.Entry<Word, List<Word>> entry : data.entrySet()){
            if(entry.getValue().size() > 500) {
                entry.setValue(entry.getValue().subList(0, 500));
            }
        }
        try{
            column = Integer.parseInt(request.getParameter("column"));
        }catch (Exception e){}
        htmlFragment = HtmlFormatter.toHtmlTableFragmentForRootAffix(data, column);
    }
%>
<html>
<head>
    <title>roots rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var roots = document.getElementById("roots").value;
            var words_type = document.getElementById("words_type").value;
            var column = document.getElementById("column").value;

            if(roots == ""){
                return;
            }
            location.href = "root-rule.jsp?roots="+roots+"&words_type="+words_type+"&column="+column;
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
        ***roots rule:
        Find out the words that are consistent with the roots rule from the set of the specified English words, such as: son or spir, multiple roots can be separated by a comma, such as: spect,spic.
    </p>
    <p>
        <font color="red">input roots: </font><input onchange="update();" id="roots" name="roots" value="<%=roots==null?"":roots%>" size="50" maxlength="50"><br/>
        <font color="red">words per line: </font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/><br/>
        <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
