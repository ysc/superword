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

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apdplat.superword.tools.SuffixExtractor;
import org.apdplat.superword.tools.WordSources;

/**
 * 从指定的英文单词的集合中找出符合后缀规则的单词
 * @author 杨尚川
 */
public class SuffixRule {

    private final AtomicInteger SUFFIX_COUNTER = new AtomicInteger();

    /**
     * 后缀规则利用工具SuffixExtractor生成
     * @param wordSet 
     */
    public void suffixs(Set<String> wordSet) {
        SuffixExtractor.extract().forEach(suffix -> suffix(wordSet, suffix.getSuffix(), suffix.getDes()));
    }

    public void suffix(Set<String> wordSet, String suffix) {
        suffix(wordSet, suffix, "");
    }

    public void suffix(Set<String> wordSet, String suffix, String des) {
        List<String> words = wordSet.parallelStream()
                .filter(word -> {
                    word = word.toLowerCase();
                    boolean hit = false;
                    String[] ps = suffix.toLowerCase().split(",");
                    for (String p : ps) {
                        p = p.replace("-", "").replaceAll("\\s+", "");
                        if (word.endsWith(p) && wordSet.contains(word.substring(0, word.length() - p.length()))) {
                            hit = true;
                            break;
                        }
                    }
                    return hit;
                })
                .sorted()
                .collect(Collectors.toList());
        System.out.println("</br><h2>" + SUFFIX_COUNTER.incrementAndGet() + "、" + suffix + " (" + des + ") (hit " + words.size() + ")</h2></br>");
        AtomicInteger i = new AtomicInteger();
        words.stream().forEach(word -> System.out.println(i.incrementAndGet() + "、<a target=\"_blank\" href=\"http://www.iciba.com/" + word + "\">" + word + "</a></br>"));
    }
    public static void main(String[] args) throws Exception {
        Set<String> wordSet = WordSources.get("/words.txt", "/words_extra.txt");

        SuffixRule suffixRule = new SuffixRule();
        suffixRule.suffixs(wordSet);
    }
}
