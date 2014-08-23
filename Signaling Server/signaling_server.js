var io = require('socket.io').listen(10080)

io.sockets.on('connection', function(socket) {
  socket.emit('news', { hello: 'world!' });
  socket.on('my other event', function(data) {
    console.log(data);
  });
});

/*
io.on('connection', function(socket) {
  io.emit('this', { will: 'be received by everyone' });

  socket.on('private message', function(from, msg) {
    console.log('I received a private message by ', from, ' saying ', msg);
  });

  socket.on('disconnect', function() {
    io.sockets.emit('user disconnected');
  });
});
*/

