/*
 * Global Variables
 */
var redis = require('redis').createClient();
var status = require('./status.js');
var crypto = require('crypto');
var random = require('randomstring');
var Log = require('./logger.js');
var inspect = require('util').inspect;
var TAG = 'redis.js';

/*
 * Main Routine
 */
redis.on('error', function(err) {
  Log.e(TAG, err);
});

redis.on('ready', function() {
  Log.i(TAG, 'ready');

  redis.flushall();
});

function /* er */ exist_room(email, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      Log.e(TAG + ' exist_room', err);
      callback(err);
      return;
    } 

    data = JSON.parse(ret);
    Log.i(TAG + ' exist_room', data);
    callback(null, data.title ? true : false);
  });
}

// BEGIN exist_session
function /* es */ exist_session(email, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      Log.e(TAG + ' exist_session', err);
      callback(err);
      return;
    }

    Log.i(TAG + ' exist_session', inspect(data));
    callback(null, data ? true : false);
  });
}

// function /* es */ exist_session(email, callback) {
/*
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
*/

function es_list_length_callback(len, callbeck) {
  redis.lrange('broadcaster', 0, len, function(err, list) {
    if(err) {
      Log.e(TAG + ' es_list_length_callback', err);
      callback(err);
      return;
    }

    Log.i(TAG + ' es_list_length_callback', inspect(list));
    es_list_range_callback(list, callback);
  });
}

function es_list_range_callback(list, callback) {
  for(i = 0; i < len; i++) {
    if(JSON.parse(ret[i]).email == email) {
      Log.i(TAG + ' es_list_range_callback', true);
      callback(null, true);
      return;
    }
  }

  Log.i(TAG + ' es_list_range_callback', false);
  callback(null, false);
}
// END exist_session

// BEGIN create_session
function /* cs */ create_session(email) {
  redis.hset('broadcaster', email, JSON.stringify({}));
}

function /* cr */ create_room(email, title, callback) {
  redis.hget('broadcaster', email, function(err, ret) {
    if(err) {
      Log.e(TAG + ' create_room', err);
      callback(err);
      return;
    }

    data = JSON.parse(ret);
    data.title = title;

    redis.hset('broadcaster', email, JSON.stringify(data));

    Log.i(TAG + ' create_room', inspect(data));
    callback(null);
  });
}

function /* dr */ destroy_room(email) {
  redis.hget('broadcaster', email, function(err, ret) {
    if(err) {
      Log.e(TAG + ' create_room', err);
      callback(err);
      return;
    }

    data = JSON.parse(ret);
    delete data.title;
    Log.i(TAG + ' destroy_room', inspect(data));
    redis.hset('broadcaster', email, JSON.stringify(data));
  });
}

// function /* cs */ create_session(email, callback) {
/*
  redis.rpush('broadcaster', JSON.stringify({
    'email': email
  }));
}
*/
// END create_session

// BEGIN destroy_session
function /* ds */ destroy_session(email) {
  redis.hdel('broadcaster', email, function(err, ret) {
    if(ret == 0) {
      return;
    }

    Log.i(TAG + ' destroy_session', email);
  });
}

// function /* ds */ destroy_session(email, callback) {
/*
  redis.llen('broadcaster', function(err, len) {
    if(err) {
      log(err);
      return;
    }

    if(len == 0) {
      return;
    }

    // TODO ds_list_lrange_session
  });
}
*/
// END destroy_session

/*
 * Export Symbol
 */
module.exports.exist_session = exist_session;
module.exports.create_session = create_session;
module.exports.exist_room = exist_room;
module.exports.create_room = create_room;
module.exports.destroy_room = destroy_room;
module.exports.destroy_session = destroy_session;

