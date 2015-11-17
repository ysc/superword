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
 * ICIBA:爱词霸
 * YOUDAO:有道
 * COLLINS:柯林斯
 * WEBSTER:韦氏
 * @author 杨尚川
 */
public class WordLinker {
    private WordLinker(){}

    //链接到哪个词典
    public volatile static String dictionary = "ICIBA";

    private static final String EM_PRE = "<span style=\"color:red\">";
    private static final String EM_SUF = "</span>";
    private static final String ICIBA = "http://www.iciba.com/";
    private static final String YOUDAO = "http://dict.youdao.com/search?q=";
    private static final String COLLINS = "http://www.collinsdictionary.com/dictionary/english/";
    private static final String WEBSTER = "http://www.wordcentral.com/cgi-bin/student?";


    public static String toLink(String word){
        return toLink(word, "");
    }

    public static String toLink(String word, String emphasize){
        return toLink(word, emphasize, EM_PRE, EM_SUF);
    }
    public static String toLink(String word, String emphasize, String emPre, String emSuf){
        switch (dictionary){
            case ICIBA: return linkToICIBA(word, emphasize, emPre, emSuf);
            case YOUDAO: return linkToYOUDAO(word, emphasize, emPre, emSuf);
            case COLLINS: return linkToCOLLINS(word, emphasize, emPre, emSuf);
            case WEBSTER: return linkToWEBSTER(word, emphasize, emPre, emSuf);
        }
        //default
        return linkToICIBA(word, emphasize, emPre, emSuf);
    }

    private static String linkToICIBA(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, ICIBA);
    }
    private static String linkToYOUDAO(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, YOUDAO);
    }
    private static String linkToCOLLINS(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, COLLINS);
    }
    private static String linkToWEBSTER(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, COLLINS);
    }
    private static String linkTo(String word, String emphasize, String emPre, String emSuf, String webSite){
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
                .append("\">");
        if(StringUtils.isNotBlank(emphasize)) {
            Set<String> targets = new HashSet<>();
            Matcher matcher = pattern.matcher(word);
            while(matcher.find()){
                String target = matcher.group();
                targets.add(target);
            }
            for(String target : targets){
                word = word.replaceAll(target, emPre+target+emSuf);
            }
        }
        html.append(word).append("</a>");
        return html.toString();
    }
}
