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
import org.apdplat.superword.model.Suffix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.SuffixExtractor;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

/**
 * 从指定的英文单词的集合中找出符合后缀规则的单词
 * @author 杨尚川
 */
public class SuffixRule{
        private SuffixRule(){}

        public static TreeMap<Suffix, List<Word>> findBySuffix(Collection<Word> words, Collection<Suffix> suffixes) {
            TreeMap<Suffix, List<Word>> map = new TreeMap<>();
            for(Suffix suffix : suffixes){
                map.put(suffix, findBySuffix(words, suffix));
            }
            return map;
        }

        public static List<Word> findBySuffix(Collection<Word> words, Suffix suffix) {
            return words
                    .parallelStream()
                    .filter(word -> {
                        String w = word.getWord();
                        boolean hit = false;
                        String[] ps = suffix.getSuffix().toLowerCase().split(",");
                        for (String p : ps) {
                            p = p.replaceAll("-", "").replaceAll("\\s+", "");
                            if (w.toLowerCase().endsWith(p)) {
                                hit = true;
                                break;
                            }
                        }
                        return hit;
                    })
                    .sorted()
                    .collect(Collectors.toList());
        }

        public static String toHtmlFragment(Map<Suffix, List<Word>> suffixToWords) {
            StringBuilder html = new StringBuilder();
            AtomicInteger suffixCounter = new AtomicInteger();
            for (Map.Entry<Suffix, List<Word>> entry : suffixToWords.entrySet()) {
                Suffix suffix = entry.getKey();
                List<Word> words = entry.getValue();
                html.append("<h2>")
                        .append(suffixCounter.incrementAndGet())
                        .append("、")
                        .append(suffix.getSuffix())
                        .append(" (")
                        .append(suffix.getDes())
                        .append(") (hit ")
                        .append(words.size())
                        .append(")</h2></br>\n");
                AtomicInteger wordCounter = new AtomicInteger();
                words.forEach(word -> {
                    html.append("\t")
                            .append(wordCounter.incrementAndGet())
                            .append("、")
                            .append(WordLinker.toLink(word.getWord(), suffix.getSuffix()))
                            .append("</br>\n");
                });
            }
            return html.toString();
        }

        public static void main(String[] args) throws Exception {
            Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt", "/words_gre.txt");
            //List<Suffix> suffixes = SuffixExtractor.extract();
            //List<Suffix> suffixes = Arrays.asList(new Suffix("ization", ""));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("iority", ""));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("onomy", "...学"), new Suffix("onomics", "...学"), new Suffix("ology", "...学"));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("ache", "...疼"));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("tain", ""));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("igation", ""));
            //List<Suffix> suffixes = Arrays.asList(new Suffix("arian", "表形容词或名词,\"……的(人)\""));
            List<Suffix> suffixes = Arrays.asList();


            TreeMap<Suffix, List<Word>> suffixToWords = SuffixRule.findBySuffix(words, suffixes);
            String htmlFragment = SuffixRule.toHtmlFragment(suffixToWords);

            Files.write(Paths.get("target/suffix_rule.txt"), htmlFragment.getBytes("utf-8"));

            System.out.println(htmlFragment);
        }
}
