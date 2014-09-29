package kr.ac.gachon.clo.apprtc.impl;

import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kr.ac.gachon.clo.apprtc.ISignalingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.util.Log;

public class SignalingService implements ISignalingService {

	private static final String TAG = PeerConnectionGenerator.class.getSimpleName();
	private static SignalingService instance;
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

	@Override
	public void start(String url) {
		synchronized(isRunning) {
			if(isRunning) {
				Log.i(TAG, "Signaling service is already running," + background.getState().name());
			} else {
				try {
					if(socket != null && socket.isConnected()) {
						socket.disconnect();
					}

					queue.clear();

					socket = new SocketIO(url);
					socket.connect(instance);

					isRunning = true;

					background = new Thread(instance);
					background.start();
				} catch(Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void stop() {
		synchronized(isRunning) {
			if(!isRunning) {
				Log.i(TAG, "Background thread is stopped.");
			} else {
				isRunning = false;

				queue.clear();
				socket.disconnect();

				background.interrupt();
			}
		}
	}

	@Override
	public void runNextJob() {
		synchronized(doSendOffer) {
			doSendOffer = false;

			signal();
		}
	}

	@Override
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
			if("answer".equals(event)) {
				Log.i(TAG, "Receive event answer.");
				JSONObject data = (JSONObject)param[0];

				Log.i(TAG, "Create answer session description...");
				SessionDescription session = new SessionDescription(Type.ANSWER, data.getString("sdp"));

				Log.i(TAG, "Retrieve peer connection from pool...");
				AnswerObserver observer = new AnswerObserver();
				PeerConnection connection = PeerConnectionPool.getInstance().dequeue();
				observer.setPeerConnection(connection);

				Log.i(TAG, "Set remote description...");
				connection.setRemoteDescription(observer, session);

				Log.i(TAG, "End of answer handling.");
				return;
			}

			throw new Exception(String.format("Unknown event: %s", event));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onConnect() {
		Log.i(TAG, "onConnect");

		PeerConnectionGenerator.getInstance().orderToCreateConnection();
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

	private SignalingService() {
		lock = new ReentrantLock();
		runtime = lock.newCondition();
	}

	private void signal() {
		lock.lock();
		runtime.signal();
		lock.unlock();
	}
}