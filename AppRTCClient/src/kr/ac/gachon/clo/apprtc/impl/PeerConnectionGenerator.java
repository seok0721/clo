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
	private Condition runtime;
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
				Log.i(TAG, "Peer connection generator is already running, " + background.getState().name());
			} else {
				isRunning = true;

				PeerConnectionPool.getInstance().release();

				background = new Thread(instance);
				background.start();
			}
		}
	}

	@Override
	public void stop() {
		synchronized(isRunning) {
			if(!isRunning) {
				Log.i(TAG, "Peer connection generator is already stopped.");
			} else {
				isRunning = false;

				signal();
			}
		}
	}

	@Override
	public void orderToCreateConnection() {
		if(!isRunning) {
			return;
		}

		signal();
	}

	@Override
	public void run() {
		Log.i(TAG, "Generator start.");

		lock.lock();

		while(isRunning) {
			try {
				Log.i(TAG, "Wait for order to create connection...");

				runtime.await();

				if(!isRunning) {
					Log.i(TAG, "Stop generating connection...");
					break;
				}

				Log.i(TAG, "Generating connection...");
				createConnection();
			} catch(InterruptedException e) {
				if(!isRunning) {
					Log.i(TAG, "Stop generating connection...");
					break;
				}
			}
		}

		Log.i(TAG, "Before release pool.");
		PeerConnectionPool.getInstance().release();
		Log.i(TAG, "After release pool.");

		lock.unlock();

		Log.i(TAG, "Generator stop.");
	}

	private void createConnection() {
		Log.i(TAG, "Create connection...");
		PeerConnectionObserver observer = new PeerConnectionObserver();
		PeerConnection connection = factory.createPeerConnection(new IceServers(), new SrtpMediaConstraints(), observer);
		observer.setPeerConnection(connection);

		Log.i(TAG, "Add stream...");
		connection.addStream(DeviceCapturer.getInstance(factory).getMediaStream(), new MediaConstraints());

		Log.i(TAG, "Create offer...");
		connection.createOffer(new OfferObserver(connection), new MediaConstraints());
	}

	private PeerConnectionGenerator() {
		Log.i(TAG, "Create generator...");
		factory = new PeerConnectionFactory();

		Log.i(TAG, "Initialize device capturer...");
		DeviceCapturer.getInstance(factory); // Initialize media stream.

		lock = new ReentrantLock();
		runtime = lock.newCondition();

		Log.i(TAG, "To create generator complete.");
	}

	private void signal() {
		lock.lock();

		runtime.signal();

		lock.unlock();
	}
}