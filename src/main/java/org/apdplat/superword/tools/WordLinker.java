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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    public static String serverRedirect = "common/server-redirect.jsp";

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
    private static final String RANDOMHOUSE = "http://dictionary.reference.com/browse/";

    private static final int timeout = 60000;
    private static final String ICIBA_CSS_PATH = "html body div.main-container div.main-result div.result-info";
    private static final String YOUDAO_CSS_PATH = "html body div#doc div#scontainer div#container div#results";
    private static final String COLLINS_CSS_PATH = "html body div#wrapper div.content.english div.dictionary div.definition_wrapper.english div.definition_main div.definition_content.col.main_bar";
    private static final String WEBSTER_CSS_PATH = "html body div.body_container div.upper_content_container div.left_content_well div.main_content_area div#wordclick.wordclick div.border-top div.border-left div.border-right div.border-bottom div.corner-top-left div.corner-top-right div.corner-bottom-left div.corner-bottom-right div#mwEntryData";
    private static final String OXFORD_CSS_PATH = "div.entryPageContent";
    private static final String CAMBRIDGE_CSS_PATH = "html body div.wrapper.responsive_container div.cdo-dblclick-area div.responsive_row div.responsive_cell_center div.cdo-section div#entryContent.entrybox.english";
    private static final String MACMILLAN_CSS_PATH = "html body div.responsive_container div.responsive_row div#rightcol.responsive_cell_center_plus_right div#contentpanel div#entryContent div.responsive_cell_center_plus_right div.HOMOGRAPH";
    private static final String HERITAGE_CSS_PATH = "html body div#content.container div.container3 div#results table tbody tr td div.pseg div.ds-list";
    private static final String WIKTIONARY_CSS_PATH = "html body div#content.mw-body div#bodyContent.mw-body-content div#mw-content-text.mw-content-ltr";
    private static final String WORDNET_CSS_PATH = "html body div.form";
    private static final String RANDOMHOUSE_CSS_PATH = "html body div.content-container.main-area div.row div.center-well-container section#source-luna.source-wrapper.source-luna.is-pm-btn-show.pm-btn-spot div.source-box.oneClick-area section.luna-box div.source-data div.def-list section.def-pbk.ce-spot div.def-set div.def-content";

    public static List<String> getDefinition(Dictionary dictionary, String word){
        switch (dictionary){
            case ICIBA: return getDefinitionForICIBA(word);
            case YOUDAO: return getDefinitionForYOUDAO(word);
            case COLLINS: return getDefinitionForCOLLINS(word);
            case WEBSTER: return getDefinitionForWEBSTER(word);
            case OXFORD: return getDefinitionForOXFORD(word);
            case CAMBRIDGE: return getDefinitionForCAMBRIDGE(word);
            case MACMILLAN: return getDefinitionForMACMILLAN(word);
            case HERITAGE: return getDefinitionForHERITAGE(word);
            case WIKTIONARY: return getDefinitionForWIKTIONARY(word);
            case WORDNET: return getDefinitionForWORDNET(word);
            case RANDOMHOUSE: return getDefinitionForRANDOMHOUSE(word);
        }
        return getDefinitionForICIBA(word);
    }

    public static List<String> getDefinitionForICIBA(String word){
        return parseDefinition(ICIBA + word, ICIBA_CSS_PATH);
    }
    public static List<String> getDefinitionForYOUDAO(String word){
        return parseDefinition(YOUDAO + word, YOUDAO_CSS_PATH);
    }
    public static List<String> getDefinitionForCOLLINS(String word){
        return parseDefinition(COLLINS + word, COLLINS_CSS_PATH);
    }
    public static List<String> getDefinitionForWEBSTER(String word){
        return parseDefinition(WEBSTER + word, WEBSTER_CSS_PATH);
    }
    public static List<String> getDefinitionForOXFORD(String word){
        return parseDefinition(OXFORD + word, OXFORD_CSS_PATH);
    }
    public static List<String> getDefinitionForCAMBRIDGE(String word){
        return parseDefinition(CAMBRIDGE + word, CAMBRIDGE_CSS_PATH);
    }
    public static List<String> getDefinitionForMACMILLAN(String word){
        return parseDefinition(MACMILLAN + word, MACMILLAN_CSS_PATH);
    }
    public static List<String> getDefinitionForHERITAGE(String word){
        return parseDefinition(HERITAGE + word, HERITAGE_CSS_PATH);
    }
    public static List<String> getDefinitionForWIKTIONARY(String word){
        return parseDefinition(WIKTIONARY + word, WIKTIONARY_CSS_PATH);
    }
    public static List<String> getDefinitionForWORDNET(String word){
        return parseDefinition(WORDNET + word, WORDNET_CSS_PATH);
    }
    public static List<String> getDefinitionForRANDOMHOUSE(String word){
        return parseDefinition(RANDOMHOUSE + word, RANDOMHOUSE_CSS_PATH);
    }

    public static List<String> parseDefinition(String url, String csspath){
        List<String> list = new ArrayList<>();
        try {
            for(Element element : Jsoup.parse(new URL(url), timeout).select(csspath)){
                String definition = element.html();
                if(StringUtils.isNotBlank(definition)){
                    list.add(definition);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static Dictionary getValidDictionary(String dictionary){
        try{
            return Dictionary.valueOf(dictionary);
        }catch (Exception e){}
        return Dictionary.ICIBA;
    }
    public static enum Dictionary{
        ICIBA, YOUDAO, COLLINS, WEBSTER, OXFORD, CAMBRIDGE, MACMILLAN, HERITAGE, WIKTIONARY, WORDNET, RANDOMHOUSE
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
        html.append("<a target=\"_blank\" href=\"")
                .append(url)
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
