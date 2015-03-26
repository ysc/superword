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
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 找出没有任何词根词缀的词
 * @author 杨尚川
 */
public class IndependentWordRule {
    private IndependentWordRule(){}

    public static List<String> getIndependentWord(){
        Set<Word> words = WordSources.getSyllabusVocabulary();

        List<Prefix> prefixes = PrefixRule.getAllPrefixes();
        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes, false);

        List<Suffix> suffixes = SuffixRule.getAllSuffixes();
        TreeMap<Suffix, List<Word>> suffixToWords = SuffixRule.findBySuffix(words, suffixes, false);

        List<Word> roots = RootRule.getAllRoots();
        TreeMap<Word, List<Word>> rootToWords = RootRule.findByRoot(words, roots);

        Set<Word> rs = new HashSet<>();
        prefixToWords.values().forEach(list -> rs.addAll(list));
        suffixToWords.values().forEach(list -> rs.addAll(list));
        rootToWords.values().forEach(list -> rs.addAll(list));

        return WordSources.minus(words, rs)
                .stream()
                .map(word -> WordLinker.toLink(word.getWord()))
                .sorted()
                .collect(Collectors.toList());
    }
    public static void main(String[] args) throws Exception {
        List<String> data = IndependentWordRule.getIndependentWord();
        String htmlFragment = HtmlFormatter.toHtmlTableFragment(data, 5);
        Files.write(Paths.get("target/independent_word_rule.txt"), htmlFragment.getBytes("utf-8"));
    }
}
