var socket = io.connect('http://211.189.19.82:10080/');

socket.on('connect', function(){
  console.log('connected');
});

socket.on('news', function(room) {
  console.log(room);
});
