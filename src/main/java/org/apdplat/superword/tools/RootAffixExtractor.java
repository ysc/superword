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

import org.apdplat.superword.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 解析词缀词根
 * @author 杨尚川
 */
public class RootAffixExtractor {
    private RootAffixExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(RootAffixExtractor.class);
    private static final String ROOT_AFFIX_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_6.dict_content.vCigen div.industry_box div.industry h4";
    private static final String MEANING_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_6.dict_content.vCigen div.industry_box div.industry div.vCigen_h4";

    public static Set<Word> parse(String path){
        if(path.endsWith(".zip")){
            return parseZip(path);
        }
        if(Files.isDirectory(Paths.get(path))){
            return parseDir(path);
        }else{
            return parseFile(path);
        }
    }

    public static Set<Word> parseDir(String dir) {
        Set<Word> roots = new HashSet<>();
        LOGGER.info("开始解析目录：" + dir);
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Set<Word> rs = parseFile(file.toFile().getAbsolutePath());
                    roots.addAll(rs);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return roots;
    }

    public static Set<Word> parseZip(String zipFile){
        Set<Word> roots = new HashSet<>();
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
                        Set<Word> rs = parseFile(temp.toFile().getAbsolutePath());
                        roots.addAll(rs);
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }catch (Exception e){
            LOGGER.error("解析文本出错", e);
        }
        return roots;
    }

    public static Set<Word> parseFile(String file){
        Set<Word> roots = new HashSet<>();
        LOGGER.info("开始解析文件："+file);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                new FileInputStream(file))))) {
            Map<String, List<String>> data = new HashMap<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                LOGGER.debug("html:"+line);
                String[] attr = line.split("杨尚川");
                if(attr == null || attr.length != 2){
                    LOGGER.error("解析文本失败，文本应该以'杨尚川'分割，前面是词，后面是网页，网页内容是去除换行符之后的一整行文本："+line);
                    continue;
                }
                String word = attr[0];
                LOGGER.info("解析单词："+word);
                String html = attr[1];
                roots.addAll(parseRootAffix(html));
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return roots;
    }

    /**
     * 解析词根词缀
     * 一个HTML可以包括多个词根词缀
     * @param html
     * @return
     */
    public static Set<Word> parseRootAffix(String html){
        Set<Word> data = new HashSet<>();
        try {
            List<String> rootAffixes = new ArrayList<>();
            List<String> meanings = new ArrayList<>();
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(ROOT_AFFIX_CSS_PATH);
            for(Element element : elements){
                String rootAffix = element.text().trim();
                rootAffixes.add(rootAffix);
            }
            elements = doc.select(MEANING_CSS_PATH);
            for(Element element : elements){
                String meaning = elements.get(0).text().replaceAll("[\n\r]","").trim();
                int index = meaning.indexOf("//");
                if(index != -1){
                    meaning = meaning.substring(0, index);
                }
                meanings.add(meaning);
            }
            if (!rootAffixes.isEmpty()
                    && !meanings.isEmpty()
                    && rootAffixes.size() == meanings.size()) {
                for(int i=0; i<rootAffixes.size(); i++) {
                    String rootAffix = rootAffixes.get(i);
                    String meaning = meanings.get(i);
                    LOGGER.info("解析出词根词缀：" + rootAffix + meaning);
                    data.add(new Word(rootAffix, meaning));
                }
            }
        }catch (Exception e){
            LOGGER.error("解析词根词缀出错", e);
        }
        return data;
    }
    private static void parseRootAffixes(){
        Set<Word> roots = parse("/Users/apple/百度云同步盘/origin_html.zip");
        List<String> rs = new ArrayList<>(roots.size());
        roots.stream().sorted().forEach(r -> rs.add(r.getWord()+"杨尚川"+r.getMeaning()));
        try{
            Files.write(Paths.get("src/main/resources/root_affix.txt"), rs);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        parseRootAffixes();
    }
}
