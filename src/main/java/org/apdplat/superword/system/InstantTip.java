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

package org.apdplat.superword.system;

import org.apdplat.superword.tools.MySQLUtils;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.apdplat.superword.tools.WordSources;

import java.util.Map;

/**
 * instant tip
 * Created by ysc on 12/11/15.
 */
public class InstantTip {

    private static final int DATA_MAX_LENGTH = 60;
    private static final int RECORD_COUNT_LIMIT = 10;

    private static final Map<String, String> WORDS_TO_DEFINITION;

    static {
        WORDS_TO_DEFINITION = MySQLUtils.getAllWordDefinitionMap(Dictionary.YOUDAO.name(), WordSources.getAll());
        WORDS_TO_DEFINITION.entrySet().stream().forEach(entry->{
            String definition = entry.getValue();
            definition = definition.replace("<br/>", "  ");
            definition = definition.length() > DATA_MAX_LENGTH ? definition.substring(0, DATA_MAX_LENGTH) + "..." : definition;
            entry.setValue(definition);
        });
    }

    public static String getWordsByPrefix(String prefix){
        StringBuilder html= new StringBuilder();
        html.append("<ul>\n");
        int i=0;
        for(String word : WORDS_TO_DEFINITION.keySet()){
            if(word.toLowerCase().startsWith(prefix.toLowerCase())){
                html.append("<li>").append(WordLinker.toLink(word)).append("  ").append(WORDS_TO_DEFINITION.get(word)).append("</li>\n");
                if((++i) >= RECORD_COUNT_LIMIT){
                    break;
                }
            }
        }
        html.append("</ul>\n");
        return html.toString();
    }

    public static void main(String[] args) {
        System.out.println(getWordsByPrefix("lo"));
    }
}
