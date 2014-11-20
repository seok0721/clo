/*
 * Global Variables
 */
var redis = require('redis').createClient();
var status = require('./status.js');
var crypto = require('crypto');
var random = require('randomstring');
var Log = require('./logger.js');
var inspect = require('util').inspect;
var TAG = 'Redis.io';

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

function get_channel_list(callback) {
  redis.hgetall('channel', function(err, data) {
    if(err) {
      callback(err);
      return;
    }

    data = JSON.parse(data);

    Log.i(TAG + '.get_channel_list', '');
    callback(null, data);
  });
}

function /* er */ exist_channel(email, callback) {
  redis.hget('channel', email, function(err, data) {
    Log.i(TAG + '.exist_channel', data);
    Log.i(TAG + '.exist_channel', inspect(data));

    if(err) {
      Log.e(TAG + '.exist_channel', err);
      callback(err);
      return;
    }

    if(!data) {
      Log.i(TAG + '.exist_channel', '채널이 없습니다.');
      callback(null, false);
      return;
    }

    var data = JSON.parse(data);

    Log.i(TAG + '.exist_channel', JSON.stringify(data));
    callback(null, data.title ? true : false);
  });
}

// BEGIN exist_session
function /* es */ exist_session(email, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      Log.e(TAG + '.exist_session', err);
      callback(err);
      return;
    }

    var exist = (data ? true : false);

    Log.i(TAG + '.exist_session', exist);
    callback(null, exist);
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
      Log.e(TAG + '.es_list_length_callback', err);
      callback(err);
      return;
    }

    Log.i(TAG + '.es_list_length_callback', inspect(list));
    es_list_range_callback(list, callback);
  });
}

function es_list_range_callback(list, callback) {
  for(i = 0; i < len; i++) {
    if(JSON.parse(ret[i]).email == email) {
      Log.i(TAG + '.es_list_range_callback', true);
      callback(null, true);
      return;
    }
  }

  Log.i(TAG + '.es_list_range_callback', false);
  callback(null, false);
}
// END exist_session

// BEGIN create_session
function /* cs */ create_session(email) {
  redis.hset('broadcaster', email, JSON.stringify({}));
}

function /* cr */ create_channel(email, name, title, img, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      Log.e(TAG + '.create_channel', err);
      callback(err);
      return;
    }

    if(!data) {
      Log.i(TAG + '.create_channel', '로그인 후 사용하세요.');
      callback('Not logged in.');
      return;
    }

    // redis.hset('broadcaster', email, JSON.stringify(data));
    redis.hset('channel', email, JSON.stringify({
      'title': title,
      'name': name,
      'img': img
    }));

    Log.i(TAG + '.create_channel', '채널을 생성합니다.');
    Log.i(TAG + '.create_channel', '계정: ' + email);
    Log.i(TAG + '.create_channel', '제목: ' + title);

    callback(null);
  });
}

function /* dr */ destroy_room(email) {
  redis.hdel('channel', email, function(err, ret) {
    if(err) {
      Log.e(TAG + '.destroy_room', err);
      callback(err);
      return;
    }
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

    Log.i(TAG + '.destroy_session', email);
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
module.exports.exist_channel = exist_channel;
module.exports.create_channel = create_channel;
module.exports.destroy_room = destroy_room;
module.exports.destroy_session = destroy_session;
module.exports.get_channel_list = get_channel_list;

