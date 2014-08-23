package kr.ac.gachon.clo.test;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.URL;

import org.json.JSONObject;
import org.junit.Test;

public class SignalingClientTest {

	@Test
	public void connectToServer() throws Exception {
		URL url = new URL("http://211.189.19.82:20130/");
		SocketIO client = new SocketIO(url);
		client.connect(new IOCallback() {

			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				System.out.println(json.toString());
			}

			@Override
			public void onMessage(String msg, IOAcknowledge ack) {
				System.out.println(msg);
			}

			@Override
			public void onError(SocketIOException e) {
				System.out.println(e.getMessage());
			}

			@Override
			public void onDisconnect() {
				System.out.println("disconnect");
			}

			@Override
			public void onConnect() {
				System.out.println("connect");
			}

			@Override
			public void on(String msg, IOAcknowledge ack, Object... args) {
				System.out.println("msg");
			}
		});
	}
}
