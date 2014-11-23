var redis = require('redis').createClient();

redis.on('ready', function() {
  redis.flushall();
  console.log('Redis.ready');
}).on('error', function(err) {
  console.log('Redis.error, 원인: ' + err);
});


function createSession(email) {
  redis.hset('broadcaster', email, JSON.stringify({}));
}

function removeSession(email, callback) {
  redis.hdel('broadcaster', email, function(err, ret) {
    if(err) {
      callback(err);
      return;
    }

    if(ret == 1) {
      callback(null, true);
    } else {
      callback(null, false);
    }
  });
}

function existSession(email, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      callback(err);
      return;
    }

    if(data) {
      callback(null, true);
    } else {
      callback(null, false);
    }
  });
}

function createChannel(email, name, img, title, callback) {
  redis.hget('broadcaster', email, function(err, data) {
    if(err) {
      callback(err);
      return;
    }

    if(!data) {
      callback('로그인 세션이 없습니다.');
      return;
    }

    redis.hset('channel', email, JSON.stringify({
      'title': title,
      'name': name,
      'img': img
    }));

    callback(null);
  });
}

function removeChannel(email, callback) {
  redis.hdel('channel', email, function(err, ret) {
    if(err) {
      callback(err);
    } else {
      callback(null);
    }
  });
}

function existChannel(email, callback) {
  redis.hget('channel', email, function(err, data) {
    if(err) {
      callback(err);
      return;
    }

    try {
//      if(!data || !JSON.parse(data.title)) { // 채널이 없을 경우
      if(!data) { // 채널이 없을 경우
        callback(null, false);
      } else { // 채널이 있을 경우
        callback(null, true);
      }
    } catch(err) {
      callback(err);
    }
  });
}

function getChannelList(callback) {
  redis.hgetall('channel', function(err, data) {
    if(err) {
      callback(err);
      return;
    }

    // callback(null, JSON.parse(data));
    callback(null, data);
  });
}

module.exports.createSession = createSession;
module.exports.removeSession = removeSession;
module.exports.existSession = existSession;
module.exports.createChannel = createChannel;
module.exports.removeChannel = removeChannel;
module.exports.existChannel = existChannel;
module.exports.getChannelList = getChannelList;
