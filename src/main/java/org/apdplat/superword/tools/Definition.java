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

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apdplat.superword.tools.WordLinker.Dictionary;

/**
 * Created by ysc on 12/2/15.
 */
public class Definition {
    private static final Logger LOGGER = LoggerFactory.getLogger(Definition.class);

    public static final String ICIBA_CSS_PATH = "ul.base-list li";
    //使用 | 分割多个CSSPATH, 如果第一个CSSPATH未提取到内容, 则使用第二个, 以此类推
    public static final String YOUDAO_CSS_PATH = "div#phrsListTab.trans-wrapper.clearfix div.trans-container ul li" +
            " | div.trans-container ul p.wordGroup";
    public static final String COLLINS_CSS_PATH = "html body div#wrapper div.content.english div.dictionary div.definition_wrapper.english div.definition_main div.definition_content.col.main_bar";
    public static final String CAMBRIDGE_CSS_PATH = "html body div.wrapper.responsive_container div.cdo-dblclick-area div.responsive_row div.responsive_cell_center div.cdo-section div#entryContent.entrybox.english";
    public static final String MACMILLAN_CSS_PATH = "html body div.responsive_container div.responsive_row div#rightcol.responsive_cell_center_plus_right div#contentpanel div#entryContent div.responsive_cell_center_plus_right div.HOMOGRAPH";
    public static final String HERITAGE_CSS_PATH = "html body div#content.container div.container3 div#results table tbody tr td div.pseg div.ds-list";
    public static final String WIKTIONARY_CSS_PATH = "html body div#content.mw-body div#bodyContent.mw-body-content div#mw-content-text.mw-content-ltr";
    public static final String WORDNET_CSS_PATH = "html body div.form";
    public static final String RANDOMHOUSE_CSS_PATH = "html body div.content-container.main-area div.row div.center-well-container section#source-luna.source-wrapper.source-luna.is-pm-btn-show.pm-btn-spot div.source-box.oneClick-area section.luna-box div.source-data div.def-list section.def-pbk.ce-spot div.def-set div.def-content";

    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    public static String getDefinitionString(Dictionary dictionary, String word, String joinString) {
        return concat(getDefinition(dictionary, word), joinString);
    }

