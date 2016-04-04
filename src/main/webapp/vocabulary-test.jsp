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

<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apdplat.superword.model.MyNewWord" %>
<%@ page import="org.apdplat.superword.model.Quiz" %>
<%@ page import="org.apdplat.superword.model.QuizItem" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apdplat.superword.freemarker.TemplateUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    if("true".equals(request.getParameter("restart"))){
        session.setAttribute("vocabulary_test", null);
    }
    Quiz quiz = (Quiz)session.getAttribute("vocabulary_test");
    if(quiz == null){
        Dictionary dictionary = WordLinker.getValidDictionary(request.getParameter("dictionary"));
        quiz = Quiz.buildQuiz(dictionary);
        session.setAttribute("vocabulary_test", quiz);
    }
    String word = request.getParameter("word");
    String answer = request.getParameter("answer");
    if(StringUtils.isNotBlank(word) && StringUtils.isNotBlank(answer)){
        answer = URLDecoder.decode(answer, "utf-8");
        boolean right = quiz.answer(word, answer);
        if(!right){
            Set<String> wrongWordsInQuiz = (Set<String>)session.getAttribute("wrong_words_in_quiz");
            if(wrongWordsInQuiz == null){
                wrongWordsInQuiz = new HashSet<>();
                session.setAttribute("wrong_words_in_quiz", wrongWordsInQuiz);
            }
            wrongWordsInQuiz.add(word);
        }
    }
    String htmlFragment = "";
    QuizItem quizItem = quiz.getQuizItem();
    if(quizItem == null){
        Map<String, Object> data = new HashMap<>();
        data.put("quiz", quiz);
        htmlFragment = TemplateUtils.getVocabularyTestResult(data);

        // 如果用户已经登录, 则将答错的词加入生词本, 如果用户没有登录, 那么会在用户注册成功或者登录的时候将答错的词加入生词本
        User user = (User)request.getSession().getAttribute("user");
        Set<String> wrongWordsInQuiz = (Set<String>)session.getAttribute("wrong_words_in_quiz");
        if(wrongWordsInQuiz != null && user != null){
            for(String w : wrongWordsInQuiz){
                MyNewWord myNewWord = new MyNewWord();
                myNewWord.setWord(w);
                myNewWord.setDateTime(new Date());
                myNewWord.setUserName(user.getUserName());
                MySQLUtils.saveMyNewWordsToDatabase(myNewWord);
            }
            session.setAttribute("wrong_words_in_quiz", null);
        }
    }else{
        Map<String, Object> data = new HashMap<>();
        data.put("quizItem", quizItem);
        data.put("step", quiz.step());
        htmlFragment = TemplateUtils.getVocabularyTestForm(data);
    }
%>

<html>
<head>
   <title>vocabulary test</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top" ondblclick="querySelectionWord();">
    <jsp:include page="common/head.jsp"/>

    <h3>vocabulary test</h3>
<%
    if(quizItem == null){
        out.println(htmlFragment);
    }else{
%>
    <%=htmlFragment%>
<%
    }
%>
    <jsp:include page="common/bottom.jsp"/>
</body>
</html>
