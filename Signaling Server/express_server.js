var app = require('express').createServer();
var io = require('socket.io')(app);

app.listen(10080);

io.on('connection', function (socket) {
  socket.emit('news', { hello: 'world' });
  socket.on('my other event', function (data) {
    console.log(data);
  });
});
