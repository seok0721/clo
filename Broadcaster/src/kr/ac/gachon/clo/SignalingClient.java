package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

public class SignalingClient {

	private String TAG = SignalingClient.class.getSimpleName();
	private Thread socketIoThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				URL url = new URL("http://211.189.19.82:20130/");
				SocketIO socketIO = new SocketIO(url);
				socketIO.connect(new IOCallback() {

					@Override
					public void onMessage(JSONObject arg0, IOAcknowledge arg1) {
						Log.i(TAG, arg0.toString());
					}

					@Override
					public void onMessage(String arg0, IOAcknowledge arg1) {
						Log.i(TAG, arg0.toString());
					}

					@Override
					public void onError(SocketIOException arg0) {
						Log.i(TAG, arg0.toString());
					}

					@Override
					public void onDisconnect() {
						Log.i(TAG, "onDisconnect");
					}

					@Override
					public void onConnect() {
						Log.i(TAG, "onConnect");
					}

					@Override
					public void on(String arg0, IOAcknowledge arg1, Object... arg2) {
						Log.i(TAG, arg0.toString());
						// Log.i(TAG, arg1.toString());
						for(Object obj : arg2) {
							Log.i(TAG, obj.toString());
						}
						/*
						try {
							Log.i(TAG, new JSONObject((String)arg2[0]).getString("args"));
						} catch (JSONException e) {
							Log.e(TAG, e.getMessage(), e);
						}
						 */
					}
				});
				socketIO.emit("message", "This is android.");
			} catch(Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	});
}