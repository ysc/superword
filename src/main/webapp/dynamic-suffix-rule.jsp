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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String dict = request.getParameter("dict");
    if(dict != null){
        if ("爱词霸".equals(dict)) {
            WordLinker.useICIBA = true;
        } else if ("有道".equals(dict)) {
            WordLinker.useICIBA = false;
        }
    }
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
            var dict = document.getElementById("dict").value;
            if(suffixes == ""){
                return;
            }
            location.href = "dynamic-suffix-rule.jsp?suffixes="+suffixes+"&dict="+dict;
        }
    </script>
</head>
<body>
    <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>
    <h2>
        ***用法说明:
        动态后缀规则，比如规则为：ise-ize，表示单词集合中
        有两个词分别以ise和ize结尾
        且除了后缀外，其他部分都相同
    </h2>
    <p>
        <font color="red">输入动态后缀：</font><input id="suffixes" name="suffixes" value="<%=suffixes==null?"":suffixes%>" size="50" maxlength="50">
    </p>
    <p>
        <font color="red">选择词典：</font>
        <select name="dict" id="dict">
            <%
                if(WordLinker.useICIBA){
            %>
            <option value="爱词霸" selected = "selected">爱词霸</option>
            <option value="有道">有道</option>
            <%
                }else{
            %>
            <option value="爱词霸">爱词霸</option>
            <option value="有道" selected = "selected">有道</option>
            <%
                }
            %>
        </select>
    </p>
    <p></p>
    <h2><a href="#" onclick="submit();">提交</a></h2>
    <h2><a href="index.jsp">主页</a></h2>
    <%=htmlFragment%>
</body>
</html>
