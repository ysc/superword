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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>superword是一个Java实现的英文单词分析软件，主要研究英语单词音近形似转化规律、前缀后缀规律、词之间的相似性规律等等。</title>
</head>
<body>
  <h2><a href="https://github.com/ysc/superword" target="_blank">superword主页</a></h2>
  <p>superword是一个Java实现的英文单词分析软件，主要研究英语单词音近形似转化规律、前缀后缀规律、词之间的相似性规律等等。Clean code、Fluent style、Java8 feature: Lambdas, Streams and Functional-style Programming。</p>
  <p>升学考试、工作求职、充电提高，都少不了英语的身影，英语对我们来说实在太重要了。你还在为记不住英语单词而苦恼吗？还在为看不懂英文资料和原版书籍而伤神吗？superword可以在你英语学习的路上助你一臂之力。</p>
  <p>superword利用计算机强大的计算能力，使用机器学习和数据挖掘算法找到读音相近、外形相似、含义相关、同义反义、词根词缀的英语单词，从而非常有利于我们深入地记忆理解这些单词。</p>

  <a target="_blank" href="aid-reading.jsp?words_type=SYLLABUS&dict=ICIBA&book=/it/java/Java%208%20in%20Action%20Lambdas,%20Streams%20and%20Functional-style%20Programming.txt&column=6">书籍辅助阅读</a><br/>
  <a target="_blank" href="aid-reading-detail.jsp?book=/it/java/Java%208%20in%20Action%20Lambdas,%20Streams%20and%20Functional-style%20Programming.txt&word=functional&dict=ICIBA&pageSize=192">搜索单词对应书籍原文</a><br/>
  <a target="_blank" href="root_affix_rule.jsp?dict=ICIBA&word=abbreviation&column=6&strict=N">词根词缀分析规则</a><br/>
  <a target="_blank" href="compound-word-rule.jsp?dict=ICIBA&word=fearless&column=6">复合词分析规则</a><br/>
  <a target="_blank" href="roots.jsp">常见词根</a><br/>
  <a target="_blank" href="root-rule.jsp?roots=spect,spic&dict=ICIBA&words_type=SYLLABUS&column=6">词根规则</a><br/>
  <a target="_blank" href="prefixes.jsp">常见前缀</a><br/>
  <a target="_blank" href="prefix-rule.jsp?prefixes=anti,counter,de,dis,il,im,in,ir,mis,non,un&dict=ICIBA&words_type=SYLLABUS&strict=Y&column=6">前缀规则</a><br/>
  <a target="_blank" href="dynamic-prefix-rule.jsp?prefixes=m-imm&dict=ICIBA&words_type=SYLLABUS">动态前缀规则</a><br/>
  <a target="_blank" href="suffixes.jsp">常见后缀</a><br/>
  <a target="_blank" href="suffix-rule.jsp?suffixes=ence,ance,age&dict=ICIBA&words_type=SYLLABUS&strict=Y&column=6">后缀规则</a><br/>
  <a target="_blank" href="dynamic-suffix-rule.jsp?suffixes=ise-ize&dict=ICIBA&words_type=SYLLABUS">动态后缀规则</a><br/>
  <a target="_blank" href="similar-word-rule.jsp?word=north&count=10&dict=ICIBA&words_type=SYLLABUS">拼写相似规则</a><br/>
  <a target="_blank" href="user-word-history.jsp?dict=ICIBA">用户查词记录</a>
</body>
</html>
