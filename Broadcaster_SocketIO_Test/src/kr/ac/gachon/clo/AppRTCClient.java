package kr.ac.gachon.clo;

import io.socket.IOCallback;
import io.socket.SocketIO;

import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

public class AppRTCClient {

	private static final String URL = "http://211.189.19.82:10080/";
	private AppRTCEventHandler handler = new AppRTCEventHandler();
	private AppRTCStatus status = new AppRTCStatus();
	private SocketIO socket;
	private String userId;
	private String username;
	private IOCallback callback = new AppRTCEventHandler();
	private String sessionKey;

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
		if(socket != null && !socket.isConnected()) {
			socket.disconnect();
			
			System.out.println("disconnect");
		}
	}

	public void login(String email, String password) {
		try {
			JSONObject json = new JSONObject();
			json.put("email", email);
			json.put("passwd", HashUtils.md5(password).toUpperCase());

			socket.emit(AppRTCEvent.B_LOGIN, json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void createRoom(String room) throws JSONException {
		if(status.hasRoom()) {
			System.out.println("Broadcaster already has room.");
			return;
		}

		JSONObject json = new JSONObject();
		json.put("room".toUpperCase(), room);
		json.put("userid".toUpperCase(), userId);
		json.put("username".toUpperCase(), username);
		json.put("email", "seok0721@gmail.com");

		socket.emit(AppRTCEvent.B_CREATE_ROOM, json);
	}

	public void destroyRoom() {
		if(!handler.hasRoom()) {
			return;
		}

		socket.emit(AppRTCEvent.B_DESTROY_ROOM, (Object)null);
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