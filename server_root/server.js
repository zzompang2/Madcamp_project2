/* module */
var mysql = require('mysql');
var express = require('express');
var app = express();
var fs = require('fs');
var multer = require('multer');       //  for uploading file
var storage = multer.diskStorage({
  destination: (req, file, cb) => {   // cb(콜백)함수를 통해
      cb(null, 'photo/')              // 전송된 file 저장 디렉토리 설정
  },
  filename: (req, file, cb) => {      // cb(콜백)함수를 통해
      cb(null, file.originalname)     // 전송된 file 이름 설정
  }
});
var upload = multer({ storage: storage});

app.use(express.json());
app.use(express.urlencoded({extended: true}));

/* server IP & port number */
const hostname = "192.249.19.244";
const port = 80;

/* connect server */
app.listen(port, function () {
    console.log('서버 실행 중... ' + hostname);
});

/* connect MySQL */
var connection = mysql.createConnection({
  host: "localhost",
  user: "root",
  database: "project2",
  password: ""
});

/* 회원가입 */
app.post('/user/join', function (req, res) {
  console.log(req.body);
  var userEmail = req.body.userEmail;
  var userPwd = req.body.userPwd;
  var userName = req.body.userName;

  // 삽입을 수행하는 sql문.
  var sql = 'INSERT INTO users (userEmail, userPwd, userName) VALUES (?, ?, ?)';
  var params = [userEmail, userPwd, userName];
  console.log('test1 ');

  // sql 문의 ?는 두번째 매개변수로 넘겨진 params의 값으로 치환된다.
  connection.query(sql, params, function (err, result, field) {
    var resultCode = 404;
    var message = '에러가 발생했습니다';
    console.log('test 2');
    
    if (err) {
        console.log('에러: ', err);
    } else {
      resultCode = 200;
      message = '회원가입에 성공했습니다.';
      console.log('잘됨: '+result[0]);
    }

    res.json({
      'code': resultCode,
      'message': message
    });
  });
});

app.post('/user/login', function (req, res) {
  var userEmail = req.body.userEmail;
  var userPwd = req.body.userPwd;
  var sql = 'select * from users where userEmail = ?';

  connection.query(sql, userEmail, function (err, result) {
    var resultCode = 404;
    var message = '에러가 발생했습니다';
    var name = '';

    if (err) {
      console.log(err);
    } else {
      if (result.length === 0) {
        resultCode = 204;
        message = '존재하지 않는 계정입니다!';
      } else if (userPwd !== result[0].userPwd) {
        resultCode = 204;
        message = '비밀번호가 틀렸습니다!';
      } else {
        resultCode = 200;
        name = result[0].userName;
        message = name + '님, 환영해요!';
      }
    }

    res.json({
      'code': resultCode,
      'message': message,
      'userName': name
    });
  });
});

app.post('/phone/upload', function (req, res) {
  
  var phoneId = req.body.phoneId;
  var phoneName = req.body.phoneName;
  var phoneNumber = req.body.phoneNumber;

  // 삽입을 수행하는 sql문.
  var sql = 'INSERT INTO phone VALUES (?, ?, ?)';
  var params = [phoneId, phoneName, phoneNumber];
  
  // sql 문의 ?는 두번째 매개변수로 넘겨진 params의 값으로 치환된다.
  connection.query(sql, params, function (err, result, field) {

    var resultCode;
    var message;

    if (err) {
      resultCode = 404;
      message = 'Upload Error...';
      console.log("Upload fail: ", req.body);

    } else {
      resultCode = 200;
      message = 'Upload Success!';
      console.log("Upload good: ", req.body);
    }

    res.json({
      code: resultCode,
      message: message
    });
  });
});

app.post('/photo/upload', upload.single('myFile'), function(req, res) {

  var resultCode;
  var message;
  var sql = "INSERT INTO photo VALUES (?, ?)";
  var value = [req.file.originalname, req.file.path]

  connection.query(sql, value, (err) => {
    if(err) {
      resultCode = 404;
      message = 'Upload Error...';
      console.log("Upload fail: ", value[0]);

    } else {
      resultCode = 200;
      message = 'Upload Success!';
      console.log("Upload good: ", value[0]);
    }
  });

  res.json({
    code: resultCode,
    message: message
  });
});

app.post('/phone/show', function(req, res) {

  var sql = "SELECT * FROM phone";

  connection.query(sql, (err, rows) => {
    if(err) {
      console.log('mysql error...')

    } else {
      var nameArray = new Array()
      var numberArray = new Array()
      const num = rows.length

      for(var i=0; i<num; i++) {
          nameArray[i] = rows[i].phoneName
          numberArray[i] = rows[i].phoneNumber
      }

      res.json({
        name: nameArray,
        numberOrPath: numberArray
      });
    }
  });
});