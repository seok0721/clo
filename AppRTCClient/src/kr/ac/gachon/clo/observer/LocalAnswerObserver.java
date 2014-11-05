package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.listener.HandshakeHandler;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class LocalAnswerObserver implements SdpObserver {

	private static final String TAG = LocalAnswerObserver.class.getSimpleName();
	private PeerConnection connection;
	private HandshakeHandler handler;
	private SessionDescription session;

	public LocalAnswerObserver(PeerConnection connection, HandshakeHandler handler) {
		this.connection = connection;
		this.handler = handler;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		this.session = session;

		connection.setLocalDescription(this, session);
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}
}