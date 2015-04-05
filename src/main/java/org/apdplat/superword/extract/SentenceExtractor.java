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

package org.apdplat.superword.extract;

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.DynamicIp;
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.TextAnalyzer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 每日一句提取工具
 * @author 杨尚川
 */
public class SentenceExtractor {

    private SentenceExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(SentenceExtractor.class);
    private static final String SENTENCE_CSS_PATH = "html body div#content.clear div.main.fl div.reading div.r_bd.clear div.reading_rg.fr.pr div.reading_txt ul";
    private static final String EN_CSS_PATH = "li.en a";
    private static final String CN_CSS_PATH = "li.cn a";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";
    private static final Map<Word, AtomicInteger> WORD_FREQUENCE = new HashMap<>();

    public static Map<String, String> extract(int totalPageNumber){
        Map<String, String> sentences = new HashMap<>();
        for (int i=1; i<=totalPageNumber; i++){
            String url = "http://news.iciba.com/dailysentence/detail-"+i+".html";
            String html = getContent(url);
            int times = 1;
            while (StringUtils.isBlank(html) && times<4){
                times++;
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(url);
            }
            //LOGGER.debug("获取到的HTML：" +html);
            while(html.contains("非常抱歉，来自您ip的请求异常频繁")){
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(url+i);
            }
            sentences.putAll(parse(html));
        }
        LOGGER.debug("期望获取句子数：" + totalPageNumber);
        LOGGER.debug("实际获取句子数：" + sentences.size());
        return sentences;
    }
    public static Map<String, String> parse(String html){
        Map<String, String> sentences = new HashMap<>();
        try {
            for(Element element : Jsoup.parse(html).select(SENTENCE_CSS_PATH)){
                String en = null;
                String cn = null;
                Elements elements = element.select(EN_CSS_PATH);
                if(elements.size()==1){
                    en = elements.get(0).text().trim();
                    LOGGER.debug("解析出句子英文:" + en);
                    if(en.split("\\s+").length<2){
                        LOGGER.debug("不是句子，放弃");
                        continue;
                    }
                }
                elements = element.select(CN_CSS_PATH);
                if(elements.size()==1){
                    cn = elements.get(0).text().trim();
                    LOGGER.debug("解析出句子中文:" + cn);
                }
                if(StringUtils.isNotBlank(en)
                        && StringUtils.isNotBlank(cn)){
                    sentences.put(en, cn);
                    //统计词频
                    TextAnalyzer.seg(en).forEach(w -> {
                        Word word = new Word(w, "");
                        WORD_FREQUENCE.putIfAbsent(word, new AtomicInteger());
                        WORD_FREQUENCE.get(word).incrementAndGet();
                    });
                }
            }
        }catch (Exception e){
            LOGGER.error("解析句子出错", e);
        }
        return sentences;
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
        Map<String, String> data = extract(1247);
        String html = HtmlFormatter.toHtmlForSentence(data, WORD_FREQUENCE);
        try {
            Files.write(Paths.get("src/main/resources/sentences.txt"), html.toString().getBytes("utf-8"));
        }catch (Exception e){
            LOGGER.error("保存文件失败", e);
        }
    }
}
