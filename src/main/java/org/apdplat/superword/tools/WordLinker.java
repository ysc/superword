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

import java.util.*;
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
 * RANDOMHOUSE:RandomHouse
 * @author 杨尚川
 */
public class WordLinker {
    private WordLinker(){}

    //是否使用服务器端做链接跳转
    //服务器端跳转可以记录用户的查词记录
    //如果是需要生成HTML代码片段贴到博客中
    //则不能使用服务器端调整
    //将此值设置为null
    public static final String SERVER_REDIRECT_VALUE = "/common/server-redirect.jspx";
    public static String serverRedirect = SERVER_REDIRECT_VALUE;
    public static boolean jsDefinition = true;

    public static final String EM_PRE = "<span style=\"color:red\">";
    public static final String EM_SUF = "</span>";
    public static final String ICIBA = "http://www.iciba.com/";
    public static final String YOUDAO = "http://dict.youdao.com/search?q=";
    public static final String COLLINS = "http://www.collinsdictionary.com/dictionary/english/";
    public static final String WEBSTER = "http://beta.merriam-webster.com/dictionary/";
    public static final String OXFORD = "http://www.oxforddictionaries.com/definition/english/";
    public static final String CAMBRIDGE = "http://dictionary.cambridge.org/dictionary/english/";
    public static final String MACMILLAN = "http://www.macmillandictionary.com/dictionary/british/";
    public static final String HERITAGE = "https://www.ahdictionary.com/word/search.html?q=";
    public static final String WIKTIONARY = "https://en.wiktionary.org/wiki/";
    public static final String WORDNET = "http://wordnetweb.princeton.edu/perl/webwn?s=";
    public static final String RANDOMHOUSE = "http://dictionary.reference.com/browse/";

    public static String getLink(Dictionary dictionary, String word){
        return getLinkPrefix(dictionary)+word;
    }

    public static String getLinkPrefix(Dictionary dictionary){
        switch (dictionary){
            case ICIBA: return ICIBA;
            case YOUDAO: return YOUDAO;
            case COLLINS: return COLLINS;
            case WEBSTER: return WEBSTER;
            case OXFORD: return OXFORD;
            case CAMBRIDGE: return CAMBRIDGE;
            case MACMILLAN: return MACMILLAN;
            case HERITAGE: return HERITAGE;
            case WIKTIONARY: return WIKTIONARY;
            case WORDNET: return WORDNET;
            case RANDOMHOUSE: return RANDOMHOUSE;
        }
        return ICIBA;
    }

    public static Dictionary getValidDictionary(String dictionary){
        try{
            return Dictionary.valueOf(dictionary);
        }catch (Exception e){}
        return Dictionary.ICIBA;
    }

    public static enum Dictionary{
        ICIBA("iCIBA"), YOUDAO("Youdao"), COLLINS("Collins"), WEBSTER("Webster's"), OXFORD("Oxford"),
        CAMBRIDGE("Cambridge"), MACMILLAN("Macmillan"), HERITAGE("Heritage"), WIKTIONARY("Wiktionary"),
        WORDNET("WordNet"), RANDOMHOUSE("RandomHouse");
        private String des;
        private Dictionary(String des){
            this.des = des;
        }
        public String getDes(){
            return des;
        }
    }

    public static String toLink(String word){
        return toLink(word, Dictionary.ICIBA);
    }

    public static String toLink(String word, Dictionary dictionary){
        return toLink(word, "", dictionary);
    }

    public static String toLink(String word, String emphasize){
        return toLink(word, emphasize, Dictionary.ICIBA);
    }

