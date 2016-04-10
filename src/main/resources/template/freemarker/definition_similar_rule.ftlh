    <p>
        ***definition similarity rule:
        To calculate the similarity among words according to the definition of words.
    </p>
    <p>
        <font color="red">input word: </font><input id="word" name="word" value="${word}" size="50" maxlength="50"><br/>
        <font color="red">result count: </font><input id="count" name="count" value="${count}" size="50" maxlength="50"><br/>
        <font color="red">select words level: </font>
        <span id="wordsLevel"></span>
        <br/>
        <font color="red">select dictionary: </font>
        <span id="dictionaries"></span>
        <br/>
        <br/>
        <span style="cursor: pointer" onclick="update();"><font color="red">Submit</font></span>
    </p>
    <table ondblclick="querySelectionWord();" border="1">
        <tr>
            <th>
                Rank
            </th>
            <th>
                Word
            </th>
            <th>
                Definition
            </th>
            <th>
                Score
            </th>
            <th>
                Compute Similarity
            </th>
        </tr>
    <#list results as result>
        <tr>
            <td>
                ${result?index+1}.
             </td>
             <td>
                ${result.url}
             </td>
             <td>
                ${result.definition}
            </td>
            <td>
                ${result.score}
            </td>
            <td>
                <a target="_blank" href="definition-similar-rule.jsp?dictionary=${dictionary}&word=${result.word}&count=${count}&words_type=${words_type}">similar word</a>
            </td>
        </tr>
    </#list>
    </table>
