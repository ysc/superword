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

<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%
    Integer primary_school_count = (Integer)application.getAttribute("primary_school_count");
    if(primary_school_count == null){
        primary_school_count = WordSources.get("/word_primary_school.txt").size();
        application.setAttribute("primary_school_count", primary_school_count);
    }
    Integer junior_school_count = (Integer)application.getAttribute("junior_school_count");
    if(junior_school_count == null){
        junior_school_count = WordSources.get("/word_junior_school.txt").size();
        application.setAttribute("junior_school_count", junior_school_count);
    }
    Integer senior_school_count = (Integer)application.getAttribute("senior_school_count");
    if(senior_school_count == null){
        senior_school_count = WordSources.get("/word_senior_school.txt").size();
        application.setAttribute("senior_school_count", senior_school_count);
    }
    Integer university_count = (Integer)application.getAttribute("university_count");
    if(university_count == null){
        university_count = WordSources.get("/word_university.txt").size();
        application.setAttribute("university_count", university_count);
    }
    Integer new_conception_count = (Integer)application.getAttribute("new_conception_count");
    if(new_conception_count == null){
        new_conception_count = WordSources.get("/word_new_conception.txt").size();
        application.setAttribute("new_conception_count", new_conception_count);
    }
    Integer CET4_count = (Integer)application.getAttribute("CET4_count");
    if(CET4_count == null){
        CET4_count = WordSources.get("/word_CET4.txt").size();
        application.setAttribute("CET4_count", CET4_count);
    }
    Integer CET6_count = (Integer)application.getAttribute("CET6_count");
    if(CET6_count == null){
        CET6_count = WordSources.get("/word_CET6.txt").size();
        application.setAttribute("CET6_count", CET6_count);
    }
    Integer KY_count = (Integer)application.getAttribute("KY_count");
    if(KY_count == null){
        KY_count = WordSources.get("/word_KY.txt").size();
        application.setAttribute("KY_count", KY_count);
    }
    Integer MBA_count = (Integer)application.getAttribute("MBA_count");
    if(MBA_count == null){
        MBA_count = WordSources.get("/word_MBA.txt").size();
        application.setAttribute("MBA_count", MBA_count);
    }
    Integer BEC_count = (Integer)application.getAttribute("BEC_count");
    if(BEC_count == null){
        BEC_count = WordSources.get("/word_BEC.txt").size();
        application.setAttribute("BEC_count", BEC_count);
    }
    Integer TEM4_count = (Integer)application.getAttribute("TEM4_count");
    if(TEM4_count == null){
        TEM4_count = WordSources.get("/word_TEM4.txt").size();
        application.setAttribute("TEM4_count", TEM4_count);
    }
    Integer TEM8_count = (Integer)application.getAttribute("TEM8_count");
    if(TEM8_count == null){
        TEM8_count = WordSources.get("/word_TEM8.txt").size();
        application.setAttribute("TEM8_count", TEM8_count);
    }
    Integer TOEFL_count = (Integer)application.getAttribute("TOEFL_count");
    if(TOEFL_count == null){
        TOEFL_count = WordSources.get("/word_TOEFL.txt").size();
        application.setAttribute("TOEFL_count", TOEFL_count);
    }
    Integer TOEIC_count = (Integer)application.getAttribute("TOEIC_count");
    if(TOEIC_count == null){
        TOEIC_count = WordSources.get("/word_TOEIC.txt").size();
        application.setAttribute("TOEIC_count", TOEIC_count);
    }
    Integer IELTS_count = (Integer)application.getAttribute("IELTS_count");
    if(IELTS_count == null){
        IELTS_count = WordSources.get("/word_IELTS.txt").size();
        application.setAttribute("IELTS_count", IELTS_count);
    }
    Integer SAT_count = (Integer)application.getAttribute("SAT_count");
    if(SAT_count == null){
        SAT_count = WordSources.get("/word_SAT.txt").size();
        application.setAttribute("SAT_count", SAT_count);
    }
    Integer GMAT_count = (Integer)application.getAttribute("GMAT_count");
    if(GMAT_count == null){
        GMAT_count = WordSources.get("/word_GMAT.txt").size();
        application.setAttribute("GMAT_count", GMAT_count);
    }
    Integer GRE_count = (Integer)application.getAttribute("GRE_count");
    if(GRE_count == null){
        GRE_count = WordSources.get("/word_GRE.txt").size();
        application.setAttribute("GRE_count", GRE_count);
    }
    Integer ADULT_count = (Integer)application.getAttribute("ADULT_count");
    if(ADULT_count == null){
        ADULT_count = WordSources.get("/word_ADULT.txt").size();
        application.setAttribute("ADULT_count", ADULT_count);
    }
    Integer CATTI_count = (Integer)application.getAttribute("CATTI_count");
    if(CATTI_count == null){
        CATTI_count = WordSources.get("/word_CATTI.txt").size();
        application.setAttribute("CATTI_count", CATTI_count);
    }

    Integer SYLLABUS_count = (Integer)application.getAttribute("SYLLABUS_count");
    if(SYLLABUS_count == null){
        SYLLABUS_count = WordSources.getSyllabusVocabulary().size();
        application.setAttribute("SYLLABUS_count", SYLLABUS_count);
    }

    Integer computer_count = (Integer)application.getAttribute("computer_count");
    if(computer_count == null){
        computer_count = WordSources.get("/word_computer.txt").size();
        application.setAttribute("computer_count", computer_count);
    }
    Integer ALL_count = (Integer)application.getAttribute("ALL_count");
    if(ALL_count == null){
        ALL_count = WordSources.getAll().size();
        application.setAttribute("ALL_count", ALL_count);
    }
