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
  log('error: ' + err);
});

redis.on('ready', function() {
  log('ready');

  redis.flushall();
});

// BEGIN exist_session
function /* es */ exist_session(email, callback) {
  redis.llen('broadcaster', function(err, len) {
    if(err) {
      callback(err);
      return;
    }

    if(len == 0) {
      callback('empty set');
      return;
    }

    es_list_length_callback(len, callback);
  });
}

function es_list_length_callback(len, callbeck) {
  redis.lrange('broadcaster', 0, len, function(err, list) {
    if(err) {
      callback(err);
      return;
    }

    es_list_range_callback(list, callback);
  });
}

function es_list_range_callback(list, callback) {
  for(i = 0; i < len; i++) {
    if(JSON.parse(ret[i]).email == email) {
      callback(null, true);
      return;
    }
  }

  callback(null, false);
}
// END exist_session

// BEGIN create_session
function /* cs */ create_session(email, callback) {
  redis.rpush('broadcaster', JSON.stringify({
    'email': email
  }));
}
// END create_session

// BEGIN destroy_session
function /* ds */ destroy_session(email, callback) {
  redis.llen('broadcaster', function(err, len) {
    if(err) {
      log(err);
      return;
    }

    if(len == 0) {
      return;
    }

//    ds_list_lrange_session
  });
}
// END destroy_session

/*
 * Export Symbol
 */
module.exports.exist_session = exist_session;
module.exports.create_session = create_session;
module.exports.destroy_session = destroy_session;

