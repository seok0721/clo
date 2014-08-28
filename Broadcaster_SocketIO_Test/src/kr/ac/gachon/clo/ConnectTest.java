package kr.ac.gachon.clo;

public class ConnectTest {

	public static void main(String[] args) throws Exception {
		AppRTCClient client = new AppRTCClient();
		client.connect();
		client.login("seok0721@gmail.com", HashUtils.md5("0000").toUpperCase());
		Thread.sleep(1000);
		client.disconnect();
	}
}