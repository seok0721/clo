package kr.ac.gachon.clo.apprtc;

import io.socket.IOCallback;

import org.json.JSONObject;
import org.webrtc.SessionDescription;

public interface ISignalingService extends IOCallback, Runnable {

	public void start(String url);

	public void stop();

	public void signup(String userId, String password, String name, String imageData);

	public void signin(String userId, String password);

	public void signout();

	public void runNextJob();

	public void push(SessionDescription session);
}