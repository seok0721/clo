package kr.ac.gachon.clo.handler;

import java.io.ByteArrayOutputStream;

import kr.ac.gachon.clo.activity.SignUpActivity;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.utils.HashUtils;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SignUpButtonHandler implements Runnable, OnClickListener {

	private static final String TAG = SignUpButtonHandler.class.getSimpleName();
	private SignUpActivity activity;

	public SignUpButtonHandler(SignUpActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		String email = activity.getEmail().getText().toString();
		String password = activity.getPassword().getText().toString();
		String confirmPassword = activity.getConfirmPassword().getText().toString();
		String name = activity.getName().getText().toString();
		Bitmap thumbnail = activity.getThumbnailBitmap();

		if(email.length() == 0) {
			Toast.makeText(activity, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(password.length() == 0) {
			Toast.makeText(activity, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(confirmPassword.length() == 0) {
			Toast.makeText(activity, "비밀번호 확인란을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(name.length() == 0) {
			Toast.makeText(activity, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(!password.equals(confirmPassword)) {
			Toast.makeText(activity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		Log.i(TAG, email);
		Log.i(TAG, HashUtils.md5(password));
		Log.i(TAG, name);
		Log.i(TAG, thumbnail.getByteCount() + "");
		Log.i(TAG, ((thumbnail == null) || thumbnail.getByteCount() == 0) ? "" : Base64.encodeToString(bitmapToByteArray(thumbnail), 0));

		SocketService.getInstance().signUp(email, HashUtils.md5(password),
				Base64.encodeToString(name.getBytes(), 0),
				((thumbnail == null) || thumbnail.getByteCount() == 0)
				? null : Base64.encodeToString(bitmapToByteArray(thumbnail), 0));
	}

	public byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

		return stream.toByteArray();
	}
}