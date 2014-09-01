## RTC Broadcasting System, Graduation project, Gachon university

- node = 0.8
- socket.io = 0.9
- mariasql = 0.1
- randomstring = 1.0

## Event

1. Broadcaster:
  - Login
  - Logout
  - Create room
  - Destroy room

2. Viewer:
  - Join room
  - Withdraw room

## Status Code
- 1st: {2: success, 4: client error, 5: server error}
- 2nd: {0: common, 1: signaling server, 2: maria db, 3: redis, 4: argument, 5: ?}

## Event Code Explain

- 1st: Who, {1: Broadcaster, 2: Viewer}
- 2nd: How, Event type
- 3th: What, Action

## Event Code

- 111 : Broadcatser login
- 112 : Broadcaster logout
- 121 : Broadcaster create room
- 122 : Broadcaster destory room
- 221 : Viewer join room
- 222 : Viewer withdraw room

## System Architecture

                   +-------> Redis <-----------+
                   |                           |
    Android <-> Node.js <-> MariaDB <-> Apache Http Server
                   |                           |
                   +------> Chrome <-----------+

