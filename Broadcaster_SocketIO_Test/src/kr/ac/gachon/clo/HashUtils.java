package kr.ac.gachon.clo;

import java.security.MessageDigest;

public class HashUtils {

	private static MessageDigest instance;
	private static StringBuffer buffer = new StringBuffer();

	static {
		try {
			instance = MessageDigest.getInstance("MD5");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static String md5(String value) {
		instance.update(value.getBytes());
		byte data[] = instance.digest();

		buffer.setLength(0);

		for(int i = 0 ; i < data.length ; i++) {
			buffer.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
		}

		return buffer.toString();
	}
}
