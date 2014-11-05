package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.PeerConnectionPool;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class OfferObserver implements SdpObserver {

	private static final String TAG = OfferObserver.class.getSimpleName();
	private PeerConnection connection;

	public OfferObserver(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		Log.i(TAG, String.format("onCreateSuccess, %s", session.type.canonicalForm()));

		connection.setLocalDescription(this, session);
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, String.format("onCreateFailure, %s", error));
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "onSetSuccess, Pool size: " + PeerConnectionPool.getInstance().size());

		PeerConnectionPool.getInstance().enqueue(connection);
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, String.format("onSetFailure, %s", error));

		connection.close();
	}
}