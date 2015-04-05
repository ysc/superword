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
import org.apdplat.superword.rule.PartOfSpeech;
import org.apdplat.superword.tools.HtmlFormatter;
import org.apdplat.superword.tools.WordClassifier;
import org.apdplat.superword.tools.WordSources;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 词性提取工具
 * @author 杨尚川
 */
public class PartOfSpeechExtractor {

    private PartOfSpeechExtractor(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeechExtractor.class);
    private static final String PART_OF_SPEECH_CSS_PATH = "html body.bg_main div#layout div#center div#main_box div#dict_main div.collins div#dict_tab_101.tab_content.tab_authorities div.part_main div.collins_content div.collins_en_cn div.caption span.st";

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
        Set<Word> data = new HashSet<>();
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

    public static Set<Word> parseZip(String zipFile){
        Set<Word> data = new HashSet<>();
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

    public static Set<Word> parseFile(String file){
        Set<Word> data = new HashSet<>();
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
                Word w = parseWord(html, word);
                if(w!=null && !w.getPartOfSpeeches().isEmpty()) {
                    data.add(w);
                }
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    /**
     * 解析词性
     * @param html
     * @return
     */
    public static Word parseWord(String html, String word){
        LOGGER.info("解析单词："+word);
        Word w = new Word(word, "");
        try {
            for(Element element : Jsoup.parse(html).select(PART_OF_SPEECH_CSS_PATH)){
                String partOfSpeech = element.text();
                LOGGER.debug("解析原始词性:" + partOfSpeech);
                if(StringUtils.isNotBlank(partOfSpeech) && !partOfSpeech.contains("See also")){
                    partOfSpeech = partOfSpeech.replace(";", "")
                            //处理组合词
                            .replace("COMB in ADJ and N-COUNT", "COMB-in-ADJ-and-N-COUNT")
                            .replace("COMB in ADJ and N", "COMB-in-ADJ-and-N")
                            .replace("COMB in ADJ", "COMB-in-ADJ")
                            .replace("COMB in ADJ-GRADED", "COMB-in-ADJ-GRADED")
                            .replace("COMB in N-COUNT", "COMB-in-N-COUNT")
                            .replace("COMB in COLOUR", "COMB-in-COLOUR")
                            .replace("COMB in N", "COMB-in-N")
                            .replace("COMB in N-UNCOUNT", "COMB-in-N-UNCOUNT")
                            .replace("COMB in QUANT", "COMB-in-QUANT")
                            .replace("COMB in VERB", "COMB-in-VERB");
                    String[] attrs = partOfSpeech.split("\\s+");
                    for(String attr : attrs){
                        if(attr.length()<1){
                            LOGGER.debug("忽略空词性:" + attr);
                            continue;
                        }
                        //短语不归入词性
                        if(attr.contains("PHR")){
                            LOGGER.debug("忽略短语:" + attr);
                            continue;
                        }
                        attr = attr.replace(",", "");
                        char c = attr.charAt(0);
                        if(c>='A' && c<='Z'){
                            if("VERB".equals(attr)){
                                attr = "V";
                            }
                            if("VERB-ERG".equals(attr)){
                                attr = "V-ERG";
                            }
                            w.addPartOfSpeech(attr);
                            LOGGER.debug("解析出词性:" + attr);
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("解析词性出错", e);
        }
        return w;
    }
    private static Set<Word> inSyllabusVocabulary(Set<Word> words){
        Set<Word> voc = WordSources.getSyllabusVocabulary();
        return words.stream().filter(w -> voc.contains(w)).collect(Collectors.toSet());
    }
    private static Set<Word> notInSyllabusVocabulary(Set<Word> words){
        Set<Word> voc = WordSources.getSyllabusVocabulary();
        return words.stream().filter(w -> !voc.contains(w)).collect(Collectors.toSet());
    }
    private static void parseWord(){
        Set<Word> words = parse("/Users/apple/百度云同步盘/origin_html.zip");
        Set<Word> inSyllabusVocabulary = inSyllabusVocabulary(words);
        compensate(inSyllabusVocabulary);

        String inSyllabusVocabularyText = formatPartOfSpeech(inSyllabusVocabulary);
        Set<Word> notInSyllabusVocabulary = notInSyllabusVocabulary(words);
        String notInSyllabusVocabularyText = formatPartOfSpeech(notInSyllabusVocabulary);
        LOGGER.info(formatPartOfSpeechType(words));
        try{
            Files.write(Paths.get("src/main/resources/part_of_speech_in_syllabus_vocabulary.txt"), inSyllabusVocabularyText.getBytes("utf-8"));
            Files.write(Paths.get("src/main/resources/part_of_speech_not_in_syllabus_vocabulary.txt"), notInSyllabusVocabularyText.getBytes("utf-8"));
            Files.write(Paths.get("src/main/resources/group_part_of_speech_in_syllabus_vocabulary.txt"), HtmlFormatter.toHtmlForPartOfSpeech(group(inSyllabusVocabulary)).getBytes("utf-8"));
            Files.write(Paths.get("src/main/resources/group_part_of_speech_not_in_syllabus_vocabulary.txt"), HtmlFormatter.toHtmlForPartOfSpeech(group(notInSyllabusVocabulary)).getBytes("utf-8"));
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static Map<String, Set<String>> group(Set<Word> words){
        Map<String, Set<String>> data = new HashMap<>();
        words.forEach(w -> {
            w.getPartOfSpeeches().forEach(pos -> {
                data.putIfAbsent(pos, new HashSet<>());
                data.get(pos).add(w.getWord());
            });
        });
        return data;
    }

    private static String formatPartOfSpeech(Set<Word> words){
        StringBuilder text = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        words.forEach(w ->
            text.append(i.incrementAndGet())
                    .append("\t")
                    .append(w.getWord())
                    .append("\t")
                    .append(w.getFormatPartOfSpeeches())
                    .append("\n")
        );
        text.append(formatPartOfSpeechType(words));
        return text.toString();
    }

    private static String formatPartOfSpeechType(Set<Word> words){
        StringBuilder text = new StringBuilder();
        Set<String> ps = new HashSet<>();
        words.forEach(w -> {
            ps.addAll(w.getPartOfSpeeches());
        });
        text.append("#词性种类(").append(ps.size()).append(")：").append("\n");
        ps.forEach(p -> text.append("#").append(p).append("=").append(PartOfSpeech.getMeaning(p)).append("\n"));
        return text.toString();
    }

    public static void compensate(Set<Word> words){
        Set<Word> minus = WordSources.minus(WordSources.getSyllabusVocabulary(), words);
        LOGGER.debug("本地文件中没有的考纲词数："+minus.size());
        minus.forEach(w -> {
            LOGGER.debug(w.getWord());
            Word word = parseWord(w.getWord());
            if(word!=null && !word.getPartOfSpeeches().isEmpty()){
                words.add(word);
            }
        });
    }

    public static Word parseWord(String word){
        try {
            return parseWord(Jsoup.parse(new URL("http://www.iciba.com/" + word), 15000).html(), word);
        }catch (Exception e){
            LOGGER.error("解析词性出错", e);
        }
        return null;
    }

    public static void main(String[] args){
        //parseWord("up");
        //parseWord("like");
        //parseWord("nothing");
        parseWord();
    }
}
