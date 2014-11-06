package kr.ac.gachon.clo;

public class Global {

	private static String channel;

	public static String getChannel() {
		return channel;
	}

	public static void setChannel(String channel) {
		Global.channel = channel;
	}

	private Global() {}
}