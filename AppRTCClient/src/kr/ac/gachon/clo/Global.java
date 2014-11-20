package kr.ac.gachon.clo;

public class Global {

	private static String email;
	private static String name;
	private static String title;
	private static String thumbnail;
	private static String address;

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		Global.email = email;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		Global.name = name;
	}

	public static String getTitle() {
		return title;
	}

	public static void setTitle(String title) {
		Global.title = title;
	}

	public static String getThumbnail() {
		return thumbnail;
	}

	public static void setThumbnail(String thumbnail) {
		Global.thumbnail = thumbnail;
	}

	public static String getAddress() {
		return address;
	}

	public static void setAddress(String address) {
		Global.address = address;
	}

	private Global() {}
}