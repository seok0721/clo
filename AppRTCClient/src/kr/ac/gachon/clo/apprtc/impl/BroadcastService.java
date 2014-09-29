package kr.ac.gachon.clo.apprtc.impl;

import kr.ac.gachon.clo.apprtc.IBroadcastService;

public class BroadcastService implements IBroadcastService {

	public static BroadcastService instance;

	public static BroadcastService getInstance() {
		if(instance == null) {
			instance = new BroadcastService();
		}

		return instance;
	} 

	@Override
	public void start() {
		PeerConnectionGenerator.getInstance().start();
		// TODO To start signaling service.
	}

	@Override
	public void stop() {
		PeerConnectionGenerator.getInstance().stop();
		// TODO To stop signaling service.
	}
}