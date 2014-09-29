package kr.ac.gachon.clo.apprtc.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import kr.ac.gachon.clo.apprtc.IPeerConnectionPool;

import org.webrtc.PeerConnection;

import android.util.Log;

public class PeerConnectionPool implements IPeerConnectionPool {

	private static final String TAG = PeerConnectionPool.class.getSimpleName();
	private static PeerConnectionPool instance;
	private BlockingQueue<PeerConnection> queue = new ArrayBlockingQueue<PeerConnection>(1);
	private Set<PeerConnection> connectionSet = new HashSet<PeerConnection>();

	public static PeerConnectionPool getInstance() {
		if(instance == null) {
			instance = new PeerConnectionPool();
		}

		return instance;
	}

	@Override
	public void accumulate(PeerConnection connection) {
		connectionSet.add(connection);
	}

	@Override
	public void flush() {
		Iterator<PeerConnection> iter = connectionSet.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();
			connection.dispose();
			iter.remove();
		}
	}

	@Override
	public void enqueue(PeerConnection connection) {
		for(;;) {
			try {
				queue.put(connection);
				return;
			} catch(InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	@Override
	public PeerConnection dequeue() {
		for(;;) {
			try {
				return queue.take();
			} catch(Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	private PeerConnectionPool() {}
}