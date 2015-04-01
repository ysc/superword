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
import org.apdplat.superword.model.Word;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 更新词库工具
 * @author 杨尚川
 */
public class WordsFetcher {
    private WordsFetcher(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(WordsFetcher.class);

    private static final String WORD_CSS_PATH = "html body div#main_block div.word_box form#word_form div.word_main ul li div.word_main_list_w span";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.iciba.com";
    private static final String REFERER = "http://www.iciba.com/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    /**
     * 小学
     */
    public static void updatePrimarySchool(){
        //小学牛津版
        update(63, 5, "/word_primary_school.txt");
        update(64, 3, "/word_primary_school.txt");
        update(65, 3, "/word_primary_school.txt");
        update(66, 3, "/word_primary_school.txt");
        update(67, 4, "/word_primary_school.txt");
        update(68, 4, "/word_primary_school.txt");
        update(69, 4, "/word_primary_school.txt");
        update(70, 5, "/word_primary_school.txt");
        update(71, 5, "/word_primary_school.txt");
        update(72, 4, "/word_primary_school.txt");
        update(73, 14, "/word_primary_school.txt");
        update(74, 10, "/word_primary_school.txt");
        //小学深圳版
        update(655, 8, "/word_primary_school.txt");
        update(656, 8, "/word_primary_school.txt");
        update(657, 2, "/word_primary_school.txt");
        update(658, 3, "/word_primary_school.txt");
        update(149, 3, "/word_primary_school.txt");
        update(150, 4, "/word_primary_school.txt");
        update(151, 3, "/word_primary_school.txt");
        update(152, 4, "/word_primary_school.txt");
        update(154, 6, "/word_primary_school.txt");
        update(155, 8, "/word_primary_school.txt");
        update(156, 8, "/word_primary_school.txt");
        //河北版小学英语
        update(265, 2, "/word_primary_school.txt");
        update(266, 3, "/word_primary_school.txt");
        update(267, 1, "/word_primary_school.txt");
        update(268, 2, "/word_primary_school.txt");
        update(269, 1, "/word_primary_school.txt");
        update(271, 2, "/word_primary_school.txt");
        update(272, 3, "/word_primary_school.txt");
    }
    /**
     * 初中
     */
    public static void updateJuniorSchool(){
        //初中牛津版
        update(57, 27, "/word_junior_school.txt");
        update(58, 24, "/word_junior_school.txt");
        update(59, 21, "/word_junior_school.txt");
        update(60, 15, "/word_junior_school.txt");
        update(61, 20, "/word_junior_school.txt");
        update(62, 16, "/word_junior_school.txt");
        //初中人教版
        update(105, 30, "/word_junior_school.txt");
        update(106, 20, "/word_junior_school.txt");
        update(107, 28, "/word_junior_school.txt");
        update(108, 25, "/word_junior_school.txt");
        update(109, 37, "/word_junior_school.txt");
        //仁爱版
        update(221, 19, "/word_junior_school.txt");
        update(222, 19, "/word_junior_school.txt");
        update(223, 18, "/word_junior_school.txt");
        update(224, 17, "/word_junior_school.txt");
        update(225, 12, "/word_junior_school.txt");
        update(226, 8, "/word_junior_school.txt");
        //初中河北版
        update(273, 20, "/word_junior_school.txt");
        update(224, 18, "/word_junior_school.txt");
        update(226, 16, "/word_junior_school.txt");
        update(227, 16, "/word_junior_school.txt");
        update(228, 12, "/word_junior_school.txt");
        update(229, 14, "/word_junior_school.txt");
        //新初中人教版
        update(728, 18, "/word_junior_school.txt");
        update(729, 25, "/word_junior_school.txt");
        //翼教版
        update(678, 17, "/word_junior_school.txt");
    }
    /**
     * 高中
     */
    public static void updateSeniorSchool(){
        //高中牛津版
        update(51, 19, "/word_senior_school.txt");
        update(52, 25, "/word_senior_school.txt");
        update(53, 24, "/word_senior_school.txt");
        update(54, 20, "/word_senior_school.txt");
        update(55, 25, "/word_senior_school.txt");
        update(56, 23, "/word_senior_school.txt");
        //高中人教版
        update(110, 14, "/word_senior_school.txt");
        update(111, 14, "/word_senior_school.txt");
        update(112, 19, "/word_senior_school.txt");
        update(113, 15, "/word_senior_school.txt");
        update(114, 18, "/word_senior_school.txt");
        update(118, 20, "/word_senior_school.txt");
        update(119, 19, "/word_senior_school.txt");
        //高考
        update(139, 5, "/word_senior_school.txt");
        update(140, 194, "/word_senior_school.txt");
    }
    /**
     * 大学
     */
    public static void updateUniversity() {
        //大学英语精读
        update(45, 27, "/word_university.txt");
        update(46, 37, "/word_university.txt");
        update(47, 40, "/word_university.txt");
        update(48, 46, "/word_university.txt");
        update(49, 25, "/word_university.txt");
        update(50, 65, "/word_university.txt");
    }
    /**
     * 新概念英语
     */
    public static void updateNewConception() {
        update(41, 41, "/word_new_conception.txt");
        update(42, 49, "/word_new_conception.txt");
        update(43, 81, "/word_new_conception.txt");
        update(44, 76, "/word_new_conception.txt");
    }
    public static void updateCET4(){
        update(11, 226, "/word_CET4.txt");
        update(122, 35, "/word_CET4.txt");
    }
    public static void updateCET6(){
        update(12, 105, "/word_CET6.txt");
        update(123, 25, "/word_CET6.txt");
    }
    public static void updateKY(){
        update(13, 274, "/word_考 研.txt");
        update(143, 3, "/word_考 研.txt");
    }
    public static void updateTOEFL(){
        update(14, 245, "/word_TOEFL.txt");
    }
    public static void updateIELTS(){
        update(15, 228, "/word_IELTS.txt");
    }
    public static void updateGRE(){
        update(16, 375, "/word_GRE.txt");
    }
    public static void updateGMAT(){
        update(36, 40, "/word_GMAT.txt");
        update(37, 54, "/word_GMAT.txt");
        update(38, 108, "/word_GMAT.txt");
    }
    public static void updateTOEIC(){
        update(682, 42, "/word_TOEIC.txt");
    }
    public static void updateSAT(){
        update(121, 11, "/word_SAT.txt");
    }
    public static void updateBEC(){
        update(680, 47, "/word_BEC.txt");
        update(681, 10, "/word_BEC.txt");
    }
    public static void updateADULT(){
        update(703, 144, "/word_ADULT.txt");
        update(704, 284, "/word_ADULT.txt");
        update(705, 143, "/word_ADULT.txt");
        update(706, 11, "/word_ADULT.txt");
        update(707, 198, "/word_ADULT.txt");
        update(708, 171, "/word_ADULT.txt");
        update(709, 89, "/word_ADULT.txt");
        update(710, 61, "/word_ADULT.txt");
        update(711, 180, "/word_ADULT.txt");
    }
    public static void updateMBA(){
        update(39, 243, "/word_MBA.txt");
    }
    public static void updateTEM4(){
        update(90, 105, "/word_TEM4.txt");
    }
    public static void updateTEM8(){
        update(91, 47, "/word_TEM8.txt");
    }
    public static void updateCATTI(){
        update(715, 70, "/word_CATTI.txt");
        update(716, 35, "/word_CATTI.txt");
        update(717, 94, "/word_CATTI.txt");
    }

    /**
     * 计算机常用词汇
     */
    public static void updateComputer() {
        update(78, 191, "/word_computer.txt");
    }

    /**
     * 其他
     */
    public static void updateOther() {
        //医学
        update(75, 58, "/words.txt");
        update(76, 46, "/words.txt");
        update(77, 27, "/words.txt");
        //金融
        update(79, 118, "/words.txt");
        //交友
        update(80, 18, "/words.txt");
        //求职
        update(81, 11, "/words.txt");
        //人力资源
        update(97, 34, "/words.txt");
        //人力资源
        update(98, 14, "/words.txt");
        //建筑
        update(147, 92, "/words.txt");
        //化学（高分子）
        update(721, 17, "/words.txt");
        //有用的单词
        update(712, 3, "/words.txt");
        update(713, 163, "/words.txt");
        //美国英语
        update(363, 29, "/words.txt");
        update(364, 25, "/words.txt");
        update(365, 46, "/words.txt");
        update(366, 50, "/words.txt");
        update(355, 31, "/words.txt");
        //基础词汇
        update(362, 59, "/words.txt");
        //柯林斯和牛津
        update(361, 54, "/words.txt");
        update(358, 55, "/words.txt");
        update(359, 33, "/words.txt");
        update(293, 49, "/words.txt");
        update(125, 24, "/words.txt");
        update(125, 24, "/words.txt");
        update(126, 42, "/words.txt");
        update(127, 60, "/words.txt");
        update(128, 109, "/words.txt");
        update(129, 212, "/words.txt");
        update(294, 53, "/words.txt");
        update(725, 122, "/words.txt");
        //其他
        update(720, 7, "/words.txt");
        update(726, 3, "/words.txt");
        update(676, 19, "/words.txt");
        update(175, 26, "/words.txt");
        update(144, 13, "/words.txt");
        update(145, 19, "/words.txt");
        update(146, 11, "/words.txt");
        update(99, 12, "/words.txt");
        update(87, 2, "/words.txt");
        update(83, 7, "/words.txt");
        update(84, 11, "/words.txt");
        update(85, 6, "/words.txt");
        update(86, 11, "/words.txt");
        update(153, 13, "/words.txt");
    }

    public static void update(int type, int pageNumber, String file){
        file = "src/main/resources"+file;
        Set<Word> existWords = WordSources.get(file);
        Set<Word> words = fetch(type, pageNumber);
        LOGGER.debug("已经存在的词数："+existWords.size());
        LOGGER.debug("新获取到的词数："+words.size());
        words.addAll(existWords);
        LOGGER.debug("新旧合并之后的词数："+words.size());
        AtomicInteger i = new AtomicInteger();
        List<String> allWords = words
                .stream()
                .sorted()
                .map(w -> i.incrementAndGet() + "\t" + w.getWord())
                .collect(Collectors.toList());
        try{
            Files.write(Paths.get(file), allWords);
        }catch (Exception e){
            LOGGER.error("保存词汇失败", e);
        }
    }
    public static Set<Word> fetch(int type, int pageNumber){
        Set<Word> words = new HashSet<>();
        String url = "http://word.iciba.com/?action=words&class="+type+"&course=";
        for (int i=1; i<=pageNumber; i++){
            String html = getContent(url+i);
            int times = 1;
            while (StringUtils.isBlank(html) && times<4){
                times++;
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(url+i);
            }
            //LOGGER.debug("获取到的HTML：" +html);
            while(html.contains("非常抱歉，来自您ip的请求异常频繁")){
                //使用新的IP地址
                DynamicIp.toNewIp();
                html = getContent(url+i);
            }
            words.addAll(parse(html));
        }
        LOGGER.debug("url:"+url+"，获取到的词数："+words.size());
        return words;
    }
    public static Set<Word> parse(String html){
        Set<Word> words = new HashSet<>();
        try {
            for(Element element : Jsoup.parse(html).select(WORD_CSS_PATH)){
                String word = element.text().trim();
                if(StringUtils.isNotBlank(word)
                        && WordSources.isEnglish(word)){
                    words.add(new Word(word, ""));
                    LOGGER.debug("解析出单词:" + word);
                }
            }
        }catch (Exception e){
            LOGGER.error("解析单词出错", e);
        }
        return words;
    }
    public static String getContent(String url) {
        LOGGER.debug("url:"+url);
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
            LOGGER.error("获取URL："+url+"页面出错", e);
        }
        return html;
    }

    public static void main(String[] args) {
        updatePrimarySchool();
        updateJuniorSchool();
        updateSeniorSchool();
        updateUniversity();
        updateNewConception();
        updateCET4();
        updateCET6();
        updateKY();
        updateTOEFL();
        updateIELTS();
        updateGRE();
        updateGMAT();
        updateTOEIC();
        updateSAT();
        updateBEC();
        updateADULT();
        updateMBA();
        updateTEM4();
        updateTEM8();
        updateCATTI();

        updateComputer();
        updateOther();
    }

}
