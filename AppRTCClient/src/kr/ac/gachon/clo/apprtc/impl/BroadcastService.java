package kr.ac.gachon.clo.apprtc.impl;

import kr.ac.gachon.clo.apprtc.IBroadcastService;

public class BroadcastService implements IBroadcastService {

	private static BroadcastService instance;
	private static final String URL = "http://211.189.20.193:10080";

	public static BroadcastService getInstance() {
		if(instance == null) {
			instance = new BroadcastService();
		}

		return instance;
	} 

	@Override
	public void start() {
		PeerConnectionGenerator.getInstance().start();

		SignalingService.getInstance().start(URL);
	}

	@Override
	public void stop() {
		PeerConnectionGenerator.getInstance().stop();

		SignalingService.getInstance().stop();
	}
}