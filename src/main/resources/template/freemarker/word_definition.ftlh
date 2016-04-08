
    <p>
        <font color="red"><span id="tip"></span></font><br/>
        <font color="red">Input Word：</font><input onkeyup="instant(this.value);" onchange="query();" id="word" name="word" value="${word}" size="50" maxlength="50" autocomplete="off"/>
        <span style="cursor: pointer" onclick="query();"><font color="red">Submit</font></span><br/>
        <div id="instant_tip"></div>
    </p>
    <script type="text/javascript">
        document.getElementById('word').select();
        document.getElementById('word').focus();
    </script>

    <#if isMyNewWord>
    ${word} has been added to <a href="history/my-new-words-book.jsp">my new words book</a><br/><br/>
    <#else>
    <span id="action_add_to_my_new_words"><span onclick="addToMyNewWordsBook('${word}');" style="cursor:pointer"><font color="red">add to my new words book</font></span></span><br/><br/>
    </#if>

    <table>
        <tr>
        <#if  hasOxfordAudio>
            <th>Oxford Audio</th>
        </#if>
        <#if  hasWebsterAudio>
            <th>Webster's Audio</th>
        </#if>
        </tr>
        <tr>
        <#if  hasOxfordAudio>
            <td>
                <#list oxfordAudios as oxfordAudio>
                    <audio controls><source src="audio/oxford/${oxfordAudio}.mp3" type="audio/mpeg">Your browser does not support the audio element.</audio>
                </#list>
                <br/>
                <br/>
            </td>
        </#if>
        <#if  hasWebsterAudio>
            <td>
                <#list websterAudios as websterAudio>
                    <audio controls><source src="audio/webster/${websterAudio}.mp3" type="audio/mpeg">Your browser does not support the audio element.</audio>
                </#list>
                <br/>
                <br/>
            </td>
        </#if>
        </tr>
    </table>

    <font color="red">Word Level: ${wordLevels}</font><br/><br/>
    <a target="_blank" href="${servletContext}/char-transform-rule.jsp?word=${word}&words_type=SYLLABUS">transform character</a> <font color="red"> | </font>
    <a target="_blank" href="${servletContext}/root-affix/root_affix_rule.jsp?dict=ICIBA&word=${word}&column=6&strict=N">analyze roots and affix</a> <font color="red"> | </font>
    <a target="_blank" href="${servletContext}/similar/spell-similar-rule.jsp?word=${word}&count=100&words_type=SYLLABUS">similar spelling</a> <font color="red"> | </font>
    <a target="_blank" href="${servletContext}/similar/definition-similar-rule.jsp?word=${word}&count=100&words_type=SYLLABUS&dictionary=WEBSTER">similar definition</a> <font color="red"> | </font>
    <a target="_blank" href="${servletContext}/similar/pronunciation-similar-rule.jsp?word=${word}&count=100&words_type=SYLLABUS&dictionary=ICIBA">similar pronunciation</a><br/>

    <br/>

    <table border="1">
        <tr>
            <td>
                ${icibaDefinitionHtml}
            </td>
            <td>
                ${youdaoDefinitionHtml}
            </td>
        </tr>
        <tr>
            <td ondblclick="querySelectionWord();">
                phonetic symbol:
                <br/>
                ${icibaPronunciation}
                <br/>
                <br/>
                definition(Chinese):
                <br/>
                ${icibaDefinition}
            </td>
            <td ondblclick="querySelectionWord();">
                phonetic symbol:
                <br/>
                ${youdaoPronunciation}
                <br/>
                <br/>
                definition(Chinese):
                <br/>
                ${youdaoDefinition}
            </td>
        </tr>
        <tr>
            <td>
                ${oxfordDefinitionHtml}
            </td>
            <td>
                ${websterDefinitionHtml}
            </td>
        </tr>
        <tr>
            <td ondblclick="querySelectionWord();">
                phonetic symbol:
                <br/>
                ${oxfordPronunciation}
                <br/>
                <br/>
                definition:
                <br/>
                ${oxfordDefinition}
            </td>
            <td ondblclick="querySelectionWord();">
                phonetic symbol:
                <br/>
                ${websterPronunciation}
                <br/>
                <br/>
                definition:
                <br/>
                ${websterDefinition}
            </td>
        </tr>
    </table>
    <br/>
    <font color="red">Other English Dictionaries's definition: </font>${otherDictionary}
    <br/>
    <br/>
    <a target="_blank" href="pos.jsp">Comparison of part of speech symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a><br/>
    <a target="_blank" href="symbol.jsp">Comparison of phonetic symbol of the Oxford dictionary, Webster's dictionary, iCIBA and Youdao dictionary</a>