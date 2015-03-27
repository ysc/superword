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
import org.apdplat.superword.model.SynonymAntonym;
import org.apdplat.superword.model.Word;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 同义词反义词提取工具
 * @author 杨尚川
 */
public class SynonymAntonymExtractor {

    private SynonymAntonymExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(SynonymAntonymExtractor.class);
    private static final String SYNONYM_ANTONYM_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.simple div#dict_content_3.dict_content.tongyi div.industry_box div.industry";
    private static final String TYPE = "h4";
    private static final String WORDS = "ul dl dd a";
    public static Set<SynonymAntonym> parse(String path){
        if(path.endsWith(".zip")){
            return parseZip(path);
        }
        if(Files.isDirectory(Paths.get(path))){
            return parseDir(path);
        }else{
            return parseFile(path);
        }
    }

    public static Set<SynonymAntonym> parseDir(String dir) {
        Set<SynonymAntonym> data = new HashSet<>();
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

    public static Set<SynonymAntonym> parseZip(String zipFile){
        Set<SynonymAntonym> data = new HashSet<>();
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

    public static Set<SynonymAntonym> parseFile(String file){
        Set<SynonymAntonym> data = new HashSet<>();
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
                SynonymAntonym sa = parseSynonymAntonym(html, word);
                if(sa.valid()) {
                    data.add(sa);
                }
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
    public static SynonymAntonym parseSynonymAntonym(String html, String word){
        SynonymAntonym synonymAntonym = new SynonymAntonym();
        synonymAntonym.setWord(new Word(word, ""));
        try {
            for(Element element : Jsoup.parse(html).select(SYNONYM_ANTONYM_CSS_PATH)){
                String type = element.select(TYPE).text().trim();
                LOGGER.debug("type:"+type);
                Elements elements = element.select(WORDS);
                for(Element ele : elements){
                    String w = ele.text().trim();
                    LOGGER.debug("word:"+w);
                    if(StringUtils.isNotBlank(w)){
                        switch (type){
                            case "同义词":synonymAntonym.addSynonym(new Word(w, ""));break;
                            case "反义词":synonymAntonym.addAntonym(new Word(w, ""));break;
                            default:LOGGER.error("同义词反义词解析遇到未知的类型："+type);
                        }
                    }else {
                        LOGGER.error("解析同义词反义词出错："+word);
                    }
                }
            }
            LOGGER.info("解析出同义词反义词：" + synonymAntonym);
        }catch (Exception e){
            LOGGER.error("解析同义词反义词出错", e);
        }
        return synonymAntonym;
    }
    private static Set<SynonymAntonym> inSyllabusVocabulary(Set<SynonymAntonym> synonymAntonyms){
        Set<Word> voc = WordSources.getSyllabusVocabulary();
        return synonymAntonyms.stream().filter(sa -> voc.contains(sa.getWord())).collect(Collectors.toSet());
    }
    private static Set<SynonymAntonym> notInSyllabusVocabulary(Set<SynonymAntonym> synonymAntonyms){
        Set<Word> voc = WordSources.getSyllabusVocabulary();
        return synonymAntonyms.stream().filter(sa -> !voc.contains(sa.getWord())).collect(Collectors.toSet());
    }
    private static void parseSynonymAntonym(){
        Set<SynonymAntonym> synonymAntonyms = parse("/Users/apple/百度云同步盘/origin_html.zip");
        String inSyllabusVocabularyHtml = HtmlFormatter.toHtmlForSynonymAntonym(inSyllabusVocabulary(synonymAntonyms), 5);
        String notInSyllabusVocabularyHtml = HtmlFormatter.toHtmlForSynonymAntonym(notInSyllabusVocabulary(synonymAntonyms), 5);
        try{
            Files.write(Paths.get("src/main/resources/synonym_antonym_in_syllabus_vocabulary.txt"), inSyllabusVocabularyHtml.getBytes("utf-8"));
            Files.write(Paths.get("src/main/resources/synonym_antonym_not_in_syllabus_vocabulary.txt"), notInSyllabusVocabularyHtml.getBytes("utf-8"));
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static SynonymAntonym parseSynonymAntonym(String word){
        try {
            return parseSynonymAntonym(Jsoup.parse(new URL("http://www.iciba.com/" + word), 15000).html(), word);
        }catch (Exception e){
            LOGGER.error("解析同义词反义词出错", e);
        }
        return null;
    }

    public static void main(String[] args){
        parseSynonymAntonym("back");
        //parseSynonymAntonym();
    }
}
