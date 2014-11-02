package kr.ac.gachon.clo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppRTCSession {

	private static final String COOKIE_FILE_NAME = "apprtc.props";
	private static final String SESSION_KEY = "session-key";
	private static boolean enableCookie = false;
	private static Properties props = new Properties();

	public static String getSessionKey() {
		if(!existSession()) {
			return null;
		} else {
			return (String)props.get(SESSION_KEY);
		}
	}

	public static void setSessionKey(String sessionKey) {
		props.put(SESSION_KEY, sessionKey);

		try {
			props.store(new FileOutputStream(COOKIE_FILE_NAME), null);
		} catch(Exception e) {
			System.err.println("Fail to store cookie.");
			System.err.println(e.getMessage());
		}
	}

	private static void createCookie() {
		File cookie = new File(COOKIE_FILE_NAME);

		if(cookie.exists()) {
			enableCookie = true;
		} else {
			try {
				cookie.createNewFile();
				enableCookie = true;
			} catch(IOException e) {
				enableCookie = false;
				return;
			}
		}
	}

	private static boolean existSession() {
		createCookie();

		if(!enableCookie) {
			return false;
		}

		try {
			props.clear();
			props.load(new FileInputStream(COOKIE_FILE_NAME));

			return (props.get(SESSION_KEY) != null);
		} catch(Exception e) {
			return false;
		}
	}

	private AppRTCSession() {

	}
}