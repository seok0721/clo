var mariadb = require('node-mariadb');
var dbconn = mariadb.createConnection({
  driverType: mariadb.DRIVER_TYPE_HANDLER_SOCKET,
  host: '127.0.0.1',
  port: 3306,
  auth: {
    key: 'Tldpffh.2014'
  }
});
var db = 'clo';
var enable_mariadb = false;

console.log(dbconn);

function find_broadcaster(idx, callback) {
  idx.find([1], {limit: 1}, function(err, data) {
    callback(err, data);
  });
}

function read_broadcaster(email, callback) {
  if(!enable_mariadb) {
    return;
  }

  dbconn.openIndex(db, 'TBL_BROADCASTER', mariadb.HandlerSocket.PRIMARY, ['EMAIL'], function(err, idx) {
    if(err) {
      callback(err);
    } else {
      find_broadcaster(idx, callback);
    }
  });
}

dbconn.on('connect', function() {
  enable_mariadb = true;

  dbconn.openIndex(db, 'TBL_BROADCASTER', mariadb.HandlerSocket.PRIMARY, ['EMAIL'], function(err, idx) {
    if(err) {
      console.log(err);
    } else {
      idx.find([1], {limit: 1}, function(err, data) {
        if(err) {
          console.log(err);
        } else {
          console.log(data);
        }
      });
    }
  });
});

dbconn.on('error', function(err) {
  enable_mariadb = false;

  console.log(err);
});

/*
read_broadcaster('asdf', function(err, data) {
  if(err) {
    console.log(err);
  } else {
    console.log(data);
  }
});
*/
