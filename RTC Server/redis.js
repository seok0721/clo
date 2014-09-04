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
  redis.get(email, function(err, ret) {
    var session_key;
    var session_value;

    if(err) {
      log('Error occur on to create session.');

      callback(err, status.REDIS_ERROR);
    } else if(ret) {
      log('Session already exists.');

      callback(null, status.SESSION_ALREADY_EXIST, ret);
    } else {
      log('To create session success.');

      // If you want to make session key start character, using next line
      // do {
      //   session_key = random.generate(32).toUpperCase();
      // } while(session_key[0] >= '0' && session_key[0] <= '9');

      session_key = random.generate(32).toUpperCase();
      session_value = {
        'email': email
      };

      redis.set(session_key, session_value);

      callback(null, status.OK, session_key);
    }
  });
}

function destroy_session(sid, callback) {
  redis.get(sid, function(err, ret) {
    if(err) {
      callback(err);
    } else {
      redis.del(sid);
      callback(null);
    }
  });
}

function exist_session(sid, callback) {
  redis.get(sid, function(err, ret) {
    if(err) {
      log('Error occured during to get session.');
      callback(err, status.REDIS_ERROR, err);
    } else if(ret) {
      log('Session exists. sid: ' + sid);
      callback(null, status.OK, sid);
    } else {
      log('Session not exists.');
      callback(null, status.SESSION_NOT_FOUND);
    }
  });
}

/*
 * Export Symbol
 */
module.exports.create_session = create_session;
module.exports.destroy_session = destroy_session;
module.exports.exist_session = exist_session;

