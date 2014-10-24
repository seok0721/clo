/*
 * Global Variables
 */
var inspect = require('util').inspect;
var client = new (require('mariasql'));
var config = {
  host: '127.0.0.1',
  user: 'root',
  password: 'Tldpffh.2014',
  db: 'clo'
};

/*
 * Global Function
 */
function log(message) {
  console.log('Maria: ' + message);
}

function read_broadcaster(email, pwd, callback) {
  client
  .query(' SELECT *' +
         '   FROM TBL_BROADCASTER' +
         '  WHERE EMAIL  = :email' +
         '    AND PASSWD = :pwd', {
    'email': email,
    'pwd': pwd
  })
  .on('result', function(result) {
    result
    .on('error', function(err) {
      callback(err);
    })
    .on('end', function(meta) {
      callback(null, meta);
    });
  });
  /*
  .on('end', function() {
    log('Query end.');

    callback(null, null, 'End of query.');
  });
  */
}

function create_broadcaster(data, callback) {
  client.query(' insert into TBL_BROADCASTER values'
           + ' ( :EMAIL'
           + ' , :PASSWD'
           + ' , :NAME'
           + ' , :IMG_TYPE'
           + ' , :IMG_DATA )', data)
  .on('result', function(ret) {
    ret.on('error', function(err) {
      callback(err);
    }).on('end', function(meta) {
      callback(null, meta);
    });
  });
}

create_broadcaster({
  EMAIL: '',
  PASSWD: '',
  NAME: '',
  IMG_TYPE: null,
  IMG_DATA: null
}, function(err, ret) {
  console.log(err);
  console.log(ret);
});

/*
 * Main Routine
 */
client
.on('connect', function() {
  log('Connect');
})
.on('error', function(err) {
  log('Error, ' + err);
})
.on('close', function(err) {
  log('Close, ' + err);
});

client.connect(config);

/*
 * Export Symbol
 */
module.exports.read_broadcaster = read_broadcaster;

