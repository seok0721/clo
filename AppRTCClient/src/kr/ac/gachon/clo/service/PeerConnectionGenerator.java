package kr.ac.gachon.clo.service;

import java.util.Calendar;

import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.observer.PeerConnectionObserver;
import kr.ac.gachon.clo.webrtc.IceServers;
import kr.ac.gachon.clo.webrtc.SrtpMediaConstraints;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.app.Activity;
import android.util.Log;

public class PeerConnectionGenerator {

	private static final String TAG = PeerConnectionGenerator.class.getSimpleName();
	private static PeerConnectionGenerator instance;
	private PeerConnectionFactory factory;
	private DeviceCapturer2 deviceCapturer;

	public static void setup(Activity activity) {
		PeerConnectionFactory.initializeAndroidGlobals(activity, true, true);

		instance = new PeerConnectionGenerator();
	}

	public static PeerConnectionGenerator getInstance() {
		return instance;
	}

	public PeerConnection createPeerConnection() {
		Log.i(TAG, "커넥션을 생성합니다.");

		HandshakeHandler handler = new HandshakeHandler();
		PeerConnectionObserver observer = new PeerConnectionObserver();
		PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
		connection.addStream(deviceCapturer.getMediaStream(), new MediaConstraints());
		observer.setPeerConnection(connection, handler);

		return connection;
	}

	public void close() {
		deviceCapturer.close();
	}

	private PeerConnectionGenerator() {
		factory = new PeerConnectionFactory();
		deviceCapturer = new DeviceCapturer2(factory);
	}

	private static class DeviceCapturer2 {

		private static final String TAG = DeviceCapturer2.class.getSimpleName();
		private static final String DEVICE_NAME = "Camera 0, Facing back, Orientation 90";
		private static final String LABEL = "CLO";
		private VideoRenderer videoRenderer;
		private MediaStream mediaStream;
		private VideoCapturer videoCapturer;
		private VideoSource videoSource;
		private AudioSource audioSource;
		private VideoTrack videoTrack;
		private AudioTrack audioTrack;
		private Callbacks videoRendererCallback;
		private long serialNumber;

		public DeviceCapturer2(PeerConnectionFactory factory) {
			Log.i(TAG, "새 멀티미디어 스트림을 생성합니다.");

			videoCapturer = VideoCapturer.create(DEVICE_NAME);

			serialNumber = Calendar.getInstance().getTimeInMillis();

			mediaStream = factory.createLocalMediaStream(String.format("%s%d", LABEL, serialNumber));

			audioSource = factory.createAudioSource(new MediaConstraints());
			audioTrack = factory.createAudioTrack(String.format("%sa%d", LABEL, serialNumber), audioSource);
			mediaStream.addTrack(audioTrack);

			videoRendererCallback = VideoRendererGui.create(0, 0, 100, 100);
			videoRenderer = new VideoRenderer(videoRendererCallback);
			videoSource = factory.createVideoSource(videoCapturer, new MediaConstraints());

			videoTrack = factory.createVideoTrack(String.format("%sv%d", LABEL, serialNumber), videoSource);
			videoTrack.addRenderer(videoRenderer);
			mediaStream.addTrack(videoTrack);
		}

		public MediaStream getMediaStream() {
			return mediaStream;
		}

		public void close() {
			Log.i(TAG, "멀티미디어 스트림을 종료합니다.");

			for(AudioTrack track : mediaStream.audioTracks) {
				mediaStream.removeTrack(track);
			}

			for(VideoTrack track : mediaStream.videoTracks) {
				mediaStream.removeTrack(track);
			}

			videoSource.stop();
		}
	}
}