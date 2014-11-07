package kr.ac.gachon.clo.observer;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class LocalAnswerObserver implements SdpObserver {

	private static final String TAG = LocalAnswerObserver.class.getSimpleName();
	private PeerConnection connection;

	public LocalAnswerObserver(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onCreateSuccess(SessionDescription session) {
		connection.setLocalDescription(this, session);
	}

	@Override
	public void onCreateFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "방송자의 세션 교환이 완료되었습니다.");
	}

	@Override
	public void onSetFailure(String error) {
		Log.i(TAG, "핸들링 하지 않습니다.");
	}
}