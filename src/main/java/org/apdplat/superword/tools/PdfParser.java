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
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apdplat.superword.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 将pdf文档转换为txt文档
 * @author 杨尚川
 */
public class PdfParser {
    private PdfParser(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfParser.class);

    private static final int SENTENCE_WORD_MIN_COUNT = 10;
    private static final int MAX_WORD_CHAR_COUNT = 18;
    private static final float SENTENCE_CAP_WORD_MAX_RATE = 0.4f;
    private static final Set<String> punctuation = new HashSet<>();
    private static final Set<Character> CORRUPT_CHAR = new HashSet<>();
    private static final Set<Word> DICTIONARY = WordSources.getAll();

    static {
        punctuation.add(",");
        punctuation.add("’");
        punctuation.add("‐");
        punctuation.add("‑");
        punctuation.add("‒");
        punctuation.add("–");
        punctuation.add("—");
        punctuation.add("-");
        punctuation.add("―");
        punctuation.add(":");
        punctuation.add(";");
        punctuation.add("/");
        punctuation.add("+");
        punctuation.add("=");
        punctuation.add("==");
        punctuation.add("%");
        punctuation.add("!");
        punctuation.add("'");
        punctuation.add("\"");
        punctuation.add("[");
        punctuation.add("]");
        punctuation.add("(");
        punctuation.add(")");
        punctuation.add("“");
        punctuation.add("”");
        punctuation.add("?");
    }

    private static final Map<Integer, AtomicInteger> SENTENCE_LENGTH_INFO = new ConcurrentHashMap<>();

