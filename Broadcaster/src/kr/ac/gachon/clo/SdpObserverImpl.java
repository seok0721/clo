package kr.ac.gachon.clo;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import android.util.Log;

public class SdpObserverImpl implements SdpObserver {

	private static final String TAG = SdpObserverImpl.class.getSimpleName();

	@Override
	public void onCreateFailure(String arg0) {
		Log.d(TAG, arg0);
	}

	@Override
	public void onCreateSuccess(SessionDescription arg0) {
		Log.d(TAG, arg0.type.canonicalForm());
		Log.d(TAG, arg0.description);
	}

	@Override
	public void onSetFailure(String arg0) {
		Log.d(TAG, arg0);
	}

	@Override
	public void onSetSuccess() {
		Log.d(TAG, "onSetSuccess");
	}
}
