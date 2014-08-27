var io = require('socket.io').listen(10080);
var maria = require('./maria.js');
var redis = require('./redis.js');
var rooms = [];
var TAG = 'Signaling Server';

function log(message) {
  console.log(TAG + ': ' + message);
}

io.sockets.on('connection', function(socket) {

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

    console.log(11111111111111111);

    maria.read_broadcaster(data.email, function(err, data, meta) {
      console.log(2222222222222222);
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
  
  socket.on('my other event', function(data) {
    console.log(data);
  });
});

