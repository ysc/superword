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

import org.apdplat.superword.tools.WordSources;
import org.apdplat.word.analysis.*;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 利用word分词提供的文本相似度算法来辅助记忆英语单词
 * @author 杨尚川
 */
public class SimilarWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimilarWord.class);
    //所有的文本相似度算法
    private static final List<TextSimilarity> ALL_TEXT_SIMILARITIES = new ArrayList<>();
    static {
        TextSimilarity similarity = new EditDistanceTextSimilarity();
        similarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        ALL_TEXT_SIMILARITIES.add(similarity);

        similarity = new JaroDistanceTextSimilarity();
        similarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        ALL_TEXT_SIMILARITIES.add(similarity);

        similarity = new JaroWinklerDistanceTextSimilarity();
        similarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        ALL_TEXT_SIMILARITIES.add(similarity);
    }
    public SimilarWord(){
        textSimilarity = new EditDistanceTextSimilarity();
        textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
    }
    private boolean all = false;
    private int limit = 45;
    private TextSimilarity textSimilarity = null;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        LOGGER.info("设置显示结果条数为："+limit);
    }

    public TextSimilarity getTextSimilarity() {
        return textSimilarity;
    }

    public void setTextSimilarity(TextSimilarity textSimilarity) {
        this.textSimilarity = textSimilarity;
        this.textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        LOGGER.info("设置相似度算法为："+textSimilarity.getClass().getName());
    }

    public Map<String, Hits> compute(String word, List<TextSimilarity> textSimilarities, List<String> words, int limit){
        Map<String, Hits> hitses = new HashMap<>();
        textSimilarities.forEach(textSimilarity -> {
            Hits hits = textSimilarity.rank(word, words, limit);
            hitses.put(textSimilarity.getClass().getSimpleName().replace("TextSimilarity", ""), hits);
        });
        return hitses;
    }

    public Hits compute(String word, TextSimilarity textSimilarity, List<String> words, int limit){
        return textSimilarity.rank(word, words, limit);
    }

    private void tip(){
        LOGGER.info("----------------------------------------------------------");
        LOGGER.info("可通过输入命令sa=edi来指定相似度算法，可用的算法有：");
        LOGGER.info("   1、sa=edi，编辑距离");
        LOGGER.info("   2、sa=ja，Jaro距离");
        LOGGER.info("   3、sa=jaw，Jaro–Winkler距离");
        LOGGER.info("可通过输入命令sa=all来启用所有的相似度算法");
        LOGGER.info("可通过输入命令limit=45来指定显示结果条数");
        LOGGER.info("可通过输入命令exit退出程序");
        LOGGER.info("输入要查询的词或命令：");
    }

    private void interact(String encoding, List<String> words) throws Exception{
        tip();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, encoding))){
            String line = null;
            while((line = reader.readLine()) != null){
                if("exit".equals(line)){
                    System.exit(0);
                }
                if(line.startsWith("limit=")){
                    try{
                        setLimit(Integer.parseInt(line.replace("limit=", "").trim()));
                    }catch (Exception e){
                        LOGGER.error("指令不正确，数字非法");
                    }
                    continue;
                }
                if(line.startsWith("sa=")){
                    switch (line.substring(3)){
                        case "edi": setTextSimilarity(new EditDistanceTextSimilarity());all=false;continue;
                        case "ja": setTextSimilarity(new JaroDistanceTextSimilarity());all=false;continue;
                        case "jaw": setTextSimilarity(new JaroWinklerDistanceTextSimilarity());all=false;continue;
                        case "all": LOGGER.info("启用所有的相似度算法");all=true;continue;
                    }
                    continue;
                }
                LOGGER.info("计算相似词：" + line);
                LOGGER.info("显示结果数目：" + limit);
                LOGGER.info("----------------------------------------------------------");
                if(all){
                    process(line, words, ALL_TEXT_SIMILARITIES);
                }else{
                    process(line, words, getTextSimilarity());
                }
                tip();
            }
        }
    }
    private void process(String word, List<String> words, List<TextSimilarity> textSimilarities){
        textSimilarities.forEach(textSimilarity -> process(word, words, textSimilarity));
    }
    private void process(String word, List<String> words, TextSimilarity textSimilarity){
        LOGGER.info("----------------------------------------------------------");
        LOGGER.info(word+" 的相似词（"+textSimilarity.getClass().getSimpleName()+"）：");
        long start = System.currentTimeMillis();
        Hits hits = compute(word, textSimilarity, words, limit);
        long cost = System.currentTimeMillis() - start;
        AtomicInteger i = new AtomicInteger();
        for(Hit hit : hits.getHits()){
            LOGGER.info("\t"+i.incrementAndGet()+"、"+hit.getScore()+" "+ hit.getText());
        }
        LOGGER.info("耗时：" + getTimeDes(cost));
        LOGGER.info("----------------------------------------------------------");
    }

    /**
     * 根据毫秒数转换为自然语言表示的时间
     * @param ms 毫秒
     * @return 自然语言表示的时间
     */
    public String getTimeDes(Long ms) {
        //处理参数为NULL的情况
        if(ms == null){
            return "";
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder str=new StringBuilder();
        if(day>0){
            str.append(day).append("天,");
        }
        if(hour>0){
            str.append(hour).append("小时,");
        }
        if(minute>0){
            str.append(minute).append("分钟,");
        }
        if(second>0){
            str.append(second).append("秒,");
        }
        if(milliSecond>0){
            str.append(milliSecond).append("毫秒,");
        }
        if(str.length()>0){
            str=str.deleteCharAt(str.length()-1);
        }

        return str.toString();
    }

    public static void main(String[] args) throws Exception {
        //所有的英语单词
        List<String> words = WordSources.getSyllabusVocabulary().parallelStream().map(word -> word.getWord()).collect(Collectors.toList());

        String encoding = "utf-8";
        if(args.length == 1){
            encoding = args[0];
        }

        SimilarWord similarWord = new SimilarWord();
        similarWord.interact(encoding, words);
    }
}
