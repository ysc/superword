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
import org.apdplat.superword.model.SynonymAntonym;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.ProxyIp;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 汉语同义词反义词提取工具
 * @author 杨尚川
 */
public class ChineseSynonymAntonymExtractor {

    private ChineseSynonymAntonymExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(ChineseSynonymAntonymExtractor.class);
    private static final String SYNONYM_ANTONYM_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_3.dict_content div.industry_box div.industry.cn_synon_box";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final List<String> USER_AGENTS = Arrays.asList("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.102 Safari/537.36 OPR"
            );
    private static final AtomicInteger uac = new AtomicInteger();
    private static final Map<String, String> ANTONYM = new ConcurrentHashMap<>();
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Set<String> CHECKED_WORDS = new ConcurrentHashSet<>();
    //用来合并不同条目
    private static final Map<Word, Set<Word>> SYNONYM_MAP = new ConcurrentHashMap<>();
    private static final Path CHECKED_WORDS_PATH = Paths.get("src/main/resources/checked_words.txt");

    public static SynonymAntonym parseSynonymAntonym(String html, String word){
        SynonymAntonym synonymAntonym = new SynonymAntonym();
        synonymAntonym.setWord(new Word(word, ""));
        try {
            for(Element element : Jsoup.parse(html).select(SYNONYM_ANTONYM_CSS_PATH)){
                int size = element.children().size();
                LOGGER.debug("element size:" + size);
                for(int i=0;i<size/2;i++) {
                    String type = element.child(i*2).text();
                    LOGGER.debug("type:"+type);
                    if ("同义词".equals(type)) {
                        String synonym = element.child(i*2+1).text();
                        LOGGER.debug("synonym:"+synonym);
                        for(String w : synonym.split("\\s+")){
                            w=w.replaceAll("\\s+", "");
                            if(w.length()<2){
                                continue;
                            }
                            if(isNotChineseChar(w)){
                                LOGGER.debug("非中文字符："+w);
                                continue;
                            }
                            if(w.equals(word)){
                                continue;
                            }
                            LOGGER.debug("word:"+w);
                            synonymAntonym.addSynonym(new Word(w, ""));
                        }
                    }
                    if ("反义词".equals(type)) {
                        String antonym = element.child(i*2+1).text();
                        LOGGER.debug("antonym:"+antonym);
                        for(String w : antonym.split("\\s+")){
                            w=w.replaceAll("\\s+", "");
                            if(w.length()<2){
                                continue;
                            }
                            if(isNotChineseChar(w)){
                                LOGGER.debug("非中文字符："+w);
                                continue;
                            }
                            LOGGER.debug("word:"+w);
                            synonymAntonym.addAntonym(new Word(w, ""));
                        }
                    }
                }
            }
            if(!synonymAntonym.getAntonym().isEmpty() || !synonymAntonym.getSynonym().isEmpty()) {
                LOGGER.info("解析出同义词反义词：" + synonymAntonym);
            }
        }catch (Exception e){
            LOGGER.error("解析同义词反义词出错", e);
        }
        return synonymAntonym;
    }

    public static void parseSynonymAntonym(List<String> words){
        LOGGER.info("开始解析，词数：" + words.size());
        Set<String> SKIP_WORDS = new ConcurrentSkipListSet<>();
        try{
            if(Files.notExists(CHECKED_WORDS_PATH)){
                CHECKED_WORDS_PATH.toFile().createNewFile();
            }
            SKIP_WORDS.addAll(Files.readAllLines(CHECKED_WORDS_PATH));
        }catch (Exception e){
            LOGGER.error("读取文件失败", e);
        }
        int total = words.size()-SKIP_WORDS.size();
        LOGGER.info("之前已经解析的词数：" + SKIP_WORDS.size());
        LOGGER.info("现在还需解析的词数：" + total);
        String url = "http://www.iciba.com/";
        AtomicInteger i = new AtomicInteger();
        EXECUTOR_SERVICE.submit(()->{
            while(true){
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                save();
            }
        });
        words.parallelStream().forEach(word -> {
            if (SKIP_WORDS.contains(word)) {
                return;
            }
            LOGGER.info("进度：" + total + "/" + i.incrementAndGet() + " 来自线程：" + Thread.currentThread());
            try {
                word = word.trim();
                if ("".equals(word) || isNotChineseChar(word) || word.length() < 2) {
                    return;
                }
                String html = getContent(url + word);
                int times = 1;
                while (StringUtils.isBlank(html) && times < 3) {
                    times++;
                    //使用新的IP地址
                    ProxyIp.toNewIp();
                    html = getContent(url + word);
                }
                if (StringUtils.isBlank(html)) {
                    LOGGER.error("获取页面失败：" + url + word);
                    return;
                }
                times = 1;
                //LOGGER.debug("获取到的HTML：" +html);
                while (html.contains("非常抱歉，来自您ip的请求异常频繁") && times < 3) {
                    times++;
                    //使用新的IP地址
                    ProxyIp.toNewIp();
                    html = getContent(url + word);
                }
                SynonymAntonym synonymAntonym = parseSynonymAntonym(html, word);
                if (!synonymAntonym.getSynonym().isEmpty()) {
                    SYNONYM_MAP.put(synonymAntonym.getWord(), synonymAntonym.getSynonym());
                }
                if (!synonymAntonym.getAntonym().isEmpty()) {
                    StringBuilder str = new StringBuilder();
                    synonymAntonym.getAntonym().forEach(w -> str.append(w.getWord()).append(" "));
                    ANTONYM.put(word, str.toString().trim());
                }
                CHECKED_WORDS.add(word);
            } catch (Exception e) {
                LOGGER.error("错误：", e);
            }
        });
        save();
    }
    private static synchronized void save(){
        System.out.println("开始保存文件");
        List<String> SYNONYM_LIST = null;
        List<String> ANTONYM_LIST = null;
        try {
            Path CHINESE_SYNONYM = Paths.get("src/main/resources/chinese_synonym.txt");
            if(Files.notExists(CHINESE_SYNONYM)){
                CHINESE_SYNONYM.toFile().createNewFile();
            }
            Path CHINESE_ANTONYM = Paths.get("src/main/resources/chinese_antonym.txt");
            if(Files.notExists(CHINESE_ANTONYM)){
                CHINESE_ANTONYM.toFile().createNewFile();
            }
            System.out.println("同义词数："+SYNONYM_MAP.size());
            Set<String> SYNONYM_STR = new HashSet<>();
            SYNONYM_MAP.keySet().forEach(k -> {
                StringBuilder str = new StringBuilder();
                str.append(k.getWord()).append(" ");
                SYNONYM_MAP.get(k).stream().sorted().forEach(w -> {
                    str.append(w.getWord()).append(" ");
                });
                SYNONYM_STR.add(str.toString().trim());
            });

            List<String> existList = Files.readAllLines(CHINESE_SYNONYM);
            SYNONYM_STR.addAll(existList);
            SYNONYM_LIST = SYNONYM_STR.stream().sorted().collect(Collectors.toList());
            System.out.println("总的同义词数："+SYNONYM_LIST.size());
            Files.write(CHINESE_SYNONYM, SYNONYM_LIST);

            Set<String> set = ANTONYM.keySet().stream().sorted().map(k -> k + " " + ANTONYM.get(k)).collect(Collectors.toSet());
            existList = Files.readAllLines(CHINESE_ANTONYM);
            set.addAll(existList);
            ANTONYM_LIST = set.stream().sorted().collect(Collectors.toList());
            System.out.println("总的反义词数："+ANTONYM_LIST.size());
            Files.write(CHINESE_ANTONYM, ANTONYM_LIST);

            existList = Files.readAllLines(CHECKED_WORDS_PATH);
            CHECKED_WORDS.addAll(existList);
            System.out.println("总的已检查词数：" + CHECKED_WORDS.size());
            Files.write(CHECKED_WORDS_PATH, CHECKED_WORDS);
        } catch (Exception e) {
            LOGGER.error("同义词：",SYNONYM_LIST.toString());
            LOGGER.error("反义词：",ANTONYM_LIST.toString());
            LOGGER.error("保存文件失败", e);
        }
    }
    public static String getContent(String url) {
        LOGGER.debug("url:" + url);
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Referer", REFERER)
                .header("Host", HOST)
                .header("User-Agent", USER_AGENTS.get(uac.incrementAndGet() % USER_AGENTS.size()))
                .header("X-Forwarded-For", getRandomIp())
                .header("Proxy-Client-IP", getRandomIp())
                .header("WL-Proxy-Client-IP", getRandomIp())
                .ignoreContentType(true);
        String html = "";
        try {
            html = conn.post().html();
        }catch (Exception e){
            if(e instanceof  HttpStatusException) {
                HttpStatusException ex = (HttpStatusException) e;
                LOGGER.error("error code:"+ex.getStatusCode());
                if(ex.getStatusCode()==404){
                    return "404";
                }
            }
            LOGGER.error("获取URL："+url+" 页面出错", e);
        }
        return html;
    }
    public static boolean isNotChineseChar(String str){
        boolean temp = false;
        Pattern p= Pattern.compile("[^\u4e00-\u9fa5]");
        Matcher m=p.matcher(str);
        if(m.find()){
            temp =  true;
        }
        return temp;
    }
    public static SynonymAntonym parseSynonymAntonym(String word){
        try {
            return parseSynonymAntonym(Jsoup.parse(new URL("http://www.iciba.com/" + word), 15000).html(), word);
        }catch (Exception e){
            LOGGER.error("解析同义词反义词出错", e);
        }
        return null;
    }
    public static String getRandomIp(){
        int first = new Random().nextInt(254)+1;
        //排除A类私有地址0.0.0.0--10.255.255.255
        while(first==10){
            first = new Random().nextInt(254)+1;
        }
        int second = new Random().nextInt(254)+1;
        //排除B类私有地址172.16.0.0--172.31.255.255
        while(first==172 && (second>=16 && second<=31)){
            first = new Random().nextInt(254)+1;
            second = new Random().nextInt(254)+1;
        }
        //排除C类私有地址192.168.0.0--192.168.255.255
        while(first==192 && second==168){
            first = new Random().nextInt(254)+1;
            second = new Random().nextInt(254)+1;
        }
        int third = new Random().nextInt(254)+1;
        int forth = new Random().nextInt(254)+1;
        return first+"."+second+"."+second+"."+forth;
    }

    public static void main(String[] args) throws Exception{
        //parseSynonymAntonym("热爱");
        //parseSynonymAntonym("一举成名");
        //parseSynonymAntonym(Arrays.asList("热爱", "一举成名"));
        //System.out.println(getContent("http://www.iciba.com/%E7%83%AD%E7%88%B1"));
        parseSynonymAntonym(Files.readAllLines(Paths.get("src/main/resources/dic.txt")).stream().sorted((a, b) -> new Integer(a.length()).compareTo(b.length())).collect(Collectors.toList()));
    }
}
