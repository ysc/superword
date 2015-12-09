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

<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="org.apdplat.superword.tools.AidReading" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.apdplat.superword.model.UserBook" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.apdplat.superword.model.User" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String book = request.getParameter("book");
    if(book == null){
        return;
    }
    book = URLDecoder.decode(book, "utf-8");
    User user = (User)session.getAttribute("user");
    UserBook userBook = new UserBook();
    userBook.setDateTime(new Date());
    userBook.setBook(book);
    userBook.setUserName(user==null?"anonymity":user.getUserName());
    //保存用户书籍分析记录
    MySQLUtils.saveUserBookToDatabase(userBook);

    Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    String htmlFragment = AidReading.analyse(words, column, true, book, book);
%>

<html>
<head>
    <title>book auxiliary reading</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var words_type = document.getElementById("words_type").value;
            var book = document.getElementById("book").value;
            var column = document.getElementById("column").value;

            if(book == ""){
                return;
            }
            book = encodeURIComponent(book);
            location.href = "book-aid-reading.jsp?words_type="+words_type+"&book="+book+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body id="top">
    <jsp:include page="../common/head.jsp"/>

    <h3>
        book auxiliary reading
    </h3>

    <p>
        <font color="red">words per line: </font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/>
        <font color="red">select book: </font>
        <jsp:include page="../select/book-select.jsp"/>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
