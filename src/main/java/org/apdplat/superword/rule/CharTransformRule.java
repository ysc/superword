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
package org.apdplat.superword.rule;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apdplat.superword.model.CharMap;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;

/**
 * 字符转换规则:
 *
 * 单词的发展是一个历史的递进的过程，从无到有，从有到多
 * 字母之间的转化是有一定规律的，如元音字母（a e i o u)之间相互转化
 * 发音相近的辅音(如双唇音唇齿音的清辅音和浊辅音等等)之间的转化
 * 发音相同的字母和字母组合之间的转化(如ph和f)
 * 长相相近的字母之间的转化（因为字母看上去长得像，古时候手写容易错，
 * 如V和U，M和N，等等，在长期的发展过程中，不小心写错的词
 * 由于作者的影响力大或者其他因素也会演化出新的单词，并具有相关的含义）。
 * @author 杨尚川
 */
public class CharTransformRule {
    private CharTransformRule(){}

    private static final List<CharMap> CHAR_MAP_REGULAR = new ArrayList<>();

    static {
        CHAR_MAP_REGULAR.add(new CharMap("b", "p"));
        CHAR_MAP_REGULAR.add(new CharMap("b", "m"));
        CHAR_MAP_REGULAR.add(new CharMap("b", "f"));
        CHAR_MAP_REGULAR.add(new CharMap("b", "v"));

        CHAR_MAP_REGULAR.add(new CharMap("p", "m"));
        CHAR_MAP_REGULAR.add(new CharMap("p", "f"));
        CHAR_MAP_REGULAR.add(new CharMap("p", "v"));

        CHAR_MAP_REGULAR.add(new CharMap("m", "f"));
        CHAR_MAP_REGULAR.add(new CharMap("m", "v"));

        CHAR_MAP_REGULAR.add(new CharMap("f", "v"));

        CHAR_MAP_REGULAR.add(new CharMap("d", "t"));
        CHAR_MAP_REGULAR.add(new CharMap("d", "s"));
        CHAR_MAP_REGULAR.add(new CharMap("d", "c"));
        CHAR_MAP_REGULAR.add(new CharMap("d", "z"));
        CHAR_MAP_REGULAR.add(new CharMap("d", "th"));

        CHAR_MAP_REGULAR.add(new CharMap("t", "s"));
        CHAR_MAP_REGULAR.add(new CharMap("t", "c"));
        CHAR_MAP_REGULAR.add(new CharMap("t", "z"));
        CHAR_MAP_REGULAR.add(new CharMap("t", "th"));

        CHAR_MAP_REGULAR.add(new CharMap("s", "c"));
        CHAR_MAP_REGULAR.add(new CharMap("s", "z"));
        CHAR_MAP_REGULAR.add(new CharMap("s", "th"));

        CHAR_MAP_REGULAR.add(new CharMap("c", "z"));
        CHAR_MAP_REGULAR.add(new CharMap("c", "th"));
        CHAR_MAP_REGULAR.add(new CharMap("ch", "k"));

        CHAR_MAP_REGULAR.add(new CharMap("z", "th"));

        CHAR_MAP_REGULAR.add(new CharMap("g", "k"));
        CHAR_MAP_REGULAR.add(new CharMap("g", "c"));
        CHAR_MAP_REGULAR.add(new CharMap("g", "h"));

        CHAR_MAP_REGULAR.add(new CharMap("k", "c"));
        CHAR_MAP_REGULAR.add(new CharMap("k", "h"));

        CHAR_MAP_REGULAR.add(new CharMap("c", "h"));

        CHAR_MAP_REGULAR.add(new CharMap("r", "l"));
        CHAR_MAP_REGULAR.add(new CharMap("r", "n"));

        CHAR_MAP_REGULAR.add(new CharMap("l", "n"));

        CHAR_MAP_REGULAR.add(new CharMap("m", "n"));

        CHAR_MAP_REGULAR.add(new CharMap("a", "e"));
        CHAR_MAP_REGULAR.add(new CharMap("a", "i"));
        CHAR_MAP_REGULAR.add(new CharMap("a", "o"));
        CHAR_MAP_REGULAR.add(new CharMap("a", "u"));

        CHAR_MAP_REGULAR.add(new CharMap("e", "i"));
        CHAR_MAP_REGULAR.add(new CharMap("e", "o"));
        CHAR_MAP_REGULAR.add(new CharMap("e", "u"));

        CHAR_MAP_REGULAR.add(new CharMap("i", "o"));
        CHAR_MAP_REGULAR.add(new CharMap("i", "u"));

        CHAR_MAP_REGULAR.add(new CharMap("o", "u"));

        //发音相同的字母和字母组合
        CHAR_MAP_REGULAR.add(new CharMap("ph", "f"));
        //字母长得像，容易写错
        CHAR_MAP_REGULAR.add(new CharMap("v", "u"));
        CHAR_MAP_REGULAR.add(new CharMap("v", "w"));
        CHAR_MAP_REGULAR.add(new CharMap("u", "w"));
        CHAR_MAP_REGULAR.add(new CharMap("i", "l"));
        CHAR_MAP_REGULAR.add(new CharMap("i", "j"));
        CHAR_MAP_REGULAR.add(new CharMap("f", "t"));
        CHAR_MAP_REGULAR.add(new CharMap("m", "w"));
    }

