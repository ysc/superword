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

import java.util.HashMap;
import java.util.Map;

/**
 * 不规则复数词工具
 * Created by ysc on 11/25/15.
 */
public class IrregularPlurals {
    private static final Map<String, String> pluralToSingular = new HashMap<>();
    static {
        FileUtils.readResource("/irregular_plurals.txt").forEach(line->{
            String[] attrs = line.split("\\s+");
            String plural = attrs[0];
            String singular = attrs[1];
            pluralToSingular.put(plural, singular);
        });
    }
    public static String getSingular(String word){
        String singular = pluralToSingular.get(word);
        if(singular == null){
            singular = word;
        }
        return singular;
    }
}
