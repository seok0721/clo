package kr.ac.gachon.clo.apprtc.impl;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class AnswerObserver implements SdpObserver {

	private static final String TAG = AnswerObserver.class.getSimpleName();
	private PeerConnection connection; 

	public void setPeerConnection(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		Log.i(TAG, "This observer does not create connection.");
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, "This observer does not create connection.");
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "onSetSuccess");

		PeerConnectionPool.getInstance().accumulate(connection);
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, String.format("onSetFailure, %s", error));

		connection.dispose();
	}
}