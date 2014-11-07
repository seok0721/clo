package kr.ac.gachon.clo;

import java.util.Calendar;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack.State;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public class DeviceCapturer {

	// private static final String TAG = DeviceCapturer.class.getSimpleName();
	private static final String DEVICE_NAME = "Camera 0, Facing back, Orientation 90";
	private static final String LABEL = "CLO";
	private static DeviceCapturer instance = new DeviceCapturer();
	private VideoRenderer videoRenderer;
	private PeerConnectionFactory factory;
	private MediaStream mediaStream;
	private VideoCapturer videoCapturer;
	private VideoSource videoSource;
	private AudioSource audioSource;
	private VideoTrack videoTrack;
	private AudioTrack audioTrack;
	private Callbacks videoRendererCallback;
	private long serialNumber;

	public static DeviceCapturer getInstance() {
		return instance;
	}	

	public void setPeerConnectionFactory(PeerConnectionFactory factory) {
		this.factory = factory;
	}

	public MediaStream getMediaStream() {
		if(mediaStream == null) {
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

		return mediaStream;
	}

	public void release() {
		for(AudioTrack track : mediaStream.audioTracks) {
			track.setEnabled(false);
			track.setState(State.ENDED);
			track.dispose();
		}

		for(VideoTrack track : mediaStream.videoTracks) {
			track.removeRenderer(videoRenderer);
			track.setEnabled(false);
			track.setState(State.ENDED);
			track.dispose();
		}

		videoRenderer.dispose();

		videoSource.dispose();
		audioSource.dispose();

		mediaStream.dispose();
		mediaStream = null;
	}

	private DeviceCapturer() {
		videoCapturer = VideoCapturer.create(DEVICE_NAME);
	}
}