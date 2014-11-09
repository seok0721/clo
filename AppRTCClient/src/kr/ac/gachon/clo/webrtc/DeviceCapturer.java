package kr.ac.gachon.clo.webrtc;

import java.util.Calendar;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import android.bluetooth.BluetoothClass.Device;
import android.util.Log;

public class DeviceCapturer {

	private static final String TAG = DeviceCapturer.class.getSimpleName();
	private static final String DEVICE_NAME = "Camera 0, Facing back, Orientation 90";
	private static final String LABEL = "CLO";
	private static DeviceCapturer instance = new DeviceCapturer();
	private static PeerConnectionFactory factory;
	private boolean isOpened = false;
	private VideoRenderer videoRenderer;
	private MediaStream mediaStream;
	private VideoCapturer videoCapturer;
	private VideoSource videoSource;
	private AudioSource audioSource;
	private VideoTrack videoTrack;
	private AudioTrack audioTrack;
	private Callbacks videoRendererCallback;
	private long serialNumber;

	public static void setPeerConnectionFactory(PeerConnectionFactory factory) {
		DeviceCapturer.factory = factory;
	}

	public MediaStream getMediaStream() {
		if(mediaStream == null) {
			isOpened = true;

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

		return mediaStream;
	}

	public void release() {
		if(!isOpened) {
			Log.i(TAG, "이미 멀티미디어 스트림은 종료되었습니다.");
			return;
		}

		Log.i(TAG, "멀티미디어 스트림을 종료합니다.");

		for(AudioTrack track : mediaStream.audioTracks) {
			mediaStream.removeTrack(track);
		}

		for(VideoTrack track : mediaStream.videoTracks) {
			mediaStream.removeTrack(track);
		}

		videoSource.stop();
	}

	private DeviceCapturer() {

	}
}