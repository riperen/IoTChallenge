var http = require('http'),
	fs = require('fs'),
	url = require('url'),
	sanitize = require('validator').sanitize,
	sockets = new Array();
	
var app = http.createServer(function (request, response) {
	//Notting
}).listen(1337);

var io = require('socket.io').listen(app);

io.sockets.on('connection', function(socket) {

	socket.on('reserveeer desk', function(data) { 
		console.log('reserve Room/desk : ' + data.roomNumber + '/' + data.deskNumber);

		//TODO: Save reservation of this desk

		//Update all clients
		io.sockets.emit('refresh_client');
	});

	// io.sockets.clients().forEach(function (socket) { 
	// 	console.log('>>>>>>>>>>> ' + socket.id);
	// });
	
	sockets.push(socket);
	console.log('New socket, socketcount: ' + sockets.length);
	

	socket.on('disconnect', function(){
		index = sockets.indexOf(this);
		
		if (~index) {
			sockets.splice(index,1);
			console.log('removing socket, socketcount: ' + sockets.length);
		}
	});
});
