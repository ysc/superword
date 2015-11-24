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

import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Suffix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.WordSources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 依赖词规则：找出同时拥有前缀、后缀和词根的词
 * @author 杨尚川
 */
public class DependentWordRule {
    private DependentWordRule(){}

    public static Map<Word, List<Word>> getDependentWord(Set<Word> words){
        Map<Word, List<Word>> data = new HashMap<>();

        List<Prefix> prefixes = PrefixRule.getAllPrefixes();
        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes, false);

        List<Suffix> suffixes = SuffixRule.getAllSuffixes();
        TreeMap<Suffix, List<Word>> suffixToWords = SuffixRule.findBySuffix(words, suffixes, false);

        List<Word> roots = RootRule.getAllRoots();
        TreeMap<Word, List<Word>> rootToWords = RootRule.findByRoot(words, roots);

        Set<Word> prefixesWords = new HashSet<>();
        Set<Word> suffixesWords = new HashSet<>();
        Set<Word> rootsWords = new HashSet<>();

        prefixToWords.values().forEach(list -> prefixesWords.addAll(list));
        suffixToWords.values().forEach(list -> suffixesWords.addAll(list));
        rootToWords.values().forEach(list -> rootsWords.addAll(list));

        Set<Word> intersectionWords = WordSources.intersection(WordSources.intersection(prefixesWords, suffixesWords), rootsWords);

        prefixToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                if (intersectionWords.contains(word)) {
                    data.putIfAbsent(word, new ArrayList<>());
                    data.get(word).add(new Word(entry.getKey().getPrefix(), entry.getKey().getDes()));
                }
            });
        });

        suffixToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                if (intersectionWords.contains(word)) {
                    data.putIfAbsent(word, new ArrayList<>());
                    data.get(word).add(new Word(entry.getKey().getSuffix(), entry.getKey().getDes()));
                }
            });
        });

        rootToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                if (intersectionWords.contains(word)) {
                    data.putIfAbsent(word, new ArrayList<>());
                    data.get(word).add(entry.getKey());
                }
            });
        });

        return data;
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getSyllabusVocabulary();
        Map<Word, List<Word>> result = DependentWordRule.getDependentWord(words);
        List<String> htmlFragment = HtmlFormatter.toHtmlTableFragmentForIndependentWord(result, 5, 640);
        for(int i=0; i<htmlFragment.size(); i++) {
            Files.write(Paths.get("target/dependent_word_rule_"+(i+1)+".txt"), htmlFragment.get(i).getBytes("utf-8"));
        }
    }
}
