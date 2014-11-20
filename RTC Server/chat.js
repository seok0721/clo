function initChatModule(socket) {
  socket.channels = {};
}

function chatHandler(data) {
  var channel = data.channel;
  var message = data.message;

  // TODO check validate channel

  socket.broadcast.to(channel).emit('chat', {
    'socketId': socket.id,
    'channel': channel,
    'message': message
  });
}

function enterTheChannelHandler(data) {
  var channel = data.channel;

  enterTheChannel(channel);
}

function leaveTheChannelHandler(data) {
  var channel = data.channel;

  leaveTheChannel(channel);
}

function enterTheChannel(socket, channel) {
  socket.join(channel);
  socket.broadcast.to(channel).emit('enterTheChannel', {
    'socketId': socket.id,
    'channel': channel
  });
  socket.channels[channel] = null; // 접속한 채널 목록에 추가합니다.

  console.log('enterTheChannel, socketId: ' + socket.id + ', channel: ' + channel);
}

function leaveTheChannel(socket, channel) {
  delete socket.channels[channel]; // 접속된 채널 정보를 삭제합니다.
  socket.broadcast.to(channel).emit('leaveTheChannel', {
    'socketId': socket.id,
    'channel': channel
  });
  socket.leave(channel);

  console.log('leaveTheChannel, socketId: ' + socket.id + ', channel: ' + channel);
}

function leaveTheChannelAll(socket) {
  for(channel in socket.channels) {
    leaveTheChannel(channel);
  }
}

/*
  socket.on('chat', chatHandler);
  socket.on('enterTheChannel', enterTheChannelHandler); // 채널 들어가기
  socket.on('leaveTheChannel', leaveTheChannelHandler); // 채널 나가기
  socket.on('disconnect', disconnectHandler);

  console.log('connection, socketId: ' + socket.id);
*/

module.exports.initChatModule = initChatModule;
module.exports.chatHandler = chatHandler;
module.exports.enterTheChannelHandler = enterTheChannelHandler;
module.exports.leaveTheChannelHandler = leaveTheChannelHandler;
module.exports.enterTheChannel = enterTheChannel;
module.exports.leaveTheChannel = leaveTheChannel;
module.exports.leaveTheChannelAll = leaveTheChannelAll;
