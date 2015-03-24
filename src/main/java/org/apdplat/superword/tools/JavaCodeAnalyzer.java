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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JDK类库源代码分析
 * @author 杨尚川
 */
public class JavaCodeAnalyzer {
    private JavaCodeAnalyzer(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaCodeAnalyzer.class);

    public static Map<String, AtomicInteger> parseDir(String dir) {
        LOGGER.info("开始解析目录：" + dir);
        Map<String, AtomicInteger> data = new HashMap<>();
        try {
            Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Map<String, AtomicInteger> r = parseFile(file.toFile().getAbsolutePath());
                    r.keySet().forEach(k -> {
                        data.putIfAbsent(k, new AtomicInteger());
                        data.get(k).addAndGet(r.get(k).get());
                    });
                    r.clear();
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            LOGGER.error("解析目录出错", e);
        }
        return data;
    }

    public static Map<String, AtomicInteger> parseZip(String zipFile){
        LOGGER.info("开始解析ZIP文件："+zipFile);
        Map<String, AtomicInteger> data = new HashMap<>();
        try (FileSystem fs = FileSystems.newFileSystem(Paths.get(zipFile), JavaCodeAnalyzer.class.getClassLoader())) {
            for(Path path : fs.getRootDirectories()){
                LOGGER.info("处理目录："+path);
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        LOGGER.info("处理文件：" + file);
                        // 拷贝到本地文件系统
                        Path temp = Paths.get("target/java-source-code.txt");
                        Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                        Map<String, AtomicInteger> r = parseFile(temp.toFile().getAbsolutePath());
                        r.keySet().forEach(k -> {
                            data.putIfAbsent(k, new AtomicInteger());
                            data.get(k).addAndGet(r.get(k).get());
                        });
                        r.clear();
                        return FileVisitResult.CONTINUE;
                    }

                });
            }
        }catch (Exception e){
            LOGGER.error("解析ZIP文件出错", e);
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
                List<String> words = TextAnalyzer.seg(line);
                words.forEach(word -> {
                    data.putIfAbsent(word, new AtomicInteger());
                    data.get(word).incrementAndGet();
                });
                words.clear();
            }
        } catch (IOException e) {
            LOGGER.error("解析文本出错", e);
        }
        return data;
    }

    /**
     *  CET4、CET6、GRE、IELTS、TOEFL、考研英语的词汇
     *  有哪些出现在了JDK的源代码中
     * @param zipFile
     * @return
     */
    public static String importantWords(String zipFile){
        Set<Word> wordSet = WordSources.get("/word_CET4.txt",
                "/word_CET6.txt",
                "/word_GRE.txt",
                "/word_IELTS.txt",
                "/word_TOEFL.txt",
                "/word_考 研.txt");
        Map<Word, AtomicInteger> data = WordSources.convert(parseZip(zipFile));
        Set<Map.Entry<Word, AtomicInteger>> entries = data.entrySet()
                                .stream()
                                .filter(entry -> wordSet.contains(entry.getKey()))
                                .collect(Collectors.toSet());
        return HtmlFormatter.toHtmlTableFragment(entries, 5);
    }

    /**
     * 将源代码解析为词典
     * @param zipFile
     * @param dicPath
     */
    public static void toDic(String zipFile, String dicPath){
        Map<String, AtomicInteger> data = parseZip(zipFile);
        List<String> words = data
                .entrySet()
                .stream()
                .filter(w -> StringUtils.isAlpha(w.getKey())
                        && w.getKey().length() < 12)
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .map(e -> e.getValue() + "\t" + e.getKey())
                .collect(Collectors.toList());
        try {
            Files.write(Paths.get(dicPath), words);
        } catch (IOException e) {
            LOGGER.error("保存词典文件出错", e);
        }
    }

    public static void main(String[] args) throws IOException {
        String zipFile = "/Library/Java/JavaVirtualMachines/jdk1.8.0_11.jdk/Contents/Home/src.zip";
        //toDic(zipFile, "src/main/resources/word_java.txt");
        System.out.print(importantWords(zipFile));
    }
}
