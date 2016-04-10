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
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>

    <select name="dict" id="dict" onchange="update();">
        <%
        if (Dictionary.ICIBA==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="ICIBA" selected="selected">iCIBA</option>
        <%
        } else {
        %>
        <option value="ICIBA">iCIBA</option>
        <%
        }
        if (Dictionary.YOUDAO==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="YOUDAO" selected="selected">Youdao</option>
        <%
        } else {
        %>
        <option value="YOUDAO">Youdao</option>
        <%
        }
        if (Dictionary.COLLINS==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="COLLINS" selected="selected">Collins</option>
        <%
        } else {
        %>
        <option value="COLLINS">Collins</option>
        <%
        }
        if (Dictionary.WEBSTER==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="WEBSTER" selected="selected">Webster's</option>
        <%
        } else {
        %>
        <option value="WEBSTER">Webster's</option>
        <%
        }
        if (Dictionary.OXFORD==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="OXFORD" selected="selected">Oxford</option>
        <%
        } else {
        %>
        <option value="OXFORD">Oxford</option>
        <%
        }
        if (Dictionary.CAMBRIDGE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="CAMBRIDGE" selected="selected">Cambridge</option>
        <%
        } else {
        %>
        <option value="CAMBRIDGE">Cambridge</option>
        <%
        }
        if (Dictionary.MACMILLAN==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="MACMILLAN" selected="selected">Macmillan</option>
        <%
        } else {
        %>
        <option value="MACMILLAN">Macmillan</option>
        <%
        }
        if (Dictionary.HERITAGE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="HERITAGE" selected="selected">Heritage</option>
        <%
        } else {
        %>
        <option value="HERITAGE">Heritage</option>
        <%
        }
        if (Dictionary.WIKTIONARY==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="WIKTIONARY" selected="selected">Wiktionary</option>
        <%
        } else {
        %>
        <option value="WIKTIONARY">Wiktionary</option>
        <%
        }
        if (Dictionary.WORDNET==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="WORDNET" selected="selected">WordNet</option>
        <%
        } else {
        %>
        <option value="WORDNET">WordNet</option>
        <%
        }
        if (Dictionary.RANDOMHOUSE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
        %>
        <option value="RANDOMHOUSE" selected="selected">RandomHouse</option>
        <%
        } else {
        %>
        <option value="RANDOMHOUSE">RandomHouse</option>
        <%
        }
        %>
    </select>