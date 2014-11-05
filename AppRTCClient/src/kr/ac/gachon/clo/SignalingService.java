package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.listener.AnswerMessageHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SessionDescription;

import android.app.Activity;
import android.util.Log;

public class SignalingService implements Runnable, IOCallback {

	private static final String TAG = SignalingService.class.getSimpleName();
	private static SignalingService instance;
	private List<Worker> workerList = new ArrayList<Worker>();
	private BlockingQueue<SessionDescription> queue = new ArrayBlockingQueue<SessionDescription>(1);
	private Boolean isRunning = false;
	private Boolean doSendOffer = false;
	private Thread background;
	private SocketIO socket;
	private Lock lock;
	private Condition runtime;

	public static SignalingService getInstance() {
		if(instance == null) {
			instance = new SignalingService();
		}

		return instance;
	}

	public void addWorker(Worker worker) {
		workerList.add(worker);
	}

	public void start(String url) {
		synchronized(isRunning) {
			if(isRunning) {
				Log.i(TAG, "Signaling service is already running," + background.getState().name());
			} else {
				try {
					queue.clear();

					isRunning = true;

					background = new Thread(instance);
					background.start();
				} catch(Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
	}

	public void stop() {
		/*
		synchronized(isRunning) {
			if(!isRunning) {
				Log.i(TAG, "Background thread is stopped.");
			} else {
				isRunning = false;

				queue.clear();

				background.interrupt();
			}
		}
		*/
	}

	public void connect(String url) {
		if(socket != null && socket.isConnected()) {
			socket.disconnect();
		}

		Log.i(TAG, "Try to connect signaling server...");

		try {
			socket = new SocketIO(url);
			socket.connect(instance);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void disconnect() {
		socket.disconnect();
	}

	public void signin(String email, String password) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);

		sendMessage("signin", param);
	}

	public void create(String title) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("title", title);

		sendMessage("create", param);
	}

	public void signout() {
		sendMessage("signout");
	}

	public void signup(String email, String password, String name, String base64EncodedBitmap) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("email", email);
		param.put("pwd", password);
		param.put("name", name);
		param.put("img", base64EncodedBitmap);

		sendMessage("signup", param);
	}

	public void runNextJob() {
		synchronized(doSendOffer) {
			doSendOffer = false;

			signal();
		}
	}

	public void push(SessionDescription session) {
		for(;;) {
			try {
				queue.put(session);
				return;
			} catch(InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "Signaling service start.");
		Log.i(TAG, "Order to generator to create connection.");

		PeerConnectionGenerator.getInstance().orderToCreateConnection();

		lock.lock();

		try {
			while(isRunning) {
				Log.i(TAG, "Before take session description.");
				SessionDescription session = queue.take();

				synchronized(doSendOffer) {
					doSendOffer = true;
				}

				Log.i(TAG, "Make json data...");
				JSONObject data = new JSONObject();

				try {
					data.put("sdp", session.description);
				} catch(JSONException e) {
					Log.e(TAG, e.getMessage(), e);
				}

				Log.i(TAG, "Loop emit offer...");
				while(isRunning) {
					Log.i(TAG, "Emit offer...");
					socket.emit("offer", data);

					try {
						Log.i(TAG, "Timer running...");
						runtime.await(2, TimeUnit.SECONDS);

						if(!doSendOffer) {
							Log.i(TAG, "Handle next offer...");
							break;
						}
					} catch(InterruptedException e) {
						Log.i(TAG, "Handle next offer...");
						break;
					}
				}

				if(!isRunning) {
					Log.i(TAG, "Stop signaling service...");
					break;
				}
			}
		} catch(InterruptedException e) {
			Log.i(TAG, "Stop signaling service...");
		}

		lock.unlock();

		Log.i(TAG, "Signaling service stop.");
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
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

			throw new Exception(String.format("Unknown event: %s", event));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onConnect() {
		Log.i(TAG, "onConnect");
	}

	@Override
	public void onDisconnect() {
		Log.i(TAG, "onDisconnect");
	}

	@Override
	public void onError(SocketIOException e) {
		Log.i(TAG, String.format("onError, %s", e.getMessage()));
	}

	@Override
	public void onMessage(String message, IOAcknowledge ack) {
		Log.i(TAG, String.format("onMessage, %s", message));
	}

	@Override
	public void onMessage(JSONObject data, IOAcknowledge ack) {
		Log.i(TAG, String.format("onMessage, %s", data.toString()));
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

	private SignalingService() {
		lock = new ReentrantLock();
		runtime = lock.newCondition();

		workerList.add(new AnswerMessageHandler());
	}

	private void signal() {
		lock.lock();
		runtime.signal();
		lock.unlock();
	}

	private boolean isConnected() {
		return ((socket != null) && socket.isConnected());
	}
}