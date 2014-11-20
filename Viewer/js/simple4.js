var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10000';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }]
};

var connectionPool = [];
var socket = io.connect(NODE_SERVER);
socket.on('handshake2', handshake2Handler);
// socket.on('enterChannel', enterChannelHandler);
socket.on('enterChannel', function(data) {
  var ret = data.ret;
  var socketId = data.socketId;
  var channel = data.channel;
  var message = data.message;

  onReceiveMessage(channel, message);
});

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
  var ret = data.ret;
  var sdp = data.sdp;
  var channel = data.channel;
  var conn;

  console.log(connectionPool);
  console.log(connectionPool.length);

  for(var i = 0; i < connectionPool.length; i++) {
    conn = connectionPool[i];

    if(conn.channel == channel) {
      console.log(conn);

      conn.connection.setRemoteDescription(new RTCSessionDescription({
        'type': 'answer',
        'sdp':  sdp
      }));
      console.log('연결 끝');
      return;
    }
  }

  console.log('핸드쉐이크 할 연결이 없습니다. 채널: ' + channel);
}

function initWatchButton(watchButtonId, channelTextBoxId, videoElementId) {
  var btnWatch = $('#' + watchButtonId);
  var txtChannel = $('#' + channelTextBoxId);

  btnWatch.click(function(e) {
    var channel = txtChannel.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel.focus();
      return;
    }

    alert('"' + channel + '" 채널에 접속합니다.');
    watch(channel, videoElementId);
  });
}

function watch(channel, videoElementId) {
  var video = $('#' + videoElementId)[0];
  var conn = new RTCPeerConnection(ICE_SERVERS); 
  conn.channel = channel;
  conn.video = video;
  conn.onicecandidate = iceCandidateListener;
  conn.onaddstream = addStreamListener;

  conn.createOffer(function(desc) {
    conn.setLocalDescription(desc);
  }, function(err) {
    console.log(err);
  }, {
    mandatory: {
      OfferToReceiveAudio: true,
      OfferToReceiveVideo: true
    }
  });

  function iceCandidateListener(e) {
    if(conn.iceGatheringState != 'complete') {
      conn.addIceCandidate(new RTCIceCandidate(e.candidate));
      return;
    }

    connectionPool.push({
      'channel': conn.channel,
      'connection': conn
    });

    socket.emit('handshake', {
      'channel': channel,
      'sdp': conn.localDescription.sdp
    });
  }

  function addStreamListener(e) {
    console.log('멀티미디어 수신');
    socket.emit('viewer_count');
    video.src = URL.createObjectURL(e.stream);
  }
}

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

$(function() {
  initWatchButton('btnWatch1', 'txtChannel1', 'vidRemote1');
  initWatchButton('btnWatch2', 'txtChannel2', 'vidRemote2');
  initWatchButton('btnWatch3', 'txtChannel3', 'vidRemote3');
});
