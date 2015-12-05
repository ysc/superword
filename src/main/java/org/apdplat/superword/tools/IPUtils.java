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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索IP地址的地理位置
 * Created by ysc on 12/5/15.
 */
public class IPUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IPUtils.class);

    public static List<String> getIPLocation(String ip){
        List<String> locations = new ArrayList<>();
        try {
            Elements elements = Jsoup
                    .parse(new URL("http://ip138.com/ips138.asp?ip=" + ip), 60000)
                    .select("ul li");
            for(Element element : elements){
                String text = element.text();
                if(StringUtils.isNotBlank(text)){
                    String[] attrs = text.split("：");
                    if(attrs != null && attrs.length == 2){
                        locations.add(attrs[1]);
                    }
                }
            }
        }catch (Exception e){
            LOG.error("获取IP地址的地理位置", e);
        }
        return locations;
    }

    public static void main(String[] args) {
        System.out.println(getIPLocation("116.90.82.28"));
        System.out.println(getIPLocation("66.249.66.74"));
        System.out.println(getIPLocation("66.249.66.74"));
    }
}
