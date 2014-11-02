/*
 * Import Module
 */
var io = require('socket.io').listen(10080);
var inspect = require('util').inspect;
var room = 'rtc';

io.sockets.on('connection', function(socket) {
  socket.on('offer', function(data) {
    console.log('offer');
    console.log(inspect(data));
    socket.broadcast.emit('offer', data);
  });

  socket.on('answer', function(data) {
    console.log('answer');
    console.log(inspect(data));
    socket.broadcast.emit('answer', data);
  });
});

