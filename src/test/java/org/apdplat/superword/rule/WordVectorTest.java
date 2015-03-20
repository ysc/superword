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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class WordVectorTest {
    @Test
    public void testOf(){
        WordVector wordVector = WordVector.of("word");
        WordVector.Score score = wordVector.score(WordVector.of("word"));
        assertEquals("vector is error", "word", score.getWord());
        assertEquals(12, score.getScore(), 0);
        assertEquals("explain is error", "", score.getExplain().toString());

        score = wordVector.score(WordVector.of("word"), true);
        assertEquals("vector is error", "word", score.getWord());
        assertEquals(12, score.getScore(), 0);
        assertEquals("explain is error", "d(1-1) o(1-1) r(1-1) w(1-1) s:4", score.getExplain().toString());

        score = wordVector.score(WordVector.of("computer"));
        assertEquals("vector is error", "computer", score.getWord());
        assertEquals(3, score.getScore(), 0);
        assertEquals("explain is error", "", score.getExplain().toString());

        score = wordVector.score(WordVector.of("computer"), true);
        assertEquals("vector is error", "computer", score.getWord());
        assertEquals(3, score.getScore(), 0);
        assertEquals("explain is error", "o(1-1) r(1-0) ", score.getExplain().toString());

        score = wordVector.score(WordVector.of("skylight"));
        assertEquals("vector is error", "skylight", score.getWord());
        assertEquals(0, score.getScore(), 0);
        assertEquals("explain is error", "", score.getExplain().toString());

        score = wordVector.score(WordVector.of("skylight"), true);
        assertEquals("vector is error", "skylight", score.getWord());
        assertEquals(0, score.getScore(), 0);
        assertEquals("explain is error", "", score.getExplain().toString());
    }
}
