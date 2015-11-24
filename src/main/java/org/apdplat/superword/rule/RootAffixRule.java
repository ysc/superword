/*
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * 词根词缀分析规则，分析单词可能拥有的所有前缀、后缀和词根
 * @author 杨尚川
 */
public class RootAffixRule {
    private RootAffixRule(){}

    public static Map<Word, List<Word>> getWord(Set<Word> words, boolean strict){
        Map<Word, Set<Word>> data = new HashMap<>();

        List<Prefix> prefixes = PrefixRule.getAllPrefixes();
        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes, strict);

        List<Suffix> suffixes = SuffixRule.getAllSuffixes();
        TreeMap<Suffix, List<Word>> suffixToWords = SuffixRule.findBySuffix(words, suffixes, strict);

        List<Word> roots = RootRule.getAllRoots();
        TreeMap<Word, List<Word>> rootToWords = RootRule.findByRoot(words, roots);

        prefixToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                data.putIfAbsent(word, new HashSet<>());
                data.get(word).add(new Word(entry.getKey().getPrefix(), entry.getKey().getDes()));
            });
        });
        suffixToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                data.putIfAbsent(word, new HashSet<>());
                data.get(word).add(new Word(entry.getKey().getSuffix(), entry.getKey().getDes()));
            });
        });
        rootToWords.entrySet().forEach(entry -> {
            entry.getValue().forEach(word -> {
                data.putIfAbsent(word, new HashSet<>());
                data.get(word).add(entry.getKey());
            });
        });

        Map<Word, List<Word>> result = new HashMap<>();
        data.entrySet().forEach(entry -> {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        });

        return result;
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getSyllabusVocabulary();
        Map<Word, List<Word>> result = RootAffixRule.getWord(words, false);
        List<String> htmlFragment = HtmlFormatter.toHtmlTableFragmentForIndependentWord(result, 5, 640);
        for(int i=0; i<htmlFragment.size(); i++) {
            Files.write(Paths.get("target/root_affix_rule_"+(i+1)+".txt"), htmlFragment.get(i).getBytes("utf-8"));
        }
    }
}
