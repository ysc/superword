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
      bdText : 'Recommend superword open source project',
      bdDesc : 'Superword is a Java open source project dedicated in the study of English words analysis and auxiliary reading.',
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
function openWindow(url, word){
  window.open(contextPath+url, word, 'width=1200,height=600');
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

function queryWord(word){
  openWindow("/definition.jsp?word="+word, word);
}

function querySelectionWord(){
  var word = "";
  if(window.getSelection){
    word = window.getSelection().toString();
    if("" == word){
      var textArea = document.getElementById("text");
      var start = textArea.selectionStart;
      var finish = textArea.selectionEnd;
      word = textArea.value.substring(start, finish);
    }
  }
  else{
    word = document.selection.createRange().text;
  }
  word = trim(word);
  if(word != ""){
    openWindow("/definition.jsp?word="+word, word);
  }
}
