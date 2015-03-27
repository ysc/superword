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
 * 英语单词前缀
 * @author 杨尚川
 */
public class Prefix implements Comparable{
    private String prefix;
    private String des;

    public Prefix(){}
    public Prefix(String prefix, String des) {
        this.prefix = prefix;
        this.des = des;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.prefix == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof Prefix)){
            return 1;
        }
        String t = ((Prefix)o).getPrefix();
        if(t == null){
            return 1;
        }
        return this.prefix.compareTo(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prefix)) return false;

        Prefix prefix1 = (Prefix) o;

        if (prefix != null ? !prefix.equals(prefix1.prefix) : prefix1.prefix != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return prefix != null ? prefix.hashCode() : 0;
    }
}