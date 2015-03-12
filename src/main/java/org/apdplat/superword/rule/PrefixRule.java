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

import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.PrefixExtractor;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

/**
 * 从指定的英文单词的集合中找出符合前缀规则的单词
 * @author 杨尚川
 */
public class PrefixRule {
    private PrefixRule(){}

    public static TreeMap<Prefix, List<Word>> findByPrefix(Collection<Word> words, Collection<Prefix> prefixes) {
        TreeMap<Prefix, List<Word>> map = new TreeMap<>();
        for(Prefix prefix : prefixes){
            map.put(prefix, findByPrefix(words, prefix));
        }
        return map;
    }

    public static List<Word> findByPrefix(Collection<Word> words, Prefix prefix) {
        return words
                .parallelStream()
                .filter(word -> {
                    String w = word.getWord();
                    boolean hit = false;
                    String[] ps = prefix.getPrefix().toLowerCase().split(",");
                    for (String p : ps) {
                        p = p.replaceAll("-", "").replaceAll("\\s+", "");
                        if (w.toLowerCase().startsWith(p)) {
                            hit = true;
                            break;
                        }
                    }
                    return hit;
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
                    .append(" (")
                    .append(prefix.getDes())
                    .append(") (hit ")
                    .append(words.size())
                    .append(")</h2></br>\n");
            AtomicInteger wordCounter = new AtomicInteger();
            words.forEach(word -> {
                html.append("\t")
                        .append(wordCounter.incrementAndGet())
                        .append("、")
                        .append(WordLinker.toLink(word.getWord()))
                        .append("</br>\n");
            });
        }
        return html.toString();
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt", "/words_gre.txt");
        //List<Prefix> prefixes = PrefixExtractor.extract();
        //List<Prefix> prefixes = Arrays.asList(new Prefix("mono,mon", "单个，一个"));
        List<Prefix> prefixes = Arrays.asList(new Prefix("antiq", "=old,表示\"古老\""));

        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes);
        String htmlFragment = PrefixRule.toHtmlFragment(prefixToWords);

        Files.write(Paths.get("target/prefix_rule.txt"),htmlFragment.getBytes("utf-8"));

        System.out.println(htmlFragment);
    }
}
