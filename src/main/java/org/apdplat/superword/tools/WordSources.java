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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 从多个文本文件中读取单词
 * 一行一个单词，单词和其他信息之间用空白字符隔开
 * @author 杨尚川
 */
public class WordSources {
    private WordSources(){}
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
    /**
     * 一行一个单词，单词和其他信息之间用空白字符隔开
     * @param index 单词用空白字符隔开后的索引，从0开始
     * @param files 单词文件类路径，以/开头
     * @return 不重复的单词集合
     */
    public static Set<Word> get(int index, String... files){
        Set<Word> set = new HashSet<>();
        for(String file : files){
            URL url = WordSources.class.getResource(file);
            System.out.println("parse word file: "+url);
            try {
                List<String> words = Files.readAllLines(Paths.get(url.toURI()));
                Set<Word> wordSet = words.parallelStream()
                                         .filter(line -> !line.trim().startsWith("#") && !"".equals(line.trim()))
                                         .map(line -> new Word(line.trim().split("\\s+")[index].replaceAll("\\s+", ""), null))
                                         .filter(word -> StringUtils.isAlphanumeric(word.getWord()))
                                         .collect(Collectors.toSet());
                set.addAll(wordSet);
            } catch (URISyntaxException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        System.out.println("unique words count: "+set.size());
        return set;
    }
    public static void main(String[] args) {
        get("/words.txt", "/words_extra.txt", "/words_gre.txt")
                .stream()
                .sorted()
                .forEach(word -> System.out.println(word.getWord()));
    }
}
