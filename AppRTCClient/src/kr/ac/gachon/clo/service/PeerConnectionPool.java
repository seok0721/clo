package kr.ac.gachon.clo.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.webrtc.PeerConnection;

import android.util.Log;

public class PeerConnectionPool {

	private static final String TAG = PeerConnectionPool.class.getSimpleName();
	private static PeerConnectionPool instance = new PeerConnectionPool();
	private BlockingQueue<PeerConnection> waitQueue = new ArrayBlockingQueue<PeerConnection>(10); // Before 1
	private Queue<PeerConnection> runQueue = new LinkedList<PeerConnection>();

	public static PeerConnectionPool getInstance() {
		return instance;
	}

	public boolean addConnection(PeerConnection connection) {
		try {
			Log.i(TAG, "연결을 풀에 추가합니다.");
			// waitQueue.put(connection);
			waitQueue.add(connection);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public PeerConnection getConnection() {
		try {
			Log.i(TAG, "연결을 풀에서 꺼냅니다.");

			PeerConnection connection = waitQueue.take();
			// PeerConnection connection = waitQueue.poll();
			runQueue.add(connection);

			return connection;
		} catch(Exception e) {
			return null;
		}
	}

	public void release() {
		Iterator<PeerConnection> iter = runQueue.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();
			connection.close();

			iter.remove();
		}

		waitQueue.clear();

		Log.i(TAG, runQueue.size() + "");
		Log.i(TAG, waitQueue.size() + "");
	}

	private PeerConnectionPool() {}
}