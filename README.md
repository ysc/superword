###superword是一个Java实现的英文单词分析和辅助阅读开源项目，主要研究英语单词音近形似转化规律、前缀后缀规律、词之间的相似性规律和辅助阅读等等。Clean code、Fluent style、Java8 feature: Lambdas, Streams and Functional-style Programming。

###升学考试、工作求职、充电提高，都少不了英语的身影，英语对我们来说实在太重要了。你还在为记不住英语单词而苦恼吗？还在为看不懂英文资料和原版书籍而伤神吗？superword可以在你英语学习的路上助你一臂之力。

###superword利用计算机强大的计算能力，使用机器学习和数据挖掘算法找到读音相近、外形相似、含义相关、同义反义、词根词缀的英语单词，从而非常有利于我们深入地记忆理解这些单词，同时，辅助阅读功能更是能够提供阅读的速度和质量。

###支持最权威的2部中文词典和9部英文词典，支持23种分级词汇，囊括了所有的英语考试，还专门针对程序员提供了249本最热门的技术书籍的辅助阅读功能。

###[在线访问地址](http://123.56.99.179/)

###[捐赠致谢](https://github.com/ysc/QuestionAnsweringSystem/wiki/donation)

###使用方法：

    1、安装JDK8
        将JDK的bin目录加入PATH环境变量，确保在命令行能调用java命令：
        java -version
            java version "1.8.0_60"
        注意:
        必须是JDK8
            
    2、获取superword源码
        git clone https://github.com/ysc/superword.git
        cd superword
        建议自己注册一个GitHub账号，将项目Fork到自己的账号下，然后再从自己的账号下签出项目源码，
        这样便于使用GitHub的Pull requests功能进行协作开发。
        注意:
        如果访问github很慢可以使用以下地址:
        git clone https://git.oschina.net/ysc/superword.git
        
    3、配置MySQL数据库
        在MySQL命令行中执行superword/src/main/resources/mysql/superword.sql文件中的脚本   
        MySQL编码：UTF-8，
        主机：127.0.0.1
        端口：3306
        数据库：superword
        用户名：root
        密码：root
    
    4、运行项目
        unix类操作系统执行：
            chmod +x startup.sh & ./startup.sh
        windows类操作系统执行：
            mvn clean install
            将target/superword-1.0.war文件手动拷贝到你自己的apache-tomcat-8.0.28/webapps目录后启动tomcat

    5、使用系统
        打开浏览器访问：http://localhost:8080/superword/index.jsp

###[如何正确地快速地看电影学英语](http://my.oschina.net/apdplat/blog/530605)
###[使用Java8实现自己的个性化搜索引擎](http://my.oschina.net/apdplat/blog/396193)
###[192本软件著作用词分析](http://my.oschina.net/apdplat/blog/392496)
###[2000个软件开发领域的高频特殊词及精选例句](http://my.oschina.net/apdplat/blog/389200)
###[英语单词音近形似转化规律研究](http://my.oschina.net/apdplat/blog/378569)
###[986组同义词辨析](http://my.oschina.net/apdplat/blog/392944)
###[3211个词及其反义词](http://my.oschina.net/apdplat/blog/392954)
###[13054个词及其词义数](http://my.oschina.net/apdplat/blog/393278)
###[词组习语3054组](http://my.oschina.net/apdplat/blog/393374)
###[1208个合成词](http://my.oschina.net/apdplat/blog/393724)
###[根据76大细分词性对单词进行归组](http://my.oschina.net/apdplat/blog/393771)
###[分析996个词根在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/391865)
###[分析113个前缀在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/392456)
###[分析151个后缀在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/392466)
###[分析在各大考纲词汇中既没有词根也没有前缀和后缀的独立单词](http://my.oschina.net/apdplat/blog/392483)
###[分析在各大考纲词汇中同时拥有前缀后缀和词根的词](http://my.oschina.net/apdplat/blog/392490)
###[JDK源代码以及200多部软件著作中出现的以连字符构造的1011个合成词](http://my.oschina.net/apdplat/blog/394495)
###[利用1691个精选句子彻底掌握2898个单词](http://my.oschina.net/apdplat/blog/394941)
###[一个月的时间让你的词汇量翻一翻](http://my.oschina.net/apdplat/blog/379303)
###[英语单词前缀规则总结](http://my.oschina.net/apdplat/blog/378753)
###[英语单词后缀规则总结](http://my.oschina.net/apdplat/blog/379330)
###考虑到爱词霸的防爬虫限制，特提供包含61821个单词的爱词霸HTML页面origin_html.zip文件供下载，[下载地址](http://pan.baidu.com/s/1bnD9gy7)，[解析程序](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifier.java)
###项目中最重要的素材之一是IT领域中和软件开发相关的249本电子书，大多数书都跟大数据和搜索引擎有关系，因为这是我的研究方向。这些书我打包到了一起并提供下载：[it-software-domain.zip](http://pan.baidu.com/s/1kT1NA3l)

###[在线英语词典](http://my.oschina.net/apdplat/blog/425004)
###[英语学习资源推荐](http://my.oschina.net/apdplat/blog/473088)