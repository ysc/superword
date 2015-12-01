$(document).ready(function(){
  $("#back-to-top").hide();
  $(function () {
    $(window).scroll(function(){
      if ($(window).scrollTop()>100){
        $("#back-to-top").fadeIn(1500);
      }else{
        $("#back-to-top").fadeOut(1500);
      }
    });
    $("#back-to-top").click(function(){
      $('body,html').animate({scrollTop:0},1000);
      return false;
    });
  });

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

  var _hmt = _hmt || [];
  (function() {
    var hm = document.createElement("script");
    hm.src = "//hm.baidu.com/hm.js?f1d1aba1dfb4b13f7b3590af95d63ae4";
    var s = document.getElementsByTagName("script")[0];
    s.parentNode.insertBefore(hm, s);
  })();
});

//查看定义
function viewDefinition(url, word){
  window.open(url, word, 'width=1200,height=600');
}
//trim()
function trim(x) {
  if(x.trim){
    return x.trim();
  }
  if(x.replace) {
    return x.replace(/^\s+|\s+$/gm, '');
  }
  return x;
}