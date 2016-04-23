<#setting url_escaping_charset='utf-8'>
<h1>Click the correct meaning for the word <font color="red">${quizItem.word.word}:</font></h1>
<h2>
    <ul>
    <#list quizItem.meanings as meaning>
        <li><a href="${servletContext}/identify.quiz?word=${quizItem.word.word}&token=${token}&answer=${meaning?url}">${meaning}</a></li>
    </#list>
    </ul>
</h2>
<h1>If you can't answer the question correctly, you won't have the permission to access the web site.</h1>