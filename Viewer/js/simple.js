var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var NODE_SERVER = 'http://211.189.20.193:10080';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }]
};

$(function() {
  var socket = io.connect(NODE_SERVER);
  var conn = new RTCPeerConnection(ICE_SERVERS);  
  var btnConnect = $('#btnConnect');
  var vidRemote = $('#vidRemote')[0];

  conn.onicecandidate = gotIceCandidate;
  conn.onaddstream = gotRemoteStream;

  socket.on('connect', connect_handler);
  socket.on('offer', offer_handler);

  function gotRemoteStream(e) {
    console.log(e.stream);
    vidRemote.src = URL.createObjectURL(e.stream);
  }

  function gotIceCandidate(e) {
    if(!e.candidate) {
      return;
    }

    console.log(e);

    conn.addIceCandidate(new RTCIceCandidate(e.candidate));
  }

  function connect_handler() {
    console.log('connect');
  }

  function offer_handler(data) {
    var sdp = data.sdp;

    console.log('receive offer');
    console.log(sdp);

    conn.setRemoteDescription(new RTCSessionDescription({
      'type': 'offer',
      'sdp': sdp
    }), function() {
      console.log('set remote description success.');

      conn.createAnswer(function(desc) {
        conn.setLocalDescription(desc, function() {
          var lock1 = setInterval(function() {
            if(conn.iceGatheringState != 'complete') {
              console.log('loop ice gathering state');
              console.log(conn.iceGatheringState);
              return;
            }

            console.log('clear ice gathering state');
            clearInterval(lock1);

            var lock2 = setInterval(function() {
              if(conn.localDescription == null) {
                console.log('loop local description');
                return;
              }

              console.log('clear local description');
              clearInterval(lock2);

              console.log('  ice gathering state');
              console.log(conn.iceGatheringState);
              console.log('  lcoal description');
              console.log(conn.localDescription);
              console.log('  remote description');
              console.log(conn.remoteDescription);

              socket.emit('answer', {
                'sdp': conn.localDescription.sdp
              });
            }, 500);
          }, 500);
        });
      });
    }, function(err) {
      console.log('set remote description failure.');
      console.log(err);
    });
  }
});

