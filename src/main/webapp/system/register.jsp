<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="java.util.Date" %>
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
            session.setAttribute("userName", userName);
            response.sendRedirect(request.getContextPath()+"/");
        }else{
            tip = "注册失败，请稍后重试或与管理员联系！";
        }
    }
    if(userName == null){
        userName = "";
    }
%>
<html>
<head>
    <title>用户注册</title>
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
                document.getElementById("tip").innerText = "密码长度要>=6";
                return;
            }
            if(userName.length < 3){
                document.getElementById("tip").innerText = "用户名长度要>=3";
                return;
            }
            if (password != _password) {
                document.getElementById("tip").innerText = "两次密码输入不相等!";
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
        <font color="red">用户名称：</font><input id="userName" name="userName" size="50" value="<%=userName%>" maxlength="50"/><br/>
        <font color="red">用户密码：</font><input id="password" name="password" type="password" size="50" maxlength="50"/><br/>
        <font color="red">确认密码：</font><input id="_password" name="_password" type="password" size="50" maxlength="50"/><br/>
        <script type="text/javascript">
            document.getElementById('userName').focus();
        </script>
    </p>
    <font color="red"><span style="cursor: pointer" onclick="register();">注册账号</span></font>
</form>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
