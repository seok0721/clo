package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.PeerConnectionGenerator;
import kr.ac.gachon.clo.PeerConnectionPool;

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
		Log.i(TAG, "onSetSuccess, PC: " + connection);

		PeerConnectionPool.getInstance().accumulate(connection);

		PeerConnectionGenerator.getInstance().orderToCreateConnection();
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, String.format("onSetFailure, %s", error));

		PeerConnectionPool.getInstance().remove(connection);
	}
}