    public static String toHtmlFragmentForWord(Map<Word,  Map<CharMap, List<Word>>> data){
        StringBuilder result = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        data.keySet().forEach(target -> {
            if(data.size() > 1) {
                result.append(i.incrementAndGet())
                        .append(". ")
                        .append(target.getWord())
                        .append("</br>\n");
            }
            AtomicInteger j = new AtomicInteger();
            data.get(target).keySet().forEach(charMap -> {
                result.append("\t")
                        .append(j.incrementAndGet())
                        .append(". ")
                        .append(charMap.getFrom())
                        .append(" - ")
                        .append(charMap.getTo())
                        .append("\n");
                String from = charMap.getFrom();
                String to = charMap.getTo();
                result.append("<ol>\n");
                data.get(target).get(charMap).forEach(word -> {
                    result.append("\t\t")
                            .append("<li>")
                            .append(WordLinker.toLink(word.getWord(), from))
                            .append(" -> ")
                            .append(WordLinker.toLink(word.getWord().replaceAll(from, to), to))
                            .append("</li>\n");
                });
                result.append("</ol>\n");
            });
        });
        return result.toString();
    }

    public static Map<Word,  Map<CharMap, List<Word>>> transforms(Set<Word> words, Word target){
        return transforms(words, new HashSet<Word>(Arrays.asList(target)));
    }

    public static Map<Word,  Map<CharMap, List<Word>>> transforms(Set<Word> words, Set<Word> targets){
        Map<CharMap, List<Word>> data = transforms(words);
        Map<Word,  Map<CharMap, List<Word>>> result = new ConcurrentHashMap<>();
        targets.parallelStream().forEach(target -> {
            Map<CharMap, List<Word>> t = new HashMap<>();
            data.entrySet().parallelStream().forEach(entry -> {
                List<Word> w = new ArrayList<>();
                String from = entry.getKey().getFrom();
                String to = entry.getKey().getTo();
                entry.getValue().parallelStream().forEach(word -> {
                    String old = word.getWord();
                    if (target.getWord().equals((old))) {
                        w.add(word);
                    } else if (target.getWord().equals(old.replaceAll(from, to))) {
                        w.add(word);
                    }
                });
                if (!w.isEmpty()) {
                    t.put(entry.getKey(), w);
                }
            });
            if(!t.isEmpty()){
                result.put(target, t);
            }
        });
        return result;
    }

    public static Map<CharMap, List<Word>> transforms(Set<Word> words) {
        Map<CharMap, List<Word>> result = new ConcurrentHashMap<>();

        CHAR_MAP_REGULAR.parallelStream().forEach(charMap -> result.putAll(transform(words, charMap)));

        return result;
    }

    /**
     * 将单词中的一部分字母转变为另一部分字母
     * @param words 英文单词的集合
     * @param charMap 转化的字母或字母组合规则描述
     */
    public static Map<CharMap, List<Word>> transform(Set<Word> words, CharMap charMap) {
        String from = charMap.getFrom();
        String to = charMap.getTo();
        List<Word> list =
            words.parallelStream()
                 .filter(word ->
                             word.getWord().contains(from)
                             && words.contains(
                                     new Word(
                                             word.getWord().replaceAll(from, to), null)))
                 .sorted()
                 .collect(Collectors.toList());
        Map<CharMap, List<Word>> result = new HashMap<>();
        if(!list.isEmpty()) {
            result.put(new CharMap(from, to), list);
        }
        return result;
    }

    public static String toHtmlFragmentForRule(Map<CharMap, List<Word>> data){
        StringBuilder html = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        List<CharMap> sortedList = new ArrayList<>(data.keySet());
        Collections.sort(sortedList);
        sortedList.forEach(charMap -> {
            String from = charMap.getFrom();
            String to = charMap.getTo();
            List<Word> list = data.get(charMap);

            html.append("<h2>")
                    .append(i.incrementAndGet())
                    .append("、")
                    .append(from)
                    .append(" - ")
                    .append(to)
                    .append(" rule total number: ")
                    .append(list.size())
                    .append("</h2></br>\n");

            AtomicInteger j = new AtomicInteger();
            list.stream()
                    .forEach(word -> html.append("\t")
                            .append(j.incrementAndGet())
                            .append("、")
                            .append(WordLinker.toLink(word.getWord()))
                            .append(" -> ")
                            .append(WordLinker.toLink(word.getWord().replaceAll(from, to)))
                            .append("</br>\n"));
        });
        return html.toString();
    }

    public static void main(String[] args) throws Exception {
        WordLinker.serverRedirect = null;
        WordLinker.jsDefinition = false;

        Set<Word> words = WordSources.getSyllabusVocabulary();

        //Map<CharMap, List<Word>> data = CharTransformRule.transforms(words);
        //String html = CharTransformRule.toHtmlFragmentForRule(data);

        Map<Word,  Map<CharMap, List<Word>>> data2 = CharTransformRule.transforms(words, new Word("back", ""));
        String html2 = CharTransformRule.toHtmlFragmentForWord(data2);

        //System.out.println(html);
        System.out.println(html2);

        //Files.write(Paths.get("target/char_transform_rule.txt"), Arrays.asList(html, html2));
        Files.write(Paths.get("target/char_transform_rule.txt"), Arrays.asList(html2));
    }
}
