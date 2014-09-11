package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import org.json.JSONObject;
import org.webrtc.PeerConnection;

import android.util.Log;

public class NodeEventHandler implements IOCallback {

	private static final String TAG = NodeEventHandler.class.getSimpleName();
	private PeerConnection connection;
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
		JSONObject data = (JSONObject) args[0];

		if (NodeEvent.B_CREATE_ROOM.equals(event)) {
			try {
				int ret = data.getInt("ret");
				String room = data.getString("room");

				switch (ret) {
				case NodeStatus.OK:
					this.room = room;
					break;
				case NodeStatus.ROOM_ALREADY_EXIST:
					break;
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		} else if (NodeEvent.V_JOIN_ROOM.equals(event)) {
			try {

			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
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

	public String getRoom() {
		return room;
	}

	public PeerConnection getConnection() {
		return connection;
	}

	public void setConnection(PeerConnection connection) {
		this.connection = connection;
	}
}