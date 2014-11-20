var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10000';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }]
};
var connectionMap = {};;
var SUCCESS = 0;
var FAILURE = 1;

var socket = io.connect(NODE_SERVER);
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
socket.on('removeChannel', function(data) {
  var email = data.email;

  connectionMap[email].close();
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
  console.log(data);
  console.log(email);
  console.log(conn);

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
/*
function onReceiveMessage(channel, message) {
  var messageBox = $('<blockquote class="blockquote-reverse"></blockquote>');

  // TODO Process

  updateMessageUi(messageListBoxId, messageBox);
}

function updateMessageUi(messageListBoxId, messageBox) {
  var messageListBox = $('#' + messageListBoxId);

  // TODO Append
}

function handshake2Handler(data) {
}
*/

function initWatchButton(watchButtonId, channelTextBoxId, videoElementId, messageTextBoxId, sendMessageButtonId) {
  var btnWatch = $('#' + watchButtonId);
  var txtChannel = $('#' + channelTextBoxId);

  btnWatch.click(function(e) {
    var email = txtChannel.val();

    if(email.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel.focus();
      return;
    }

    alert('"' + email + '" 채널에 접속합니다.');
    watch(email, videoElementId, messageTextBoxId, sendMessageButtonId);
  });
}

function watch(email, videoElementId, messageTextBoxId, sendMessageButtonId) {
  var txtMessage = $('#' + messageTextBoxId);
  var btnSend = $('#' + sendMessageButtonId);
  var video = $('#' + videoElementId)[0];
  var conn = new RTCPeerConnection(ICE_SERVERS); 
  conn.email = email;
  conn.video = video;
  conn.onicecandidate = function(e) {
    if(conn.iceGatheringState != 'complete') {
      conn.addIceCandidate(new RTCIceCandidate(e.candidate));
    } else {
      socket.emit('offer', {
        'email': email,
        'sdp': conn.localDescription.sdp
      });
    }
  };
  conn.onaddstream = function(e) {
    video.src = URL.createObjectURL(e.stream);
  };
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

  connectionMap[email] = conn;
  console.log(connectionMap[email]);

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

/*
function initChatSendButton(messageTextBoxId, sendMessageButtonId, messageListId) {
  var txtMessage = $('#' + messageTextBoxId);
  var btnSndMsg = $('#' + sendMessageButtonId);
  var lstMessage = $('#' + messageListId);

  btnSndMsg.click(function() {
    var message = txtMessage.val();

    if(message.length() == 0) {
      return;
    }

    lstMessage.append('\n' + socket.id + ': ' + message);

    socket.emit('chat', {
      'channel': '',
      'message': message
    });
  });
}

function initChatReceiveList(messageListId) {
  var lstMessage = $('#' + messageListId);

  socket.on('chat', function(data) {
    var userId = data.id;
    var message = data.message;

    lstMessage.append('\n' + userId + ': ' + message);
  });
}
*/

$(function() {
  initWatchButton('btnWatch1', 'txtChannel1', 'vidRemote1', 'txtMessage1', 'btnMessage1');
//  initWatchButton('btnWatch2', 'txtChannel2', 'vidRemote2');
//  initWatchButton('btnWatch3', 'txtChannel3', 'vidRemote3');
});
