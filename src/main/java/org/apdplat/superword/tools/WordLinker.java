/**
 *
 * APDPlat - Application Product Development Platform Copyright (c) 2013, 杨尚川,
 * yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.apdplat.superword.tools;

import org.apache.commons.lang.StringUtils;

/**
 * 单词链接工具
 * @author 杨尚川
 */
public class WordLinker {
    private WordLinker(){}

    public static String toLink(String word){
        return toLink(word, "");
    }

    public static String toLink(String word, String emphasize){
        return linkToICIBA(word, emphasize);
    }

    private static String linkToICIBA(String word, String emphasize){
        StringBuilder html = new StringBuilder();
        html.append("<a target=\"_blank\" href=\"http://www.iciba.com/")
                .append(word)
                .append("\">");
        if(StringUtils.isNotBlank(emphasize)) {
            html.append(word.replace(emphasize, "<font color=\"red\">" + emphasize + "</font>"));
        }else{
            html.append(word);
        }
        html.append("</a>");
        return html.toString();
    }
    private static String linkToYOUDAO(String word, String emphasize){
        StringBuilder html = new StringBuilder();
        html.append("<a target=\"_blank\" href=\"http://dict.youdao.com/search?q=")
                .append(word)
                .append("\">");
        if(StringUtils.isNotBlank(emphasize)) {
            html.append(word.replace(emphasize, "<font color=\"red\">" + emphasize + "</font>"));
        }else{
            html.append(word);
        }
        html.append("</a>");
        return html.toString();
    }
}
