package kr.ac.gachon.clo;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.net.MalformedURLException;

import kr.ac.gachon.clo.utils.HashUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class NodeClient {

	private static final String URL = "http://211.189.19.82:10080/";
	private NodeEventHandler handler = new NodeEventHandler();
	private SocketIO socket;
	private String userId;
	private String username;
	private IOCallback callback = new NodeEventHandler();

	public NodeClient() {
		handler.setSocket(socket);
	}

	public void connect() throws MalformedURLException {
		if(socket != null && socket.isConnected()) {
			return;
		}

		socket = new SocketIO(URL);
		socket.connect(callback);
	}

	public void disconnect() {
		socket.disconnect();

		System.out.println("disconnect");
	}

	public void login(String email, String password) {
		try {
			JSONObject json = new JSONObject();
			json.put("sid", NodeSession.getSessionKey());
			json.put("email", email);
			json.put("pwd", HashUtils.md5(password).toUpperCase());

			socket.emit(NodeEvent.B_LOGIN, json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void logout() {
		try {
			JSONObject json = new JSONObject();
			json.put("sid", NodeSession.getSessionKey());

			socket.emit(NodeEvent.B_LOGOUT, json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void createRoom(String room) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("sid", NodeSession.getSessionKey());
		json.put("room", room);

		socket.emit(NodeEvent.B_CREATE_ROOM, json);
	}

	public void destroyRoom() {
		socket.emit(NodeEvent.B_DESTROY_ROOM, (Object)null);
		handler.clearRoom();
	}

	public String getUserid() {
		return userId;
	}

	public void setUserid(String userid) {
		this.userId = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}