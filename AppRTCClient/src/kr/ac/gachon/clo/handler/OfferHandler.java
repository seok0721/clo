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

public class OfferHandler implements EventHandler {

	private static final String TAG = OfferHandler.class.getSimpleName();
	private static final String EVENT = "offer";
	private String socketId;

	@Override
	public void onMessage(JSONObject data) {
		try {
			String email = data.getString("email");
			String socketId = data.getString("socketId");
			String sdp = data.getString("sdp");

			if(!Global.getEmail().equals(email)) {
				return;
			}

			this.socketId = socketId;

			SocketService.getInstance().removeEventHandler(this);
			SocketService.getInstance().addEventHandler(new OfferHandler());

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
		SocketService.getInstance().answer(socketId, sdp);
	}

	public String getViewer() {
		return socketId;
	}

	public void setViewer(String viewer) {
		this.socketId = viewer;
	}
}