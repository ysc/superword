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
     * 在识别用户是否是机器人的测试中, 如果用户测试识别, 则向用户显示这里生成的HTML代码
     * @param data 需要保护两个数据项, 一是测试数据集quizItem, 二是用户的回答answer
     * @return 测试结果HTML代码
     */
    public static String getIdentifyQuiz(Map<String, Object> data){
        try {
            Template template = CFG.getTemplate("identify_quiz.ftlh");
            Writer out = new StringWriter();
            template.process(data, out);
            return out.toString();
        }catch (Exception e){
            LOGGER.error("generate authentication template failed", e);
        }
        return "";
    }

    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        QuizItem quizItem = QuizItem.buildIdentifyHumanQuiz(12);
        data.put("quizItem", quizItem);
        data.put("answer", "random answer");
        System.out.println(TemplateUtils.getIdentifyQuiz(data));
    }
}
