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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity implements SignInView, ActivityEventHandler {

	// private static final String TAG = SignInActivity.class.getSimpleName();
	private static final String EVENT = "signin";
	private Button btnSignIn;
	private TextView txtSignUp;
	private EditText edtEmail;
	private EditText edtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	public void onMessage(JSONObject data) {
		int ret = EventResult.FAILURE;
		String email = null;
		String name = null;
		String encodedPortrait = null;

		try {
			ret = data.getInt("ret");
			email = data.getString("email");
			name = data.getString("name");
			encodedPortrait = data.getString("img");
		} catch(Exception e) {}

		if(ret == EventResult.FAILURE) {
			Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent(this, ReadyActivity.class);
		intent.putExtra("email", email);
		intent.putExtra("name", name);
		intent.putExtra("img", encodedPortrait);
		startActivity(intent);

		Global.setChannel(email);

		Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
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
}