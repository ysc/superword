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
import org.apdplat.superword.model.ComplexPrefix;
import org.apdplat.superword.model.Prefix;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.WordSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从指定的英文单词的集合中找出符合前缀规则的单词
 * @author 杨尚川
 */
public class PrefixRule {
    private PrefixRule(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(PrefixRule.class);

    public static List<Prefix> getAllPrefixes(){
        List<Prefix> prefixes = new ArrayList<>();
        try{
            List<String> lines = Files.readAllLines(Paths.get("src/main/resources/root_affix.txt"));
            lines.forEach(line ->{
                if(StringUtils.isNotBlank(line)
                        && !line.startsWith("#")
                        && line.startsWith("前缀：")){
                    String[] attr = line.substring(3).split("杨尚川");
                    if(attr != null && attr.length == 2){
                        String prefix = attr[0];
                        String meaning = attr[1];
                        if(prefix.contains(",")){
                            prefixes.addAll(new ComplexPrefix(prefix, meaning).simplify());
                            LOGGER.debug("复杂前缀："+prefix+meaning);
                        }else{
                            prefixes.add(new Prefix(prefix, meaning));
                            LOGGER.debug("前缀："+prefix+meaning);
                        }
                    }else{
                        LOGGER.error("解析前缀出错："+line);
                    }
                }
            });
        } catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        return prefixes;
    }

    public static TreeMap<Prefix, List<Word>> findByPrefix(Collection<Word> words, Collection<Prefix> prefixes, boolean strict) {
        TreeMap<Prefix, List<Word>> map = new TreeMap<>();
        for(Prefix prefix : prefixes){
            map.put(prefix, findByPrefix(words, prefix, strict));
        }
        return map;
    }

    public static List<Word> findByPrefix(Collection<Word> words, Prefix prefix, boolean strict) {
        return words
                .parallelStream()
                .filter(word -> {
                    String w = word.getWord();
                    if(Character.isUpperCase(w.charAt(0))){
                        return false;
                    }
                    String p = prefix.getPrefix().replace("-", "").toLowerCase();

                    if(strict){
                        if(w.startsWith(p)
                                && w.length()-p.length()>2
                                && words.contains(new Word(w.substring(p.length()), ""))){
                            return true;
                        }
                    } else if (w.startsWith(p)) {
                        return true;
                    }

                    return false;
                })
                .sorted()
                .collect(Collectors.toList());
    }

    public static Map<Word, List<Word>> convert(Map<Prefix, List<Word>> data){
        Map<Word, List<Word>> r = new HashMap<>();
        data.keySet().forEach(k -> r.put(new Word(k.getPrefix(), k.getDes()), data.get(k)));
        return r;
    }

    public static void main(String[] args) throws Exception {
        Set<Word> words = WordSources.getSyllabusVocabulary();
        //List<Prefix> prefixes = PrefixExtractor.extract();
        //List<Prefix> prefixes = Arrays.asList(new Prefix("mono,mon", "单个，一个"));
        //List<Prefix> prefixes = new ComplexPrefix("dis-,in-,im-,il-,ir-,un-,mis-,non-,dis-,de-,anti-,counter-", "否定前缀").simplify();
        List<Prefix> prefixes = PrefixRule.getAllPrefixes();

        TreeMap<Prefix, List<Word>> prefixToWords = PrefixRule.findByPrefix(words, prefixes, false);
        String htmlFragment = HtmlFormatter.toHtmlTableFragmentForRootAffix(convert(prefixToWords), 6);

        Files.write(Paths.get("target/prefix_rule.txt"), htmlFragment.getBytes("utf-8"));
    }
}
