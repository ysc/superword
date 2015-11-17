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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String suffixes = request.getParameter("suffixes");
    String htmlFragment = "";
    if(suffixes != null && !"".equals(suffixes.trim()) && suffixes.contains("-")){
        Set<Word> words = (Set<Word>)application.getAttribute("words");
        if(words == null){
            words = WordSources.getAll();
        }
        List<Suffix> suffixList = new ArrayList<Suffix>();
        for(String suffix : suffixes.trim() .split("-")){
            suffixList.add(new Suffix(suffix, ""));
        }
        List<Word> data = DynamicSuffixRule.findBySuffix(words, suffixList);
        htmlFragment = DynamicSuffixRule.toHtmlFragment(data, suffixList);
    }
%>
<html>
<head>
    <title>动态后缀规则</title>
    <script type="text/javascript">
        function submit(){
            var suffixes = document.getElementById("suffixes").value;
            if(suffixes == ""){
                return;
            }
            location.href = "dynamic-suffix-rule.jsp?suffixes="+suffixes;
        }
    </script>
</head>
<body>
    <p>
        ***用法说明:
        动态后缀规则，比如规则为：ise-ize，表示单词集合中
        有两个词分别以ise和ize结尾
        且除了后缀外，其他部分都相同
    </p>
    <font color="red">输入动态后缀：</font><input id="suffixes" name="suffixes" size="50" maxlength="50">
    <p></p>
    <h2><a href="#" onclick="submit();">提交</a></h2>
    <%=htmlFragment%>
</body>
</html>
