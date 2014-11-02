/*
 * Import Module
 */
var io = require('socket.io').listen(10080);
var inspect = require('util').inspect;
var maria = require('./maria.js');
var redis = require('./redis.js');
var event = require('./event.js');
var status = require('./status.js');
var Log = require('./logger.js');
var inspect = require('util').inspect;

/*
 * Static Constant
 */
var SUCCESS = 0;
var FAILURE = 1;
var TAG = 'server.js'

/*
 * Static Function
 */
function socket_handler(socket) {
  socket.on('signup', signup_handler);
  socket.on('signin', signin_handler); // 로그인
  socket.on('signout', signout_handler); // 로그아웃
  socket.on('create', create_channel_handler); // 방 생성
  socket.on('offer', offer_handler); // SDP 전송
  socket.on('answer', answer_handler); // SDP 응답
  socket.on('destroy', destroy_handler); // 방 제거
  socket.on('join', join_channel_handler); // 방 들어가기
  socket.on('withdraw', withdraw_channel_handler); // 방 나오기
  socket.on('disconnect', disconnect_handler); // 서버 접속 종료

  function signup_handler(data) {
    var email = data.email;
    var pwd = data.pwd;
    var name = data.name;
    var image = data.image;

    maria.exist_broadcaster(email, pwd, function(err, exist) {
      if(err) {
        Log.e(TAG + '.signup_handler', err);
        emit_failure('signup');
        return;
      }

      if(exist) {
        Log.i(TAG + '.signup_handler', 'Broadcaster\' info already exists.');
        emit_failure('signup');
        return;
      }

      maria.create_broadcaster(data, function(err, ret) {
        if(err) {
          Log.e(TAG + '.signup_handler', err);
          emit_failure('signup');
        } else {
          Log.i(TAG + '.signup_handler', ret);
          emit_success('signup');
        }
      });
    });
  }

  function signin_handler(data) {
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    redis.exist_session(email, function(err, exist) {
      if(err) {
        Log.e(TAG + '.signin_handler', err);
        emit_failure('signin');
        return;
      }

      if(exist) {
        Log.i(TAG + '.signin_handler', 'Session already exists.');
        emit_failure('signin');
        return;
      }

      maria.read_broadcaster(email, pwd, function(err, data) {
        var email = data.email;
        var pwd   = data.pwd;
        var name  = data.name;
        var img   = data.img;

        if(err) {
          Log.e(TAG + '.signin_handler', err);
          emit_failure('signin');
          return;
        }

        if(!data) {
          Log.i(TAG, 'Broadcaster info not exists.');
          emit_failure('signin');
          return;
        }

        Log.i(TAG, 'Sign in success, email: ' + data.email);

        socket.email = email;
        socket.name  = name;
        socket.img   = img;

        redis.create_session(socket.email);

        socket.emit('signin', {
          'ret':   SUCCESS,
          'email': socket.email,
          'name':  socket.name,
          'img':   socket.img
        });
      });
    });
  }

  function signout_handler(data) {
    Log.i(TAG + 'signout_handler', inspect(data));

    redis.destroy_session(socket.email);
    socket.email = null;
  }

  function create_channel_handler(data) {
    var title = data.title;

    Log.i(TAG + '.create_channel_handler', socket.email);

    if(!socket.email) {
      Log.i(TAG + '.create_channel_handler', '로그인 후 사용하세요.');
      emit_failure('create');
      return;
    }

    if(socket.title) {
      Log.i(TAG + '.create_channel_handler', '이미 방송중입니다.');
      Log.i(TAG + '.create_channel_handler', '계정: ' + socket.email);
      Log.i(TAG + '.create_channel_handler', '제목: ' + socket.title);
      return;
    }

    redis.create_channel(socket.email, title, function(err) {
      if(err) {
        Log.e(TAG + '.create_channel_handler', err);
        emit_failure('create');
        return;
      }

      socket.title = title;
      socket.join(socket.email);

      Log.i(TAG + '.create_channel_handler', '채널을 성공적으로 만들었습니다.');
      Log.i(TAG + '.create_channel_handler', '계정: ' + socket.email);
      Log.i(TAG + '.create_channel_handler', '제목: ' + socket.title);

      emit_success('create');
    });
  }

  function offer_handler(data) {
    var sdp = data.sdp;

    Log.i(TAG + '.offer_handler', 'before offer, broadcaster: ' + socket.email);

    if(!socket.email) { // Only broadcaster
      Log.e(TAG + '.offer_handler', 'Do not send offer except broadcaster.');
      return;
    }

    socket.broadcast.to(socket.email).emit('offer', data);

    Log.i(TAG + '.offer_handler', 'offer, broadcaster: ' + socket.email);
    // Log.d(TAG + '.offer_handler', 'offer, sdp: ' + sdp);
  }

  function answer_handler(data) {
    var sdp = data.sdp;

    if(socket.email) { // Only viewer
      Log.e(TAG + '.answer_handler', 'Can not send answer except to viewer.');
      return;
    }

    socket.broadcast.to(socket.room).emit('answer', data);

    Log.i(TAG + '.answer_handler', 'answer, viewer: ' + socket.room);
  }

  function destroy_handler(data) {
    if(!socket.email) {
      Log.e(TAG + '.destroy_handler', 'Can not destroy room except broadcaster.');
      return;
    }

    if(!socket.title) {
      Log.e(TAG + '.destroy_handler', 'Can not destroy non exist room.');
      return;
    }

    redis.destroy_room(socket.email);

    Log.i(TAG + '.destory_handler', 'destroy, broadcaster: ' + socket.email + ', title: ' + socket.title);

    socket.leave(socket.email);
    socket.title = null;

    socket.broadcast.emit('destroy');
  }

  function join_channel_handler(data) {
    var email = data.email;

    redis.exist_channel(email, function(err, exist) {
      if(err) {
        Log.e(TAG + '.exist_channel', err);
        emit_failure('join');
        return;
      }

      if(!exist) {
        Log.i(TAG + '.exist_channel', '채널이 없습니다.');
        emit_failure('join');
        return;
      }

      socket.channel = email;
      socket.join(socket.channel);
      socket.broadcast.to(socket.channel).emit('join');

      Log.i(TAG + '.join_channel_handler', '채널에 접속하였습니다.');
      Log.i(TAG + '.join_channel_handler', '채널: ' + socket.channel);
    });  
  }

  function withdraw_channel_handler(data) {
    if(!socket.room) {
      return;
    }

    Log.i(TAG + '.withdraw_channel_handler', 'withdraw, room: ' + socket.room);

    socket.broadcast.to(socket.room).emit('withdraw');
    socket.leave(socket.room);
    socket.room = null;
  }

  function disconnect_handler() {
    Log.i(TAG + '.disconnect_handler', 'disconnect');

    if(socket.email) {
      Log.i(TAG + '.disconnect_handler', 'destroy session');
      redis.destroy_session(socket.email);
      socket.broadcast.to(socket.email).emit('destroy');
    }

    socket.leave(socket.email);
  }

  function emit_failure(event_type) {
    socket.emit(event_type, {
      ret: FAILURE
    });
  }

  function emit_success(event_type) {
    socket.emit(event_type, {
      ret: SUCCESS
    });
  }
}

/*
 * Main Routine
 */
io.sockets.on('connection', socket_handler);
