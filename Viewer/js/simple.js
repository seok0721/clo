var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10080';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }, {
    'url': 'turn:clo@211.189.20.193',
    'credential': 'clo'
  }]
};

function spin(condition, after) {
  var lock = setInterval(critical, 500);
  
  function critical() {
    if(!condition()) {
      return;
    }

    clearInterval(lock);

    if(!after) {
      return;
    }

    after();
  }
}

$(function() {
  var container = [];
  var video_container = $('#divScreen');
  var btnConnect = $('#btnConnect');
  var txtEmail = $('#txtEmail');

  btnConnect.click(function() {
    if(txtEmail.val().length == 0) {
      alert('방송자 메일 주소를 입력하세요.');
      return 0;
    }

    var socket = io.connect(NODE_SERVER);
    var conn = new RTCPeerConnection(ICE_SERVERS);  
    var video = $('<video id="vidRemote" autoplay="true" style="width:320px; height:240px; border: 1px solid #e8e8e8;"></video>');

    conn.onicecandidate = gotIceCandidate;
    conn.onaddstream = gotRemoteStream;

    function gotRemoteStream(e) {
      console.log('스트림을 수신합니다.');

      video.src = URL.createObjectURL(e.stream);
    }

    function gotIceCandidate(e) {
      if(!e.candidate) {
        return;
      }

      console.log(e);

      conn.addIceCandidate(new RTCIceCandidate(e.candidate));
    }

    socket.on('connect', connect_handler);
    socket.on('offer', offer_handler);

    function connect_handler() {
      console.log('접속되었습니다.');
    }

    function offer_handler(data) {
      var sdp = data.sdp;

      console.log('오퍼를 처리합니다.');

      if(conn.remoteDescription) {
        console.log('have remote description');
        return;
      }

      conn.setRemoteDescription(new RTCSessionDescription({
        'type': 'offer',
        'sdp': sdp
      }), function() {
        console.log('set remote description success.');

        conn.createAnswer(function(desc) {
          conn.setLocalDescription(desc, function() {
            spin(checkIceGatheringState, afterCheckingIceGatheringState);

            function checkIceGatheringState() {
              return conn.iceGatheringState == 'complete';
            }

            function afterCheckingIceGatheringState() {
              spin(checkLocalDescription, emitAnswer);
            }

            function checkLocalDescription() {
              return conn.localDescription != null;
            }

            function emitAnswer() {
              socket.emit('answer', {
                'sdp': conn.localDescription.sdp
              });
            }
          });
        });
      }, function(err) {
        console.log('set remote description failure.');
        console.log(err);
      });
    }
  });
});

