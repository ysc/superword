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

<%@ page import="org.apdplat.jsearch.index.TextIndexer" %>
<%@ page import="org.apdplat.jsearch.score.WordFrequencyScore" %>
<%@ page import="org.apdplat.jsearch.search.Doc" %>
<%@ page import="org.apdplat.jsearch.search.Hits" %>
<%@ page import="org.apdplat.jsearch.search.SearchMode" %>
<%@ page import="org.apdplat.jsearch.search.TextSearcher" %>
<%@ page import="org.apdplat.superword.tools.WordLinker" %>
<%@ page import="java.util.concurrent.atomic.AtomicInteger" %>
<%@ page import="java.net.URLDecoder" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String book = request.getParameter("book");
    String word = request.getParameter("word");
    if(book == null || word == null){
        return;
    }
    book = URLDecoder.decode(book, "utf-8");
    int pageSize = 10;
    try{
        pageSize = Integer.parseInt(request.getParameter("pageSize"));
    }catch (Exception e){}
    String key = "TextSearcher_"+book;
    TextSearcher textSearcher = (TextSearcher)application.getAttribute(key);
    if(textSearcher == null){
        String bookPath = request.getServletContext().getRealPath("/WEB-INF/classes"+book);
        System.out.println("book path: " + bookPath);
        String index = request.getServletContext().getRealPath("/WEB-INF/data/index/"+book.replace("/", "_"));
        String indexText = request.getServletContext().getRealPath("/WEB-INF/data/index_text/"+book.replace("/", "_"));
        int indexLengthLimit = 1000;
        System.out.println("index path: "+index);
        System.out.println("indexText path: " + indexText);
        System.out.println("indexLengthLimit: " + indexLengthLimit);
        TextIndexer textIndexer = new TextIndexer(index, indexText, indexLengthLimit);
        textIndexer.indexDir(bookPath);
        textSearcher = new TextSearcher(index, indexText);
        application.setAttribute(key, textSearcher);
    }
    textSearcher.setPageSize(pageSize);
    textSearcher.setScore(new WordFrequencyScore());
    Hits hits = textSearcher.search(word, SearchMode.INTERSECTION);
    StringBuilder regex = new StringBuilder();
    for(char c : word.toCharArray()){
        regex.append("[").append(Character.toUpperCase(c)).append("|").append(Character.toLowerCase(c)).append("]");
    }
    AtomicInteger i = new AtomicInteger();
    StringBuilder htmlFragment = new StringBuilder();
    htmlFragment.append("hit(").append(hits.getHitCount()).append("):<br/>\n");
    for(Doc doc : hits.getDocs()){
        htmlFragment.append(i.incrementAndGet())
                .append(". ")
                .append(doc.getText().replaceAll(regex.toString(), "<font color=\"red\">"+ WordLinker.toLink(word, WordLinker.getValidDictionary(request.getParameter("dict")))+"</font>"))
                .append("<br/>\n");
    }
    if(pageSize > hits.getHitCount() && hits.getHitCount() > 0){
        pageSize = hits.getHitCount();
    }
%>

<html>
<head>
    <title>辅助阅读</title>
    <script type="text/javascript">
        function update(){
            var dict = document.getElementById("dict").value;
            var book = document.getElementById("book").value;
            var word = document.getElementById("word").value;
            var pageSize = document.getElementById("pageSize").value;

            if(book == ""){
                return;
            }
            book = encodeURIComponent(book);
            location.href = "aid-reading-detail.jsp?word="+word+"&dict="+dict+"&book="+book+"&pageSize="+pageSize;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
        var linkPrefix = '<%=WordLinker.serverRedirect+"?url="+WordLinker.getLinkPrefix(WordLinker.getValidDictionary(request.getParameter("dict")))%>';
        function querySelectionWord(){
            var word = "";
            if(window.getSelection){
                word = window.getSelection();
            }
            else{
                word = document.selection.createRange().text;
            }
            if(/^[a-zA-Z]{3,15}$/.test(word)){
                window.open(linkPrefix+word+"&word="+word+"&dict=<%=WordLinker.getValidDictionary(request.getParameter("dict"))%>", word, 'width=1200,height=600');
            }
        }
    </script>
</head>
<body>
    <jsp:include page="../common/head.jsp"/>

    <p>
        辅助阅读-搜索单词对应原文
    </p>

    <p>
        <font color="red">搜索单词：</font><input onchange="update();" id="word" name="word" value="<%=word%>" size="50" maxlength="50"><br/>
        <font color="red">显示条数：</font><input onchange="update();" id="pageSize" name="pageSize" value="<%=pageSize%>" size="50" maxlength="50"><br/>
        <font color="red">选择词典：</font>
        <jsp:include page="../select/dictionary-select.jsp"/><br/>
        <font color="red">选择书籍：</font>
        <jsp:include page="../select/book-select.jsp"/>
    </p>
    <div ondblclick="querySelectionWord();">
        <font color="red">双击文本选中单词可查看定义</font><br/>
        <%=htmlFragment%>
    </div>
    <p><a target="_blank" href="index.jsp">主页</a></p>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
