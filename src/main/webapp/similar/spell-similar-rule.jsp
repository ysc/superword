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
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordLinker.Dictionary" %>
<%@ page import="org.apdplat.word.analysis.Hit" %>
<%@ page import="org.apdplat.word.analysis.Hits" %>
<%@ page import="org.apdplat.word.analysis.EditDistanceTextSimilarity" %>
<%@ page import="org.apdplat.word.analysis.TextSimilarity" %>
<%@ page import="org.apdplat.word.segmentation.SegmentationAlgorithm" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.UserSimilarWord" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String word = request.getParameter("word");
    int count = 100;
    try{
        count = Integer.parseInt(request.getParameter("count"));
    }catch (Exception e){
        //none
    }
    if(count > 100){
        count = 100;
    }
    String htmlFragment = "";
    if(word != null && !"".equals(word.trim())){
        User user = (User)session.getAttribute("user");
        UserSimilarWord userSimilarWord = new UserSimilarWord();
        userSimilarWord.setSimilarWord(word);
        userSimilarWord.setDateTime(new Date());
        userSimilarWord.setUserName(user == null ? "anonymity" : user.getUserName());
        MySQLUtils.saveUserSimilarWordToDatabase(userSimilarWord);

        Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

        TextSimilarity textSimilarity = new EditDistanceTextSimilarity();
        textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        List<String> target = new ArrayList<String>();
        for(Word w : words){
            target.add(w.getWord());
        }
        Hits result = textSimilarity.rank(word, target, count);

        StringBuilder temp = new StringBuilder();
        int i=1;
        temp.append("<table border=\"1\" ondblclick=\"querySelectionWord();\">");
        temp.append("<tr><th>No.</th><th>Word</th><th>Similar Score</th><th>Chinese Meaning</th><th>English Meaning</th><th>Similar Word</th></tr>");
        for(Hit hit : result.getHits()){
            String w = hit.getText();
            String englishMeaning = MySQLUtils.getWordDefinition(w, Dictionary.WEBSTER.name());
            if(StringUtils.isBlank(englishMeaning)){
                englishMeaning = MySQLUtils.getWordDefinition(w, Dictionary.OXFORD.name());
            }
            String chineseMeaning = MySQLUtils.getWordDefinition(w, Dictionary.YOUDAO.name());
            if(StringUtils.isBlank(chineseMeaning)){
                chineseMeaning = MySQLUtils.getWordDefinition(w, Dictionary.ICIBA.name());
            }
            temp.append("<tr>");
            temp.append("<td> ").append(i++)
                    .append(". </td><td> ")
                    .append(WordLinker.toLink(w))
                    .append(" </td><td> ")
                    .append(hit.getScore())
                    .append("</td><td>")
                    .append(chineseMeaning)
                    .append("</td><td>")
                    .append(englishMeaning)
                    .append("</td><td> ")
                    .append("<a target=\"_blank\" href=\"spell-similar-rule.jsp?word=" + hit.getText() + "&count=" + count + "&words_type=" + request.getAttribute("words_type") + "\">similar word</a>")
                    .append(" </td>\n");
            temp.append("</tr>\n");
        }
        temp.append("</table>\n");
        htmlFragment = temp.toString();
    }
%>
<html>
<head>
    <title>spelling similarity rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var count = document.getElementById("count").value;
            var words_type = document.getElementById("words_type").value;
            if(word == ""){
                return;
            }
            location.href = "spell-similar-rule.jsp?word="+word+"&count="+count+"&words_type="+words_type;
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
    <p>
        ***spelling similarity rule:
        English is alphabetic writing but not ideographs, so similar spelling almost doesn't have the similar meaning, but we often mistake them in reading because they look like so alike. Therefore, when you learn a new word, you should learn the other words which are similar in spelling too.
    </p>
    <p>
        <font color="red">input word: </font><input id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">result count: </font><input id="count" name="count" value="<%=count%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/><br/>
        <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
