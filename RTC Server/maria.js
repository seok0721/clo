/*
 * Import Module
 */
var Log = require('./logger.js');
var inspect = require('util').inspect;
var client = new (require('mariasql'));
var config = {
  host: '127.0.0.1',
  user: 'root',
  password: 'Tldpffh.2014',
  db: 'clo'
};

/*
 * Static Constant
 */
var SUCCESS = 0;
var FAILURE = 1;
var TAG = 'Maria DB'

/*
 * Global Function
 */
function log(message) {
  console.log('Maria: ' + message);
}

function existBroadcaster(email, pwd, callback) {
  client.query('SELECT 1 FROM TBL_BROADCASTER WHERE EMAIL = :email AND PASSWD = :pwd', {
    'email': email,
    'pwd': pwd
  }).on('result', function(ret) {
    ret.on('error', function(err) {
      callback(err);
    }).on('end', function(meta) {
      callback(null, ((meta.numRows == 1) ? true : false));
    });
  });
}

function read_broadcaster(email, pwd, callback) {
  client.query(' SELECT MAX(EMAIL)    email'
             + '      , MAX(PASSWD)   pwd'
             + '      , MAX(NAME)     name'
             + '      , MAX(IMG_DATA) img'
             + '   FROM TBL_BROADCASTER'
             + '  WHERE EMAIL  = :email'
             + '    AND PASSWD = :pwd', {
    'email': email,
    'pwd': pwd
  }).on('result', function(ret) {
    ret.on('error', function(err) {
      Log.e(TAG + '.read_broadcaster', err);
      callback(err);
    }).on('row', function(data) {
      Log.i(TAG + '.read_broadcaster', data.email + ',' + data.pwd + ',' + data.name);
      callback(null, data.email ? data : {});
    });
  });
}

function createBroadcaster(data, callback) {
  var email = data.email;
  var pwd   = data.pwd;
  var name  = data.name;
  var img   = data.img;

  client.query('INSERT INTO TBL_BROADCASTER VALUES ( :email, :pwd , :name, :img )', {
    'email': email,
    'pwd':   pwd,
    'name':  name,
    'img':   img
  }).on('result', function(ret) {
    ret.on('error', function(err) {
      callback(err);
    }).on('end', function(meta) {
      callback(null, (meta.numRows == 1) ? true : false);
    });
  });
}

/*
 * Main Routine
 */
client
.on('connect', function() {
  Log.i(TAG, 'ready');
})
.on('error', function(err) {
  Log.i(TAG, 'Error, ' + err);
})
.on('close', function(err) {
  Log.i(TAG, 'Close, ' + err);
});

client.connect(config);

/*
 * Export Symbol
 */
module.exports.existBroadcaster = existBroadcaster;
module.exports.read_broadcaster = read_broadcaster;
module.exports.createBroadcaster = createBroadcaster;