    public static String toLink(String word, String emphasize, Dictionary dictionary){
        return toLink(word, emphasize, EM_PRE, EM_SUF, dictionary);
    }
    public static String toLink(String word, String emphasize, String emPre, String emSuf){
        return toLink(word, emphasize, emPre, emSuf, Dictionary.ICIBA);
    }
    public static String toLink(String word, String emphasize, String emPre, String emSuf, Dictionary dictionary){
        if(dictionary == null){
            dictionary = Dictionary.ICIBA;
        }
        switch (dictionary){
            case ICIBA: return linkToICIBA(word, emphasize, emPre, emSuf);
            case YOUDAO: return linkToYOUDAO(word, emphasize, emPre, emSuf);
            case COLLINS: return linkToCOLLINS(word, emphasize, emPre, emSuf);
            case WEBSTER: return linkToWEBSTER(word, emphasize, emPre, emSuf);
            case OXFORD: return linkToOXFORD(word, emphasize, emPre, emSuf);
            case CAMBRIDGE: return linkToCAMBRIDGE(word, emphasize, emPre, emSuf);
            case MACMILLAN: return linkToMACMILLAN(word, emphasize, emPre, emSuf);
            case HERITAGE: return linkToHERITAGE(word, emphasize, emPre, emSuf);
            case WIKTIONARY: return linkToWIKTIONARY(word, emphasize, emPre, emSuf);
            case WORDNET: return linkToWORDNET(word, emphasize, emPre, emSuf);
            case RANDOMHOUSE: return linkToRANDOMHOUSE(word, emphasize, emPre, emSuf);
        }
        //default
        return linkToICIBA(word, emphasize, emPre, emSuf);
    }

    public static String linkToICIBA(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, ICIBA, Dictionary.ICIBA);
    }
    public static String linkToYOUDAO(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, YOUDAO, Dictionary.YOUDAO);
    }
    public static String linkToCOLLINS(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, COLLINS, Dictionary.COLLINS);
    }
    public static String linkToWEBSTER(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WEBSTER, Dictionary.WEBSTER);
    }
    public static String linkToOXFORD(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, OXFORD, Dictionary.OXFORD);
    }
    public static String linkToCAMBRIDGE(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, CAMBRIDGE, Dictionary.CAMBRIDGE);
    }
    public static String linkToMACMILLAN(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, MACMILLAN, Dictionary.MACMILLAN);
    }
    public static String linkToHERITAGE(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, HERITAGE, Dictionary.HERITAGE);
    }
    public static String linkToWIKTIONARY(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WIKTIONARY, Dictionary.WIKTIONARY);
    }
    public static String linkToWORDNET(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, WORDNET, Dictionary.WORDNET);
    }
    public static String linkToRANDOMHOUSE(String word, String emphasize, String emPre, String emSuf){
        return linkTo(word, emphasize, emPre, emSuf, RANDOMHOUSE, Dictionary.RANDOMHOUSE);
    }
    public static String linkTo(String word, String emphasize, String emPre, String emSuf, String webSite, Dictionary dictionary){
        StringBuilder p = new StringBuilder();
        for (char c : emphasize.toCharArray()) {
            p.append("[")
                    .append(Character.toUpperCase(c))
                    .append(Character.toLowerCase(c))
                    .append("]{1}");
        }
        Pattern pattern = Pattern.compile(p.toString());
        StringBuilder html = new StringBuilder();
        String url = webSite+word;
        if(serverRedirect != null){
            url = serverRedirect+"?url="+url+"&word="+word+"&dict="+dictionary.name();
        }
        if(jsDefinition){
            html.append("<a href=\"#")
                    .append(UUID.randomUUID())
                    .append("\" onclick=\"queryWord('")
                    .append(word)
                    .append("');\">");

        }else {
            html.append("<a target=\"_blank\" href=\"")
                    .append(url)
                    .append("\">");
        }
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
        Dictionary dictionary = Dictionary.ICIBA;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.YOUDAO;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.COLLINS;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.WEBSTER;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.OXFORD;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.CAMBRIDGE;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.MACMILLAN;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.HERITAGE;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.WIKTIONARY;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.WORDNET;
        System.out.println(toLink(word, dictionary));
        dictionary = Dictionary.RANDOMHOUSE;
        System.out.println(toLink(word, dictionary));
    }
}
