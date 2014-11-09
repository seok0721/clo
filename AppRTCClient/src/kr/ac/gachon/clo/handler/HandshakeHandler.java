package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.event.EventHandler;
import kr.ac.gachon.clo.observer.RemoteOfferObserver;
import kr.ac.gachon.clo.service.PeerConnectionGenerator;
import kr.ac.gachon.clo.service.SocketService;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.util.Log;

public class HandshakeHandler implements EventHandler {

	private static final String TAG = HandshakeHandler.class.getSimpleName();
	private static final String EVENT = "handshake";
	private String viewer;

	@Override
	public void onMessage(JSONObject data) {
		try {
			String channel = data.getString("channel");
			String viewer = data.getString("viewer");
			String sdp = data.getString("sdp");

			if(!Global.getChannel().equals(channel)) {
				return;
			}

			this.viewer = viewer;

			PeerConnection connection = PeerConnectionGenerator.getInstance().createPeerConnection();
			connection.setRemoteDescription(new RemoteOfferObserver(connection, this), new SessionDescription(Type.OFFER, sdp));
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	public void handshake(String sdp) {
		SocketService.getInstance().handshake(viewer, sdp);
	}

	public String getViewer() {
		return viewer;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}
}