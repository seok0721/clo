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

function read_broadcaster(email, passwd, callback) {
  client
  .query('SELECT * FROM TBL_BROADCASTER WHERE EMAIL = :email AND PASSWD = :passwd', {
    'email': email,
    'passwd': passwd
  })
  .on('result', function(result) {
    result
    .on('row', function(row) {
      callback(null, inspect(row));
    })
    .on('error', function(err) {
      callback(inspect(err));
    })
    .on('end', function(meta) {
      callback(null, null, inspect(meta));
    });
  })
  .on('end', function() {
    log('Query end.');

    callback(null, null, 'End of query.');
  });
}

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

