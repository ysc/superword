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

import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HTML格式化工具，将生成的HTML片段发布到网络上的博客、日志中
 * @author 杨尚川
 */
public class HtmlFormatter {
    private HtmlFormatter(){}

    public static String toHtmlTableFragment(Map<Word, AtomicInteger> words, int rowLength) {
        return toHtmlTableFragment(words.entrySet(), rowLength);
    }
    public static String toHtmlTableFragment(Set<Map.Entry<Word, AtomicInteger>> words, int rowLength) {
        StringBuilder html = new StringBuilder();

        AtomicInteger rowCounter = new AtomicInteger();
        AtomicInteger wordCounter = new AtomicInteger();
        html.append("<table  border=\"1\">\n");
        words
            .stream()
            .sorted((a, b) -> b.getValue().get() - a.getValue().get())
            .forEach(entry -> {
                if (wordCounter.get() % rowLength == 0) {
                    if (wordCounter.get() == 0) {
                        rowCounter.incrementAndGet();
                        html.append("\t<tr><td>").append(rowCounter.get()).append("</td>");
                    } else {
                        rowCounter.incrementAndGet();
                        html.append("</tr>\n\t<tr><td>").append(rowCounter.get()).append("</td>");
                    }
                }
                wordCounter.incrementAndGet();
                html.append("<td>")
                    .append(WordLinker.toLink(entry.getKey().getWord()))
                    .append("-")
                    .append(entry.getValue().get())
                    .append("</td>");
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
