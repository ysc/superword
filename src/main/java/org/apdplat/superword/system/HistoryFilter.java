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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户历史记录过滤器
 * Created by ysc on 11/29/15.
 */
public class HistoryFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        User user = (User) request.getSession().getAttribute("user");

        if(user==null){
            HttpServletResponse response = (HttpServletResponse)resp;
            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("In order to access the resource, you must sign in!<br/>");
            response.getWriter().write("<a href=\"");
            response.getWriter().write(request.getContextPath());
            response.getWriter().write("/system/register.jsp\">Sign up</a>");
            response.getWriter().write("    <a href=\"");
            response.getWriter().write(request.getContextPath());
            response.getWriter().write("/system/login.jsp\">Sign in</a>");
            //response.getWriter().write("    <a href=\"");
            //response.getWriter().write(request.getContextPath());
            //response.getWriter().write("/system/login.jspx\"><img src=\""+request.getContextPath()+"/images/qq_32.png\" alt=\"QQ Account Sign In\"/></a>");

            return;
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
