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
import org.apdplat.superword.tools.WordSources;

/**
 *
 * @author 杨尚川
 */
public class CharTransformRule {

    public void transforms(Set<String> wordSet) {
        transform(wordSet, "b", "p");
        transform(wordSet, "b", "m");
        transform(wordSet, "b", "f");
        transform(wordSet, "b", "v");

        transform(wordSet, "p", "m");
        transform(wordSet, "p", "f");
        transform(wordSet, "p", "v");

        transform(wordSet, "m", "f");
        transform(wordSet, "m", "v");

        transform(wordSet, "f", "v");

        transform(wordSet, "d", "t");
        transform(wordSet, "d", "s");
        transform(wordSet, "d", "c");
        transform(wordSet, "d", "z");
        transform(wordSet, "d", "th");

        transform(wordSet, "t", "s");
        transform(wordSet, "t", "c");
        transform(wordSet, "t", "z");
        transform(wordSet, "t", "th");

        transform(wordSet, "s", "c");
        transform(wordSet, "s", "z");
        transform(wordSet, "s", "th");

        transform(wordSet, "c", "z");
        transform(wordSet, "c", "th");

        transform(wordSet, "z", "th");

        transform(wordSet, "g", "k");
        transform(wordSet, "g", "c");
        transform(wordSet, "g", "h");

        transform(wordSet, "k", "c");
        transform(wordSet, "k", "h");

        transform(wordSet, "c", "h");

        transform(wordSet, "r", "l");
        transform(wordSet, "r", "n");

        transform(wordSet, "l", "n");

        transform(wordSet, "m", "n");

        transform(wordSet, "a", "e");
        transform(wordSet, "a", "i");
        transform(wordSet, "a", "o");
        transform(wordSet, "a", "u");

        transform(wordSet, "e", "i");
        transform(wordSet, "e", "o");
        transform(wordSet, "e", "u");

        transform(wordSet, "i", "o");
        transform(wordSet, "i", "u");

        transform(wordSet, "o", "u");

        //发音相同的字母和字母组合
        transform(wordSet, "ph", "f");
        //字母长得像，容易写错
        transform(wordSet, "v", "u");
        transform(wordSet, "v", "w");
        transform(wordSet, "u", "w");
        transform(wordSet, "i", "l");
        transform(wordSet, "i", "j");
        transform(wordSet, "f", "t");
    }

    private void transform(Set<String> wordSet, String from, String to) {
        List<String> words = wordSet.parallelStream()
                .filter(word -> word.contains(from) && wordSet.contains(word.replaceAll(from, to)))
                .sorted()
                .collect(Collectors.toList());
        System.out.println("</br><h2>" + from + " - " + to + " rule total number: " + words.size() + "</h2></br>");
        AtomicInteger i = new AtomicInteger();
        words.stream().forEach(word -> System.out.println(i.incrementAndGet() + "、<a target=\"_blank\" href=\"http://www.iciba.com/" + word + "\">" + word + "</a> -> <a target=\"_blank\" href=\"http://www.iciba.com/" + word.replaceAll(from, to) + "\">" + word.replaceAll(from, to) + "</a></br>"));
    }
    
    public static void main(String[] args) throws Exception {
        Set<String> wordSet = WordSources.get("/words.txt", "/words_extra.txt");

        CharTransformRule charTransformRule = new CharTransformRule();
        charTransformRule.transforms(wordSet);
    }
}
