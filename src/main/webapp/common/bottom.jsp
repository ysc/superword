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


<p>
  <%
    if(!"true".equals(request.getParameter("index"))){
  %>
  <a href="<%=request.getContextPath()%>/index.jsp">Home page</a> <br/>
  <%
    }
  %>
  <a target="_blank" href="<%=request.getContextPath()%>/common/feedback.jsp">Feedback</a> <br/>
  <a target="_blank" href="https://github.com/ysc/QuestionAnsweringSystem/wiki/donation">Donate to support Superword</a><br/>
  We recommend the use of the <a target="_blank" href="http://pan.baidu.com/s/1bnwTVC7">Google's Chrome browser</a>
</p>
<p id="back-to-top"><a href="#top"><span></span>Back Top</a></p>