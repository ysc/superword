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

/**
 * 用户动态前缀分析记录
 * Created by ysc on 11/24/15.
 */
public class UserDynamicPrefix extends HistoryRecord {
    private String dynamicPrefix;

    public String getDynamicPrefix() {
        return dynamicPrefix;
    }

    public void setDynamicPrefix(String dynamicPrefix) {
        this.dynamicPrefix = dynamicPrefix;
    }

    @Override
    public String toString() {
        return "UserDynamicPrefix{" +
                "id='" + id + '\'' +
                "userName='" + userName + '\'' +
                ", dateTime=" + simpleDateFormat.format(dateTime) +
                ", dynamicPrefix='" + dynamicPrefix + '\'' +
                '}';
    }
}
