package kr.ac.gachon.clo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.webrtc.PeerConnection;

import android.util.Log;

public class PeerConnectionPool {

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

	public int size() {
		return queue.size();
	}

	public void accumulate(PeerConnection connection) {
		connectionSet.add(connection);
	}

	public void remove(PeerConnection connection) {
		Iterator<PeerConnection> iter = connectionSet.iterator();

		while(iter.hasNext()) {
			PeerConnection savedConnection = iter.next();

			if(connection != savedConnection) {
				continue;
			}

			iter.remove();
			connection.close();

			return;
		}
	}

	public void release() {
		Iterator<PeerConnection> iter = connectionSet.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();
			iter.remove();
			connection.close();
		}

		queue.clear();
	}

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

	public PeerConnection dequeue() {
		for(;;) {
			try {
				return queue.take();
			} catch(Exception e) {
				Log.i(TAG, "Maybe signaling service will stop...");
			}
		}
	}

	private PeerConnectionPool() {}
}