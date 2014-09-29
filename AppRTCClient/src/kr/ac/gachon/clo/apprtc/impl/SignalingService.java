package kr.ac.gachon.clo.apprtc.impl;

import io.socket.IOAcknowledge;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

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
	private Boolean repeatToSendOffer;
	private Thread offerHandler;
	private SocketIO socket;
	private Lock lock;
	private Condition doNextJob;

	public static SignalingService getInstance() {
		if(instance == null) {
			instance = new SignalingService();
		}

		return instance;
	}

	@Override
	public void start(String url) {
		synchronized(isRunning) {
			if(isRunning && (offerHandler != null) && offerHandler.isAlive()) {
				Log.i(TAG, "Background thread is already running.");
				return;
			}

			try {
				socket = new SocketIO(url);
				socket.connect(instance);
			} catch(Exception e) {
				Log.e(TAG, e.getMessage(), e);
				return;
			}

			isRunning = true;

			offerHandler = new Thread(instance);
			offerHandler.start();
		}
	}

	@Override
	public void stop() {
		synchronized(isRunning) {
			queue.clear();
			isRunning = false;
			offerHandler.interrupt();
		}
	}

	@Override
	public void runNextJob() {
		lock.lock();
		repeatToSendOffer = false;
		doNextJob.signal();
		lock.unlock();
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
		lock.lock();

		try {
			while(isRunning) {
				SessionDescription session = queue.take();
				repeatToSendOffer = true;

				JSONObject data = new JSONObject();

				try {
					data.put("sdp", session.description);
				} catch(JSONException ex) { 
					Log.e(TAG, ex.getMessage(), ex);
				}

				for(;;) {
					socket.emit("offer", data);

					try {
						doNextJob.await(2, TimeUnit.SECONDS);

						if(!repeatToSendOffer) {
							Log.i(TAG, "Handle next offer...");
							break;
						}
					} catch(InterruptedException e) {
						Log.i(TAG, "Handle next offer...");
						break;
					}
				}
			}
		} catch(InterruptedException e) {
			Log.i(TAG, "Stop signaling service...");
		}

		lock.unlock();
	}

	public void setSocket(SocketIO socket) {
		this.socket = socket;
	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... param) {
		if(!isRunning) {
			return;
		}

		try {
			if("answer".equals(event)) {
				JSONObject data = (JSONObject)param[0];

				SessionDescription session = new SessionDescription(Type.ANSWER, data.getString("sdp"));
				AnswerObserver observer = new AnswerObserver();
				PeerConnection connection = PeerConnectionPool.getInstance().dequeue();
				observer.setPeerConnection(connection);
				connection.setRemoteDescription(observer, session);

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

	private SignalingService() {}
}