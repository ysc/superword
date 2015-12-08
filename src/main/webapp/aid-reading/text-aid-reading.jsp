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
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.UserText" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.model.User" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String text = request.getParameter("text");
    if(text != null) {
        text = URLDecoder.decode(text, "utf-8");
        User user = (User)session.getAttribute("user");
        UserText userText = new UserText();
        userText.setDateTime(new Date());
        userText.setText(text);
        userText.setUserName(user==null?"anonymity":user.getUserName());
        //保存用户文本分析记录
        MySQLUtils.saveUserTextToDatabase(userText);
    }else{
        String id = request.getParameter("id");
        try {
            text = MySQLUtils.getUseTextFromDatabase(Integer.parseInt(id)).getText();
        }catch (Exception e){}
    }
    if(text == null) {
        return;
    }

    Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    String htmlFragment = AidReading.analyse(words, column, false, null, Arrays.asList(text));
%>

<html>
<head>
    <title>text auxiliary reading</title>
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
            var text = document.getElementById("text").value;
            if(text == ""){
                return;
            }
            text = encodeURIComponent(text);
            document.getElementById("text").value = text;
            document.getElementById("form").submit();
        }
        var display = true;
        function change(){
            var text_div = document.getElementById("text_div");
            var tip = document.getElementById("tip");
            if(display){
                text_div.style.display = "none";
                tip.innerText = "Double click the word on the page to see the definition(Click Display): ";
            }else{
                text_div.style.display = "block";
                tip.innerText = "Double click the word on the page to see the definition(Click Hide): ";
            }
            display = !display;
        }
    </script>
</head>
<body id="top">
    <jsp:include page="../common/head.jsp"/>

    <p>
        text auxiliary reading
    </p>

    <form method="post" id="form" action="text-aid-reading.jsp">
        <p>
        <font color="red">words per line: </font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"/><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/>
        </p>
        <font color="red"><span style="cursor: pointer" onclick="change();" id="tip">Double click the word on the page to see the definition(Click Hide): </span></font>
        <div id="text_div" style="display:block">
            <textarea ondblclick="querySelectionWord();" id="text" name="text" rows="13" cols="100"  maxlength="10000"><%=text%></textarea><br/>
            <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
        </div>
    </form>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
