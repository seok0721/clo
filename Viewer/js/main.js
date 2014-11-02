<<<<<<< HEAD
var url = 'http://211.189.19.82:10080/';
var socket;

function connect() {
	socket = io.connect(url);
	socket.on('connect', function() {
		console.log('connected');
	});
}
=======
/*
 * Global Variables
 */
var RTCPeerConnection = window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
var channelPool = [];
var divScreen = $('#divScreen');

/*
 * Global Constant
 */
var TAG = 'main.js';
var NODE_SERVER = 'http://211.189.20.193:10080';
var ICE_SERVERS = {
  'iceServers' : [{
    'url': 'stun:stun.l.google.com:19302'
  }/*, {
    'url': 'turn:clo@211.189.20.193',
    'credential': 'clo'
  }*/]
};

/*
 * Global Function Interface
 */
function pluginAdapter(email){}
function unplugAdapter(email){}
function createSocket(email){}
function createConnection(email){}

/*
 * Global Function Implementation
 */
function pluginAdapter(email) {
  var adapter;

  for(i = 0; i < channelPool.length; i++) {
    if(channelPool[i].email == email) {
      Log.i(TAG + ' pluginAdapter', 'Adapter already exists.');
      return channelPool[i];
    }
  }

  adapter = { 'email': email };
  adapter.connection = createConnection(email);
  adapter.socket = createSocket(email);
  adapter.socket.connection = adapter.connection;

  channelPool.push(adapter);

  Log.i(TAG + ' pluginAdapter', 'Adapter is created.');

  return adapter;
}

function unplugAdapter(email) {
  // TODO release socket & connection

  for(i = 0; i < channelPool.length; i++) {
    if(channelPool[i].email == email) {
      Log.i(TAG + ' unplugAdapter', 'Unplug adapter, email: ' + email);
      channelPool.splice(i);
      return;
    }
  }
}

function createSocket(email) {
  var socket = io.connect(NODE_SERVER);
  // socket.connection = null;

  /*
   * Default Socket Event Bind
   */
  socket.on('connect', connectHandler);
  socket.on('close', closeHandler);
  socket.on('connecting', connectingHandler);
  socket.on('connect_failed', connect_failedHandler);
  socket.on('message', messageHandler);
  socket.on('close', closeHandler);
  socket.on('reconnect', reconnectHandler);
  socket.on('reconnecting', reconnectingHandler);
  socket.on('reconnect_failed', reconnect_failedHandler);
  socket.on('disconnect', disconnectHandler);

  /*
   * Custom Socket Event Bind
   */
  socket.on('offer', offerHandler);

  /*
   * Handle Socket
   */
  Log.i(TAG + ' createSocket', 'join room: ' + email);

  socket.emit('join', {
    'room': email
  });

  function offerHandler(data) {
    if(socket.connection == null) {
      Log.i(TAG + ' offerHandler', 'Connection is null.');
      return;
    }

    if(socket.connection.remoteDescription != null) {
      Log.i(TAG + ' offerHandler', 'RemoteDescription already exists.');
      return;
    }

    var sdp = data.sdp;
    var session = new RTCSessionDescription({
      'type': 'offer',
      'sdp': sdp
    });

    console.log(session);

    socket.connection.setRemoteDescription(session, function() {
      Log.i(TAG + ' offerHandler', 'success set remote description.');
      socket.connection.createAnswer(answerHandler);
      divScreen.append(socket.connection.video);
    }, function(err) {
      Log.e(TAG + ' offerHandler', err);
    });
  }

  function answerHandler(desc) {
    socket.connection.setLocalDescription(desc);

    var answerLoop = setInterval(function() {
      if(!socket.socket.open) {
        clearInterval(answerLoop);
      }

      Log.i(TAG + ' answerHandler', 'send answer: ' + socket.connection.localDescription.sdp);

      socket.emit('answer', {
        'sdp': socket.connection.localDescription.sdp
      });
    }, 5000);
  }

  function connectHandler() {
    Log.d(TAG + ' connectHandler', 'is called');
  }

  function closeHandler() {
    Log.d(TAG + ' closeHandler', 'is called');
  }

  function connectingHandler() {
    Log.d(TAG + ' connectingHandler', 'is called');
  }

  function connect_failedHandler() {
    Log.d(TAG + ' connect_failedHandler', 'is called');
  }

  function messageHandler() {
    Log.d(TAG + ' messageHandler', 'is called');
  }

  function closeHandler() {
    Log.d(TAG + ' closeHandler', 'is called');
  }

  function reconnectHandler() {
    Log.d(TAG + ' reconnectHandler', 'is called');
  }

  function reconnectingHandler() {
    Log.d(TAG + ' reconnectingHandler', 'is called');
  }

  function reconnect_failedHandler() {
    Log.d(TAG + ' reconnect_failedHandler', 'is called');
  }

  function disconnectHandler() {
    Log.d(TAG + ' disconnectHandler', 'is called');
  }

  return socket;
}

function createConnection(email) {

  /*
   * Member Variable
   */
  var connection;

  /*
   * Execute Routine
   */
  connection = new RTCPeerConnection(ICE_SERVERS, {
/*
    'optional': [{
      'DtlsSrtpKeyAgreement': true,
    }],
    mandatory: [{
      'OfferToReceiveAudio': true
    }, {
      'OfferToReceiveVideo': true
    }]
*/
  });
  connection.email = email;
  connection.video = $('<video autoplay="true"></video>');
  connection.onicecandidate = gotIceCandidate;
  connection.onaddstream = gotRemoteStream;
  connection.video[0].addEventListener('loadedmetadata', function() {
    console.log('source: ' + this.currentSrc);
    console.log('width: ' + this.videoWidth);
    console.log('height: ' + this.videoHeight);
  });

  Log.i(TAG + ' createConnection', 'New connection is created.');

  /*
   * Member Function
   */
  function gotIceCandidate(e) {
    Log.i(TAG + ' gotIceCandidate', e);
    console.log(e);

    if(e.candidate) {
      connection.addIceCandidate(new RTCIceCandidate(e.candidate));
    }
  }

  function gotRemoteStream(e) {
    remoteVideo.src = URL.createObjectURL(e.stream);
  }

  return connection;
}

/*
 * Main Routine
 */
$(function() {

  /*
   * Local Variable
   */
  var btnConnect = $('#btnConnect');
  var txtRoomId = $('#txtRoomId');

  btnConnect.click(connectHandler);

  function connectHandler() {
    var email = txtRoomId.val();
    var connection;

    if(!email || email.trim().length == 0) {
      Log.i(TAG + ' connectHandler', 'Room Id is empty.');
      return;
    }

    pluginAdapter(email);
  }
});
>>>>>>> master
