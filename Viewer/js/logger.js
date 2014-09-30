/*
 * Global Function
 */
function log(type, tag, message) {
  console.log('[' + type + ']\t#[' + tag + '] ' + message);
}

/*
 * Global Variable
 */
var level = 1;

/*
 * Global Constant
 */
var DEBUG = 1;
var INFO  = 2;
var WARN  = 3;
var ERROR = 4;
var FATAL = 5;

/*
 * Global Function
 */
var Log = {};
Log.d = function(tag, message) { if(level > DEBUG) return; log('DEBUG', tag, message); }
Log.i = function(tag, message) { if(level > INFO)  return; log('INFO',  tag, message); }
Log.w = function(tag, message) { if(level > WARN)  return; log('WARN',  tag, message); }
Log.e = function(tag, message) { if(level > ERROR) return; log('ERROR', tag, message); }
Log.f = function(tag, message) { if(level > FATAL) return; log('FATAL', tag, message); }

