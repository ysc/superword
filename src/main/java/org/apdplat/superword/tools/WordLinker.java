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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 单词链接工具
 * @author 杨尚川
 */
public class WordLinker {
    private WordLinker(){}

    private static final String EM_PRE = "<span style=\"color:red\">";
    private static final String EM_SUF = "</span>";
    private static final String ICIBA = "http://www.iciba.com/";
    private static final String YOUDAO = "http://dict.youdao.com/search?q=";

    public static String toLink(String word){
        return toLink(word, "");
    }

    public static String toLink(String word, String emphasize){
        return linkToICIBA(word, emphasize);
    }

    private static String linkToICIBA(String word, String emphasize){
        return linkTo(word, emphasize, ICIBA);
    }
    private static String linkToYOUDAO(String word, String emphasize){
        return linkTo(word, emphasize, YOUDAO);
    }
    private static String linkTo(String word, String emphasize, String webSite){
        StringBuilder p = new StringBuilder();
        for (char c : emphasize.toCharArray()) {
            p.append("[")
                    .append(Character.toUpperCase(c))
                    .append(Character.toLowerCase(c))
                    .append("]{1}");
        }
        Pattern pattern = Pattern.compile(p.toString());
        StringBuilder html = new StringBuilder();
        html.append("<a target=\"_blank\" href=\"")
                .append(webSite)
                .append(word)
                .append("\">");
        if(StringUtils.isNotBlank(emphasize)) {
            Set<String> targets = new HashSet<>();
            Matcher matcher = pattern.matcher(word);
            while(matcher.find()){
                String target = matcher.group();
                targets.add(target);
            }
            for(String target : targets){
                word = word.replaceAll(target, EM_PRE+target+EM_SUF);
            }
        }
        html.append(word).append("</a>");
        return html.toString();
    }
}
