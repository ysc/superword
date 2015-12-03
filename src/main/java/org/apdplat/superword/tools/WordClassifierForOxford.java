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
import org.apdplat.superword.model.Word;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * 利用牛津词典提取单词的英文释义
 * 并提供包含33376个单词的牛津词典HTML页面origin_html_oxford.zip文件供下载
 * 下载地址http://pan.baidu.com/s/1pJmwr95
 * @author 杨尚川
 */
public class WordClassifierForOxford {
    private WordClassifierForOxford(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(WordClassifierForOxford.class);
    private static final String OXFORD = WordLinker.OXFORD;
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.oxforddictionaries.com";
    private static final String REFERER = "http://www.oxforddictionaries.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";
    private static final Set<String> NOT_FOUND_WORDS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<String> ORIGIN_HTML = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final AtomicInteger COUNT = new AtomicInteger();

    public static void download(Set<Word> words){
        LOGGER.debug("待处理词数目："+words.size());
        AtomicInteger i = new AtomicInteger();
        words.parallelStream().forEach(word -> {
            if (i.get() % 1000 == 999) {
                save();
            }
            showStatus(i.incrementAndGet(), words.size(), word.getWord());
            String html = getContent(word.getWord());
            //LOGGER.debug("获取到的HTML：" +html);
            int times = 0;
            while (StringUtils.isNotBlank(html) && html.contains("非常抱歉，来自您ip的请求异常频繁")) {
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(word.getWord());
                if (++times > 2) {
                    break;
                }
            }

            if (StringUtils.isNotBlank(html)) {
                html = word.getWord() + "杨尚川" + html;
                parseHtml(html);
                if (!NOT_FOUND_WORDS.contains(word.getWord())) {
                    ORIGIN_HTML.add(html);
                }
            } else {
                NOT_FOUND_WORDS.add(word.getWord());
            }

        });
        //写入磁盘
        save();
        LOGGER.debug("处理完毕，总词数目："+words.size());
    }

    public static void parse(String path){
        if(path.endsWith(".zip")){
            parseZip(path);
        }else if(Files.isDirectory(Paths.get(path))){
            parseDir(path);
        }else{
            parseFile(path);
        }
    }

    public static void parseDir(String dir) {
        LOGGER.info("开始解析目录：" + dir);
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    parseFile(file.toFile().getAbsolutePath());
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
    }

    public static void parseZip(String zipFile){
        LOGGER.info("开始解析ZIP文件："+zipFile);
        try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFile), WordClassifierForOxford.class.getClassLoader())) {
            for(Path path : fs.getRootDirectories()){
                LOGGER.info("处理目录："+path);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        LOGGER.info("处理文件："+file);
                        // 拷贝到本地文件系统
                        Path temp = Paths.get("target/origin-html-temp.txt");
                        Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                        parseFile(temp.toFile().getAbsolutePath());
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }catch (Exception e){
            LOGGER.error("解析文本出错", e);
        }
    }

    public static void parseFile(String file){
        LOGGER.info("开始解析文件：" + file);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                new FileInputStream(file))))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                parseHtml(line);
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
    }
    public static void parseHtml(String html){
        LOGGER.debug("html:"+html);
        String[] attr = html.split("杨尚川");
        if(attr == null || attr.length != 2){
            LOGGER.error("解析文本失败，文本应该以'杨尚川'分割，前面是词，后面是网页，网页内容是去除换行符之后的一整行文本："+html);
            return;
        }
        String word = attr[0];
        String htm = attr[1];
        parse(word, htm);
    }

    public static void showStatus(int current, int total, String word){
        LOGGER.debug("开始处理词 " + current + "/" + total + " ，完成进度 " + current / (float) total * 100 + "% ：" + word);
    }

    public static synchronized void save(){
        LOGGER.info("将数据写入磁盘，防止丢失");
        try {
            if(!NOT_FOUND_WORDS.isEmpty()) {
                String path = "src/main/resources/word_not_found.txt";
                LOGGER.info("保存词典文件：" + path);
                AtomicInteger i = new AtomicInteger();
                //NOT_FOUND_WORDS比较少，常驻内存
                List<String> list = NOT_FOUND_WORDS
                        .stream()
                        .sorted()
                        .map(word -> i.incrementAndGet() + "\t" + word)
                        .collect(Collectors.toList());
                Files.write(Paths.get(path), list);
                list.clear();
            }
            //保存原始HTML
            if(!ORIGIN_HTML.isEmpty()) {
                String path = "src/main/resources/origin_html_" + System.currentTimeMillis() + ".txt";
                LOGGER.info("保存词典文件：" + path);
                Files.write(Paths.get(path), ORIGIN_HTML);
                ORIGIN_HTML.clear();
            }
        }catch (Exception e){
            LOGGER.error("保存词典文件失败", e);
        }
    }

    public static String getContent(String word) {
        String url = OXFORD + word + "?renovate=" + (new Random(System.currentTimeMillis()).nextInt(899999)+100000);
        LOGGER.debug("url:"+url);
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Referer", REFERER)
                .header("Host", HOST)
                .header("User-Agent", USER_AGENT)
                .timeout(60000)
                .ignoreContentType(true);
        String html = "";
        try {
            html = conn.post().html();
            html = html.replaceAll("[\n\r]", "");
        }catch (Exception e){
            //LOGGER.error("获取URL："+url+"页面出错", e);
            LOGGER.error("获取URL："+url+"页面出错");
        }
        return html;
    }

    public static void parse(String word, String html){
        String wordDefinition = MySQLUtils.getWordDefinition(word, WordLinker.Dictionary.OXFORD.name());
        if(StringUtils.isNotBlank(wordDefinition)){
            return ;
        }
        List<String> list = Definition.parseDefinitionFromHtml(html, null, word, WordLinker.Dictionary.OXFORD);
        if(!list.isEmpty()) {
            LOGGER.info(COUNT.incrementAndGet() + "、成功解析单词：" + word);
            list.stream().forEach(System.out::println);
            MySQLUtils.saveWordDefinition(word, WordLinker.Dictionary.OXFORD.name(), Definition.concat(list, "<br/>"));
        }else{
            NOT_FOUND_WORDS.add(word);
        }
    }

    public static void main(String[] args) {
        //Set<Word> words = new HashSet<>();
        //words.add(new Word("time", ""));
        //words.add(new Word("yangshangchuan", ""));
        //download(words);
        //download(WordSources.getAll());
        //parse("src/main/resources/origin_html_1449054801557.txt");
        //origin_html_oxford.zip包含33376个单词的牛津词典解析HTML页面，下载地址http://pan.baidu.com/s/1pJmwr95
        parse("/Users/apple/百度云同步盘/origin_html_oxford.zip");
    }
}
