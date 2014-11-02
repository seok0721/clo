package kr.ac.gachon.clo.apprtc;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kr.ac.gachon.clo.apprtc.util.NetworkUtils;
import kr.ac.gachon.clo.apprtc.util.SessionUtils;

import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class PeerConnectionPool {

	private static PeerConnectionPool instance = new PeerConnectionPool();
	private PeerConnectionFactory connectionFactory = new PeerConnectionFactory();
	private Set<PeerConnection> connectionSet = new HashSet<PeerConnection>();
	private MediaStream mediaStream;

	public static PeerConnectionPool getInstance() {
		return instance;
	}

	public PeerConnection getConnection() {
		Iterator<PeerConnection> iter = connectionSet.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();

			switch(connection.signalingState()) {
			case CLOSED:
				iter.remove();
				continue;
			case STABLE:
				return connection;
			default:
				break;
			}
		}

		return createConnection();
	}

	private PeerConnection createConnection() {
		lazyInitMediaStream();

		PeerConnectionCallback callback = new PeerConnectionCallback();
		PeerConnection connection = connectionFactory.createPeerConnection(NetworkUtils.getIceServers(), SessionUtils.getConstraint(), callback);
		connection.addStream(mediaStream, new MediaConstraints());
		callback.setConnection(connection);

		connectionSet.add(connection);

		return connection;
	}

	private void lazyInitMediaStream() {
		if(mediaStream != null) {
			return;
		}

		mediaStream = connectionFactory.createLocalMediaStream("CLO");
		// videoCapture = VideoCapturer.create("Camera 1, Facing front, Orientation 270");
		VideoCapturer videoCapture = VideoCapturer.create("Camera 0, Facing back, Orientation 90");
		VideoSource videoSource = connectionFactory.createVideoSource(videoCapture, SessionUtils.getConstraint());
		VideoTrack videoTrack = connectionFactory.createVideoTrack("CLOv0", videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100)));
		mediaStream.addTrack(videoTrack);
	}
}