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

<%@ page import="org.apdplat.superword.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) request.getSession().getAttribute("user");
%>

<html>
<head>
  <title>Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading.</title>
  <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
</head>
<body id="top">
  <jsp:include page="common/head.jsp?index=true"/>
  <p>
    Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading, including but not limited to, spelling similarity, definition similarity, pronunciation similarity, the transformation rules of the spelling, the prefix and the dynamic prefix, the suffix and the dynamic suffix, roots, compound words, text auxiliary reading, web page auxiliary reading, book auxiliary reading, etc..
  </p>
  <p>
    <ol>
      <li><a href="<%=request.getContextPath()%>/definition.jsp?word=fantastic">word definition</a></li>
      <li><a href="<%=request.getContextPath()%>/aid-reading/text-aid-reading.jsp?words_type=CET4&column=6&text=A+macadamia+is+an+Australian+rainforest+tree+with+slender%2c+glossy+evergreen+leaves+and+globular+edible+nuts.%0d%0a%0d%0aA+dandelion+is+a+wild+plant+which+has+yellow+flowers+with+lots+of+thin+petals.+When+the+petals+of+each+flower+drop+off%2c+a+fluffy+white+ball+of+seeds+grows.%0d%0a%0d%0aReeds+are+tall+plants+that+grow+in+large+groups+in+shallow+water+or+on+ground+that+is+always+wet+and+soft.+They+have+strong%2c+hollow+stems+that+can+be+used+for+making+things+such+as+mats+or+baskets.%0d%0a%0d%0aA+hummingbird+is+a+small+nectar-feeding+tropical+brightly+coloured+American+bird+that+is+able+to+hover+and+fly+backwards%2c+and+typically+has+colourful+iridescent+plumage.+It+has+a+long+thin+beak+and+powerful+narrow+wings+that+can+move+very+fast.%0d%0a%0d%0aA+prawn+is+a+small+shellfish+with+a+long+tail+and+many+legs%2c+which+can+be+eaten.">text auxiliary reading</a></li>
      <li><a href="<%=request.getContextPath()%>/aid-reading/web-aid-reading.jsp?words_type=CET4&url=http://spark.apache.org/docs/latest/streaming-programming-guide.html&column=6">web page auxiliary reading</a></li>
      <li><a href="<%=request.getContextPath()%>/aid-reading/book-aid-reading.jsp?words_type=CET4&book=/it/java/Java%208%20in%20Action%20Lambdas,%20Streams%20and%20Functional-style%20Programming.txt&column=6">book auxiliary reading</a></li>
      <li><a href="<%=request.getContextPath()%>/aid-reading/book-aid-reading-detail.jsp?book=/it/java/Java%208%20in%20Action%20Lambdas,%20Streams%20and%20Functional-style%20Programming.txt&word=functional&pageSize=192">search word corresponding to the original text</a></li>
      <li><a href="<%=request.getContextPath()%>/independence-word-rule.jsp?dict=ICIBA&column=8&words_type=CET4">independent word rule</a></li>
      <li><a href="<%=request.getContextPath()%>/compound-word-rule.jsp?all=false&word=fearless&words_type=CET6&column=12">compound word analysis rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/root_affix_rule.jsp?dict=ICIBA&word=abbreviation&column=6&strict=N">roots and affix analysis rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/roots.jsp">commonly used roots</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/root-rule.jsp?roots=spect,spic&words_type=CET4&column=6">roots rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/prefixes.jsp">commonly used prefix</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/prefix-rule.jsp?prefixes=anti,counter,de,dis,il,im,in,ir,mis,non,un&words_type=SYLLABUS&strict=Y&column=6">prefix rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/dynamic-prefix-rule.jsp?prefixes=m-imm&words_type=SYLLABUS">dynamic prefix rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/suffixes.jsp">commonly used suffix</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/suffix-rule.jsp?suffixes=ence,ance,age&words_type=SYLLABUS&strict=Y&column=6">suffix rule</a></li>
      <li><a href="<%=request.getContextPath()%>/root-affix/dynamic-suffix-rule.jsp?suffixes=ise-ize&words_type=SYLLABUS">dynamic suffix rule</a></li>
      <li><a href="<%=request.getContextPath()%>/similar/spell-similar-rule.jsp?word=legendary&count=100&words_type=SYLLABUS">spelling similarity rule</a></li>
      <li><a href="<%=request.getContextPath()%>/similar/definition-similar-rule.jsp?word=fabulous&count=100&words_type=SYLLABUS&dictionary=WEBSTER">definition similarity rule</a></li>
      <li><a href="<%=request.getContextPath()%>/similar/pronunciation-similar-rule.jsp?word=think&count=100&words_type=SYLLABUS&dictionary=ICIBA">pronunciation similarity rule</a></li>
    <%
      if(user != null){
    %>
      <li><a href="<%=request.getContextPath()%>/history/user-word-history.jsp">search word record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-text-history.jsp">text analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-url-history.jsp">web analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-book-history.jsp">book analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-similar-word-history.jsp">similar word analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-dynamic-prefix-history.jsp">dynamic prefix analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/user-dynamic-suffix-history.jsp">dynamic suffix analysis record</a></li>
      <li><a href="<%=request.getContextPath()%>/history/my-new-words-book.jsp">my new words book</a></li>
    <%
      }
    %>
    <%
      if(user != null && "ysc".equals(user.getUserName())){
    %>
      <li><a href="<%=request.getContextPath()%>/history/anti-robot-manager.jsp?limit=1000">anti robot management</a></li>
    <%
      }
    %>
    </ol>
  </p>
  <jsp:include page="common/bottom.jsp?index=true"/>
</body>
</html>
