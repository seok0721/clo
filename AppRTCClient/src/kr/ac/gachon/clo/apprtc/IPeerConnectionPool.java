package kr.ac.gachon.clo.apprtc;

import org.webrtc.PeerConnection;

public interface IPeerConnectionPool extends IQueue<PeerConnection> {

	public void accumulate(PeerConnection connection);

	public void flush();
}