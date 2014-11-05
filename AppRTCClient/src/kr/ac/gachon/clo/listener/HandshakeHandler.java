package kr.ac.gachon.clo.listener;

import kr.ac.gachon.clo.DeviceCapturer;
import kr.ac.gachon.clo.IceServers;
import kr.ac.gachon.clo.PeerConnectionGenerator;
import kr.ac.gachon.clo.SocketService;
import kr.ac.gachon.clo.SrtpMediaConstraints;
import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.observer.PeerConnectionObserver;
import kr.ac.gachon.clo.observer.RemoteOfferObserver;

import org.json.JSONObject;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.app.Activity;
import android.util.Log;

public class HandshakeHandler implements Worker {

	private static final String TAG = HandshakeHandler.class.getSimpleName();
	private static final String EVENT = "handshake";
	private String viewer;
	private String channel;

	@Override
	public void onMessage(JSONObject data) {
		try {
			String channel = data.getString("channel");
			String sdp = data.getString("sdp");

			if(this.channel == null) {
				this.channel = channel;
			}

			if(!this.channel.equals(channel)) {
				return;
			}

			this.viewer = data.getString("viewer");

			PeerConnectionObserver observer = new PeerConnectionObserver();
			PeerConnectionFactory factory = PeerConnectionGenerator.getInstance().getFactory();
			PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
			observer.setPeerConnection(connection, this);
			SocketService.getInstance().addWorker(this);

			connection.addStream(DeviceCapturer.getInstance(factory).getMediaStream(), new MediaConstraints());
			connection.setRemoteDescription(new RemoteOfferObserver(connection, this), new SessionDescription(Type.OFFER, sdp));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public Activity getActivity() {
		return null;
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	public void handshake(String sdp) {
		SocketService.getInstance().handshake(viewer, channel, sdp);
	}
}