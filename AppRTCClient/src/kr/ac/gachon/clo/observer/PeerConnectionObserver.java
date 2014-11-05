package kr.ac.gachon.clo.observer;

import kr.ac.gachon.clo.PeerConnectionPool;
import kr.ac.gachon.clo.listener.HandshakeHandler;

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

		// connection.updateIce(new IceServers(), new SrtpMediaConstraints());
	}

	@Override
	public void onAddStream(MediaStream mediaStream) {
		Log.i(TAG, String.format("onAddStream, %s", mediaStream.toString()));
	}

	@Override
	public void onRemoveStream(MediaStream mediaStream) {
		Log.i(TAG, String.format("onRemoveStream, %s", mediaStream.toString()));
	}

	@Override
	public void onSignalingChange(SignalingState signalingState) {
		Log.i(TAG, String.format("onSignalingChange, %s", signalingState.name()));

		if(signalingState == SignalingState.CLOSED) {
			PeerConnectionPool.getInstance().remove(connection);
		}
	}

	@Override
	public void onIceGatheringChange(IceGatheringState iceGatheringState) {
		Log.i(TAG, String.format("onIceGatheringChange, %s", iceGatheringState.name()));

		if(iceGatheringState == IceGatheringState.COMPLETE) {
			handler.handshake(connection.getLocalDescription().description);
			// SocketService.getInstance().push(connection.getLocalDescription());
		}
	}

	@Override
	public void onIceConnectionChange(IceConnectionState iceConnectionState) {
		Log.i(TAG, String.format("onIceConnectionChange, %s", iceConnectionState.name()));

		switch(iceConnectionState) {
		case FAILED:
		case DISCONNECTED:
		case CLOSED:
			PeerConnectionPool.getInstance().remove(connection);
			break;
		default:
			break;
		}
	}

	@Override
	public void onIceCandidate(IceCandidate candidate) {
		Log.i(TAG, String.format("onIceCandidate, %s", candidate.sdp));

		connection.addIceCandidate(candidate);
	}

	@Override
	public void onError() {
		Log.i(TAG, "onError");
	}

	@Override
	public void onDataChannel(DataChannel dataChannel) {
		Log.i(TAG, String.format("onDataChannel, %s", dataChannel.label()));
	}
}