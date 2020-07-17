var http = require("http");
var express = require("express");
var socketio = require("socket.io");
var app = express();

const hostname = "192.249.19.242";
const port = 80;

// 서버 생성
const server = http.createServer((request, response) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Hello Ham!');
});

// 클라이언트 대기..
server.listen(port, () => {
  console.log('Server running at http://'+hostname+':'+port+'/');
});

// socket IO 를 서버에 등록하고
// socket 서버 생성
var io = socketio.listen(server);
io.sockets.on('connection', function(socket) {
  console.log('Socket ID: ' + socket.id + ', Connect.')
  socket.on('clientMessage', function (data) {
    console.log('Client Message: ' + data);

    var message = {
      msg: 'server',
      data: 'data'
    };
    socket.emit('serverMessage', message);
  })
})