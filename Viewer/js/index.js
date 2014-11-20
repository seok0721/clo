var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10000';
var SUCCESS = 0;
var FAILURE = 1;
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }]
};
var metaInfoPool = [];
var channelList = [ null, null, null, null ];
var socket = io.connect(NODE_SERVER);
socket.on('handshake2', handshake2Handler);

function handshake2Handler(data) {
  var ret = data.ret;
  var sdp = data.sdp;
  var channel = data.channel;
  var meta;

  if(ret == FAILURE) {
    alert('핸드쉐이크 도중 오류가 발생하였습니다.');
    return;
  }

  console.log(metaInfoPool);
  console.log(metaInfoPool.length);

  for(var i = 0; i < metaInfoPool.length; i++) {
    meta = metaInfoPool[i];

    if(meta.channel == channel) {
      meta.connection.setRemoteDescription(new RTCSessionDescription({
        'type': 'answer',
        'sdp':  sdp
      }));
      console.log('연결 끝');
      return;
    }
  }

  console.log('핸드쉐이크 할 연결이 없습니다. 채널: ' + channel);
  alert('핸드쉐이크 할 연결이 없습니다. 채널: ' + channel);
}

$(function() {
  var btnWatch = $('#btnWatch');
  var txtChannel = $('#txtChannel');

  btnWatch.click(function(e) {
    var channel = txtChannel.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel.focus();
      return;
    }

    for(i = 0; i < channelList.length; i++) {
      if(channel == channelList[i]) {
        alert('이미 시청중인 채널입니다.');
        return;
      }
    }

    for(i = 0; i < channelList.length; i++) {
      if(channelList[i] == null) {
        channelList[i] = channel;
        alert(channel + '채널에 접속을 시도합니다.');
        alert('vidChannel' + (i + 1));
        watch(channel, 'vidChannel' + (i + 1));
        return;
      }
    }

    alert('모두 시청 중 입니다.');
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

      metaInfoPool.push({
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
