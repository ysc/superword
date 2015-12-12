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

package org.apdplat.superword.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ysc on 12/12/15.
 */
public class TimeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeUtils.class);

    public static String toString(long time, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));
    }

    public static String toString(Date time, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(time);
    }


    public static String toString(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    public static String toString(Date time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(time);
    }

    public static long fromString(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
            if(date != null){
                return date.getTime();
            }
        } catch (Exception e) {
            LOGGER.error("time parse error: "+time, e);
        }
        return -1;
    }

    public static String getTimeEnglishDes(Long ms) {
        //处理参数为NULL的情况
        if(ms == null){
            return "";
        }
        boolean minus = false;
        if(ms < 0){
            minus = true;
            ms = -ms;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder str=new StringBuilder();
        if(day>0){
            str.append(day).append("day,");
        }
        if(hour>0){
            str.append(hour).append("hour,");
        }
        if(minute>0){
            str.append(minute).append("minute,");
        }
        if(second>0){
            str.append(second).append("second,");
        }
        if(milliSecond>0){
            str.append(milliSecond).append("milli second,");
        }
        if(str.length()>0){
            str.setLength(str.length() - 1);
        }

        if(minus){
            return "-"+str.toString();
        }

        return str.toString();
    }

    public static String getTimeDes(Long ms) {
        //处理参数为NULL的情况
        if(ms == null){
            return "";
        }
        boolean minus = false;
        if(ms < 0){
            minus = true;
            ms = -ms;
        }
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder str=new StringBuilder();
        if(day>0){
            str.append(day).append("天,");
        }
        if(hour>0){
            str.append(hour).append("小时,");
        }
        if(minute>0){
            str.append(minute).append("分钟,");
        }
        if(second>0){
            str.append(second).append("秒,");
        }
        if(milliSecond>0){
            str.append(milliSecond).append("毫秒,");
        }
        if(str.length()>0){
            str.setLength(str.length() - 1);
        }

        if(minus){
            return "-"+str.toString();
        }

        return str.toString();
    }

    public static void main(String[] args) {
        //String time = "2015-08-06 00:00:38";
        //String time = "2015-08-16 00:00:04,199";
        String time = "2015-08-16 00:00:00";
        System.out.println("time:"+time);
        long t = fromString(time);
        System.out.println("time:"+t);
        String ts = toString(t);
        System.out.println("time:"+ts);

        System.out.println(TimeUtils.toString(new Date(), "yyyyMMddHHmm"));

        System.out.println(getTimeDes(10000l));
    }
}
