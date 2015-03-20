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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 句子评分工具
 *
 * @author 杨尚川
 */
public class SentenceScorer {

    private SentenceScorer() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SentenceScorer.class);
    public static TreeMap<Float, Map<String, List<String>>> score(String path){
        return score(path, Integer.MAX_VALUE);
    }
    public static TreeMap<Float, Map<String, List<String>>> score(String path, int limit) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = TextAnalyzer.getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> frequency = TextAnalyzer.frequency(fileNames);
        //有序
        TreeMap<Float, Map<String, List<String>>> sentences = new TreeMap<>();
        //避免重复句子
        Set<Integer> hashes = new HashSet<>();
        Set<String> repeat = new HashSet<>();
        //句子数限制，防止内存放不下啊，改进方法有很多，这里先不考虑了，以后再说，或有兴趣的朋友来加上。
        int count = 0;
        for(String fileName : fileNames) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(fileName))))) {
                String book = Paths.get(fileName).toFile().getName().replace(".txt", "");
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isBlank(line)) {
                        continue;
                    }
                    int hc = line.hashCode();
                    if(hashes.contains(hc)){
                        repeat.add(line);
                        continue;
                    }
                    hashes.add(hc);
                    //计算分值
                    float score = score(line, frequency);
                    if(score > 0) {
                        if(count >= limit) {
                            LOGGER.debug("句子评分达到要求的句子数："+limit+"，评分结束");
                            return sentences;
                        }
                        count++;
                        sentences.putIfAbsent(score, new HashMap<>());
                        sentences.get(score).putIfAbsent(book, new ArrayList<>());
                        sentences.get(score).get(book).add(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        LOGGER.debug("重复句子数："+repeat.size());
        AtomicInteger i = new AtomicInteger();
        repeat.forEach(r -> {
            LOGGER.debug("\t"+i.incrementAndGet()+"、"+r);
        });
        LOGGER.debug("评分结束，句子数："+count);
        return sentences;
    }
    public static void toTextFile(TreeMap<Float, Map<String, List<String>>> scores, String fileName){
        LOGGER.debug("将评分结果写入文件："+fileName);
        AtomicInteger bookCount = new AtomicInteger();
        AtomicInteger sentenceCount = new AtomicInteger();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new BufferedOutputStream(
                                new FileOutputStream(fileName))))) {
            AtomicInteger i = new AtomicInteger();
            scores.entrySet().forEach(score -> {
                writeLine(writer, "score_(" + i.incrementAndGet() + "/"+scores.size()+")" + "：" + score.getKey());
                Map<String, List<String>> books = score.getValue();
                AtomicInteger j = new AtomicInteger();
                books.entrySet().forEach(book -> {
                    writeLine(writer, "\tbook_(" + j.incrementAndGet() + "/"+books.size()+")" + "：" + book.getKey());
                    bookCount.incrementAndGet();
                    AtomicInteger k = new AtomicInteger();
                    book.getValue().forEach(sentence -> {
                        writeLine(writer, "\t\tsentence_(" + k.incrementAndGet() + "/"+book.getValue().size()+")" + "：" + sentence);
                        sentenceCount.incrementAndGet();
                    });
                });
            });
            writeLine(writer, "所有的句子数目："+sentenceCount.get());
        }catch (IOException e){
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.debug("所有的分值级别："+scores.keySet().size());
        LOGGER.debug("所有的句子数目："+sentenceCount.get());
        LOGGER.debug("评分结果写入文件完成");
    }
    private static void writeLine(BufferedWriter writer, String text){
        try {
            writer.write(text+"\n");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    public static float score(String sentence, Map<String, AtomicInteger> frequency){
        //int maxFrequency = frequency.values().parallelStream().max((a,b) -> a.get()-b.get()).get().get();
        //LOGGER.debug("最大词频："+maxFrequency);
        //计算分值
        //加isDebugEnabled判断，是因为，这个句子会被高频率调用啊，SO...YOU GOT IT?
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("为句子评分："+sentence);
        }
        float score = 0;
        List<String> words = TextAnalyzer.seg(sentence);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("分词结果：" + words);
        }
        for(String word : words){
            AtomicInteger fre = frequency.get(word);
            if(fre == null || fre.get() == 0){
                LOGGER.error("词"+word+"没有词频信息");
                continue;
            }
            int f = fre.get();
            float s = 1/(float)f;
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("词"+word+"的词频："+f+"，词的分数："+s);
            }
            score += s;
        }
        words.clear();
        score = Math.round(score*100)/(float)100;
        LOGGER.debug("分数总和："+score);
        return score;
    }

    public static void main(String[] args){
        TreeMap<Float, Map<String, List<String>>> scores = score("src/main/resources/it");
        toTextFile(scores, "target/sentence_score_rank.txt");
    }
}
