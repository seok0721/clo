package kr.ac.gachon.clo;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.SignalingState;

import android.util.Log;

public class CreatePeerConnectionCallback implements PeerConnection.Observer {

	private static final String TAG = CreatePeerConnectionCallback.class.getSimpleName();
	private PeerConnection connection;

	public CreatePeerConnectionCallback(PeerConnection connection) {
		this.connection = connection;
	}

	@Override
	public void onSignalingChange(SignalingState arg0) {
		Log.d(TAG, arg0.name());
	}

	@Override
	public void onRenegotiationNeeded() {
		Log.d(TAG, "onRenegotiationNeeded");
	}

	@Override
	public void onRemoveStream(MediaStream arg0) {
		Log.d(TAG, arg0.label());
	}

	@Override
	public void onIceGatheringChange(IceGatheringState arg0) {
		Log.d(TAG, arg0.name());
	}

	@Override
	public void onIceConnectionChange(IceConnectionState arg0) {
		Log.d(TAG, arg0.name());
	}

	@Override
	public void onIceCandidate(IceCandidate iceCandidate) {
		Log.d(TAG, iceCandidate.sdp);

		if(connection == null) {
			return;
		}

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
	public void onAddStream(MediaStream arg0) {
		Log.d(TAG, arg0.label());
	}

	public PeerConnection getConnection() {
		return connection;
	}

	public void setConnection(PeerConnection connection) {
		this.connection = connection;
	}
}