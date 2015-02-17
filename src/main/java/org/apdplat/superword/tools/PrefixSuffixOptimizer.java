/**
 *
 * APDPlat - Application Product Development Platform Copyright (c) 2013, 杨尚川,
 * yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.apdplat.superword.tools;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author 杨尚川
 */
public class PrefixSuffixOptimizer {

    private static final String SRC_HTML = "/tools/prefix_suffix.txt";
    private static final String DST_HTML = "target/prefix_suffix_replaced.txt";
    private static final String DST_WORD = "target/prefix_suffix_words.txt";
    private static final Set<String> WORDS = new HashSet<>();

    public static void main(String[] args) throws Exception {
        InputStream in = PrefixSuffixOptimizer.class.getResourceAsStream(SRC_HTML);
        Document document = Jsoup.parse(in, "utf-8", "");
        document.select("table tbody tr td p")
                .stream()
                .forEach(PrefixSuffixOptimizer::replace);
        Files.write(Paths.get(DST_HTML), document.body().html().getBytes("utf-8"));
        
        AtomicInteger i = new AtomicInteger();
        StringBuilder text = new StringBuilder();
        WORDS.stream()
                .sorted()
                .forEach(word -> text.append(i.incrementAndGet())
                                     .append("\t")
                                     .append(word)
                                     .append("\n"));
        Files.write(Paths.get(DST_WORD), text.toString().getBytes("utf-8"));
    }

    /**
     * 为元素中的英文单词添加链接，点击之后跳转到爱词霸页面
     *
     * @param element
     */
    public static void replace(Element element) {
        String oldText = element.text();
        StringBuilder newText = new StringBuilder();
        System.out.println("oldText: " + oldText);
        String[] items = oldText.trim().replace(".", ",").split(",");
        for (String item : items) {
            item = item.trim();
            if (!StringUtils.isAlpha(item)) {
                newText.append(item).append(", ");
                continue;
            }
            if (StringUtils.isAllUpperCase(item)) {
                newText.append("<strong><a target=\"_blank\" href=\"http://www.iciba.com/")
                       .append(item)
                       .append("\">")
                       .append(item)
                       .append("</a></strong>")
                       .append(", ");
            } else {
                newText.append("<a target=\"_blank\" href=\"http://www.iciba.com/")
                       .append(item)
                       .append("\">")
                       .append(item)
                       .append("</a>")
                       .append(", ");
            }
            WORDS.add(item.toLowerCase());
        }
        if (newText.length() > 2) {
            String text = newText.substring(0, newText.length() - 2);
            System.out.println("newText: " + text);
            element.html(text);
        }
    }
}
