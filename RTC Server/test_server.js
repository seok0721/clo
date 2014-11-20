/*
 * Import Module
 */
var io = require('socket.io').listen(10000, { log: false });
var inspect = require('util').inspect;
var maria = require('./maria.js');
var redis = require('./redis.js');
var event = require('./event.js');
var status = require('./status.js');
var Log = require('./logger.js');
var inspect = require('util').inspect;
var chat = require('./chat.js');

/*
 * Static Constant
 */
var SUCCESS = 0;
var FAILURE = 1;
var TAG = 'server.js'

/*
 * Static Variables
 */
var broadcastPool = {}; // 키: 채널, 값: 소켓

/*
 * Static Function
 */
function socket_handler(socket) {
  socket.on('signUp', signUpHandler);
  socket.on('signIn', signInHandler); // 로그인
  socket.on('signOut', signOutHandler); // 로그아웃
  socket.on('createChannel', createChannelHandler); // 방 생성
  socket.on('offer', offerHandler); // SDP 전송
  socket.on('answer', answerHandler); // SDP 응답
  socket.on('destroy', removeChannelHandler); // 방 제거
  socket.on('enterChannel', enterChannelHandler); // 방 들어가기
  socket.on('leaveChannel', leaveChannelHandler); // 방 나오기
  socket.on('disconnect', disconnect_handler); // 서버 접속 종료
  socket.on('handshake', handshakeHandler); // 오퍼-앤서 핸드쉐이크
  socket.on('handshake2', handshake2Handler); // 오퍼-앤서 핸드쉐이크
  socket.on('chat', chatHandler);
  // socket.on('viewer_count', viewer_count_handler); // 시청자 카운트
  // socket.on('channel_list', channel_list_handler); // 채널 리스트

  /*
  function channel_list_handler(data) {
    redis.get_channel_list(function(err, data) {
      Log.i(TAG + '.channel_list_handler');

      if(err) {
        emitFailure('channel_list');
        return;
      }

      socket.emit('channel_list', {
        'ret': SUCCESS,
        'data': data
      });
    });
  }

  function viewer_count_handler(data) {
    console.log(broadcastPool[socket.channel]);
    broadcastPool[socket.channel].emit('viewer_count', {
      'ret': 1,
      'count': io.sockets.clients(socket.email).length
    });
  }
  */

  function chatHandler(data) {
    var channel = data.channel;
    var message = data.message;

    chat.sendMessage(socket, channel, message);
  }

  function handshakeHandler(data) {
    var channel = data.channel;
    var sdp = data.sdp;
    var broadcaster = broadcastPool[channel];

    if(!broadcaster) {
      console.log('방송중인 채널이 없습니다.');
      return;
    }

    socket.channel = channel;
    socket.join(channel);

    broadcaster.emit('handshake', {
      'viewer': socket.id,
      'channel': channel,
      'sdp': sdp
    });
  }

  function handshake2Handler(data) {
    var viewer = data.viewer;
    var channel = data.channel;
    var sdp = data.sdp;

    Log.i(TAG + '.handshake2Handler', sdp);

    socket.broadcast.to(viewer).emit('handshake2', {
      'channel': channel,
      'sdp': sdp
    });
  }

  function signUpHandler(data) {
    var email = data.email;
    var pwd = data.pwd;
    var name = data.name;
    var image = data.img;

    maria.existBroadcaster(email, pwd, function(err, exist) {
      if(err) {
        console.log('server.signUpHandler, error: ' + err);
        emitFailure('signUp', err);
        return;
      }

      if(!exist) {
        console.log('server.signUpHandler, error: ' + err);
        emitFailure('signUp', err);
        return;
      }

      maria.createBroadcaster(data, function(err, ret) {
        if(err) {
          console.log('server.signUpHandler, error: ' + err);
          emitFailure('signUp', err);
        } else {
          console.log('server.signUpHandler, result: ' + ret);
          emitSuccess('signUp');
        }
      });
    });
  }

  function signInHandler(data) {
    var email = data.email;
    var pwd = data.pwd;

    redis.exist_session(email, function(err, exist) {
      if(err) {
        console.log('server.signInHandler, error: ' + err);
        emitFailure('signIn', err);
        return;
      }

      if(exist) {
        console.log('server.signInHandler, error: Session already exists.');
        emitFailure('signIn', 'Session already exists.');
        return;
      }

      maria.read_broadcaster(email, pwd, function(err, data) {
        if(err) {
          console.log('server.signInHandler, error: ' + err);
          emitFailure('signIn', err);
          return;
        }

        if(!data) {
          console.log('server.signInHandler, error: Broadcaster info not exists.');
          emitFailure('signIn', 'Broadcaster info not exists.');
          return;
        }

        var email = data.email;
        var pwd   = data.pwd;
        var name  = data.name;
        var img   = data.img;

        Log.i(TAG, 'Sign in success, email: ' + data.email);

        socket.email = email;
        socket.name = name;
        socket.img = img;

        redis.create_session(socket.email);

        socket.emit('signIn', {
          'ret':   SUCCESS,
          'email': socket.email,
          'name':  socket.name,
          'img':   socket.img
        });
      });
    });
  }

  function signOutHandler(data) {
    Log.i(TAG + 'signOutHandler', inspect(data));

    redis.destroy_session(socket.email);
    socket.email = null;
  }

  function createChannelHandler(data) {
    var title = data.title;

    Log.i(TAG + '.createChannelHandler', socket.email);

    if(!socket.email) {
      Log.i(TAG + '.createChannelHandler', '로그인 후 사용하세요.');
      emitFailure('createChannel');
      return;
    }

    if(socket.title) {
      Log.i(TAG + '.createChannelHandler', '이미 방송중입니다.');
      Log.i(TAG + '.createChannelHandler', '계정: ' + socket.email);
      Log.i(TAG + '.createChannelHandler', '제목: ' + socket.title);
      return;
    }

    redis.create_channel(socket.email, socket.name, socket.img, title, function(err) {
      if(err) {
        Log.e(TAG + '.createChannelHandler', err);
        emitFailure('createChannel');
        return;
      }

      socket.title = title;
      socket.join(socket.email);

      Log.i(TAG + '.createChannelHandler', '채널을 성공적으로 만들었습니다.');
      Log.i(TAG + '.createChannelHandler', '계정: ' + socket.email);
      Log.i(TAG + '.createChannelHandler', '제목: ' + socket.title);

      // 2014-11-03 ADD
      broadcastPool[socket.email] = socket;
      console.log(broadcastPool[socket.email]);

      emitSuccess('createChannel');
    });
  }

  function offerHandler(data) {
    var sdp = data.sdp;

    Log.i(TAG + '.offerHandler', 'before offer, broadcaster: ' + socket.email);

    if(!socket.email) { // Only broadcaster
      Log.e(TAG + '.offerHandler', 'Do not send offer except broadcaster.');
      return;
    }

    socket.broadcast.to(socket.email).emit('offer', data);

    Log.i(TAG + '.offerHandler', 'offer, broadcaster: ' + socket.email);
    // Log.d(TAG + '.offerHandler', 'offer, sdp: ' + sdp);
  }

  function answerHandler(data) {
    var sdp = data.sdp;

    if(socket.email) { // Only viewer
      Log.e(TAG + '.answerHandler', 'Can not send answer except to viewer.');
      return;
    }

    socket.broadcast.to(socket.room).emit('answer', data);

    Log.i(TAG + '.answerHandler', 'answer, viewer: ' + socket.room);
  }

  function removeChannelHandler(data) {
    if(!socket.email) {
      Log.e(TAG + '.removeChannelHandler', 'Can not destroy room except broadcaster.');
      return;
    }

    if(!socket.title) {
      Log.e(TAG + '.removeChannelHandler', 'Can not destroy non exist room.');
      return;
    }

    redis.destroy_room(socket.email);

    Log.i(TAG + '.destory_handler', 'destroy, broadcaster: ' + socket.email + ', title: ' + socket.title);

    socket.leave(socket.email);
    socket.title = null;

    socket.broadcast.emit('destroy');
  }

  function enterChannelHandler(data) {
    var email = data.email;

    redis.exist_channel(email, function(err, exist) {
      if(err) {
        Log.e(TAG + '.exist_channel', err);
        emitFailure('enterChannel');
        return;
      }

      if(!exist) {
        Log.i(TAG + '.exist_channel', '채널이 없습니다.');
        emitFailure('enterChannel');
        return;
      }

      chat.enterChannel(socket, email);

      /*
      socket.channel = email;
      socket.join(socket.channel);
      socket.broadcast.to(socket.channel).emit('enterChannel');
      */

      Log.i(TAG + '.enterChannelHandler', '채널에 접속하였습니다.');
      Log.i(TAG + '.enterChannelHandler', '채널: ' + socket.channel);
    });  
  }

  function leaveChannelHandler(data) {
    var channel = data.channel;

    if(!socket.channels.hasOwnProperty(channel)) {
      return;
    }

    leaveChannel(socket, socket.room);

    /*
    if(!socket.room) {
      return;
    }

    Log.i(TAG + '.leaveChannelHandler', 'withdraw, room: ' + socket.room);

    socket.broadcast.to(socket.room).emit('leaveChannel');
    socket.leave(socket.room);
    socket.room = null;
    */
  }

  function disconnect_handler() {
    Log.i(TAG + '.disconnect_handler', 'disconnect');

    if(socket.email) {
      Log.i(TAG + '.disconnect_handler', 'destroy session');
      redis.destroy_session(socket.email);
      socket.broadcast.to(socket.email).emit('destroy');
    }

    chat.leaveTheChannelAll(socket);
    // socket.leave(socket.email);
  }

  function emitFailure(event_type, message) {
    socket.emit(event_type, {
      ret: FAILURE,
      'message': message
    });
  }

  function emitSuccess(event_type) {
    socket.emit(event_type, {
      ret: SUCCESS
    });
  }

  chat.initModule(socket);
}

/*
 * Main Routine
 */
io.sockets.on('connection', socket_handler);
