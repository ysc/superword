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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apdplat.superword.model.ComplexPrefix;
import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

/**
 * 从指定的英文单词的集合中找出符合前缀规则的单词
 * @author 杨尚川
 */
public class PrefixRule {
    private PrefixRule(){}

    public static TreeMap<Prefix, List<Word>> findByPrefix(Collection<Word> words, Collection<Prefix> prefixes, boolean strict) {
        TreeMap<Prefix, List<Word>> map = new TreeMap<>();
        for(Prefix prefix : prefixes){
            map.put(prefix, findByPrefix(words, prefix, strict));
        }
        return map;
    }

    public static List<Word> findByPrefix(Collection<Word> words, Prefix prefix, boolean strict) {
        return words
                .parallelStream()
                .filter(word -> {
                    String w = word.getWord();
                    if(Character.isUpperCase(w.charAt(0))){
                        return false;
                    }
                    String p = prefix.getPrefix().toLowerCase();

                    if(strict){
                        if(w.startsWith(p)
                                && w.length()-p.length()>2
                                && words.contains(new Word(w.substring(p.length()), ""))){
                            return true;
                        }
                    } else if (w.startsWith(p)) {
                        return true;
                    }

                    return false;
                })
                .sorted()
                .collect(Collectors.toList());
    }

    public static String toHtmlFragment(Map<Prefix, List<Word>> prefixToWords) {
        StringBuilder html = new StringBuilder();
        AtomicInteger prefixCounter = new AtomicInteger();
        for (Map.Entry<Prefix, List<Word>> entry : prefixToWords.entrySet()) {
            Prefix prefix = entry.getKey();
            List<Word> words = entry.getValue();
            html.append("<h2>")
                    .append(prefixCounter.incrementAndGet())
                    .append("、")
                    .append(prefix.getPrefix())
                    .append("- (")
                    .append(prefix.getDes())
                    .append(") (hit ")
                    .append(words.size())
                    .append(")</h2></br>\n");
            AtomicInteger wordCounter = new AtomicInteger();
            html.append("<table>\n");
            words.forEach(word -> {
                if(wordCounter.get()%3 == 0){
                    if(wordCounter.get() == 0){
                        html.append("\t<tr>");
                    }else{
                        html.append("</tr>\n\t<tr>");
                    }
                }
                wordCounter.incrementAndGet();
                html.append("<td>")
                        .append(WordLinker.toLink(word.getWord(), prefix.getPrefix(), "<b>", "</b>-"))
                        .append("    ")
                        .append(WordLinker.toLink(word.getWord().substring(prefix.getPrefix().length())))
                        .append("</td>");
            });
            if(html.toString().endsWith("<tr>")){
                html.setLength(html.length()-5);
            }else{
                html.append("</tr>\n");
            }
            html.append("</table>\n");
        }
        return html.toString();
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.get("/words.txt",
                                        "/words_extra.txt",
                                        //"/words_anc.txt",
                                        "/words_gre.txt"
                                        );
        //List<Prefix> prefixes = PrefixExtractor.extract();
        //List<Prefix> prefixes = Arrays.asList(new Prefix("mono,mon", "单个，一个"));
        //List<Prefix> prefixes = Arrays.asList(new Prefix("antiq", "=old,表示\"古老\""));
        //List<Prefix> prefixes = Arrays.asList(new Prefix("pseud", "=fake,表示\"假的\""));
        //List<Prefix> prefixes = Arrays.asList(new Prefix("super", "表示\"在……上面\"或表示\"超级,超过,过度\""));
        //List<Prefix> prefixes = Arrays.asList(new Prefix("semi", "表示\"在……上面\"或表示\"超级,超过,过度\""));
        List<Prefix> prefixes = new ComplexPrefix("dis-,in-,im-,il-,ir-,un-,mis-,non-,dis-,de-,anti-,counter-", "否定前缀").simplify();

        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes, true);
        String htmlFragment = PrefixRule.toHtmlFragment(prefixToWords);

        Files.write(Paths.get("target/prefix_rule.txt"),htmlFragment.getBytes("utf-8"));

        System.out.println(htmlFragment);
    }
}
