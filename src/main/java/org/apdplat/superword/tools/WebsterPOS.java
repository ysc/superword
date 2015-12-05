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

import java.util.Arrays;

/**
 * 韦氏词典词性列表
 * Created by ysc on 12/6/15.
 */
public enum WebsterPOS {
    symbol, definite, interjection, prefix,
    verb, noun, combining, suffix,
    indefinite, adjective, conjunction, pronoun,
    service, intransitive, preposition, trademark,
    adverb, transitive;

    public static String highlight(String definition){
        StringBuilder result = new StringBuilder(definition);
        Arrays.asList(WebsterPOS.values())
                .stream()
                .sorted((a, b) -> b.name().length() - a.name().length())
                .forEach(item -> {
                            String temp = result.toString().replace(item.name(), "<font color=\"red\">" + item.name() + "</font>");
                            result.setLength(0);
                            result.append(temp);
                        }
                );
        return result.toString();
    }
}
