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

import org.apdplat.superword.model.User;
import org.apdplat.superword.model.UserWord;
import org.apdplat.superword.tools.MySQLUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by ysc on 11/28/15.
 */
public class ViewWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    private void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = request.getParameter("url");
        String word = request.getParameter("word");
        String dict = request.getParameter("dict");

        if(url==null || word==null || dict==null){
            return;
        }

        User user = (User)request.getSession().getAttribute("user");

        UserWord userWord = new UserWord();
        userWord.setDateTime(new Date());
        userWord.setUserName(user==null?"anonymity":user.getUserName());
        userWord.setWord(word);
        userWord.setDictionary(dict);
        MySQLUtils.saveUserWordToDatabase(userWord);

        response.sendRedirect(url);
    }
}
