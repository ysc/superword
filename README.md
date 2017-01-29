###Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading, including but not limited to, spelling similarity, definition similarity, pronunciation similarity, the transformation rules of the spelling, the prefix and the dynamic prefix, the suffix and the dynamic suffix, roots, compound words, text auxiliary reading, web page auxiliary reading, book auxiliary reading, etc.. 

###[Donate to support Superword](https://github.com/ysc/QuestionAnsweringSystem/wiki/donation)

###Getting Started: 

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
        
    3、Configure MySQL database
    
        MySQL character encoding: UTF-8，
        Server IP Address: 127.0.0.1
        Server Port: 3306
        Database: superword
        User name: root
        Password: 
        
        Execute the script in MySQL command line:
        source src/main/resources/mysql/superword.sql
        source src/main/resources/mysql/word_definition.sql
        source src/main/resources/mysql/word_pronunciation.sql
    
    4、Run the project
    
        mvn jetty:run

    5、Use system
    
        Open browser access: http://localhost:8080/index.jsp
        Notice: The first time to access the system may be a little bit slow, be patient please.
      
###Engaging in complex language behavior requires various kinds of knowledge of language:

    Phonetics and Phonology — knowledge about linguistic sounds
    Morphology — knowledge of the meaningful components of words
    Syntax — knowledge of the structural relationships between words
    Semantics — knowledge of meaning
    Pragmatics — knowledge of the relationship of meaning to the goals and intentions of the speaker
    Discourse — knowledge about linguistic units larger than a single utterance
      
###Resources download

[4000 Essential English Words](http://pan.baidu.com/s/1kUhEUKR)

The audio files of the Merriam-Webster dictionary that contain 11053 words: [Download address](http://pan.baidu.com/s/1bnQLyJP)

The audio files of the Oxford dictionary that contain 31222 words: [Download address](http://pan.baidu.com/s/1qXe8cO0)

The HTML pages of the Oxford dictionary that contain 33376 words: [Download address](http://pan.baidu.com/s/1c0UNim8)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForOxford.java)

The HTML pages of the Merriam-Webster dictionary that contain 59809 words: [Download address](http://pan.baidu.com/s/1ntGmA3B)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForWebster.java)

The HTML pages of the old version iCIBA dictionary that contain 61809 words: [Download address](http://pan.baidu.com/s/1bnD9gy7)，[Parse Program](https://github.com/ysc/superword/blob/a78ab4aa2ab62fddeb664065accb06e538eb0059/src/main/java/org/apdplat/superword/tools/WordClassifier.java)

The HTML pages of the new version iCIBA dictionary that contain 63777 words: [Download address](http://pan.baidu.com/s/1ntky0zR)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifier.java)

The HTML pages of the youdao dictionary that contain 63789 words: [Download address](http://pan.baidu.com/s/1pJH4ugj)，[Parse Program](https://github.com/ysc/superword/blob/master/src/main/java/org/apdplat/superword/tools/WordClassifierForYouDao.java)

The 249 PDF e-books are related to IT field and software development: [it-software-domain.zip](http://pan.baidu.com/s/1kT1NA3l)

###Related articles

[一种使用随机抽样梯度下降算法来预估词汇量的方法](http://my.oschina.net/apdplat/blog/547668)

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

[https://travis-ci.org/ysc/superword](https://travis-ci.org/ysc/superword)
