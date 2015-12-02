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

    private static final String ICIBA_CSS_PATH = "ul.base-list li";
    //使用 | 分割多个CSSPATH, 如果第一个CSSPATH未提取到内容, 则使用第二个, 以此类推
    private static final String YOUDAO_CSS_PATH = "div#phrsListTab.trans-wrapper.clearfix div.trans-container ul li" +
            " | div.trans-container ul p.wordGroup";
    private static final String COLLINS_CSS_PATH = "html body div#wrapper div.content.english div.dictionary div.definition_wrapper.english div.definition_main div.definition_content.col.main_bar";
    private static final String WEBSTER_CSS_PATH = "html body div.body_container div.upper_content_container div.left_content_well div.main_content_area div#wordclick.wordclick div.border-top div.border-left div.border-right div.border-bottom div.corner-top-left div.corner-top-right div.corner-bottom-left div.corner-bottom-right div#mwEntryData";
    private static final String OXFORD_CSS_PATH = "div.entryPageContent";
    private static final String CAMBRIDGE_CSS_PATH = "html body div.wrapper.responsive_container div.cdo-dblclick-area div.responsive_row div.responsive_cell_center div.cdo-section div#entryContent.entrybox.english";
    private static final String MACMILLAN_CSS_PATH = "html body div.responsive_container div.responsive_row div#rightcol.responsive_cell_center_plus_right div#contentpanel div#entryContent div.responsive_cell_center_plus_right div.HOMOGRAPH";
    private static final String HERITAGE_CSS_PATH = "html body div#content.container div.container3 div#results table tbody tr td div.pseg div.ds-list";
    private static final String WIKTIONARY_CSS_PATH = "html body div#content.mw-body div#bodyContent.mw-body-content div#mw-content-text.mw-content-ltr";
    private static final String WORDNET_CSS_PATH = "html body div.form";
    private static final String RANDOMHOUSE_CSS_PATH = "html body div.content-container.main-area div.row div.center-well-container section#source-luna.source-wrapper.source-luna.is-pm-btn-show.pm-btn-spot div.source-box.oneClick-area section.luna-box div.source-data div.def-list section.def-pbk.ce-spot div.def-set div.def-content";

    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    public static String getDefinitionString(Dictionary dictionary, String word, String joinString){
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
        return parseDefinition(WordLinker.ICIBA + word, ICIBA_CSS_PATH, word, "ICIBA");
    }
    public static List<String> getDefinitionForYOUDAO(String word){
        return parseDefinition(WordLinker.YOUDAO + word, YOUDAO_CSS_PATH, word, "YOUDAO");
    }
    public static List<String> getDefinitionForCOLLINS(String word){
        return parseDefinition(WordLinker.COLLINS + word, COLLINS_CSS_PATH, word, "COLLINS");
    }
    public static List<String> getDefinitionForWEBSTER(String word){
        return parseDefinition(WordLinker.WEBSTER + word, WEBSTER_CSS_PATH, word, "WEBSTER");
    }
    public static List<String> getDefinitionForOXFORD(String word){
        return parseDefinition(WordLinker.OXFORD + word, OXFORD_CSS_PATH, word, "OXFORD");
    }
    public static List<String> getDefinitionForCAMBRIDGE(String word){
        return parseDefinition(WordLinker.CAMBRIDGE + word, CAMBRIDGE_CSS_PATH, word, "CAMBRIDGE");
    }
    public static List<String> getDefinitionForMACMILLAN(String word){
        return parseDefinition(WordLinker.MACMILLAN + word, MACMILLAN_CSS_PATH, word, "MACMILLAN");
    }
    public static List<String> getDefinitionForHERITAGE(String word){
        return parseDefinition(WordLinker.HERITAGE + word, HERITAGE_CSS_PATH, word, "HERITAGE");
    }
    public static List<String> getDefinitionForWIKTIONARY(String word){
        return parseDefinition(WordLinker.WIKTIONARY + word, WIKTIONARY_CSS_PATH, word, "WIKTIONARY");
    }
    public static List<String> getDefinitionForWORDNET(String word){
        return parseDefinition(WordLinker.WORDNET + word, WORDNET_CSS_PATH, word, "WORDNET");
    }
    public static List<String> getDefinitionForRANDOMHOUSE(String word){
        return parseDefinition(WordLinker.RANDOMHOUSE + word, RANDOMHOUSE_CSS_PATH, word, "RANDOMHOUSE");
    }

    public static List<String> parseDefinition(String url, String cssPath, String word, String dictionary){
        String wordDefinition = MySQLUtils.getWordDefinition(word, dictionary);
        if(StringUtils.isNotBlank(wordDefinition)){
            return Arrays.asList(wordDefinition.split("<br/>"));
        }

        List<String> list = new ArrayList<>();
        try {
            String html = getContent(url);
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
            LOGGER.error("解析定义出错：" + url, e);
        }
        if(!list.isEmpty()){
            MySQLUtils.saveWordDefinition(word, dictionary, concat(list, "<br/>"));
        }
        return list;
    }

    public static String getContent(String url) {
        String html = _getContent(url);
        int times = 0;
        while(html.contains("非常抱歉，来自您ip的请求异常频繁") || StringUtils.isBlank(html)){
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
}