%>

<select name="words_type" id="words_type" onchange="update();">
    <%
        if ("primary_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="primary_school" selected="selected">小学(<%=primary_school_count%>)</option>
    <%
        } else {
    %>
    <option value="primary_school">小学(<%=primary_school_count%>)</option>
    <%
        }
        if ("junior_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="junior_school" selected="selected">初中(<%=junior_school_count%>)</option>
    <%
        } else {
    %>
    <option value="junior_school">初中(<%=junior_school_count%>)</option>
    <%
        }
        if ("senior_school".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="senior_school" selected="selected">高中(<%=senior_school_count%>)</option>
    <%
        } else {
    %>
    <option value="senior_school">高中(<%=senior_school_count%>)</option>
    <%
        }
        if ("university".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="university" selected="selected">大学(<%=university_count%>)</option>
    <%
        } else {
    %>
    <option value="university">大学(<%=university_count%>)</option>
    <%
        }
        if ("new_conception".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="new_conception" selected="selected">新概念英语(<%=new_conception_count%>)</option>
    <%
        } else {
    %>
    <option value="new_conception">新概念英语(<%=new_conception_count%>)</option>
    <%
        }
        if ("CET4".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CET4" selected="selected">4级(<%=CET4_count%>)</option>
    <%
        } else {
    %>
    <option value="CET4">4级(<%=CET4_count%>)</option>
    <%
        }
        if ("CET6".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CET6" selected="selected">6级(<%=CET6_count%>)</option>
    <%
        } else {
    %>
    <option value="CET6">6级(<%=CET6_count%>)</option>
    <%
        }
        if ("KY".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="KY" selected="selected">考研(<%=KY_count%>)</option>
    <%
        } else {
    %>
    <option value="KY">考研(<%=KY_count%>)</option>
    <%
        }
        if ("MBA".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="MBA" selected="selected">MBA(<%=MBA_count%>)</option>
    <%
        } else {
    %>
    <option value="MBA">MBA(<%=MBA_count%>)</option>
    <%
        }
        if ("BEC".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="BEC" selected="selected">BEC(<%=BEC_count%>)</option>
    <%
        } else {
    %>
    <option value="BEC">BEC(<%=BEC_count%>)</option>
    <%
        }
        if ("TEM4".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TEM4" selected="selected">专4(<%=TEM4_count%>)</option>
    <%
        } else {
    %>
    <option value="TEM4">专4(<%=TEM4_count%>)</option>
    <%
        }
        if ("TEM8".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TEM8" selected="selected">专8(<%=TEM8_count%>)</option>
    <%
        } else {
    %>
    <option value="TEM8">专8(<%=TEM8_count%>)</option>
    <%
        }
        if ("TOEFL".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TOEFL" selected="selected">托福(<%=TOEFL_count%>)</option>
    <%
        } else {
    %>
    <option value="TOEFL">托福(<%=TOEFL_count%>)</option>
    <%
        }
        if ("TOEIC".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="TOEIC" selected="selected">托业(<%=TOEIC_count%>)</option>
    <%
        } else {
    %>
    <option value="TOEIC">托业(<%=TOEIC_count%>)</option>
    <%
        }
        if ("IELTS".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="IELTS" selected="selected">雅思(<%=IELTS_count%>)</option>
    <%
        } else {
    %>
    <option value="IELTS">雅思(<%=IELTS_count%>)</option>
    <%
        }
        if ("SAT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="SAT" selected="selected">SAT(<%=SAT_count%>)</option>
    <%
        } else {
    %>
    <option value="SAT">SAT(<%=SAT_count%>)</option>
    <%
        }
        if ("GMAT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="GMAT" selected="selected">GMAT(<%=GMAT_count%>)</option>
    <%
        } else {
    %>
    <option value="GMAT">GMAT(<%=GMAT_count%>)</option>
    <%
        }
        if ("GRE".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="GRE" selected="selected">GRE(<%=GRE_count%>)</option>
    <%
        } else {
    %>
    <option value="GRE">GRE(<%=GRE_count%>)</option>
    <%
        }
        if ("ADULT".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="ADULT" selected="selected">成人英语(<%=ADULT_count%>)</option>
    <%
        } else {
    %>
    <option value="ADULT">成人英语(<%=ADULT_count%>)</option>
    <%
        }
        if ("CATTI".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="CATTI" selected="selected">翻译考试(<%=CATTI_count%>)</option>
    <%
        } else {
    %>
    <option value="CATTI">翻译考试(<%=CATTI_count%>)</option>
    <%
        }
        if ("SYLLABUS".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="SYLLABUS" selected="selected">所有以上考纲词汇(<%=SYLLABUS_count%>)</option>
    <%
        } else {
    %>
    <option value="SYLLABUS">所有以上考纲词汇(<%=SYLLABUS_count%>)</option>
    <%
        }
        if ("computer".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="computer" selected="selected">计算机(<%=computer_count%>)</option>
    <%
        } else {
    %>
    <option value="computer">计算机(<%=computer_count%>)</option>
    <%
        }
        if ("ALL".equalsIgnoreCase((String)request.getAttribute("words_type"))) {
    %>
    <option value="ALL" selected="selected">所有词汇(<%=ALL_count%>)</option>
    <%
        } else {
    %>
    <option value="ALL">所有词汇(<%=ALL_count%>)</option>
    <%
        }
    %>
</select>