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

        <select name="dict" id="dict">
    <%
        if ("ICIBA".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA" selected="selected">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("YOUDAO".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO" selected="selected">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("COLLINS".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS" selected="selected">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("WEBSTER".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER" selected="selected">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("OXFORD".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD" selected="selected">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("CAMBRIDGE".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE" selected="selected">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("MACMILLAN".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN" selected="selected">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("HERITAGE".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE" selected="selected">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("WIKTIONARY".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY" selected="selected">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
            <%
    } else if ("WORDNET".equals(WordLinker.dictionary)) {
            %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET" selected="selected">WordNet</option>
            <option value="RANDOMHOUSE">RandomHouse</option>
    <%
    } else if ("RANDOMHOUSE".equals(WordLinker.dictionary)) {
    %>
            <option value="ICIBA">爱词霸</option>
            <option value="YOUDAO">有道</option>
            <option value="COLLINS">柯林斯</option>
            <option value="WEBSTER">韦氏</option>
            <option value="OXFORD">牛津</option>
            <option value="CAMBRIDGE">剑桥</option>
            <option value="MACMILLAN">麦克米伦</option>
            <option value="HERITAGE">美国传统</option>
            <option value="WIKTIONARY">维基词典</option>
            <option value="WORDNET">WordNet</option>
            <option value="RANDOMHOUSE" selected="selected">RandomHouse</option>
    <%
    }
    %>
        </select>