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

import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 词干规则
 * @author 杨尚川
 */
public class StemRule {
    private StemRule(){}

    public static TreeMap<Word, List<Word>> findByStem(Collection<Word> words, Collection<Word> stems) {
        TreeMap<Word, List<Word>> map = new TreeMap<>();
        stems.forEach(stem -> map.put(stem, findByStem(words, stem)));
        return map;
    }

    public static List<Word> findByStem(Collection<Word> words, Word stem) {
        return words
                .parallelStream()
                .filter(word -> {
                    //词区分大小写
                    String w = word.getWord();
                    //词干不区分大小写
                    String s = stem.getWord().toLowerCase();
                    //词中包含词干即可，不考虑位置和剩余部分
                    return w.contains(s);
                })
                .sorted()
                .collect(Collectors.toList());
    }

    public static String toHtmlFragment(Map<Word, List<Word>> stemToWords) {
        StringBuilder html = new StringBuilder();
        AtomicInteger stemCounter = new AtomicInteger();
        for (Map.Entry<Word, List<Word>> entry : stemToWords.entrySet()) {
            Word stem = entry.getKey();
            List<Word> words = entry.getValue();
            html.append("<h2>")
                    .append(stemCounter.incrementAndGet())
                    .append("、")
                    .append(stem.getWord())
                    .append(" (")
                    .append(stem.getMeaning())
                    .append(") (hit ")
                    .append(words.size())
                    .append(")</h2></br>\n");
            AtomicInteger wordCounter = new AtomicInteger();
            words.forEach(word -> {
                html.append("\t")
                        .append(wordCounter.incrementAndGet())
                        .append("、")
                        .append(WordLinker.toLink(word.getWord(), stem.getWord().toLowerCase()))
                        .append("</br>\n");
            });
        }
        return html.toString();
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt", "/words_gre.txt");
        List<Word> stems = Arrays.asList(new Word("onym", "=nam,表示\"名字\""));

        TreeMap<Word, List<Word>> stemToWords = StemRule.findByStem(words, stems);
        String htmlFragment = StemRule.toHtmlFragment(stemToWords);

        Files.write(Paths.get("target/stem_rule.txt"),htmlFragment.getBytes("utf-8"));

        System.out.println(htmlFragment);
    }
}
