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

<%@ page import="org.apdplat.superword.tools.AidReading" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.apdplat.superword.model.Word" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.apdplat.extractor.html.HtmlFetcher" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.apdplat.superword.model.UserUrl" %>
<%@ page import="org.apdplat.superword.tools.MySQLUtils" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="org.apdplat.superword.model.User" %>
<%@ page import="org.apdplat.extractor.html.impl.JSoupHtmlFetcher" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String url = request.getParameter("url");
    if(url == null){
        return;
    }
    url = URLDecoder.decode(url, "utf-8");
    User user = (User)session.getAttribute("user");
    UserUrl userUrl = new UserUrl();
    userUrl.setDateTime(new Date());
    userUrl.setUrl(url);
    userUrl.setUserName(user==null?"anonymity":user.getUserName());
    //保存用户网页分析记录
    MySQLUtils.saveUserUrlToDatabase(userUrl);

    Set<Word> words = (Set<Word>)application.getAttribute("words_"+request.getAttribute("words_type"));

    int column = 10;
    try{
        column = Integer.parseInt(request.getParameter("column"));
    }catch (Exception e){}
    String text = "";
    String htmlFragment = "";
    try{
        String fileName = MySQLUtils.MD5(url)+".txt";
        File file = new File(application.getRealPath("/WEB-INF/data/web-aid-reading/"+fileName));
        if(!Files.exists(file.getParentFile().toPath())){
            file.getParentFile().mkdirs();
        }
        if(Files.exists(file.toPath())){
            List<String> list = Files.readAllLines(file.toPath());
            if(list != null && list.size() >= 2){
                StringBuilder str = new StringBuilder();
                for(int i=1; i<list.size(); i++){
                    str.append(list.get(i)).append("\r\n");
                }
                text = str.toString();
            }
        }
        if(StringUtils.isBlank(text)) {
            HtmlFetcher htmlFetcher = new JSoupHtmlFetcher();
            text = Jsoup.parse(htmlFetcher.fetch(url)).text();
            if (StringUtils.isBlank(text)) {
                htmlFragment = "Failed to get the web page content, please try once again or re-enter the other web page url.";
            } else {
                Files.write(file.toPath(), Arrays.asList(url, text));
            }
        }
        if(StringUtils.isNotBlank(text)){
            htmlFragment = AidReading.analyse(words, column, false, null, Arrays.asList(text));
        }
    }catch (Exception e){
        htmlFragment = "Failed to get the web page content, please try once again or re-enter the other web page url.";
        e.printStackTrace();
    }
%>

<html>
<head>
    <title>web page auxiliary reading</title>
    <link href="<%=request.getContextPath()%>/css/superword.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/superword.js"></script>
    <script type="text/javascript">
        function update(){
            var words_type = document.getElementById("words_type").value;
            var url = document.getElementById("url").value;
            var column = document.getElementById("column").value;

            if(url == ""){
                return;
            }
            url = encodeURIComponent(url);
            location.href = "web-aid-reading.jsp?words_type="+words_type+"&url="+url+"&column="+column;
        }
        document.onkeypress=function(e){
            var e = window.event || e ;
            if(e.charCode == 13){
                update();
            }
        }
        var display = true;
        function change(){
            var text_div = document.getElementById("text_div");
            var tip = document.getElementById("tip");
            if(display){
                text_div.style.display = "none";
                tip.innerText = "Double click the word on the page to see the definition(Click Display): ";
            }else{
                text_div.style.display = "block";
                tip.innerText = "Double click the word on the page to see the definition(Click Hide): ";
            }
            display = !display;
        }
    </script>
</head>
<body id="top">
    <jsp:include page="../common/head.jsp"/>

    <h3>
        web page auxiliary reading
    </h3>

    <p>
        <font color="red">web page url: </font><input onchange="update();" id="url" name="url" value="<%=url%>" size="150" maxlength="500"><br/>
        <font color="red">words per line: </font><input onchange="update();" id="column" name="column" value="<%=column%>" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <jsp:include page="../select/words-select.jsp"/><br/>
    </p>
    <p>
        <font color="red"><span style="cursor: pointer" onclick="change();" id="tip">Double click the word on the page to see the definition(Click Hide): </span></font><br/>
        <div ondblclick="querySelectionWord();" id="text_div" style="display:block">
            <%=text.replace("\r", "").replace("\n", "</br>")%>
        </div>
    </p>
    <%=htmlFragment%>
    <jsp:include page="../common/bottom.jsp"/>
</body>
</html>
