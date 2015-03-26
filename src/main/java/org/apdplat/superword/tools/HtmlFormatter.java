/**
 *
 * APDPlat - Application Product Development Platform Copyright (c) 2013, 杨尚川,
 * yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.apdplat.superword.tools;

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.Word;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * HTML格式化工具，将生成的HTML片段发布到网络上的博客、日志中
 * @author 杨尚川
 */
public class HtmlFormatter {
    private HtmlFormatter(){}
    private static final String EM_PRE = "<span style=\"color:red\">";
    private static final String EM_SUF = "</span>";

    public static String toHtmlTableFragmentForRootAffix(Map<Word, List<Word>> rootAffixToWords, int rowLength) {
        StringBuilder html = new StringBuilder();
        AtomicInteger rootCounter = new AtomicInteger();
        Set<Word> unique = new HashSet<>();
        rootAffixToWords
        .keySet()
        .stream()
        .sorted()
        .forEach(rootAffix -> {
            List<Word> words = rootAffixToWords.get(rootAffix);
            html.append("<h4>")
                .append(rootCounter.incrementAndGet())
                .append("、")
                .append(rootAffix.getWord());
            if(StringUtils.isNotBlank(rootAffix.getMeaning())) {
                html.append(" (")
                    .append(rootAffix.getMeaning())
                    .append(") ");
            }
            html.append(" (hit ")
                .append(words.size())
                .append(")</h4></br>\n");
            List<String> data =
                    words
                        .stream()
                        .sorted()
                        .map(word -> {
                            unique.add(word);
                            return emphasize(word, rootAffix);
                        })
                        .collect(Collectors.toList());
            html.append(toHtmlTableFragment(data, rowLength));
        });
        String head = "词根词缀数："+rootAffixToWords.keySet().size()+"，单词数："+unique.size()+"<br/>\n";
        return head+html.toString();
    }

    public static String emphasize(Word word, Word rootAffix){
        String w = word.getWord();
        String r = rootAffix.getWord().replace("-", "").toLowerCase();
        //词就是词根
        if (w.length() == r.length()) {
            return WordLinker.toLink(w, r);
        }
        //词根在中间
        if (w.length() > r.length()
                && !w.startsWith(r)
                && !w.endsWith(r)) {
            return WordLinker.toLink(w, r, "-" + EM_PRE, EM_SUF + "-");
        }
        //词根在前面
        if (w.length() > r.length() && w.startsWith(r)) {
            return WordLinker.toLink(w, r, "" + EM_PRE, EM_SUF + "-");
        }
        //词根在后面面
        if (w.length() > r.length() && w.endsWith(r)) {
            return WordLinker.toLink(w, r, "-" + EM_PRE, EM_SUF + "");
        }
        return WordLinker.toLink(w, r);
    }

    public static String toHtmlTableFragment(Map<Word, AtomicInteger> words, int rowLength) {
        return toHtmlTableFragment(words.entrySet(), rowLength);
    }
    public static String toHtmlTableFragment(Set<Map.Entry<Word, AtomicInteger>> words, int rowLength) {

        List<String> data =
        words
            .stream()
            .sorted((a, b) -> b.getValue().get() - a.getValue().get())
            .map(entry -> {
                String link = WordLinker.toLink(entry.getKey().getWord());
                if (entry.getValue().get() > 0) {
                    link = link+"-"+entry.getValue().get();
                }
                return link;
            })
            .collect(Collectors.toList());

        return toHtmlTableFragment(data, rowLength);
    }

    public static String toHtmlTableFragmentForIndependentWord(Map<Word, List<Word>> data, int rowLength) {
        StringBuilder html = new StringBuilder();
        AtomicInteger wordCounter = new AtomicInteger();
        data
            .keySet()
            .stream()
            .sorted()
            .forEach(word -> {
                html.append("<h4>")
                        .append(wordCounter.incrementAndGet())
                        .append("、")
                        .append(word.getWord())
                        .append(" (form ")
                        .append(data.get(word).size())
                        .append(")</h4></br>\n");
                List<String> result = data
                        .get(word)
                        .stream()
                        .map(rootAffix -> emphasize(word, rootAffix))
                        .collect(Collectors.toList());
                html.append(toHtmlTableFragment(result, rowLength));
                result.clear();
                result = data
                        .get(word)
                        .stream()
                        .flatMap(rootAffix -> Arrays.asList(rootAffix.getWord(),rootAffix.getMeaning()).stream())
                        .collect(Collectors.toList());
                html.append(toHtmlTableFragment(result, 2));
                result.clear();
            });
        return html.toString();
    }

    public static String toHtmlTableFragment(List<String> data, int rowLength) {
        StringBuilder html = new StringBuilder();

        AtomicInteger rowCounter = new AtomicInteger();
        AtomicInteger wordCounter = new AtomicInteger();
        html.append("<table  border=\"1\">\n");
        data
            .forEach(datum -> {
                if (wordCounter.get() % rowLength == 0) {
                    if (wordCounter.get() == 0) {
                        html.append("\t<tr>");
                    } else {
                        html.append("</tr>\n\t<tr>");
                    }
                    rowCounter.incrementAndGet();
                    html.append("<td>").append(rowCounter.get()).append("</td>");
                }
                wordCounter.incrementAndGet();
                html.append("<td>").append(datum).append("</td>");
            });
        if(html.toString().endsWith("<tr>")){
            html.setLength(html.length()-5);
        }else{
            html.append("</tr>\n");
        }
        html.append("</table>\n");

        return html.toString();
    }
}
