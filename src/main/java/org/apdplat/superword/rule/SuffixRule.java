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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.*;
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.WordSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从指定的英文单词的集合中找出符合后缀规则的单词
 * @author 杨尚川
 */
public class SuffixRule{
    private SuffixRule(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(SuffixRule.class);

    public static List<Suffix> getAllSuffixes(){
    List<Suffix> suffixes = new ArrayList<>();
    try{
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/root_affix.txt"));
        lines.forEach(line ->{
            if(StringUtils.isNotBlank(line)
                    && !line.startsWith("#")
                    && line.startsWith("后缀：")){
                String[] attr = line.substring(3).split("杨尚川");
                if(attr != null && attr.length == 2){
                    String suffix = attr[0];
                    String meaning = attr[1];
                    if(suffix.contains(",")){
                        suffixes.addAll(new ComplexSuffix(suffix, meaning).simplify());
                        LOGGER.debug("复杂后缀："+suffix+meaning);
                    }else{
                        suffixes.add(new Suffix(suffix, meaning));
                        LOGGER.debug("后缀："+suffix+meaning);
                    }
                }else{
                    LOGGER.error("解析后缀出错："+line);
                }
            }
        });
    } catch (Exception e){
        LOGGER.error(e.getMessage(), e);
    }
    return suffixes;
}

    public static TreeMap<Suffix, List<Word>> findBySuffix(Collection<Word> words, Collection<Suffix> suffixes, boolean strict) {
        TreeMap<Suffix, List<Word>> map = new TreeMap<>();
        for(Suffix suffix : suffixes){
            map.put(suffix, findBySuffix(words, suffix, strict));
        }
        return map;
    }

    public static List<Word> findBySuffix(Collection<Word> words, Suffix suffix, boolean strict) {
        return words
                .parallelStream()
                .filter(word -> {
                    String w = word.getWord();
                    if(Character.isUpperCase(w.charAt(0))){
                        return false;
                    }
                    String s = suffix.getSuffix().replace("-", "").toLowerCase();

                    if(strict){
                        if(w.endsWith(s)
                                && w.length()-s.length()>2
                                && words.contains(new Word(w.substring(0, w.length()-s.length()), ""))){
                            return true;
                        }
                    } else if (w.endsWith(s)) {
                        return true;
                    }

                    return false;
                })
                .sorted()
                .collect(Collectors.toList());
    }

    private static Map<Word, List<Word>> convert(Map<Suffix, List<Word>> data){
        Map<Word, List<Word>> r = new HashMap<>();
        data.keySet().forEach(k -> r.put(new Word(k.getSuffix(), k.getDes()), data.get(k)));
        return r;
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getSyllabusVocabulary();
        //List<Suffix> suffixes = SuffixExtractor.extract();
        List<Suffix> suffixes = getAllSuffixes();

        TreeMap<Suffix, List<Word>> suffixToWords = SuffixRule.findBySuffix(words, suffixes, false);
        String htmlFragment = HtmlFormatter.toHtmlTableFragmentForRootAffix(convert(suffixToWords), 6);

        Files.write(Paths.get("target/suffix_rule.txt"), htmlFragment.getBytes("utf-8"));
    }
}
