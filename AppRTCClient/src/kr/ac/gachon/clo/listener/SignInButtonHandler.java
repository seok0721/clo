package kr.ac.gachon.clo.listener;

import kr.ac.gachon.clo.SignInActivity;
import kr.ac.gachon.clo.apprtc.impl.SignalingService;
import kr.ac.gachon.clo.utils.HashUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SignInButtonHandler implements Runnable, OnClickListener {

	private SignInActivity activity;
	private SignalingService signalingService = SignalingService.getInstance();

	public SignInButtonHandler(SignInActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		String email = activity.getEmail().getText().toString();
		String password = activity.getPassword().getText().toString();

		if(email.length() == 0) {
			Toast.makeText(activity, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(password.length() == 0) {
			Toast.makeText(activity, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		password = HashUtils.md5(password);

		signalingService.signin(email, password);
	}

	@Override
	public void run() {
		activity.runOnUiThread(this);
	}
}