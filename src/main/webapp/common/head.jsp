<%--
  ~ APDPlat - Application Product Development Platform
  ~ Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
  ~
  ~  This program is free software: you can redistribute it and/or modify
  ~  it under the terms of the GNU General Public License as published by
  ~  the Free Software Foundation, either version 3 of the License, or
  ~  (at your option) any later version.
  ~
  ~  This program is distributed in the hope that it will be useful,
  ~  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~  GNU General Public License for more details.
  ~
  ~  You should have received a copy of the GNU General Public License
  ~  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<table>
  <tr>
    <td>
      <h2><a href="https://github.com/ysc/superword" target="_blank">superword开源项目</a></h2>
    </td>
    <td>
      <div class="bdsharebuttonbox" data-tag="share_1">
        <a class="bds_mshare" data-cmd="mshare"></a>
        <a class="bds_qzone" data-cmd="qzone" href="#"></a>
        <a class="bds_tsina" data-cmd="tsina"></a>
        <a class="bds_baidu" data-cmd="baidu"></a>
        <a class="bds_renren" data-cmd="renren"></a>
        <a class="bds_tqq" data-cmd="tqq"></a>
        <a class="bds_more" data-cmd="more">更多</a>
        <a class="bds_count" data-cmd="count"></a>
      </div>
    </td>
  </tr>
</table>
<script>
  window._bd_share_config = {
    common : {
      bdText : '推荐superword开源项目',
      bdDesc : 'superword支持最权威的2部中文词典和9部英文词典，支持23种分级词汇，囊括了所有的英语考试，还专门针对程序员提供了249本最热门的技术书籍的辅助阅读功能。',
      bdUrl : 'https://github.com/ysc/superword',
      bdPic : ''
    },
    share : [{
      "bdSize" : 16
    }],
    slide : [{
      bdImg : 0,
      bdPos : "right",
      bdTop : 100
    }],
    image : [{
      viewType : 'list',
      viewPos : 'top',
      viewColor : 'black',
      viewSize : '16',
      viewList : ['qzone','tsina','huaban','tqq','renren']
    }],
    selectShare : [{
      "bdselectMiniList" : ['qzone','tqq','kaixin001','bdxc','tqf']
    }]
  }
  with(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?cdnversion='+~(-new Date()/36e5)];
</script>