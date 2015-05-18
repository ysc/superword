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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 文本索引
 * @author 杨尚川
 */
public class TextIndexer {
    private TextIndexer(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(TextIndexer.class);
    private static final String INDEX_TEXT = "target/index_text.txt";
    private static final String INDEX = "target/index.txt";
    private static final int INDEX_LENGTH_LIMIT = 1000;

    public static void index(String path){
        try {
            //词 ->  [{文档ID,位置}, {文档ID,位置}]
            Map<String, Posting> index = new HashMap<>();
            AtomicInteger lineCount = new AtomicInteger();
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(INDEX_TEXT), Charset.forName("utf-8"));
            //将所有文本合成一个文件，每一行分配一个行号
            TextAnalyzer.getFileNames(path).forEach(file -> {
                try {
                    List<String> lines = Files.readAllLines(Paths.get(file));
                    AtomicInteger i = new AtomicInteger();
                    lines.forEach(line -> {
                        try {
                            writer.append(line).append("《").append(Paths.get(file).getFileName().toString().split("\\.")[0]).append("》【").append(lines.size()+"/"+i.incrementAndGet()).append("】\n");
                            lineCount.incrementAndGet();
                            List<String> words = TextAnalyzer.seg(line);
                            for(int j=0; j< words.size(); j++){
                                String word = words.get(j);
                                //准备倒排表
                                index.putIfAbsent(word, new Posting());
                                //倒排表长度限制
                                if(index.get(word).size()<INDEX_LENGTH_LIMIT) {
                                    //一篇文档对应倒排表中的一项
                                    index.get(word).putIfAbsent(lineCount.get());
                                    index.get(word).get(lineCount.get()).addPosition(j+1);
                                }
                            }
                        } catch (IOException e) {
                            LOGGER.error("文件写入错误", e);
                        }
                    });

                } catch (IOException e) {
                    LOGGER.error("文件读取错误", e);
                }
            });
            writer.close();
            List<String> indices =
            index
                .entrySet()
                .stream()
                .sorted((a,b)->(b.getValue().size()-a.getValue().size()))
                .map(entry -> {
                    StringBuilder docs = new StringBuilder();
                    AtomicInteger lastDocId = new AtomicInteger();
                    entry.getValue().getPostingItems().stream().sorted().forEach(postingItem -> {
                        //保存增量
                        docs.append(postingItem.getDocId()-lastDocId.get()).append("_").append(postingItem.getFrequency()).append("_").append(postingItem.positionsToStr()).append("|");
                        lastDocId.set(postingItem.getDocId());
                    });
                    if (docs.length() > 1) {
                        docs.setLength(docs.length() - 1);
                        return entry.getKey() + "=" + entry.getValue().size() + "=" + docs.toString();
                    }
                    return entry.getKey() + "=0";
                })
                .collect(Collectors.toList());
            Files.write(Paths.get(INDEX), indices, Charset.forName("utf-8"));
        }catch (Exception e){
            LOGGER.error("索引操作出错", e);
        }
    }

    public static void main(String[] args) {
        //index("src/test/resources/text");
        index("src/main/resources/it");
    }
}
