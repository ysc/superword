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

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.QuizItem;
import org.apdplat.superword.model.User;
import org.apdplat.superword.tools.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 反爬虫反机器人攻击
 * Created by ysc on 12/4/15.
 */
public class AntiRobotFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AntiRobotFilter.class);

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static volatile int limit = 1000;
    public static volatile int invalidCount = 0;

    private static ServletContext servletContext = null;

    public void destroy() {
    }

    private String getKey(HttpServletRequest request){
        String ip = request.getRemoteAddr();
        User user = (User) request.getSession().getAttribute("user");
        String userString = user==null?"anonymity":user.getUserName();
        return "anti-robot-"+userString+"-"+ip+"-"+request.getHeader("User-Agent").replace("-", "_")+"-"+SIMPLE_DATE_FORMAT.format(new Date());
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;

        if(request.getRequestURI().endsWith("/favicon.ico")){
            chain.doFilter(req, resp);
            return;
        }

        if(servletContext == null){
            servletContext = request.getServletContext();
        }

        HttpServletResponse response = (HttpServletResponse)resp;
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        String userAgent = request.getHeader("User-Agent");
        if(StringUtils.isBlank(userAgent)
                || userAgent.length() < 50
                || userAgent.contains("Java")
                || userAgent.contains("bingbot")
                || userAgent.contains("360Spider")
                || userAgent.contains("HaosouSpider")
                || userAgent.contains("Googlebot")){
            invalidCount++;
            response.getWriter().write("Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading, including but not limited to, spelling similarity, definition similarity, pronunciation similarity, the transformation rules of the spelling, the prefix and the dynamic prefix, the suffix and the dynamic suffix, roots, compound words, text auxiliary reading, web page auxiliary reading, book auxiliary reading, etc..");
            return;
        }

        HttpSession session = request.getSession(true);
        if(session.getAttribute("isHuman") == null){
            AtomicInteger identifyCount = (AtomicInteger)session.getAttribute("identifyCount");
            if(identifyCount == null){
                identifyCount = new AtomicInteger();
                session.setAttribute("identifyCount", identifyCount);
            }
            identifyCount.incrementAndGet();
            if(identifyCount.intValue() > 200){
                response.getWriter().write("System has detected that you may not be a human, because you can't answer the question correctly.");
                return;
            }

            if(session.getAttribute("forward") == null){
                session.setAttribute("forward", StringUtils.isBlank(request.getPathInfo()) ? "/" : request.getPathInfo());
            }

            String _token = request.getParameter("token");
            String _word = request.getParameter("word");
            String _answer = request.getParameter("answer");
            if(StringUtils.isNotBlank(_token)
                    && StringUtils.isNotBlank(_word)
                    && StringUtils.isNotBlank(_answer)
                    && session.getAttribute("token") != null
                    && session.getAttribute("token").toString().equals(_token)
                    && session.getAttribute("quizItem") != null){
                session.setAttribute("token", null);
                QuizItem quizItem = (QuizItem)session.getAttribute("quizItem");
                if(_word.equals(quizItem.getWord().getWord())){
                    quizItem.setAnswer(_answer);
                    if(quizItem.isRight()){
                        String path = session.getAttribute("forward").toString();
                        if(path.contains("identify.quiz")){
                            path = path.replace("identify.quiz", "");
                        }
                        session.setAttribute("forward", null);
                        session.setAttribute("isHuman", "true");
                        request.getRequestDispatcher(path).forward(request, response);
                        return;
                    }else{
                        StringBuilder html = new StringBuilder();
                        html.append("<h1>The meaning of red color font is your answer, but the right answer is the meaning of blue color font for the word <font color=\"red\">")
                                .append(quizItem.getWord().getWord())
                                .append(":</font></h1>");
                        html.append("<h2><ul>");
                        for(String option : quizItem.getMeanings()){
                            html.append("<li>");
                            if(option.equals(_answer)) {
                                html.append("<font color=\"red\">").append(option).append("</font>");
                            }else if(option.equals(quizItem.getWord().getMeaning())){
                                html.append("<font color=\"blue\">").append(option).append("</font>");
                            }else{
                                html.append(option);
                            }
                            html.append("</li>\n");
                        }
                        html.append("</ul></h2>\n<h1><a href=\"")
                                .append(servletContext.getContextPath())
                                .append("\">Continue...</a></h1>\n");
                        response.getWriter().write(html.toString());
                        return;
                    }
                }
            }

            QuizItem quizItem = QuizItem.buildIdentifyHumanQuiz(12);
            String token = UUID.randomUUID().toString();
            session.setAttribute("quizItem", quizItem);
            session.setAttribute("token", token);
            StringBuilder html = new StringBuilder();
            html.append("<h1>").append("Click the correct meaning for the word <font color=\"red\">").append(quizItem.getWord().getWord()).append(":</font></h1>\n");
            html.append("<h2><ul>");
            for(String option : quizItem.getMeanings()){
                html.append("<li>")
                        .append("<a href=\"")
                        .append(servletContext.getContextPath())
                        .append("/identify.quiz?word=")
                        .append(quizItem.getWord().getWord())
                        .append("&token=")
                        .append(token)
                        .append("&answer=")
                        .append(URLEncoder.encode(option, "utf-8"))
                        .append("\">")
                        .append(option)
                        .append("</a></li>\n");
            }
            html.append("</ul></h2>\n")
                    .append("<h1>If you can't answer the question correctly, you won't have the permission to access the web site.")
                    .append("</h1>");
            response.getWriter().write(html.toString());
            return;
        }

        String key = getKey(request);
        AtomicInteger count = (AtomicInteger)servletContext.getAttribute(key);
        if(count == null){
            count = new AtomicInteger();
            servletContext.setAttribute(key, count);
        }

        if(count.incrementAndGet() > limit){
            response.getWriter().write("System has detected that your IP visit is too frequent and has automatically forbidden your vist. We are sorry to bring inconvenience to you, please understand, please come back tomorrow. Bye Bye!");

            return;
        }

        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
        int initialDelay = 24-LocalDateTime.now().getHour();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                LOG.info("clear last day anti-robot counter");
                LocalDateTime timePoint = LocalDateTime.now().minusDays(1);
                String date = SIMPLE_DATE_FORMAT.format(Date.from(timePoint.atZone(ZoneId.systemDefault()).toInstant()));
                Map<String, Integer> archive = new HashMap<String, Integer>();
                Enumeration<String> keys = servletContext.getAttributeNames();
                while (keys.hasMoreElements()) {
                    String key = keys.nextElement();
                    if (key.startsWith("anti-robot-") && key.endsWith(date)) {
                        archive.put(key, ((AtomicInteger) servletContext.getAttribute(key)).intValue());
                    }
                }
                archive.keySet().forEach(servletContext::removeAttribute);
                File path = new File(servletContext.getRealPath("/WEB-INF/data/anti-robot-archive/"));
                if (!path.exists()) {
                    path.mkdirs();
                }
                String file = path.getPath() + "/" + date + "__user_agent_invalid_count_" + invalidCount + ".txt";
                Files.write(Paths.get(file),
                        archive.entrySet()
                                .stream()
                                .map(e -> e.getKey().replace("anti-robot-", "").replace("-", "\t") + "\t" + e.getValue())
                                .map(line -> {
                                    String[] attrs = line.split("\\s+");
                                    String location = "";
                                    if (attrs != null && attrs.length > 1) {
                                        String ip = attrs[1];
                                        location = IPUtils.getIPLocation(ip).toString();
                                    }
                                    return line+"\t"+location;
                                })
                                .collect(Collectors.toList()));
                invalidCount = 0;
                LOG.info("clear last day anti-robot counter finished: " + file);
            } catch (Exception e) {
                LOG.error("save anti-robot-archive failed", e);
            }
        }, initialDelay, 24, TimeUnit.HOURS);
    }

    public static List<String> getData(){
        Map<String, Integer> map = new HashMap<>();
        Enumeration<String> keys = servletContext.getAttributeNames();
        while(keys.hasMoreElements()){
            String key = keys.nextElement();
            if(key.startsWith("anti-robot-")){
                map.put(key.substring(11), ((AtomicInteger) servletContext.getAttribute(key)).intValue());
            }
        }
        return map
                .entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> e.getKey() + "-" + e.getValue())
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LocalDateTime timePoint = LocalDateTime.now().minusDays(1);
        String date = SIMPLE_DATE_FORMAT.format(Date.from(timePoint.atZone(ZoneId.systemDefault()).toInstant()));
        System.out.println(date);
    }
}
