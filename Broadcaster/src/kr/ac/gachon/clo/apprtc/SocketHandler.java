package kr.ac.gachon.clo.apprtc;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SessionDescription.Type;

import android.util.Log;

public class SocketHandler implements IOCallback {

	private static final String TAG = SocketHandler.class.getSimpleName();
	private PeerConnectionPool connectionPool;
	private boolean isLogin = false;
	private PipedInputStream chatInputStream;
	private PipedOutputStream chatOutputStream = new PipedOutputStream();

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		JSONObject data = (JSONObject)args[0];

		try {
			if("login".equals(event)) {
				if(isLogin) {
					return;
				}

				int ret = data.getInt("ret");

				if(ret == SocketResponse.SUCCESS) {
					isLogin = true;
				}

				return;
			}

			if("chat".equals(event)) {
				if(chatInputStream != null) {
					byte[] message = data.getString("msg").getBytes();

					chatOutputStream.write(message, 0, message.length);
				}

				return;
			}

			if("answer".equals(event)) {
				String sdp = data.getString("sdp");

				lazyInitConnectionPool();

				PeerConnection connection = connectionPool.getConnection();
				OfferAnswerCallback callback = new OfferAnswerCallback(connection);

				connection.getStats(new StatsObserver() {
					
					@Override
					public void onComplete(StatsReport[] arg0) {
						for(int i = 0; i < arg0.length; i++) {
							Log.d(TAG, arg0[i].id);
							Log.d(TAG, arg0[i].type);
							Log.d(TAG, arg0[i].timestamp + "");
							for(int j = 0; j < arg0[i].values.length; j++) {
								Log.d(TAG, arg0[i].values[j].name);
								Log.d(TAG, arg0[i].values[j].value);
							}
						}
					}
				}, null);
				
				connection.setRemoteDescription(callback, new SessionDescription(Type.ANSWER, sdp));
				return;
			}
			
			if("join".equals(event)) {
				Log.i(TAG, event);
			}

			throw new Exception(String.format("Unknown event: %s", event));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onMessage(String event, IOAcknowledge ack) {
		Log.i(TAG, event);
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		Log.i(TAG, json.toString());
	}

	@Override
	public void onConnect() {
		Log.i(TAG, "connect");
	}

	@Override
	public void onDisconnect() {
		Log.i(TAG, "disconnect");
	}

	@Override
	public void onError(SocketIOException e) {
		Log.e(TAG, e.getMessage(), e);
	}

	public PipedInputStream getChatInputStream() {
		return chatInputStream;
	}

	public void setChatInputStream(PipedInputStream chatInputStream) throws IOException {
		this.chatInputStream = chatInputStream;

		chatOutputStream.connect(chatInputStream);
	}

	private void lazyInitConnectionPool() {
		if(connectionPool == null) {
			connectionPool = PeerConnectionPool.getInstance();
		}
	}
}