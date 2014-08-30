package kr.ac.gachon.clo;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

public class AppRTCClient {

	private static final String URL = "http://211.189.19.82:10080/";
	private AppRTCEventHandler handler = new AppRTCEventHandler();
	private SocketIO socket;
	private String userId;
	private String username;
	private IOCallback callback = new AppRTCEventHandler();

	public AppRTCClient() {
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
			json.put("sid", AppRTCSession.getSessionKey());
			json.put("email", email);
			json.put("pwd", HashUtils.md5(password).toUpperCase());

			socket.emit(AppRTCEvent.B_LOGIN, json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void logout() {
		try {
			JSONObject json = new JSONObject();
			json.put("sid", AppRTCSession.getSessionKey());

			socket.emit(AppRTCEvent.B_LOGOUT, json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void createRoom(String room) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("sid", AppRTCSession.getSessionKey());
		json.put("room", room);

		socket.emit(AppRTCEvent.B_CREATE_ROOM, json);
	}

	public void destroyRoom() {
		socket.emit(AppRTCEvent.B_DESTROY_ROOM, (Object)null);
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