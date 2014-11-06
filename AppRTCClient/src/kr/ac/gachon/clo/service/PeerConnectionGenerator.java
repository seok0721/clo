package kr.ac.gachon.clo.service;

import kr.ac.gachon.clo.DeviceCapturer;
import kr.ac.gachon.clo.IceServers;
import kr.ac.gachon.clo.SrtpMediaConstraints;
import kr.ac.gachon.clo.handler.HandshakeHandler;
import kr.ac.gachon.clo.observer.PeerConnectionObserver;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

public class PeerConnectionGenerator implements Runnable {

	private static PeerConnectionGenerator instance = new PeerConnectionGenerator();
	private PeerConnectionFactory factory = new PeerConnectionFactory();
	private Thread thread;

	public static PeerConnectionGenerator getInstance() {
		return instance;
	}

	public void setPeerConnectionFactory(PeerConnectionFactory factory) {
		this.factory = factory;
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
		while(!thread.isInterrupted()) {
			HandshakeHandler handler = new HandshakeHandler();
			PeerConnectionObserver observer = new PeerConnectionObserver();
			PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
			connection.addStream(DeviceCapturer.getInstance().getMediaStream(), new MediaConstraints());
			observer.setPeerConnection(connection, handler);

			SocketService.getInstance().addEventHandler(handler);

			PeerConnectionPool.getInstance().addConnection(connection);
		}
	}

	private PeerConnectionGenerator() {
		DeviceCapturer.setPeerConnectionFactory(factory);
	}
}