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

package org.apdplat.superword.system;

import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordSources;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * 分级词汇过滤器
 * Created by ysc on 11/29/15.
 */
public class WordsFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;

        String words_type = request.getParameter("words_type");
        if (words_type == null) {
            words_type = "ALL";
        }
        words_type = words_type.trim();
        request.setAttribute("words_type", words_type);
        String key = "words_" + words_type;
        Set<Word> words = (Set<Word>) request.getServletContext().getAttribute(key);
        if (words == null) {
            synchronized (this){
                if (words == null) {
                    if ("ALL".equals(words_type)) {
                        words = WordSources.getAll();
                    } else if ("SYLLABUS".equals(words_type)) {
                        words = WordSources.getSyllabusVocabulary();
                    } else {
                        String resource = "/word_" + words_type + ".txt";
                        words = WordSources.get(resource);
                    }
                    request.getServletContext().setAttribute(key, words);
                }
            }
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
