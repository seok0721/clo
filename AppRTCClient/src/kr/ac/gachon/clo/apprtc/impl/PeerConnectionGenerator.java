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
	private DeviceCapturer deviceCapturer;
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
			if(isRunning) {
				Log.i(TAG, "Peer connection generator is already running.");
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
			if(!isRunning) {
				Log.i(TAG, "Peer connection generator is already stopped.");
				return;
			}

			isRunning = false;

			signalToCreateConnection();
		}
	}

	@Override
	public void orderToCreateConnection() {
		if(!isRunning) {
			return;
		}

		signalToCreateConnection();
	}

	@Override
	public void run() {
		Log.i(TAG, "Generator start.");

		lock.lock();

		while(isRunning) {
			try {
				Log.i(TAG, "Wait for order to create connection...");

				doToCreateConnection.await();
			} catch(InterruptedException e) {
				if(!isRunning) {
					Log.i(TAG, "Stop generating connection...");
					break;
				}

				Log.i(TAG, "Generating connection...");
			}

			createConnection();
		}

		PeerConnectionPool.getInstance().release();

		lock.unlock();

		Log.i(TAG, "Generator stop.");
	}

	private void createConnection() {
		Log.i(TAG, "Create connection...");

		PeerConnectionObserver observer = new PeerConnectionObserver();
		PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
		observer.setPeerConnection(connection);

		Log.i(TAG, "Add stream...");

		connection.addStream(deviceCapturer.getMediaStream(), new MediaConstraints());

		Log.i(TAG, "Create offer...");

		connection.createOffer(new OfferObserver(connection), new MediaConstraints());
	}

	private PeerConnectionGenerator() {
		Log.i(TAG, "Create generator...");

		factory = new PeerConnectionFactory();

		deviceCapturer = DeviceCapturer.getInstance(factory);

		lock = new ReentrantLock();
		doToCreateConnection = lock.newCondition();

		Log.i(TAG, "To create generator complete.");
	}

	private void signalToCreateConnection() {
		lock.lock();

		doToCreateConnection.signal();

		lock.unlock();
	}
}