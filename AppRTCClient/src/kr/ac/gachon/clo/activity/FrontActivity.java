package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.event.ActivityExecuteResultHandler;
import kr.ac.gachon.clo.service.SocketService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class FrontActivity extends Activity implements ActivityExecuteResultHandler {

	private static final String TAG = FrontActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_front);

		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {}

		SocketService.getInstance().setFrontActivityHandler(this);
		SocketService.getInstance().start();
	}

	@Override
	public void onSuccess() {
		Log.i(TAG, "onSuccess");

		Toast.makeText(this, "시그널링 서버와 연결되었습니다.", Toast.LENGTH_SHORT).show();
		startActivity(new Intent(this, SignInActivity.class));
		finish();
	}

	@Override
	public void onFailure(String message) {
		Log.i(TAG, "onFailure");

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}