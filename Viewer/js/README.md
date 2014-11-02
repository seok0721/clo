# socket.io refernece

## socket

- Events
  - connect. Fired upon connecting.
  - error. Fired upon a connection error
    - Parameters:
      - Object error data
  - disconnect. Fired upon a disconnection.
  - reconnect. Fired upon a successful reconnection.
    - Parameters:
      - Number reconnection attempt number
  - reconnect\_attempt. Fired upon an attempt to reconnect.
  - reconnecting. Fired upon an attempt to reconnect.
    - Parameters:
      - Number reconnection attempt number
  - reconnect\_error. Fired upon a reconnection attempt error.
    - Parameters:
      - Object error object
  - reconnect\_failed. Fired when couldn’t reconnect within reconnectionAttempts
