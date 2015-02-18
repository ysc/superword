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
import java.util.stream.Collectors;

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

    public static List<PrefixInfo> extract() {
        try(InputStream in = PrefixExtractor.class.getResourceAsStream(SRC_HTML)) {
            Document document = Jsoup.parse(in, "utf-8", "");
            return document.select("table tbody tr")
                            .stream()
                            .map(PrefixExtractor::extractPrefix)
                            .filter(item -> item.getPrefix() != null)
                            .sorted()
                            .distinct()
                            .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static PrefixInfo extractPrefix(Element element){
        PrefixInfo prefixInfo = new PrefixInfo();
        List<Element> tds = element.children();
        if(tds==null || tds.size()!=3){
            return prefixInfo;
        }
        String prefix = tds.get(0).text().trim();
        if(!prefix.endsWith("-")){
            return prefixInfo;
        }
        String des = tds.get(1).text();
        return new PrefixInfo(prefix, des);
    }
    public static class PrefixInfo implements Comparable{
        private String prefix;
        private String des;

        public PrefixInfo(){}
        public PrefixInfo(String prefix, String des) {
            this.prefix = prefix;
            this.des = des;
        }

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public int compareTo(Object o) {
            if(o == null){
                return 1;
            }
            return this.prefix.compareTo(((PrefixInfo)o).getPrefix());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrefixInfo that = (PrefixInfo) o;

            if (!prefix.equals(that.prefix)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return prefix.hashCode();
        }
    }
    public static void main(String[] args){
        extract().forEach(prefix ->
                System.out.println("prefix(wordSet, \"" + prefix.getPrefix() + "\", \"" + prefix.getDes()+"\");"));
    }
}
