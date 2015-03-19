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

import org.apache.commons.lang.StringUtils;
import org.apdplat.superword.model.Word;
import org.apdplat.superword.tools.WordLinker;
import org.apdplat.superword.tools.WordSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文本词频统计
 *
 * @author 杨尚川
 */
public class TextAnalysis {
    private TextAnalysis() {
    }

    private static final List<String> UN = Arrays.asList("tion co ed ng ca alice jp gc ".split("\\s+"));
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    private static final Logger LOGGER = LoggerFactory.getLogger(TextAnalysis.class);

    /**
     * @param files 文件相对路径或绝对路径
     * @return 词频统计数据
     */
    public static Map<String, AtomicInteger> frequency(Collection<String> files) {
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();
        for (String file : files) {
            LOGGER.info("parse text file: " + file);
            //统计词频
            Map<String, AtomicInteger> data = frequency(file);
            //合并结果
            data.entrySet().forEach(entry -> {
                map.putIfAbsent(entry.getKey(), new AtomicInteger());
                map.get(entry.getKey()).addAndGet(entry.getValue().get());
            });
            data.clear();
        }
        LOGGER.info("total unique words count: " + map.size());
        return map;
    }

    public static Map<String, AtomicInteger> frequency(String file) {
        try{
            return frequency(new FileInputStream(file));
        }catch (IOException e){
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    public static Map<String, AtomicInteger> frequency(InputStream inputStream) {
        Map<String, AtomicInteger> map = new ConcurrentHashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new BufferedInputStream(
                                inputStream)))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                List<String> words = seg(line);
                words.forEach(word -> {
                    map.putIfAbsent(word, new AtomicInteger());
                    map.get(word).incrementAndGet();
                });
                words.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOGGER.info("unique words count: " + map.size());
        return map;
    }

