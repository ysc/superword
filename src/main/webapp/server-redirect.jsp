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
<%@ page import="org.apdplat.superword.model.UserWord" %>
<%@ page import="java.util.Date" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String url = request.getParameter("url");
  String word = request.getParameter("word");
  String dict = request.getParameter("dict");

  if(url==null || word==null || dict==null){
    return;
  }

  String userName = (String)session.getAttribute("userName");
  if(userName != null) {
    UserWord userWord = new UserWord();
    userWord.setDateTime(new Date());
    userWord.setUserName(userName);
    userWord.setWord(word);
    userWord.setDictionary(dict);
    MySQLUtils.saveUserWordToDatabase(userWord);
  }

  response.sendRedirect(url);
%>