    /**
     * 将PDF文件解析为文本
     * @param file 本地PDF文件的相对路径或绝对路径
     * @return 提取的文本
     */
    public static String parsePdfFileToPlainText(String file) {
        try(InputStream stream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
            AutoDetectParser parser = new AutoDetectParser();
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata);
            return handler.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static void parseDirectory(String dir){
        parseDirectory(Paths.get(dir));
    }
    public static void parseDirectory(Path dir){
        try {
            LOGGER.info("处理目录：" + dir);
            List<String> fileNames = new ArrayList<>();
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = parseFile(file);
                    if(StringUtils.isNotBlank(fileName)) {
                        fileNames.add(fileName);
                    }
                    return FileVisitResult.CONTINUE;
                }

            });
            Files.write(Paths.get("src/main/resources/it/manifest"), fileNames);
            LOGGER.info("处理完毕");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static String parseFile(String file) {
        return parseFile(Paths.get(file));
    }
    public static String parseFile(Path file) {
        try {
            if(invalid(file)){
                return null;
            }
            String sourceName = file.toFile().getAbsolutePath();
            String targetName = prepareTarget(file);
            LOGGER.info("处理文件：" + sourceName);
            LOGGER.info("生成文件：" + targetName);
            //解析文本
            String text = parsePdfFileToPlainText(sourceName);
            //处理文本
            List<String> sentences = toSentence(text);
            //保存文本
            Files.write(Paths.get(targetName), sentences);
            return targetName.replace("src/main/resources", "");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static boolean paragraphFinish(String line){
        //遇到空白说明段落结束
        if(StringUtils.isBlank(line)){
            return true;
        }
        return false;
    }
    /**
     * 将文本分割为句子
     * @param text
     * @return
     */
    private static List<String> toSentence(String text){
        List<String> data = new ArrayList<>();
        StringBuilder paragraph = new StringBuilder();
        //将PDF解析出来的文本按行分割
        String[] lines = text.split("[\n\r]");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            //段落结束
            if (paragraphFinish(line)) {
                process(paragraph.toString().trim(), data);
                //重置
                paragraph.setLength(0);
            }
            LOGGER.debug("PDF"+(i+1)+"行：" + line);
            //移除行间连接符-
            while (line.endsWith("-")
                    || line.endsWith("‐")
                    || line.endsWith("‑")
                    || line.endsWith("‒")
                    || line.endsWith("–")
                    || line.endsWith("—")
                    || line.endsWith("―")) {
                LOGGER.debug("发现行被折断");
                if ((i + 1) < lines.length) {
                    //去除行末的-
                    String pre = line.substring(0, line.length() - 1);
                    //获取下一行
                    String n = lines[i+1].trim();
                    if(StringUtils.isNotBlank(n)){
                        LOGGER.debug("连接下一行");
                        line = pre+n;
                    }
                    LOGGER.debug("PDF"+(i+2)+"行：" + lines[i + 1]);
                    i++;
                } else {
                    LOGGER.debug("连接完毕");
                    break;
                }
            }
            //组装段落
            String lastLine = null;
            String nextLine = null;
            if(i-1 > -1){
                lastLine = lines[i-1].trim();
            }
            if(i+1 < lines.length){
                nextLine = lines[i+1].trim();
            }
            addLineToParagraph(line, lastLine, nextLine, paragraph);
        }
        //内容结束
        process(paragraph.toString(), data);
        return data;
    }

    private static void addLineToParagraph(String line, String lastLine, String nextLine, StringBuilder paragraph){
        if(StringUtils.isBlank(line)){
            return;
        }
        if(nextLine!=null){
            //当前行是数字开头、字母结束
            if(Character.isDigit(line.charAt(0))
                    && Character.isAlphabetic(line.charAt(line.length()-1))
                    //下一行为空或者是数字开头或者是大写字母开头
                    && (StringUtils.isBlank(nextLine)
                        || Character.isDigit(nextLine.charAt(0))
                        || Character.isUpperCase(nextLine.charAt(0)))){
                LOGGER.debug("忽略数字标题，不做分析："+line);
                return;
            }
        }
        paragraph.append(line).append(" ");
    }

    public static boolean isProgramCode(String paragraph){
        if(//Java代码
                paragraph.startsWith("package")
                        || paragraph.startsWith("import")
                        || paragraph.startsWith("public")
                        || paragraph.startsWith("private")
                        || paragraph.startsWith("/**")
                        || paragraph.contains(");")
                        || paragraph.contains("}")
                        || paragraph.contains("{")
                        //html和xml标签
                        || paragraph.startsWith("<")){
            return true;
        }
        return false;
    }

    private static void process(String paragraph, List<String> data){
        if (StringUtils.isNotBlank(paragraph)) {
            LOGGER.debug("段落：" + paragraph);
            //检查段落合法性
            if(paragraphValid(paragraph)) {
                //将段落切分为句子
                List<String> sentences = segSentence(paragraph);
                if (!sentences.isEmpty()) {
                    data.addAll(sentences);
                }
            }
        }
    }

    public static boolean paragraphValid(String paragraph){
        //分析文本是否已经被损坏
        //首字母可以不用检查
        char[] chars = paragraph.toCharArray();
        for(int i=1; i<chars.length; i++){
            char c = chars[i];
            /**
             8208 ‐
             8209 ‑
             8210 ‒
             8211 –
             8212 —
             8213 ―
             8214 ‖
             8215 ‗
             8216 ‘
             8217 ’
             8218 ‚
             8219 ‛
             8220 “
             8221 ”
             8222 „
             8223 ‟
             8224 †
             8225 ‡
             8226 •
             8227 ‣
             8228 ․
             8229 ‥
             8230 …
             8231 ‧
             */
            if(c >= 8208 && c <= 8231){
                continue;
            }
            /**
             32
             33 !
             34 "
             35 #
             36 $
             37 %
             38 &
             39 '
             40 (
             41 )
             42 *
             43 +
             44 ,
             45 -
             46 .
             47 /
             48 0
             49 1
             50 2
             51 3
             52 4
             53 5
             54 6
             55 7
             56 8
             57 9
             58 :
             59 ;
             60 <
             61 =
             62 >
             63 ?
             64 @
             65 A
             66 B
             67 C
             68 D
             69 E
             70 F
             71 G
             72 H
             73 I
             74 J
             75 K
             76 L
             77 M
             78 N
             79 O
             80 P
             81 Q
             82 R
             83 S
             84 T
             85 U
             86 V
             87 W
             88 X
             89 Y
             90 Z
             91 [
             92 \
             93 ]
             94 ^
             95 _
             96 `
             97 a
             98 b
             99 c
             100 d
             101 e
             102 f
             103 g
             104 h
             105 i
             106 j
             107 k
             108 l
             109 m
             110 n
             111 o
             112 p
             113 q
             114 r
             115 s
             116 t
             117 u
             118 v
             119 w
             120 x
             121 y
             122 z
             123 {
             124 |
             125 }
             126 ~
             */
            if(c >= 32 && c <= 126){
                continue;
            }
            /**
             *
             64256 ﬀ
             64257 ﬁ
             64258 ﬂ
             64259 ﬃ
             64260 ﬄ
             64261 ﬅ
             64262 ﬆ
             */
            if(c >= 64256 && c <= 64262){
                continue;
            }
            CORRUPT_CHAR.add(c);
            LOGGER.debug("忽略含有非法字符（"+c+"="+(int)c+"）的文本，字符下标："+i+"，不做分析："+paragraph);
            return false;
        }
        if(isProgramCode(paragraph)){
            LOGGER.debug("忽略程序代码，不做分析："+paragraph);
            return false;
        }
        return true;
    }

    /**
     * 将一个段落切分为多个句子
     * @param paragraph
     * @return
     */
    private static List<String> segSentence(String paragraph){
        List<String> data = new ArrayList<>();
        //切分之前进行预处理
        paragraph = prepareSeg(paragraph);
        if(StringUtils.isBlank(paragraph)){
            return data;
        }
        //根据分隔符分割句子
        for(String s : paragraph.split("[.．。•]")) {
            if(StringUtils.isBlank(s)){
                continue;
            }
            LOGGER.debug("处理句子：" + s);
            s = processSentence(s);
            if(s == null){
                continue;
            }
            //加上句号
            if(Character.isAlphabetic(s.charAt(s.length() - 1))){
                s += ".";
            }
            //还原.
            s = s.replace("杨尚川", ".");
            data.add(s);
            LOGGER.debug("得到句子：" + s);
            if(LOGGER.isDebugEnabled()) {
                int length = s.split("\\s+").length;
                //统计句子长度分布情况
                SENTENCE_LENGTH_INFO.putIfAbsent(length, new AtomicInteger());
                SENTENCE_LENGTH_INFO.get(length).incrementAndGet();
            }
        }
        return data;
    }
    public static String processSentence(String sentence){
        //忽略空行
        if(StringUtils.isBlank(sentence)){
            LOGGER.debug("忽略没有内容的句子：" + sentence);
            return null;
        }
        sentence = sentence.trim();
        if(sentence.endsWith(",")){
            LOGGER.debug("以逗号结尾，不做分析："+sentence);
            return null;
        }
        //移除行首的非字母字符
        int i=0;
        for(char c : sentence.toCharArray()){
            if(Character.isAlphabetic(c)){
                break;
            }
            i++;
        }
        if(i>=sentence.length()){
            LOGGER.debug("忽略没有字母的句子：" + sentence);
            return null;
        }
        if(i>0) {
            sentence = sentence.substring(i);
        }
        if(StringUtils.isBlank(sentence)){
            LOGGER.debug("忽略没有内容的句子：" + sentence);
            return null;
        }
        //忽略首字母非大写的句子
        if(!Character.isUpperCase(sentence.charAt(0))){
            LOGGER.debug("忽略首字母非大写的句子：" + sentence);
            return null;
        }
        String[] words = sentence.split("\\s+");
        if(words[0].length() == 1
                && !"A".equals(words[0])
                && !"I".equals(words[0])){
            LOGGER.debug("忽略第一个单词不合法的句子：" + sentence);
            return null;
        }
        if(words[0].length() > 1 && StringUtils.isAllUpperCase(words[0])){
            LOGGER.debug("忽略首单词全大写的句子：" + sentence);
            return null;
        }
        //判断句子长度
        if(words.length < SENTENCE_WORD_MIN_COUNT){
            LOGGER.debug("忽略长度小于" + SENTENCE_WORD_MIN_COUNT + "的句子：" + sentence);
            return null;
        }
        //判断是否最后一个单词是数字
        if(StringUtils.isNumeric(words[words.length-1])){
            LOGGER.debug("忽略最后一个单词是数字" + words[words.length-1] + "的句子：" + sentence);
            return null;
        }
        //判断句子中的大写字母开头的单词数
        int capWordCount = 0;
        //最长单词
        int maxWordCharCount = 0;
        for(String word : words){
            if(Character.isUpperCase(word.charAt(0))){
                capWordCount++;
            }
            if(!word.contains("http://") && word.length() > maxWordCharCount){
                maxWordCharCount = word.length();
            }
        }
        if(capWordCount > words.length*SENTENCE_CAP_WORD_MAX_RATE){
            LOGGER.debug("忽略首字母大写单词数" + capWordCount + "多于" + words.length*SENTENCE_CAP_WORD_MAX_RATE + "的句子：" + sentence);
            return null;
        }
        if(maxWordCharCount > MAX_WORD_CHAR_COUNT){
            LOGGER.debug("忽略有超长单词的句子，单词长度" + maxWordCharCount + "大于" + MAX_WORD_CHAR_COUNT + "的句子：" + sentence);
            return null;
        }
        //判断句子中的非字母单词数
        int specialWordCount = 0;
        for(String word : words){
            for(String c : punctuation){
                word = word.replace(c, "");
            }
            if(StringUtils.isNotBlank(word)
                    && !StringUtils.isAlpha(word)){
                LOGGER.debug("特殊非字母单词："+word);
                specialWordCount++;
            }
        }
        if(specialWordCount > Math.log(words.length)/2){
            LOGGER.debug("总次数："+words.length+"，忽略非字母单词数" + specialWordCount + "多于" + Math.log(words.length)/2 + "的句子：" + sentence);
            return null;
        }
        //不是单词的词数
        int notWordCount = 0;
        Set<String> toCheck = TextAnalyzer.seg(sentence).stream().collect(Collectors.toSet());
        LOGGER.debug("需要检查单词个数："+toCheck.size());
        for(String word : toCheck){
            if(!DICTIONARY.contains(new Word(word.toLowerCase(), ""))){
                LOGGER.debug("未知单词："+word);
                notWordCount++;
            }
        }
        LOGGER.debug("未知的单词个数："+notWordCount);
        if(notWordCount > toCheck.size()*0.4){
            LOGGER.debug("待检查的单词在已有词典中不存在数" + notWordCount + "大于" + toCheck.size()*0.4 + "的句子：" + sentence);
            return null;
        }
        //检查[]()是否配对
        if(sentence.contains("[")
                || sentence.contains("]")
                || sentence.contains("(")
                || sentence.contains(")")
                || sentence.contains("“")
                || sentence.contains("”")
                || sentence.contains("\"")){
            char[] chars = sentence.toCharArray();
            int pre=0;
            int suf=0;
            int quotCount=0;
            for(int j=0; j<chars.length; j++){
                char c = chars[j];
                switch (c){
                    case '[': LOGGER.debug("匹配："+c+"，下标："+j);pre++;break;
                    case '(': LOGGER.debug("匹配："+c+"，下标："+j);pre++;break;
                    case ']': LOGGER.debug("匹配："+c+"，下标："+j);suf++;break;
                    case ')': LOGGER.debug("匹配："+c+"，下标："+j);suf++;break;
                    case '“': LOGGER.debug("匹配："+c+"，下标："+j);pre++;break;
                    case '”': LOGGER.debug("匹配："+c+"，下标："+j);suf++;break;
                    case '"': LOGGER.debug("匹配："+c+"，下标："+j);quotCount++;break;
                }
            }
            if(pre != suf){
                LOGGER.debug("[]()配对检查失败，前向数："+pre+"，后向数："+suf);
                return null;
            }
            if(quotCount%2==1){
                LOGGER.debug("[]()配对检查失败，双引号数："+quotCount);
                return null;
            }
        }
        return sentence;
    }

    /**
     * 检查段落是否合法，不合法则放弃切分句子
     * @param paragraph
     * @return
     */
    private static String prepareSeg(String paragraph){
        paragraph = paragraph.replace(".)", ". ");
        paragraph = paragraph.replace("!)", ". ");
        if(paragraph.contains(".")) {
            paragraph = paragraph.trim();
            StringBuilder data = new StringBuilder();
            int index = 0;
            int last = 0;
            boolean r = false;
            while ((index = paragraph.indexOf(".", index)) > -1) {
                boolean remain = false;
                if(index+1 < paragraph.length()){
                    if(Character.isWhitespace(paragraph.charAt(index+1))){
                        remain = true;
                    }
                }
                if(index == paragraph.length()-1){
                    remain = true;
                }
                if(!remain){
                    data.append(paragraph.substring(last, index)).append("杨尚川");
                    r = true;
                }else{
                    data.append(paragraph.substring(last, index+1));
                }
                index++;
                last = index;
            }
            if (last < paragraph.length()) {
                data.append(paragraph.substring(last, paragraph.length()));
            }
            paragraph = data.toString();
            if(r){
                LOGGER.debug("将.替换之后："+paragraph);
            }
        }
        return paragraph;
    }

    /**
     * 检查文件是否有效，只处理PDF文档
     * @param file
     * @return
     */
    private static boolean invalid(Path file){
        if (file.toFile().getName().startsWith(".")) {
            return true;
        }
        String fileName = file.toFile().getAbsolutePath();
        if (!fileName.endsWith(".pdf")) {
            LOGGER.info("放弃处理非PDF文件：" + fileName);
            return true;
        }
        return false;
    }

    /**
     * 将PDF解析之后的文本保存到哪个文件
     * @param file
     * @return
     */
    private static String prepareTarget(Path file){
        try {
            String fileName = file.toFile().getAbsolutePath();
            String targetName = "src/main/resources/it"
                    + fileName.replace(file.getParent().getParent().toFile().getAbsolutePath(), "").replace(".pdf", "")
                    + ".txt";
            Path target = Paths.get(targetName);
            //删除以前生成的文件
            Files.deleteIfExists(target);
            //准备目录结构
            if (Files.notExists(target.getParent())) {
                Files.createDirectories(target.getParent());
            }
            return targetName;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static void resetSentenceWordLengthInfo(){
        SENTENCE_LENGTH_INFO.clear();
    }
    public static void showSentenceWordLengthInfo(){
        LOGGER.debug("句子词长分布：");
        SENTENCE_LENGTH_INFO
                .keySet()
                .stream()
                .sorted()
                .forEach(k -> {
                    LOGGER.debug(k + " -> " + SENTENCE_LENGTH_INFO.get(k));
                });
        LOGGER.debug("未识别的字符：");
        CORRUPT_CHAR.stream().sorted().forEach(c -> LOGGER.debug((int)c+"="+c.toString()));
    }
    public static void main(String[] args) throws Exception{
        resetSentenceWordLengthInfo();
        //提取文件
        //String file = "/Users/apple/百度云同步盘/【大数据】相关技术英文原版电子书/activemq/ActiveMQ in Action.pdf";
        //parseFile(file);
        //提取子类别
        //String path = "/Users/apple/百度云同步盘/【大数据】相关技术英文原版电子书/cassandra";
        //提取所有类别
        String path = "/Users/apple/百度云同步盘/【大数据】相关技术英文原版电子书";
        //提取目录
        parseDirectory(path);
        showSentenceWordLengthInfo();
    }
}
