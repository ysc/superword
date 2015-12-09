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
<%@ page import="java.util.*" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.UserSimilarWord" %>
<%@ page import="org.apdplat.superword.model.User" %>

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

        Dictionary dictionary = WordLinker.getValidDictionary(request.getParameter("dictionary"));
        List<String> pronunciations = MySQLUtils.getWordPurePronunciation(word, dictionary.name());
        Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

        Map<String, Set<String>> pronunciationToWordMap = MySQLUtils.getAllWordPronunciation(dictionary.name(), words);


        TextSimilarity textSimilarity = new EditDistanceTextSimilarity(){

            @Override
            public double similarScore(String text1, String text2) {
                if(LOGGER.isDebugEnabled()) {
                    LOGGER.debug("文本1: ");
                    LOGGER.debug("\t" + text1);
                    LOGGER.debug("文本2: ");
                    LOGGER.debug("\t" + text2);
                }
                if(text1 == null || text2 == null){
                    //只要有一个文本为null，规定相似度分值为0，表示完全不相等
                    return 0.0;
                }
                //分词
                List<org.apdplat.word.segmentation.Word> words1 = Arrays.asList(new org.apdplat.word.segmentation.Word(text1));
                List<org.apdplat.word.segmentation.Word> words2 = Arrays.asList(new org.apdplat.word.segmentation.Word(text2));
                //计算相似度分值
                return similarScore(words1, words2);
            }
        };

        List<String> target = new ArrayList<String>();
        target.addAll(pronunciationToWordMap.keySet());

        StringBuilder temp = new StringBuilder();
        temp.append("<table>\n")
                .append("<tr>\n");
        for(String pronunciation : pronunciations) {
            temp.append("<td>\n")
                .append("<table border=\"1\">\n")
                .append("<tr>")
                .append("<th align=\"left\" colspan=\"5\">The pronunciations of words are similar with ")
                .append(word)
                .append(" ")
                .append(pronunciation)
                .append(" list below: </th>")
                .append("</tr>\n");
            Hits result = textSimilarity.rank(pronunciation, target, count);
            int i=1;
            for(Hit hit : result.getHits()){
                temp.append("<tr>")
                        .append("<td> ")
                        .append(i++)
                        .append(". </td><td> ");
                StringBuilder hitWord = new StringBuilder();
                for(String w : pronunciationToWordMap.get(hit.getText())){
                    temp.append(WordLinker.toLink(w))
                        .append(" ");
                    if(hitWord.length() == 0) {
                        hitWord.append(w);
                    }
                }
                temp.append("</td><td> ")
                        .append(hit.getText())
                        .append(" </td><td> ")
                        .append(hit.getScore())
                        .append("</td><td> ")
                        .append("<a target=\"_blank\" href=\"pronunciation-similar-rule.jsp?word=" + hitWord.toString() + "&count=" + count + "&words_type=" + request.getAttribute("words_type") + "&dictionary=" + request.getParameter("dictionary") + "\">similar word</a>")
                        .append(" </td>")
                        .append("</tr>\n");
            }
            temp.append("</table>\n")
                .append("</td>");
        }
        temp.append("</tr>\n")
            .append("</table>\n");
        htmlFragment = temp.toString();
    }
%>
<html>
<head>
    <title>pronunciation similarity rule</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var count = document.getElementById("count").value;
            var words_type = document.getElementById("words_type").value;
            var dictionary = document.getElementById("dictionary").value;
            if(word == ""){
                return;
            }
            location.href = "pronunciation-similar-rule.jsp?word="+word+"&count="+count+"&words_type="+words_type+"&dictionary="+dictionary;
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
        ***pronunciation similarity rule: 
        To calculate the similarity among words according to word pronunciation.
    </p>
    <p>
        <font color="red">input word: </font><input id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">result count: </font><input id="count" name="count" value="<%=count%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/>
        <font color="red">select dictionary: </font>
        <jsp:include page="../select/dictionary-select-for-symbol.jsp"/><br/><br/>
        <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
