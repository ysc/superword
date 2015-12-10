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

<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="org.apdplat.superword.model.Quiz" %>
<%@ page import="org.apdplat.superword.model.QuizItem" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    if("true".equals(request.getParameter("restart"))){
        session.setAttribute("vocabulary_test", null);
    }
    Quiz quiz = (Quiz)session.getAttribute("vocabulary_test");
    if(quiz == null){
        quiz = Quiz.buildQuiz(Dictionary.YOUDAO);
        session.setAttribute("vocabulary_test", quiz);
    }
    String word = request.getParameter("word");
    String answer = request.getParameter("answer");
    if(StringUtils.isNotBlank(word) && StringUtils.isNotBlank(answer)){
        answer = URLDecoder.decode(answer, "utf-8");
        boolean right = quiz.answer(word, answer);
        if(!right){
            User user = (User)request.getSession().getAttribute("user");
            String newWord = word;
            if(StringUtils.isNotBlank(newWord)
                    && user != null
                    && StringUtils.isNotBlank(user.getUserName())){
                MyNewWord myNewWord = new MyNewWord();
                myNewWord.setWord(newWord);
                myNewWord.setDateTime(new Date());
                myNewWord.setUserName(user.getUserName());
                MySQLUtils.saveMyNewWordsToDatabase(myNewWord);
            }
        }
    }
    String htmlFragment = "";
    QuizItem quizItem = quiz.getQuizItem();
    if(quizItem == null){
        StringBuilder table = new StringBuilder();
        table.append("<table>")
                .append("<tr align=\"left\"><th>No.</th><th>Word</th><th>Right Or Wrong</th><th>Your Answer</th><th>Right Answer</th></tr>");
        int i=1;
        int rightCount=0;
        int wrongCount=0;
        for(QuizItem item : quiz.getQuizItems()){
            if(item.isRight()){
                rightCount++;
            }else{
                wrongCount++;
            }
            table.append("<tr>")
                    .append("<td>")
                    .append(i++)
                    .append("</td>")
                    .append("<td>")
                    .append(WordLinker.toLink(item.getWord().getWord()))
                    .append("</td>")
                    .append("<td><font color=\"red\">")
                    .append(item.isRight()?"Right":"Wrong")
                    .append("</font></td>")
                    .append("<td>")
                    .append(item.getAnswer())
                    .append("</td>")
                    .append("<td><font color=\"red\">")
                    .append(item.getWord().getMeaning())
                    .append("</font>")
                    .append("</td>")
                    .append("</tr>");
        }
        table.append("</table><br/>")
            .append("<a href=\"vocabulary-test.jsp?restart=true\">在测一次</a><br/>");
        htmlFragment = "<font color=\"red\">Right Count: "+rightCount+", Wrong Count: "+wrongCount+", Your vocabulary is likely "+rightCount*300*0.75+" words.</font><br/>"+table.toString();
    }else{
        StringBuilder html = new StringBuilder();
        html.append("<font color=\"red\"><h4>").append(quiz.step()).append(". ").append(quizItem.getWord().getWord()).append(":</h4></font>\n");
        for(String option : quizItem.getMeanings()){
            html.append("<input onchange=\"update();\" type=\"checkbox\" name=\"answer\" value=\"")
                    .append(URLEncoder.encode(option, "utf-8"))
                    .append("\">")
                    .append("<a href=\"vocabulary-test.jsp?word=")
                    .append(quizItem.getWord().getWord())
                    .append("&answer=")
                    .append(URLEncoder.encode(option, "utf-8"))
                    .append("\">")
                    .append(option)
                    .append("</a><br/>\n");
        }
        htmlFragment = html.toString();
    }
%>

<html>
<head>
   <title>vocabulary test</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        var lock = false;
        function update(){
            if(lock){
                return;
            }
            lock = true;
            document.getElementById("form").submit();
        }
    </script>
</head>
<body id="top" ondblclick="querySelectionWord();">
    <jsp:include page="common/head.jsp"/>

    <h3>vocabulary test</h3>
<%
    if(quizItem == null){
        out.println(htmlFragment);
    }else{
%>
    <form method="post" id="form" action="vocabulary-test.jsp?word=<%=quizItem.getWord().getWord()%>">
        <%=htmlFragment%>
    </form>
<%
    }
%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
