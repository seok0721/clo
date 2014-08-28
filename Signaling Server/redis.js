/*
 * Global Variables
 */
var redis = require('redis').createClient();
var status = require('./status.js');
var crypto = require('crypto');
var random = require('randomstring');

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

function create_room(room, callback) {

}

function create_session(email, callback) {
  redis.get(email, function(err, reply) {
    if(err) {
      log('Error occur on to create session.');

      callback(status.REDIS_ERROR, err);
    } else if(reply) {
      log('Session already exists.');

      callback(status.SESSION_ALREADY_EXIST, reply);
    } else {
      log('To create session success.');

      session_key = random.generate(32);
      session_value = {
        'email': email
      };

      redis.set(session_key, session_value);

      callback(status.OK, session_value);
    }
  });
}

function destroy_session(key, callback) {
  redis.get(key, function(err, reply) {
    if(err) {
      callback(err);
    } else {
      redis.del(key);
      callback(null);
    }
  });
}

function exist_session(key, callback) {
  redis.get(key, function(err, reply) {
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
module.exports.create_session = create_session;
module.exports.destroy_session = destroy_session;
module.exports.exist_session = exist_session;

