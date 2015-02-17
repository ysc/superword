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
 * 单词的发展是一个历史的递进的过程，从无到有，从有到多
 * 字母之间的转化是有一定规律的，如元音字母（a e i o u)之间相互转化
 * 发音相近的辅音(如双唇音唇齿音的清辅音和浊辅音等等)之间的转化
 * 发音相同的字母和字母组合之间的转化(如ph和f)
 * 长相相近的字母之间的转化（因为字母看上去长得像，古时候手写容易错，
 * 如V和U，M和N，等等，在长期的发展过程中，不小心写错的词
 * 由于作者的影响力大或者其他因素也会演化出新的单词，并具有相关的含义）。
 * @author 杨尚川
 */
public class CharTransformRule {
    /**
     * 内置规则
     * @param wordSet
     */
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

    /**
     * 将单词中的一部分字母转变为另一部分字母
     * @param wordSet 英文单词的集合
     * @param from 待转化的字母或字母组合
     * @param to 转换目标字母或字母组合
     */
    public void transform(Set<String> wordSet, String from, String to) {
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
