var io = require('socket.io').listen(10080);
var inspect = require('util').inspect;
var maria = require('./maria.js');
var redis = require('./redis.js');
var event = require('./event.js');
var status = require('./status.js');
var rooms = [];

function log(message) {
  console.log('Signaling Server: ' + message);
}

io.sockets.on('connection', function(socket) {
  socket.on(event.B_LOGIN, function(data) {
    var email = data.email;
    var passwd = data.passwd;

    log(inspect(data));

    redis.exist_session(email, passwd, function(reply, message) {
      log(reply);
      log(message);

      switch(reply) {
      case status.OK:
        socket.emit(event.B_LOGIN, {
          'reply': reply,
          'session': message
        });
        break;
      case status.SESSION_NOT_FOUND:
        maria.read_broadcaster(email, passwd, function(err, data, meta) {
          if(err) {
            log(err);

            // TODO socket.emit()
          }
        });
        break;
      default:
        log('login failure.');

        socket.emit(event.B_LOGIN, {
          'reply': reply,
          'message': message
        });
        break;
      }
    });
  });

  socket.on(event.B_LOGOUT, function(data) {
    
  });

/*
  socket.on('LOGIN', function(data) {
    log(data);

    maria.read_broadcaster(data.email, function(err, data) {
      if(err) {
      } else {
      }
    });
  });

  socket.on('CREATE', function(data) {

    redis.session_create('aaaa', function(code, message) {
      console.log(code + ': ' + message);
    });

    var room = data.room;

    if(rooms.indexOf(room) > -1) {
      console.log(TAG + ': Already room exists.');

      socket.emit('CREATE', {code: 500, message: "Already room exists."});
      return;
    }

    maria.read_broadcaster(data.email, function(err, data, meta) {
      if(err) {
        console.log(TAG + ': ' + err);

        socket.emit('CREATE', {code: 500});
      } else if(data) {
        console.log(TAG + ': ' + rooms);
        console.log(TAG + ': ' + room);

        rooms.push(room);

        socket.emit('CREATE', {code: 200});
      } else {
        console.log(TAG + ': ' + meta);
      }
    });
  });

  socket.on('DESTROY', function(data) {
    console.log(data);

    socket.emit('DESTROY', {code: 200});
  });

  socket.on('disconnect', function(data) {
    console.log(data);

    io.sockets.emit('user disconnected');
  });
*/
});

