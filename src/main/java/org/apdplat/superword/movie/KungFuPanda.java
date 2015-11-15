/*
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.apdplat.superword.movie;

import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 电影功夫熊猫使用的单词分析
 * 你英语四级过了吗? 功夫熊猫看了吗?
 * 功夫熊猫使用了995个英语单词,你会说很简单吧,别急,这些单词中仍然有236个单词不在四级词汇表中,花两分钟时间看看你是否认识这些单词.
 * Created by ysc on 11/15/15.
 */
public class KungFuPanda {
    public static void main(String[] args) throws IOException {
        Set<Word> cet4 = WordSources.get("/word_CET4.txt");

        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
        Files.readAllLines(Paths.get("src/main/resources/kungfupanda.txt")).forEach(line -> {
            StringBuilder buffer = new StringBuilder();
            for (String word : line.split("\\s+")) {
                if (word.contains("'")) {
                    continue;
                }
                buffer.setLength(0);
                for (char c : word.toCharArray()) {
                    if (Character.isAlphabetic(c)) {
                        buffer.append(Character.toLowerCase(c));
                    }
                }
                if (buffer.length() < 1) {
                    return;
                }
                map.putIfAbsent(buffer.toString(), new AtomicInteger());
                map.get(buffer.toString()).incrementAndGet();
            }
        });
        AtomicInteger i = new AtomicInteger();
        System.out.println("<h3>words:</h3><br/>");
        map.entrySet().stream().sorted((a, b) -> b.getValue().get() - a.getValue().get()).forEach(entry -> {
            System.out.println(i.incrementAndGet() + ". " + WordLinker.toLink(entry.getKey()) + "\t" + entry.getValue()+"<br/>");
        });
        i.set(0);
        System.out.println("<h3>words don't occur in CET4:</h3><br/>");
        map.entrySet().stream().sorted((a, b) -> b.getValue().get() - a.getValue().get()).forEach(entry -> {
            String w= entry.getKey();
            if(cet4.contains(new Word(w, ""))){
                return;
            }
            if(w.endsWith("s") && cet4.contains(new Word(w.substring(0, w.length()-1), ""))){
                return;
            }
            if(w.endsWith("ed") && cet4.contains(new Word(w.substring(0, w.length() - 2), ""))){
                return;
            }
            if(w.endsWith("ing") && cet4.contains(new Word(w.substring(0, w.length()-3), ""))){
                return;
            }
            System.out.println(i.incrementAndGet() + ". " + WordLinker.toLink(entry.getKey()) + "\t" + entry.getValue()+"<br/>");
        });
    }
}
