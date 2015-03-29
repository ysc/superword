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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 常用词组习语提取工具
 * @author 杨尚川
 */
public class PhraseExtractor {

    private PhraseExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(PhraseExtractor.class);
    private static final String PHRASE_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_2.dict_content.word_group dl.def_list dd.dd_show h4.cx_mean_switch";

    public static Set<String> parse(String path){
        if(path.endsWith(".zip")){
            return parseZip(path);
        }
        if(Files.isDirectory(Paths.get(path))){
            return parseDir(path);
        }else{
            return parseFile(path);
        }
    }

    public static Set<String> parseDir(String dir) {
        Set<String> data = new HashSet<>();
        LOGGER.info("开始解析目录：" + dir);
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    data.addAll(parseFile(file.toFile().getAbsolutePath()));
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    public static Set<String> parseZip(String zipFile){
        Set<String> data = new HashSet<>();
        LOGGER.info("开始解析ZIP文件："+zipFile);
        try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFile), PhraseExtractor.class.getClassLoader())) {
            for(Path path : fs.getRootDirectories()){
                LOGGER.info("处理目录："+path);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>(){

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        LOGGER.info("处理文件："+file);
                        // 拷贝到本地文件系统
                        Path temp = Paths.get("target/origin-html-temp.txt");
                        Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                        data.addAll(parseFile(temp.toFile().getAbsolutePath()));
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }catch (Exception e){
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    public static Set<String> parseFile(String file){
        Set<String> data = new HashSet<>();
        LOGGER.info("开始解析文件："+file);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                new FileInputStream(file))))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                //LOGGER.debug("html:"+line);
                String[] attr = line.split("杨尚川");
                if(attr == null || attr.length != 2){
                    LOGGER.error("解析文本失败，文本应该以'杨尚川'分割，前面是词，后面是网页，网页内容是去除换行符之后的一整行文本："+line);
                    continue;
                }
                String word = attr[0];
                String html = attr[1];
                Set<String> set = parsePhrase(html, word);
                data.addAll(set);
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    /**
     * 解析常用词组习语
     * @param html
     * @return
     */
    public static Set<String> parsePhrase(String html, String word){
        Set<String> phrases = new HashSet<>();
        LOGGER.info("解析单词："+word);
        if(Character.isUpperCase(word.charAt(0))){
            LOGGER.info("忽略首字母大写的单词");
            return phrases;
        }
        try {
            o:for(Element element : Jsoup.parse(html).select(PHRASE_CSS_PATH)){
                String phrase = element.text().trim();
                if(StringUtils.isNotBlank(phrase)){
                    if(phrase.length() >= 50){
                        LOGGER.debug("忽略太长的短语:" + phrase);
                        break o;
                    }
                    String[] attrs = phrase.split("\\s+");
                    if(attrs == null || attrs.length < 2){
                        LOGGER.debug("忽略太短的短语:" + phrase);
                        break o;
                    }
                    for(String attr : attrs){
                        for(char c : attr.toCharArray()){
                            if(!(c>='a'&&c<='z' || c>='A'&&c<='Z')) {
                                LOGGER.debug("忽略短语:" + phrase);
                                break o;
                            }
                        }
                    }
                    phrases.add(phrase);
                    LOGGER.debug("解析出短语:" + phrase);
                }
            }
        }catch (Exception e){
            LOGGER.error("解析短语出错", e);
        }
        return phrases;
    }

    private static void parsePhrase(){
        Set<String> parses = parse("/Users/apple/百度云同步盘/origin_html.zip");
        List<String> ps = parses
                .stream()
                .sorted()
                .map(p -> WordLinker.toLink(p))
                .collect(Collectors.toList());
        String html = HtmlFormatter.toHtmlTableFragment(ps, 2);
        try{
            Files.write(Paths.get("src/main/resources/phrases.txt"), html.getBytes("utf-8"));
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static Set<String> parsePhrase(String String){
        try {
            return parsePhrase(Jsoup.parse(new URL("http://www.iciba.com/" + String), 15000).html(), String);
        }catch (Exception e){
            LOGGER.error("解析定义出错", e);
        }
        return null;
    }

    public static void main(String[] args){
        //parsePhrase("up");
        //parsePhrase("like");
        //parsePhrase("nothing");
        parsePhrase();
    }
}
