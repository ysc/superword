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

import org.apdplat.superword.model.SynonymDiscrimination;
import org.apdplat.superword.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 同义词辨析提取工具
 * @author 杨尚川
 */
public class SynonymDiscriminationExtractor {

    private SynonymDiscriminationExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(SynonymDiscriminationExtractor.class);
    private static final String SYNONYM_DISCRIMINATION_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_5.dict_content.more_data dl.def_list dd";
    private static final String TITLE = "h4";
    private static final String DES = "div";
    private static final String WORDS = "div ul li";
    public static Set<SynonymDiscrimination> parse(String path){
        if(path.endsWith(".zip")){
            return parseZip(path);
        }
        if(Files.isDirectory(Paths.get(path))){
            return parseDir(path);
        }else{
            return parseFile(path);
        }
    }

    public static Set<SynonymDiscrimination> parseDir(String dir) {
        Set<SynonymDiscrimination> data = new HashSet<>();
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

    public static Set<SynonymDiscrimination> parseZip(String zipFile){
        Set<SynonymDiscrimination> data = new HashSet<>();
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

    public static Set<SynonymDiscrimination> parseFile(String file){
        Set<SynonymDiscrimination> data = new HashSet<>();
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
                LOGGER.info("解析单词："+word);
                String html = attr[1];
                data.addAll(parseSynonymDiscrimination(html));
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    /**
     * 解析同义词辨析
     * @param html
     * @return
     */
    public static Set<SynonymDiscrimination> parseSynonymDiscrimination(String html){
        Set<SynonymDiscrimination> data = new HashSet<>();
        try {
            for(Element element : Jsoup.parse(html).select(SYNONYM_DISCRIMINATION_CSS_PATH)){
                String title = element.select(TITLE).text().trim();
                Elements elements = element.select(DES);
                if(elements.size() != 2){
                    LOGGER.error("解析描述信息出错，elements.size="+elements.size());
                    continue;
                }
                String des = elements.get(0).text().replace("“ ”", "").replace("“ ", "“").trim();
                SynonymDiscrimination synonymDiscrimination = new SynonymDiscrimination();
                synonymDiscrimination.setTitle(title);
                synonymDiscrimination.setDes(des);
                elements = element.select(WORDS);
                for(Element ele : elements){
                    String word = ele.text();
                    String[] attr = word.split("：");
                    if(attr != null && attr.length == 2){
                        synonymDiscrimination.addWord(new Word(attr[0].trim(), attr[1].trim()));
                    }else {
                        LOGGER.error("解析词义信息出错："+word);
                    }
                }
                data.add(synonymDiscrimination);
                LOGGER.info("解析出同义词辨析：" + synonymDiscrimination);
            }
        }catch (Exception e){
            LOGGER.error("解析同义词辨析出错", e);
        }
        return data;
    }
    private static void parseSynonymDiscrimination(){
        Set<SynonymDiscrimination> synonymDiscrimination = parse("/Users/apple/百度云同步盘/origin_html.zip");
        String html = HtmlFormatter.toHtmlForSynonymDiscrimination(synonymDiscrimination);
        try{
            Files.write(Paths.get("src/main/resources/synonym_discrimination.txt"), html.getBytes("utf-8"));
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        parseSynonymDiscrimination();
    }
}
