package kr.ac.gachon.clo;

import java.util.Calendar;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class DeviceCapturer {

	private static final String TAG = DeviceCapturer.class.getSimpleName();
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

	public static DeviceCapturer getInstance(PeerConnectionFactory factory) {
		if(instance == null) {
			instance = new DeviceCapturer(factory);
		}

		return instance;
	}

	public MediaStream getMediaStream() {
//		long serialNumber = Calendar.getInstance().getTimeInMillis();

//		MediaStream mediaStream = factory.createLocalMediaStream(String.format("%s%d", LABEL, serialNumber));
//
//		videoTrack = factory.createVideoTrack(String.format("%sv%d", LABEL, serialNumber), videoSource);
//		videoTrack.addRenderer(videoRenderer);
//		mediaStream.addTrack(videoTrack);
//
//		audioTrack = factory.createAudioTrack(String.format("%sa%d", LABEL, serialNumber), audioSource);
//		mediaStream.addTrack(audioTrack);

		return mediaStream;
	}

	private DeviceCapturer(PeerConnectionFactory factory) {
		this.factory = factory;

		videoCapturer = VideoCapturer.create(DEVICE_NAME);
		videoSource = factory.createVideoSource(videoCapturer, new MediaConstraints());
		videoRenderer = new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100));
		audioSource = factory.createAudioSource(new MediaConstraints());
		long serialNumber = Calendar.getInstance().getTimeInMillis();
		mediaStream = factory.createLocalMediaStream(String.format("%s%d", LABEL, serialNumber));

		videoTrack = factory.createVideoTrack(String.format("%sv%d", LABEL, serialNumber), videoSource);
		videoTrack.addRenderer(videoRenderer);
		mediaStream.addTrack(videoTrack);

		audioTrack = factory.createAudioTrack(String.format("%sa%d", LABEL, serialNumber), audioSource);
		mediaStream.addTrack(audioTrack);

	}

	/*
	public MediaStream getMediaStream() {
		return mediaStream;
	}

	private DeviceCapturer(PeerConnectionFactory factory) {
		this.factory = factory;

		Log.i(TAG, "Create media stream...");
		mediaStream = factory.createLocalMediaStream(LABEL);

		Log.i(TAG, "Initialize video track...");
		initVideoTrack();

		Log.i(TAG, "Initialize audio track...");
		initAudioTrack();

		Log.i(TAG, "Set audio & video...");
		mediaStream.addTrack(videoTrack);
		mediaStream.addTrack(audioTrack);
	}

	private void initVideoTrack() {
		Log.i(TAG, "Create video capturer...");
		VideoCapturer videoCapturer = VideoCapturer.create(DEVICE_NAME);

		Log.i(TAG, "Create video source...");
		VideoSource videoSource = factory.createVideoSource(videoCapturer, new MediaConstraints());

		Log.i(TAG, "Create video track...");
		videoTrack = factory.createVideoTrack(String.format("%sv0", LABEL), videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100)));
	}

	private void initAudioTrack() {
		AudioSource audioSource = factory.createAudioSource(new MediaConstraints());

		audioTrack = factory.createAudioTrack(String.format("%sa0", LABEL), audioSource);
	}
	 */
}