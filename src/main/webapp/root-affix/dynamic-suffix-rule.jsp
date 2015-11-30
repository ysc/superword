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
<%@ page import="org.apdplat.superword.model.Suffix" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.superword.rule.DynamicSuffixRule" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.model.UserDynamicSuffix" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String suffixes = request.getParameter("suffixes");
    String htmlFragment = "";
    if(suffixes != null && !"".equals(suffixes.trim()) && suffixes.contains("-")){
        String userName = (String)session.getAttribute("userName");
        UserDynamicSuffix userDynamicSuffix = new UserDynamicSuffix();
        userDynamicSuffix.setDynamicSuffix(suffixes);
        userDynamicSuffix.setDateTime(new Date());
        userDynamicSuffix.setUserName(userName == null ? "anonymity" : userName);
        MySQLUtils.saveUserDynamicSuffixToDatabase(userDynamicSuffix);

        String words_type = request.getParameter("words_type");
        if(words_type == null){
            words_type = "ALL";
        }
        request.setAttribute("words_type", words_type.trim());
        String key = "words_"+words_type;
        Set<Word> words = (Set<Word>)application.getAttribute(key);
        if(words == null){
            if("ALL".equals(words_type.trim())){
                words = WordSources.getAll();
            }else if("SYLLABUS".equals(words_type.trim())){
                words = WordSources.getSyllabusVocabulary();
            }else{
                String resource = "/word_"+words_type+".txt";
                words = WordSources.get(resource);
            }
            application.setAttribute(key, words);
        }

        List<Suffix> suffixList = new ArrayList<Suffix>();
        for(String suffix : suffixes.trim() .split("-")){
            suffixList.add(new Suffix(suffix, ""));
        }
        List<Word> data = DynamicSuffixRule.findBySuffix(words, suffixList);
        if(data.size() > 500){
            data = data.subList(0, 500);
        }
        htmlFragment = DynamicSuffixRule.toHtmlFragment(data, suffixList, WordLinker.getValidDictionary(request.getParameter("dict")));
    }
%>
<html>
<head>
    <title>动态后缀规则</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var suffixes = document.getElementById("suffixes").value;
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;
            if(suffixes == ""){
                return;
            }
            location.href = "dynamic-suffix-rule.jsp?suffixes="+suffixes+"&dict="+dict+"&words_type="+words_type;
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
        ***用法说明:
        动态后缀规则，比如规则为：ise-ize，
        表示单词集合中，有两个词分别以ise和ize结尾，
        且除了后缀外，其他部分都相同
    </p>
    <p>
        <font color="red">输入动态后缀：</font><input onchange="update();" id="suffixes" name="suffixes" value="<%=suffixes==null?"":suffixes%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="../select/dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="../select/words-select.jsp"/>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
