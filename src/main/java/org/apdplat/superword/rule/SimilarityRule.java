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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apdplat.superword.model.Word;
import org.apdplat.superword.rule.WordVector.Score;
import org.apdplat.superword.tools.WordSources;

/**
 * 如何判断两个英文单词是否相似？
 * 1、含义
 * 2、读音
 * 3、拼写
 * 利用词向量技术，可以从拼写方面找出相似的词
 * 我们一般都很难分辨双胞胎，因为他们长得太像了
 * 不过呢，双胞胎的父母却能一眼识别，为什么？
 * 因为有长期的对比识别啊
 * 记忆英语单词也一样，把相似的词找出来对比记忆
 * 往往事半功倍
 * @author 杨尚川
 */
public class SimilarityRule {

    public void similarity(Set<Word> words, String target) {
        WordVector targetWordVecotr = WordVector.of(target);
        List<Score> scores = words.parallelStream()
                                    .map(word -> targetWordVecotr.score(WordVector.of(word.getWord()), true))
                                    .filter(item -> item.getScore() > 5)
                                    .sorted()
                                    .collect(Collectors.toList());
        Collections.reverse(scores);
        System.out.println("word "+target+" similarity rank: ");
        AtomicInteger i = new AtomicInteger();
        scores.forEach(score -> System.out.println("\t"+i.incrementAndGet() + "、" + score.getWord() + " " + score.getScore() + " " + score.getExplain()));
    }
    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getAll();

        SimilarityRule similarityRule = new SimilarityRule();
        similarityRule.similarity(words, "book");
    }
}
