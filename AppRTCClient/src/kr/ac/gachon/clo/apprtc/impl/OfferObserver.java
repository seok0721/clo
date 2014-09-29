package kr.ac.gachon.clo.apprtc.impl;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class OfferObserver implements SdpObserver {

	private static final String TAG = OfferObserver.class.getSimpleName();
	private static int MAX_RETRY = 3;
	private int retry = 0;
	private PeerConnection connection;

	public OfferObserver(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		Log.i(TAG, String.format("onCreateSuccess, %s", session.type.canonicalForm()));

		clearRetry();

		connection.setLocalDescription(this, session);
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, String.format("onCreateFailure, %s", error));

		retryCreateOffer();
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "onSetSuccess");

		clearRetry();

		PeerConnectionPool.getInstance().enqueue(connection);
		SignalingService.getInstance().push(connection.getLocalDescription());
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, String.format("onSetFailure, %s", error));

		retryCreateOffer();

		connection.dispose();
	}

	private void clearRetry() {
		retry = 0;
	}

	private void retryCreateOffer() {
		if(retry == MAX_RETRY) {
			Log.i(TAG, "Can't create connection.");
			return;
		}

		retry++;

		try {
			Thread.sleep(2000);
		} catch(InterruptedException e) {}

		connection.createOffer(this, new MediaConstraints());
	}
}