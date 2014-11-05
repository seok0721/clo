package kr.ac.gachon.clo.listener;

import kr.ac.gachon.clo.PeerConnectionPool;
import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.observer.AnswerObserver;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.app.Activity;
import android.util.Log;

public class AnswerMessageHandler implements Worker {

	private static final String TAG = AnswerMessageHandler.class.getSimpleName();
	private static final String EVENT = "answer";

	@Override
	public void onMessage(JSONObject data) {
		String sdp;

		try {
			sdp = data.getString("sdp");
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return;
		}

		Log.i(TAG, "Create answer session description...");

		SessionDescription session = new SessionDescription(Type.ANSWER, sdp);

		Log.i(TAG, "Retrieve peer connection from pool...");
		AnswerObserver observer = new AnswerObserver();
		PeerConnection connection = PeerConnectionPool.getInstance().dequeue();
		observer.setPeerConnection(connection);

		Log.i(TAG, "Set remote description...");
		connection.setRemoteDescription(observer, session);

		Log.i(TAG, "End of answer handling.");
	}

	@Override
	public Activity getActivity() {
		return null;
	}

	@Override
	public String getEvent() {
		return EVENT;
	}
}