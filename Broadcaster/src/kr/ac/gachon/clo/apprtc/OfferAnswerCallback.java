package kr.ac.gachon.clo.apprtc;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class OfferAnswerCallback implements SdpObserver {

	private static final String TAG = OfferAnswerCallback.class.getSimpleName();
	private PeerConnection connection;
	private SessionDescription session;

	public OfferAnswerCallback(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(final SessionDescription session) {
		Log.d(TAG, "onCreateSuccess");

		connection.setLocalDescription(this, session);

		this.session = session;
	}

	@Override
	public void onCreateFailure(String message) {
		Log.e(TAG, "onCreateFailure");
		Log.e(TAG, message);
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "onSetSuccess");
	}

	@Override
	public void onSetFailure(String message) {
		Log.e(TAG, "onSetFailure");
		Log.e(TAG, message);
	}

	public SessionDescription getSessionDescription() {
		return session;
	}
}