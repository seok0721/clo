package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.service.PeerConnectionPool;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;

import android.util.Log;

public class PeerConnectionObserver implements Observer {

	private static final String TAG = PeerConnectionObserver.class.getSimpleName();
	private PeerConnection connection;
	private HandshakeHandler handler;

	public void setPeerConnection(PeerConnection connection, HandshakeHandler handler) {
		this.connection = connection;
		this.handler = handler;
	}

	@Override
	public void onRenegotiationNeeded() {
		Log.i(TAG, "onRenegotiationNeeded");
	}

	@Override
	public void onAddStream(MediaStream mediaStream) {
		Log.i(TAG, "onAddStream");
	}

	@Override
	public void onRemoveStream(MediaStream mediaStream) {
		Log.i(TAG, "onRemoveStream");
	}

	@Override
	public void onSignalingChange(SignalingState signalingState) {
		Log.i(TAG, "onSignalingChange");

		if(connection.iceGatheringState() == IceGatheringState.COMPLETE && connection.signalingState() == SignalingState.STABLE) {
			handler.handshake(connection.getLocalDescription().description);
		}
	}

	@Override
	public void onIceGatheringChange(IceGatheringState iceGatheringState) {
		Log.i(TAG, "onIceGatheringChange");

		if(connection.iceGatheringState() == IceGatheringState.COMPLETE && connection.signalingState() == SignalingState.STABLE) {
			handler.handshake(connection.getLocalDescription().description);
		}
	}

	@Override
	public void onIceConnectionChange(IceConnectionState iceConnectionState) {
		Log.i(TAG, "onIceConnectionChange");
	}

	@Override
	public void onIceCandidate(IceCandidate candidate) {
		Log.i(TAG, "onIceCandidate");

		connection.addIceCandidate(candidate);
	}

	@Override
	public void onError() {
		Log.i(TAG, "onError");
	}

	@Override
	public void onDataChannel(DataChannel dataChannel) {
		Log.i(TAG, "onDataChannel");
	}
}