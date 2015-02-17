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

package org.apdplat.superword.tools;

import java.io.InputStream;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 从 http://www.palosverdes.com/jesse/pvphs/www-freecollege-com-vocab.htm 文章中
 * 提取前缀
 * @author 杨尚川
 */
public class PrefixExtractor {
    private static final String SRC_HTML = "/tools/prefix_suffix.txt";
    
    public static void main(String[] args) throws Exception {
        InputStream in = PrefixExtractor.class.getResourceAsStream(SRC_HTML);
        Document document = Jsoup.parse(in, "utf-8", "");
        document.select("table tbody tr")
                .stream()
                .forEach(PrefixExtractor::extractPrefix);
    }
    
    public static void extractPrefix(Element element){
        List<Element> tds = element.children();
        if(tds==null || tds.size()!=3){
            return ;
        }
        String prefix = tds.get(0).text().trim();
        if(!prefix.endsWith("-")){
            return;
        }
        System.out.println("prefix(wordSet, \""+prefix+"\", \""+tds.get(1).text()+"\");");
    }
}
