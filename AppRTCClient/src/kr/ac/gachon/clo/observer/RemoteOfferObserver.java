package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.listener.HandshakeHandler;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class RemoteOfferObserver implements SdpObserver {

	private static final String TAG = RemoteOfferObserver.class.getSimpleName();
	private PeerConnection connection;
	private HandshakeHandler handler;

	public RemoteOfferObserver(PeerConnection connection, HandshakeHandler handler) {
		this.connection = connection;
		this.handler = handler;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		connection.setLocalDescription(new LocalAnswerObserver(connection, handler), session);
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}

	@Override
	public void onSetSuccess() {
		connection.createAnswer(this, new MediaConstraints());
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}
}