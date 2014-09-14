/*
 * Global Variables
 */
var io = require('socket.io').listen(10080);
var inspect = require('util').inspect;
var maria = require('./maria.js');
var redis = require('./redis.js');
var event = require('./event.js');
var status = require('./status.js');

var SUCCESS = 0;
var FAILURE = 1;

/*
 * Global Function
 */
function log(msg) {
  console.log('Signaling Server: ' + msg);
}

/*
 * Main Routine
 */
io.sockets.on('connection', function(socket) {
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

  socket.on('login', function(data) {
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    redis.exist_session(email, function(err, exist) {
      if(err || exist) {
        emit_failure('login');
        return;
      }

      maria.read_broadcaster(email, pwd, function(err, meta) {
        if(err || meta.numRows == 0) {
          emit_failure('login');
          return;
        }

        socket.email = email;
        redis.create_session(email);
        emit_success('login');

        log('login, broadcaster: ' + socket.email);
      });
    });
  });

  socket.on('logout', function(data) {
    log('logout, broadcaster: ' + socket.email);

    redis.destroy_session(socket.email);
    socket.email = null;
  });

  socket.on('create', function(data) {
    if(!socket.email) {
      emit_failure('create');
      return;
    }

    if(socket.title) {
      return;
    }

    redis.create_room(socket.email, data.title, function(err) {
      if(err) {
        emit_failure('create');
        return;
      }

      socket.join(socket.email);
      socket.title = data.title;

      emit_success('create');

      log('create, broadcaster: ' + socket.email + ', title: ' + socket.title);
    });
  });

  socket.on('offer', function(sdp) {
    if(socket.email) {
      socket.broadcast.to(socket.email).emit(sdp);

      log('offer, broadcaster: ' + socket.email);
    }
  });

/*
  socket.on('answer', function(sdp) {
    if(!socket.email) {
      io.to(socket.room).emit(sdp);
    }
  });
*/

  socket.on('destroy', function(data) {
    if(!socket.email) {
      return;
    }

    if(!socket.title) {
      return;
    }

    redis.destroy_room(socket.email);

    log('destroy, broadcaster: ' + socket.email + ', title: ' + socket.title);

    socket.leave(socket.email);
    socket.title = null;

    socket.broadcast.emit('destroy');
  });

  socket.on('join', function(data) {
    var room = data.room;

    redis.exist_room(room, function(err, exist) {
      if(err || !exist) {
        emit_failure('join');
        return;
      }

      socket.room = room;
      socket.join(room);
      socket.broadcast.to(room).emit('join');

      log('join, room: ' + room);
    });  
  });

  socket.on('withdraw', function() {
    if(!socket.room) {
      return;
    }

    log('withdraw, room: ' + socket.room);

    socket.broadcast.to(socket.room).emit('withdraw');
    socket.leave(socket.room);
    socket.room = null;
  });

  socket.on('disconnect', function() {
    log('disconnect');
  });
});

