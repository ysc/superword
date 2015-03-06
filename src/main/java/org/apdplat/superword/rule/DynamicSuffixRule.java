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
package org.apdplat.superword.rule;

import org.apdplat.superword.model.Suffix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordSources;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 动态后缀规则，比如规则为：ise-ize，表示单词集合中
 * 有两个词分别以ise和ize结尾
 * 且除了后缀外，其他部分都相同
 * @author 杨尚川
 */
public class DynamicSuffixRule {
        private DynamicSuffixRule(){}

        public static List<Word> findBySuffix(Collection<Word> words, List<Suffix> suffixes) {
            if(suffixes == null || suffixes.size() < 2){
                return Arrays.asList();
            }
            return words
                    .parallelStream()
                    .filter(word -> {
                        String w = word.getWord();

                        String p = suffixes.get(0).getSuffix().toLowerCase();
                        p = p.replaceAll("-", "").replaceAll("\\s+", "");

                        if (!w.toLowerCase().endsWith(p)) {
                            return false;
                        }

                        String common = w.substring(0, w.length()-p.length());
                        //这里要用for，忽略第一个元素
                        for(int i=1; i<suffixes.size(); i++){
                            String s = suffixes.get(i).getSuffix().toLowerCase();
                            s = s.replaceAll("-", "").replaceAll("\\s+", "");
                            if(!words.contains(new Word(common+s, ""))){
                                return false;
                            }
                        }

                        return true;
                        })
                    .sorted()
                    .collect(Collectors.toList());
        }

        public static String toHtmlFragment(List<Word> words, List<Suffix> suffixes) {
            StringBuilder html = new StringBuilder();
            html.append("<h4>common prefix different suffix: ");
            suffixes.forEach(suffix -> html.append(suffix.getSuffix()).append("\t"));
            html.append(" (hit ")
                .append(words.size())
                .append(")</h4></br>\n")
                .append("<table>\n");
            AtomicInteger wordCounter = new AtomicInteger();
            words.forEach(word -> {
                String w = word.getWord();
                html.append("\t")
                        .append("<tr><td>")
                        .append(wordCounter.incrementAndGet())
                        .append("、</td><td>")
                        .append("<a target=\"_blank\" href=\"http://www.iciba.com/")
                        .append(w)
                        .append("\">")
                        .append(w)
                        .append("</a>")
                        .append("</td>");
                String common = null;
                //这里用for比较适合，因为要break
                for(Suffix suffix : suffixes) {
                    String s = suffix.getSuffix().toLowerCase();
                    s = s.replaceAll("-", "").replaceAll("\\s+", "");
                    if(w.endsWith(s)){
                        common = w.substring(0, w.length() - s.length());
                        break;
                    }
                }
                if(common != null){
                    final String c = common;
                    suffixes.forEach(suffix -> {
                        String s = suffix.getSuffix().toLowerCase();
                        s = s.replaceAll("-", "").replaceAll("\\s+", "");
                        if(!w.endsWith(s)){
                            html.append("<td><a target=\"_blank\" href=\"http://www.iciba.com/")
                                    .append(c+s)
                                    .append("\">")
                                    .append(c+s)
                                    .append("</a></td>");
                        }
                    });
                }
                html.append("</tr>\n");
            });
            html.append("</table>");
            return html.toString();
        }

        public static void main(String[] args) throws Exception {
            Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt", "/words_anc.txt");
            List<Suffix> suffixes = Arrays.asList(new Suffix("ise", ""), new Suffix("ize", ""));

            List<Word> data = DynamicSuffixRule.findBySuffix(words, suffixes);
            String htmlFragment = DynamicSuffixRule.toHtmlFragment(data, suffixes);

            Files.write(Paths.get("target/dynamic_suffix_rule.txt"), htmlFragment.getBytes("utf-8"));

            System.out.println(htmlFragment);
        }
}
