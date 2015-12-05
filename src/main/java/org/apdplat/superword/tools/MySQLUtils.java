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


import org.apache.commons.dbcp2.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apdplat.superword.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据存储层
 * @author 杨尚川
 */
public class MySQLUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MySQLUtils.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/superword?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static DataSource dataSource = null;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    static {
        try {
            Class.forName(DRIVER);
            dataSource = setupDataSource(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            LOG.error("MySQL驱动加载失败：", e);
        }
    }

    private MySQLUtils() {
    }

    public static String getWordPronunciation(String word, String dictionary) {
        String sql = "select pronunciation from word_pronunciation where word=? and dictionary=?";
        Connection con = getConnection();
        if(con == null){
            return "";
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, word);
            pst.setString(2, dictionary);
            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            LOG.error("查询单词音标失败", e);
        } finally {
            close(con, pst, rs);
        }
        return "";
    }

    public static void saveWordPronunciation(String word, String dictionary, String pronunciation) {
        String sql = "insert into word_pronunciation (word, dictionary, pronunciation) values (?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, word);
            pst.setString(2, dictionary);
            pst.setString(3, pronunciation);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("单词音标保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static boolean deleteWordDefinition(String word) {
        String sql = "delete from word_definition where word=?";
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, word);
            return pst.execute();
        } catch (SQLException e) {
            LOG.error("删除单词定义失败", e);
        } finally {
            close(con, pst, rs);
        }
        return false;
    }

    public static String getWordDefinition(String word, String dictionary) {
        String sql = "select definition from word_definition where word=? and dictionary=?";
        Connection con = getConnection();
        if(con == null){
            return "";
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, word);
            pst.setString(2, dictionary);
            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            LOG.error("查询单词定义失败", e);
        } finally {
            close(con, pst, rs);
        }
        return "";
    }

    public static void saveWordDefinition(String word, String dictionary, String definition) {
        String sql = "insert into word_definition (word, dictionary, definition) values (?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, word);
            pst.setString(2, dictionary);
            pst.setString(3, definition);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("单词定义保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static boolean existUser(User user, String table){
        String sql = "select id from "+table+" where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            rs = pst.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            LOG.error("操作失败", e);
        } finally {
            close(con, pst, rs);
        }
        return false;
    }

    public static boolean login(User user){
        String sql = "select password from user where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            rs = pst.executeQuery();
            if (rs.next()) {
                String password = rs.getString(1);
                if(StringUtils.isNotBlank(user.getPassword()) && user.getPassword().equals(password)){
                    return true;
                }
            }
        } catch (SQLException e) {
            LOG.error("登录失败", e);
        } finally {
            close(con, pst, rs);
        }
        return false;
    }

    public static boolean register(User user) {
        String sql = "insert into user (user_name, password, date_time) values (?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return false;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setTimestamp(3, new Timestamp(user.getDateTime().getTime()));
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOG.error("注册失败", e);
        } finally {
            close(con, pst, rs);
        }
        return false;
    }

    public static UserText getUseTextFromDatabase(int id) {
        String sql = "select id,text,date_time,user_name from user_text where id=?";
        Connection con = getConnection();
        if(con == null){
            return null;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int _id = rs.getInt(1);
                String text = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);;
                String user_name = rs.getString(4);
                UserText userText = new UserText();
                userText.setId(id);
                userText.setText(text);
                userText.setDateTime(new java.util.Date(timestamp.getTime()));
                userText.setUserName(user_name);
                return userText;
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return null;
    }

    public static List<UserDynamicPrefix> getHistoryUserDynamicPrefixesFromDatabase(String userName) {
        List<UserDynamicPrefix> userDynamicPrefixes = new ArrayList<>();
        String sql = "select id,dynamic_prefix,date_time from user_dynamic_prefix where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userDynamicPrefixes;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String dynamicPrefix = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserDynamicPrefix userDynamicPrefix = new UserDynamicPrefix();
                userDynamicPrefix.setId(id);
                userDynamicPrefix.setDynamicPrefix(dynamicPrefix);
                userDynamicPrefix.setDateTime(new java.util.Date(timestamp.getTime()));
                userDynamicPrefix.setUserName(userName);
                userDynamicPrefixes.add(userDynamicPrefix);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userDynamicPrefixes;
    }

    public static List<UserDynamicSuffix> getHistoryUserDynamicSuffixesFromDatabase(String userName) {
        List<UserDynamicSuffix> userDynamicSuffixes = new ArrayList<>();
        String sql = "select id,dynamic_suffix,date_time from user_dynamic_suffix where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userDynamicSuffixes;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String dynamicSuffix = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserDynamicSuffix userDynamicSuffix = new UserDynamicSuffix();
                userDynamicSuffix.setId(id);
                userDynamicSuffix.setDynamicSuffix(dynamicSuffix);
                userDynamicSuffix.setDateTime(new java.util.Date(timestamp.getTime()));
                userDynamicSuffix.setUserName(userName);
                userDynamicSuffixes.add(userDynamicSuffix);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userDynamicSuffixes;
    }

    public static List<UserSimilarWord> getHistoryUserSimilarWordsFromDatabase(String userName) {
        List<UserSimilarWord> userSimilarWords = new ArrayList<>();
        String sql = "select id,similar_word,date_time from user_similar_word where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userSimilarWords;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String similarWord = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserSimilarWord userSimilarWord = new UserSimilarWord();
                userSimilarWord.setId(id);
                userSimilarWord.setSimilarWord(similarWord);
                userSimilarWord.setDateTime(new java.util.Date(timestamp.getTime()));
                userSimilarWord.setUserName(userName);
                userSimilarWords.add(userSimilarWord);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userSimilarWords;
    }

    public static List<UserBook> getHistoryUserBooksFromDatabase(String userName) {
        List<UserBook> userBooks = new ArrayList<>();
        String sql = "select id,book,date_time from user_book where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userBooks;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String book = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserBook userBook = new UserBook();
                userBook.setId(id);
                userBook.setBook(book);
                userBook.setDateTime(new java.util.Date(timestamp.getTime()));
                userBook.setUserName(userName);
                userBooks.add(userBook);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userBooks;
    }

    public static List<UserUrl> getHistoryUserUrlsFromDatabase(String userName) {
        List<UserUrl> userUrls = new ArrayList<>();
        String sql = "select id,url,date_time from user_url where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userUrls;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String url = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserUrl userUrl = new UserUrl();
                userUrl.setId(id);
                userUrl.setUrl(url);
                userUrl.setDateTime(new java.util.Date(timestamp.getTime()));
                userUrl.setUserName(userName);
                userUrls.add(userUrl);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userUrls;
    }

    public static List<UserText> getHistoryUserTextsFromDatabase(String userName) {
        List<UserText> userTexts = new ArrayList<>();
        String sql = "select id,text,date_time from user_text where user_name=?";
        Connection con = getConnection();
        if(con == null){
            return userTexts;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserText userText = new UserText();
                userText.setId(id);
                userText.setText(text);
                userText.setDateTime(new java.util.Date(timestamp.getTime()));
                userText.setUserName(userName);
                userTexts.add(userText);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userTexts;
    }

    public static List<UserWord> getHistoryUserWordsFromDatabase(String userName) {
        List<UserWord> userWords = new ArrayList<>();
        String sql = "select id,word,date_time from user_word where user_name=? order by date_time desc";
        Connection con = getConnection();
        if(con == null){
            return userWords;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userName);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String word = rs.getString(2);
                Timestamp timestamp = rs.getTimestamp(3);
                UserWord userWord = new UserWord();
                userWord.setId(id);
                userWord.setWord(word);
                userWord.setDateTime(new java.util.Date(timestamp.getTime()));
                userWord.setUserName(userName);
                userWords.add(userWord);
            }
        } catch (SQLException e) {
            LOG.error("查询失败", e);
        } finally {
            close(con, pst, rs);
        }
        return userWords;
    }

    public static void saveUserSimilarWordToDatabase(UserSimilarWord userSimilarWord) {
        EXECUTOR_SERVICE.execute(()->_saveUserSimilarWordToDatabase(userSimilarWord));
    }

    public static void _saveUserSimilarWordToDatabase(UserSimilarWord userSimilarWord) {
        String sql = "insert into user_similar_word (user_name, similar_word, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userSimilarWord.getUserName());
            pst.setString(2, userSimilarWord.getSimilarWord());
            pst.setString(3, MD5(userSimilarWord.getUserName()+userSimilarWord.getSimilarWord()));
            pst.setTimestamp(4, new Timestamp(userSimilarWord.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserDynamicPrefixToDatabase(UserDynamicPrefix userDynamicPrefix) {
        EXECUTOR_SERVICE.execute(()->_saveUserDynamicPrefixToDatabase(userDynamicPrefix));
    }

    public static void _saveUserDynamicPrefixToDatabase(UserDynamicPrefix userDynamicPrefix) {
        String sql = "insert into user_dynamic_prefix (user_name, dynamic_prefix, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userDynamicPrefix.getUserName());
            pst.setString(2, userDynamicPrefix.getDynamicPrefix());
            pst.setString(3, MD5(userDynamicPrefix.getUserName()+userDynamicPrefix.getDynamicPrefix()));
            pst.setTimestamp(4, new Timestamp(userDynamicPrefix.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserDynamicSuffixToDatabase(UserDynamicSuffix userDynamicSuffix) {
        EXECUTOR_SERVICE.execute(()->_saveUserDynamicSuffixToDatabase(userDynamicSuffix));
    }

    public static void _saveUserDynamicSuffixToDatabase(UserDynamicSuffix userDynamicSuffix) {
        String sql = "insert into user_dynamic_suffix (user_name, dynamic_suffix, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userDynamicSuffix.getUserName());
            pst.setString(2, userDynamicSuffix.getDynamicSuffix());
            pst.setString(3, MD5(userDynamicSuffix.getUserName()+userDynamicSuffix.getDynamicSuffix()));
            pst.setTimestamp(4, new Timestamp(userDynamicSuffix.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserBookToDatabase(UserBook userBook) {
        EXECUTOR_SERVICE.execute(()->_saveUserBookToDatabase(userBook));
    }

    public static void _saveUserBookToDatabase(UserBook userBook) {
        String sql = "insert into user_book (user_name, book, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userBook.getUserName());
            pst.setString(2, userBook.getBook());
            pst.setString(3, MD5(userBook.getUserName()+userBook.getBook()));
            pst.setTimestamp(4, new Timestamp(userBook.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserUrlToDatabase(UserUrl userUrl) {
        EXECUTOR_SERVICE.execute(()->_saveUserUrlToDatabase(userUrl));
    }

    public static void _saveUserUrlToDatabase(UserUrl userUrl) {
        String sql = "insert into user_url (user_name, url, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userUrl.getUserName());
            pst.setString(2, userUrl.getUrl());
            pst.setString(3, MD5(userUrl.getUserName()+userUrl.getUrl()));
            pst.setTimestamp(4, new Timestamp(userUrl.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserTextToDatabase(UserText userText) {
        EXECUTOR_SERVICE.execute(()->_saveUserTextToDatabase(userText));
    }

    public static void _saveUserTextToDatabase(UserText userText) {
        String sql = "insert into user_text (user_name, text, md5, date_time) values (?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userText.getUserName());
            pst.setString(2, userText.getText());
            pst.setString(3, MD5(userText.getUserName() + userText.getText()));
            pst.setTimestamp(4, new Timestamp(userText.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static void saveUserWordToDatabase(UserWord userWord) {
        EXECUTOR_SERVICE.execute(()->_saveUserWordToDatabase(userWord));
    }

    public static void _saveUserWordToDatabase(UserWord userWord) {
        String sql = "insert into user_word (user_name, word, date_time) values (?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, userWord.getUserName());
            pst.setString(2, userWord.getWord());
            pst.setTimestamp(3, new Timestamp(userWord.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (Exception e) {
            LOG.error("MySQL获取数据库连接失败：", e);
        }
        return con;
    }

    private static DataSource setupDataSource(String connectUri, String uname, String passwd) {
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(connectUri, uname, passwd);

        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);

        // Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource<PoolableConnection> dataSource =
                new PoolingDataSource<>(connectionPool);

        return dataSource;
    }

    public static void close(Statement st) {
        close(null, st, null);
    }

    public static void close(Statement st, ResultSet rs) {
        close(null, st, rs);
    }

    public static void close(Connection con, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            LOG.error("数据库关闭失败", e);
        }
    }

    public static void close(Connection con, Statement st) {
        close(con, st, null);
    }

    public static void close(Connection con) {
        close(con, null, null);
    }
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
    public static void main(String[] args) throws Exception {
        UserWord userWord = new UserWord();
        userWord.setDateTime(new Date(System.currentTimeMillis()));
        userWord.setWord("fabulous");
        userWord.setUserName("ysc");
        MySQLUtils.saveUserWordToDatabase(userWord);

        System.out.println(MySQLUtils.getHistoryUserWordsFromDatabase("ysc"));
    }

    public static boolean processQQUser(QQUser qqUser) {
        if(qqUser.getUserName() == null){
            return false;
        }
        qqUser.setPassword("");
        if(!existUser(qqUser, "user")){
            register(qqUser);
        }
        if(!existUser(qqUser, "user_qq")){
            saveQQUser(qqUser);
        }
        return true;
    }

    private static void saveQQUser(QQUser user) {
        String sql = "insert into user_qq (user_name, password, nickname, gender, birthday, location, avatarURL30, avatarURL50, avatarURL100, date_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, user.getUserName());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getNickname());
            pst.setString(4, user.getGender());
            pst.setString(5, user.getBirthday());
            pst.setString(6, user.getLocation());
            pst.setString(7, user.getAvatarURL30());
            pst.setString(8, user.getAvatarURL50());
            pst.setString(9, user.getAvatarURL100());
            pst.setTimestamp(10, new Timestamp(user.getDateTime().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error("保存失败", e);
        } finally {
            close(con, pst, rs);
        }
    }
}