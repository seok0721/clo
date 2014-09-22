package kr.ac.gachon.clo.apprtc;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import kr.ac.gachon.clo.apprtc.util.Log;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

public class SocketHandler implements IOCallback {

	private static final String TAG = SocketHandler.class.getSimpleName();
	private PeerConnectionPool connectionPool = PeerConnectionPool.getInstance();
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

				PeerConnection connection = connectionPool.getConnection();
				OfferAnswerCallback callback = new OfferAnswerCallback(connection);

				connection.setRemoteDescription(callback, new SessionDescription(Type.ANSWER, sdp));
				return;
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
}