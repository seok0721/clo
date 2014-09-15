package kr.ac.gachon.clo;

import io.socket.SocketIO;

import java.net.MalformedURLException;
import java.util.Locale;

import kr.ac.gachon.clo.utils.HashUtils;

import org.json.JSONObject;

public class NodeClient {

	private SocketIO socket;
	private String room;
	private NodeHandler callback;

	public void connect() throws MalformedURLException {
		socket.connect(callback);
	}

	public void disconnect() {
		socket.disconnect();
	}

	public void login(String email, String password) {
		try {
			JSONObject json = new JSONObject();
			json.put("email", email);
			json.put("pwd", HashUtils.md5(password).toUpperCase(Locale.US));

			socket.emit("login", json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void logout() {
		try {
			socket.emit("logout");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void create(String title) {
		try {
			JSONObject json = new JSONObject();
			json.put("title", title);

			socket.emit("create", json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void destroy() {
		socket.emit("destroy");
	}

	public void start() {
		socket.emit("start");
	}

	public void stop() {
		socket.emit("stop");
	}

	public void offer(String sdp) {
		try {
			JSONObject json = new JSONObject();
			json.put("sdp", sdp);

			socket.emit("offer", json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void join(String room) {
		socket.emit("join", room);
	}

	public void withdraw() {
		socket.emit("withdraw");
	}

	public void chat(String message) {
		try {
			JSONObject json = new JSONObject();
			json.put("msg", message);

			socket.emit("chat", json);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public String getRoom() {
		return room;
	}

	public SocketIO getSocket() {
		return socket;
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}

	public NodeHandler getCallback() {
		return callback;
	}

	public void setCallback(NodeHandler callback) {
		this.callback = callback;
	}
}