var inspect = require('util').inspect;
var maria = new (require('mariasql'));
var config = {
  host: '127.0.0.1',
  user: 'root',
  password: 'Tldpffh.2014',
  db: 'clo'
};

function existBroadcaster(email, pwd, callback) {
  maria.query(' SELECT 1 FROM TBL_BROADCASTER'
             + '  WHERE EMAIL = :email AND PASSWD = :pwd', {
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

function readBroadcaster(email, pwd, callback) {
  maria.query(' SELECT MAX(EMAIL)    email'
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
      callback(err);
      // console.log('Maria.readBroadcaster, Error: ' + err);
    }).on('row', function(data) {
      callback(null, (data.email) ? data : null);
      delete data.img;
      // console.log('Maria.readBroadcaster, ' + inspect(data));
    });
  });
}

function createBroadcaster(data, callback) {
  var email = data.email;
  var pwd = data.pwd;
  var name = data.name;
  var img = data.img;

  maria.query(' INSERT INTO TBL_BROADCASTER VALUES'
             + ' ( :email, :pwd , :name, :img )', {
    'email': email,
    'pwd': pwd,
    'name': name,
    'img': img
  }).on('result', function(ret) {
    ret.on('error', function(err) {
      callback(err);
      // console.log('Maria.createBroadcaster, Error: ' + err);
    }).on('end', function(meta) {
      callback(null, (meta.numRows == 1) ? true : false);
      // console.log('Maria.createBroadcaster, Result: ' + ((meta.numRows == 1) ? true : false));
    });
  });
}

maria.on('connect', function() {
  console.log('Maria.connect');
}).on('error', function(err) {
  console.log('Maria.error, Error: ' + err);
}).on('close', function(err) {
  console.log('Maria.close' + (err ? (', Error: ' + err) : ''));
});

maria.connect(config);

/*
 * Export Symbol
 */
module.exports.existBroadcaster = existBroadcaster;
module.exports.createBroadcaster = createBroadcaster;
module.exports.readBroadcaster = readBroadcaster;
