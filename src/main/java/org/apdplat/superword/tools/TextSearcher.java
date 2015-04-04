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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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

    public static Map<Integer, String> search(String keyword){
        long start = System.currentTimeMillis();
        Map<Integer, String> data = docs(hit(keyword));
        long cost = System.currentTimeMillis()-start;
        LOGGER.info("搜索耗时："+cost+"毫秒");
        return data;
    }

    public static List<Integer> hit(String keyword){
        long start = System.currentTimeMillis();
        LOGGER.info("搜索关键词："+keyword);
        List<String> words = TextAnalyzer.seg(keyword);
        LOGGER.info("分词结果："+words);
        List<Integer> result = Collections.EMPTY_LIST;
        if(words.size()==1){
            result = term(words.get(0));
        }else{
            result = term(words.get(0));
            for(int i=1; i<words.size(); i++){
                result = intersection(result, term(words.get(i)));
            }
        }
        long cost = System.currentTimeMillis()-start;
        LOGGER.info("命中数：："+result.size());
        LOGGER.info("查询索引耗时："+cost+"毫秒");
        return result;
    }

    public static Map<Integer, String> docs(List<Integer> ids){
        long start = System.currentTimeMillis();
        int lineCount=0;
        Map<Integer, String> data = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(INDEX_TEXT)))){
            String line = null;
            while (data.size()<ids.size() && (line=reader.readLine())!=null){
                lineCount++;
                if(ids.contains(lineCount)){
                    data.put(lineCount, line);
                }
            }
        }catch (Exception e){
            LOGGER.error("读取文件失败", e);
        }
        long cost = System.currentTimeMillis()-start;
        LOGGER.info("准备文本耗时："+cost+"毫秒");
        return data;
    }

    private static List<Integer> term(String word){
        String docs = INDEX_MAP.get(word);
        if(docs==null){
            return Collections.emptyList();
        }
        String[] ids = docs.split("\\|");
        List<Integer> list = new ArrayList<>();
        int last=0;
        for(String id : ids){
            int delta = Integer.parseInt(id);
            last+=delta;
            list.add(last);
        }
        return list;
    }

    private static List<Integer> intersection(List<Integer> one, List<Integer> two){
        return one.stream().filter(i->two.contains(i)).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Map<Integer, String> data = search("In addition, if ent is not specified, the named resource is not initialized in the naming.");
        data.entrySet().forEach(e->LOGGER.info("行号："+e.getKey()+"，句子："+e.getValue()));
    }
}
