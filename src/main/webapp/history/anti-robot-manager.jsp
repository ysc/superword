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

<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %>
<%@ page import="org.apdplat.superword.system.AntiRobotFilter" %>
<%@ page import="org.apdplat.superword.tools.IPUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) request.getSession().getAttribute("user");
    if(user == null || !user.getUserName().equals("ysc")){
        out.println("您没有权限访问该页面");
        return;
    }

    StringBuilder html = new StringBuilder();

    String limit = request.getParameter("limit");
    try{
        AntiRobotFilter.limit = Integer.parseInt(limit);
    }catch (Exception e){}
    html.append("每日每用户访问请求次数限制: ").append(AntiRobotFilter.limit).append("<br/><br/>");

    html.append("<table>")
            .append("<tr><th>序号</th><th>用户名称</th><th>用户地址</th><th>访问日期</th><th>访问次数</th><th>IP地址的地理位置</th></tr>");
    AtomicInteger i = new AtomicInteger();
    for(String item : AntiRobotFilter.getData()){
        String[] attrs = item.split("-");
        html.append("<tr><th>")
                .append(i.incrementAndGet())
                .append("</th><th>")
                .append(attrs[0])
                .append("</th><th>")
                .append(attrs[1])
                .append("</th><th>")
                .append(attrs[2])
                .append("</th><th>")
                .append(attrs[3])
                .append("</th><th>")
                .append(IPUtils.getIPLocation(attrs[2]))
                .append("</th></tr>");
    }
    html.append("</table>");
%>
<html>
<head>
    <title>反机器人管理</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
<jsp:include page="../common/head.jsp"/>
<%=html.toString()%>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
