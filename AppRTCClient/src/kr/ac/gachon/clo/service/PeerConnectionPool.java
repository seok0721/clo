package kr.ac.gachon.clo.service;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.webrtc.PeerConnection;

public class PeerConnectionPool {

	private static PeerConnectionPool instance = new PeerConnectionPool();
	private BlockingQueue<PeerConnection> waitQueue = new ArrayBlockingQueue<PeerConnection>(4);
	private Queue<PeerConnection> runQueue = new LinkedList<PeerConnection>();

	public static PeerConnectionPool getInstance() {
		return instance;
	}

	public boolean addConnection(PeerConnection connection) {
		try {
			waitQueue.put(connection);
			return true;
		} catch(InterruptedException e) {
			return false;
		}
	}

	public PeerConnection getConnection() {
		try {
			return waitQueue.take();
		} catch(InterruptedException e) {
			return null;
		}
	}

	public void accumulate(PeerConnection connection) {
		runQueue.add(connection);
	}

	public void release() {
		Iterator<PeerConnection> iter = runQueue.iterator();

		while(iter.hasNext()) {
			PeerConnection connection = iter.next();
			connection.close();

			iter.remove();
		}

		waitQueue.clear();
	}

	private PeerConnectionPool() {}
}