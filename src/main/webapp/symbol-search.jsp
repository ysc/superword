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

<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.apdplat.superword.tools.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="java.util.Set" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String symbol = request.getParameter("symbol");
    String dictionary = request.getParameter("dictionary");
    int limit = 100;
    int column = 5;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    StringBuilder htmlFragment = new StringBuilder();
    if(StringUtils.isNotBlank(symbol) && StringUtils.isNotBlank(dictionary)){
        String key = "symbol-"+symbol+dictionary+limit+request.getAttribute("words_type");
        Map<String, String> map = (Map<String, String>)application.getAttribute(key);
        if(map == null){
            Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));
            map = MySQLUtils.getWordAndPronunciationBySymbol(symbol, dictionary, limit, words);
            application.setAttribute(key, map);
        }
        if(!map.isEmpty()){
            htmlFragment.append("In ")
                    .append(WordLinker.Dictionary.valueOf(dictionary).getDes())
                    .append(" dictionary, the words have the phonetic symbol ")
                    .append(symbol)
                    .append(" list below: <br/><br/>");
            List<String> data = new ArrayList<String>();
            for(Map.Entry<String, String> entry : map.entrySet()){
                data.add(WordLinker.toLink(entry.getKey())+"<br/>"+entry.getValue().replace(symbol, "<font color=\"red\">" + symbol + "</font>").replace(" | ", "<br/>"));
            }
            htmlFragment.append(HtmlFormatter.toHtmlTableFragment(data, column));
        }
    }
%>

<html>
<head>
   <title>phonetic symbol search</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        var lock = false;
        function update(){
            if(lock){
                return;
            }
            lock = true;
            var symbol = document.getElementById("symbol").value;
            if(symbol == ""){
                return;
            }
            document.getElementById("form").submit();
        }
    </script>
</head>
<body id="top">
    <jsp:include page="common/head.jsp"/>
    <p>
        phonetic symbol search
    </p>
    <form method="get" id="form" action="symbol-search.jsp">
        <p>
            <font color="red">input phonetic symbol: </font><input onchange="update();" id="symbol" name="symbol" value="<%=symbol==null?"":symbol%>" size="50" maxlength="50"><br/>
            <font color="red">words per line: </font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"/><br/>
            <font color="red">select dictionary: </font>
            <jsp:include page="select/dictionary-select-for-symbol.jsp"/><br/>
            <font color="red">select words level: </font>
            <jsp:include page="select/words-select.jsp"/>
        </p>
    </form>

    <%=htmlFragment%>

    <br/>
    <br/>
    <a target="_blank" href="symbol.jsp">Comparison of phonetic symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
