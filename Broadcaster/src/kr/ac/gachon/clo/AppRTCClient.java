package kr.ac.gachon.clo;

import java.util.LinkedList;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnectionFactory;

import android.util.Log;

public class AppRTCClient {

	private static final String TAG = AppRTCClient.class.getSimpleName();
	private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
	private PeerConnectionFactory factory = new PeerConnectionFactory();
	private PeerConnection conn1;
	private MediaConstraints pcConstraints = new MediaConstraints();

	public AppRTCClient() {
		initIceServers();

		pcConstraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));

		conn1 = factory.createPeerConnection(iceServers, pcConstraints, new PeerConnection.Observer() {

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
			public void onIceCandidate(IceCandidate arg0) {
				Log.d(TAG, arg0.sdp);
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
		});
		conn1.createOffer(new SdpObserverImpl(), pcConstraints);
	}

	private void initIceServers() {
		iceServers.add(new IceServer("stun.l.google.com:19302"));
		iceServers.add(new IceServer("stun1.l.google.com:19302"));
		iceServers.add(new IceServer("stun2.l.google.com:19302"));
		iceServers.add(new IceServer("stun3.l.google.com:19302"));
		iceServers.add(new IceServer("stun4.l.google.com:19302"));
	}
}