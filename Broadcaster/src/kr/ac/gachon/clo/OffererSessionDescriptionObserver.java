package kr.ac.gachon.clo;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class OffererSessionDescriptionObserver implements SdpObserver {

	private static final String TAG = OffererSessionDescriptionObserver.class.getSimpleName();
	private SessionDescription desc;

	@Override
	public void onCreateFailure(String message) {
		Log.e(TAG, message);
	}

	@Override
	public void onCreateSuccess(SessionDescription desc) {
		Log.d(TAG, desc.type.canonicalForm());
		Log.d(TAG, desc.description);

		this.desc = desc;
	}

	@Override
	public void onSetFailure(String message) {
		Log.e(TAG, message);
	}

	@Override
	public void onSetSuccess() {
		Log.i(TAG, "To create offerer session description successful.");
	}

	public SessionDescription getSessionDescription() {
		return desc;
	}
}