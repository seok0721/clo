package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.activity.SignInActivity;
import kr.ac.gachon.clo.activity.SignUpActivity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SignUpLableHandler implements Runnable, OnClickListener {

	private SignInActivity activity;

	public SignUpLableHandler(SignInActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		Intent intent = new Intent(activity, SignUpActivity.class);
		activity.startActivity(intent);
	}
}