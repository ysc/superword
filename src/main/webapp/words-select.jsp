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

<select name="words_type" id="words_type" onchange="update();">
    <%
        if ("primary_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="primary_school" selected="selected">小学(1354)</option>
    <%
        } else {
    %>
    <option value="primary_school">小学(1354)</option>
    <%
        }
        if ("junior_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="junior_school" selected="selected">初中(3359)</option>
    <%
        } else {
    %>
    <option value="junior_school">初中(3359)</option>
    <%
        }
        if ("senior_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="senior_school" selected="selected">高中(5060)</option>
    <%
        } else {
    %>
    <option value="senior_school">高中(5060)</option>
    <%
        }
        if ("university".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="university" selected="selected">大学(3670)</option>
    <%
        } else {
    %>
    <option value="university">大学(3670)</option>
    <%
        }
        if ("new_conception".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="new_conception" selected="selected">新概念英语(4198)</option>
    <%
        } else {
    %>
    <option value="new_conception">新概念英语(4198)</option>
    <%
        }
        if ("CET4".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CET4" selected="selected">4级(5042)</option>
    <%
        } else {
    %>
    <option value="CET4">4级(5042)</option>
    <%
        }
        if ("CET6".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CET6" selected="selected">6级(6652)</option>
    <%
        } else {
    %>
    <option value="CET6">6级(6652)</option>
    <%
        }
        if ("KY".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="KY" selected="selected">考研(5534)</option>
    <%
        } else {
    %>
    <option value="KY">考研(5534)</option>
    <%
        }
        if ("MBA".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="MBA" selected="selected">MBA(4168)</option>
    <%
        } else {
    %>
    <option value="MBA">MBA(4168)</option>
    <%
        }
        if ("BEC".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="BEC" selected="selected">BEC(672)</option>
    <%
        } else {
    %>
    <option value="BEC">BEC(672)</option>
    <%
        }
        if ("TEM4".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TEM4" selected="selected">专4(2085)</option>
    <%
        } else {
    %>
    <option value="TEM4">专4(2085)</option>
    <%
        }
        if ("TEM8".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TEM8" selected="selected">专8(932)</option>
    <%
        } else {
    %>
    <option value="TEM8">专8(932)</option>
    <%
        }
        if ("TOEFL".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TOEFL" selected="selected">托福(4864)</option>
    <%
        } else {
    %>
    <option value="TOEFL">托福(4864)</option>
    <%
        }
        if ("TOEIC".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TOEIC" selected="selected">托业(755)</option>
    <%
        } else {
    %>
    <option value="TOEIC">托业(755)</option>
    <%
        }
        if ("IELTS".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="IELTS" selected="selected">雅思(4540)</option>
    <%
        } else {
    %>
    <option value="IELTS">雅思(4540)</option>
    <%
        }
        if ("SAT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="SAT" selected="selected">SAT(108)</option>
    <%
        } else {
    %>
    <option value="SAT">SAT(108)</option>
    <%
        }
        if ("GMAT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="GMAT" selected="selected">GMAT(3328)</option>
    <%
        } else {
    %>
    <option value="GMAT">GMAT(3328)</option>
    <%
        }
        if ("GRE".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="GRE" selected="selected">GRE(7496)</option>
    <%
        } else {
    %>
    <option value="GRE">GRE(7496)</option>
    <%
        }
        if ("ADULT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="ADULT" selected="selected">成人英语(7216)</option>
    <%
        } else {
    %>
    <option value="ADULT">成人英语(7216)</option>
    <%
        }
        if ("CATTI".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CATTI" selected="selected">翻译考试(3574)</option>
    <%
        } else {
    %>
    <option value="CATTI">翻译考试(3574)</option>
    <%
        }
        if ("SYLLABUS".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="SYLLABUS" selected="selected">所有以上考纲词汇(18123)</option>
    <%
        } else {
    %>
    <option value="SYLLABUS">所有以上考纲词汇(18123)</option>
    <%
        }
        if ("computer".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="computer" selected="selected">计算机(3210)</option>
    <%
        } else {
    %>
    <option value="computer">计算机(3210)</option>
    <%
        }
        if ("ALL".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="ALL" selected="selected">所有词汇(63789)</option>
    <%
        } else {
    %>
    <option value="ALL">所有词汇(63789)</option>
    <%
        }
    %>
</select>