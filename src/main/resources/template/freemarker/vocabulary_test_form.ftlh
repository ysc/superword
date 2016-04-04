<#setting url_escaping_charset='utf-8'>

<font color="red">
    <h1>${step}. ${quizItem.word.word}:</h1>
</font>
<h1>
    <ul>
    <#list quizItem.meanings as meaning>
        <p><li><a href="vocabulary-test.jsp?word=${quizItem.word.word}&answer=${meaning?url}">${meaning}</a></li></p>
    </#list>
    </ul>
</h1>


