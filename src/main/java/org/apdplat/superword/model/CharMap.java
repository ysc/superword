/**
 *
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.apdplat.superword.model;

/**
 * 字符映射
 * @author 杨尚川
 */
public class CharMap implements Comparable{
    private String from;
    private String to;

    public CharMap(){}
    public CharMap(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CharMap charMap = (CharMap) o;

        if (!from.equals(charMap.from)) return false;
        if (!to.equals(charMap.to)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.from == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof CharMap)){
            return 1;
        }
        String t = ((CharMap)o).getFrom();
        if(t == null){
            return 1;
        }
        return this.from.compareTo(t);
    }

}