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
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.WordSources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 合成词：由多个现有词简单拼装在一起形成的词
 * @author 杨尚川
 */
public class CompoundWord {
    private CompoundWord(){}

    public static Map<Word, Map<Integer, List<Word>>> find(Set<Word> words){
        return find(words, words);
    }
    public static Map<Word, Map<Integer, List<Word>>> find(Set<Word> words, Set<Word> target){
        Map<Word, Map<Integer, List<Word>>> data = new HashMap<>();
        target.forEach(word -> data.put(word, find(words, word)));
        return data;
    }

    public static Map<Integer, List<Word>> find(Set<Word> words, String word){
        return find(words, new Word(word, ""));
    }

    public static Map<Integer, List<Word>> find(Set<Word> words, Word word){
        Map<Integer, List<Word>> data = new HashMap<>();
        String w = word.getWord();
        //从前向后
        for(int i=1; i<w.length(); i++){
            check(w, i, data, words);
        }
        return data;
    }

    private static void check(String word, int position, Map<Integer, List<Word>> data, Set<Word> words){
        //忽略长度小于3的词
        if(position < 3 || word.length() - position < 3){
            return;
        }
        String one = word.substring(0, position);
        String two = word.substring(position, word.length());
        if(words.contains(new Word(one, "")) && words.contains(new Word(two, ""))){
            data.put(position, new ArrayList<>());
            data.get(position).add(new Word(one, ""));
            data.get(position).add(new Word(two, ""));
        }

    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getSyllabusVocabulary();

        //Set<Word> target = new HashSet<>();
        //target.add(new Word("pendent", ""));
        //target.add(new Word("abhorrent", ""));
        Set<Word> target = WordSources.getSyllabusVocabulary();

        Map<Word, Map<Integer, List<Word>>> data = CompoundWord.find(words, target);
        String htmlFragment = HtmlFormatter.toHtmlForCompoundWord(data);

        Files.write(Paths.get("src/main/resources/compound_word.txt"), htmlFragment.getBytes("utf-8"));

        System.out.println(htmlFragment);
    }
}
