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

import org.apdplat.superword.model.Word;
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
     * @param words
     */
    public void transforms(Set<Word> words) {
        transform(words, "b", "p");
        transform(words, "b", "m");
        transform(words, "b", "f");
        transform(words, "b", "v");

        transform(words, "p", "m");
        transform(words, "p", "f");
        transform(words, "p", "v");

        transform(words, "m", "f");
        transform(words, "m", "v");

        transform(words, "f", "v");

        transform(words, "d", "t");
        transform(words, "d", "s");
        transform(words, "d", "c");
        transform(words, "d", "z");
        transform(words, "d", "th");

        transform(words, "t", "s");
        transform(words, "t", "c");
        transform(words, "t", "z");
        transform(words, "t", "th");

        transform(words, "s", "c");
        transform(words, "s", "z");
        transform(words, "s", "th");

        transform(words, "c", "z");
        transform(words, "c", "th");

        transform(words, "z", "th");

        transform(words, "g", "k");
        transform(words, "g", "c");
        transform(words, "g", "h");

        transform(words, "k", "c");
        transform(words, "k", "h");

        transform(words, "c", "h");

        transform(words, "r", "l");
        transform(words, "r", "n");

        transform(words, "l", "n");

        transform(words, "m", "n");

        transform(words, "a", "e");
        transform(words, "a", "i");
        transform(words, "a", "o");
        transform(words, "a", "u");

        transform(words, "e", "i");
        transform(words, "e", "o");
        transform(words, "e", "u");

        transform(words, "i", "o");
        transform(words, "i", "u");

        transform(words, "o", "u");

        //发音相同的字母和字母组合
        transform(words, "ph", "f");
        //字母长得像，容易写错
        transform(words, "v", "u");
        transform(words, "v", "w");
        transform(words, "u", "w");
        transform(words, "i", "l");
        transform(words, "i", "j");
        transform(words, "f", "t");
    }

    /**
     * 将单词中的一部分字母转变为另一部分字母
     * @param words 英文单词的集合
     * @param from 待转化的字母或字母组合
     * @param to 转换目标字母或字母组合
     */
    public void transform(Set<Word> words, String from, String to) {
        List<Word> list =
            words.parallelStream()
                 .filter(word ->
                             word.getWord().contains(from)
                             && words.contains(
                                     new Word(
                                             word.getWord().replaceAll(from, to), null)))
                 .sorted()
                 .collect(Collectors.toList());
        System.out.println("</br><h2>"
                            + from + " - " + to + " rule total number: "
                            + list.size() + "</h2></br>");
        AtomicInteger i = new AtomicInteger();
        list.stream()
            .forEach(word -> System.out.println(
                    i.incrementAndGet()
                    + "、<a target=\"_blank\" href=\"http://www.iciba.com/"
                    + word.getWord()
                    + "\">"
                    + word.getWord()
                    + "</a> -> <a target=\"_blank\" href=\"http://www.iciba.com/"
                    + word.getWord().replaceAll(from, to)
                    + "\">"
                    + word.getWord().replaceAll(from, to)
                    + "</a></br>"));
    }
    
    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt");

        CharTransformRule charTransformRule = new CharTransformRule();
        charTransformRule.transforms(words);
    }
}
