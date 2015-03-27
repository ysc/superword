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

package org.apdplat.superword.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 同义词辨析
 * @author 杨尚川
 */
public class SynonymDiscrimination implements Comparable {
    private String title;
    private String des;
    private List<Word> words= new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public void addWord(Word word) {
        this.words.add(word);
    }

    public void removeWord(Word word) {
        this.words.remove(word);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(title).append("\n").append(des).append("\n");
        this.words.forEach(w -> str.append(w.getWord()).append("： ").append(w.getMeaning()).append("\n"));
        return str.toString();
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.title == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof SynonymDiscrimination)){
            return 1;
        }
        String t = ((SynonymDiscrimination)o).getTitle();
        if(t == null){
            return 1;
        }
        return this.title.compareTo(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynonymDiscrimination)) return false;

        SynonymDiscrimination that = (SynonymDiscrimination) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}
