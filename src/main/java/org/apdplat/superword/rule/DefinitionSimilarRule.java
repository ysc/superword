package org.apdplat.superword.rule;

import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.MySQLUtils;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.apdplat.word.analysis.CosineTextSimilarity;
import org.apdplat.word.analysis.Hit;
import org.apdplat.word.analysis.TextSimilarity;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 单词定义相似性计算规则
 * Created by ysc on 4/10/16.
 */
public class DefinitionSimilarRule {
    public static class Result{
        private String word;
        private String definition;
        private String url;
        private double score;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
    public static List<Result> run(Dictionary dictionary, Set<Word> words, String wordDefinition, int count) {
        List<String> allWordDefinition = MySQLUtils.getAllWordDefinition(dictionary.name(), words);

        TextSimilarity textSimilarity = new CosineTextSimilarity();

        if (dictionary == Dictionary.OXFORD || dictionary == Dictionary.WEBSTER) {
            textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.PureEnglish);
        }
        if (dictionary == Dictionary.ICIBA || dictionary == Dictionary.YOUDAO) {
            textSimilarity.setSegmentationAlgorithm(SegmentationAlgorithm.MaxNgramScore);
        }

        List<Result> results = new ArrayList<>();

        for (Hit hit : textSimilarity.rank(wordDefinition, allWordDefinition, count).getHits()) {
            String[] attrs = hit.getText().split("_");
            String word = attrs[0];
            StringBuilder definition = new StringBuilder(attrs[1]);
            for (int j = 2; j < attrs.length; j++) {
                definition.append(attrs[j]).append("_");
            }

            Result result = new Result();
            result.setWord(word);
            result.setDefinition(definition.toString());
            result.setUrl(WordLinker.toLink(word));
            result.setScore(hit.getScore());

            results.add(result);
        }
        return results;
    }
}
