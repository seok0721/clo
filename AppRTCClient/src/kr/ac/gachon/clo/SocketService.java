package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.listener.AnswerMessageHandler;
import kr.ac.gachon.clo.listener.HandshakeHandler;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class SocketService implements IOCallback {

	private static final String TAG = SocketService.class.getSimpleName();
	private static SocketService instance;
	private List<Worker> workerList = new ArrayList<Worker>();
	private SocketIO socket;

	public static SocketService getInstance() {
		if(instance == null) {
			instance = new SocketService();
		}

		return instance;
	}

	@Override
	public void onConnect() {
		Log.i(TAG, "시그널링 서버와 연결되었습니다.");
	}

	@Override
	public void onDisconnect() {
		Log.i(TAG, "시그널링 서버와 연결이 종료되었습니다.");
	}

	@Override
	public void onError(SocketIOException e) {
		Log.e(TAG, e.getMessage(), e);
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... param) {
		try {
			final JSONObject data = (JSONObject)param[0];

			for(final Worker worker : workerList) {
				if(event.equals(worker.getEvent())) {
					Activity activity = worker.getActivity();

					if(activity == null) {
						worker.onMessage(data);
					} else {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								worker.onMessage(data);
							}
						});
					}

					return;
				}
			}

			throw new Exception(String.format("알 수 없는 이벤트: %s", event));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onMessage(String message, IOAcknowledge ack) {
		Log.i(TAG, message);
	}

	@Override
	public void onMessage(JSONObject data, IOAcknowledge ack) {
		Log.i(TAG, data.toString());
	}

	public boolean isConnected() {
		return ((socket != null) && socket.isConnected());
	}

	public void start() {
		if(socket != null && socket.isConnected()) {
			return;
		}

		// String serverIP = Resources.getSystem().getString(R.string.serverIP);
		// String serverPort = Resources.getSystem().getString(R.string.serverPort);

		try {
			// socket = new SocketIO(String.format("http://%s:%s", serverIP, serverPort));
			socket = new SocketIO("http://211.189.20.193:10000");
			socket.connect(this);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void stop() {
		socket.disconnect();
	}

	public void addWorker(Worker worker) {
		workerList.add(worker);
	}

	public void signin(String email, String password) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);

		sendMessage("signin", param);
	}

	public void signup(String email, String password, String name, String base64EncodedBitmap) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);
		param.put("name", name);
		param.put("img", base64EncodedBitmap);

		sendMessage("signup", param);
	}

	public void create(String title) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("title", title);

		sendMessage("create", param);
	}

	public void handshake(String viewer, String channel, String sdp) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("viewer", viewer);
		param.put("channel", channel);
		param.put("sdp", sdp);

		sendMessage("handshake2", param);
	}

	public void signout() {
		sendMessage("signout");
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}

	private void sendMessage(String event, Map<String, Object> param) {
		if(!isConnected()) {
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

			on(event, null, json);
		}
	}

	private void sendMessage(String event) {
		sendMessage(event, null);
	}

	private SocketService() {
		workerList.add(new AnswerMessageHandler());
		workerList.add(new HandshakeHandler());
	}
}