var static = require('node-static');
var http = require('http');
var file = new(static.Server)();
var app = http.createServer(function(req, res) {
  file.serve(req, res);
}).listen(20130);

var io = require('socket.io').listen(app);

io.sockets.on('connection', function(socket) {
  function log() {
    var array = ['>>> Message from server: '];
    for(var i = 0; i < arguments.length; i++) {
      array.push(arguments[i]);
    }
    socket.emit('log', array);
  }

  socket.on('message', function(message) {
    // log('Got message:', message);
    console.log('message: ' + message);
    // socket.broadcast.emit('message', message);
    socket.emit('message', message);
  });

  socket.on('create or join', function(room) {
    // var numClients = io.sockets.clients(room).length;
    console.log(io.sockets.in(room));
    console.log(socket.join(room));
    console.log('================================');
    for(var i in io.sockets.sockets) {
 //     console.log(io.sockets.sockets[i]);
    }
  console.log(io.sockets.adapter);

 //   console.log(io.sockets.sockets[0].rooms);
    console.log('--------------------');
    console.log(socket.id);
  //  console.log(socket);

//    log('Room ' + room + ' has ' + numClients + ' client(s)');
//    log('Request to create or join room ' + room);
/*
    if(!io.sockets.rooms) {
      log('Room ' + room + ' has 0 client(s)');
      log('Request to create room ' + room);

      io.sockets.in(room).emit('join', room);

      socket.join(room);
      socket.emit('created', room);
    } else {
      io.socket.rooms.contains(room, function(ret) {
        io.sockets.in(room).emit('join', room);
        if(ret == true) {
          socket.join(room);
          socket.emit('joined', room);
        } else {
    log('Room ' + room + ' has ' + numClients + ' client(s)');
    log('Request to create or join room ' + room);

          socket.emit('full', room);
        }
      });
    }
*/
    socket.emit('emit(): client ' + socket.id + ' joined room ' + room);
    socket.broadcast.emit('broadcast(): client ' + socket.id + ' joined room ' + room);
  });
});

Array.prototype.contains = function(k, callback) {
  var self = this;
  return (function check(i) {
    if(i >= self.length) {
      return callback(false);
    }
    if(self[i] === k) {
      return callback(true);
    }

    return process.nextTick(check.bind(null, i + 1));
  }(0));
}

