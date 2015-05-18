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
package org.apdplat.jsearch;

import org.apdplat.superword.tools.TextAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 文本搜索
 * @author 杨尚川
 */
public class TextSearcher {
    private TextSearcher(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(TextSearcher.class);
    private static final String INDEX_TEXT = "target/index_text.txt";
    private static final String INDEX = "target/index.txt";
    private static final Map<String, String> INDEX_MAP = new HashMap<>();
    static{
        try{
            LOGGER.error("开始初始化索引");
            long start = System.currentTimeMillis();
            Files.readAllLines(Paths.get(INDEX)).forEach(line -> {
                String[] attrs = line.split("=");
                if(attrs!=null && attrs.length==3){
                    INDEX_MAP.put(attrs[0], attrs[2]);
                }
            });
            LOGGER.error("索引初始化完毕，耗时：" + (System.currentTimeMillis()-start) + "毫秒");
        }catch (Exception e){
            LOGGER.error("索引初始化失败", e);
        }
    }

    public static List<Doc> search(String keyword){
        return search(keyword, SearchMode.INTERSECTION);
    }
    public static List<Doc> search(String keyword, SearchMode searchMode){
        long start = System.currentTimeMillis();
        //搜索关键词
        List<Doc> docs = hit(keyword, searchMode);
        int limit = docs.size() > 10 ? 10 : docs.size();
        docs = docs.subList(0, limit);
        //获取文档
        docs(docs);
        long cost = System.currentTimeMillis()-start;
        LOGGER.info("搜索耗时："+cost+"毫秒");
        return docs;
    }

    public static List<Doc> hit(String keyword, SearchMode searchMode){
        long start = System.currentTimeMillis();
        LOGGER.info("搜索关键词："+keyword);
        List<String> words = TextAnalyzer.seg(keyword);
        LOGGER.info("分词结果："+words);
        final Set<PostingItem> result = new ConcurrentSkipListSet<>();
        //文档打分使用
        Map<Integer, AtomicInteger> termCountPerDoc = new HashMap<>();
        if(words.size()==1){
            //单 词 查询
            result.addAll(term(words.get(0), termCountPerDoc));
        }else{
            //多 词 查询
            result.addAll(term(words.get(0), termCountPerDoc));
            for(int i=1; i<words.size(); i++){
                if(searchMode==SearchMode.INTERSECTION) {
                    SearchMode.intersection(result, term(words.get(i), termCountPerDoc));
                }else {
                    SearchMode.union(result, term(words.get(i), termCountPerDoc));
                }
            }
        }
        List<Doc> finalResult = termCountPerDoc
                .entrySet()
                .stream()
                .filter(entry->result.contains(new PostingItem(entry.getKey())))
                .sorted((a, b) -> new Integer(b.getValue().get()).compareTo(a.getValue().get()))
                .map(entry->{
                    Doc doc = new Doc();
                    doc.setId(entry.getKey());
                    doc.setHitTermCount(entry.getValue().get());
                    return doc;
                })
                .collect(Collectors.toList());
        long cost = System.currentTimeMillis()-start;
        LOGGER.info("命中数：："+result.size());
        LOGGER.info("查询索引耗时："+cost+"毫秒");
        return finalResult;
    }

    public static void docs(List<Doc> docs, int start, int length){
        docs(docs.subList(start, start+length));
    }

    public static void docs(List<Doc> docs){
        long startTime = System.currentTimeMillis();
        int lineCount = 0;
        Set<Integer> ids = docs.parallelStream().map(doc -> doc.getId()).collect(Collectors.toSet());
        final Map<Integer, String> data = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(INDEX_TEXT)))){
            String line = null;
            while (data.size()<docs.size() && (line=reader.readLine())!=null){
                lineCount++;
                if(ids.contains(lineCount)){
                    data.put(lineCount, line);
                }
            }
        }catch (Exception e){
            LOGGER.error("读取文件失败", e);
        }
        docs.parallelStream().forEach(doc->doc.setText(data.get(doc.getId())));
        data.clear();
        ids.clear();
        ids = null;
        long cost = System.currentTimeMillis()-startTime;
        LOGGER.info("准备文本耗时："+cost+"毫秒");
    }

    private static Set<PostingItem> term(String word, Map<Integer, AtomicInteger> frequencyContainer){
        String posting = INDEX_MAP.get(word);
        if(posting==null){
            return Collections.emptySet();
        }
        String[] postingItems = posting.split("\\|");
        Set<PostingItem> set = new HashSet<>();
        int lastDocId=0;
        for(String postingItem : postingItems){
            String[] postingItemAttr = postingItem.split("_");
            int delta = Integer.parseInt(postingItemAttr[0]);
            lastDocId+=delta;
            int frequency = Integer.parseInt(postingItemAttr[1]);
            String positions = postingItemAttr[2];
            PostingItem item = new PostingItem(lastDocId);
            item.setFrequency(frequency);
            for(String position : positions.split(":")){
                item.addPosition(Integer.parseInt(position));
            }
            set.add(item);
            frequencyDoc(frequencyContainer, lastDocId, frequency);
        }
        return set;
    }

    private static void frequencyDoc(Map<Integer, AtomicInteger> frequencyContainer, Integer docId, int frequency){
        frequencyContainer.putIfAbsent(docId, new AtomicInteger());
        frequencyContainer.get(docId).addAndGet(frequency);
    }

    public static enum SearchMode{
        INTERSECTION, UNION;

        /**
         * 求one和two的交集，合并结果存储于one中
         * @param one
         * @param two
         */
        private static void intersection(Set<PostingItem> one, Set<PostingItem> two){
            one.forEach(item->{
                if(!two.contains(item)){
                    one.remove(item);
                }
            });
        }

        /**
         * 求one和two的并集，合并结果存储于one中
         * @param one
         * @param two
         */
        private static void union(Set<PostingItem> one, Set<PostingItem> two){
            two.forEach(item->{
                if(!one.contains(item)){
                    one.add(item);
                }
            });
        }
    }
    public static void main(String[] args) {
        //SearchMode.INTERSECTION
        List<Doc> docs = search("In addition, if ent is not specified, the named resource is not initialized in the naming.");
        LOGGER.info("搜索结果数："+docs.size());
        AtomicInteger i = new AtomicInteger();
        docs.forEach(doc -> LOGGER.info("结果" + i.incrementAndGet() + "、ID：" + doc.getId() + "，包含的关键词个数：" + doc.getHitTermCount() + "，句子：" + doc.getText()));
        //SearchMode.UNION
        docs = search("Distributed Algorithms", SearchMode.UNION);
        LOGGER.info("搜索结果数："+docs.size());
        AtomicInteger j = new AtomicInteger();
        docs.forEach(doc -> LOGGER.info("结果" + j.incrementAndGet() + "、ID：" + doc.getId() + "，包含的关键词个数：" + doc.getHitTermCount() + "，句子：" + doc.getText()));
    }
}
