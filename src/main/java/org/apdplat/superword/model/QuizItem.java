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

import org.apache.commons.lang3.StringUtils;
import org.apdplat.superword.tools.MySQLUtils;
import org.apdplat.superword.tools.WordLinker.Dictionary;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 词汇量测试项目
 * @author 杨尚川
 */
public class QuizItem implements Comparable {
    private Word word;
    private List<Word> otherWords = new ArrayList<>();
    private String answer;
    private int level;

    private QuizItem(){}

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isRight(){
        if(word.getMeaning().equals(answer)){
            return true;
        }
        return false;
    }

    public static String getDefinition(String word, Dictionary dictionary) {
        String definition = MySQLUtils.getWordDefinition(word, dictionary.name());
        String[] attrs = definition.split("<br/>");
        if(attrs == null || attrs.length < 1){
            return null;
        }
        String selectedDefinition = attrs[new Random(System.nanoTime()).nextInt(attrs.length)].trim();
        if(StringUtils.isBlank(selectedDefinition)
                || selectedDefinition.contains(word)){
            return null;
        }
        for(char c : selectedDefinition.substring(selectedDefinition.indexOf(".")+1).toCharArray()){
            if( (c>='a' && c<='z') || (c>='A' && c<='Z')){
                return null;
            }
        }
        return selectedDefinition;
    }

    public static QuizItem buildQuizItem(String word, List<Word> words, Dictionary dictionary){
        try {
            QuizItem quizItem = new QuizItem();
            String selectedDefinition = getDefinition(word, dictionary);
            if(StringUtils.isBlank(selectedDefinition)){
                return null;
            }
            quizItem.word = new Word(word, selectedDefinition);

            for(;;){
                String candidate = words.get(new Random(System.nanoTime()).nextInt(words.size())).getWord();
                if(word.equals(candidate)
                        || candidate.length() < 3){
                    continue;
                }
                String definition = getDefinition(candidate, dictionary);
                if(StringUtils.isBlank(definition)){
                    continue;
                }
                quizItem.otherWords.add(new Word(candidate, definition));
                if(quizItem.otherWords.size() >= 3){
                    return quizItem;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Set<String> getMeanings(){
        Set<String> meanings = new HashSet<>();
        meanings.add(word.getMeaning());
        otherWords.forEach(w->meanings.add(w.getMeaning()));
        return meanings;
    }

    public void print(){
        System.out.println("word: " + word.getWord());
        AtomicInteger i = new AtomicInteger();
        getMeanings().forEach(w -> System.out.println("\t"+i.incrementAndGet()+". "+w));
    }

    public Word getWord() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizItem)) return false;

        QuizItem quizItem = (QuizItem) o;

        return word.equals(quizItem.word);

    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.word == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof QuizItem)){
            return 1;
        }
        Word w = ((QuizItem)o).getWord();
        if(w == null){
            return 1;
        }
        return this.word.compareTo(w);
    }
}