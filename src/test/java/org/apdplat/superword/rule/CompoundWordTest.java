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

import junit.framework.TestCase;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordSources;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author 杨尚川
 */
public class CompoundWordTest extends TestCase {

    @Test
    public void testFind(){
        Set<Word> words = new HashSet<>();
        words.add(new Word("love", ""));
        words.add(new Word("true", ""));
        Map<Integer, List<Word>> r = CompoundWord.find(words, "truelove");
        assertEquals(1, r.size(), 0);
        assertEquals(2, r.get(4).size(), 0);
        r = CompoundWord.find(words, "falselove");
        assertEquals(0, r.size(), 0);
        assertNull(r.get(5));
    }
}