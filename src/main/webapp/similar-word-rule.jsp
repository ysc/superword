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
<%@ page import="org.apdplat.superword.rule.SimilarWord" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="org.apdplat.superword.tools.WordSources" %>
<%@ page import="org.apdplat.word.analysis.Hit" %>
<%@ page import="org.apdplat.word.analysis.Hits" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apdplat.word.analysis.EditDistanceTextSimilarity" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String word = request.getParameter("word");
    int count = 10;
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
        String words_type = request.getParameter("words_type");
        if(words_type == null){
            words_type = "ALL";
        }
        request.setAttribute("words_type", words_type.trim());
        String key = "words_string_"+words_type;
        List<String> words = (List<String>)session.getAttribute(key);
        if(words == null){
            words = new ArrayList<String>();
            if("ALL".equals(words_type.trim())){
                for(Word item : WordSources.getAll()){
                    words.add(item.getWord());
                }
            }else if("SYLLABUS".equals(words_type.trim())){
                for(Word item : WordSources.getSyllabusVocabulary()){
                    words.add(item.getWord());
                }
            }else{
                String resource = "/word_"+words_type+".txt";
                for(Word item : WordSources.get(resource)){
                    words.add(item.getWord());
                }
            }
            session.setAttribute(key, words);
        }
        SimilarWord similarWord = new SimilarWord();
        Hits result = similarWord.compute(word, new EditDistanceTextSimilarity(), words, count);

        StringBuilder temp = new StringBuilder();
        int i=1;
        temp.append("<table>\n");
        for(Hit hit : result.getHits()){
            temp.append("<tr>");
            temp.append("<td> ").append(i++)
                    .append(". </td><td> ")
                    .append(WordLinker.toLink(hit.getText(), WordLinker.getValidDictionary(request.getParameter("dict"))))
                    .append(" </td><td> ")
                    .append(hit.getScore())
                    .append("</td><td> ")
                    .append("<a target=\"_blank\" href=\"similar-word-rule.jsp?word=" + hit.getText() + "&count=" + count + "&dict=" + WordLinker.getValidDictionary(request.getParameter("dict")).name() + "&words_type=" + words_type + "\">相似</a>")
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
    <script src="js/statistics.js"></script>
    <script type="text/javascript">
        function update(){
            var word = document.getElementById("word").value;
            var count = document.getElementById("count").value;
            var dict = document.getElementById("dict").value;
            var words_type = document.getElementById("words_type").value;
            if(word == ""){
                return;
            }
            location.href = "similar-word-rule.jsp?word="+word+"&count="+count+"&dict="+dict+"&words_type="+words_type;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
    </script>
</head>
<body>
    <jsp:include page="head.jsp"/>
    <p>
        ***用法说明:
        拼写相似规则，英语是拼音文字而不是表意文字，
        所以，拼写相似的单词大多没有什么意义的关联，
        但是我们在阅读的时候因为长得像所以很容易认错，
        因此，在学习一个新单词的时候，
        系统地学习跟新单词拼写相似的单词是非常有必要的
    </p>
    <p>
        <font color="red">输入单词：</font><input id="word" name="word" value="<%=word==null?"":word%>" size="50" maxlength="50"><br/>
        <font color="red">结果数目：</font><input id="count" name="count" value="<%=count%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="dictionary-select.jsp"/><br/>
        <font color="red">选择词汇：</font>
        <jsp:include page="words-select.jsp"/>
    </p>
    <p></p>
    <p><a href="#" onclick="update();">提交</a></p>
    <%=htmlFragment%>
    <jsp:include page="bottom.jsp"/>
</body>
</html>
