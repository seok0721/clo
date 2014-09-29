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

public class DeviceCapturer {

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

		initVideoTrack();
		initAudioTrack();

		mediaStream = factory.createLocalMediaStream(LABEL);
		mediaStream.addTrack(videoTrack);
		mediaStream.addTrack(audioTrack);
	}

	private void initVideoTrack() {
		VideoCapturer videoCapturer = VideoCapturer.create(DEVICE_NAME);
		VideoSource videoSource = factory.createVideoSource(videoCapturer, new MediaConstraints());

		videoTrack = factory.createVideoTrack(String.format("%sv0", LABEL), videoSource);
		videoTrack.addRenderer(new VideoRenderer(VideoRendererGui.create(0, 0, 100, 100)));
	}

	private void initAudioTrack() {
		AudioSource audioSource = factory.createAudioSource(new MediaConstraints());

		audioTrack = factory.createAudioTrack(String.format("%sa0", LABEL), audioSource);
	}
}