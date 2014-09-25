package kr.ac.gachon.clo;

import java.util.LinkedList;
import java.util.List;

import kr.ac.gachon.clo.apprtc.OfferAnswerCallback;
import kr.ac.gachon.clo.apprtc.PeerConnectionCallback;
import kr.ac.gachon.clo.apprtc.util.Log;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.PeerConnection.IceServer;

public class ConnectTest {

	private static PeerConnectionFactory factory = new PeerConnectionFactory();

	public static void main(String[] args) throws Exception {
		List<IceServer> iceServers = new LinkedList<IceServer>();

		// createMediaStream();
		// PeerConnectionFactory factory = new PeerConnectionFactory();
		PeerConnectionCallback callback1 = new PeerConnectionCallback();
		PeerConnectionCallback callback2 = new PeerConnectionCallback();
		MediaConstraints m1 = new MediaConstraints();
		MediaConstraints m2 = new MediaConstraints();
		PeerConnection pc1 = factory.createPeerConnection(iceServers, m1, callback1);
		PeerConnection pc2 = factory.createPeerConnection(iceServers, m2, callback2);
		iceServers.add(new IceServer("stun:stun.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun1.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun2.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun3.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun4.l.google.com:19302"));
		pc1.updateIce(iceServers, new MediaConstraints());
		pc2.updateIce(iceServers, new MediaConstraints());

		pc1.updateIce(iceServers, new MediaConstraints());
		callback1.setConnection(pc1);
		callback2.setConnection(pc2);
		OfferAnswerCallback ocb1 = new OfferAnswerCallback(pc1);
		OfferAnswerCallback ocb2 = new OfferAnswerCallback(pc2);
		pc1.createOffer(ocb1, m1);

		Thread.sleep(1500);

		pc2.setRemoteDescription(ocb2, ocb1.getSessionDescription());
		pc2.createAnswer(ocb2, m2);

		Thread.sleep(1500);

		pc1.setRemoteDescription(ocb1, ocb2.getSessionDescription());

		Log.d("SDP", pc1.getLocalDescription().description);
		/*
		NodeClient client = new NodeClient();
		client.setCallback(new SocketHandler());
		client.setSocket(new SocketIO("http://211.189.20.193:10080/"));
		client.connect();

		Thread.sleep(1000);

		client.login("seok0721@gmail.com", "0000"); // socket.email

		Thread.sleep(500);

		client.create("news"); // socket.room

		Thread.sleep(500);

		// create peer connection
		// create offer
		// set local description

		Thread.sleep(500);

		client.offer("sdp info");
		Thread.sleep(500);

		// if receive answer
		//   set remote description
		//   create answer

		client.destroy();

		Thread.sleep(500);

		client.logout();

		Thread.sleep(1000);

		client.disconnect();
		 */
	}
}