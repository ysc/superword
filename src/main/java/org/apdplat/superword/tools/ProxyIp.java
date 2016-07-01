/*
 * *
 *  *
 *  * APDPlat - Application Product Development Platform
 *  * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *
 */

package org.apdplat.superword.tools;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * 自动更改IP地址反爬虫封锁，支持多线程
 * 使用代理服务器的方式
 *
 * @author 杨尚川
 */
public class ProxyIp {
    private ProxyIp(){}
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyIp.class);
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";
    private static volatile boolean isSwitching = false;
    private static volatile long lastSwitchTime = 0l;
    private static final WebClient WEB_CLIENT = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);
    private static final Pattern IP_PATTERN = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
    //可用代理IP列表
    private static final List<String> IPS = new Vector<>();
    private static volatile int currentIpIndex = 0;
    private static volatile boolean detect = true;
    //五分钟
    private static volatile int detectInterval = 300000;
    private static final Path PROXY_IPS_FILE = Paths.get("src/main/resources/proxy_ips.txt");
    //自身IP地址
    private static String previousIp = getCurrentIp();
    //能隐藏自己IP的代理
    private static final Set<String> EXCELLENT_IPS = new ConcurrentSkipListSet<>();
    private static final Set<String> EXCELLENT_USA_IPS = new ConcurrentSkipListSet<>();
    //不能隐藏自己IP的代理
    private static final Set<String> NORMAL_IPS = new ConcurrentSkipListSet<>();
    private static final Path EXCELLENT_PROXY_IPS_FILE = Paths.get("src/main/resources/proxy_ips_excellent.txt");;
    private static final Path EXCELLENT_USA_PROXY_IPS_FILE = Paths.get("src/main/resources/proxy_ips_excellent_usa.txt");
    private static final Path NORMAL_PROXY_IPS_FILE = Paths.get("src/main/resources/proxy_ips_normal.txt");
    static {
        Set<String> ipSet = new HashSet<>();
        //如果本地有则读取
        try {
            if(Files.notExists(PROXY_IPS_FILE.getParent())){
                PROXY_IPS_FILE.getParent().toFile().mkdirs();
            }
            if(Files.notExists(PROXY_IPS_FILE)){
                PROXY_IPS_FILE.toFile().createNewFile();
            }
            if(Files.notExists(EXCELLENT_PROXY_IPS_FILE)){
                EXCELLENT_PROXY_IPS_FILE.toFile().createNewFile();
            }
            if(Files.notExists(EXCELLENT_USA_PROXY_IPS_FILE)){
                EXCELLENT_USA_PROXY_IPS_FILE.toFile().createNewFile();
            }
            if(Files.notExists(NORMAL_PROXY_IPS_FILE)){
                NORMAL_PROXY_IPS_FILE.toFile().createNewFile();
            }
            LOGGER.info("代理IP存放路径："+PROXY_IPS_FILE.toAbsolutePath().toString());
            ipSet.addAll(Files.readAllLines(PROXY_IPS_FILE));
            ipSet.addAll(Files.readAllLines(EXCELLENT_PROXY_IPS_FILE));
        }catch (Exception e){
            LOGGER.error("读取本地代理IP失败", e);
        }
        if(ipSet.isEmpty()){
            //从已知的网站获取代理IP和端口
            ipSet.addAll(getProxyIps());
        }
        IPS.addAll(ipSet);
        LOGGER.info("所有IP列表("+IPS.size()+")：");
        AtomicInteger i = new AtomicInteger();
        IPS.forEach(ip->LOGGER.info(i.incrementAndGet()+"、"+ip));

        new Thread(()->{
            //检查次数
            int count=0;
            while(detect) {
                try {
                    save();
                    if(count%10==9){
                        //也要防止被更新IP站点封锁
                        toNewIp();
                    }
                    Thread.sleep(detectInterval);
                    //检查网站是否有新IP
                    getProxyIps().forEach(ip -> {
                        if (!IPS.contains(ip)) {
                            IPS.add(ip);
                            LOGGER.info("发现新代理IP：" + ip);
                        }
                    });
                    count++;
                } catch (Exception e) {
                    LOGGER.error("更新代理IP出错", e);
                }
            }
        }).start();
    }
    public static void stopDetect(){
        detect = false;
    }
    public static void startDetect(){
        detect = true;
    }
    private static void save(){
        try {
            //将本地的和新发现的代理IP进行合并保存到本地
            Set<String> ips = new ConcurrentSkipListSet<>();
            ips.addAll(Files.readAllLines(PROXY_IPS_FILE));
            ips.addAll(IPS);
            //移除不能隐藏自己的IP
            ips.removeAll(NORMAL_IPS);
            Files.write(PROXY_IPS_FILE, toVerify(ips));
            LOGGER.info("将" + ips.size() + "条代理IP地址写入本地");
            Set<String> excellentIps = new HashSet<>();
            excellentIps.addAll(Files.readAllLines(EXCELLENT_PROXY_IPS_FILE));
            excellentIps.addAll(EXCELLENT_IPS);
            Files.write(EXCELLENT_PROXY_IPS_FILE, toVerify(excellentIps));
            LOGGER.info("将" + excellentIps.size() + "条能隐藏自己的代理IP地址写入本地");
            Set<String> excellentUsaIps = new HashSet<>();
            excellentUsaIps.addAll(Files.readAllLines(EXCELLENT_USA_PROXY_IPS_FILE));
            excellentUsaIps.addAll(EXCELLENT_USA_IPS);
            Files.write(EXCELLENT_USA_PROXY_IPS_FILE, toVerify(excellentUsaIps));
            LOGGER.info("将" + excellentUsaIps.size() + "条能隐藏自己的美国代理IP地址写入本地");
            Set<String> normalIps = new HashSet<>();
            normalIps.addAll(Files.readAllLines(NORMAL_PROXY_IPS_FILE));
            normalIps.addAll(NORMAL_IPS);
            Files.write(NORMAL_PROXY_IPS_FILE, toVerify(normalIps));
            LOGGER.info("将" + normalIps.size() + "条不能隐藏自己的代理IP地址写入本地");
        }catch (Exception e){
            LOGGER.error("保存失败", e);
        }
    }

    private static List<String> toVerify(Set<String> ips){
        AtomicInteger i = new AtomicInteger();
        AtomicInteger f = new AtomicInteger();
        List<String> list = ips.parallelStream().filter(ip->{
            LOGGER.info("验证进度："+ips.size()+"/"+i.incrementAndGet());
            String[] attr = ip.split(":");
            if(verify(attr[0], Integer.parseInt(attr[1]))){
                return true;
            }
            IPS.remove(ip);
            f.incrementAndGet();
            return false;
        }).sorted().collect(Collectors.toList());
        LOGGER.info("验证成功的IP数："+(ips.size()-f.get()));
        LOGGER.info("验证失败的IP数："+f.get());
        return list;
    }

    private static String getNextProxyIp(){
        int index = currentIpIndex%IPS.size();
        currentIpIndex++;
        return IPS.get(index);
    }

    public static boolean toNewIp() {
        long requestSwitchTime = System.currentTimeMillis();
        LOGGER.info(Thread.currentThread()+"请求重新更换代理");
        synchronized (ProxyIp.class) {
            if (isSwitching) {
                LOGGER.info(Thread.currentThread()+"已经有其他线程在进行更换代理了，我睡觉等待吧，其他线程更换代理完毕会叫醒我的");
                try {
                    ProxyIp.class.wait();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                LOGGER.info(Thread.currentThread()+"其他线程已经更换完代理了，我可以返回了");
                return true;
            }
            isSwitching = true;
        }
        //保险起见，这里再判断一下
        //如果请求更换代理的时间小于上次成功更换代理的时间，则说明这个请求来的【太迟了】，则返回。
        if(requestSwitchTime <= lastSwitchTime){
            LOGGER.info("请求来的太迟了");
            isSwitching = false;
            return true;
        }
        LOGGER.info(Thread.currentThread()+"开始重新更换代理");
        long start = System.currentTimeMillis();
        String proxyIp = useNewProxyIp();
        String currentIp = null;
        int times=0;
        //如果当前IP没有变化，还是等于之前的IP，则继续设置下一个代理IP
        //为了防止无休止重试，设置限制次数
        while((currentIp=getCurrentIp()).equals(previousIp)
                && (times++)<Integer.MAX_VALUE){
            NORMAL_IPS.add(proxyIp);
            IPS.remove(proxyIp);
            proxyIp = useNewProxyIp();
        }
        if(!currentIp.equals(previousIp)) {
            previousIp =currentIp;
            EXCELLENT_IPS.add(proxyIp);
            LOGGER.info(Thread.currentThread()+"自动更换代理成功！");
            LOGGER.info(Thread.currentThread()+"更换代理耗时："+(System.currentTimeMillis()-start)+"毫秒");
            //通知其他线程结束等待
            synchronized (ProxyIp.class) {
                ProxyIp.class.notifyAll();
            }
            isSwitching = false;
            lastSwitchTime = System.currentTimeMillis();
            return true;
        }
        NORMAL_IPS.add(proxyIp);
        IPS.remove(proxyIp);
        LOGGER.info(Thread.currentThread()+"自动更换代理失败！");
        LOGGER.info(Thread.currentThread()+"更换代理耗时："+(System.currentTimeMillis()-start)+"毫秒");
        //通知其他线程结束等待
        synchronized (ProxyIp.class) {
            ProxyIp.class.notifyAll();
        }
        isSwitching = false;
        return false;
    }
    private static String useNewProxyIp(){
        String newProxy = getNextProxyIp();
        String[] attr = newProxy.split(":");
        System.setProperty("proxySet", "true");
        System.setProperty("http.proxyHost", attr[0]);
        System.setProperty("http.proxyPort", attr[1]);
        LOGGER.info("尝试使用新的代理："+newProxy);
        return newProxy;
    }
    /**
     * 验证代理IP是否能工作，能工作不代表能向目标网站隐藏自己的IP
     * @param host
     * @param port
     * @return
     */
    public static boolean verify(String host, int port){
        try {
            String url = "http://apdplat.org";
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(proxy);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setUseCaches(false);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line = null;
            while ((line=reader.readLine()) != null){
                html.append(line);
            }
            LOGGER.info("HTML："+html);
            if(html.toString().contains("APDPlat应用级产品开发平台")){
                LOGGER.info("代理IP验证成功："+host+":"+port);
                return true;
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("代理IP验证失败："+host+":"+port);
        return false;
    }
    /**
     * 看看在ip138的眼中，自己的IP是多少
     * @return
     */
    public static String getCurrentIp(){
        try {
            String url = "http://1212.ip138.com/ic.asp?timestamp="+System.nanoTime();
            String text = Jsoup.connect(url)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("Host", "1111.ip138.com")
                    .header("Referer", "http://ip138.com/")
                    .header("User-Agent", USER_AGENT)
                    .ignoreContentType(true)
                    .timeout(5000)
                    .get()
                    .text();
            LOGGER.info("检查自身IP地址："+text);
            Matcher matcher = IP_PATTERN.matcher(text);
            if(matcher.find()){
                String ip = matcher.group();
                LOGGER.info("自身IP地址："+ip);
                if(text.contains("美国")){
                    EXCELLENT_USA_IPS.add(System.getProperty("http.proxyHost") + ":" + System.getProperty("http.proxyPort"));
                }
                return ip;
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("检查自身IP地址失败，返回之前的IP地址："+ previousIp);
        return previousIp;
    }
    private static Set<String> getProxyIps(){
        Set<String> ips = new HashSet<>();
        ips.addAll(getProxyIpOne());
        ips.addAll(getProxyIpTwo());
        ips.addAll(getProxyIpThree());
        ips.addAll(getProxyIpFour());
        return ips;
    }
    private static List<String> getProxyIpOne(){
        String url = "http://proxy.goubanjia.com/?timestamp="+System.nanoTime();
        String cssPath = "html body div.wrap.fullwidth div#content div#post-2.post-2.page.type-page.status-publish.hentry div.entry.entry-content div#list table.table tbody tr";
        return getProxyIp(url, cssPath);
    }
    private static List<String> getProxyIpTwo(){
        String url = "http://ip.qiaodm.com/?timestamp="+System.nanoTime();
        String cssPath = "html body div#main_container div.inner table.iplist tbody tr";
        return getProxyIp(url, cssPath);
    }
    private static List<String> getProxyIp(String url, String cssPath){
        List<String> ips = new ArrayList<>();
        try {
            String html = ((HtmlPage)WEB_CLIENT.getPage(url)).getBody().asXml();
            //LOGGER.info("html："+html);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(cssPath);
            elements
                    .forEach(element -> {
                        try {
                            Elements tds = element.children();
                            String ip = null;
                            int port = 0;
                            if (tds.size() > 1) {
                                Element ele = tds.get(0);
                                ip = getIps(ele);
                                String text = tds.get(1).text();
                                LOGGER.info("端口："+text+" -> "+tds.get(1).outerHtml());
                                port = Integer.parseInt(text);
                            }
                            if(ip != null && port > 0){
                                LOGGER.info("解析出IP："+ip+"，端口："+port);
                                if(verify(ip, port)){
                                    LOGGER.info("IP："+ip+"，端口："+port+"可以使用");
                                    ips.add(ip + ":" + port);
                                }else {
                                    LOGGER.info("IP："+ip+"，端口："+port+"不能使用");
                                }
                            }
                        }catch (Exception e){
                            LOGGER.error("解析IP出错", e);
                        }
                    });
        }catch (Exception e){
            LOGGER.error("解析IP出错", e);
        }
        return ips;
    }
    private static List<String> getProxyIpThree(){
        List<String> ips = new ArrayList<>();
        for(int i=1; i<=10; i++){
            ips.addAll(getProxyIpThree(i));
        }
        return ips;
    }
    private static List<String> getProxyIpThree(int page){
        List<String> ips = new ArrayList<>();
        try {
            String url = "http://www.kuaidaili.com/proxylist/"+page;
            String html = ((HtmlPage)WEB_CLIENT.getPage(url)).getBody().asXml();
            //LOGGER.info("html："+html);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("html body div#container div#list table.table.table-bordered.table-striped tbody tr");
            elements
                    .forEach(element -> {
                        try {
                            Elements tds = element.children();
                            String ip = null;
                            int port = 0;
                            if (tds.size() > 1) {
                                ip = tds.get(0).text();
                                String text = tds.get(1).text();
                                LOGGER.info("IP："+ip);
                                LOGGER.info("端口："+text);
                                Matcher matcher = IP_PATTERN.matcher(ip.toString());
                                if(matcher.find()){
                                    ip = matcher.group();
                                    LOGGER.info("ip地址验证通过："+ip);
                                }else{
                                    LOGGER.info("ip地址验证失败："+ip);
                                    ip = null;
                                }
                                try{
                                    port = Integer.parseInt(text);
                                    LOGGER.info("端口验证通过："+port);
                                }catch (Exception e){
                                    LOGGER.info("端口验证失败："+port);
                                }
                            }
                            if(ip != null && port > 0){
                                LOGGER.info("解析出IP："+ip+"，端口："+port);
                                if(verify(ip, port)){
                                    LOGGER.info("IP："+ip+"，端口："+port+"可以使用");
                                    ips.add(ip + ":" + port);
                                }else {
                                    LOGGER.info("IP："+ip+"，端口："+port+"不能使用");
                                }
                            }
                        }catch (Exception e){
                            LOGGER.error("解析IP出错", e);
                        }
                    });
        }catch (Exception e){
            LOGGER.error("解析IP出错", e);
        }
        return ips;
    }
    private static List<String> getProxyIpFour(){
        List<String> ips = new ArrayList<>();
        for(int i=1; i<=10; i++){
            ips.addAll(getProxyIpFour(i));
        }
        return ips;
    }
    private static List<String> getProxyIpFour(int page){
        List<String> ips = new ArrayList<>();
        try {
            String url = "http://www.kxdaili.com/ipList/"+page+".html";
            String html = ((HtmlPage)WEB_CLIENT.getPage(url)).getBody().asXml();
            //LOGGER.info("html："+html);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("html body#nav_btn01 div.tab_c_box.buy_tab_box table.ui.table.segment tbody tr");
            elements
                    .forEach(element -> {
                        try {
                            Elements tds = element.children();
                            String ip = null;
                            int port = 0;
                            if (tds.size() > 1) {
                                ip = tds.get(0).text();
                                String text = tds.get(1).text();
                                LOGGER.info("IP："+ip);
                                LOGGER.info("端口："+text);
                                Matcher matcher = IP_PATTERN.matcher(ip.toString());
                                if(matcher.find()){
                                    ip = matcher.group();
                                    LOGGER.info("ip地址验证通过："+ip);
                                }else{
                                    LOGGER.info("ip地址验证失败："+ip);
                                    ip = null;
                                }
                                try{
                                    port = Integer.parseInt(text);
                                    LOGGER.info("端口验证通过："+port);
                                }catch (Exception e){
                                    LOGGER.info("端口验证失败："+port);
                                }
                            }
                            if(ip != null && port > 0){
                                LOGGER.info("解析出IP："+ip+"，端口："+port);
                                if(verify(ip, port)){
                                    LOGGER.info("IP："+ip+"，端口："+port+"可以使用");
                                    ips.add(ip + ":" + port);
                                }else {
                                    LOGGER.info("IP："+ip+"，端口："+port+"不能使用");
                                }
                            }
                        }catch (Exception e){
                            LOGGER.error("解析IP出错", e);
                        }
                    });
        }catch (Exception e){
            LOGGER.error("解析IP出错", e);
        }
        return ips;
    }
    private static String getIps(Element element){
        StringBuilder ip = new StringBuilder();
        Elements all = element.children();
        LOGGER.info("");
        LOGGER.info("开始解析IP地址，机器读到的文本："+element.text());
        AtomicInteger count = new AtomicInteger();
        all.forEach(ele -> {
            String html = ele.outerHtml();
            LOGGER.info(count.incrementAndGet() + "、" + "原始HTML："+html.replaceAll("[\n\r]", ""));
            String text = ele.text();
            if(ele.hasAttr("style")
                    && (ele.attr("style").equals("display: none;")
                        || ele.attr("style").equals("display:none;"))) {
                LOGGER.info("忽略不显示的文本："+text);
            }else{
                if(StringUtils.isNotBlank(text)){
                    LOGGER.info("需要的文本："+text);
                    ip.append(text);
                }else{
                    LOGGER.info("忽略空文本");
                }
            }
        });
        LOGGER.info("----------------------------------------------------------------");
        LOGGER.info("解析到的ip: "+ip);
        LOGGER.info("----------------------------------------------------------------");
        Matcher matcher = IP_PATTERN.matcher(ip.toString());
        if(matcher.find()){
            String _ip = matcher.group();
            LOGGER.info("ip地址验证通过："+_ip);
            return _ip;
        }else{
            LOGGER.info("ip地址验证失败："+ip);
        }
        return null;
    }
    public static void main(String[] args) {
        //如果只是想收集IP，则一直运行此程序即可，更新时间改为1秒钟。
        detectInterval=1000;
        while(true){
            toNewIp();
        }
    }
}
