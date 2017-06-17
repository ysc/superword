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

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by ysc on 12/5/15.
 */
public class Pronunciation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pronunciation.class);

    public static final String ICIBA_CSS_PATH = "div.base-speak span";
    public static final String YOUDAO_CSS_PATH = "span.pronounce";
    public static final String OXFORD_CSS_PATH = "header.entryHeader div.headpron";
    public static final String WEBSTER_CSS_PATH = "div.word-attributes span.pr";
    public static final String COLLINS_CSS_PATH = "";
    public static final String CAMBRIDGE_CSS_PATH = "";
    public static final String MACMILLAN_CSS_PATH = "";
    public static final String HERITAGE_CSS_PATH = "";
    public static final String WIKTIONARY_CSS_PATH = "";
    public static final String WORDNET_CSS_PATH = "";
    public static final String RANDOMHOUSE_CSS_PATH = "";
    
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    public static String getPronunciationString(Dictionary dictionary, String word, String joinString) {
        return concat(getPronunciation(dictionary, word), joinString);
    }

    public static String concat(List<String> list, String joinString){
        if(list.isEmpty()){
            return "";
        }
        StringBuilder string = new StringBuilder();
        list.forEach(d -> string.append(d).append(joinString));
        int len = string.length()-joinString.length();
        if(len < 1){
            return "";
        }
        string.setLength(len);
        return string.toString();
    }

    public static List<String> getPronunciation(Dictionary dictionary, String word){
        switch (dictionary){
            case ICIBA: return getPronunciationForICIBA(word);
            case YOUDAO: return getPronunciationForYOUDAO(word);
            case COLLINS: return getPronunciationForCOLLINS(word);
            case WEBSTER: return getPronunciationForWEBSTER(word);
            case OXFORD: return getPronunciationForOXFORD(word);
            case CAMBRIDGE: return getPronunciationForCAMBRIDGE(word);
            case MACMILLAN: return getPronunciationForMACMILLAN(word);
            case HERITAGE: return getPronunciationForHERITAGE(word);
            case WIKTIONARY: return getPronunciationForWIKTIONARY(word);
            case WORDNET: return getPronunciationForWORDNET(word);
            case RANDOMHOUSE: return getPronunciationForRANDOMHOUSE(word);
        }
        return getPronunciationForICIBA(word);
    }

    public static List<String> getPronunciationForICIBA(String word){
        return parsePronunciation(WordLinker.ICIBA + word, ICIBA_CSS_PATH, word, Dictionary.ICIBA);
    }
    public static List<String> getPronunciationForYOUDAO(String word){
        return parsePronunciation(WordLinker.YOUDAO + word, YOUDAO_CSS_PATH, word, Dictionary.YOUDAO);
    }
    public static List<String> getPronunciationForCOLLINS(String word){
        return parsePronunciation(WordLinker.COLLINS + word, COLLINS_CSS_PATH, word, Dictionary.COLLINS);
    }
    public static List<String> getPronunciationForWEBSTER(String word){
        return parsePronunciation(WordLinker.WEBSTER + word, WEBSTER_CSS_PATH, word, Dictionary.WEBSTER);
    }
    public static List<String> getPronunciationForOXFORD(String word){
        return parsePronunciation(WordLinker.OXFORD + word, OXFORD_CSS_PATH, word, Dictionary.OXFORD);
    }
    public static List<String> getPronunciationForCAMBRIDGE(String word){
        return parsePronunciation(WordLinker.CAMBRIDGE + word, CAMBRIDGE_CSS_PATH, word, Dictionary.CAMBRIDGE);
    }
    public static List<String> getPronunciationForMACMILLAN(String word){
        return parsePronunciation(WordLinker.MACMILLAN + word, MACMILLAN_CSS_PATH, word, Dictionary.MACMILLAN);
    }
    public static List<String> getPronunciationForHERITAGE(String word){
        return parsePronunciation(WordLinker.HERITAGE + word, HERITAGE_CSS_PATH, word, Dictionary.HERITAGE);
    }
    public static List<String> getPronunciationForWIKTIONARY(String word){
        return parsePronunciation(WordLinker.WIKTIONARY + word, WIKTIONARY_CSS_PATH, word, Dictionary.WIKTIONARY);
    }
    public static List<String> getPronunciationForWORDNET(String word){
        return parsePronunciation(WordLinker.WORDNET + word, WORDNET_CSS_PATH, word, Dictionary.WORDNET);
    }
    public static List<String> getPronunciationForRANDOMHOUSE(String word){
        return parsePronunciation(WordLinker.RANDOMHOUSE + word, RANDOMHOUSE_CSS_PATH, word, Dictionary.RANDOMHOUSE);
    }

    public static List<String> parsePronunciation(String url, String cssPath, String word, Dictionary dictionary){
        String wordPronunciation = MySQLUtils.getWordPronunciation(word, dictionary.name());
        if(StringUtils.isNotBlank(wordPronunciation)) {
            return Arrays.asList(wordPronunciation.split(" \\| "));
        }
        String html = getContent(url);
        List<String> list = parsePronunciationFromHtml(html, cssPath, word, dictionary);
        if(!list.isEmpty()){
            MySQLUtils.saveWordPronunciation(word, dictionary.name(), concat(list, " | "));
        }
        return list;
    }

    public static List<String> parsePronunciationFromHtml(String html, String cssPath, String word, Dictionary dictionary){
        List<String> list = new ArrayList<>();
        try {
            for (Element element : Jsoup.parse(html).select(cssPath)) {
                String pronunciation = element.text();
                if (StringUtils.isNotBlank(pronunciation)) {
                    pronunciation = pronunciation.replace("Pronunciation:", "");
                    pronunciation = pronunciation.trim();
                    if(!list.contains(pronunciation)) {
                        list.add(pronunciation);
                    }
                }
            }
        } catch (Exception e){
            LOGGER.error("解析音标出错：" + word, e);
        }
        return list;
    }

    public static String getContent(String url) {
        long start = System.currentTimeMillis();
        String html = _getContent(url, 1000);
        LOGGER.info("获取拼音耗时: {}", TimeUtils.getTimeDes(System.currentTimeMillis()-start));
        int times = 0;
        while(StringUtils.isNotBlank(html) && html.contains("非常抱歉，来自您ip的请求异常频繁")){
            //使用新的IP地址
            ProxyIp.toNewIp();
            html = _getContent(url);
            if(++times > 2){
                break;
            }
        }
        return html;
    }

    private static String _getContent(String url, int timeout) {
        Future<String> future = ThreadPool.EXECUTOR_SERVICE.submit(()->_getContent(url));
        try {
            Thread.sleep(timeout);
            return future.get(1, TimeUnit.NANOSECONDS);
        } catch (Throwable e) {
            LOGGER.error("获取网页异常", e);
        }
        return "";
    }

    private static String _getContent(String url) {
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Referer", REFERER)
                .header("Host", HOST)
                .header("User-Agent", USER_AGENT)
                .timeout(1000)
                .ignoreContentType(true);
        String html = "";
        try {
            html = conn.post().html();
            html = html.replaceAll("[\n\r]", "");
        }catch (Exception e){
            LOGGER.error("获取URL：" + url + "页面出错", e);
        }
        return html;
    }

    public static void main(String[] args) {
        System.out.println(getPronunciationString(Dictionary.ICIBA, "resume", " | "));
        System.out.println(getPronunciationString(Dictionary.YOUDAO, "resume", " | "));
        System.out.println(getPronunciationString(Dictionary.OXFORD, "resume", " | "));
        System.out.println(getPronunciationString(Dictionary.WEBSTER, "resume", " | "));
    }
}
