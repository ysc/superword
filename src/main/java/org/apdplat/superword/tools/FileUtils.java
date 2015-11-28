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

package org.apdplat.superword.tools;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具
 * Created by ysc on 11/25/15.
 */
public class FileUtils {
    public static List<String> readResource(String resource){
        List<String> data = new ArrayList<>();
        try {
            InputStream stream = FileUtils.class.getResourceAsStream(resource);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                if(StringUtils.isNotBlank(line)) {
                    data.add(line.trim());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
