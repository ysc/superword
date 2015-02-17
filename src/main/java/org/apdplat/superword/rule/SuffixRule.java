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

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apdplat.superword.tools.WordSources;

/**
 * 从指定的英文单词的集合中找出符合后缀规则的单词
 * @author 杨尚川
 */
public class SuffixRule {

    private final AtomicInteger SUFFIX_COUNTER = new AtomicInteger();

    /**
     * 后缀规则利用工具SuffixExtractor生成
     * @param wordSet 
     */
    public void suffixs(Set<String> wordSet) {
        suffix(wordSet, "-ABLE, -ABLY", "able to, capable of being.");
        suffix(wordSet, "-ACY", "state or quality of being");
        suffix(wordSet, "-AGE", "belonging to, related to");
        suffix(wordSet, "-AL", "pertaining to, act of");
        suffix(wordSet, "-ANA", "sayings, writtings, facts of");
        suffix(wordSet, "-AN, -ANT, -ENT", "one who is");
        suffix(wordSet, "-ANCE", "act of, state of being, thing that");
        suffix(wordSet, "-ARY", "belonging to, a relation to.");
        suffix(wordSet, "-ATE", "having");
        suffix(wordSet, "-ATION, -ITION", "the act of, result of");
        suffix(wordSet, "-AR", "relating to, like; the nature of");
        suffix(wordSet, "-BULUM, -BLE", "means, instrument, place");
        suffix(wordSet, "-CIDE, -CIDAL", "killer; having power to kill");
        suffix(wordSet, "-CRAT, -CRACY", "to govern");
        suffix(wordSet, "-CULUM, -CLE", "means, instrument, place");
        suffix(wordSet, "-EN", "to become, or cause to be");
        suffix(wordSet, "-ENCE", "act, fact, quality, state");
        suffix(wordSet, "-ENCY", "state of being");
        suffix(wordSet, "-ER, -OR", "one who");
        suffix(wordSet, "-ENT", "has, shows, or does");
        suffix(wordSet, "-ERY, -RY", "a place to/for; occupation of");
        suffix(wordSet, "-ESCE", "becoming");
        suffix(wordSet, "-FY", "to make, cause to be, or become");
        suffix(wordSet, "-HOOD", "state, quality; group of");
        suffix(wordSet, "-IAN", "belonging to, a relation to");
        suffix(wordSet, "-IER", "a person concerned with");
        suffix(wordSet, "-IBLE", "able to be.");
        suffix(wordSet, "-IC", "pertaining to");
        suffix(wordSet, "-ICE", "state or quality of being");
        suffix(wordSet, "-IL, -ILE", "having to do with");
        suffix(wordSet, "-ION", "the act or result of, one who");
        suffix(wordSet, "-IUM, -Y, -CE, -GE", "the act of");
        suffix(wordSet, "-CIUM, -TIUM, -GIUM", "something connected with the act");
        suffix(wordSet, "-ISH", "of, or belonging to; like");
        suffix(wordSet, "-IST", "a person who does, makes, practices");
        suffix(wordSet, "-ITIOUS", "having the nature of.");
        suffix(wordSet, "-ITUDE, -TUDE", "state of being.");
        suffix(wordSet, "-ITY", "state, character, condition");
        suffix(wordSet, "-IVE", "one who, that which is");
        suffix(wordSet, "-LESS", "(OE) without, lacking.");
        suffix(wordSet, "-LIKE", "characteristic of; suitable for");
        suffix(wordSet, "-LY", "specified manner, extent, direction");
        suffix(wordSet, "-MEN", "result, or means");
        suffix(wordSet, "-MENTUM, -MENT", "result, or means of an act");
        suffix(wordSet, "-NESS", "state, quality of being");
        suffix(wordSet, "-OON", "one who");
        suffix(wordSet, "-OR", "act, or condition of");
        suffix(wordSet, "-ORY", "relating to, thing which, place where");
        suffix(wordSet, "-OSE, -OUS", "having, full of, characterized by");
        suffix(wordSet, "-SHIP", "quality, condition, state of");
        suffix(wordSet, "-SOME", "like, tending to be");
        suffix(wordSet, "-TION", "state of that which");
        suffix(wordSet, "-TUDE", "state of being");
        suffix(wordSet, "-ULUS, -ULOUS", "tending to");
        suffix(wordSet, "-URE", "state or act of");
        suffix(wordSet, "-UUS, -UOUS", "tending to");
        suffix(wordSet, "-VOROUS, -VORE", "eating, feeding on");
        suffix(wordSet, "-WISE", "direction, manner, in regard to");
        suffix(wordSet, "-Y", "state of being.");
        suffix(wordSet, "-AC, -IAC", "pertaining to");
        suffix(wordSet, "-AGIA, -ALGIA", "pain");
        suffix(wordSet, "-AST", "one who does");
        suffix(wordSet, "-CRACY", "government");
        suffix(wordSet, "-CLASM", "destruction");
        suffix(wordSet, "-EMIA", "condition of the blood");
        suffix(wordSet, "-GENESIS", "creation, formation");
        suffix(wordSet, "-GENIC", "suitable");
        suffix(wordSet, "-GRAM", "record");
        suffix(wordSet, "-GRAPH", "written");
        suffix(wordSet, "-GRAPHY", "process/method of writing");
        suffix(wordSet, "-IA, -Y", "act, state of");
        suffix(wordSet, "-IATRICS", "treatment of disease");
        suffix(wordSet, "-IATRY", "healing");
        suffix(wordSet, "-IC", "pertaining to, one who");
        suffix(wordSet, "-ICAL", "pertaining to, made of");
        suffix(wordSet, "-IC, -ICE", "art, science, study of");
        suffix(wordSet, "-INE", "used to form feminine nouns");
        suffix(wordSet, "-ITE", "inhabitant of, product");
        suffix(wordSet, "-ITIS", "inflammation");
        suffix(wordSet, "-ISE, -IZE", "to make to give");
        suffix(wordSet, "-ISK, -ISCUS", "little");
        suffix(wordSet, "-ISM", "the belief in, profession of");
        suffix(wordSet, "-IST", "one who believes in");
        suffix(wordSet, "-LITE, -LITH", "stone");
        suffix(wordSet, "-LYSIS", "loosening");
        suffix(wordSet, "-MA, -M, -ME", "result of");
        suffix(wordSet, "-MANCY", "prophecy");
        suffix(wordSet, "-MANIA", "madness for");
        suffix(wordSet, "-METER", "to measure");
        suffix(wordSet, "-OID", "resembling, like, shaped");
        suffix(wordSet, "-OLOGY", "science or study of");
        suffix(wordSet, "-OMA", "tumor");
        suffix(wordSet, "-ORAMA", "view");
        suffix(wordSet, "-OSIS", "abnormal condition");
        suffix(wordSet, "-PATHY", "feeling, disease");
        suffix(wordSet, "-PHILIA", "love, affinity for");
        suffix(wordSet, "-PHILIC", "love, affinity for");
        suffix(wordSet, "-PHOBIA", "fear of");
        suffix(wordSet, "-PHOR", "that which carries");
        suffix(wordSet, "-PHORIA", "production of");
        suffix(wordSet, "-PHOROUS", "producing");
        suffix(wordSet, "-POLY", "sale, selling");
        suffix(wordSet, "-SCOPE", "instrument for visual exam");
        suffix(wordSet, "-SIS", "act, state of");
        suffix(wordSet, "-THERAPY", "to nurse, care for");
        suffix(wordSet, "-TIC", "pertaining to");
        suffix(wordSet, "-Y", "state of being");
    }

