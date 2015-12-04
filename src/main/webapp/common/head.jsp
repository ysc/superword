<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.QQUser" %>
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
<script type="text/javascript">
    var contextPath = '<%=request.getServletContext().getContextPath()%>';
</script>
<table>
  <tr>
    <td>
      <h2><a href="https://github.com/ysc/superword" target="_blank">superword开源项目</a></h2>
    </td>
    <td>
      <div class="bdsharebuttonbox" data-tag="share_1">
        <a class="bds_mshare" data-cmd="mshare"></a>
        <a class="bds_qzone" data-cmd="qzone" href="#"></a>
        <a class="bds_tsina" data-cmd="tsina"></a>
        <a class="bds_baidu" data-cmd="baidu"></a>
        <a class="bds_renren" data-cmd="renren"></a>
        <a class="bds_tqq" data-cmd="tqq"></a>
        <a class="bds_more" data-cmd="more">更多</a>
        <a class="bds_count" data-cmd="count"></a>
      </div>
    </td>
<%
  User user = (User)session.getAttribute("user");
  if(user==null){
%>
    <td>
      <a href="<%=request.getContextPath()%>/system/register.jsp">注册</a>
    </td>
    <td>
      <a href="<%=request.getContextPath()%>/system/login.jsp">登录</a>
    </td>
    <td>
      <a href="<%=request.getContextPath()%>/system/login.jspx">QQ账号登录</a>
    </td>
<%
  }else{
      String displayName = user.getUserName();
      if(user instanceof QQUser){
          displayName = ((QQUser)user).getNickname();
      }
%>
    <td>
      欢迎【<%=displayName%>】
    </td>
    <td>
      <a href="<%=request.getContextPath()+"/system/logout.jsp"%>">注销</a>
    </td>
<%
  }
%>
<%
  if(!"true".equals(request.getParameter("index"))){
%>
    <td>
    <a href="<%=request.getContextPath()%>/index.jsp">返回主页</a> <br/>
    </td>
<%
  }
%>
  </tr>
</table>