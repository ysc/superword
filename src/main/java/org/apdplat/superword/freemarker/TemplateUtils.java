package org.apdplat.superword.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apdplat.superword.model.QuizItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 模板工具, 用于生成html代码
 * Created by ysc on 4/2/16.
 */
public class TemplateUtils {
    private TemplateUtils(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateUtils.class);
    private static final Configuration CFG = new Configuration(Configuration.VERSION_2_3_23);

    static{
        LOGGER.info("开始初始化模板配置");
        CFG.setClassLoaderForTemplateLoading(TemplateUtils.class.getClassLoader(), "/template/freemarker/");
        CFG.setDefaultEncoding("UTF-8");
        if(LOGGER.isDebugEnabled()) {
            CFG.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        }else{
            CFG.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
        }
        CFG.setLogTemplateExceptions(false);
        LOGGER.info("模板配置初始化完毕");
    }

    /**
     * 在识别用户是否是机器人的测试中, 如果用户测试失败, 则向用户显示这里生成的HTML代码
     * @param data 需要两个数据项, 一是测试数据集quizItem, 二是用户的回答answer
     * @return 测试结果HTML代码
     */
    public static String getIdentifyQuizFailure(Map<String, Object> data){
        return merge(data, "identify_quiz_failure.ftlh");
    }

    /**
     * 在识别用户是否是机器人的测试中, 给用户展示这里生成的HTML代码
     * @param data 需要三个数据项, 一是servletContext, 二是token, 三是quizItem
     * @return 测试HTML代码
     */
    public static String getIdentifyQuizForm(Map<String, Object> data){
        return merge(data, "identify_quiz_form.ftlh");
    }

    /**
     * 在词汇量测试中, 给用户展示这里生成的HTML代码
     * @param data 需要二个数据项, 一是step, 二是quizItem
     * @return 词汇量测试HTML代码
     */
    public static String getVocabularyTestForm(Map<String, Object> data){
        return merge(data, "vocabulary_test_form.ftlh");
    }

    /**
     * 在词汇量测试结束后, 给用户展示这里生成的HTML代码
     * @param data 需要一个数据项: quiz
     * @return 词汇量测试结果HTML代码
     */
    public static String getVocabularyTestResult(Map<String, Object> data){
        return merge(data, "vocabulary_test_result.ftlh");
    }

    /**
     * 生成单词定义页面的HTML
     * @param data
     * word
     * servletContext
     * hasOxfordAudio
     * oxfordAudios
     * hasWebsterAudio
     * websterAudios
     * icibaDefinitionHtml
     * youdaoDefinitionHtml
     * icibaPronunciation
     * icibaDefinition
     * youdaoPronunciation
     * youdaoDefinition
     * oxfordDefinitionHtml
     * websterDefinitionHtml
     * oxfordPronunciation
     * oxfordDefinition
     * websterPronunciation
     * websterDefinition
     * otherDictionary
     * isMyNewWord
     * wordLevels
     * @return
     */
    public static String getWordDefinition(Map<String, Object> data){
        return merge(data, "word_definition.ftlh");
    }

    /**
     * 单词定义相似性计算结果展示HTML
     * @param data
     * results
     * word
     * dictionary
     * count
     * words_type
     * @return
     */
    public static String getDefinitionSimilarResult(Map<String, Object> data) {
        return merge(data, "definition_similar_rule.ftlh");
    }

    public static String merge(Map<String, Object> data, String templateName){
        try {
            Template template = CFG.getTemplate(templateName);
            Writer out = new StringWriter();
            template.process(data, out);
            return out.toString();
        }catch (Exception e){
            LOGGER.error("generate template "+templateName+" failed", e);
        }
        return "";
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        QuizItem quizItem = QuizItem.buildIdentifyHumanQuiz(12);
        data.put("quizItem", quizItem);
        data.put("answer", "random answer");
        System.out.println(TemplateUtils.getIdentifyQuizFailure(data));

        quizItem = QuizItem.buildIdentifyHumanQuiz(12);
        data.put("quizItem", quizItem);
        data.put("servletContext", "");
        data.put("token", UUID.randomUUID().toString());
        System.out.println(TemplateUtils.getIdentifyQuizForm(data));

        quizItem = QuizItem.buildIdentifyHumanQuiz(12);
        data.put("quizItem", quizItem);
        data.put("servletContext", "/superword");
        data.put("token", UUID.randomUUID().toString());
        System.out.println(TemplateUtils.getIdentifyQuizForm(data));
    }
}
