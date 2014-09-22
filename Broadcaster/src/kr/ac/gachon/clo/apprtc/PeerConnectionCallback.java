package kr.ac.gachon.clo.apprtc;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.SignalingState;

import android.util.Log;

public class PeerConnectionCallback implements PeerConnection.Observer {

	private static final String TAG = PeerConnectionCallback.class.getSimpleName();
	private PeerConnection connection;

	@Override
	public void onSignalingChange(SignalingState state) {
		Log.d(TAG, "onSignalingChange");
		Log.d(TAG, state.name());
	}

	@Override
	public void onRenegotiationNeeded() {
		Log.d(TAG, "onRenegotiationNeeded");
	}

	@Override
	public void onRemoveStream(MediaStream media) {
		Log.d(TAG, "onRemoveStream");
		Log.d(TAG, media.label());
	}

	@Override
	public void onIceGatheringChange(IceGatheringState state) {
		Log.d(TAG, "onIceGatheringChange");
		Log.d(TAG, state.name());
	}

	@Override
	public void onIceConnectionChange(IceConnectionState state) {
		Log.d(TAG, "onIceConnectionChange");
		Log.d(TAG, state.name());
	}

	@Override
	public void onIceCandidate(IceCandidate iceCandidate) {
		Log.d(TAG, "onIceCandidate(IceCandidate");
		Log.d(TAG, iceCandidate.sdp);

		connection.addIceCandidate(iceCandidate);
	}

	@Override
	public void onError() {
		Log.d(TAG, "onError");
	}

	@Override
	public void onDataChannel(DataChannel arg0) {
		Log.d(TAG, arg0.label());
	}

	@Override
	public void onAddStream(MediaStream media) {
		Log.d(TAG, media.label());
	}

	public PeerConnection getConnection() {
		return connection;
	}

	public void setConnection(PeerConnection connection) {
		this.connection = connection;
	}
}