package kr.ac.gachon.clo.apprtc.handler;

import org.json.JSONObject;

import android.util.Log;

public class SignupEventHandler implements EventHandler<String> {

	private static final String TAG = SignupEventHandler.class.getSimpleName();
	private static final String EVENT = "signup";

	@Override
	public void handle(JSONObject data) {
		Log.i(TAG, "SignUp 이벤트 발생");

		final int signUpResult = data.getInt("ret");

		signUpActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(signUpResult == 0) {

					signInActivity.onSuccess();
				} else {
					signInActivity.onFailure();
				}
			}
		});
	}

	@Override
	public String getEventType() {
		return EVENT;
	}
}