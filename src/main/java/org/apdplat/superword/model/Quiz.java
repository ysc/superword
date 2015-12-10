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

import org.apdplat.superword.tools.WordLinker.Dictionary;
import org.apdplat.superword.tools.WordSources;
import org.apdplat.word.util.AtomicFloat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 词汇量测试
 * @author 杨尚川
 */
public class Quiz{
    private static final Map<Integer, Integer> LEVEL_TO_TOTAL_COUNT = new ConcurrentHashMap<>();
    private static final int SCALE = 300;
    private List<QuizItem> quizItems = new ArrayList<>();
    private int step=0;

    private Quiz(){}

    public int getEvaluationCount(){
        Map<Integer, AtomicInteger> levelRightCount = new HashMap<>();
        quizItems.stream().filter(quizItem -> quizItem.isRight()).forEach(quizItem -> {
            levelRightCount.putIfAbsent(quizItem.getLevel(), new AtomicInteger());
            levelRightCount.get(quizItem.getLevel()).incrementAndGet();
        });
        AtomicFloat count = new AtomicFloat();
        quizItems.stream().filter(quizItem -> quizItem.isRight()).forEach(quizItem -> {
            float rightRate = levelRightCount.get(quizItem.getLevel()).intValue()
                    / (float)LEVEL_TO_TOTAL_COUNT.get(quizItem.getLevel());
            count.addAndGet(SCALE*rightRate);
        });
        return count.intValue();
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

        return quiz;
    }

    private static void build(List<Word> words, Dictionary dictionary, Quiz quiz, int limit, int level){
        LEVEL_TO_TOTAL_COUNT.put(level, limit);
        int count = 0;
        for(;;){
            Word word = words.get(new Random(System.nanoTime()).nextInt(words.size()));
            QuizItem quizItem = QuizItem.buildQuizItem(word.getWord(), words, dictionary);
            if(quizItem == null){
                continue;
            }
            if(quiz.quizItems.contains(quizItem)){
                continue;
            }
            quizItem.setLevel(level);
            quiz.quizItems.add(quizItem);
            if((++count) >= limit){
                break;
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