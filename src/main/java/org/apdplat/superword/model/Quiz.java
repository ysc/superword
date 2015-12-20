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

package org.apdplat.superword.model;

import org.apdplat.superword.tools.TimeUtils;
import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.apdplat.superword.tools.WordSources;
import org.apdplat.word.util.AtomicFloat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 词汇量测试
 * @author 杨尚川
 */
public class Quiz{
    private static final Logger LOGGER = LoggerFactory.getLogger(Quiz.class);

    private static final Map<Integer, Integer> LEVEL_TO_TOTAL_COUNT = new ConcurrentHashMap<>();
    private static final int SCALE = 300;
    private List<QuizItem> quizItems = new ArrayList<>();
    private int step=0;
    private long startQuizTime;
    private long endQuizTime;

    private Quiz(){}

    public String getConsumedTime(){
        return TimeUtils.getTimeEnglishDes(endQuizTime - startQuizTime);
    }

    public int getEvaluationCount(){
        //答题完成时间
        endQuizTime = System.currentTimeMillis();
        //计算每一个级别答对的问题数目
        //如:
        //1 -> 2
        //2 -> 3
        //...
        //9 -> 3
        Map<Integer, AtomicInteger> levelRightCount = new HashMap<>();
        quizItems.stream().forEach(quizItem -> {
            levelRightCount.putIfAbsent(quizItem.getLevel(), new AtomicInteger());
            if(quizItem.isRight()) {
                levelRightCount.get(quizItem.getLevel()).incrementAndGet();
            }
        });
        //预估总词数
        AtomicFloat count = new AtomicFloat();
        quizItems.stream().filter(quizItem -> quizItem.isRight()).forEach(quizItem -> {
            //计算每一个级别答对的比率 = 每一个级别答对的题数 / 每一个级别总的题数
            float rightRate = levelRightCount.get(quizItem.getLevel()).intValue()
                    / (float)LEVEL_TO_TOTAL_COUNT.get(quizItem.getLevel());
            //如果题目属于第一级, 则将第一级答对的比率乘以固定的预估值作为该题预估词数
            if(quizItem.getLevel() > 1){
                //如果题目不属于第一级, 则将上一级答对的比率和本级相乘
                //然后用这个比率乘以固定的预估值作为该题预估词数
                int lastLevel = quizItem.getLevel() - 1;
                float lastRightRate = levelRightCount.get(lastLevel).intValue()
                        / (float)LEVEL_TO_TOTAL_COUNT.get(lastLevel);
                //如果上一级全部答错, 则将上一级的答对比率固定设置为0.1
                if(lastRightRate == 0){
                    lastRightRate = 0.1f;
                }
                rightRate *= lastRightRate;
            }
            count.addAndGet(SCALE*rightRate);
        });
        int cost = (480 - (int)(endQuizTime - startQuizTime)/1000) * 20;
        //期望答题时间是8分钟。每落后一秒钟预估词数减20, 最多减量不超过9600
        if(cost < -9600){
            cost = -9600;
        }
        //期望答题时间是8分钟。每提前一秒钟预估词数加20，最多加量不超过3600
        if(cost > 3600){
            cost = 3600;
        }
        //假定做题最快时间不少于4分钟，如果少于四分钟，每少N秒预估词数就减去4800+N*20
        if(cost > 4800){
            cost = -cost;
        }
        //返回预估值
        if((count.intValue() + cost) > 0){
            return count.intValue() + cost;
        }
        //如果如上算法最后获得的预估词数是负数，则去除负号取绝对值
        return - (count.intValue() + cost);
    }

    public QuizItem getQuizItem(){
        if(step < quizItems.size()){
            return quizItems.get(step);
        }
        return null;
    }

    public String step(){
        return quizItems.size()+"/"+(step+1);
    }

    public boolean answer(String word, String answer){
        for(QuizItem quizItem : quizItems) {
            if(quizItem.getWord().getWord().equals(word)){
                step++;
                quizItem.setAnswer(answer);
                return quizItem.isRight();
            }
        }
        return false;
    }

