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
 *
 * @author 杨尚川
 */
public class PrefixRule {
    
    private final AtomicInteger PREFIX_COUNTER = new AtomicInteger();

    /**
     * 前缀规则利用工具PrefixExtractor生成
     * @param wordSet 
     */
    public void prefixs(Set<String> wordSet) {
        prefix(wordSet, "A-", "not, without");
        prefix(wordSet, "AB-, ABS-", "away from, off, apart");
        prefix(wordSet, "AD-, AC-, AN-, AS-", "toward, against");
        prefix(wordSet, "AMBI-, AMB-", "around, about, on both sides");
        prefix(wordSet, "ANTE-", "before, in front of, early");
        prefix(wordSet, "ARCH-", "main, chief");
        prefix(wordSet, "BI-", "two");
        prefix(wordSet, "BENE-", "well");
        prefix(wordSet, "CIRCUM-, CIRA-", "around, about");
        prefix(wordSet, "CIS-", "on this side of");
        prefix(wordSet, "CON-", "with, together");
        prefix(wordSet, "COM-, COR-, COL-", "together, with, very");
        prefix(wordSet, "CONTRA-", "against");
        prefix(wordSet, "COUNTER-", "against");
        prefix(wordSet, "DE-", "down, down from, off, utterly");
        prefix(wordSet, "DEMI-", "half, partly belongs to");
        prefix(wordSet, "DIS-, DI-, DIF-", "apart, in different directions");
        prefix(wordSet, "DU-, DUO-", "two");
        prefix(wordSet, "EM-, EN-", "in, into");
        prefix(wordSet, "EX-, E-, EF-, EC-", "out, out of, from, away");
        prefix(wordSet, "EXTRA-,EXTRO-", "outside of, beyond");
        prefix(wordSet, "FORE-", "before");
        prefix(wordSet, "IN-, I-, IL-, IM-, IR-", "in, into, on, toward, put into,");
        prefix(wordSet, "INDU-, INDI-", "a strengthened form of IN-");
        prefix(wordSet, "INFRA-", "below, beneath, inferior to, after");
        prefix(wordSet, "INTER-, INTEL-", "among, between, at intervals");
        prefix(wordSet, "INTRA-", "in, within, inside of");
        prefix(wordSet, "INTRO-", "in, into, within");
        prefix(wordSet, "JUXTA-", "near, beside");
        prefix(wordSet, "MAL-, MALE-", "evil, badly");
        prefix(wordSet, "MEDI-, MEDIO-", "middle");
        prefix(wordSet, "MILLI-, MILLE-", "thousand");
        prefix(wordSet, "MONO-", "one");
        prefix(wordSet, "MULTI-, MULTUS-", "much, many");
        prefix(wordSet, "NE-", "not");
        prefix(wordSet, "NON-", "not (less emphatic than IN or UN)");
        prefix(wordSet, "NUL-, NULL-", "none, not any");
        prefix(wordSet, "OP-, O-", "toward, against, across, down, for");
        prefix(wordSet, "OMNI-", "all, everywhere");
        prefix(wordSet, "PED-, PEDI-", "foot");
        prefix(wordSet, "PER-, PEL-", "through, by, thoroughly, away");
        prefix(wordSet, "POST-", "behind, after (in time or place)");
        prefix(wordSet, "PRE-", "before, early, toward");
        prefix(wordSet, "PRO-, PUR-", "before, for, forth");
        prefix(wordSet, "QUADRI-, QUADR-", "four times, four fold");
        prefix(wordSet, "RE-, RED-", "back, again, against, behind");
        prefix(wordSet, "RETRO-", "backwards, behind");
        prefix(wordSet, "SE-, SED-", "aside, apart, away from");
        prefix(wordSet, "SEMI-", "half");
        prefix(wordSet, "SINE-", "without");
        prefix(wordSet, "SUB-, SUC-, SUF-", "under, beneath, inferior,");
        prefix(wordSet, "SUG-, SUM-, SUP-", "less than, in place of, secretly");
        prefix(wordSet, "SUR-, SUS-", "(same as above meanings)");
        prefix(wordSet, "SUBTER-", "beneath, secretly");
        prefix(wordSet, "SUPER-, SUPRA-", "over, above, excessively");
        prefix(wordSet, "SUR-", "over, above, excessively");
        prefix(wordSet, "TRANS-, TRA-", "across, over, beyond, through");
        prefix(wordSet, "TRI-", "three");
        prefix(wordSet, "ULTRA-", "beyond, on other side");
        prefix(wordSet, "UN-", "no, not, without");
        prefix(wordSet, "UNI-", "one");
        prefix(wordSet, "PRIM-, PRIMO-", "first");
        prefix(wordSet, "DU-", "two");
        prefix(wordSet, "BI-, BIN-", "two, twice");
        prefix(wordSet, "SECOND-", "second");
        prefix(wordSet, "TRI-", "three");
        prefix(wordSet, "TERTI-", "third");
        prefix(wordSet, "QUADR-, QUADRU-", "four");
        prefix(wordSet, "QUART-", "fourth");
        prefix(wordSet, "QUINQUE-, QUINT-", "five, fifth");
        prefix(wordSet, "SEX-, SEXT-", "six, sixth");
        prefix(wordSet, "SEPT-, SEPTEM-", "seven");
        prefix(wordSet, "OCT-", "eight");
        prefix(wordSet, "OCTAV-", "eighth");
        prefix(wordSet, "NOVEM-", "nine");
        prefix(wordSet, "NON-", "ninth");
        prefix(wordSet, "DECEM-", "ten");
        prefix(wordSet, "DECIM-", "tenth");
        prefix(wordSet, "CENT-", "hundre");
        prefix(wordSet, "MILL-", "thousand");
        prefix(wordSet, "SESQUI-", "one and a half times");
        prefix(wordSet, "SEMI-", "half, partly");
        prefix(wordSet, "A-, AN-", "not, without");
        prefix(wordSet, "ACRO-", "top, end");
        prefix(wordSet, "ALLO-", "other");
        prefix(wordSet, "AMB-, AMPHI-", "around");
        prefix(wordSet, "AMB-, AMPH-", "both, more than one");
        prefix(wordSet, "ANA-, AN-", "up, back, again, similar to");
        prefix(wordSet, "ANDRO-, ANDR-", "human, male");
        prefix(wordSet, "ANTI-, ANT-", "instead, against");
        prefix(wordSet, "APO-, AP-", "away, from, off, utterly");
        prefix(wordSet, "ARCHAE-", "ancient");
        prefix(wordSet, "AUTO-", "self");
        prefix(wordSet, "BI-", "two, twice");
        prefix(wordSet, "CACO-", "bad");
        prefix(wordSet, "CATA-, CAT-", "down, down from, against");
        prefix(wordSet, "DECA-", "ten");
        prefix(wordSet, "DERMAT-", "skin");
        prefix(wordSet, "DEUTERO-", "second, farther");
        prefix(wordSet, "DIA-, DI-", "through, across");
        prefix(wordSet, "DIS-, DI-", "two, through, across");
        prefix(wordSet, "DYS-", "bad, difficult, faulty");
        prefix(wordSet, "EC-, EX-", "out, from, off");
        prefix(wordSet, "ECO-", "environment, habitat");
        prefix(wordSet, "ECTO-", "on the outside, without");
        prefix(wordSet, "EN-, EM-", "in, into");
        prefix(wordSet, "ENDO-", "within, inside, internal");
        prefix(wordSet, "ENNEA-", "nine");
        prefix(wordSet, "EPI-, EP-", "upon, at, over, near");
        prefix(wordSet, "ESO-", "inward, within");
        prefix(wordSet, "EU-", "good, well");
        prefix(wordSet, "EXO-", "outside, external");
        prefix(wordSet, "HECATO-", "hundred");
        prefix(wordSet, "HEPTA-", "seven");
        prefix(wordSet, "HETERO-", "unlike, other");
        prefix(wordSet, "HEXA-", "six");
        prefix(wordSet, "HIER-", "sacred");
        prefix(wordSet, "HOLO-", "whole");
        prefix(wordSet, "HOMEO-", "like, similar");
        prefix(wordSet, "HOMO-", "like, similar");
        prefix(wordSet, "HYPER-", "over, above, beyond");
        prefix(wordSet, "HYPO-", "under, less than");
        prefix(wordSet, "IDIO-", "individual");
        prefix(wordSet, "IDEO-", "idea");
        prefix(wordSet, "ISO-", "equal");
        prefix(wordSet, "KILO-", "thousand");
        prefix(wordSet, "MACRO-", "large");
        prefix(wordSet, "MEGA-, MEGALO-", "large");
        prefix(wordSet, "MESO-", "middle");
        prefix(wordSet, "META-", "among, between, beyond");
        prefix(wordSet, "MICRO-", "small");
        prefix(wordSet, "MONO-", "one");
        prefix(wordSet, "MYRIAD-", "ten thousand");
        prefix(wordSet, "NEO-", "new");
        prefix(wordSet, "OCTO-", "eight");
        prefix(wordSet, "OLIG-", "few");
        prefix(wordSet, "ORTHO-", "straight, regular, upright");
        prefix(wordSet, "PALEO-", "ancient");
        prefix(wordSet, "PALIN-, PALI-", "back, again, backwards");
        prefix(wordSet, "PAN-", "all");
        prefix(wordSet, "PARA-", "beside, beyond, near, incorrectly, resembling");
        prefix(wordSet, "PACHY-", "thick");
        prefix(wordSet, "PENTA-", "five");
        prefix(wordSet, "PERI-", "around, about");
        prefix(wordSet, "POLY-", "many");
        prefix(wordSet, "PRO-", "before, forward, for");
        prefix(wordSet, "PROS-", "to, toward, besides");
        prefix(wordSet, "PROTO-", "first");
        prefix(wordSet, "PSEUDO-", "false");
        prefix(wordSet, "SCHIZO-", "cleave, cut, split");
        prefix(wordSet, "SYN-, SYM-, SYS-", "together, with");
        prefix(wordSet, "TAUTO-", "same");
        prefix(wordSet, "TELE-", "far, distant");
        prefix(wordSet, "TELEO-", "end, result");
        prefix(wordSet, "TETRA-", "four");
        prefix(wordSet, "TRI-", "three");
        prefix(wordSet, "MONO-", "one");
        prefix(wordSet, "BI-", "two");
        prefix(wordSet, "TRI-", "three");
        prefix(wordSet, "TETRA-", "four");
        prefix(wordSet, "PENTA-", "five");
        prefix(wordSet, "HEXA-", "six");
        prefix(wordSet, "HEPTA-", "seven");
        prefix(wordSet, "OCTO-", "eight");
        prefix(wordSet, "ENNEA-", "nine");
        prefix(wordSet, "DECA-", "ten");
        prefix(wordSet, "HECATO-", "hundred");
        prefix(wordSet, "KILO-", "thousand");
        prefix(wordSet, "MYRIAD-", "ten thousand");
        prefix(wordSet, "MEGA-", "one million");
    }

