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
import org.apdplat.superword.tools.WordSources;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对词长分布进行统计
 * @author 杨尚川
 */
public class WordLengthStatistics {
    private WordLengthStatistics(){}

    public static Map<Integer, List<Word>> stat(Set<Word> words){
        Map<Integer, List<Word>> data = new TreeMap<>();
        words.forEach(word -> {
            Integer key = word.getWord().length();
            data.putIfAbsent(key, new LinkedList<>());
            data.get(key).add(word);
        });
        return data;
    }

    public static String toHtmlFragment(Map<Integer, List<Word>> data, Integer... len){
        return toHtmlFragment(data, false, len);
    }

    public static String toHtmlFragment(Map<Integer, List<Word>> data, Boolean detail, Integer... len){
        Set<Integer> lens = new HashSet<>(Arrays.asList(len));
        StringBuilder html = new StringBuilder();
        html.append("词长分布统计：\n");
        for (Integer length : data.keySet()){
            int size = data.get(length).size();
            html.append("长度")
                .append(length)
                .append(" : ")
                .append(size)
                .append("\n");
            if(detail || lens.contains(length)){
                AtomicInteger i = new AtomicInteger();
                data.get(length).forEach(word -> html.append("\t")
                        .append(i.incrementAndGet())
                        .append("、")
                        .append(word.getWord())
                        .append("\n"));
            }
        }
        return html.toString();
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.get("/words.txt", "/words_extra.txt");
        Map<Integer, List<Word>> data = WordLengthStatistics.stat(words);
        String html = WordLengthStatistics.toHtmlFragment(data, 1, 19, 20, 22, 28);
        System.out.println(html);
    }
}
