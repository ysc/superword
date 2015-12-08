###Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading, including but not limited to, spelling similarity, definition similarity, pronunciation similarity, the transformation rules of the spelling, the prefix and the dynamic prefix, the suffix and the dynamic suffix, roots, compound words, text auxiliary reading, web page auxiliary reading, book auxiliary reading, etc.. 

###[Online Superword](http://123.56.99.179/)

###[Donate to support Superword](https://github.com/ysc/QuestionAnsweringSystem/wiki/donation)

###Quick Start: 

    1、Install JDK8
    
        Add the $JAVA_HOME/bin directory into the $PATH environment variable，ensure you can use Java command: 
        
        java -version
            java version "1.8.0_60"
            
        Tip:
        Must use JDK8 not JDK7.
            
    2、Get the source code of superword
    
        git clone https://github.com/ysc/superword.git
        cd superword
        
        We suggest  you register a GitHub account, fork the superword project to your own account, 
        and then clone the source code from your own account.
        This facilitates the application of GitHub features "Pull requests" for collaborative development.
        
        Tip for Chinese:
        If you can't access GitHub or the download speed is very slow, use the following address:
        
        git clone https://git.oschina.net/ysc/superword.git
        
    3、Configure MySQL database
    
        MySQL character encoding: UTF-8，
        Server IP Address: 127.0.0.1
        Server Port: 3306
        Database: superword
        User name: root
        Password: root
        
        Execute the script in MySQL command line:
        source src/main/resources/mysql/superword.sql
        source src/main/resources/mysql/word_definition.sql
        source src/main/resources/mysql/word_pronunciation.sql
    
    4、Run the project
    
        UNIX-like operating systems: 
            chmod +x startup.sh & ./startup.sh
            
        Windows operating system: 
            mvn clean install
            Manually copy the target/superword-1.0.war file to your own apache-tomcat-8.0.28/webapps directory, 
            then start Tomcat.

    5、Use system
    
        Open browser access: http://localhost:8080/superword/index.jsp
      
###Resources download

The HTML page of the Oxford dictionary that contains 33376 words: [Download address](http://pan.baidu.com/s/1pJmwr95)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForOxford.java)

The HTML page of the Merriam-Webster dictionary that contains 59809 words: [Download address](http://pan.baidu.com/s/1ntGmA3B)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForWebster.java)

The HTML page of the old version iCIBA dictionary that contains 61809 words: [Download address](http://pan.baidu.com/s/1bnD9gy7)，[Parse Program](https://github.com/ysc/superword/blob/a78ab4aa2ab62fddeb664065accb06e538eb0059/src/main/java/org/apdplat/superword/tools/WordClassifier.java)

The HTML page of the new version iCIBA dictionary that contains 63777 words: [Download address](http://pan.baidu.com/s/1ntky0zR)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifier.java)

The HTML page of the youdao dictionary that contains 63789 words: [Download address](http://pan.baidu.com/s/1pJH4ugj)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForYouDao.java)

The 249 PDF e-books is related to IT field and software development: [it-software-domain.zip](http://pan.baidu.com/s/1kT1NA3l)

###Related articles

[如何正确地快速地看电影学英语](http://my.oschina.net/apdplat/blog/530605)

[使用Java8实现自己的个性化搜索引擎](http://my.oschina.net/apdplat/blog/396193)

[192本软件著作用词分析](http://my.oschina.net/apdplat/blog/392496)

[2000个软件开发领域的高频特殊词及精选例句](http://my.oschina.net/apdplat/blog/389200)

[英语单词音近形似转化规律研究](http://my.oschina.net/apdplat/blog/378569)

[986组同义词辨析](http://my.oschina.net/apdplat/blog/392944)

[3211个词及其反义词](http://my.oschina.net/apdplat/blog/392954)

[13054个词及其词义数](http://my.oschina.net/apdplat/blog/393278)

[词组习语3054组](http://my.oschina.net/apdplat/blog/393374)

[1208个合成词](http://my.oschina.net/apdplat/blog/393724)

[根据76大细分词性对单词进行归组](http://my.oschina.net/apdplat/blog/393771)

[分析996个词根在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/391865)

[分析113个前缀在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/392456)

[分析151个后缀在各大考纲词汇中的作用](http://my.oschina.net/apdplat/blog/392466)

[分析在各大考纲词汇中既没有词根也没有前缀和后缀的独立单词](http://my.oschina.net/apdplat/blog/392483)

[分析在各大考纲词汇中同时拥有前缀后缀和词根的词](http://my.oschina.net/apdplat/blog/392490)

[JDK源代码以及200多部软件著作中出现的以连字符构造的1011个合成词](http://my.oschina.net/apdplat/blog/394495)

[利用1691个精选句子彻底掌握2898个单词](http://my.oschina.net/apdplat/blog/394941)