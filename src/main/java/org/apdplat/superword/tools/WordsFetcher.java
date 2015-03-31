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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 更新词库工具
 * @author 杨尚川
 */
public class WordsFetcher {
    private WordsFetcher(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(WordsFetcher.class);

    private static final String WORD_CSS_PATH = "html body div#main_block div.word_box form#word_form div.word_main ul li div.word_main_list_w span";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    public static void updateCET4(){
        update(11, 226, "/word_CET4.txt");
    }
    public static void updateCET6(){
        update(12, 105, "/word_CET6.txt");
    }
    public static void updateKY(){
        update(13, 274, "/word_考 研.txt");
    }
    public static void updateTOEFL(){
        update(14, 245, "/word_TOEFL.txt");
    }
    public static void updateIELTS(){
        update(15, 228, "/word_IELTS.txt");
    }
    public static void updateGRE(){
        update(16, 375, "/word_GRE.txt");
    }
    public static void update(int type, int pageNumber, String file){
        Set<Word> IELTSWords = fetch(type, pageNumber);
        IELTSWords.addAll(WordSources.get(file));
        AtomicInteger i = new AtomicInteger();
        List<String> words = IELTSWords
                .stream()
                .sorted()
                .map(w -> i.incrementAndGet() + "\t" + w.getWord())
                .collect(Collectors.toList());
        try{
            Files.write(Paths.get("src/main/resources"+file), words);
        }catch (Exception e){
            LOGGER.error("保存词汇失败", e);
        }
    }
    public static Set<Word> fetch(int type, int pageNumber){
        Set<Word> words = new HashSet<>();
        //雅思词汇
        String url = "http://word.iciba.com/?action=words&class="+type+"&course=";
        for (int i=1; i<=pageNumber; i++){
            String html = getContent(url+i);
            //LOGGER.debug("获取到的HTML：" +html);
            while(StringUtils.isBlank(html)
                    ||html.contains("非常抱歉，来自您ip的请求异常频繁")){
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(url+i);
            }
            words.addAll(parse(html));
        }
        return words;
    }
    public static Set<Word> parse(String html){
        Set<Word> words = new HashSet<>();
        try {
            for(Element element : Jsoup.parse(html).select(WORD_CSS_PATH)){
                String word = element.text().trim();
                if(StringUtils.isNotBlank(word)
                        && StringUtils.isAlpha(word)){
                    words.add(new Word(word, ""));
                    LOGGER.debug("解析出单词:" + word);
                }
            }
        }catch (Exception e){
            LOGGER.error("解析单词出错", e);
        }
        return words;
    }
    public static String getContent(String url) {
        LOGGER.debug("url:"+url);
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Referer", REFERER)
                .header("Host", HOST)
                .header("User-Agent", USER_AGENT)
                .ignoreContentType(true);
        String html = "";
        try {
            html = conn.post().html();
            html = html.replaceAll("[\n\r]", "");
        }catch (Exception e){
            LOGGER.error("获取URL："+url+"页面出错", e);
        }
        return html;
    }

    public static void main(String[] args) {
        updateCET4();
        updateCET6();
        updateKY();
        updateTOEFL();
        updateIELTS();
        updateGRE();
    }

}
