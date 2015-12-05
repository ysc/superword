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
 * 牛津词典词性列表
 * Created by ysc on 12/6/15.
 */
public enum OxfordPOS {
    symbol, plural, prefix, interrogative,
    exclamation, adjective, predeterminer, combiningForm,
    conjunction, pronoun, auxiliary, possessive,
    preposition, contraction, modal, infinitiveMarker,
    relativePossessive_determiner, verb, noun, cardinal,
    abbreviation, interrogativePossessive_determiner, determiner, adverb,
    ordinal, relative;

    public static String highlight(String definition){
        StringBuilder result = new StringBuilder(definition);
        Arrays.asList(OxfordPOS.values())
                .stream()
                .sorted((a, b) -> b.name().length() - a.name().length())
                .forEach(item -> {
                            String pos = item.name().replace("_", " ");
                            String temp = result.toString().replace(pos, "<font color=\"red\">" + pos + "</font>");
                            result.setLength(0);
                            result.append(temp);
                        }
                );
        return result.toString();
    }
}
