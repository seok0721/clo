package kr.ac.gachon.clo.apprtc.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import io.socket.SocketIO;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;

import android.util.Log;

public class SocketClient {

	private static final String TAG = SocketClient.class.getSimpleName();
	private static final String URL = "http://211.189.20.193:10080";
	private SocketIO socket;

	public SocketClient() {
		try {
			socket = new SocketIO(URL);
			socket.connect(new SocketHandler());
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
//
//	public static void start() {
//		PeerConnection connection = PeerConnectionPool.getInstance().getHaveLocalOfferConnection();
//
//		if(connection != null) {
//			OfferHandler.start(connection, socket);
//			return;
//		}
//
//		connection = PeerConnectionPool.getInstance().getStableConnection();
//		connection.createOffer(new OfferObserver(connection), new MediaConstraints());
//	}
//
//	public static void stop() {
//		
//	}
//
//	public void sendOffer() {
//		PeerConnection connection = PeerConnectionPool.getInstance().getHaveLocalOfferConnection();
//
//		if(connection != null) {
//			OfferHandler.start(connection, socket);
//			return;
//		}
//
//		connection = PeerConnectionPool.getInstance().getStableConnection();
//		connection.createOffer(new OfferObserver(connection), new MediaConstraints());
//	}

	public SocketIO getSocket() {
		return socket;
	}
}