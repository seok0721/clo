package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.SocketService;
import kr.ac.gachon.clo.event.EventResult;
import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.listener.SignInButtonHandler;
import kr.ac.gachon.clo.listener.SignUpLableHandler;
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

public class SignInActivity extends Activity implements SignInView, Worker {

	private static final String TAG = SignInActivity.class.getSimpleName();
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

		SocketService.getInstance().addWorker(this);
		SocketService.getInstance().start();
	}

	@Override
	public void onMessage(JSONObject data) {
		String message;

		try {
			if(data.getInt("ret") == EventResult.FAILURE) {
				throw new Exception();
			}

			Intent intent = new Intent(this, ReadyActivity.class);
			intent.putExtra("email", data.getString("email"));
			intent.putExtra("name", data.getString("name"));
			intent.putExtra("img", data.getString("img"));
			startActivity(intent);

			message = "로그인에 성공하였습니다.";
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);

			message = "로그인에 실패하였습니다.";
		}

		Log.i(TAG, message);

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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