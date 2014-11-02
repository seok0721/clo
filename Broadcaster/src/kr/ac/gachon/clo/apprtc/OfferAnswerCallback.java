package kr.ac.gachon.clo.apprtc;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class OfferAnswerCallback implements SdpObserver {

	private static final String TAG = OfferAnswerCallback.class.getSimpleName();
	private PeerConnection connection;

	public OfferAnswerCallback(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(final SessionDescription session) {
		Log.d(TAG, "onCreateSuccess");
		Log.d(TAG, session.description);

		connection.setLocalDescription(this, session);
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
		Log.e(TAG, connection.signalingState().name());
		Log.e(TAG, connection.iceConnectionState().name());
		Log.e(TAG, message);
	}
}