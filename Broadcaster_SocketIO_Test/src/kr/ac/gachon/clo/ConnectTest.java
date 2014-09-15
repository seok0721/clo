package kr.ac.gachon.clo;

import io.socket.SocketIO;

public class ConnectTest {

	public static void main(String[] args) throws Exception {
		SocketIOClient client = new SocketIOClient();
		client.setCallback(new SocketIOHandler());
		client.setSocket(new SocketIO("http://211.189.20.193:10080/"));
		client.connect();

		Thread.sleep(1000);

		client.login("seok0721@gmail.com", "0000");

		Thread.sleep(500);

		client.create("news");

		Thread.sleep(500);

		client.offer("asdf");
		Thread.sleep(500);

		client.destroy();

		Thread.sleep(500);

		client.logout();

		Thread.sleep(1000);

		client.disconnect();
	}
}