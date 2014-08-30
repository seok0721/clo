package kr.ac.gachon.clo;

public class ConnectTest {

	public static void main(String[] args) throws Exception {
		AppRTCClient client = new AppRTCClient();
		client.connect();
		client.login("seok0721@gmail.com", HashUtils.md5("0000").toUpperCase());
		Thread.sleep(3000);
		client.createRoom("asdf");
		Thread.sleep(2000);
		client.logout();
		Thread.sleep(3000);
		System.out.println(11111111);
		client.disconnect();
		System.out.println(2222);
	}
}