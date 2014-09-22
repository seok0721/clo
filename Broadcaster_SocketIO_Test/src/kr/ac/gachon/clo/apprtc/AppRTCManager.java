package kr.ac.gachon.clo.apprtc;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import kr.ac.gachon.clo.apprtc.util.HashUtils;
import kr.ac.gachon.clo.apprtc.util.Log;

import org.json.JSONObject;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

public class AppRTCManager {

	private static final String TAG = AppRTCManager.class.getSimpleName();
	private PeerConnectionPool connectionPool = PeerConnectionPool.getInstance();
	private SessionDescription localDescription; 
	private String title;
	private SocketIO socket;
	private SocketHandler socketHandler;
	private boolean isStarted = false;

	public void getDescription() {
		if(localDescription == null) {
			connectionPool.getConnection().getLocalDescription();
		}
	}

	public void connect() throws MalformedURLException {
		socket.connect(socketHandler);
	}

	public void disconnect() {
		socket.disconnect();
	}

	public void login(String email, String password) {
		try {
			JSONObject json = new JSONObject();
			json.put("email", email);
			json.put("pwd", HashUtils.md5(password).toUpperCase());

			socket.emit("login", json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void logout() {
		socket.emit("logout");
	}

	public void create(String title) {
		try {
			JSONObject json = new JSONObject();
			json.put("title", title);

			socket.emit("create", json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void destroy() {
		socket.emit("destroy");
	}

	public void start() {
		if(isStarted) {
			return;
		}

		socket.emit("start");
		isStarted = true;

		PeerConnection connection = connectionPool.getConnection();
		final OfferAnswerCallback callback = new OfferAnswerCallback(connection);

		connection.createOffer(callback, new MediaConstraints());

		new Thread(new Runnable() {

			@Override
			public void run() {
				while(isStarted) {
					try {
						if(callback.getSessionDescription() != null) {
							offer(callback.getSessionDescription().description);
						}

						Thread.sleep(5000);
					} catch(InterruptedException e) {
						Log.e(TAG, e.getMessage());
					}
				}
			}
		}).start();
	}

	public void stop() {
		isStarted = false;

		socket.emit("stop");
	}

	public void chat(String message) {
		try {
			JSONObject json = new JSONObject();
			json.put("msg", message);

			socket.emit("chat", json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public String getTitle() {
		return title;
	}

	public SocketIO getSocket() {
		return socket;
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}

	public SocketHandler getSocketHandler() {
		return socketHandler;
	}

	public void setSocketHandler(SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	private void offer(String sdp) {
		try {
			JSONObject json = new JSONObject();
			json.put("sdp", sdp);

			socket.emit("offer", json);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
}