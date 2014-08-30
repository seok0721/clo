package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONObject;

public class AppRTCEventHandler implements IOCallback {

	private SocketIO socket;
	private String room;

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
		JSONObject data = (JSONObject)args[0];

		if(AppRTCEvent.B_CREATE_ROOM.equals(event)) {
			try {
				int ret = data.getInt("ret");
				String room = data.getString("room");

				switch(ret) {
				case AppRTCStatus.OK:
					this.room = room;
					break;
				case AppRTCStatus.ROOM_ALREADY_EXIST:
					break;
				}
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
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

	public void clearRoom() {
		room = null;
	}
}