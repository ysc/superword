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

<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.superword.model.UserUrl" %>
<%@ page import="java.net.URLEncoder" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String userName = (String) session.getAttribute("userName");
    if(userName==null){
%>
未登录用户不能查看!<br/>
<a href="<%=request.getContextPath()+"/system/register.jsp"%>">注册</a>
<a href="<%=request.getContextPath()+"/system/login.jsp"%>">登录</a>
<%
        return;
    }

    List<UserUrl> userUrls = MySQLUtils.getHistoryUserUrlsFromDatabase(userName);
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("<table>");
    htmlFragment.append("<tr><th>序号</th><th>分析网页</th><th>时间</th></tr>");
    int i = 1;
    for (UserUrl userUrl : userUrls) {
        htmlFragment.append("<tr><td>")
                .append(i++)
                .append("</td><td>")
                .append("<a target=\"_blank\" href=\"../aid-reading/web-aid-reading.jsp?words_type=CET4&dict=ICIBA&column=6&url=")
                .append(URLEncoder.encode(userUrl.getUrl(), "utf-8"))
                .append("\">")
                .append(userUrl.getUrl())
                .append("</a>")
                .append("</td><td>")
                .append(userUrl.getDateTimeString())
                .append("</td></tr>");
    }
    htmlFragment.append("</table>");
%>

<html>
<head>
    <title>用户网页分析记录</title>
</head>
<body>
<jsp:include page="../common/head.jsp"/>
<p>用户 <%=userName%> 网页分析记录</p>
<%=htmlFragment%>
<jsp:include page="../common/bottom.jsp"/>
</body>
</html>
