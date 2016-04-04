<font color="red">
    Right Count: ${quiz.rightCount}, Wrong Count: ${quiz.wrongCount}, Your vocabulary is likely ${quiz.evaluationCount} words. The time you spent is ${quiz.consumedTime}
</font>
<br/>
<br/>
<table border="1">
    <tr align="left">
        <th>No.</th>
        <th>Word</th>
        <th>Right Or Wrong</th>
        <th>Your Answer</th>
        <th>Right Answer</th>
    </tr>
<#list quiz.quizItems as quizItem>
    <tr>
        <td>${quizItem?index+1}</td>
        <td>${quizItem.wordLink}</td>
    <#if  quizItem.answer == quizItem.word.meaning>
        <td>Right</td>
        <td>${quizItem.answer}</td>
        <td>${quizItem.word.meaning}</td>
    <#else>
        <td><font color="red">Wrong</font></td>
        <td><font color="red">${quizItem.answer}</font></td>
        <td><font color="blue">${quizItem.word.meaning}</font></td>
    </#if>
    </tr>
</#list>
</table>
<br/>
<a href="vocabulary-test.jsp?restart=true&dictionary=YOUDAO">Test Again (Chinese)</a>
<br/>
<a href="vocabulary-test.jsp?restart=true&dictionary=WEBSTER">Test Again (English)</a>
<br/>
