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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 英语单词复杂后缀
 * @author 杨尚川
 */
public class ComplexSuffix{
    private String suffix;
    private String des;

    public ComplexSuffix(String suffix, String des) {
        this.suffix = suffix;
        this.des = des;
    }

    public List<Suffix> simplify(){
        List<Suffix> suffixes = new ArrayList<>();
        if(StringUtils.isBlank(suffix)){
            return suffixes;
        }
        String[] ps = suffix.toLowerCase().split(",");
        for (String p : ps) {
            p = p.replaceAll("\\s+", "");
            suffixes.add(new Suffix(p, des));
        }
        return suffixes;
    }
}