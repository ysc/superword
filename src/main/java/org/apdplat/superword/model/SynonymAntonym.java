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

import java.util.*;

/**
 * 同义词反义词
 * @author 杨尚川
 */
public class SynonymAntonym implements Comparable{
    private Word word;
    private Set<Word> synonym = new HashSet<>();
    private Set<Word> antonym = new HashSet<>();

    public int size(){
        return synonym.size()+antonym.size();
    }

    public boolean valid(){
        return !synonym.isEmpty() || !antonym.isEmpty();
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Set<Word> getSynonym() {
        return Collections.unmodifiableSet(synonym);
    }

    public void addSynonym(Word synonym) {
        this.synonym.add(synonym);
    }

    public void removeSynonym(Word synonym) {
        this.synonym.remove(synonym);
    }

    public Set<Word> getAntonym() {
        return Collections.unmodifiableSet(antonym);
    }

    public void addAntonym(Word antonym) {
        this.antonym.add(antonym);
    }

    public void removeAntonym(Word antonym) {
        this.antonym.remove(antonym);
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.word == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof SynonymAntonym)){
            return 1;
        }
        Word t = ((SynonymAntonym)o).getWord();
        if(t == null){
            return 1;
        }
        return this.word.compareTo(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynonymAntonym)) return false;

        SynonymAntonym that = (SynonymAntonym) o;

        if (word != null ? !word.equals(that.word) : that.word != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return word != null ? word.hashCode() : 0;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(word.getWord()).append("\n");
        str.append("同义词：");
        this.synonym.forEach(w -> str.append(w.getWord()).append("\t"));
        str.append("\n");
        str.append("反义词：");
        this.antonym.forEach(w -> str.append(w.getWord()).append("\t"));
        str.append("\n");
        return str.toString();
    }
}
