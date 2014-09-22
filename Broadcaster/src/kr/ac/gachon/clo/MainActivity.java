package kr.ac.gachon.clo;

import io.socket.SocketIO;

import java.net.MalformedURLException;
import java.util.LinkedList;

import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private PeerConnectionFactory connectionFactory;
	private PeerConnection connection;
	private CameraView cameraView;
	private VideoSource videoSource;
	private VideoTrack videoTrack;
	private VideoCapturer videoCapture;
	private MediaStream localMediaStream;
	private String mediaStreamLabel = "cielo"; // Desired local media stream
	private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
	private NodeClient nodeClient = new NodeClient();
	private NodeHandler nodeHandler = new NodeHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initNodeClient();
		initWindow();
		initCameraView();

		setupPeerConnectionFactory();

		// createMediaStream();

		setupPeerConnection();
	}

	private void initNodeClient() {
		try {
			nodeClient.setCallback(nodeHandler);
			nodeClient.setSocket(new SocketIO("http://211.189.20.193:10080/"));
			nodeClient.connect();
			nodeClient.login("seok0721@gmail.com", "0000");
			nodeClient.create("news");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void initWindow() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void initCameraView() {
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(screenSize);

		cameraView = new CameraView(this, screenSize);

		VideoRendererGui.setView(cameraView);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(cameraView);
	}

	private void setupPeerConnectionFactory() {
		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		connectionFactory = new PeerConnectionFactory();
	}

	private void createMediaStream() {
		localMediaStream = connectionFactory.createLocalMediaStream(mediaStreamLabel);
		// videoCapture = VideoCapturer.create("Camera 1, Facing front, Orientation 270");
		videoCapture = VideoCapturer.create("Camera 0, Facing back, Orientation 90");
		videoSource = connectionFactory.createVideoSource(videoCapture, new MediaConstraints());
		videoTrack = connectionFactory.createVideoTrack(String.format("%sv0", mediaStreamLabel), videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100)));
		localMediaStream.addTrack(videoTrack);
	}

	private void setupPeerConnection() {
		CreatePeerConnectionCallback callback = new CreatePeerConnectionCallback(null);
		connection = connectionFactory.createPeerConnection(iceServers, new MediaConstraints(), callback);
		initIceServers();
		nodeHandler.setConnection(connection);
		callback.setConnection(connection);
		connection.updateIce(iceServers, new MediaConstraints());
		// connection.addStream(localMediaStream, new MediaConstraints());
		connection.createOffer(new CreateOfferCallback(connection, nodeClient), new MediaConstraints());
	}

	private void initIceServers() {
		iceServers.add(new IceServer("stun:stun.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun1.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun2.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun3.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun4.l.google.com:19302"));
	}
}
