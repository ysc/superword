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

package org.apdplat.superword.tools;

import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 辅助阅读:
 * 以电影功夫熊猫使用的单词分析为例
 * 你英语四级过了吗? 功夫熊猫看了吗?
 * 去除停用词后,功夫熊猫使用了794个英语单词,你会说很简单吧,别急,这些单词中仍然有148个单词不在四级词汇表中,花两分钟时间看看你是否认识这些单词.
 * Created by ysc on 11/15/15.
 */
public class AidReading {
    private static final Set<Word> STOP_WORDS = WordSources.get("/word_stop.txt");

    public static void main(String[] args) throws IOException {
        WordLinker.serverRedirect = null;

        String result = analyse(WordSources.get("/word_CET4.txt"), Dictionary.ICIBA, 6, "/it/movie/kungfupanda.txt");

        //String result = analyse(WordSources.get("/word_CET4.txt"), Dictionary.ICIBA, 6, "/it/movie/kungfupanda.txt", "/it/movie/kungfupanda2.txt");

        /*
        String url = "http://spark.apache.org/docs/latest/streaming-programming-guide.html";
        String text = Jsoup.parse(new URL(url), 60000).text();
        System.out.println(text);
        String result = analyse(WordSources.get("/word_CET4.txt"), Dictionary.ICIBA, 6, false, null, Arrays.asList(text));
        */

        System.out.println(result);
    }
    public static String analyse(Set<Word> words, Dictionary dictionary, int column, String... resources) {
        return analyse(words, dictionary, column, false, null, resources);
    }
    public static String analyse(Set<Word> words, Dictionary dictionary, int column, boolean searchOriginalText, String book, String... resources) {
        List<String> text = new ArrayList<>();
        for(String resource : resources) {
            text.addAll(FileUtils.readResource(resource));
        }
        return analyse(words, dictionary, column, searchOriginalText, book, text);
    }
    public static String analyse(Set<Word> words, Dictionary dictionary, int column, boolean searchOriginalText, String book, List<String> text) {
        Set<String> wordSet = new HashSet<>();
        words.forEach(word -> wordSet.add(word.getWord().toLowerCase()));
        StringBuilder result = new StringBuilder();
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

        text.forEach(line -> {
            StringBuilder buffer = new StringBuilder();
            line = line.replaceAll("[^a-zA-Z0-9]*[a-zA-Z0-9]+'[a-zA-Z0-9]+[^a-zA-Z0-9]*", " ")
                       .replaceAll("[^a-zA-Z0-9]*[a-zA-Z0-9]+`[a-zA-Z0-9]+[^a-zA-Z0-9]*", " ");
            for (org.apdplat.word.segmentation.Word term : WordSegmenter.segWithStopWords(line, SegmentationAlgorithm.PureEnglish)) {
                String word = term.getText();

                if (word.contains("'")) {
                    continue;
                }
                buffer.setLength(0);
                for (char c : word.toCharArray()) {
                    if (Character.isAlphabetic(c)) {
                        buffer.append(Character.toLowerCase(c));
                    }
                }
                String baseForm = IrregularVerbs.getBaseForm(buffer.toString());
                buffer.setLength(0);
                buffer.append(baseForm);
                String singular = IrregularPlurals.getSingular(buffer.toString());
                buffer.setLength(0);
                buffer.append(singular);
                if (buffer.length() < 2 || buffer.length() > 14) {
                    continue;
                }
                if (STOP_WORDS.contains(new Word(buffer.toString().toLowerCase(), ""))) {
                    continue;
                }
                map.putIfAbsent(buffer.toString(), new AtomicInteger());
                map.get(buffer.toString()).incrementAndGet();
            }
        });

        List<String> list = new ArrayList<>();

        map.entrySet().stream().sorted((a, b) -> b.getValue().get() - a.getValue().get()).forEach(entry -> {
            String w = entry.getKey().toLowerCase();
            if(w.length() < 3){
                return;
            }
            if (wordSet.contains(w)) {
                return;
            }
            if (w.endsWith("ly") && wordSet.contains(w.substring(0, w.length() - 2))) {
                return;
            }
            if (w.endsWith("s") && wordSet.contains(w.substring(0, w.length() - 1))) {
                return;
            }
            if (w.endsWith("es") && wordSet.contains(w.substring(0, w.length() - 2))) {
                return;
            }
            if (w.endsWith("ies") && wordSet.contains(w.substring(0, w.length() - 3)+"y")) {
                return;
            }
            if (w.endsWith("ed") && wordSet.contains(w.substring(0, w.length() - 1))) {
                return;
            }
            if (w.endsWith("ed") && wordSet.contains(w.substring(0, w.length() - 2))) {
                return;
            }
            if (w.endsWith("ed") && w.length()>5 && wordSet.contains(w.substring(0, w.length() - 3)) && (w.charAt(w.length()-3)==w.charAt(w.length()-4))) {
                return;
            }
            if (w.endsWith("ied") && wordSet.contains(w.substring(0, w.length() - 3)+"y")) {
                return;
            }
            if (w.endsWith("ing") && wordSet.contains(w.substring(0, w.length() - 3))) {
                return;
            }
            if (w.endsWith("ing") && wordSet.contains(w.substring(0, w.length() - 3)+"e")) {
                return;
            }
            if (w.endsWith("ing") && w.length()>6 && wordSet.contains(w.substring(0, w.length() - 4)) && (w.charAt(w.length()-4)==w.charAt(w.length()-5))) {
                return;
            }
            if (w.endsWith("er") && wordSet.contains(w.substring(0, w.length() - 1))) {
                return;
            }
            if (w.endsWith("er") && wordSet.contains(w.substring(0, w.length() - 2))) {
                return;
            }
            if (w.endsWith("er") && w.length()>5 && wordSet.contains(w.substring(0, w.length() - 3)) && (w.charAt(w.length()-3)==w.charAt(w.length()-4))) {
                return;
            }
            if (w.endsWith("est") && wordSet.contains(w.substring(0, w.length() - 2))) {
                return;
            }
            if (w.endsWith("est") && wordSet.contains(w.substring(0, w.length() - 3))) {
                return;
            }
            if (w.endsWith("est") && w.length()>6 && wordSet.contains(w.substring(0, w.length() - 4)) && (w.charAt(w.length()-4)==w.charAt(w.length()-5))) {
                return;
            }
            if (w.endsWith("ier") && wordSet.contains(w.substring(0, w.length() - 3)+"y")) {
                return;
            }
            if (w.endsWith("iest") && wordSet.contains(w.substring(0, w.length() - 4)+"y")) {
                return;
            }
            if (w.endsWith("ves") && wordSet.contains(w.substring(0, w.length() - 3)+"f")) {
                return;
            }
            String originalText = "";
            if(searchOriginalText){
                originalText = "\t<a target=\"_blank\" href=\"aid-reading-detail.jsp?book="+book+"&word="+entry.getKey()+"&dict=ICIBA&pageSize="+entry.getValue()+"\">[" + entry.getValue() + "]</a>";
            }else{
                originalText = "\t[" + entry.getValue() + "]";
            }
            list.add(WordLinker.toLink(entry.getKey(), dictionary) + originalText);
        });
        result.append("<h3>words don't occur in specified set: ("+list.size()+") </h3>\n");
        result.append(HtmlFormatter.toHtmlTableFragment(list, column));

        list.clear();

        map.entrySet().stream().sorted((a, b) -> b.getValue().get() - a.getValue().get()).forEach(entry -> {
            String originalText = "";
            if(searchOriginalText){
                originalText = "\t<a target=\"_blank\" href=\"aid-reading-detail.jsp?book="+book+"&word="+entry.getKey()+"&dict=ICIBA&pageSize="+entry.getValue()+"\">[" + entry.getValue() + "]</a>";
            }else{
                originalText = "\t[" + entry.getValue() + "]";
            }
            list.add(WordLinker.toLink(entry.getKey(), dictionary) + originalText);
        });
        result.append("<h3>words: (" + list.size() + ") </h3>\n");
        result.append(HtmlFormatter.toHtmlTableFragment(list, column));
        return result.toString();
    }
}
