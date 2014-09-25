package kr.ac.gachon.clo.apprtc.util;

import java.util.LinkedList;
import java.util.List;

import org.webrtc.PeerConnection.IceServer;

public class NetworkUtils {

	private static List<IceServer> iceServers = new LinkedList<IceServer>();

	static {
		iceServers.add(new IceServer("stun:stun.l.google.com:19302"));
		// iceServers.add(new IceServer("turn:211.189.20.193", "clo", "clo"));
		/*
		iceServers.add(new IceServer("stun:stun1.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun2.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun3.l.google.com:19302"));
		iceServers.add(new IceServer("stun:stun4.l.google.com:19302"));
		*/
	}

	public static List<IceServer> getIceServers() {
		return iceServers;
	}

	private NetworkUtils() {}
}