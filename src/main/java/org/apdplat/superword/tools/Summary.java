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
import org.apdplat.superword.rule.CompoundWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 句子摘要
 *
 * @author 杨尚川
 */
public class Summary {
    private static final Logger LOGGER = LoggerFactory.getLogger(Summary.class);

    public static String summaryForPreciousWords(int topN, String path, String... dics) {
        return summaryForPreciousWords(0, topN, path, dics);
    }
    public static String summaryForPreciousWords(int from, int to, String path, String... dics) {
        //摘要的词
        List<String> words = preciousWords(path, dics);
        if(to > words.size()){
            to = words.size();
        }
        if(from < 0){
            from = 0;
        }
        words = words.subList(from, to);
        Map<String, Map<String, List<String>>> data = findEvidence(Paths.get(path), words, 10, 1);
        String html = toHtmlFragment(data, from);
        LOGGER.info(words.toString());
        return html;
    }

    public static List<String> preciousWords(String path, String... dics) {
        //词典
        Set<Word> dic = WordSources.get(dics);
        return preciousWords(path, dic).stream().map(e -> e.getKey()).collect(Collectors.toList());
    }
    public static List<Map.Entry<String, AtomicInteger>> preciousWords(String path, Set<Word> dic) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = TextAnalyzer.getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> frequency = TextAnalyzer.frequency(fileNames);
        Map<String, AtomicInteger> unknown = new HashMap<>();
        LOGGER.debug("需要检查单词个数：" + frequency.keySet().size());
        frequency
                .keySet()
                .forEach(key -> {
                    if (!dic.contains(new Word(key.toLowerCase(), ""))) {
                        unknown.put(key, frequency.get(key));
                    }
                });
        LOGGER.debug("未知的单词个数：" + unknown.size());
        AtomicInteger i = new AtomicInteger();
        List<Map.Entry<String, AtomicInteger>> result = unknown
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() > 2)
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .collect(Collectors.toList());
        return result;
    }

    public static String toHtmlFragment(Map<String, Map<String, List<String>>> data) {
        return toHtmlFragment(data, 0);
    }
    public static String toHtmlFragment(Map<String, Map<String, List<String>>> data, int base) {
        Set<String> books = new HashSet<>();
        StringBuilder html = new StringBuilder();
        AtomicInteger i = new AtomicInteger(base);
        data.keySet()
                .stream()
                .forEach(word -> {
                    if (data.get(word).isEmpty()) {
                        System.err.println("词：" + word + "没有找到匹配文本");
                        return;
                    }
                    StringBuilder p = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        p.append("[")
                         .append(Character.toUpperCase(c))
                         .append(Character.toLowerCase(c))
                         .append("]{1}");
                    }
                    html.append("<h1>")
                            .append(i.incrementAndGet())
                            .append("、单词 ")
                            .append(WordLinker.toLink(word))
                            .append(" 的匹配文本：</h1><br/>\n");
                    html.append("<ol>\n");
                    String emPre = "<span style=\"color:red\">";
                    String emSuf = "</span>";
                    data.get(word)
                            .entrySet()
                            .forEach(entry -> {
                                books.add(entry.getKey());
                                String book = " <u><i>" + entry.getKey() + "</i></u>";
                                entry.getValue()
                                     .forEach(t -> {
                                         t = t.replaceAll(p.toString(), emPre+word+emSuf);
                                         if(t.startsWith(emPre)){
                                             t = emPre+Character.toUpperCase(t.charAt(18))+t.substring(19);
                                         }
                                         html.append("\t<li>")
                                             .append(t)
                                             .append(book)
                                             .append("</li><br/>\n");
                                     });
                            });
                    html.append("</ol><br/>\n");
                });
        html.append("涉及文献数目：").append(books.size()).append("<br/>\n");
        AtomicInteger j = new AtomicInteger();
        books.stream().sorted().forEach(b -> html.append("\t").append(j.incrementAndGet()).append("、").append(b).append("<br/>\n"));
        return html.toString();
    }

    /**
     * @param dir PDF文档解析之后形成的文本文档所在目录
     * @param words 待处理词列表
     * @param totalLimitForWord 一个词最多需要多少个句子
     * @param bookLimitForBook 一本书里面最多取多少个句子
     * @return
     */
    public static Map<String, Map<String, List<String>>> findEvidence(Path dir, List<String> words, int totalLimitForWord, int bookLimitForBook) {
        LOGGER.info("处理目录：" + dir);
        Map<String, Map<String, List<String>>> data = new LinkedHashMap<>();
        Map<String, AtomicInteger> wordInOneBookCollectCount = new HashMap<>();
        Map<String, AtomicInteger> wordInAllBookCollectCount = new HashMap<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.toFile().getAbsolutePath();
                    if (file.toFile().getName().startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (!fileName.endsWith(".txt")) {
                        LOGGER.info("放弃处理非txt文件：" + fileName);
                        return FileVisitResult.CONTINUE;
                    }

                    LOGGER.info("处理文件：" + fileName);
                    List<String> lines = Files.readAllLines(file);
                    String book = file.toFile().getName().replace(".txt", "");
                    lines.forEach(line -> {
                        final List<String> wordSet = TextAnalyzer.seg(line);
                        words
                            .forEach(word -> {
                                String wordBook = word + "_" + book;
                                wordInOneBookCollectCount.putIfAbsent(wordBook, new AtomicInteger());
                                wordInAllBookCollectCount.putIfAbsent(word, new AtomicInteger());
                                data.putIfAbsent(word, new HashMap<>());
                                if (wordSet.contains(word)
                                        && wordInOneBookCollectCount.get(wordBook).get() < bookLimitForBook
                                        && wordInAllBookCollectCount.get(word).get() < totalLimitForWord) {
                                    wordInOneBookCollectCount.get(wordBook).incrementAndGet();
                                    wordInAllBookCollectCount.get(word).incrementAndGet();
                                    data.get(word).putIfAbsent(book, new ArrayList<>());
                                    data.get(word).get(book).add(line);
                                }
                            });
                    });

                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String summary() {
        List<String> words =
                Arrays.asList("categorically",
                        "misleadingly",
                        "weightings",
                        "uniques",
                        "alphanumerics",
                        "misspell",
                        "conducive",
                        "dissection",
                        "marvel",
                        "graciously",
                        "inspections",
                        "appetite",
                        "visualizations",
                        "commonalities",
                        "dissecting",
                        "fidelity",
                        "creativity",
                        "coyote",
                        "reaction");
        Map<String, Map<String, List<String>>> data = findEvidence(Paths.get("src/main/resources/it"), words, 100, 10);
        String html = toHtmlFragment(data);
        return html;
    }

    public static String summary(int totalLimitForWord, int bookLimitForBook, String... word) {
        List<String> words =
                Arrays.asList(word);
        Map<String, Map<String, List<String>>> data = findEvidence(Paths.get("src/main/resources/it"), words, totalLimitForWord, bookLimitForBook);
        String html = toHtmlFragment(data);
        return html;
    }

    public static void main(String[] args) throws Exception {
        //String html = summary();
        //String html = summaryForPreciousWords(275, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(275, 550, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(550, 800, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(800, 1100, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(1100, 1400, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(1400, 1700, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(1700, 2000, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summaryForPreciousWords(2000, "src/main/resources/it", "/words.txt", "/words_extra.txt", "/words_gre.txt");
        //String html = summary(Integer.MAX_VALUE, Integer.MAX_VALUE, "mixin");
        List<Map.Entry<String, AtomicInteger>> words = preciousWords("src/main/resources/it", WordSources.get("/words.txt", "/words_extra.txt", "/words_gre.txt"));
        AtomicInteger i = new AtomicInteger();
        words.forEach(e -> LOGGER.info(i.incrementAndGet()+"、"+e.getKey()+"\t"+e.getValue()));
        //Files.write(Paths.get("target/summary.txt"), html.getBytes("utf-8"));
    }
}
