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

package org.apdplat.jsearch.score;

import org.apdplat.jsearch.search.Doc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 词距评分组件
 * @author 杨尚川
 */
public class ProximityScore implements Score {
    private int slop = 0;

    @Override
    public Float score(Doc doc, List<String> words) {
        if(words.size() < 2){
            return 0f;
        }
        Map<String, List<Integer>> wordPosition = doc.getWordPosition();
        if(words.size() != wordPosition.size()){
            return 0f;
        }
        AtomicInteger score = new AtomicInteger();
        String lastWord = words.get(words.size()-1);
        wordPosition.get(lastWord).stream().forEach(endPosition->{
            //endPosition 是最后一个词在文本中的位置
            int previousPosition = endPosition;
            int permitPosition = previousPosition - slop - 1;
            int times = 0;
            for(int i=words.size()-2; i>-1; i--){
                boolean find = false;
                for(int position : wordPosition.get(words.get(i))){
                    if(position<previousPosition && position>=permitPosition){
                        find = true;
                        previousPosition = position;
                        permitPosition = previousPosition - slop - 1;
                        times++;
                    }
                }
                if(!find){
                    break ;
                }
            }
            if(times == words.size()-1){
                score.incrementAndGet();
            }
        });
        return Float.valueOf(score.get());
    }

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }
}
