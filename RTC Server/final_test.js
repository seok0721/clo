var io = require('socket.io').listen(10000, { log: false });
var maria = require('./maria.js');
var redis = require('./redis.js');

var SUCCESS = 0;
var FAILURE = 1;
var broadcasterMap = {}; // 키: 이메일, 값: 소켓

function emitSuccess(socket, eventType, data) {
  if(!data) {
    data = {};
  }

  data.ret = SUCCESS;
  socket.emit(eventType, data);
  console.log('소켓: ' + socket.id + ', 결과: 성공, 이벤트: '  + eventType);
}

function emitFailure(socket, eventType, message) {
  socket.emit(eventType, {
    'ret': FAILURE,
    'message': message
  });
  console.log('소켓: '  + socket.id + ', 결과: 실패, 이벤트: ' + eventType + ', 메시지: ' + message);
}

function removeChannel(socket) {
  redis.removeChannel(socket.email, function(err) {
    if(err) {
      return;
    }

    socket.broadcast.to(socket.email).emit('removeChannel', {
      'email': socket.email
    });
    socket.leave(socket.email);
    socket.title = null;

    delete broadcasterMap[socket.email];
  });
}

function leaveChannel(socket, email) {
  delete socket.channelMap[email];
  socket.leave(email);
  socket.broadcast.to(email).emit('leaveChannel', {
    'socketId': socket.id,
    'email': email
  });
}

function signOut(socket) {
  redis.removeSession(socket.email, function(err, ret) {
    socket.email = null;
    socket.name = null;
    socket.img = null;
  });
}

io.sockets.on('connection', function(socket) {
  socket.channelMap = {};

  socket.on('signUp', function(data) {
    var email = data.email;
    var pwd = data.pwd;
    var name = data.name;
    var image = data.img;

    maria.existBroadcaster(email, pwd, function(err, exist) {
      if(err) {
        emitFailure(socket, 'signUp', err);
        return;
      }

      if(exist) {
        emitFailure(socket, 'signUp', '방송자 정보가 이미 존재합니다.');
        return;
      }

      maria.createBroadcaster(data, function(err, ret) {
        if(err) {
          emitFailure(socket, 'signUp', err);
        } else {
          emitSuccess(socket, 'signUp');
        }
      });
    });
  });

  socket.on('signIn', function(data) {
    var email = data.email;
    var pwd = data.pwd;

    redis.existChannel(email, function(err, exist) {
      if(err) {
        emitFailure(socket, 'signIn', err);
        return;
      }

      if(exist) {
        emitFailure(socket, 'signIn', '채널이 이미 존재합니다.');
        return;
      }

      maria.readBroadcaster(email, pwd, function(err, data) {
        if(err) {
          emitFailure(socket, 'signIn', err);
          return;
        }

        if(!data) {
          emitFailure(socket, 'signIn', '방송자 정보가 존재하지 않습니다.');
          return;
        }

        socket.email = data.email;
        socket.name = data.name;
        socket.img = data.img;

        redis.createSession(socket.email);

        emitSuccess(socket, 'signIn', {
          'email': socket.email,
          'name': socket.name,
          'img': socket.img
        });
      });
    });
  });

  socket.on('signOut', function(data) {
    signOut(socket);
  });

  socket.on('createChannel', function(data) {
    var title = data.title;

    if(!socket.email) { // 로그인을 안했을 경우
      emitFailure(socket, 'createChannel', '로그인 후 사용하세요.');
      return;
    }

    if(socket.title) { // 이미 방송중인 경우
      emitFailure(socket, 'createChannel', '이미 방송중입니다.');
      return;
    }

    redis.createChannel(socket.email, socket.name, socket.img, title, function(err) {
      if(err) {
        emitFailure(socket, 'createChannel', err);
        return;
      }

      socket.title = title;
      socket.join(socket.email);

      broadcasterMap[socket.email] = socket;

      emitSuccess(socket, 'createChannel');
    });
  });

  socket.on('removeChannel', function(data) {
    if(!socket.email) { // 로그인 하지 않은 경우
      emitFailure(socket, 'removeChannel', '로그인 하지 않았습니다.');
      return;
    }

    if(!socket.title) { // 방송을 시작하지 않은 경우
      emitFailure(socket, 'removeChannel', '방송중이 아닙니다.');
      return;
    }

    removeChannel(socket);
    emitSuccess(socket, 'removeChannel');
  });

  socket.on('enterChannel', function(data) {
    var email = data.email;

    redis.existChannel(email, function(err, exist) {
      if(err) {
        emitFailure(socket, 'enterChannel', err);
        return;
      }

      if(!exist) {
        emitFailure(socket, 'enterChannel', '채널이 없습니다.');
        return;
      }

      socket.channelMap[email] = email;
      socket.join(email);
      socket.broadcast.to(email).emit('enterChannel', {
        'socketId': socket.id,
        'email': email
      });

      emitSuccess(socket, 'enterChannel', {
        'email': email
      });
    });  
  });

  socket.on('leaveChannel', function(data) {
    var email = data.email;

    if(socket.channelMap.hasOwnProperty(email)) {
      leaveChannel(socket, socket.channelMap[email]);
    }
  });

  socket.on('chat', function(data) {
    var email = data.email;
    var message = data.message;

    socket.broadcast.to(email).emit('chat', {
      'socketId': socket.id,
      'email': email,
      'message': message
    });
  });

  socket.on('offer', function(data) {
    var email = data.email;
    var sdp = data.sdp;
    var broadcaster = broadcasterMap[email];

    if(!broadcaster) {
      emitFailure(socket, 'offer', '방송중인 채널이 없습니다.');
      return;
    }

    socket.channelMap[email] = email;
    socket.join(email);

    broadcaster.emit('offer', {
      'socketId': socket.id,
      'email': email,
      'sdp': sdp
    });

    emitSuccess(socket, 'offer');
  });

  socket.on('answer', function(data) {
    var socketId = data.socketId;
    var email = data.email;
    var sdp = data.sdp;

    socket.broadcast.to(socketId).emit('answer', {
      'email': email,
      'sdp': sdp
    });

    emitSuccess(socket, 'answer');
  });

  socket.on('disconnect', function(data) {
    if(socket.email) { // 방송자인 경우
      if(socket.title) { // 방송 하는 경우
        removeChannel(socket); // 방송 종료
      }

      signOut(socket); // 방송자 로그아웃
    } else { // 시청자인 경우
      for(var email in socket.channelMap) { // 시청자가 시청중인 모든 경우
        leaveChannel(socket, email); // 시청자 채널 나감
      }
    }

    console.log('소켓: ' + socket.id + ', 결과: 성공, 이벤트: disconnect');
  });

  console.log('소켓: ' + socket.id + ', 결과: 성공, 이벤트: connect');
});
