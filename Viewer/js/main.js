var url = 'http://211.189.19.82:10080/';
var socket;

function connect() {
	socket = io.connect(url);
	socket.on('connect', function() {
		console.log('connected');
	});
}
