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

package org.apdplat.jsearch.index;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 倒排表
 * @author 杨尚川
 */
public class Posting {
    private Map<Integer, PostingItem> postingItems = new HashMap<>();

    public int size(){
        return this.postingItems.size();
    }

    public Collection<PostingItem> getPostingItems() {
        return Collections.unmodifiableCollection(this.postingItems.values());
    }

    public void putIfAbsent(int docId){
        this.postingItems.putIfAbsent(docId, new PostingItem(docId));
    }

    public PostingItem get(int docId){
        return this.postingItems.get(docId);
    }

    public void remove(PostingItem postingItem){
        this.postingItems.remove(postingItem);
    }

    public void clear(){
        this.clear();
    }
}
