<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>
<%@ page import="java.util.Set" %>
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
        boolean success = MySQLUtils.register(user);
        if(success) {
            session.setAttribute("user", user);

            // 如果用户注册之前做过单词测试
            // 将用户回答错误的单词保存到我的生词本
            MySQLUtils.saveWrongWordsInQuizToMyNewWords(session);

            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }else{
            tip = "failed to sign up，please try once more or contact with administrator!";
        }
    }
    if(userName == null){
        userName = "";
    }
%>
<html>
<head>
    <title>Sign up</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript" src="../js/md5.js"></script>
    <script type="text/javascript">
        var lock = false;
        function register() {
            var userName = document.getElementById("userName").value;
            var password = document.getElementById("password").value;
            var _password = document.getElementById("_password").value;
            if(password.length < 6){
                document.getElementById("tip").innerText = "the length of password must greater than five";
                return;
            }
            if(userName.length < 3){
                document.getElementById("tip").innerText = "the length of username must greater than two";
                return;
            }
            if (password != _password) {
                document.getElementById("tip").innerText = "check the password";
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
                register();
            }
        }
    </script>
</head>
<body id="top">
<jsp:include page="../common/head.jsp"/>
<form method="post" id="form" action="register.jsp">
    <p><font color="red"><span id="tip"><%=tip%></span></font></p>
    <p>
        <br/>
        <font color="red">username: </font><input id="userName" name="userName" size="50" value="<%=userName%>" maxlength="50"/><br/>
        <font color="red">password: </font><input id="password" name="password" type="password" size="50" maxlength="50"/><br/>
        <font color="red">re-input password: </font><input id="_password" name="_password" type="password" size="50" maxlength="50"/><br/>
        <script type="text/javascript">
            document.getElementById('userName').focus();
        </script>
    </p>
    <font color="red"><span style="cursor: pointer" onclick="register();">Sign up</span></font>
</form>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
