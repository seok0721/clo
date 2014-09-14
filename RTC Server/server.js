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

  function leave_room(room) {
    socket.leave(room);

    log('Host leave room.');
  }

  function destroy_room() {
    if(socket.room) {
      log('Begin to destory room.');

      io.sockets.in(socket.room).emit('destroy', {
        'room': socket.room
      });
      socket.leave(socket.room);
      socket.room = null;

      log('End to destory room.');
    } else {
      log('Host not has room.');
    }
  }

  function destroy_session() {
    if(socket.sid) {
      log('Begin to destory session.');

      redis.destroy_session(socket.sid, function(err) {
        if(err) {
          log(err);
        } else {
          socket.sid = null;
        }
      });

      log('End to destory session.');
    } else {
      log('Host not has session.');
    }
  }

  // login

  function login_callback(data) {
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    redis.exist_session(email, login_exist_session_callback);
  }

  function login_exist_session_callback(err, exist) {
    if(err || !exist) {
      emit_failure('login');
      return;
    }

    maria.read_broadcaster(email, pwd, login_read_broadcaster_callback);
  }

  function login_read_broadcaster_callback(err, data) {
    if(err || !data) {
      emit_failure('login');
      return;
    }

    redis.create_session(email, login_create_session_callback);
  }

  function login_create_session_callback(err) {
    if(err) {
      emit_failure('login');
      return;
    }

    emit_success('login');
  }

  socket.on('login', function(data) {
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    redis.exist_session(email, function(err, ret) {
      if(err) {
        socket.emit('login', {
          'ret': FAILURE,
          'msg': msg
        });
        return;
      }

      switch(ret) {
      case status.SESSION_NOT_FOUND:
        maria.read_broadcaster(email, pwd, function(err, data) {
          if(err) {
            socket.emit('login', {
              'ret': FAILURE,
              'msg': err
            });
            return;
          }

          redis.create_session(email, function(err, ret, msg) {
            if(err) {
              socket.emit('login', {
                'ret': FAILURE,
                'msg': err
              });
              return;
            }

            switch(ret) {
            case SUCCESS:
              socket.sid = msg;
              socket.emit('login', {
                'ret': SUCCESS,
                'sid': msg
              });

              log('login, sid: ' + socket.sid);
              break;
            default:
              socket.sid = msg;
              socket.emit('login', {
                'ret': status.SESSION_ALREADY_EXIST
              });

              log('login, sid: ' + socket.sid);
              break;
            }
          });
        });
        break;
      case SUCCESS:
        socket.sid = msg;
      default:
        socket.emit('login', {
          'ret': ret,
          'msg': msg
        });
        break;
      }
    });
  });

  socket.on('logout', function(data) {
    destroy_room();
    destroy_session();
    
    log('logout, sid: ' + socket.sid);
  });

  socket.on('create', function(data) {
    var room = data.room;
    var clients = io.sockets.clients(room);

    if(!socket.sid) {
      log('Host is not broadcaster.');
      return;
    }

    if(socket.room) {
      socket.emit('create', {
        'ret': SUCCESS,
        'room': socket.room
      });
      log('Host already has room.');
      return;
    }

    if(clients.length > 0) {
      socket.emit('create', {
        'ret': status.ROOM_ALREADY_EXIST,
        'room': room
      });
      log('The other hosts already have room.');
      return;
    }

    socket.room = room;
    socket.join(room);
    socket.emit('create', {
      'ret': SUCCESS,
      'room': room
    });
    log('To create room: ' + room);
  });

  socket.on(event.B_DESTROY_ROOM, function(data) {
    var room = data.room;

    if(socket.room) {
      destroy_room();
    } else {
      leave_room(room);
    }
  });

  socket.on('join', function(data) {
    var room = data.room;
    var sdp = data.sdp;

    log(inspect(data));

    socket.broadcast.emit('join', sdp);

/*
    if(!io.rooms.hasOwnProperty('/' + room)) {
      log('"' + room + '" room not exists.');

      socket.emit('join', {
        'ret': status.ROOM_NOT_EXIST
      });
      return;
    }
*/
    if(socket.sid) {
      // TODO Reference, appspot, apprtc.py, 492 line.
      // TODO Broadcaster send sdp to viewer.
    } else {
      socket.join(room);

      log('To join room, ' + room);
    }
  });

  socket.on('withdraw', function(data) {
    var room = data.room;

    if(!socket.sid) {
      socket.leave(room);

      log('To join room, ' + room);
    }
  });

  socket.on('disconnect', function() {
    if(socket.sid) {
      destroy_session();
    }

    if(socket.room) {
      destroy_room();
    }

    log('Host disconnected.');
  });
});

