/*
 * Protocol:
 *
 *   2xx Succesful
 *   4xx Client error
 *   5xx Server error
 *
 *   x0x Common info
 *   x1x Signaling server
 *   x2x Maria database
 *   x3x Redis cache server
 *   x4x Message info
 */

/*
 * Export Symbol
 */
module.exports.OK = 200;
module.exports.LOGIN = 201;
module.exports.LOGOUT = 202;
module.exports.REDIS_ERROR = 430;
module.exports.SESSION_ALREADY_EXIST = 431;
module.exports.SESSION_NOT_FOUND = 432;
module.exports.GET_SESSION_ERROR = 433;

