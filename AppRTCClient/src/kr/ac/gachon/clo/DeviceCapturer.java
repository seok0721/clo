package kr.ac.gachon.clo;

import java.util.Calendar;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class DeviceCapturer {

	// private static final String TAG = DeviceCapturer.class.getSimpleName();
	private static final String DEVICE_NAME = "Camera 0, Facing back, Orientation 90";
	private static final String LABEL = "CLO";
	private static DeviceCapturer instance;
	private PeerConnectionFactory factory;
	private MediaStream mediaStream;
	private VideoCapturer videoCapturer;
	private VideoSource videoSource;
	private AudioSource audioSource;
	private VideoTrack videoTrack;
	private AudioTrack audioTrack;
	private VideoRenderer videoRenderer;
	// private Callbacks videoRendererCallback;
	private long serialNumber;

	public static void setPeerConnectionFactory(PeerConnectionFactory factory) {
		if(instance == null) {
			instance = new DeviceCapturer();
		}

		instance.factory = factory;
	}

	public static DeviceCapturer getInstance() {
		if(instance == null) {
			instance = new DeviceCapturer();
		}

		return instance;
	}

	public MediaStream getMediaStream() {
		return mediaStream;
	}

	private DeviceCapturer() {
		serialNumber = Calendar.getInstance().getTimeInMillis();

		mediaStream = factory.createLocalMediaStream(String.format("%s%d", LABEL, serialNumber));

		// videoRendererCallback = VideoRendererGui.create(0, 0, 100, 100);
		// videoRenderer = new VideoRenderer(videoRendererCallback);
		videoCapturer = VideoCapturer.create(DEVICE_NAME);
		videoSource = factory.createVideoSource(videoCapturer, new MediaConstraints());
		videoTrack = factory.createVideoTrack(String.format("%sv%d", LABEL, serialNumber), videoSource);
		videoTrack.addRenderer(videoRenderer);
		mediaStream.addTrack(videoTrack);

		audioSource = factory.createAudioSource(new MediaConstraints());
		audioTrack = factory.createAudioTrack(String.format("%sa%d", LABEL, serialNumber), audioSource);
		mediaStream.addTrack(audioTrack);
	}
}