    public void suffix(Set<String> wordSet, String suffix) {
        suffix(wordSet, suffix, "");
    }

    public void suffix(Set<String> wordSet, String suffix, String des) {
        List<String> words = wordSet.parallelStream()
                .filter(word -> {
                    word = word.toLowerCase();
                    boolean hit = false;
                    String[] ps = suffix.toLowerCase().split(",");
                    for (String p : ps) {
                        p = p.replace("-", "").replaceAll("\\s+", "");
                        if (word.endsWith(p) && wordSet.contains(word.substring(0, word.length() - p.length()))) {
                            hit = true;
                            break;
                        }
                    }
                    return hit;
                })
                .sorted()
                .collect(Collectors.toList());
        System.out.println("</br><h2>" + SUFFIX_COUNTER.incrementAndGet() + "、" + suffix + " (" + des + ") (hit " + words.size() + ")</h2></br>");
        AtomicInteger i = new AtomicInteger();
        words.stream().forEach(word -> System.out.println(i.incrementAndGet() + "、<a target=\"_blank\" href=\"http://www.iciba.com/" + word + "\">" + word + "</a></br>"));
    }
    public static void main(String[] args) throws Exception {
        Set<String> wordSet = WordSources.get("/words.txt", "/words_extra.txt");

        SuffixRule suffixRule = new SuffixRule();
        suffixRule.suffixs(wordSet);
    }
}
