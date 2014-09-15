package kr.ac.gachon.clo;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIOException;

import org.json.JSONObject;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

public class NodeHandler implements IOCallback {

	private PeerConnection connection;
	private NodeClient nodeClient;

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		JSONObject data = (JSONObject)args[0];

		try {
			if("login".equals(event)) {
				System.out.println(event);
				System.out.println(data.getInt("ret"));

				return;
			}

			if("chat".equals(event)) {
				System.out.println(event);

				return;
			}

			if("join".equals(event)) {
				System.out.println(event);

				return;
			}

			if("withdraw".equals(event)) {
				System.out.println(event);

				return;
			}

			if("answer".equals(event)) {
				if(connection == null) {
					return;
				}

				SessionDescription.Type type = "offer".equals(data.getString("type")) ? Type.OFFER : Type.ANSWER;
				SessionDescription sdp = new SessionDescription(type, data.getString("desc"));

				connection.setRemoteDescription(new CreateOfferCallback(connection, nodeClient), sdp);

				System.out.println(event);
				return;
			}

			throw new Exception(String.format("Unknown event: %s", event));
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void onMessage(String event, IOAcknowledge ack) {
		System.out.println("event: " + event + ", ack: " + ack.toString());
	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {
		System.out.println("json: " + json.toString() + ", ack: " + ack.toString());
	}

	@Override
	public void onConnect() {
		System.out.println("connect");
	}

	@Override
	public void onDisconnect() {
		System.out.println("disconnect");
	}

	@Override
	public void onError(SocketIOException e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
	}

	public PeerConnection getConnection() {
		return connection;
	}

	public void setConnection(PeerConnection connection) {
		this.connection = connection;
	}

	public NodeClient getNodeClient() {
		return nodeClient;
	}

	public void setNodeClient(NodeClient nodeClient) {
		this.nodeClient = nodeClient;
	}
}