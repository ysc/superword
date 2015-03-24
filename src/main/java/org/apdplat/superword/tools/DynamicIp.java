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

package org.apdplat.superword.tools;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 *
 * 自动更改IP地址反爬虫封锁，支持多线程
 *
 * ADSL拨号上网使用动态IP地址，每一次拨号得到的IP都不一样
 *
 * 使用腾达300M无线路由器，型号：N302 v2
 * 其他的路由器使用方法类似，参照本类替换相应的登录地址、断开连接及建立连接地址即可
 *
 * @author 杨尚川
 */
public class DynamicIp {
    private DynamicIp(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicIp.class);
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "192.168.0.1";
    private static final String REFERER = "http://192.168.0.1/login.asp";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";
    private static volatile boolean isDialing = false;

    public static void main(String[] args) {
        toNewIp();
    }

    /**
     * 假设有10个线程在跑，大家都正常的跑，跑着跑着达到限制了，
     * 于是大家争先恐后（几乎是同时）请求拨号，
     * 这个时候同步的作用就显示出来了，只会有一个线程能拨号，
     * 在他结束之前其他线程都在等，等他拨号成功之后，
     * 其他线程会被唤醒并返回
     * @return
     */
    public static boolean toNewIp() {
        LOGGER.info(Thread.currentThread()+"请求重新拨号");
        synchronized (DynamicIp.class) {
            if (isDialing) {
                LOGGER.info(Thread.currentThread()+"已经有其他线程在进行拨号了，我睡觉等待吧，其他线程拨号完毕会叫醒我的");
                try {
                    DynamicIp.class.wait();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                LOGGER.info(Thread.currentThread()+"其他线程已经拨完号了，我可以返回了");
                return true;
            }
            isDialing = true;
        }
        long start = System.currentTimeMillis();
        Map<String, String> cookies = login("username***", "password***", "phonenumber***");
        if("true".equals(cookies.get("success"))) {
            LOGGER.info(Thread.currentThread()+"登陆成功");
            cookies.remove("success");
            while (!disConnect(cookies)) {
                LOGGER.info(Thread.currentThread()+"断开连接失败，重试！");
            }
            LOGGER.info(Thread.currentThread()+"断开连接成功");
            while (!connect(cookies)) {
                LOGGER.info(Thread.currentThread()+"建立连接失败，重试！");
            }
            LOGGER.info(Thread.currentThread()+"建立连接成功");
            LOGGER.info(Thread.currentThread()+"自动更改IP地址成功！");
            LOGGER.info(Thread.currentThread()+"拨号耗时："+(System.currentTimeMillis()-start)+"毫秒");
            //通知其他线程拨号成功
            synchronized (DynamicIp.class) {
                DynamicIp.class.notifyAll();
            }
            isDialing = false;
            return true;
        }
        isDialing = false;
        return false;
    }

    public static boolean connect(Map<String, String> cookies){
        return execute(cookies, "3");
    }
    public static boolean disConnect(Map<String, String> cookies){
        return execute(cookies, "4");
    }
    public static boolean execute(Map<String, String> cookies, String action){
        String url = "http://192.168.0.1/goform/SysStatusHandle";
        Map<String, String> map = new HashMap<>();
        map.put("action", action);
        map.put("CMD", "WAN_CON");
        map.put("GO", "system_status.asp");
        Connection conn = Jsoup.connect(url)
                .header("Accept", ACCEPT)
                .header("Accept-Encoding", ENCODING)
                .header("Accept-Language", LANGUAGE)
                .header("Connection", CONNECTION)
                .header("Host", HOST)
                .header("Referer", REFERER)
                .header("User-Agent", USER_AGENT)
                .ignoreContentType(true)
                .timeout(30000);
        for(String cookie : cookies.keySet()){
            conn.cookie(cookie, cookies.get(cookie));
        }

        String title = null;
        try {
            Connection.Response response = conn.method(Connection.Method.POST).data(map).execute();
            String html = response.body();
            Document doc = Jsoup.parse(html);
            title = doc.title();
            LOGGER.info("操作连接页面标题："+title);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        if("LAN | LAN Settings".equals(title)){
            //发出命令5秒之后再检查网络状态
            try{Thread.sleep(5000);}catch (Exception e){LOGGER.error(e.getMessage(), e);}
            if(("3".equals(action) && isConnected())
                    || ("4".equals(action) && !isConnected())){
                return true;
            }
        }
        return false;
    }
    public static boolean isConnected(){
        try {
            Document doc = Jsoup.connect("http://www.baidu.com/s?wd=杨尚川&t=" + System.currentTimeMillis())
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("Referer", "https://www.baidu.com")
                    .header("Host", "www.baidu.com")
                    .header("User-Agent", USER_AGENT)
                    .ignoreContentType(true)
                    .timeout(30000)
                    .get();
            LOGGER.info("搜索结果页面标题："+doc.title());
            if(doc.title() != null && doc.title().contains("杨尚川")){
                return true;
            }
        }catch (Exception e){
            if("Network is unreachable".equals(e.getMessage())){
                return false;
            }else{
                LOGGER.error("状态检查失败:"+e.getMessage(), e);
            }
        }
        return false;
    }
    public static Map<String, String> login(String userName, String password, String verify){
        try {
            Map<String, String> map = new HashMap<>();
            map.put("Username", userName);
            map.put("Password", password);
            map.put("checkEn", "0");
            Connection conn = Jsoup.connect("http://192.168.0.1/LoginCheck")
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("Referer", REFERER)
                    .header("Host", HOST)
                    .header("User-Agent", USER_AGENT)
                    .ignoreContentType(true)
                    .timeout(30000);

            Connection.Response response = conn.method(Connection.Method.POST).data(map).execute();
            String html = response.body();
            Document doc = Jsoup.parse(html);
            LOGGER.info("登陆页面标题："+doc.title());
            Map<String, String> cookies = response.cookies();
            if(html.contains(verify)){
                cookies.put("success", Boolean.TRUE.toString());
            }
            LOGGER.info("*******************************************************cookies start:");
            cookies.keySet().stream().forEach((cookie) -> {
                LOGGER.info(cookie + ":" + cookies.get(cookie));
            });
            LOGGER.info("*******************************************************cookies end:");
            return cookies;
        }catch (Exception e){
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
