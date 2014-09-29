package kr.ac.gachon.clo.apprtc.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import android.util.Log;

public class AnswerHandler implements Runnable {

	private static final String TAG = AnswerHandler.class.getSimpleName();
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
	private static SdpObserver answerObserver = new AnswerObserver();
	private static Thread answerReceiver;

	public static void enqueue(String answerSDP) {
		try {
			queue.put(answerSDP);
		} catch(InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public static void start() {
		if(answerReceiver != null) {
			return;
		}

		answerReceiver = new Thread(new AnswerHandler());
		answerReceiver.start();
	}

	public static void stop() {
		answerReceiver.interrupt();
		answerReceiver = null;
	}

	@Override
	public void run() {
		try {
			for(;;) {
				String answerSDP = queue.take();

				SessionDescription session = new SessionDescription(Type.ANSWER, answerSDP);

				try {
//					PeerConnection connection = PeerConnectionPool.getInstance().getHaveLocalOfferConnection();
//					connection.setRemoteDescription(answerObserver, session);
				} catch(Exception e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} catch(InterruptedException e) {
			Log.i(TAG, "Interrupt is occured, but ignore this interrupt.");
		}
	}

	private AnswerHandler() {}
}