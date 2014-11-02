var http = require('http');
var express = require('express');
var mysql = require("mysql");
var app = express();
var timeout = express.timeout;

var connection = mysql.createConnection({
    host : "localhost",
    user : "root",
    password : "Tldpffh.2014",
    database : "clo"
});

 
function callback(err,result){
    if(err){
        throw err
    }
    console.log("Insert Complete!");
    console.log(query.sql);
}

app.get('/image', function(req, res) {
  connection.query('SELECT * from TBL_BROADCASTER'
   + ' where IMG_DATA is not null',
function(err, rows, fields) {
    console.log(rows[0].IMG_DATA);
    res.write('<img src="data:image/jpeg;base64,')
    res.write(rows[0].IMG_DATA);
    res.write('" />');
    res.end();
  });
});

app.get('/', function (request,response){
  console.log('get!!');
  var type = request.param('type', 'none');
  var output = '';  
  
  console.log('Mode : ', type);
  
  if(type == 'signin'){
  
    var id = request.param('id','none');
    var pwd = request.param('pwd','none');    
    
    output = 'false';

    console.log('id :' + id);
    console.log('pwd :' + pwd);
    
    //var sqlQuery = "INSERT INTO cielo SET ?";
    //var post = {id : "test@test.com", pwd : "1333", name : "noname"};
    
    connection.query('SELECT * from TBL_BROADCASTER', function(err, rows, fields){
      if (err) throw err;
      
      for(var i in rows) {
        if(rows[i].EMAIL == id && rows[i].PASSWD == pwd){
          //request.session.auth_user = 'test';
          console.log('success');
          output = 'success';
          var name = rows[i].name;
        }
        
        /*
        else if(request.session.auth_user){
          console.log('already login');
          output = 'aleady login';
        }
        else {
          console.log('fail');
          output = 'fail';
        }
        */
      }
      response.send(output + '/' + name);
      response.end();
      output = ''; // initialization after send messgae
    });
  }
  else if(type == 'signup'){
  
    var mail = request.param('mail', 'none');
    var pwd = request.param('pwd', 'none');
    var cpwd = request.param('cpwd', 'none');
    var name = request.param('name', 'none');
    var pByte = request.param('byte', 'none');
    
    output = 'possible';
    
      connection.query('SELECT * from TBL_BROADCASTER', function(err, rows, fields){
        if (err) throw err;
        
        for(var i in rows){
          if(rows[i].id == mail){
            console.log('exist');
            output = 'exist';
          }
        }
        response.send(output);
        possible = '';
      });
    
    //add database
    var data = {
      EMAIL : mail,
      PASSWD: pwd,
      NAME: name,
      IMG_DATA: pByte,
    };

    connection.query('INSERT INTO TBL_BROADCASTER SET ?', data, function(err, result) {
      if(err) throw err;
      
      console.log('insert mode');
    
    }); 
  }
});

http.createServer(app).listen(9000, function(){
  console.log('Server Running');
});
