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

package org.apdplat.superword.model;

/**
 * QQ用户信息
 * Created by ysc on 11/24/15.
 */
public class QQUser extends User{
    private String nickname;
    private String gender;
    private String birthday;
    private String location;
    private String avatarURL30;
    private String avatarURL50;
    private String avatarURL100;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatarURL30() {
        return avatarURL30;
    }

    public void setAvatarURL30(String avatarURL30) {
        this.avatarURL30 = avatarURL30;
    }

    public String getAvatarURL50() {
        return avatarURL50;
    }

    public void setAvatarURL50(String avatarURL50) {
        this.avatarURL50 = avatarURL50;
    }

    public String getAvatarURL100() {
        return avatarURL100;
    }

    public void setAvatarURL100(String avatarURL100) {
        this.avatarURL100 = avatarURL100;
    }

    @Override
    public String toString() {
        return "QQUser{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", dateTime=" + simpleDateFormat.format(dateTime) +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", location='" + location + '\'' +
                ", avatarURL30='" + avatarURL30 + '\'' +
                ", avatarURL50='" + avatarURL50 + '\'' +
                ", avatarURL100='" + avatarURL100 + '\'' +
                '}';
    }
}