    public static String concat(List<String> list, String joinString){
        if(list.isEmpty()){
            return "";
        }
        StringBuilder string = new StringBuilder();
        list.forEach(d -> string.append(d).append(joinString));
        int len = string.length()-joinString.length();
        if(len < 1){
            return "";
        }
        string.setLength(len);
        return string.toString();
    }

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
        return parseDefinition(WordLinker.ICIBA + word, ICIBA_CSS_PATH, word, Dictionary.ICIBA);
    }
    public static List<String> getDefinitionForYOUDAO(String word){
        return parseDefinition(WordLinker.YOUDAO + word, YOUDAO_CSS_PATH, word, Dictionary.YOUDAO);
    }
    public static List<String> getDefinitionForCOLLINS(String word){
        return parseDefinition(WordLinker.COLLINS + word, COLLINS_CSS_PATH, word, Dictionary.COLLINS);
    }
    public static List<String> getDefinitionForWEBSTER(String word){
        return parseDefinition(WordLinker.WEBSTER + word, null, word, Dictionary.WEBSTER);
    }
    public static List<String> getDefinitionForOXFORD(String word){
        return parseDefinition(WordLinker.OXFORD + word, null, word, Dictionary.OXFORD);
    }
    public static List<String> getDefinitionForCAMBRIDGE(String word){
        return parseDefinition(WordLinker.CAMBRIDGE + word, CAMBRIDGE_CSS_PATH, word, Dictionary.CAMBRIDGE);
    }
    public static List<String> getDefinitionForMACMILLAN(String word){
        return parseDefinition(WordLinker.MACMILLAN + word, MACMILLAN_CSS_PATH, word, Dictionary.MACMILLAN);
    }
    public static List<String> getDefinitionForHERITAGE(String word){
        return parseDefinition(WordLinker.HERITAGE + word, HERITAGE_CSS_PATH, word, Dictionary.HERITAGE);
    }
    public static List<String> getDefinitionForWIKTIONARY(String word){
        return parseDefinition(WordLinker.WIKTIONARY + word, WIKTIONARY_CSS_PATH, word, Dictionary.WIKTIONARY);
    }
    public static List<String> getDefinitionForWORDNET(String word){
        return parseDefinition(WordLinker.WORDNET + word, WORDNET_CSS_PATH, word, Dictionary.WORDNET);
    }
    public static List<String> getDefinitionForRANDOMHOUSE(String word){
        return parseDefinition(WordLinker.RANDOMHOUSE + word, RANDOMHOUSE_CSS_PATH, word, Dictionary.RANDOMHOUSE);
    }

    public static List<String> parseDefinition(String url, String cssPath, String word, Dictionary dictionary){
        String wordDefinition = MySQLUtils.getWordDefinition(word, dictionary.name());
        if(StringUtils.isNotBlank(wordDefinition)) {
            return Arrays.asList(wordDefinition.split("<br/>"));
        }
        String html = getContent(url);
        List<String> list = parseDefinitionFromHtml(html, cssPath, word, dictionary);
        if(!list.isEmpty()){
            MySQLUtils.saveWordDefinition(word, dictionary.name(), concat(list, "<br/>"));
        }
        return list;
    }

    public static List<String> parseDefinitionFromHtml(String html, String cssPath, String word, Dictionary dictionary){
        if(dictionary == Dictionary.OXFORD){
            return parseDefinitionForOxford(html, null);
        }
        if(dictionary == Dictionary.WEBSTER){
            return parseDefinitionForWebster(html, null);
        }
        List<String> list = new ArrayList<>();
        try {
            Document document = Jsoup.parse(html);
            for(String cp : cssPath.split("\\|")) {
                cp = cp.trim();
                if(StringUtils.isBlank(cp)){
                    continue;
                }
                for (Element element : document.select(cp)) {
                    String definition = element.text();
                    if (StringUtils.isNotBlank(definition)) {
                        definition = definition.trim();
                        if (!definition.startsWith("变形")) {
                            list.add(definition);
                        }
                    }
                }
                if(!list.isEmpty()){
                    break;
                }
            }
        } catch (Exception e){
            LOGGER.error("解析定义出错：" + word, e);
        }
        return list;
    }

    public static List<String> parseDefinitionForWebster(String html, String cssPath){
        List<String> list = new ArrayList<>();
        try {
            for (Element element : Jsoup.parse(html).select("div.tense-box.quick-def-box.simple-def-box.card-box.def-text div.inner-box-wrapper")) {
                StringBuilder definition = new StringBuilder();
                String partOfSpeech = element.select("div.word-attributes span.main-attr em").text().trim();
                for (Element defElement : element.select("div.definition-block.def-text ul.definition-list.no-count li p.definition-inner-item span")){
                    String def = defElement.text().trim();
                    if(def.length() < 3){
                        continue;
                    }
                    if(Character.isAlphabetic(def.charAt(0))){
                        def = ": " + def;
                    }else{
                        int index = 0;
                        while(!Character.isAlphabetic(def.charAt(++index))){
                            //
                        }
                        def = ": " + def.substring(index);
                    }
                    definition.append(partOfSpeech)
                            .append(" ")
                            .append(def);
                    list.add(definition.toString());
                    definition.setLength(0);
                }
            }
        } catch (Exception e){
            LOGGER.error("解析定义出错：", e);
        }
        return list;
    }

    public static List<String> parseDefinitionForOxford(String html, String cssPath){
        List<String> list = new ArrayList<>();
        try {
            for (Element element : Jsoup.parse(html).select("section.se1.senseGroup")) {
                StringBuilder definition = new StringBuilder();
                String partOfSpeech = element.select("span.partOfSpeech").text().trim();
                for (Element defElement : element.select("div.senseInnerWrapper")){
                    String seq = defElement.select("span.iteration").text().trim();
                    String def = defElement.select("span.definition").text().trim();
                    if(def.endsWith(":")){
                        def = def.substring(0, def.length()-1);
                    }
                    definition.append(partOfSpeech)
                            .append(" ")
                            .append(seq)
                            .append(" ")
                            .append(def);
                    list.add(definition.toString());
                    definition.setLength(0);
                }
            }
        } catch (Exception e){
            LOGGER.error("解析定义出错：", e);
        }
        return list;
    }

    public static String getContent(String url) {
        String html = _getContent(url);
        int times = 0;
        while(StringUtils.isNotBlank(html) && html.contains("非常抱歉，来自您ip的请求异常频繁")){
            //使用新的IP地址
            ProxyIp.toNewIp();
            html = _getContent(url);
            if(++times > 2){
                break;
            }
        }
        return html;
    }

    private static String _getContent(String url) {
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Referer", REFERER)
                .header("Host", HOST)
                .header("User-Agent", USER_AGENT)
                .timeout(60000)
                .ignoreContentType(true);
        String html = "";
        try {
            html = conn.post().html();
            html = html.replaceAll("[\n\r]", "");
        }catch (Exception e){
            LOGGER.error("获取URL：" + url + "页面出错", e);
        }
        return html;
    }

    public static void main(String[] args) {
        //getDefinitionForOXFORD("make").forEach(System.out::println);
        getDefinitionForWEBSTER("make").forEach(System.out::println);
    }
}
