package kr.ac.gachon.clo.apprtc;

import io.socket.IOCallback;

import org.webrtc.SessionDescription;

public interface ISignalingService extends IOCallback, Runnable {

	public void start(String url);

	public void stop();

	public void runNextJob();

	public void push(SessionDescription session);
}