package kr.ac.gachon.clo;

public class BroadcastService {

	private static BroadcastService instance;

	public static BroadcastService getInstance() {
		if(instance == null) {
			instance = new BroadcastService();
		}

		return instance;
	}

	public void start() {
		// PeerConnectionGenerator.getInstance().start();

		// SignalingService.getInstance().start(URL);
	}

	public void stop() {
		// PeerConnectionGenerator.getInstance().stop();

		// SignalingService.getInstance().stop();
	}
}