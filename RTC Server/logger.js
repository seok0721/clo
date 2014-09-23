/*
 * Global Function
 */
function log(type, tag, message) {
  console.log('[' + type + '] ' + tag + ' ' + message);
}

module.exports.d = function(tag, message) { log('DEBUG', tag, message); }
module.exports.i = function(tag, message) { log('INFO',  tag, message); }
module.exports.w = function(tag, message) { log('WARN',  tag, message); }
module.exports.e = function(tag, message) { log('ERROR', tag, message); }
module.exports.f = function(tag, message) { log('FATAL', tag, message); }

