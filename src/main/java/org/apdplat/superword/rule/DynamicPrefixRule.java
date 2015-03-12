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

import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordSources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 动态前缀规则，比如规则为：m-imm，表示单词集合中
 * 有两个词分别以m和imm开始
 * 且除了前缀外，其他部分都相同
 * @author 杨尚川
 */
public class DynamicPrefixRule {
        private DynamicPrefixRule(){}

        public static List<Word> findByPrefix(Collection<Word> words, List<Prefix> prefixes) {
            if(prefixes == null || prefixes.size() < 2){
                return Arrays.asList();
            }
            return words
                    .parallelStream()
                    .filter(word -> {
                        String w = word.getWord();

                        String p = prefixes.get(0).getPrefix().toLowerCase();
                        p = p.replaceAll("-", "").replaceAll("\\s+", "");

                        if (!w.toLowerCase().startsWith(p)) {
                            return false;
                        }

                        String common = w.substring(p.length());
                        //这里要用for，忽略第一个元素
                        for(int i=1; i<prefixes.size(); i++){
                            String s = prefixes.get(i).getPrefix().toLowerCase();
                            s = s.replaceAll("-", "").replaceAll("\\s+", "");
                            if(!words.contains(new Word(s+common, ""))){
                                return false;
                            }
                        }

                        return true;
                        })
                    .sorted()
                    .collect(Collectors.toList());
        }

        public static String toHtmlFragment(List<Word> words, List<Prefix> prefixes) {
            StringBuilder html = new StringBuilder();
            html.append("<h4>common suffix different prefix: ");
            prefixes.forEach(prefix -> html.append(prefix.getPrefix()).append("\t"));
            html.append(" (hit ")
                .append(words.size())
                .append(")</h4></br>\n")
                .append("<table>\n");
            AtomicInteger wordCounter = new AtomicInteger();
            words.forEach(word -> {
                String w = word.getWord();
                String common = null;
                //这里用for比较适合，因为要break
                for(Prefix prefix : prefixes) {
                    String s = prefix.getPrefix().toLowerCase();
                    s = s.replaceAll("-", "").replaceAll("\\s+", "");
                    if(w.startsWith(s)){
                        common = w.substring(s.length());
                        break;
                    }
                }
                if(common != null){
                    html.append("\t")
                            .append("<tr><td>")
                            .append(wordCounter.incrementAndGet())
                            .append("、</td>");
                    final String c = common;
                    prefixes.forEach(prefix -> {
                        String s = prefix.getPrefix().toLowerCase();
                        s = s.replaceAll("-", "").replaceAll("\\s+", "");
                        html.append("<td><a target=\"_blank\" href=\"http://www.iciba.com/")
                                .append(s+c)
                                .append("\">")
                                .append(s+c)
                                .append("</a></td>");
                    });
                }
                html.append("</tr>\n");
            });
            html.append("</table>");
            return html.toString();
        }

        public static void main(String[] args) throws Exception {
            Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt");
            List<Prefix> prefixes = Arrays.asList(new Prefix("m", ""), new Prefix("imm", ""));

            List<Word> data = DynamicPrefixRule.findByPrefix(words, prefixes);
            String htmlFragment = DynamicPrefixRule.toHtmlFragment(data, prefixes);

            Files.write(Paths.get("target/dynamic_prefix_rule.txt"), htmlFragment.getBytes("utf-8"));

            System.out.println(htmlFragment);
        }
}
