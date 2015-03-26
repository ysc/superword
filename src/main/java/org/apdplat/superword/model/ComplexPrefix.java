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
 * 英语单词复杂前缀
 * @author 杨尚川
 */
public class ComplexPrefix{
    private String prefix;
    private String des;

    public ComplexPrefix(){}
    public ComplexPrefix(String prefix, String des) {
        this.prefix = prefix;
        this.des = des;
    }

    public List<Prefix> simplify(){
        List<Prefix> prefixes = new ArrayList<>();
        if(StringUtils.isBlank(prefix)){
            return prefixes;
        }
        String[] ps = prefix.toLowerCase().split(",");
        for (String p : ps) {
            p = p.replaceAll("\\s+", "");
            prefixes.add(new Prefix(p, des));
        }
        return prefixes;
    }
}