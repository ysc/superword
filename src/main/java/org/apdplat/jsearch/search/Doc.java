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

package org.apdplat.jsearch.search;

import java.util.*;

/**
 * 搜索结果文档
 * @author 杨尚川
 */
public class Doc implements Comparable{
    /**
     * 文档ID
     */
    private int id;
    /**
     * 累计词频
     */
    private int frequency;
    /**
     * 文档文本
     */
    private String text;
    /**
     * 词和词位
     */
    private Map<String, List<Integer>> wordPosition = new HashMap<>();
    /**
     * 文档评分
     */
    private float score;

    /**
     * 合并通过不同关键词搜索到的同一个文档
     * @param doc
     */
    public void merge(Doc doc){
        if(id==doc.getId()){
            this.frequency += doc.frequency;
            this.wordPosition.putAll(doc.getWordPosition());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, List<Integer>> getWordPosition() {
        return Collections.unmodifiableMap(wordPosition);
    }

    public void putWordPosition(String word, List<Integer> positions) {
        this.wordPosition.put(word, positions);
    }

    public void removeWordPosition(String word) {
        this.wordPosition.remove(word);
    }

    public void clearWordPositions() {
        this.wordPosition.clear();
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doc)) return false;

        Doc doc = (Doc) o;

        return id == doc.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(Object o) {
        return new Integer(id).compareTo(((Doc) o).getId());
    }
}
