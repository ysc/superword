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
import org.apdplat.superword.rule.WordVector.Score;
import org.apdplat.superword.tools.WordSources;

/**
 *
 * @author 杨尚川
 */
public class SimilarityRule {

    public void similarity(Set<String> wordSet, String target) {
        WordVector targetWordVecotr = WordVector.of(target);
        List<Score> result = wordSet.parallelStream()
                                    .map(item -> targetWordVecotr.score(WordVector.of(item), true))
                                    .filter(item -> item.getScore() > 5)
                                    .sorted()
                                    .collect(Collectors.toList());
        Collections.reverse(result);
        System.out.println("word "+target+" similarity rank: ");
        AtomicInteger i = new AtomicInteger();
        result.forEach(item -> System.out.println("\t"+i.incrementAndGet() + "、" + item.getWord() + " " + item.getScore() + " " + item.getExplain()));
    }
    public static void main(String[] args) throws Exception {
        Set<String> wordSet = WordSources.get("/words.txt", "/words_extra.txt");

        SimilarityRule similarityRule = new SimilarityRule();
        similarityRule.similarity(wordSet, "book");
    }
}
