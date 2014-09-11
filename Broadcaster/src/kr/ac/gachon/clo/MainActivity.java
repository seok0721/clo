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
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private PeerConnectionFactory connectionFactory;
	private PeerConnection connection;
	private CameraView cameraView;
	private VideoSource videoSource;
	private VideoTrack videoTrack;
	private VideoCapturer videoCapture;
	private MediaStream mediaStream;
	private String mediaStreamLabel = "cielo"; // Desired local media stream
	private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initWindow();
		initCameraView();
		initIceServers();

		setupPeerConnectionFactory();

		createMediaStream();

		setupPeerConnection();
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
		mediaStream = connectionFactory
				.createLocalMediaStream(mediaStreamLabel);
		videoCapture = VideoCapturer.create("");

		videoSource = connectionFactory.createVideoSource(videoCapture,
				new MediaConstraints());
		videoTrack = connectionFactory.createVideoTrack(
				String.format("%sv0", mediaStreamLabel), videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0,
				100, 100)));
		mediaStream.addTrack(videoTrack);
	}

	private void setupPeerConnection() {
		connection = connectionFactory.createPeerConnection(iceServers,
				new MediaConstraints(), new PeerConnection.Observer() {

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
			public void onIceCandidate(IceCandidate iceCandidate) {
				Log.d(TAG, iceCandidate.sdp);
				connection.addIceCandidate(iceCandidate);
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

		try {
			connection.addStream(mediaStream, new MediaConstraints());
			SdpObserver offerObserver = new SdpObserver() {

				@Override
				public void onSetSuccess() {
					Log.d(TAG, "onSetSuccess");
				}

				@Override
				public void onSetFailure(String error) {
					Log.d(TAG, "onSetFailure");
					Log.d(TAG, error);
				}

				@Override
				public void onCreateSuccess(SessionDescription sessionDescription) {
					Log.d(TAG, "onCreateSuccess");
					Log.d(TAG, sessionDescription.description);

					connection.setLocalDescription(this, sessionDescription);
				}

				@Override
				public void onCreateFailure(String error) {
					Log.d(TAG, "onCreateFailure");
					Log.d(TAG, error);
				}
			};
			connection.createOffer(offerObserver, new MediaConstraints());
			SdpObserver answerObserver = new SdpObserver() {

				@Override
				public void onSetSuccess() {
					Log.d(TAG, "onSetSuccess");
				}

				@Override
				public void onSetFailure(String error) {
					Log.d(TAG, "onSetFailure");
					Log.d(TAG, error);
				}

				@Override
				public void onCreateSuccess(SessionDescription sessionDescription) {
					Log.d(TAG, "onCreateSuccess");
					Log.d(TAG, sessionDescription.description);

					connection.setRemoteDescription(this, sessionDescription);
				}

				@Override
				public void onCreateFailure(String error) {
					Log.d(TAG, "onCreateFailure");
					Log.d(TAG, error);
				}
			};
			connection.createAnswer(answerObserver, new MediaConstraints());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void initIceServers() {
		iceServers.add(new IceServer("stun:stun.l.google.com:19302"));
		// iceServers.add(new IceServer("stun:stun1.l.google.com:19302"));
		// iceServers.add(new IceServer("stun:stun2.l.google.com:19302"));
		// iceServers.add(new IceServer("stun:stun3.l.google.com:19302"));
		// iceServers.add(new IceServer("stun:stun4.l.google.com:19302"));
	}
}
