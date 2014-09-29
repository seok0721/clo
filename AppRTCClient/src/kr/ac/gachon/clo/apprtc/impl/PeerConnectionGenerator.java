package kr.ac.gachon.clo.apprtc.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kr.ac.gachon.clo.apprtc.IPeerConnectionGenerator;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;

import android.util.Log;

public class PeerConnectionGenerator implements IPeerConnectionGenerator, Runnable {

	private static final String TAG = PeerConnectionGenerator.class.getSimpleName();
	private static PeerConnectionGenerator instance;
	private PeerConnectionFactory factory;
	private Thread background;
	private Lock lock;
	private Condition doToCreateConnection;
	private Boolean isRunning = false;

	public static PeerConnectionGenerator getInstance() {
		if(instance == null) {
			instance = new PeerConnectionGenerator();
		}

		return instance;
	}

	@Override
	public void start() {
		synchronized(isRunning) {
			if(isRunning && (background != null) && background.isAlive()) {
				Log.i(TAG, "Background thread is already running.");
				return;
			}

			isRunning = true;

			background = new Thread(instance);
			background.start();
		}
	}

	@Override
	public void stop() {
		synchronized(isRunning) {
			isRunning = false;
		}
	}

	@Override
	public void orderToCreateConnection() {
		lock.lock();

		doToCreateConnection.signal();

		lock.unlock();
	}

	@Override
	public void run() {
		while(isRunning) {
			lock.lock();

			try {
				doToCreateConnection.await();
			} catch(InterruptedException e) {
				Log.i(TAG, "Generating connection...");
			}

			createConnection();

			lock.unlock();
		}
	}

	private void createConnection() {
		PeerConnectionObserver observer = new PeerConnectionObserver();
		PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
		connection.addStream(DeviceCapturer.getInstance(factory).getMediaStream(), new MediaConstraints());
		observer.setPeerConnection(connection);
		connection.createOffer(new OfferObserver(connection), new MediaConstraints());
	}

	private PeerConnectionGenerator() {
		factory = new PeerConnectionFactory();
		lock = new ReentrantLock();
		doToCreateConnection = lock.newCondition();
	}
}