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

package org.apdplat.superword.tools;

import java.util.HashSet;
import java.util.Set;

/**
 * 倒排表项
 * @author 杨尚川
 */
public class PostingItem implements Comparable{
    private int docId;
    private int frequency;
    private Set<Integer> positions = new HashSet<>();

    public PostingItem(int docId) {
        this.docId = docId;
    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getFrequency(){
        return positions.isEmpty()?frequency:positions.size();
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String positionsToStr(){
        StringBuilder str = new StringBuilder();
        this.positions.stream().sorted().forEach(p -> str.append(p).append(":"));
        str.setLength(str.length()-1);
        return str.toString();
    }

    public Set<Integer> getPositions() {
        return this.positions;
    }

    public void addPosition(int position) {
        this.positions.add(position);
    }

    public void removePosition(int position) {
        this.positions.remove(position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof org.apdplat.superword.tools.PostingItem)) return false;

        org.apdplat.superword.tools.PostingItem postingItem = (org.apdplat.superword.tools.PostingItem) o;

        return docId == postingItem.docId;

    }

    @Override
    public int hashCode() {
        return docId;
    }

    @Override
    public int compareTo(Object o) {
        return new Integer(docId).compareTo(((org.apdplat.superword.tools.PostingItem) o).getDocId());
    }
}
