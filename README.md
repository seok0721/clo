# RTC Broadcasting System, Graduation project, Gachon university

## Version

- node = 0.8
  - socket.io = 0.9
  - mariasql = 0.1
  - randomstring = 1.0

## Event Type

- connect
- disconnect
- login
- logout
- create
- destroy
- join
- withdraw
- chat
- session

## Status Code

- 1st: {2: success, 4: client error, 5: server error}
- 2nd: {0: common, 1: signaling server, 2: maria db, 3: redis, 4: argument, 5: ?}

## System Architecture

                   +-------> Redis <-----------+
                   |                           |
    Android <-> Node.js <-> MariaDB <-> Apache Http Server
                   |                           |
                   +------> Chrome <-----------+

