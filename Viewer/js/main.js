var s = io.connect('http://211.189.19.82:10080/');

s.on('connect', function(){
  console.log('connected');
});

s.on('news', function(room) {
	console.log(room);
});
