$(function() {
  var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
  var nodeServer = 'http://211.189.20.193:10000';
  var iceServers = {
    'iceServers' : [{
      'url': 'stun:stun.l.google.com:19302'
    }, {
      'url': 'stun:211.189.20.193:3478'
    }]
  };
  var connectionMap = {};
  var SUCCESS = 0;
  var FAILURE = 1;
  var btnChannel = $('#btnChannel');
  var lstChannel = $('#divChannelList');
  var lstMessage1 = $('#divMessageList1');
  var lstMessage2 = $('#divMessageList2');
  var lstMessage3 = $('#divMessageList3');
  var lstMessage4 = $('#divMessageList4');
  var socket = io.connect(nodeServer);
  var waitForReceiveChannelList = false;

  function connect(email) {
    if(Object.keys(connectionMap).length == 5) {
      alert('비디오를 재생할 공간이 없습니다.');
    }

    var conn = new RTCPeerConnection(iceServers); 
    conn.email = email;
    conn.video = $('<video autoplay="true" style="width:100%; height:100%"></video>')[0];
    conn.onicecandidate = function(e) {
      if(conn.iceGatheringState == 'complete') {
        socket.emit('offer', {
          'email': email,
          'sdp': conn.localDescription.sdp
        });
        return;
      }

      if(e.candidate) {
        conn.addIceCandidate(new RTCIceCandidate(e.candidate));
      }
    };
    conn.onaddstream = function(e) {
      conn.video.src = URL.createObjectURL(e.stream);
    };
    conn.onsignalingstatechange = function(e) {
      if(conn.signalingState == 'closed') {
        conn.video.pause();
        conn.container.empty();
        alert('방송이 종료되었습니다.');
      }
    };

    connectionMap[email] = conn;

    var i = 0;

    while(i < 5) {
      var container = $('#divVideo' + i); 

      if(container.children().length == 0) {
        container.append(conn.video);
        conn.container = container;
        alert('비디오 추가');
        break;
      }

      i++;
    }

    if(i == 5) {
      alert('비디오가 들어갈 빈 자리가 없습니다.');
      // TODO 예외 처리
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

  socket.on('enterChannel', function(data) {
    var ret = data.ret;
    var email = data.email;

    if(ret == FAILURE) {
      alert(email + ' 채널에 접속할 수 없습니다.');
    } else {
      socket.emit('offer', {
        'email': email,
        'sdp': connectionMap[email].localDescription.sdp
      });

      alert(email + ' 채널에 접속하였습니다.');
    }
  });

  socket.on('getChannelList', function(data) {
    var count = 0;

    for(var channel in data.channelList) {
      var broadcaster = JSON.parse(data.channelList[channel]);
      var link = $('<a></a>');
      var card = $('<p></p>');

      link.click(function() {
        connect(channel);
      });

      card.append($('<img style="border:1px solid #e8e8e8; width:5em;height:5em;" src="data:image/png;base64,' + broadcaster.img + '" />'));
      card.append($('<br />'));
      card.append($('<span></span>').html('제목:' + broadcaster.title));
      card.append($('<br />'));
      card.append($('<span></span>').html('방송자: ' + atob(broadcaster.name)));

      link.append(card);
      lstChannel.append(link);
      lstChannel.append($('<hr />'));

      count++;
    }

    if(count == 0) {
      console.log('방송중인 채널이 없습니다.');
    }

    waitForReceiveChannelList = false;
  });

  socket.on('removeChannel', function(data) {
    var email = data.email;

    connectionMap[email].close();
    delete connectionMap[email];
    alert(email + ' 채널이 종료되었습니다.');
  });

  socket.on('offer', function(data) {
    var ret = data.ret;

    if(ret == FAILURE) {
      alert('방송중인 채널이 없습니다.');
    }
  });

  socket.on('answer', function(data) {
    var email = data.email;
    var sdp = data.sdp;
    var conn = connectionMap[email];

    if(conn) {
      conn.setRemoteDescription(new RTCSessionDescription({
        'type': 'answer',
        'sdp':  sdp
      }));
    } else {
      alert('멀티미디어를 받을 연결이 없습니다.');
    }
  });

  socket.on('chat', function(data) {
    var socketId = data.socketId;
    var email = data.email;
    var message = data.message;

    // TODO Update UI
    console.log('소켓: ' + socketId + ', 채널: ' + email + ', 메시지: ' + message);
  });

  function watch(email, videoElementId, messageTextBoxId, sendMessageButtonId) {
    var txtMessage = $('#' + messageTextBoxId);
    var btnSend = $('#' + sendMessageButtonId);
    var video = $('#' + videoElementId)[0];
    btnSend.click(function(e) {
      var message = txtMessage.val().trim();

      console.log(message);

      if(message.length == 0) {
        return;
      }

      socket.emit('chat', {
        'email': email,
        'message': message
      });
    });

  }

  btnChannel.click(function(e) {
    if(waitForReceiveChannelList) {
      return;
    }

    waitForReceiveChannelList = true;
    lstChannel.empty();
    socket.emit('getChannelList');
  });

//  initWatchButton('btnWatch3', 'txtChannel3', 'vidRemote3', 'txtMessage1', 'btnMessage1');
//  initWatchButton('btnWatch2', 'txtChannel2', 'vidRemote2', 'txtMessage1', 'btnMessage1');
//  initWatchButton('btnWatch1', 'txtChannel1', 'vidRemote1', 'txtMessage1', 'btnMessage1');
});
