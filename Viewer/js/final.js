$(function() {
  var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
  var nodeServer = 'http://211.189.20.193:10000';
  var iceServers = {
    'iceServers' : [{
      'url': 'stun:stun.l.google.com:19302'
    }, {
      'url': 'turn:211.189.20.193:3478'
    }]
  };
  var SUCCESS = 0;
  var FAILURE = 1;
  var btnChannel = $('#btnChannel');
  var btnChat = $('#btnChat');
  var pnlChannel = $('#divChannelList');
  var lstChat0 = $('#divChatList0');
  var lstChat1 = $('#divChatList1');
  var lstChat2 = $('#divChatList2');
  var lstChat3 = $('#divChatList3');
  var lstChat4 = $('#divChatList4');
  var socket = io.connect(nodeServer);
  var waitForReceiveChannelList = false;

  socket.topChatEmail = 0;
  socket.containerMap = {};
  socket.connectionMap = {};
  socket.chatMap = {};
  socket.videoMap = {};

  function swapChatPanel(email) {
    var chatPanel1 = $('#divChatList' + socket.chatMap[socket.topChatEmail]);
    var chatPanel2 = $('#divChatList' + socket.chatMap[email];

    chatPanel1.hide();
    chatPanel2.show();

    socket.topChatEmail = email;
  }

  function sortVideo() {
    for(var i = 1; i < 5; i++) {
      var container = $('#divContainer' + i);

      if(container.children().length == 0) {
        continue;
      }

      var email = $('#divContainer' + i).attr('email');

      $('#divContainer' + i + ' a').first().appendTo('#divContainer0');
      $('#divContainer' + i).removeAttr('email');

      $('#divContainer0 video').first()[0].play();
      $('#divContainer0').attr('email', email);

      socket.containerMap[email] = 0;

      $('#divContainer0 a').unbind('click');
      $('#divContainer0 a').click(function() {
        onClickVideo(0);
      });

      // socket.topChatEmail = socket.chatMap[email];
//      console.log('채팅맵!!! ' + email);
//      console.log('채팅맵!!! ' + socket.chatMap[email]);
      swapChatPanel(email);
      break;
    }
  }

  function onClickVideo(index) {
    console.log(index);
    if(index == 0) { // 큰 화면인 경우
      var email = $('#divContainer0').attr('email');

      if(!confirm(email + ' 시청중인 방송을 끄시겠습니까?')) {
        return;
      }

      socket.connectionMap[email].close();
    } else { // 작은 화면인 경우
      // 비디오 스왑
      var anchor = $('#divContainer' + index + ' a').first();
      $('#divContainer' + index).empty();
      $('#divContainer0 a').first().appendTo('#divContainer' + index);
      anchor.appendTo('#divContainer0');

      // 이메일 속성 스왑
      var email1 = $('#divContainer' + index).attr('email');
      var email2 = $('#divContainer0').attr('email');

//      socket.connectionMap[email1].email = email2;
//      socket.connectionMap[email2].email = email1;

      $('#divContainer' + index).attr('email', email2);
      $('#divContainer0').attr('email', email1);

      console.log(socket.containerMap);

      $('#divContainer' + index + ' video').first()[0].play();
      $('#divContainer0 video').first()[0].play();

      $('#divContainer' + index + ' a').unbind('click');
      $('#divContainer' + index + ' a').click(function() {
        onClickVideo(index);
      });

      $('#divContainer0 a').unbind('click');
      $('#divContainer0 a').click(function() {
        onClickVideo(0);
      });

      // 컨테이너 스왑
      socket.containerMap[email1] = 0;
      socket.containerMap[email2] = index;

      // socket.topChatEmail = socket.chatMap[email];
      swapChatPanel(email1);
    }
  }

  function connect(email) {
    if(Object.keys(socket.connectionMap).length == 5) {
      alert('비디오를 재생할 공간이 없습니다.');
      return;
    }

    var conn = new RTCPeerConnection(iceServers);
    conn.email = email;

    conn.onaddstream = function(e) {
      socket.videoMap[conn.email].src = URL.createObjectURL(e.stream);
      alert('비디오를 수신합니다.');
    };

    conn.onicecandidate = function(e) {
      if(conn.iceGatheringState != 'complete') {
        if(e.candidate) {
          conn.addIceCandidate(new RTCIceCandidate(e.candidate));
        }
        return;
      }

      if(conn.isEntered) {
        return;
      }

      socket.emit('enterChannel', {
        'email': conn.email
      });

      conn.isEntered = true;
    };

    conn.onsignalingstatechange = function(e) {
      if(conn.signalingState == 'closed') {
        $('#divContainer' + socket.containerMap[conn.email]).empty();
        $('#divContainer' + socket.containerMap[conn.email]).removeAttr('email');
        delete socket.containerMap[conn.email];

        console.log(socket.chatMap[conn.email]);
        console.log($('#divChatList' + socket.chatMap[conn.email]));

        $('#divChatList' + socket.chatMap[conn.email]).empty();
//        delete socket.chatMap[conn.email];

        sortVideo();

        socket.emit('leaveChannel', {
          'email': conn.email
        });

        delete socket.connectionMap[conn.email];
        delete socket.videoMap[conn.email];;

        console.log(conn.email + ' 방송이 종료되었습니다.');
        console.log(socket.connectionMap);
        console.log(socket.containerMap);
        console.log(socket.videoMap);
        console.log(socket.chatMap);
      }
    };

    socket.connectionMap[conn.email] = conn;
    socket.videoMap[conn.email] = $('<video autoplay="true" style="width:100%; height:100%"></video>')[0];

    for(var i = 0; i < 5; i++) {
      var container = $('#divContainer' + i); 

      console.log('loop');
      console.log(container.children().length);

      if(container.children().length == 0) {
        socket.containerMap[conn.email] = i;

        console.log('select number');
        console.log(socket.containerMap);

        var anchor = $('<a></a>');
        anchor.append(socket.videoMap[conn.email]);
        anchor.click(function() {
          onClickVideo(socket.containerMap[conn.email]);
        });

        console.log('awefawef');
        console.log(conn.email);
        console.log(i);

        container.append(anchor);
        container.attr('email', conn.email);

        console.log('connect');
        console.log(socket.containerMap[conn.email]);
        console.log(container);
        break;
      }
    }

    for(var i = 0; i < 5; i++) {
      if($('#divChatList' + i).children().length == 0) {
        $('#divChatList' + i).append($('<span></span>'));
        socket.chatMap[conn.email] = i;
        if(i == 0) {
          socket.topChatEmail = conn.email;
        }
        break;
      }
    }

    conn.createOffer(function(desc) {
      conn.setLocalDescription(desc);
    }, function(err) {
      alert(err);
    }, {
      mandatory: {
        OfferToReceiveAudio: true,
        OfferToReceiveVideo: true
      }
    });
  }

  function getFormattedDate(d) {
    var date = new Date();
    var y = date.getFullYear();
    var M = date.getMonth();
    var d = date.getDate();
    var h = date.getHours();
    var m = date.getMinutes();
    var s = date.getSeconds();

    return y + '-' + M + '-' + d + ' ' + h + ':' + m + ':' + s;
  }

  function createChannelBox(email, encodedImage, title, name) { 
    var channelBox = $('<a style="display:inline-block;width:93%;'
      + 'background:#fff;'
      + 'margin:8px 8px 0 8px;'
      + '"></a>');
    channelBox.click(function() {
/*
      if(socket.containerMap[email] != null) {
        alert('이미 시청중인 채널입니다.');
        return;
      }
*/
      connect(email);
    });

    var thumbnail;
    if(encodedImage == null || encodedImage == 'undefined') {
      thumbnail = $('<img style="display:inline-block;width:50px;height:100%;float:left;" src="/img/user.png" />');
    } else {
      thumbnail = $('<img style="display:inline-block;width:50px;height:100%;float:left;" src="data:image/png;base64,' + encodedImage + '" />');
    }
    channelBox.append(thumbnail);

    var titleBox = $('<div style="display:block;margin-left:55px;'
      + 'font-weight:bold;'
      + 'color:#000;'
      + 'font-size:12pt;'
      + 'padding:2px;'
      + '"></div>');
    titleBox.html(title);
    channelBox.append(titleBox);

    var nameBox = $('<div style="display:block;margin-left:55px;'
      + 'font-size:9pt;'
      + 'color:#444;'
      + '"></div>');
    nameBox.html(name);
    channelBox.append(nameBox);
    return channelBox;
  }

  function createChatBox(message, isAlignRight) {
    var chatBox = $('<div style="display:inline-block;width:100%;'
      + 'background:#ebb867;'
      + 'border-bottom:1px solid #6f5e55;'
      + 'border-radius:15px;'
      + 'padding:2px;'
      + 'margin-bottom:2px;'
      + 'text-align:' + (isAlignRight ? 'right' : 'left') + ';'
      + '"></div>');
    var thumbnail = $('<img src="/img/user.png" style="'
      + 'display:inline-block;width:50px;height:50px;'
      + 'float:' + (isAlignRight ? 'right' : 'left') + ';'
      + '" />');
    var dateBox = $('<div style="display:block;margin-left:55px;'
//      + 'border:1px solid #000;'
      + 'color:#3c536f;'
      + 'text-decoration:underline;'
      + 'padding:0 0 2px 0;'
      + '"></div>');
    dateBox.html(getFormattedDate());

    var messageBox = $('<div style="display:block;margin-left:55px;'
//      + 'border:1px solid #000;'
      + '"></div>');
    messageBox.html(message);

    chatBox.append(thumbnail);
    chatBox.append(dateBox);
    chatBox.append(messageBox);
    return chatBox;
  }

  socket.on('enterChannel', function(data) {
    var ret = data.ret;
    var email = data.email;
    var socketId = data.socketId;

    if(ret == FAILURE) {
      console.log(email + ' 채널에 접속할 수 없습니다.');
      return;
    }

    if(socket.socket.sessionid != socketId) {
      console.log('다른 사람이 채널에 접속했습니다.');
      return;
    }

/*
    // FIXME 이 부분 어떻게 해결하지.. ??
    if(socket.containerMap[email] != null) {
      alert('이미 시청중인 채넣입니다.');
      return;
    }

    for(var i = 0; i < 5; i++) {
      if($('#divContainer' + i).children().length == 0) {
        socket.containerMap[email] = i;
        break;
      }
    }
*/
    socket.emit('offer', {
      'email': email,
      'sdp': socket.connectionMap[email].localDescription.sdp
    });

    alert(email + ' 채널에 접속합니다.');
  });

  socket.on('getChannelList', function(data) {
    var pnlChannel = $('#divChannelList');
    var channelList = data.channelList;
    var count = 0;

    for(var channel in channelList) {
      var broadcaster = JSON.parse(channelList[channel]);
      var name;

      try {
        name = decodeURIComponent(escape(atob(broadcaster.name)));
      } catch(err) {
        name = broadcaster.name;
      }

      $('#divChannelList').append(createChannelBox(channel, broadcaster.img, broadcaster.title, name));
      count++;
    }

    if(count == 0) {
      alert('방송중인 채널이 없습니다.');
    }

    waitForReceiveChannelList = false;
  });

  socket.on('removeChannel', function(data) {
    var email = data.email;

    socket.connectionMap[email].close();
/*
    console.log(socket.containerMap[email]);
    $('#divConatiner' + socket.containerMap[email]).empty();
    delete socket.containerMap[email];

    $('#divChat' + socket.chatMap[email]).empty();
    delete socket.chatMap[email];

    socket.connectionMap[email].close();
    delete socket.connectionMap[email];

    delete socket.videoMap[email];
    sortVideo();

    console.log(email + ' 채널이 종료되었습니다.');
*/

  });

  socket.on('offer', function(data) {
    var ret = data.ret;

    if(ret == FAILURE) {
      alert('채널 시청에 실패하였습니다.');
    }
  });

  socket.on('answer', function(data) {
    var email = data.email;
    var sdp = data.sdp;
    var conn = socket.connectionMap[email];

    if(conn) {
      conn.setRemoteDescription(new RTCSessionDescription({
        'type': 'answer',
        'sdp':  sdp
      }));
    } else {
      console.log('멀티미디어를 받을 연결이 없습니다.');
    }
  });

  socket.on('chat', function(data) {
    var socketId = data.socketId;
    var email = data.email;
    var message = data.message;

    $('#divChatList' + socket.chatMap[email]).append(createChatBox(message));

    console.log('소켓: ' + socketId + ', 채널: ' + email + ', 메시지: ' + message);

//    var elem = $('#divChatList' + socket.chatMap[email]);
//    elem.scrollHeight - elem.scrollTop() == elem.outerHeight();
  });

  btnChannel.click(function(e) {
    if(waitForReceiveChannelList) {
      return;
    }

    var pnlChannel = $('#divChannelList');
    var pnlChat = $('#divChatContainer');

    pnlChat.hide();
    pnlChannel.show();

    btnChannel.addClass('btn-primary');
    btnChannel.removeClass('btn-default');
    btnChat.addClass('btn-default');
    btnChat.removeClass('btn-primary');

    waitForReceiveChannelList = true;

    pnlChannel.empty();
    socket.emit('getChannelList');
  });

  btnChat.click(function(e) {
    var pnlChannel = $('#divChannelList');
    var pnlChat = $('#divChatContainer');

    btnChat.addClass('btn-primary');
    btnChat.removeClass('btn-default');
    btnChannel.addClass('btn-default');
    btnChannel.removeClass('btn-primary');

    pnlChannel.hide();
    pnlChat.show();
  });

  $('#txtMessage').keydown(function(e) {
    if(e.keyCode == 13) {
      $('#btnTalk').click();
    }
  });

  $('#btnTalk').click(function() {
    var email = $('#divContainer0').attr('email');

    if(!email) {
      alert('방송 시청중이 아닙니다.');
      return;
    }

    var message = $('#txtMessage').val();

    if(message.length == 0) {
      return;
    }

    console.log(socket.topChatEmail);

//    $('#divChatList' + socket.topChatEmail).append(createChatBox(message, true));
    $('#divChatList' + socket.chatMap[email]).prepend(createChatBox(message, true));

    $('#txtMessage').val('');
    $('#txtMessage').focus();

    socket.emit('chat', {
      'email': email,
      'message': message
    });
  });
});
