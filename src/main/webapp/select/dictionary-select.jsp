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

<%
    Dictionary selectedDictionary = WordLinker.getValidDictionary(request.getParameter("dict"));
    String ICIBA = "";
    String YOUDAO = "";
    String COLLINS = "";
    String WEBSTER = "";
    String OXFORD = "";
    String CAMBRIDGE = "";
    String MACMILLAN = "";
    String HERITAGE = "";
    String WIKTIONARY = "";
    String WORDNET = "";
    String RANDOMHOUSE = "";
    String selected = "selected=\"selected\"";
    switch (selectedDictionary){
        case ICIBA:
            ICIBA = selected; break;
        case YOUDAO:
            YOUDAO = selected; break;
        case COLLINS:
            COLLINS = selected; break;
        case WEBSTER:
            WEBSTER = selected; break;
        case OXFORD:
            OXFORD = selected; break;
        case CAMBRIDGE:
            CAMBRIDGE = selected; break;
        case MACMILLAN:
            MACMILLAN = selected; break;
        case HERITAGE:
            HERITAGE = selected; break;
        case WIKTIONARY:
            WIKTIONARY = selected; break;
        case WORDNET:
            WORDNET = selected; break;
        case RANDOMHOUSE:
            RANDOMHOUSE = selected; break;
    }
%>
<select name="dict" id="dict" onchange="update();">
    <option value="ICIBA" <%=ICIBA%>>iCIBA</option>
    <option value="YOUDAO" <%=YOUDAO%>>Youdao</option>
    <option value="COLLINS" <%=COLLINS%>>Collins</option>
    <option value="WEBSTER" <%=WEBSTER%>>Webster's</option>
    <option value="OXFORD" <%=OXFORD%>>Oxford</option>
    <option value="CAMBRIDGE" <%=CAMBRIDGE%>>Cambridge</option>
    <option value="MACMILLAN" <%=MACMILLAN%>>Macmillan</option>
    <option value="HERITAGE" <%=HERITAGE%>>Heritage</option>
    <option value="WIKTIONARY" <%=WIKTIONARY%>>Wiktionary</option>
    <option value="WORDNET" <%=WORDNET%>>WordNet</option>
    <option value="RANDOMHOUSE" <%=RANDOMHOUSE%>>RandomHouse</option>
</select>