package kr.ac.gachon.clo;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.util.Log;

public class CreateOfferCallback implements SdpObserver {

	private static final String TAG = CreateOfferCallback.class.getSimpleName();
	private PeerConnection connection;
	private NodeClient nodeClient;
	private SessionDescription desc;

	public CreateOfferCallback(PeerConnection connection, NodeClient nodeClient) {
		this.connection = connection;
		this.nodeClient = nodeClient;
	}

	@Override
	public void onCreateSuccess(final SessionDescription desc) {
		if(desc.type != Type.OFFER) {
			return;
		}

		Log.d(TAG, desc.type.canonicalForm());
		Log.d(TAG, desc.description);

		this.desc = desc;

		System.out.println(desc);
		System.out.println(this);
		connection.setLocalDescription(this, desc);

		new Thread(new Runnable() {

			@Override
			public void run() {
				JSONObject json = new JSONObject();
				try {
					json.put("type", desc.type.canonicalForm());
					json.put("desc", desc.description);

					for(;;) {
						nodeClient.offer(json.toString());
						Thread.sleep(5000);
					}
				} catch(Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}

	@Override
	public void onCreateFailure(String message) {
		Log.e(TAG, message);
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