    public static Quiz buildQuiz(Dictionary dictionary){
        Quiz quiz = new Quiz();

        List<Word> level1 = new ArrayList<>();
        level1.addAll(WordSources.get("/word_primary_school.txt"));
        build(level1, dictionary, quiz, 5, 1);

        List<Word> level2 = new ArrayList<>();
        level2.addAll(WordSources.get("/word_junior_school.txt"));
        level2.removeAll(level1);
        build(level2, dictionary, quiz, 10, 2);

        List<Word> level3 = new ArrayList<>();
        level3.addAll(WordSources.get("/word_senior_school.txt"));
        level3.removeAll(level1);
        level3.removeAll(level2);
        build(level3, dictionary, quiz, 15, 3);

        List<Word> level4 = new ArrayList<>();
        level4.addAll(WordSources.get("/word_CET4.txt"));
        level4.removeAll(level1);
        level4.removeAll(level2);
        level4.removeAll(level3);
        build(level4, dictionary, quiz, 15, 4);

        List<Word> level5 = new ArrayList<>();
        level5.addAll(WordSources.get("/word_CET6.txt"));
        level5.removeAll(level1);
        level5.removeAll(level2);
        level5.removeAll(level3);
        level5.removeAll(level4);
        build(level5, dictionary, quiz, 15, 5);

        List<Word> level6 = new ArrayList<>();
        level6.addAll(WordSources.get("/word_IELTS.txt"));
        level6.removeAll(level1);
        level6.removeAll(level2);
        level6.removeAll(level3);
        level6.removeAll(level4);
        level6.removeAll(level5);
        build(level6, dictionary, quiz, 10, 6);

        List<Word> level7 = new ArrayList<>();
        level7.addAll(WordSources.get("/word_TOEFL.txt"));
        level7.removeAll(level1);
        level7.removeAll(level2);
        level7.removeAll(level3);
        level7.removeAll(level4);
        level7.removeAll(level5);
        level7.removeAll(level6);
        build(level7, dictionary, quiz, 10, 7);

        List<Word> level8 = new ArrayList<>();
        level8.addAll(WordSources.get("/word_GRE.txt"));
        level8.removeAll(level1);
        level8.removeAll(level2);
        level8.removeAll(level3);
        level8.removeAll(level4);
        level8.removeAll(level5);
        level8.removeAll(level6);
        level8.removeAll(level7);
        build(level8, dictionary, quiz, 10, 8);

        List<Word> level9 = new ArrayList<>();
        level9.addAll(WordSources.getSyllabusVocabulary());
        level9.removeAll(level1);
        level9.removeAll(level2);
        level9.removeAll(level3);
        level9.removeAll(level4);
        level9.removeAll(level5);
        level9.removeAll(level6);
        level9.removeAll(level7);
        level9.removeAll(level8);
        build(level9, dictionary, quiz, 10, 9);

        //答题开始时间
        quiz.startQuizTime = System.currentTimeMillis();

        return quiz;
    }

    private static void build(List<Word> words, Dictionary dictionary, Quiz quiz, int limit, int level){
        LEVEL_TO_TOTAL_COUNT.put(level, limit);
        int count = 0;
        for(;;){
            try {
                Word word = words.get(new Random(System.nanoTime()).nextInt(words.size()));
                if (word.getWord().length() < 3) {
                    continue;
                }
                QuizItem quizItem = QuizItem.buildQuizItem(word.getWord(), words, dictionary);
                if (quizItem == null) {
                    continue;
                }
                if (quiz.quizItems.contains(quizItem)) {
                    continue;
                }
                quizItem.setLevel(level);
                quiz.quizItems.add(quizItem);
                if ((++count) >= limit) {
                    break;
                }
            }catch (Throwable e){
                LOGGER.error("something wrong when build quiz", e);
            }
        }
    }

    public int size(){
        return quizItems.size();
    }

    public List<QuizItem> getQuizItems() {
        return Collections.unmodifiableList(quizItems);
    }

    public void print(){
        AtomicInteger i = new AtomicInteger();
        getQuizItems().forEach(w -> {
            System.out.println(i.incrementAndGet()+". "+w.getWord().getWord()+" "+w.getWord().getMeaning());
            w.getMeanings().forEach(m->System.out.println("\t"+m+"\n"));
        });
    }
}