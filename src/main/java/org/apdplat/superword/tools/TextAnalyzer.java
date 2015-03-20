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

/**
 * 文本分析工具
 *
 * @author 杨尚川
 */
public class TextAnalyzer {
    private TextAnalyzer() {
    }
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAnalyzer.class);

    /**
     * @param files 文件相对路径或绝对路径
     * @return 词频统计数据
     */
    public static Map<String, AtomicInteger> frequency(Collection<String> files) {
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
        for (String file : files) {
            LOGGER.info("parse text file: " + file);
            //统计词频
            Map<String, AtomicInteger> data = frequency(file);
            //合并结果
            data.entrySet().forEach(entry -> {
                map.putIfAbsent(entry.getKey(), new AtomicInteger());
                map.get(entry.getKey()).addAndGet(entry.getValue().get());
            });
            data.clear();
        }
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
            List<String> list = new ArrayList<String>();
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
                        if(StringUtils.isNotBlank(w) && !StringUtils.isNumeric(w)) {
                            data.add(w);
                            if (LOGGER.isDebugEnabled()) {
                                log.append(w).append(" ");
                            }
                        }
                    });
        }
        LOGGER.debug("分词："+log);
        return data;
    }

    /**
     * 处理分词意外，即无规则情况
     * @param word
     * @return
     */
    private static String irregularity(String word){
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

    public static String toHtmlFragment(Map<String, AtomicInteger> data, Set<String> fileNames) {
        StringBuilder html = new StringBuilder();
        html.append("统计书籍：<br/>\n");
        AtomicInteger i = new AtomicInteger();
        fileNames.stream()
                .sorted()
                .forEach(fileName -> html.append(i.incrementAndGet())
                        .append("、")
                        .append(Paths.get(fileName).toFile().getName().replace(".txt", ""))
                        .append("<br/>\n"));
        Map<Integer, Stat> stat = distribute(data);
        html.append("共有")
                .append(data.size())
                .append("个单词，出现次数统计：<br/>\n")
                .append("<table  border=\"1\"  bordercolor=\"#00CCCC\"  width=\"850\">\n\t<tr><td>序号</td><td>出现次数</td><td>单词个数</td><td>单词</td></tr>\n");
        AtomicInteger k = new AtomicInteger();
        stat.keySet()
                .stream()
                .sorted((a, b) -> b - a)
                .forEach(s -> {
                    html.append("\t<tr><td>")
                            .append(k.incrementAndGet())
                            .append("</td><td>")
                            .append(s)
                            .append("</td><td>")
                            .append(stat.get(s).count())
                            .append("</td><td>");
                    AtomicInteger z = new AtomicInteger();
                    List<String> list = stat.get(s).getWords();
                    list.stream()
                            .sorted()
                            .forEach(w -> {
                                if (list.size() > 1) {
                                    html.append(z.incrementAndGet())
                                            .append(".")
                                            .append(WordLinker.toLink(w))
                                            .append(" ");
                                } else if (list.size() == 1) {
                                    html.append(WordLinker.toLink(w));
                                }
                            });
                    html.append("</td></tr>\n");
                });
        html.append("</table>")
                .append("\n共有(")
                .append(data.size())
                .append(")个单词：<br/>\n")
                .append("<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger wordCounter = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() <= 14)
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .forEach(entry -> {
                    html.append("\t")
                            .append("<tr><td>")
                            .append(wordCounter.incrementAndGet())
                            .append("</td><td>")
                            .append(WordLinker.toLink(entry.getKey()))
                            .append("</td><td>")
                            .append(entry.getValue().get())
                            .append("</td></tr>\n");

                });
        html.append("</table>\n")
                .append("长度大于14的词：")
                .append("\n<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger j = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() > 14)
                .sorted((a, b) ->
                        b.getValue().get() - a.getValue().get())
                .forEach(entry ->
                        html.append("\t")
                                .append("<tr><td>")
                                .append(j.incrementAndGet())
                                .append("</td><td>")
                                .append(WordLinker.toLink(entry.getKey()))
                                .append("</td><td>")
                                .append(entry.getValue().get())
                                .append("</td></tr>\n"));

        html.append("</table>\n")
                .append("长度为2的词：")
                .append("\n<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger z = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() == 2)
                .sorted((a, b) ->
                        b.getValue().get() - a.getValue().get())
                .forEach(entry ->
                        html.append("\t")
                                .append("<tr><td>")
                                .append(z.incrementAndGet())
                                .append("</td><td>")
                                .append(WordLinker.toLink(entry.getKey()))
                                .append("</td><td>")
                                .append(entry.getValue().get())
                                .append("</td></tr>\n"));
        html.append("</table>");
        return html.toString();
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
        String htmlFragment = toHtmlFragment(data, fileNames);
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

    public static void main(String[] args) throws Exception {
        //parse("src/main/resources/it/spring/Spring in Action 4th Edition.txt");
        //parse("src/main/resources/it/spring");
        parse("src/main/resources/it");
        //footN("src/main/resources/it", 100);
        //topN("src/main/resources/it", 100);
    }

    public static void footN(String path, int limit) {
        sentence(path, limit, false);
    }

    public static void topN(String path, int limit) {
        sentence(path, limit, true);
    }
    public static void sentence(String path, int limit, boolean isTopN) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> fres = frequency(fileNames);
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
                        AtomicInteger fre = fres.get(word);
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
                ex.printStackTrace();
            }
        }
        AtomicInteger i = new AtomicInteger();
        sentences.entrySet().forEach(entry -> {
            //LOGGER.info(i.incrementAndGet()+"、分值："+entry.getKey()+":<br/>\n\t"+entry.getValue()+"<br/>\n");
            LOGGER.info(i.incrementAndGet()+"、"+entry.getValue()+"<br/><br/>\n");
        });
    }

    private static class Stat {
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
