package kr.ac.gachon.clo.apprtc.impl;

import io.socket.SocketIO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OfferHandler implements Runnable {

	private static final String TAG = OfferHandler.class.getSimpleName();
	private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
	private static Thread offerSender; 
	private SocketIO socket;

	public static void enqueue(String offerSDP) {
		try {
			queue.put(offerSDP);
		} catch(InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	public static void start(SocketIO socket) {
		if(offerSender != null) {
			return;
		}

		OfferHandler runnable = new OfferHandler();
		runnable.socket = socket;

		offerSender = new Thread(runnable);
		offerSender.start();
	}

	public static void clear() {
		offerSender.interrupt();
	}

	@Override
	public void run() {
		for(;;) {
			try {
				String offerSDP = queue.take();

				JSONObject data = new JSONObject();
				data.put("sdp", offerSDP);

				for(;;) {
					try {
						socket.emit("offer", data);
						Thread.sleep(3000);
					} catch(InterruptedException e) {
						Log.i(TAG, "clear to send offer sdp.");
					}
				}
			} catch(JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch(InterruptedException e) {
				Log.i(TAG, "Interrupt is occured, but ignore this interrupt.");
			}
		}
	}

	private OfferHandler() {}
}