    /**
     * 分词
     * @param sentence
     * @param debug 打开开关可在开发时跟踪分词细节
     * @return
     */
    public static List<String> seg(String sentence) {
        List<String> data = new ArrayList<>();
        //以非字母字符切分行
        String[] words = sentence.trim().split("[^a-zA-Z0-9]");
        StringBuilder log = new StringBuilder();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("句子:" + sentence);
        }
        for (String word : words) {
            if (StringUtils.isBlank(word) || word.length()<2) {
                continue;
            }
            List<String> list = new ArrayList<String>();
            //转换为全部小写
            if (word.length() < 6
                    //PostgreSQL等
                    || (Character.isUpperCase(word.charAt(word.length()-1))
                          && Character.isUpperCase(word.charAt(0)))
                    //P2P,Neo4j等
                    || PATTERN.matcher(word).find()
                    || StringUtils.isAllUpperCase(word)) {
                word = word.toLowerCase();
            }
            //按照大写字母进行单词拆分
            int last = 0;
            for (int i = 1; i < word.length(); i++) {
                if (Character.isUpperCase(word.charAt(i))
                        && Character.isLowerCase(word.charAt(i - 1))) {
                    list.add(word.substring(last, i));
                    last = i;
                }
            }
            if (last < word.length()) {
                list.add(word.substring(last, word.length()));
            }
            list.stream()
                    .map(w -> w.toLowerCase())
                    .forEach(w -> {
                        if (w.length() < 2) {
                            return;
                        }
                        w = irregularity(w);
                        if(StringUtils.isNotBlank(w) && !StringUtils.isNumeric(w)) {
                            data.add(w);
                            if (LOGGER.isDebugEnabled()) {
                                log.append(w).append(" ");
                            }
                        }
                    });
        }
        LOGGER.debug("分词："+log);
        return data;
    }

    /**
     * 处理分词意外，即无规则情况
     * @param word
     * @return
     */
    private static String irregularity(String word){
        switch (word){
            //I’ll do it. You'll see.
            case "ll": return "will";
            //If you’re already building applications using Spring.
            case "re": return "are";
            //package com.manning.sdmia.ch04;
            case "ch": return "chapter";
            //you find you’ve made a
            case "ve": return "have";
            //but it doesn’t stop there.
            case "doesn": return "does";
            //but it isn’t enough.
            case "isn": return "is";
            //<input type="text" name="firstName" /><br/>
            case "br": return null;
        }
        return word;
    }

    /**
     * 将 {词 : 词频} 逆转过来为{词频 : 词数，前10个词}
     * @param data 词频统计结果
     * @return 词频分布统计
     */
    public static Map<Integer, Stat> distribute(Map<String, AtomicInteger> data) {
        Map<Integer, Stat> stat = new HashMap<>();
        data.entrySet()
                .forEach(entry -> {
                    Integer key = entry.getValue().get();
                    stat.putIfAbsent(key, new Stat());
                    stat.get(key).increment();
                    stat.get(key).addWords(entry.getKey());
                });
        return stat;
    }

    public static String toHtmlFragment(Map<String, AtomicInteger> data, Set<String> fileNames) {
        StringBuilder html = new StringBuilder();
        html.append("统计书籍：<br/>\n");
        AtomicInteger i = new AtomicInteger();
        fileNames.stream()
                .sorted()
                .forEach(fileName -> html.append(i.incrementAndGet())
                        .append("、")
                        .append(Paths.get(fileName).toFile().getName().replace(".txt", ""))
                        .append("<br/>\n"));
        Map<Integer, Stat> stat = distribute(data);
        html.append("共有")
                .append(data.size())
                .append("个单词，出现次数统计：<br/>\n")
                .append("<table  border=\"1\"  bordercolor=\"#00CCCC\"  width=\"850\">\n\t<tr><td>序号</td><td>出现次数</td><td>单词个数</td><td>单词</td></tr>\n");
        AtomicInteger k = new AtomicInteger();
        stat.keySet()
                .stream()
                .sorted((a, b) -> b - a)
                .forEach(s -> {
                    html.append("\t<tr><td>")
                            .append(k.incrementAndGet())
                            .append("</td><td>")
                            .append(s)
                            .append("</td><td>")
                            .append(stat.get(s).count())
                            .append("</td><td>");
                    AtomicInteger z = new AtomicInteger();
                    List<String> list = stat.get(s).getWords();
                    list.stream()
                            .sorted()
                            .forEach(w -> {
                                if (list.size() > 1) {
                                    html.append(z.incrementAndGet())
                                            .append(".")
                                            .append(WordLinker.toLink(w))
                                            .append(" ");
                                } else if (list.size() == 1) {
                                    html.append(WordLinker.toLink(w));
                                }
                            });
                    html.append("</td></tr>\n");
                });
        html.append("</table>")
                .append("\n共有(")
                .append(data.size())
                .append(")个单词：<br/>\n")
                .append("<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger wordCounter = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() <= 14)
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .forEach(entry -> {
                    html.append("\t")
                            .append("<tr><td>")
                            .append(wordCounter.incrementAndGet())
                            .append("</td><td>")
                            .append(WordLinker.toLink(entry.getKey()))
                            .append("</td><td>")
                            .append(entry.getValue().get())
                            .append("</td></tr>\n");

                });
        html.append("</table>\n")
                .append("长度大于14的词：")
                .append("\n<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger j = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() > 14)
                .sorted((a, b) ->
                        b.getValue().get() - a.getValue().get())
                .forEach(entry ->
                        html.append("\t")
                                .append("<tr><td>")
                                .append(j.incrementAndGet())
                                .append("</td><td>")
                                .append(WordLinker.toLink(entry.getKey()))
                                .append("</td><td>")
                                .append(entry.getValue().get())
                                .append("</td></tr>\n"));

        html.append("</table>\n")
                .append("长度为2的词：")
                .append("\n<table>\n\t<tr><td>序号</td><td>单词</td><td>词频</td></tr>\n");
        AtomicInteger z = new AtomicInteger();
        data.entrySet()
                .stream()
                .filter(entry -> entry.getKey().length() == 2)
                .sorted((a, b) ->
                        b.getValue().get() - a.getValue().get())
                .forEach(entry ->
                        html.append("\t")
                                .append("<tr><td>")
                                .append(z.incrementAndGet())
                                .append("</td><td>")
                                .append(WordLinker.toLink(entry.getKey()))
                                .append("</td><td>")
                                .append(entry.getValue().get())
                                .append("</td></tr>\n"));
        html.append("</table>");
        return html.toString();
    }

    /**
     * 解析目录或文件
     * @param path
     */
    public static void parse(String path) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> data = frequency(fileNames);
        //渲染结果
        String htmlFragment = toHtmlFragment(data, fileNames);
        try{
            //保存结果
            String resultFile = "target/words_" + Paths.get(path).toFile().getName().replace(".txt", "") + ".txt";
            Files.write(Paths.get(resultFile), htmlFragment.getBytes("utf-8"));
            LOGGER.info("统计结果输出到文件：" + resultFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Set<String> getFileNames(String path){
        Set<String> fileNames = new HashSet<>();
        if(Files.isDirectory(Paths.get(path))) {
            LOGGER.info("处理目录：" + path);
        }else{
            LOGGER.info("处理文件：" + path);
            fileNames.add(path);
            return fileNames;
        }
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String fileName = file.toFile().getAbsolutePath();
                    if (!fileName.endsWith(".txt")) {
                        LOGGER.info("放弃处理非txt文件：" + fileName);
                        return FileVisitResult.CONTINUE;
                    }
                    fileNames.add(fileName);
                    return FileVisitResult.CONTINUE;
                }

            });
        }catch (IOException e){
            e.printStackTrace();
        }
        return fileNames;
    }

    public static Map<String, List<String>> findEvidence(Path dir, List<String> words, int limit) {
        LOGGER.info("处理目录：" + dir);
        Map<String, List<String>> data = new HashMap<>();
        Set<String> set = new HashSet<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.toFile().getAbsolutePath();
                    if (file.toFile().getName().startsWith(".")) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (!fileName.endsWith(".txt")) {
                        LOGGER.info("放弃处理非txt文件：" + fileName);
                        return FileVisitResult.CONTINUE;
                    }

                    LOGGER.info("处理文件：" + fileName);
                    List<String> lines = Files.readAllLines(file);
                    final String book = file.toFile().getName().replace(".txt", "");
                    for (int i = 0; i < lines.size(); i++) {
                        final String line = lines.get(i);
                        final List<String> wordSet = seg(line);
                        words
                            .forEach(word -> {
                                String id = word+"_"+book;
                                data.putIfAbsent(word, new ArrayList<>());
                                //一个词在一本书中只取一个句子
                                if (data.get(word).size() < limit
                                        && !set.contains(id)
                                        && wordSet.contains(word)) {
                                    set.add(id);
                                    data.get(word).add(line
                                            + " <u><i>"
                                            + book
                                            + "</i></u>");
                                }
                            });
                    }

                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String toHtmlFragment(Map<String, List<String>> data) {
        StringBuilder html = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        data.keySet()
                .stream()
                .forEach(word -> {
                    if(data.get(word).isEmpty()){
                        System.err.println("词："+word+"没有找到匹配文本");
                        return ;
                    }
                    StringBuilder p = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        p.append("[")
                                .append(Character.toUpperCase(c))
                                .append(Character.toLowerCase(c))
                                .append("]{1}");
                    }
                    html.append("<h1>")
                            .append(i.incrementAndGet())
                            .append("、单词 ")
                            .append(WordLinker.toLink(word))
                            .append(" 的匹配文本：</h1><br/>\n");
                    html.append("<ol>\n");
                    data.get(word)
                            .forEach(t -> html.append("\t<li>")
                                    .append(t.replaceAll(p.toString(), "<font color=\"red\">" + word + "</font>"))
                                    .append("</li><br/>\n"));
                    html.append("</ol><br/>\n");
                });
        return html.toString();
    }

    /**
     * 这些词由wordDetect方法生成
     */
    public static void summary2(){
        List<String> words = Arrays.asList("hadoop",
                "http",
                "api",
                "xml",
                "solr",
                "hbase",
                "hdfs",
                "mysql",
                "apache",
                "gradle",
                "url",
                "schema",
                "metadata",
                "mongodb",
                "jvm",
                "plugin",
                "sql",
                "implementations",
                "osgi",
                "dependencies",
                "runtime",
                "jenkins",
                "couchdb",
                "cpu",
                "bytes",
                "ip",
                "lucene",
                "redis",
                "html",
                "metrics",
                "dm",
                "cassandra",
                "mapper",
                "filesystem",
                "json",
                "annotations",
                "servlet",
                "jdbc",
                "parser",
                "activemq",
                "jms",
                "tika",
                "configuring",
                "namespace",
                "www",
                "jrockit",
                "vm",
                "linux",
                "jpa",
                "rabbitmq",
                "concurrency",
                "frameworks",
                "subclass",
                "boolean",
                "permissions",
                "roo",
                "apis",
                "asynchronous",
                "mvc",
                "google",
                "ruby",
                "sqoop",
                "innodb",
                "caching",
                "scheduler",
                "initialization",
                "config",
                "classpath",
                "superclass",
                "plugins",
                "mahout",
                "enum",
                "proofreaders",
                "copyeditors",
                "iterator",
                "username",
                "jdk",
                "timestamp",
                "tcp",
                "tuple",
                "screenshot",
                "scalability",
                "constructors",
                "dataset",
                "daemon",
                "topology",
                "partitioning",
                "urls",
                "jmx",
                "unix",
                "packt",
                "ids",
                "aop",
                "serialization",
                "namenode",
                "uri",
                "jsp",
                "dynamically",
                "cached",
                "avro",
                "oozie",
                "writable",
                "nutch",
                "subclasses",
                "operand",
                "php",
                "applet",
                "sharding",
                "optimized",
                "transactional",
                "amazon",
                "ssl",
                "ejb",
                "manning",
                "querying",
                "junit",
                "workflow",
                "gui",
                "faceting",
                "gmond",
                "endpoint",
                "tuples",
                "standalone",
                "ssh",
                "kafka",
                "rmi",
                "bytecode",
                "datagram",
                "hostname",
                "clojure",
                "descriptor",
                "lifecycle",
                "optimizations",
                "repositories",
                "reducers",
                "dao",
                "znode",
                "netty",
                "localhost",
                "iteration",
                "pom",
                "ldap",
                "nosql",
                "udp",
                "rpc",
                "dsl",
                "acl",
                "optimizer",
                "mappings",
                "runnable",
                "init",
                "multicast",
                "singleton",
                "optionally",
                "failover",
                "parameterized",
                "serialized",
                "combiner",
                "generics",
                "compaction",
                "datasets",
                "daemons",
                "foo",
                "filename",
                "scripting",
                "mac",
                "gc",
                "browsers",
                "ec2",
                "lambda",
                "grained",
                "append",
                "jit",
                "jndi",
                "enumerated",
                "benchmarks",
                "bigtable",
                "myisam",
                "wiki",
                "analytics",
                "executable",
                "elasticsearch",
                "comparator",
                "meta",
                "cpus",
                "dom",
                "wildcard",
                "kerberos",
                "aws",
                "backend",
                "jmeter",
                "predicate",
                "erlang",
                "parsed",
                "nagios",
                "src",
                "css",
                "endpoints",
                "mappers",
                "rdbms",
                "instantiate",
                "facebook",
                "udf",
                "xpath",
                "errata",
                "recommender",
                "amqp",
                "optimizing",
                "jspa",
                "workloads",
                "sharded",
                "latin",
                "dns",
                "parsers",
                "ajax",
                "emr",
                "co",
                "configurable",
                "db",
                "partitioner",
                "ubuntu",
                "frontend",
                "txt",
                "schemas",
                "javadoc",
                "aspectj",
                "keystore",
                "repl",
                "mbean",
                "bootstrap",
                "declarative",
                "nio",
                "mongo",
                "akka",
                "stateful",
                "instantiated",
                "classifier",
                "invocations",
                "asynchronously",
                "jsf",
                "multithreaded",
                "sh",
                "pointcut",
                "util",
                "reusable",
                "reilly",
                "spel",
                "predefined",
                "serializable",
                "unicode",
                "sts",
                "microsoft",
                "codec",
                "deprecated",
                "foreach",
                "csv",
                "searcher",
                "applets",
                "literals",
                "recursion",
                "solaris",
                "mongos",
                "iterable",
                "jobtracker",
                "sbt",
                "orm",
                "zset",
                "ql",
                "js",
                "udfs",
                "mongod",
                "vms",
                "openid",
                "datanode",
                "adapters",
                "javafx",
                "firefox",
                "mcollective",
                "servlets",
                "aggregates",
                "ca",
                "rollback",
                "lookups",
                "monad",
                "hashing",
                "tokenizer",
                "accumulo",
                "jvms",
                "s3",
                "programmatically",
                "jp",
                "ping",
                "ascii",
                "wikipedia",
                "enumeration",
                "percona",
                "deletes",
                "callable",
                "solrconfig",
                "resolver",
                "perl",
                "mb",
                "urlconnection",
                "validator",
                "hypervisor",
                "pojo",
                "traversal",
                "pdf",
                "operands",
                "gmetad",
                "hashes",
                "virtualization",
                "hfile",
                "jconsole",
                "fs",
                "tasktracker",
                "subdirectory",
                "parses",
                "recursively",
                "subflow",
                "configures",
                "lang",
                "subquery",
                "tasklet",
                "aggregated",
                "ivy",
                "matcher",
                "ioexception",
                "neo4j",
                "cron",
                "chubby",
                "english",
                "formatter",
                "keyspace",
                "dfs",
                "namespaces",
                "oplog",
                "checksum",
                "compilers",
                "refactoring",
                "mbeans",
                "datanodes",
                "codebase",
                "benchmarking",
                "dhcp",
                "metastore",
                "debian",
                "impl",
                "gb",
                "riak",
                "whitespace",
                "paging",
                "iterating",
                "granularity",
                "jax",
                "jsr",
                "timestamps",
                "timeline",
                "nonblocking",
                "modifiers",
                "iterative",
                "acls",
                "nfs",
                "rss",
                "datagrams",
                "visualvm",
                "mesos",
                "tweets",
                "batis",
                "iff",
                "bson",
                "compiles",
                "cloudera",
                "xen",
                "initializer",
                "ftp",
                "statically",
                "zookeeper",
                "descriptors",
                "delimited",
                "rowkey",
                "memcached",
                "stm",
                "charset",
                "yahoo",
                "programmatic",
                "cms",
                "extensible",
                "customizing",
                "hiveql",
                "wildcards",
                "sphinx",
                "versioning",
                "https",
                "incremented",
                "filesystems",
                "awt",
                "dev",
                "combinators",
                "cd",
                "oop",
                "filenames",
                "jta",
                "finalizer",
                "sflow",
                "subprojects",
                "iterates",
                "connectors",
                "buf",
                "subproject",
                "schedulers",
                "atomically",
                "sudo",
                "classloader",
                "deployer",
                "traversable",
                "htable",
                "bytecodes",
                "workflows",
                "keystone",
                "env",
                "tmp",
                "leveraging",
                "sawzall",
                "delimiter",
                "untrusted",
                "polymorphism",
                "aggregator",
                "syslog",
                "implicits",
                "vlan",
                "println",
                "san",
                "middleware",
                "jruby",
                "hashtable",
                "nonzero",
                "suggester",
                "logfile",
                "initializing",
                "inet",
                "bidirectional",
                "gridfs",
                "uris",
                "zsets",
                "cryptographic",
                "natively",
                "timeouts",
                "subtree",
                "alice",
                "concatenation",
                "py",
                "znodes",
                "ips",
                "checkstyle",
                "readability",
                "utf",
                "smtp",
                "accessor",
                "xx",
                "logout",
                "searchable",
                "subdirectories",
                "bundlor",
                "lzo",
                "gzip",
                "leverages",
                "enums",
                "predicates",
                "deadlocks",
                "metamodel",
                "jaas",
                "wal",
                "initializes",
                "regex",
                "gwt",
                "ioc",
                "lua",
                "coprocessor",
                "multicore",
                "refactor",
                "jre",
                "instantiating",
                "usr",
                "mutability",
                "thymeleaf",
                "gfs",
                "autocomplete",
                "paxos",
                "postgresql",
                "mutex",
                "jni",
                "topologies",
                "latencies",
                "xxx",
                "log4j",
                "lzop",
                "tf",
                "osds",
                "pmd",
                "multipart",
                "throwable",
                "inline",
                "dsls",
                "ctrl",
                "rdds",
                "atomicity",
                "instanceof",
                "nodetool",
                "covariant",
                "ntp",
                "installer",
                "packtpub",
                "facter",
                "uuid",
                "mapred",
                "bnd",
                "combinator",
                "embeddable",
                "async",
                "mixin",
                "idf",
                "unicast",
                "appending",
                "args",
                "xhtml",
                "javac",
                "param",
                "superclasses",
                "uppercase",
                "logfiles",
                "testable",
                "rs",
                "brainz",
                "checkbox",
                "mvn",
                "osd",
                "testng",
                "pt",
                "corba",
                "immutability",
                "interoperability",
                "multithreading",
                "opentsdb",
                "hfiles",
                "bz",
                "mng",
                "normalization",
                "customization",
                "rcfile");
        Map<String, List<String>> data = findEvidence(Paths.get("src/main/resources/it"), words, 10);
        String html = toHtmlFragment(data);
        LOGGER.info(html);
    }
    public static void summary() {
        List<String> words =
                Arrays.asList("resurgent",
                        "categorically",
                        "misleadingly",
                        "weightings",
                        "uniques",
                        "alphanumerics",
                        "misspell",
                        "conducive",
                        "dissection",
                        "marvel",
                        "graciously",
                        "inspections",
                        "appetite",
                        "visualizations",
                        "commonalities",
                        "dissecting",
                        "fidelity",
                        "creativity",
                        "coyote",
                        "reaction");
        Map<String, List<String>> data = findEvidence(Paths.get("src/main/resources/it"), words, 100);
        String html = toHtmlFragment(data);
        LOGGER.info(html);
    }

    public static void main(String[] args) throws Exception {
        //parse("src/main/resources/it/spring/Spring in Action 4th Edition.txt");
        //parse("src/main/resources/it/spring");
        parse("src/main/resources/it");
        //summary();
        //footN("src/main/resources/it", 100);
        //topN("src/main/resources/it", 100);
        //wordDetect("src/main/resources/it");
        //summary2();
    }

    public static void wordDetect(String path){
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> fres = frequency(fileNames);
        //词典
        Set<Word> DICTIONARY = WordSources.get("/words.txt", "/words_extra.txt", "/words_gre.txt");
        Map<String, AtomicInteger> unknown = new HashMap<>();
        LOGGER.debug("需要检查单词个数：" + fres.keySet().size());
        fres
            .keySet()
            .forEach(key -> {
                if (!DICTIONARY.contains(new Word(key.toLowerCase(), ""))) {
                    unknown.put(key, fres.get(key));
                }
            });
        LOGGER.debug("未知的单词个数："+unknown.size());
        AtomicInteger i = new AtomicInteger();
        unknown.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().get() - a.getValue().get())
                .forEach(entry -> {
                    i.incrementAndGet();
                    if(i.get()<551) {
                        LOGGER.info("\t\"" + entry.getKey() +"\",");
                    }
                });
    }

    public static void footN(String path, int limit) {
        sentence(path, limit, false);
    }

    public static void topN(String path, int limit) {
        sentence(path, limit, true);
    }
    public static void sentence(String path, int limit, boolean isTopN) {
        //获取目录下的所有文件列表 或 文件本身
        Set<String> fileNames = getFileNames(path);
        //词频统计
        Map<String, AtomicInteger> fres = frequency(fileNames);
        //有序
        TreeMap<Float, String> sentences = new TreeMap<>();
        //句子评分
        int count = 0;
        for(String fileName : fileNames) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new BufferedInputStream(
                                    new FileInputStream(fileName))))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isBlank(line)) {
                        continue;
                    }
                    //计算分值
                    float score = 0;
                    List<String> words = seg(line);
                    for(String word : words){
                        AtomicInteger fre = fres.get(word);
                        if(fre == null || fre.get() == 0){
                            LOGGER.error("评分句子没有词频信息：" + line);
                            score = 0;
                            break;
                        }
                        score += 1/(float)fre.get();
                    }
                    words.clear();
                    if(score > 0) {
                        //保存句子
                        if(sentences.get(score) != null){
                            continue;
                        }
                        sentences.put(score, line + " <u><i>" + Paths.get(fileName).toFile().getName().replace(".txt", "") + "</i></u>");
                        count++;
                        if(count >= limit) {
                            if(isTopN){
                                //删除分值最低的
                                sentences.pollFirstEntry();
                            }else{
                                //删除分值最高的
                                sentences.pollLastEntry();
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        AtomicInteger i = new AtomicInteger();
        sentences.entrySet().forEach(entry -> {
            //LOGGER.info(i.incrementAndGet()+"、分值："+entry.getKey()+":<br/>\n\t"+entry.getValue()+"<br/>\n");
            LOGGER.info(i.incrementAndGet()+"、"+entry.getValue()+"<br/><br/>\n");
        });
    }

    private static class Stat {
        private AtomicInteger count = new AtomicInteger();
        private List<String> words = new ArrayList<>();

        public int count() {
            return count.get();
        }

        public void increment() {
            count.incrementAndGet();
        }

        public List<String> getWords() {
            return words;
        }

        public void addWords(String word) {
            if (this.words.size() < 11) {
                this.words.add(word);
            }
        }
    }
}
