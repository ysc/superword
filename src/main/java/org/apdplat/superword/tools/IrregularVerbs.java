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
 * 不规则动词工具
 * Created by ysc on 11/25/15.
 */
public class IrregularVerbs {
    private static final Map<String, String> pastTenseToBaseForm = new HashMap<>();
    private static final Map<String, String> pastParticipleToBaseForm = new HashMap<>();
    static {
        FileUtils.readResource("/irregular_verbs.txt").forEach(line->{
            String[] attrs = line.split("\t");
            String baseForm = attrs[0];
            String pastTense = attrs[1];
            String pastParticiple = attrs[2];
            pastTenseToBaseForm.put(pastTense, baseForm);
            pastParticipleToBaseForm.put(pastParticiple, baseForm);
        });
    }
    public static String getBaseForm(String word){
        String baseForm = pastTenseToBaseForm.get(word);
        if(baseForm == null){
            baseForm = pastParticipleToBaseForm.get(word);
        }
        if(baseForm == null){
            baseForm = word;
        }
        return baseForm;
    }
}
