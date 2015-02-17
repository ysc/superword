/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.superword.rule;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 杨尚川
 */
public class WordVector {
    private final BitSet wordVector = new BitSet(26);
    private String word = null;
    private Map<Character, Integer> times = null;
    private Map<Character, List<Integer>> loc = null;
    private WordVector(){}
    public static WordVector of(String word){
        if(word == null || word.trim().isEmpty()){
            return null;
        }
        WordVector result = new WordVector();
        word = word.toLowerCase();
        result.times = new HashMap<>(word.length());
        result.loc = new HashMap<>();
        int i = 1;
        for(char c : word.toCharArray()){
            int index = c - 'a';
            if(index<0 || index>25){
                continue;
            }
            result.wordVector.set(index, true);
            //统计字母的出现次数
            Integer v = result.times.get(c);
            if(v == null){
                v = 1;
            }else{
                v++;
            }
            result.times.put(c, v);
            //记住词序
            List<Integer> s = result.loc.get(c);
            if(s == null){
                s = new ArrayList<>(5);
                result.loc.put(c, s);
            }
            s.add(i++);
        }
        result.word = word;
        
        return result;
    }
    public Score score(WordVector wordVector){
        return score(wordVector, false);
    }
    public Score score(WordVector wordVector, boolean explain){
        StringBuilder tip = new StringBuilder();
        Score score = new Score();
        float sum = 0.0f;
        BitSet temp = this.wordVector.get(0, 26);
        temp.and(wordVector.wordVector);
        for(int i=0;i<26;i++){
            if(temp.get(i)){
                //有共同字符加一分
                sum++;
                //还原字符
                char c = (char)(i+'a');
                //计算共同出现次数
                int t = Math.min(this.times.get(c), wordVector.times.get(c));
                if(t > 1){
                    sum += t-1;
                }
                //如果有相同的位置，则加分
                long locc = 0;
                List<Integer> wordVectorLoc = wordVector.loc.get(c);
                locc = this.loc.get(c).stream().filter(item -> wordVectorLoc.contains(item)).count();
                sum += locc;
                
                if(explain){
                    tip.append(c).append("(").append(t).append("-").append(locc).append(")").append(" ");
                }
            }
        }
        //前后顺序加分
        int len=0;
        int limit = Math.min(this.word.length(), wordVector.word.length());
        for(int i=0;i<limit;i++){
            if(this.word.charAt(i)==wordVector.word.charAt(i)){
                len++;
            }else{
                break;
            }
        }
        if(len>2){
            sum+=len;
            tip.append("s:").append(len);
        }
        score.setWord(wordVector.word);
        score.setScore(sum);
        if(explain){
            score.setExplain(tip);
        }
        return score;
    }
    public static class Score implements Comparable{
        private String word;
        private float score;
        private StringBuilder explain = new StringBuilder();

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public StringBuilder getExplain() {
            return explain;
        }

        public void setExplain(StringBuilder explain) {
            this.explain = explain;
        }

        @Override
        public String toString() {
            return "Score{" + "word=" + getWord() + ", score=" + getScore() + ", explain=" + getExplain() + '}';
        }

        @Override
        public int compareTo(Object o) {
            return ((Float)score).compareTo(((Score)o).score);
        }
    }
    public static void main(String[] args) {
        WordVector word = WordVector.of("amazed");
        System.out.println("amazed score:");
        System.out.println(word.score(WordVector.of("amazing"), true));
        System.out.println(word.score(WordVector.of("interested"), true));
        System.out.println(word.score(WordVector.of("interesting"), true));
        System.out.println(word.score(WordVector.of("fox"), true));
    }
}
