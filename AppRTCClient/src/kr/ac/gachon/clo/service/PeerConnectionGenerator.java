package kr.ac.gachon.clo.service;

import kr.ac.gachon.clo.DeviceCapturer;
import kr.ac.gachon.clo.IceServers;
import kr.ac.gachon.clo.SrtpMediaConstraints;
import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.observer.PeerConnectionObserver;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import android.util.Log;

public class PeerConnectionGenerator implements Runnable {

	private static final String TAG = PeerConnectionGenerator.class.getSimpleName();
	private static PeerConnectionGenerator instance = new PeerConnectionGenerator();
	private PeerConnectionFactory factory = new PeerConnectionFactory();
	private Thread thread;


	public static PeerConnectionGenerator getInstance() {
		return instance;
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			HandshakeHandler handler = new HandshakeHandler();
			PeerConnectionObserver observer = new PeerConnectionObserver();
			PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
			connection.addStream(DeviceCapturer.getInstance().getMediaStream(), new MediaConstraints());
			observer.setPeerConnection(connection, handler);

			SocketService.getInstance().addEventHandler(handler);

			PeerConnectionPool.getInstance().addConnection(connection);
		}

		Log.i(TAG, "모든 연결을 종료합니다.");

		PeerConnectionPool.getInstance().release();
	}

	private PeerConnectionGenerator() {
		DeviceCapturer.getInstance().setPeerConnectionFactory(factory);
	}
}