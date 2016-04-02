<h1>
    The meaning of red color font is your answer, but the right answer is the meaning of blue color font for the word <font color="red">${quizItem.word.word}:</font>
</h1>
<h2>
    <ul>
<#list quizItem.meanings as meaning>
    <#if meaning == answer>
    <#--  用户答案 -->
        <li><font color="red">${meaning}</font></li>
    <#elseif meaning == quizItem.word.meaning>
    <#--  正确答案 -->
        <li><font color="blue">${meaning}</font></li>
    <#else>
    <#--  其他选项 -->
        <li>${meaning}</li>
    </#if>
</#list>
    </ul>
</h2>
<h1>
    <a href="">Continue...</a>
</h1>