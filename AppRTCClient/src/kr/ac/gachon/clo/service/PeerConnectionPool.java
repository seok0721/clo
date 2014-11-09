package kr.ac.gachon.clo.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.webrtc.PeerConnection;

import android.util.Log;

public class PeerConnectionPool {

	private static final String TAG = PeerConnectionPool.class.getSimpleName();
	private static PeerConnectionPool instance = new PeerConnectionPool();
	private Queue<PeerConnection> runQueue = new LinkedList<PeerConnection>();

	public static PeerConnectionPool getInstance() {
		return instance;
	}

	public void addConnection(PeerConnection connection) {
		Log.i(TAG, "연결을 풀에 추가합니다.");

		runQueue.add(connection);
	}

	public void release() {
		Iterator<PeerConnection> iter = runQueue.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();
			connection.close();

			iter.remove();
		}

		runQueue.clear();
	}

	private PeerConnectionPool() {}
}