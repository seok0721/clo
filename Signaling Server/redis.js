/*
 * Global Variables
 */
var redis = require('redis').createClient();
var status = require('./status.js');

/*
 * Global Function
 */
function log(message) {
  console.log('Redis: ' + message);
}

/*
 * Main Routine
 */
redis.on('error', function(err) {
  log(err);
});

redis.on('ready', function() {
  log('flush all.');

  redis.flushall();
});

function session_create(email, callback) {
  redis.get(email, function(err, reply) {
    if(err) {
      log('Error occur on to create session.');

      callback(status.REDIS_ERROR, err);
    } else if(reply) {
      log('Session already exists.');

      callback(status.SESSION_ALREADY_EXIST, reply);
    } else {
      log('To create session success.');

      redis.set(email, {});

      callback(OK);
    }
  });
}

function is_created(email, callback) {
  redis.get(email, function(err, reply) {
    if(err) {
      log('Error occur on to get session.');
      callback(status.REDIS_ERROR, err);
    } else if(reply) {
      callback(OK);
    } else {
      callback(status.SESSION_NOT_FOUND);
    }
  });
}

/*
 * Export Symbol
 */
module.exports.session_create = session_create;
module.exports.is_created = is_created;
