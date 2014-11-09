package kr.ac.gachon.clo.webrtc;

import java.util.LinkedList;

import org.webrtc.PeerConnection.IceServer;

public class IceServers extends LinkedList<IceServer> {

	private static final long serialVersionUID = 1L;

	public IceServers() {
		this.add(new IceServer("stun:stun.l.google.com:19302"));
		this.add(new IceServer("turn:211.189.20.193", "clo", "clo"));
	}
}