package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONObject;

public class AppRTCEventHandler implements IOCallback {

	private SocketIO socket;
	private boolean roomCreated = false;

	@Override
	public void onConnect() {
		System.out.println("connect");
	}

	@Override
	public void onDisconnect() {
		System.out.println("disconnect");
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		System.out.println("event: " + event);
		System.out.println("args:" + args[0]);
		ack.ack(args[0]);
		/*
		try {
			JSONObject json = (JSONObject)args[0];

			if(AppRTCEvent.CREATE.equals(event)) {
				createRoom(json);
			} else if(AppRTCEvent.DESTROY.equals(event)) {
				destroyRoom(json);
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		*/
	}

	@Override
	public void onError(SocketIOException e) {
		System.out.println(e.getMessage());
	}

	@Override
	public void onMessage(String event, IOAcknowledge ack) {
		System.out.println("event: " + event + ", ack: " + ack.toString());
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		System.out.println("json: " + json.toString() + ", ack: " + ack.toString());
	}

	public SocketIO getSocket() {
		return socket;
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}
	
	public boolean hasRoom() {
		return roomCreated;
	}

	/*
	private void createRoom(final JSONObject json) {
		try {
			int code = json.getInt("code");

			if(AppRTCResponse.SUCCESS == code) {
				System.out.println("create success");
				roomCreated = true;
			} else {
				System.err.println("create failure");
				roomCreated = false;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void destroyRoom(final JSONObject json) {
		try {
			int code = json.getInt("code");

			if(AppRTCResponse.SUCCESS == code) {
				System.out.println("destroy success");
				roomCreated = false;
			} else {
				System.err.println("destroy failure");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	*/
}