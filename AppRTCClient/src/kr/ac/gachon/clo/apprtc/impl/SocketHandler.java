package kr.ac.gachon.clo.apprtc.impl;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.json.JSONObject;

import android.util.Log;

public class SocketHandler implements IOCallback {

	private static final String TAG = SocketHandler.class.getSimpleName();

	@Override
	public void on(String event, IOAcknowledge ack, Object... param) {
		try {
			if("answer".equals(event)) {
				JSONObject data = (JSONObject)param[0];

				AnswerHandler.enqueue(data.getString("sdp"));

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
}