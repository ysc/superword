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
 * 英语单词后缀
 * @author 杨尚川
 */
public class Suffix implements Comparable{
    private String suffix;
    private String des;

    public Suffix(){}
    public Suffix(String suffix, String des) {
        this.suffix = suffix;
        this.des = des;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.suffix == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof Suffix)){
            return 1;
        }
        String t = ((Suffix)o).getSuffix();
        if(t == null){
            return 1;
        }
        return this.suffix.compareTo(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Suffix that = (Suffix) o;

        if (!suffix.equals(that.suffix)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return suffix.hashCode();
    }
}