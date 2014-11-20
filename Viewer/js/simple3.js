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

$(function() {
  var btnWatch1 = $('#btnWatch1');
  var btnWatch2 = $('#btnWatch2');
  var btnWatch3 = $('#btnWatch3');
  var txtChannel1 = $('#txtChannel1');
  var txtChannel2 = $('#txtChannel2');
  var txtChannel3 = $('#txtChannel3');

  btnWatch1.click(function(e) {
    var channel = txtChannel1.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel1.focus();
      return;
    }

    alert('1번을 시청합니다.');
    watch(channel, 'vidRemote1');
  });

  btnWatch2.click(function(e) {
    var channel = txtChannel2.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel2.focus();
      return;
    }

    alert('2번을 시청합니다.');
    watch(channel, 'vidRemote2');
  });

  btnWatch3.click(function(e) {
    var channel = txtChannel3.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel3.focus();
      return;
    }

    alert('3번을 시청합니다.');
    watch(channel, 'vidRemote3');
  });

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
      console.log('get media');
      socket.emit('viewer_count');
      video.src = URL.createObjectURL(e.stream);
    }
  }
});
