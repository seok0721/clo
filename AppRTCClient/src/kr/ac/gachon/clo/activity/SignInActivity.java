package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.EventResult;
import kr.ac.gachon.clo.handler.SignInButtonHandler;
import kr.ac.gachon.clo.handler.SignUpLableHandler;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.view.SignInView;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity implements SignInView, ActivityEventHandler {

	private static final String TAG = SignInActivity.class.getSimpleName();
	private static final String EVENT = "signIn";
	private static Boolean waitForDisconnect = true;
	private Button btnSignIn;
	private TextView txtSignUp;
	private EditText edtEmail;
	private EditText edtPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		btnSignIn = (Button)findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(new SignInButtonHandler(this));

		txtSignUp = (TextView)findViewById(R.id.txtSignUp);
		txtSignUp.setOnClickListener(new SignUpLableHandler(this));

		edtEmail = (EditText)findViewById(R.id.edtSignInEmail);
		edtPassword = (EditText)findViewById(R.id.edtSignInPassword);

		SocketService.getInstance().addEventHandler(this);
	}

	@Override
	public void onStop() {
		super.onStop();

		if(waitForDisconnect) {
			SocketService.getInstance().stop();
			finish();
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		waitForDisconnect = true;
	}

	@Override
	public void onMessage(JSONObject data) {
		try {
			int ret = data.getInt("ret");

			if(ret == EventResult.FAILURE) {
				Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
				return;
			}

			String email = data.getString("email");
			String name = data.getString("name");
			String encodedPortrait = data.getString("img");

			if(ret == EventResult.FAILURE) {
				Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "로그인에 실패하였습니다.");
				return;
			}

			waitForDisconnect = false;

			Global.setEmail(email);
			Global.setName(name);
			Global.setThumbnail(encodedPortrait);

			startActivity(new Intent(this, ReadyActivity.class));
			finish();

			Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "로그인에 성공하였습니다.");
		} catch(Exception e) {
			Toast.makeText(this, "서버에 요청하는 도중 오류가 발생하였습니다. 앱을 다시 실행시키세요.", Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	@Override
	public Button getSignInButton() {
		return btnSignIn;
	}

	@Override
	public EditText getEmail() {
		return edtEmail;
	}

	@Override
	public EditText getPassword() {
		return edtPassword;
	}

	@Override
	public TextView getSignUpLabel() {
		return txtSignUp;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	public static void setWaitForDisconnection(boolean waitForDisconnect) {
		SignInActivity.waitForDisconnect = waitForDisconnect;
	}
}