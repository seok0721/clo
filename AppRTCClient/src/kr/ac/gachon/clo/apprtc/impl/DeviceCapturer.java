package kr.ac.gachon.clo.apprtc.impl;

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

import android.util.Log;

public class DeviceCapturer {

	private static final String TAG = DeviceCapturer.class.getSimpleName();
	private static final String DEVICE_NAME = "Camera 0, Facing back, Orientation 90";
	private static final String LABEL = "CLO";
	private static DeviceCapturer instance;
	private PeerConnectionFactory factory;
	private MediaStream mediaStream;
	private VideoTrack videoTrack;
	private AudioTrack audioTrack;

	public static DeviceCapturer getInstance(PeerConnectionFactory factory) {
		if(instance == null) {
			instance = new DeviceCapturer(factory);
		}

		return instance;
	}

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
}