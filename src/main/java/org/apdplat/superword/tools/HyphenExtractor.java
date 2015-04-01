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

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 用连字符构造的合成词提取工具
 * @author 杨尚川
 */
public class HyphenExtractor {

    private HyphenExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(HyphenExtractor.class);
    private static final String ICIBA = "http://www.iciba.com/";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";
    private static final String COLLINS_DEFINITION_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.collins div#dict_tab_101.tab_content.tab_authorities div.part_main div.collins_content div.collins_en_cn div.caption";
    
    public static Map<String, AtomicInteger> parse(String path){
        if(path.endsWith(".zip")){
            return parseZip(path);
        }
        if(Files.isDirectory(Paths.get(path))){
            return parseDir(path);
        }else{
            return parseFile(path);
        }
    }

    public static Map<String, AtomicInteger> parseDir(String dir) {
        Map<String, AtomicInteger> data = new HashMap<>();
        LOGGER.info("开始解析目录：" + dir);
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Map<String, AtomicInteger> rs = parseFile(file.toFile().getAbsolutePath());
                    rs.keySet().forEach(k -> {
                        data.putIfAbsent(k, new AtomicInteger());
                        data.get(k).addAndGet(rs.get(k).get());
                    });
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    public static Map<String, AtomicInteger> parseZip(String zipFile){
        Map<String, AtomicInteger> data = new HashMap<>();
        LOGGER.info("开始解析ZIP文件："+zipFile);
        try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFile), WordClassifier.class.getClassLoader())) {
            for(Path path : fs.getRootDirectories()){
                LOGGER.info("处理目录："+path);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        LOGGER.info("处理文件："+file);
                        // 拷贝到本地文件系统
                        Path temp = Paths.get("target/origin-html-temp.txt");
                        Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                        Map<String, AtomicInteger> rs = parseFile(temp.toFile().getAbsolutePath());
                        rs.keySet().forEach(k -> {
                            data.putIfAbsent(k, new AtomicInteger());
                            data.get(k).addAndGet(rs.get(k).get());
                        });
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }catch (Exception e){
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    public static Map<String, AtomicInteger> parseFile(String file){
        Map<String, AtomicInteger> data = new HashMap<>();
        LOGGER.info("开始解析文件："+file);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                new FileInputStream(file))))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //LOGGER.debug("line:"+line);
                String[] attrs = line.split("\\s+");
                for(String attr : attrs){
                    if(attr.contains("-")){
                        String[] parts = attr.split("-");
                        if(parts.length==2
                                && parts[0].length()>1
                                && parts[1].length()>1
                                && WordSources.isEnglish(parts[0])
                                && WordSources.isEnglish(parts[1])){
                            LOGGER.debug("发现连字符："+attr);
                            attr = attr.toLowerCase();
                            data.putIfAbsent(attr, new AtomicInteger());
                            data.get(attr).incrementAndGet();
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    /**
     * 解析单词定义
     * @param html
     * @return
     */
    public static Word parseWord(String html, String word){
        LOGGER.info("解析单词："+word);
        Word w = new Word(word, "");
        try {
            for(Element element : Jsoup.parse(html).select(COLLINS_DEFINITION_CSS_PATH)){
                String definition = element.text().trim();
                if(StringUtils.isNotBlank(definition)
                        && definition.toLowerCase().contains(word.toLowerCase())){
                    w.addDefinition(definition);
                    LOGGER.debug("解析出定义:" + definition);
                }
            }
        }catch (Exception e){
            LOGGER.error("解析定义出错", e);
        }
        return w;
    }

    public static String getContent(String word) {
        String url = ICIBA + word + "?renovate=" + (new Random(System.currentTimeMillis()).nextInt(899999)+100000);
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

    public static boolean verify(String word){
        String html = getContent(word);
        int times = 1;
        while (StringUtils.isBlank(html) && times<4){
            times++;
            //使用新的IP地址
            DynamicIp.toNewIp();
            html = getContent(word);
        }
        //LOGGER.debug("获取到的HTML：" +html);
        while(html.contains("非常抱歉，来自您ip的请求异常频繁")){
            //使用新的IP地址
            DynamicIp.toNewIp();
            html = getContent(word);
        }
        if(StringUtils.isNotBlank(html)) {
            Word w = parseWord(html, word);
            if(!w.getDefinitions().isEmpty()){
                LOGGER.debug("词"+word+"验证通过");
                return true;
            }
        }
        LOGGER.debug("词"+word+"验证失败");
        return false;
    }

    public static void extract(String allPath, String wordPath, String htmlPath, boolean verify){
        Map<String, AtomicInteger> data = parse("/Users/apple/百度云同步盘/origin_html.zip");
        Map<String, AtomicInteger> wordsIT = parse("src/main/resources/it");
        wordsIT.keySet().forEach(k -> {
            data.putIfAbsent(k, new AtomicInteger());
            data.get(k).addAndGet(wordsIT.get(k).get());
        });
        Map<String, AtomicInteger> wordsJDK = parse("/Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home/src.zip");
        wordsJDK.keySet().forEach(k -> {
            data.putIfAbsent(k, new AtomicInteger());
            data.get(k).addAndGet(wordsJDK.get(k).get());
        });
        try {
            Files.write(Paths.get(allPath), data
                                .entrySet()
                                .stream()
                                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                                .map(e -> e.getValue() + "\t" + e.getKey())
                                .collect(Collectors.toList()));
            List<String> result =
                            data
                            .entrySet()
                            .stream()
                            .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                            .filter(e -> {
                                if (verify) {
                                    return verify(e.getKey());
                                }
                                return true;
                            })
                            .map(e -> e.getValue() + "\t" + e.getKey())
                            .collect(Collectors.toList());
            Files.write(Paths.get(wordPath), result);
            List<String> forHtmlResult =
                            result
                            .stream()
                            .map(s -> {
                                String[] attr = s.split("\t");
                                return WordLinker.toLink(attr[1]) + "(" + attr[0] + ")";
                            })
                            .collect(Collectors.toList());
            Files.write(Paths.get(htmlPath), HtmlFormatter.toHtmlTableFragment(forHtmlResult, 3).getBytes("utf-8"));
            LOGGER.info("完成");
        }catch (Exception e){
            LOGGER.error("保存文件出错", e);
        }
    }

    public static void main(String[] args) throws Exception{
        extract("src/main/resources/hyphen_word_all.txt", "src/main/resources/hyphen_word.txt", "src/main/resources/hyphen.txt", true);
    }
}
