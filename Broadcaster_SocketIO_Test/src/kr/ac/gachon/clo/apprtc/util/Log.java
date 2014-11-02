package kr.ac.gachon.clo.apprtc.util;

public class Log {

	private static final String LOG_FORMAT = "[%s] %s, %s";
	private static final String STR_DEBUG = "DEBUG";
	private static final String STR_INFO = "INFO";
	private static final String STR_WARN = "WARN";
	private static final String STR_ERROR = "ERROR";
	private static final String STR_FATAL = "FATAL";

	private enum Type {

		Debug(1, STR_DEBUG),
		Info(2, STR_INFO),
		Warn(3, STR_WARN),
		Error(4, STR_ERROR),
		Fatal(5, STR_FATAL);

		private int level;
		private String value;

		private Type(int level, String value) {
			this.level = level;
			this.value = value;
		}

		private int getLevel() {
			return level;
		}

		public String getValue() {
			return value;
		}
	}

	public static void d(String tag, Object msg) {
		log(Type.Debug, tag, msg);
	}

	public static void i(String tag, Object msg) {
		log(Type.Info, tag, msg);
	}

	public static void w(String tag, Object msg) {
		log(Type.Warn, tag, msg);
	}

	public static void e(String tag, Object msg) {
		log(Type.Error, tag, msg);
	}

	public static void e(String tag, Object msg, Throwable e) {
		log(Type.Error, tag, msg);

		e.printStackTrace();
	}

	public static void f(String tag, Object msg) {
		log(Type.Fatal, tag, msg);
	}

	private static void log(Type type, String tag, Object msg) {
		if(type.getLevel() < 4) {
			System.out.println(String.format(LOG_FORMAT, type.getValue(), tag, msg));
		} else {
			System.err.println(String.format(LOG_FORMAT, type.getValue(), tag, msg));
		}
	}

	private Log() {}
}
