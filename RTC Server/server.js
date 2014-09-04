var io = require('socket.io').listen(10080);
var inspect = require('util').inspect;
var maria = require('./maria.js');
var redis = require('./redis.js');
var event = require('./event.js');
var status = require('./status.js');

function log(msg) {
  console.log('Signaling Server: ' + msg);
}

io.sockets.on('connection', function(socket) {
  function leaveRoom(room) {
    socket.leave(room);

    log('Host leave room.');
  }

  function destroyRoom() {
    if(socket.room) {
      log('Begin to destory room.');

      io.sockets.in(socket.room).emit(event.B_DESTROY_ROOM, {
        'room': socket.room
      });
      socket.leave(socket.room);
      socket.room = null;

      log('End to destory room.');
    } else {
      log('Host not has room.');
    }
  }

  function destroySession() {
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

  socket.on(event.B_LOGIN, function(data) {
    var sid = data.sid; // Session key.
    var email = data.email; // Broadcaster email.
    var pwd = data.pwd; // Broadcaster md5 hash upper case string password.

    redis.exist_session(sid, function(err, ret, msg) {
      if(err) {
        socket.emit(event.B_LOGIN, {
          'ret': status.REDIS_ERROR,
          'msg': msg
        });
        return;
      }

      switch(ret) {
      case status.SESSION_NOT_FOUND:
        maria.read_broadcaster(email, pwd, function(err, data) {
          if(err) {
            socket.emit(event.B_LOGIN, {
              'ret': status.MARIA_ERROR,
              'msg': err
            });
            return;
          }

          redis.create_session(email, function(err, ret, msg) {
            if(err) {
              socket.emit(event.B_LOGIN, {
                'ret': status.REDIS_ERROR,
                'msg': err
              });
              return;
            }

            switch(ret) {
            case status.OK:
              socket.sid = msg;
              socket.emit(event.B_LOGIN, {
                'ret': status.OK,
                'sid': msg
              });

              log('login, sid: ' + socket.sid);
              break;
            default:
              socket.sid = msg;
              socket.emit(event.B_LOGIN, {
                'ret': status.SESSION_ALREADY_EXIST
              });

              log('login, sid: ' + socket.sid);
              break;
            }
          });
        });
        break;
      case status.OK:
        socket.sid = msg;
      default:
        socket.emit(event.B_LOGIN, {
          'ret': ret,
          'msg': msg
        });
        break;
      }
    });
  });

  socket.on(event.B_LOGOUT, function(data) {
    destroyRoom();
    destroySession();
    
    log('logout, sid: ' + socket.sid);
  });

  socket.on(event.B_CREATE_ROOM, function(data) {
    var room = data.room;
    var clients = io.sockets.clients(room);

    if(!socket.sid) {
      log('Host is not broadcaster.');
      return;
    }

    if(socket.room) {
      socket.emit(event.B_CREATE_ROOM, {
        'ret': status.OK,
        'room': socket.room
      });
      log('Host already has room.');
      return;
    }

    if(clients.length > 0) {
      socket.emit(event.B_CREATE_ROOM, {
        'ret': status.ROOM_ALREADY_EXIST,
        'room': room
      });
      log('The other hosts already have room.');
      return;
    }

    socket.room = room;
    socket.join(room);
    socket.emit(event.B_CREATE_ROOM, {
      'ret': status.OK,
      'room': room
    });
    log('To create room: ' + room);
  });

  socket.on(event.B_DESTROY_ROOM, function(data) {
    var room = data.room;

    if(socket.room) {
      destroyRoom();
    } else {
      leaveRoom(room);
    }
  });

  socket.on(event.V_JOIN_ROOM, function(data) {
    var room = data.room;
    var sdp = data.sdp;

    log(inspect(data));

    socket.broadcast.emit(event.V_JOIN_ROOM, sdp);

/*
    if(!io.rooms.hasOwnProperty('/' + room)) {
      log('"' + room + '" room not exists.');

      socket.emit(event.V_JOIN_ROOM, {
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

  socket.on(event.V_WITHDRAW_ROOM, function(data) {
    var room = data.room;

    if(!socket.sid) {
      socket.leave(room);

      log('To join room, ' + room);
    }
  });

  socket.on('disconnect', function() {
    if(socket.sid) {
      destroySession();
    }

    if(socket.room) {
      destroyRoom();
    }

    log('Host disconnected.');
  });
});