    public void prefix(Set<String> wordSet, String prefix) {
        prefix(wordSet, prefix, "");
    }

    public void prefix(Set<String> wordSet, String prefix, String des) {
        List<String> words = wordSet.parallelStream()
                .filter(word -> {
                    word = word.toLowerCase();
                    boolean hit = false;
                    String[] ps = prefix.toLowerCase().split(",");
                    for (String p : ps) {
                        p = p.replace("-", "").replaceAll("\\s+", "");
                        if (word.startsWith(p) && wordSet.contains(word.substring(p.length(), word.length()))) {
                            hit = true;
                            break;
                        }
                    }
                    return hit;
                })
                .sorted()
                .collect(Collectors.toList());
        System.out.println("</br><h2>" + PREFIX_COUNTER.incrementAndGet() + "、" + prefix + " (" + des + ") (hit " + words.size() + ")</h2></br>");
        AtomicInteger i = new AtomicInteger();
        words.stream().forEach(word -> System.out.println(i.incrementAndGet() + "、<a target=\"_blank\" href=\"http://www.iciba.com/" + word + "\">" + word + "</a></br>"));
    }
    public static void main(String[] args) throws Exception {
        Set<String> wordSet = WordSources.get("/words.txt", "/words_extra.txt");

        PrefixRule prefixRule = new PrefixRule();
        prefixRule.prefixs(wordSet);
    }
}
