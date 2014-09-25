package kr.ac.gachon;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private PeerConnection connection;
	private SdpObserver sdpObserver;
	private SocketIO socket;
	private MediaStream mediaStream;
	private PeerConnectionFactory factory;
	private CameraView cameraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			start();
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void initCameraView() {
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(screenSize);

		cameraView = new CameraView(this, screenSize);

		VideoRendererGui.setView(cameraView);

		setContentView(cameraView);
	}

	private void start() throws Exception {
		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);
		factory = new PeerConnectionFactory();

		initCameraView();

		mediaStream = factory.createLocalMediaStream("CLO");
		VideoCapturer videoCapture = VideoCapturer.create("Camera 0, Facing back, Orientation 90");
		VideoSource videoSource = factory.createVideoSource(videoCapture, new MediaConstraints());
		VideoTrack videoTrack = factory.createVideoTrack("CLOv0", videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100)));
		mediaStream.addTrack(videoTrack);

		List<IceServer> iceServers = new LinkedList<IceServer>();
		iceServers.add(new IceServer("stun:stun.l.google.com:19302"));
		sdpObserver = new SdpObserverImpl();
		MediaConstraints constraint = new MediaConstraints();
		constraint.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
		connection = factory.createPeerConnection(iceServers, constraint, new ObserverImpl());
		connection.addStream(mediaStream, new MediaConstraints());
		socket = new SocketIO("http://211.189.20.193:10080");
		socket.connect(new IOCallbackImpl());
	}

	class ObserverImpl implements Observer {

		@Override
		public void onSignalingChange(SignalingState arg0) {

		}

		@Override
		public void onRenegotiationNeeded() {

		}

		@Override
		public void onRemoveStream(MediaStream arg0) {

		}

		@Override
		public void onIceGatheringChange(IceGatheringState arg0) {

		}

		@Override
		public void onIceConnectionChange(IceConnectionState arg0) {

		}

		@Override
		public void onIceCandidate(IceCandidate candidate) {
			connection.addIceCandidate(candidate);
		}

		@Override
		public void onError() {

		}

		@Override
		public void onDataChannel(DataChannel arg0) {

		}

		@Override
		public void onAddStream(MediaStream arg0) {

		}
	}

	class SdpObserverImpl implements SdpObserver {

		@Override
		public void onCreateFailure(String arg0) {

		}

		@Override
		public void onCreateSuccess(SessionDescription session) {
			connection.setLocalDescription(this, session);

			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						while(connection.iceGatheringState() != IceGatheringState.COMPLETE) {
							Log.d(TAG, "iceGatheringState...");

							Thread.sleep(500);
						}

						while(connection.getLocalDescription() == null) {
							Log.d(TAG, "getLocalDescription...");

							Thread.sleep(500);
						}

						JSONObject json = new JSONObject();
						json.put("sdp", connection.getLocalDescription().description);

						socket.emit("offer", json);
					} catch(Exception e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}).start();
		}

		@Override
		public void onSetFailure(String error) {
			Log.i(TAG, "onSetFailure");
			Log.i(TAG, error);
		}

		@Override
		public void onSetSuccess() {
			Log.i(TAG, "onSetSuccess");
		}
	}

	class IOCallbackImpl implements IOCallback {

		@Override
		public void on(String event, IOAcknowledge ack, Object... param) {
			try {
				if("answer".equals(event)) {
					JSONObject json = (JSONObject)param[0];
					SessionDescription session = new SessionDescription(Type.ANSWER, json.getString("sdp"));

					connection.setRemoteDescription(sdpObserver, session);

					Log.i(TAG, "Receive answer description.");
				}
			} catch(Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

		@Override
		public void onConnect() {
			connection.createOffer(sdpObserver, new MediaConstraints());
		}

		@Override
		public void onDisconnect() {

		}

		@Override
		public void onError(SocketIOException arg0) {

		}

		@Override
		public void onMessage(String arg0, IOAcknowledge arg1) {

		}

		@Override
		public void onMessage(JSONObject arg0, IOAcknowledge arg1) {

		}
	}

	class CameraView extends GLSurfaceView {

		private Point screenSize;

		public CameraView(Context context, Point screenPoint) {
			super(context);

			this.screenSize = screenPoint;
		}

		public void updateScreenSize(Point screenPoint) {
			this.screenSize = screenPoint;
		}

		@Override
		protected void onMeasure(int unusedX, int unusedY) {
			setMeasuredDimension(screenSize.x, screenSize.y);
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();

			setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION |
					SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
}
