package kr.ac.gachon.clo.service;

import io.socket.SocketIO;

import java.util.HashMap;
import java.util.Map;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.event.ActivityExecuteResultHandler;
import kr.ac.gachon.clo.event.EventHandler;

import org.json.JSONObject;

import android.util.Log;

public class SocketService {

	private static final String TAG = SocketService.class.getSimpleName();
	private static SocketService instance = new SocketService();
	private SocketCallback callback = new SocketCallback();
	private SocketIO socket;

	public static SocketService getInstance() {
		return instance;
	}

	public void setFrontActivityHandler(ActivityExecuteResultHandler frontActivityHandler) {
		callback.setFrontActivityHandler(frontActivityHandler);
	}

	public void start() {
		if(socket != null && socket.isConnected()) {
			Log.w(TAG, "이미 서버에 접속되어 있습니다.");
			return;
		}

		try {
			socket = new SocketIO("http://211.189.20.193:10000");
			socket.connect(callback);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void stop() {
		socket.disconnect();
	}

	public void addEventHandler(EventHandler eventHandler) {
		callback.addEventHandler(eventHandler);
	}

	public void signIn(String email, String password) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);

		sendMessage("signin", param);
	}

	public void signUp(String email, String password, String name, String base64EncodedBitmap) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);
		param.put("name", name);
		param.put("img", base64EncodedBitmap);

		sendMessage("signup", param);
	}

	public void createRoom(String title) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("title", title);

		sendMessage("create", param);
	}

	public void handshake(String viewer, String sdp) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("viewer", viewer);
		param.put("channel", Global.getChannel());
		param.put("sdp", sdp);

		sendMessage("handshake2", param);
	}

	public void signOut() {
		sendMessage("signout");
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}

	private void sendMessage(String event, Map<String, Object> param) {
		if((socket == null) || !socket.isConnected()) {
			Log.e(TAG, "시그널링 서버와 연결되지 않았습니다.");
			return;
		}

		if(param == null) {
			socket.emit(event);
			return;
		}

		JSONObject json = new JSONObject();

		try {
			for(String key : param.keySet()) {
				json.put(key, param.get(key));
			}

			socket.emit(event, json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);

			try {
				json.put("ret", 1);
			} catch(Exception ex) {}

			callback.on(event, null, json);
		}
	}

	private void sendMessage(String event) {
		sendMessage(event, null);
	}
}