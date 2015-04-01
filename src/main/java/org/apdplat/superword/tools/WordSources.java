/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.superword.tools;

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 从多个文本文件中读取单词
 * 一行一个单词，单词和其他信息之间用空白字符隔开
 * @author 杨尚川
 */
public class WordSources {
    private WordSources(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(WordSources.class);

    /**
     * 考纲词汇
     * @return
     */
    public static Set<Word> getSyllabusVocabulary(){
        return get("/word_primary_school.txt",
                "/word_junior_school.txt",
                "/word_senior_school.txt",
                "/word_university.txt",
                "/word_new_conception.txt",
                "/word_ADULT.txt",
                "/word_CET4.txt",
                "/word_CET6.txt",
                "/word_TEM4.txt",
                "/word_TEM8.txt",
                "/word_CATTI.txt",
                "/word_GMAT.txt",
                "/word_GRE.txt",
                "/word_SAT.txt",
                "/word_BEC.txt",
                "/word_MBA.txt",
                "/word_IELTS.txt",
                "/word_TOEFL.txt",
                "/word_TOEIC.txt",
                "/word_考 研.txt");
    }
    public static Set<Word> getAll(){
        Set<Word> data = get("/words.txt", "/word_computer.txt");
        data.addAll(getSyllabusVocabulary());
        return data;
    }
    /**
     * 
     * 一行一个单词，单词和其他信息之间用空白字符隔开
     * 默认 index 为1
     * @param files 单词文件类路径，以/开头
     * @return 不重复的单词集合
     */
    public static Set<Word> get(String... files){
        return get(1, files);
    }

    public static Map<Word, AtomicInteger> convert(Map<String, AtomicInteger> words){
        Map<Word, AtomicInteger> result = new HashMap<>();
        words.keySet().forEach(w -> result.put(new Word(w, ""), words.get(w)));
        return result;
    }

    public static boolean isEnglish(String string){
        for(char c : string.toLowerCase().toCharArray()){
            if(c<'a' || c>'z'){
                return false;
            }
        }
        return true;
    }

    /**
     * 求交集
     * @param first
     * @param second
     * @return
     */
    public static Set<Word> intersection(Set<Word> first, Set<Word> second){
        LOGGER.info("求交集词典1："+first.size());
        LOGGER.info("求交集词典2："+second.size());
        Set<Word> result = first
                .stream()
                .filter(w -> second.contains(w))
                .collect(Collectors.toSet());
        LOGGER.info("交集词典："+result.size());
        return result;
    }
    public static Set<Word> minus(Set<Word> minuend, Set<Word> subtrahend){
        LOGGER.info("被减数个数："+minuend.size());
        LOGGER.info("减数个数："+subtrahend.size());
        Set<Word> result = minuend
                .stream()
                .filter(word -> !subtrahend.contains(word))
                .collect(Collectors.toSet());
        LOGGER.info("结果个数："+result.size());
        return result;
    }
    public static void save(Set<Word> words, String path){
        try {
            path = "src/main/resources" + path;
            LOGGER.info("开始保存词典：" + path);
            AtomicInteger i = new AtomicInteger();
            List<String> list = words
                        .stream()
                        .sorted()
                        .map(word -> i.incrementAndGet() + "\t" + word.getWord())
                        .collect(Collectors.toList());
            Files.write(Paths.get(path), list);
            LOGGER.info("保存成功");
        }catch (Exception e){
            LOGGER.error("保存词典失败", e);
        }
    }
    /**
     * 一行一个单词，单词和其他信息之间用空白字符隔开
     * @param index 单词用空白字符隔开后的索引，从0开始
     * @param files 单词文件类路径，以/开头
     * @return 不重复的单词集合
     */
    public static Set<Word> get(int index, String... files){
        Set<Word> set = new HashSet<>();
        for(String file : files){
            URL url = null;
            if(file.startsWith("/")){
                url = WordSources.class.getResource(file);
            }else{
                try {
                    url = Paths.get(file).toUri().toURL();
                }catch (Exception e){
                    LOGGER.error("构造URL出错", e);
                }
            }
            if(url == null){
                LOGGER.error("解析词典失败："+file);
                continue;
            }
            System.out.println("parse word file: "+url);
            List<String> words = getExistWords(url);
            Set<Word> wordSet = words.parallelStream()
                    .filter(line -> !line.trim().startsWith("#") && !"".equals(line.trim()))
                    .filter(line -> line.trim().split("\\s+").length >= index+1)
                    .map(line -> new Word(line.trim().split("\\s+")[index], ""))
                    .filter(word -> StringUtils.isAlphanumeric(word.getWord()))
                    .collect(Collectors.toSet());
            set.addAll(wordSet);
        }
        System.out.println("unique words count: "+set.size());
        return set;
    }
    private static List<String> getExistWords(URL url){
        try {
            return Files.readAllLines(Paths.get(url.toURI()));
        }catch (Exception e){
            return Collections.emptyList();
        }
    }
    public static Set<Word> stem(Set<Word> words){
        return words
                .stream()
                .filter(word -> word.getWord().length() > 3)
                .filter(word -> !isPlural(words, word))
                .collect(Collectors.toSet());
    }
    public static Map<String, String> plural(Set<Word> words){
        Map<String, String> data = new HashMap<>();
        words
                .stream()
                .filter(word -> word.getWord().length() > 3)
                .forEach(word -> {
                    isPlural(words, word, data);
                });
        return data;
    }
    public static boolean isPlural(Set<Word> words, Word word){
        return isPlural(words, word, new HashMap<>());
    }
    public static boolean isPlural(Set<Word> words, Word word, Map<String, String> data){
        String w = word.getWord();
        //1、以辅音字母+y结尾,变y为i再加es
        if (w.endsWith("ies")){
            char c = w.charAt(w.length()-4);
            if(!(isVowel(c))
                    && words.contains(new Word(w.substring(0, w.length()-4)+"y", ""))){
                log(w, "ies");
                data.put(w, "ies");
                return true;
            }
        }
        //2、以ce, se, ze结尾, 加s
        if(w.endsWith("ces")
                || w.endsWith("ses")
                || w.endsWith("zes")){
            if(words.contains(new Word(w.substring(0, w.length()-1), ""))){
                log(w, "s");
                data.put(w, "s");
                return true;
            }
        }
        //3、以s, sh, ch, x结尾, 加es
        if(w.endsWith("ses")
                || w.endsWith("shes")
                || w.endsWith("ches")
                || w.endsWith("xes")){
            if(words.contains(new Word(w.substring(0, w.length()-2), ""))){
                log(w, "es");
                data.put(w, "es");
                return true;
            }
        }
        //4、一般情况，加s
        if(w.endsWith("s")){
            if(words.contains(new Word(w.substring(0, w.length()-1), ""))){
                log(w, "s");
                data.put(w, "s");
                return true;
            }
        }
        return false;
    }
    private static void log(String word, String suffix){
        LOGGER.debug("发现复数："+word+"\t"+suffix);
    }
    public static boolean isVowel(char _char){
        switch (_char){
            case 'a':return true;
            case 'e':return true;
            case 'i':return true;
            case 'o':return true;
            case 'u':return true;
        }
        return false;
    }
    public static void main(String[] args) {
        //AtomicInteger i = new AtomicInteger();
        //stem(getSyllabusVocabulary()).forEach(w -> System.out.println(i.incrementAndGet() + "、" + w.getWord()));
        String html = HtmlFormatter.toHtmlForPluralFormat(plural(getSyllabusVocabulary()));
        System.out.println(html);
    }
}
