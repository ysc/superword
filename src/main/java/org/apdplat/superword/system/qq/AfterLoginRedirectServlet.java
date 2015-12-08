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

package org.apdplat.superword.system.qq;

import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.apdplat.superword.model.QQUser;
import org.apdplat.superword.tools.MySQLUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class AfterLoginRedirectServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");

        PrintWriter out = response.getWriter();

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

            QQUser qqUser = new QQUser();
            qqUser.setDateTime(new Date());
            if (!accessTokenObj.getAccessToken().equals("")) {
                String openID        = null;
                String accessToken = accessTokenObj.getAccessToken();
                long tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("access_token", accessToken);
                request.getSession().setAttribute("token_expire_in", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                qqUser.setUserName(openID);
                request.getSession().setAttribute("qqUser", qqUser);
                // 利用获取到的accessToken 去获取当前用户的openid --------- end

                // 利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                    //获取用户的昵称
                    qqUser.setNickname(userInfoBean.getNickname());
                    //获取用户的性别
                    qqUser.setGender(userInfoBean.getGender());
                    //获取用户的Qzone头像----------------------start
                    qqUser.setAvatarURL30(userInfoBean.getAvatar().getAvatarURL30());
                    qqUser.setAvatarURL50(userInfoBean.getAvatar().getAvatarURL50());
                    qqUser.setAvatarURL100(userInfoBean.getAvatar().getAvatarURL100());
                    //获取用户的Qzone头像----------------------end
                }

                // 利用获取到的accessToken,openid 去获取用户在微博的昵称等信息
                com.qq.connect.api.weibo.UserInfo weiboUserInfo = new com.qq.connect.api.weibo.UserInfo(accessToken, openID);
                com.qq.connect.javabeans.weibo.UserInfoBean weiboUserInfoBean = weiboUserInfo.getUserInfo();
                if (weiboUserInfoBean.getRet() == 0) {
                    //获取用户的微博头像----------------------start
                    if(qqUser.getAvatarURL30() == null){
                        qqUser.setAvatarURL30(weiboUserInfoBean.getAvatar().getAvatarURL30());
                    }
                    if(qqUser.getAvatarURL50() == null) {
                        qqUser.setAvatarURL50(weiboUserInfoBean.getAvatar().getAvatarURL50());
                    }
                    if(qqUser.getAvatarURL100() == null) {
                        qqUser.setAvatarURL100(weiboUserInfoBean.getAvatar().getAvatarURL100());
                    }
                    //获取用户的微博头像 ---------------------end

                    //获取用户的生日信息
                    qqUser.setBirthday(weiboUserInfoBean.getBirthday().getYear() + "-" +
                            weiboUserInfoBean.getBirthday().getMonth() + "-" +
                            weiboUserInfoBean.getBirthday().getDay());

                    //获取用户的地址信息
                    qqUser.setLocation(weiboUserInfoBean.getLocation());
                }
            }

            if(MySQLUtils.processQQUser(qqUser)){
                request.getSession().setAttribute("user", qqUser);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.println("<h1><a href=\""+request.getContextPath()+"/index.jsp\">Failed to login via QQ, please try again or use other ways.</a></h1>");
    }
}
