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

<%@ page import="org.apdplat.superword.tools.FileUtils" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<String> books = (List<String>)application.getAttribute("books");
    if(books == null){
        books = FileUtils.readResource("/it/manifest");
        application.setAttribute("books", books);
    }
%>

<select name="book" id="book">
<%
for(String book : books) {
    if(book.equals(request.getParameter("book"))){
%>
    <option value="<%=book%>" selected="selected"><%=book.substring(4)%></option>
<%
    }else{
%>
    <option value="<%=book%>"><%=book.substring(4)%></option>
<%
    }
}
%>
</select>