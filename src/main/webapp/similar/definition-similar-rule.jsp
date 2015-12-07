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
<%@ page import="org.apdplat.word.segmentation.SegmentationAlgorithm" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="org.apdplat.superword.model.UserSimilarWord" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.word.analysis.*" %>

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
        String wordDefinition = MySQLUtils.getWordDefinition(word, WordLinker.Dictionary.WEBSTER.name());
        List<String> allWordDefinition = MySQLUtils.getAllWordDefinition(WordLinker.Dictionary.WEBSTER.name(), words);

        TextSimilarity textSimilarity = new CosineTextSimilarity();
        textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);

        Hits result = textSimilarity.rank(wordDefinition, allWordDefinition, count);

        StringBuilder temp = new StringBuilder();
        int i=1;
        temp.append("<table border=\"1\">\n");
        for(Hit hit : result.getHits()){
            String[] attrs = hit.getText().split("_");
            String w = attrs[0];
            StringBuilder definition = new StringBuilder(attrs[1]);
            for(int j=2; j<attrs.length; j++){
                definition.append(attrs[j]).append("_");
            }
            temp.append("<tr>");
            temp.append("<td> ").append(i++)
                    .append(". </td><td> ")
                    .append(WordLinker.toLink(w))
                    .append(" </td><td> ")
                    .append(definition)
                    .append(" </td><td> ")
                    .append(hit.getScore())
                    .append("</td><td> ")
                    .append("<a target=\"_blank\" href=\"definition-similar-rule.jsp?word=" + hit.getText() + "&count=" + count + "&words_type=" + request.getAttribute("words_type") + "\">相似</a>")
                    .append(" </td>\n");
            temp.append("</tr>\n");
        }
        temp.append("</table>\n");
        htmlFragment = temp.toString();
    }
%>
<html>
<head>
    <title>拼写相似规则</title>
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
            location.href = "definition-similar-rule.jsp?word="+word+"&count="+count+"&words_type="+words_type;
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
        ***用法说明:
        定义相似规则，根据单词的定义来计算两个单词之间的相似度
    </p>
    <p>
        <font color="red">输入单词：</font><input id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">结果数目：</font><input id="count" name="count" value="<%=count%>" size="50" maxlength="50"><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="../select/words-select.jsp"/>
    </p>
    <p></p>
    <p><a href="#" onclick="update();">提交</a></p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
