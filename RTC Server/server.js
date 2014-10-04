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
  socket.on('login', login_handler);
  socket.on('logout', logout_handler);
  socket.on('create', create_handler);
  socket.on('offer', offer_handler);
  socket.on('answer', answer_handler);
  socket.on('destroy', destroy_handler);
  socket.on('join', join_handler);
  socket.on('withdraw', withdraw_handler);
  socket.on('disconnect', disconnect_handler);

  function login_handler(data) {
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    Log.d(TAG + 'login_handler', 'before login, data: ' + inspect(data));

    redis.exist_session(email, function(err, exist) {
      if(err || exist) {
        Log.i(TAG + 'login_handler', 'login failure, data: ' + inspect(data));
        emit_failure('login');
        return;
      }

      maria.read_broadcaster(email, pwd, function(err, meta) {
        if(err || meta.numRows == 0) {
          Log.i(TAG + 'login_handler', 'login failure, data: ' + inspect(data));
          emit_failure('login');
          return;
        }

        socket.email = email;
        redis.create_session(email);
        emit_success('login');

        Log.i(TAG + 'login_handler', 'login success, data: ' + inspect(data));
      });
    });
  }

  function logout_handler(data) {
    Log.i(TAG + 'logout_handler', inspect(data));

    redis.destroy_session(socket.email);
    socket.email = null;
  }

  function create_handler(data) {
    if(!socket.email) {
      Log.i(TAG + ' create_handler', inspect(data));
      emit_failure('create');
      return;
    }

    if(socket.title) {
      Log.i(TAG + ' create_handler', 'room is already created.');
      return;
    }

    redis.create_room(socket.email, data.title, function(err) {
      if(err) {
        Log.e(TAG + ' create_handler', err);
        emit_failure('create');
        return;
      }

      socket.join(socket.email);
      socket.title = data.title;
      emit_success('create');

      Log.i(TAG + ' create_handler', 'to create room success.');
    });
  }

  function offer_handler(data) {
    var sdp = data.sdp;

    Log.i(TAG + ' offer_handler', 'before offer, broadcaster: ' + socket.email);

    // FIXME Final Exam, uncomment this block.
    /*
    if(!socket.email) { // Only broadcaster
      Log.e(TAG + ' offer_handler', 'Do not send offer except broadcaster.');
      return;
    }
    */

    socket.broadcast.to(socket.email).emit('offer', data);

    Log.i(TAG + ' offer_handler', 'offer, broadcaster: ' + socket.email);
    // Log.d(TAG + ' offer_handler', 'offer, sdp: ' + sdp);
  }

  function answer_handler(data) {
    var sdp = data.sdp;

    if(socket.email) { // Only viewer
      Log.e(TAG + ' answer_handler', 'Can not send answer except viewer.');
      return;
    }

    socket.broadcast.to(socket.room).emit('answer', data);

    Log.i(TAG + ' answer_handler', 'answer, viewer: ' + socket.room);
  }

  function destroy_handler(data) {
    if(!socket.email) {
      Log.e(TAG + ' destroy_handler', 'Can not destroy room except broadcaster.');
      return;
    }

    if(!socket.title) {
      Log.e(TAG + ' destroy_handler', 'Can not destroy non exist room.');
      return;
    }

    redis.destroy_room(socket.email);

    Log.i(TAG + ' destory_handler', 'destroy, broadcaster: ' + socket.email + ', title: ' + socket.title);

    socket.leave(socket.email);
    socket.title = null;

    socket.broadcast.emit('destroy');
  }

  function join_handler(data) {
    var room = data.room;

    redis.exist_room(room, function(err, exist) {
      if(err || !exist) {
        
        emit_failure('join');
        return;
      }

      socket.room = room;
      socket.join(room);
      socket.broadcast.to(room).emit('join', data);

      Log.i(TAG + ' join_handler', 'room: ' + JSON.stringify(data));
    });  
  }

  function withdraw_handler(data) {
    if(!socket.room) {
      return;
    }

    Log.i(TAG + ' withdraw_handler', 'withdraw, room: ' + socket.room);

    socket.broadcast.to(socket.room).emit('withdraw');
    socket.leave(socket.room);
    socket.room = null;
  }

  function disconnect_handler() {
    Log.i(TAG + ' disconnect_handler', 'disconnect');

    if(socket.email) {
      Log.i(TAG + ' disconnect_handler', 'destroy session');
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
