/*
 * Static Function
 */
function log(type, tag, message) {
  console.log('[' + type + ']\t#[' + tag + '] ' + message);
}

/*
 * Static Variable
 */
var level = 1;

/*
 * Static Constant
 */
var DEBUG = 1;
var INFO  = 2;
var WARN  = 3;
var ERROR = 4;
var FATAL = 5;

/*
 * Export Symbol
 */
module.exports.DEBUG = DEBUG;
module.exports.INFO  = INFO;
module.exports.WARN  = WARN;
module.exports.ERROR = ERROR;
module.exports.FATAL = FATAL;

module.exports.d = function(tag, message) { if(level > DEBUG) return; log('DEBUG', tag, message); }
module.exports.i = function(tag, message) { if(level > INFO)  return; log('INFO',  tag, message); }
module.exports.w = function(tag, message) { if(level > WARN)  return; log('WARN',  tag, message); }
module.exports.e = function(tag, message) { if(level > ERROR) return; log('ERROR', tag, message); }
module.exports.f = function(tag, message) { if(level > FATAL) return; log('FATAL', tag, message); }

