/**
 * Socket
 *
 *   Events
 *     connect. Fired upon connecting.
 *     error. Fired upon a connection error
 *       Parameters:
 *         Object error data
 *     disconnect. Fired upon a disconnection.
 *     reconnect. Fired upon a successful reconnection.
 *       Parameters:
 *         Number reconnection attempt number
 *     reconnect_attempt. Fired upon an attempt to reconnect.
 *     reconnecting. Fired upon an attempt to reconnect.
 *       Parameters:
 *         Number reconnection attempt number
 *     reconnect_error. Fired upon a reconnection attempt error.
 *       Parameters:
 *         Object error object
 *     reconnect_failed. Fired when couldn’t reconnect within reconnectionAttempts
 */

$(function() {
  var event = {
    B_LOGIN : 111,
    B_LOGOUT : 112,
    B_CREATE_ROOM : 121,
    B_DESTROY_ROOM : 122,
    V_JOIN_ROOM : 221,
    V_WITHDRAW_ROOM : 222
  };

  var client;
  var remoteVideo = $('#vidRemote')[0];
  var btnView = $('#btnView');
  var btnConnect = $('#btnConnect');
  var btnInit = $('#btnInit');
  var txtRoomId = $('#txtRoomId');
  var servers = {
    'iceServers' : [{
      'url' : 'stun:stun.l.google.com:19302'
    }]
  };
  var localPeerConnection = new webkitRTCPeerConnection(servers);
  localPeerConnection.onicecandidate = gotIceCandidate;
  localPeerConnection.onaddstream = gotRemoteStream;

  remoteVideo.addEventListener('loadedmetadata', function() {
    console.log('source: ' + this.currentSrc);
    console.log('width: ' + this.videoWidth);
    console.log('height: ' + this.videoHeight);
  });

  function gotRemoteStream(event) {
    remoteVideo.src = URL.createObjectURL(event.stream);
  }

  function gotIceCandidate(e) {
    if (localPeerConnection.iceGatheringState == 'complete') {
      client.emit(event.V_JOIN_ROOM, {
        'room' : txtRoomId.val(),
        'sdp' : localPeerConnection.localDescription.sdp
      });
      return;
    }

    console.log(e);

    if (e.candidate) {
      localPeerConnection.addIceCandidate(new RTCIceCandidate(e.candidate));
      //console.log(e.candidate.candidate);
    }
  }

  function init() {

  }

  function connect() {

  }

  // 1. The event bind.
  btnView.click(function() {
  });

  btnConnect.click(function() {
    if (client != null && client.socket.open) {
      console.log('already socket open.');
      return;
    }

    client = io.connect('http://211.189.19.82:10080/');
    console.log(client.socket.open);
    client.on('connect', function() {
      console.log('connected.');
      localPeerConnection.createOffer(function(desc) {
        localPeerConnection.setLocalDescription(desc);
      });
    });

    client.on('close', function() {
      console.log(1);
    }).on('connecting', function() {
      console.log(2);
    }).on('connect_failed', function() {
      console.log(3);
    }).on('message', function() {
      console.log(4);
    }).on('close', function() {
      console.log(5);
    }).on('reconnect', function() {
      console.log(6);
    }).on('reconnecting', function() {
      console.log(7);
    }).on('reconnect_failed', function() {
      console.log(8);
    });

    client.on('disconnect', function() {
      console.log('disconnected');
    });
    client.on('error', function(err) {
      console.log('error, cause: ' + err);
    });
  });

  /*
   btnInit.click(function() {
   init();
   });
   */
});
