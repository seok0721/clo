package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.activity.SignInActivity;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.utils.HashUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SignInButtonHandler implements OnClickListener {

	private SignInActivity activity;

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

		SocketService.getInstance().signIn(email, HashUtils.md5(password));
	}
}