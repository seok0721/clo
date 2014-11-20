var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10080';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }]
};
/*
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }, {
    'url': 'turn:clo@211.189.20.193',
    'credential': 'clo'
  }]
};
*/

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
  var btnWatch1 = $('#btnWatch1');
  var btnWatch2 = $('#btnWatch2');
  var txtChannel1 = $('#txtChannel1');
  var txtChannel2 = $('#txtChannel2');

  btnWatch1.click(function(e) {
    var channel = txtChannel1.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel1.focus();
      return;
    }

    var email = channel;
    var conn = new RTCPeerConnection(ICE_SERVERS);  
    var socket = io.connect(NODE_SERVER);
    var vidRemote = $('#vidRemote1')[0];

    conn.onicecandidate = function(e) {
      if(!e.candidate) {
        return;
      }

      console.log(e);

      conn.addIceCandidate(new RTCIceCandidate(e.candidate));
    };

    conn.onaddstream = function(e) {
      console.log(e.stream);
      vidRemote.src = URL.createObjectURL(e.stream);
    };

    socket.on('connect', connect_handler);
    socket.on('offer', offer_handler);

    function connect_handler() {
      console.log('connect, join channel:' + email);

      socket.emit('join', {
        'email': email
      });
    }

    function offer_handler(data) {
      var sdp = data.sdp;

      // console.log('receive offer');
      // console.log(sdp);

      if(conn.remoteDescription) {
        // console.log('have remote description');
        return;
      }

      console.log('set remote description');

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

  btnWatch2.click(function(e) {
    var channel = txtChannel2.val();

    if(channel.length == 0) {
      alert('채널을 입력하세요.');
      txtChannel2.focus();
      return;
    }

    var email = channel;
    var conn = new RTCPeerConnection(ICE_SERVERS);
    var socket = io.connect(NODE_SERVER);
    var vidRemote = $('#vidRemote2')[0];

    conn.onicecandidate = function(e) {
      if(!e.candidate) {
        return;
      }

      console.log(e);

      conn.addIceCandidate(new RTCIceCandidate(e.candidate));
    };

    conn.onaddstream = function(e) {
      console.log(e.stream);
      vidRemote.src = URL.createObjectURL(e.stream);
    };

    socket.on('connect', connect_handler);
    socket.on('offer', offer_handler);
    socket.on('error', function(e) {
      console.log(e);
    });

    function connect_handler() {
      console.log('connect, join channel:' + email);

      socket.emit('join', {
        'email': email
      });
    }

    function offer_handler(data) {
      var sdp = data.sdp;

      // console.log('receive offer');
      // console.log(sdp);

      if(conn.remoteDescription) {
        // console.log('have remote description');
        return;
      }

      console.log('set remote description');

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

