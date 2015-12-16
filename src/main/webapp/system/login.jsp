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
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if(session.getAttribute("user") != null){
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
    String userName = request.getParameter("userName");
    String password = request.getParameter("password");
    String tip = "";
    if(StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)){
        User user = new User();
        user.setDateTime(new Date());
        user.setUserName(userName);
        user.setPassword(password);
        boolean success = MySQLUtils.login(user);
        if(success) {
            Set<String> wrongWordsInQuiz = (Set<String>)session.getAttribute("wrong_words_in_quiz");
            final String u = userName;
            if(wrongWordsInQuiz != null){
                wrongWordsInQuiz.stream().map(w->{
                    MyNewWord myNewWord = new MyNewWord();
                    myNewWord.setWord(w);
                    myNewWord.setDateTime(new Date());
                    myNewWord.setUserName(u);
                    return myNewWord;
                }).forEach(w -> MySQLUtils.saveMyNewWordsToDatabase(w));
                session.setAttribute("wrong_words_in_quiz", null);
            }
            session.setAttribute("user", user);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }else{
            tip = "failed to sign in，username or password is incorrect!";
        }
    }
    if(userName == null){
        userName = "";
    }
%>
<html>
<head>
    <title>Sign in</title>
    <script type="text/javascript" src="../js/md5.js"></script>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        var lock = false;
        function login() {
            var userName = document.getElementById("userName").value;
            var password = document.getElementById("password").value;
            if (userName == "" || password == "") {
                document.getElementById("tip").innerText = "username or password is empty!";
                return;
            }
            if (lock) {
                return;
            }
            lock = true;
            password = hex_md5("superword555555555!"+password+"APDPlatysc&?!~");
            document.getElementById("password").value = password;
            document.getElementById("form").submit();
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                login();
            }
        }
    </script>
</head>
<body id="top">
<jsp:include page="../common/head.jsp"/>
<form method="post" id="form" action="login.jsp">
    <p><font color="red"><span id="tip"><%=tip%></span></font></p>
    <p>
        <br/>
        <font color="red">username: </font><input id="userName" name="userName" value="<%=userName%>" size="50" maxlength="50"/><br/>
        <font color="red">password: </font><input id="password" name="password" type="password" size="50" maxlength="50"/><br/>
    </p>
    <script type="text/javascript">
        if(document.getElementById('userName').value==""){
            document.getElementById('userName').focus();
        }else {
            document.getElementById('password').focus();
        }
    </script>
    <font color="red"><span style="cursor: pointer" onclick="login();">Sign in</span></font>
</form>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
