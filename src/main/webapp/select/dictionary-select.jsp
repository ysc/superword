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
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.YOUDAO==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO" selected="selected">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.COLLINS==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS" selected="selected">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.WEBSTER==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER" selected="selected">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.OXFORD==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD" selected="selected">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.CAMBRIDGE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE" selected="selected">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.MACMILLAN==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN" selected="selected">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.HERITAGE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE" selected="selected">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.WIKTIONARY==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY" selected="selected">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
            <%
    } else if (Dictionary.WORDNET==WordLinker.getValidDictionary(request.getParameter("dict"))) {
            %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET" selected="selected">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if (Dictionary.RANDOMHOUSE==WordLinker.getValidDictionary(request.getParameter("dict"))) {
    %>
            <option value="ICIBA">iCIBA</option>
            <option value="YOUDAO">Youdao</option>
            <option value="COLLINS">Collins</option>
            <option value="WEBSTER">Webster's</option>
            <option value="OXFORD">Oxford</option>
            <option value="CAMBRIDGE">Cambridge</option>
            <option value="MACMILLAN">Macmillan</option>
            <option value="HERITAGE">Heritage</option>
            <option value="WIKTIONARY">Wiktionary</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE" selected="selected">RandomHouse</option>
    <%
    }
    %>
        </select>