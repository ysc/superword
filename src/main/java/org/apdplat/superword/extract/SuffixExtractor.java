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

package org.apdplat.superword.extract;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apdplat.superword.model.Suffix;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 从 http://www.palosverdes.com/jesse/pvphs/www-freecollege-com-vocab.htm 文章中
 * 提取后缀
 * @author 杨尚川
 */
public class SuffixExtractor {
    private static final String SRC_HTML = "/tools/prefix_suffix.txt";

    public static List<Suffix> extract() {
        try(InputStream in = SuffixExtractor.class.getResourceAsStream(SRC_HTML)) {
            Document document = Jsoup.parse(in, "utf-8", "");
            return document
                    .select("table tbody tr")
                    .stream()
                    .map(SuffixExtractor::extractSuffix)
                    .filter(suffix -> suffix.getSuffix() != null)
                    .sorted()
                    .distinct()
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static Suffix extractSuffix(Element element){
        Suffix suffix = new Suffix();
        List<Element> tds = element.children();
        if(tds==null || tds.size()!=3){
            return suffix;
        }
        String s = tds.get(0).text().trim();
        if(!s.startsWith("-")){
            return suffix;
        }
        String des = tds.get(1).text();
        return new Suffix(s, des);
    }
    public static void main(String[] args){
        extract()
                .forEach(suffix ->
                        System.out.println("suffix(wordSet, \""
                                + suffix.getSuffix()
                                + "\", \""
                                + suffix.getDes()
                                + "\");"));
    }
}
