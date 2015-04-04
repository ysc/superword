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
package org.apdplat.superword.tools;

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本分析工具
 *
 * @author 杨尚川
 */
public class TextAnalyzer {
    private TextAnalyzer() {
    }
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    private static final Pattern UNICODE = Pattern.compile("[uU][0-9a-fA-F]{4}");
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAnalyzer.class);

    /**
     * @param files 文件相对路径或绝对路径
     * @return 词频统计数据
     */
    public static Map<String, AtomicInteger> frequency(Collection<String> files) {
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
        files.forEach(file -> {
            LOGGER.info("parse text file: " + file);
            //统计词频
            Map<String, AtomicInteger> data = frequency(file);
            //合并结果
            data.entrySet().forEach(entry -> {
                map.putIfAbsent(entry.getKey(), new AtomicInteger());
                map.get(entry.getKey()).addAndGet(entry.getValue().get());
            });
            data.clear();
        });
        LOGGER.info("total unique words count: " + map.size());
        return map;
    }

    public static Map<String, AtomicInteger> frequency(String file) {
        try{
            return frequency(new FileInputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public static Map<String, AtomicInteger> frequency(InputStream inputStream) {
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                inputStream)))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                List<String> words = seg(line);
                words.forEach(word -> {
                    map.putIfAbsent(word, new AtomicInteger());
                    map.get(word).incrementAndGet();
                });
                words.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOGGER.info("unique words count: " + map.size());
        return map;
    }

    /**
     * 分词
     * @param sentence
     * @return
     */
    public static List<String> seg(String sentence) {
        List<String> data = new ArrayList<>();
        //以非字母字符切分行
        String[] words = sentence.trim().split("[^a-zA-Z0-9]");
        StringBuilder log = new StringBuilder();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("句子:" + sentence);
        }
        for (String word : words) {
            if (StringUtils.isBlank(word) || word.length()<2) {
                continue;
            }
            List<String> list = new ArrayList<>();
            //转换为全部小写
            if (word.length() < 6
                    //PostgreSQL等
                    || (Character.isUpperCase(word.charAt(word.length()-1))
                          && Character.isUpperCase(word.charAt(0)))
                    //P2P,Neo4j等
                    || PATTERN.matcher(word).find()
                    || StringUtils.isAllUpperCase(word)) {
                word = word.toLowerCase();
            }
            //按照大写字母进行单词拆分
            int last = 0;
            for (int i = 1; i < word.length(); i++) {
                if (Character.isUpperCase(word.charAt(i))
                        && Character.isLowerCase(word.charAt(i - 1))) {
                    list.add(word.substring(last, i));
                    last = i;
                }
            }
            if (last < word.length()) {
                list.add(word.substring(last, word.length()));
            }
            list.stream()
                    .map(w -> w.toLowerCase())
                    .forEach(w -> {
                        if (w.length() < 2) {
                            return;
                        }
                        w = irregularity(w);
                        if(StringUtils.isNotBlank(w)) {
                            data.add(w);
                            if (LOGGER.isDebugEnabled()) {
                                log.append(w).append(" ");
                            }
                        }
                    });
        }
        LOGGER.debug("分词：" + log);
        return data;
    }

    /**
     * 处理分词意外，即无规则情况
     * @param word
     * @return
     */
    private static String irregularity(String word){
        if(Character.isDigit(word.charAt(0))){
            LOGGER.debug("词以数字开头，忽略："+word);
            return null;
        }
        if(word.startsWith("0x")
                || word.startsWith("0X")){
            LOGGER.debug("词为16进制，忽略："+word);
            return null;
        }
        if(word.endsWith("l")
                && StringUtils.isNumeric(word.substring(0, word.length()-1))){
            LOGGER.debug("词为long类型数字，忽略："+word);
            return null;
        }
        if(UNICODE.matcher(word).find()){
            LOGGER.debug("词为UNICODE字符编码，忽略："+word);
            return null;
        }
        switch (word){
            //I’ll do it. You'll see.
            case "ll": return "will";
            //If you’re already building applications using Spring.
            case "re": return "are";
            //package com.manning.sdmia.ch04;
            case "ch": return "chapter";
            //you find you’ve made a
            case "ve": return "have";
            //but it doesn’t stop there.
            case "doesn": return "does";
            //but it isn’t enough.
            case "isn": return "is";
            //<input type="text" name="firstName" /><br/>
            case "br": return null;
        }
        return word;
    }

    /**
     * 将 {词 : 词频} 逆转过来为{词频 : 词数，前10个词}
     * @param data 词频统计结果
     * @return 词频分布统计
     */
    public static Map<Integer, Stat> distribute(Map<String, AtomicInteger> data) {
        Map<Integer, Stat> stat = new HashMap<>();
        data.entrySet()
                .forEach(entry -> {
                    Integer key = entry.getValue().get();
                    stat.putIfAbsent(key, new Stat());
                    stat.get(key).increment();
                    stat.get(key).addWords(entry.getKey());
                });
        return stat;
    }

    /**
     * 解析目录或文件
     * @param path
     */
    public static void parse(String path) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> data = frequency(fileNames);
        //渲染结果
        String htmlFragment = HtmlFormatter.toHtmlFragmentForText(data, fileNames);
        try{
            //保存结果
            String resultFile = "target/words_" + Paths.get(path).toFile().getName().replace(".txt", "") + ".txt";
            Files.write(Paths.get(resultFile), htmlFragment.getBytes("utf-8"));
            LOGGER.info("统计结果输出到文件：" + resultFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Set<String> getFileNames(String path){
        Set<String> fileNames = new HashSet<>();
        if(Files.isDirectory(Paths.get(path))) {
            LOGGER.info("处理目录：" + path);
        }else{
            LOGGER.info("处理文件：" + path);
            fileNames.add(path);
            return fileNames;
        }
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String fileName = file.toFile().getAbsolutePath();
                    if (!fileName.endsWith(".txt")) {
                        LOGGER.info("放弃处理非txt文件：" + fileName);
                        return FileVisitResult.CONTINUE;
                    }
                    fileNames.add(fileName);
                    return FileVisitResult.CONTINUE;
                }

            });
        }catch (IOException e){
            e.printStackTrace();
        }
        return fileNames;
    }

    /**
     *
     * @param path 待分析的文本路径，目录或文件的绝对路径
     * @param limit 句子限制
     * @param isTopN 是否是分值最高，反之为分值最低
     */
    public static TreeMap<Float, String> sentence(String path, int limit, boolean isTopN) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> frequency = frequency(fileNames);
        //有序
        TreeMap<Float, String> sentences = new TreeMap<>();
        //句子评分
        int count = 0;
        for(String fileName : fileNames) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(fileName))))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isBlank(line)) {
                        continue;
                    }
                    //计算分值
                    float score = 0;
                    List<String> words = seg(line);
                    for(String word : words){
                        AtomicInteger fre = frequency.get(word);
                        if(fre == null || fre.get() == 0){
                            LOGGER.error("评分句子没有词频信息：" + line);
                            score = 0;
                            break;
                        }
                        score += 1/(float)fre.get();
                    }
                    words.clear();
                    if(score > 0) {
                        //保存句子
                        if(sentences.get(score) != null){
                            continue;
                        }
                        sentences.put(score, line + " <u><i>" + Paths.get(fileName).toFile().getName().replace(".txt", "") + "</i></u>");
                        count++;
                        if(count >= limit) {
                            if(isTopN){
                                //删除分值最低的
                                sentences.pollFirstEntry();
                            }else{
                                //删除分值最高的
                                sentences.pollLastEntry();
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                LOGGER.error("句子评分出错", ex);
            }
        }
        return sentences;
    }

    /**
     * 将文本解析为词典
     * @param textPath
     * @param dicPath
     */
    public static void toDic(String textPath, String dicPath){
        Map<String, AtomicInteger> data = frequency(getFileNames(textPath));
        List<String> words = data
                .entrySet()
                .stream()
                .filter(w -> StringUtils.isAlpha(w.getKey())
                        && w.getKey().length() < 12)
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .map(e -> e.getValue()+"\t"+e.getKey())
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(dicPath), words);
        } catch (IOException e) {
            LOGGER.error("保存词典文件出错", e);
        }
    }

    /**
     *  CET4、CET6、GRE、IELTS、TOEFL、考研英语的词汇
     *  有哪些出现在了指定文本中
     * @param textPath
     * @return
     */
    public static String importantWords(String textPath){
        Set<Word> wordSet = WordSources.get("/word_CET4.txt",
                "/word_CET6.txt",
                "/word_GRE.txt",
                "/word_IELTS.txt",
                "/word_TOEFL.txt",
                "/word_考 研.txt");
        Map<Word, AtomicInteger> data = WordSources.convert(
                                                        frequency(
                                                                getFileNames(textPath)));
        Set<Map.Entry<Word, AtomicInteger>> entries = data.entrySet()
                .stream()
                .filter(entry -> wordSet.contains(entry.getKey()))
                .collect(Collectors.toSet());
        return HtmlFormatter.toHtmlTableFragment(entries, 5);
    }

    public static void main(String[] args) throws Exception {
        //parse("src/main/resources/it/spring/Spring in Action 4th Edition.txt");
        //parse("src/main/resources/it/spring");
        //parse("src/main/resources/it");
        //toDic("src/main/resources/it", "src/main/resources/word_it.txt");
        System.out.print(importantWords("src/main/resources/it"));
    }

    public static class Stat {
        private AtomicInteger count = new AtomicInteger();
        private List<String> words = new ArrayList<>();

        public int count() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }

        public List<String> getWords() {
            return words;
        }

        public void addWords(String word) {
            if (this.words.size() < 11) {
                this.words.add(word);
            }
        }
    }
}
