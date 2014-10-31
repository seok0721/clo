package kr.ac.gachon.clo;

import kr.ac.gachon.clo.apprtc.impl.SignalingService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity implements View.OnClickListener {

	private static final String TAG = SignInActivity.class.getSimpleName();
	private SignalingService signalingService = SignalingService.getInstance();
	private String serverIP;
	private String serverPort;
	private Button btnSignIn;
	private TextView txtSignUp;
	private EditText edtEmail;
	private EditText edtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);

		btnSignIn = (Button)findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);

		txtSignUp = (TextView)findViewById(R.id.txtSignUp);
		txtSignUp.setOnClickListener(this);

		edtEmail = (EditText)findViewById(R.id.edtSignInEmail);
		edtPassword = (EditText)findViewById(R.id.edtSignInPassword);

		serverIP = getResources().getString(R.string.serverIP);
		serverPort = getResources().getString(R.string.serverPort);
	}

	@Override
	protected void onStart() {
		super.onStart();

		signalingService.setSignInActivity(this);
		signalingService.connect(String.format("http://%s:%s", serverIP, serverPort));
	}

	public void onClick(View view) {
		String email;
		String password;

		switch(view.getId()) {
		case R.id.btnSignIn:
			email = edtEmail.getText().toString();
			password = edtPassword.getText().toString();

			if(email.length() == 0) {
				Toast.makeText(this, "	이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
				return;
			}

			if(password.length() == 0) {
				Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
				return;
			}

			signalingService.signin(email, password);

			break;
		case R.id.txtSignUp:
			Intent intent = new Intent(this, SignUpActivity.class);
			startActivity(intent);
		}
	}

	public void onSuccess() {
		Log.i(TAG, "로그인에 성공하였습니다.");

		Intent intent = new Intent(this, ReadyActivity.class);
		startActivity(intent);
	}

	public void onFailure() {
		Log.i(TAG, "로그인에 실패하였습니다.");

		Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
	}
}