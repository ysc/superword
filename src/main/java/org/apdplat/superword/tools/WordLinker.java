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
 * OXFORD:牛津
 * CAMBRIDGE:剑桥
 * MACMILLAN:麦克米伦
 * HERITAGE:美国传统
 * WIKTIONARY:维基词典
 * WORDNET:WordNet
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
    private static final String WEBSTER = "http://www.merriam-webster.com/dictionary/";
    private static final String OXFORD = "http://www.oxforddictionaries.com/definition/english/";
    private static final String CAMBRIDGE = "http://dictionary.cambridge.org/dictionary/english/";
    private static final String MACMILLAN = "http://www.macmillandictionary.com/dictionary/british/";
    private static final String HERITAGE = "https://www.ahdictionary.com/word/search.html?q=";
    private static final String WIKTIONARY = "https://en.wiktionary.org/wiki/";
    private static final String WORDNET = "http://wordnetweb.princeton.edu/perl/webwn?s=";

    public static String toLink(String word){
        return toLink(word, "");
    }

    public static String toLink(String word, String emphasize){
        return toLink(word, emphasize, EM_PRE, EM_SUF);
    }
    public static String toLink(String word, String emphasize, String emPre, String emSuf){
        switch (dictionary){
            case "ICIBA": return linkToICIBA(word, emphasize, emPre, emSuf);
            case "YOUDAO": return linkToYOUDAO(word, emphasize, emPre, emSuf);
            case "COLLINS": return linkToCOLLINS(word, emphasize, emPre, emSuf);
            case "WEBSTER": return linkToWEBSTER(word, emphasize, emPre, emSuf);
            case "OXFORD": return linkToOXFORD(word, emphasize, emPre, emSuf);
            case "CAMBRIDGE": return linkToCAMBRIDGE(word, emphasize, emPre, emSuf);
            case "MACMILLAN": return linkToMACMILLAN(word, emphasize, emPre, emSuf);
            case "HERITAGE": return linkToHERITAGE(word, emphasize, emPre, emSuf);
            case "WIKTIONARY": return linkToWIKTIONARY(word, emphasize, emPre, emSuf);
            case "WORDNET": return linkToWORDNET(word, emphasize, emPre, emSuf);
        }
        //default
        return linkToICIBA(word, emphasize, emPre, emSuf);
    }

    public static String linkToICIBA(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, ICIBA);
    }
    public static String linkToYOUDAO(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, YOUDAO);
    }
    public static String linkToCOLLINS(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, COLLINS);
    }
    public static String linkToWEBSTER(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WEBSTER);
    }
    public static String linkToOXFORD(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, OXFORD);
    }
    public static String linkToCAMBRIDGE(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, CAMBRIDGE);
    }
    public static String linkToMACMILLAN(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, MACMILLAN);
    }
    public static String linkToHERITAGE(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, HERITAGE);
    }
    public static String linkToWIKTIONARY(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WIKTIONARY);
    }
    public static String linkToWORDNET(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WORDNET);
    }
    public static String linkTo(String word, String emphasize, String emPre, String emSuf, String webSite){
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
                word = word.replaceAll(target, emPre+target+emSuf);
            }
        }
        html.append(word).append("</a>");
        return html.toString();
    }

    public static void main(String[] args) {
        String word = "fabulous";
        dictionary = "ICIBA";
        System.out.println(toLink(word));
        dictionary = "YOUDAO";
        System.out.println(toLink(word));
        dictionary = "COLLINS";
        System.out.println(toLink(word));
        dictionary = "WEBSTER";
        System.out.println(toLink(word));
        dictionary = "OXFORD";
        System.out.println(toLink(word));
        dictionary = "CAMBRIDGE";
        System.out.println(toLink(word));
        dictionary = "MACMILLAN";
        System.out.println(toLink(word));
        dictionary = "HERITAGE";
        System.out.println(toLink(word));

    }
}
