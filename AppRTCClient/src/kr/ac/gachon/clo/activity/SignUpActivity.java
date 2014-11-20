package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.EventResult;
import kr.ac.gachon.clo.handler.SignUpButtonHandler;
import kr.ac.gachon.clo.handler.ThumbnailClickHandler;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.utils.BitmapUtils;
import kr.ac.gachon.clo.view.SignUpView;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements SignUpView, ActivityEventHandler {

	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final String EVENT = "signUp";
	private ImageView imgThumbnail;
	private Bitmap bitThumbnail;
	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtConfirmPassword;
	private EditText edtName;
	private Button btnSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		imgThumbnail = (ImageView)findViewById(R.id.imgSignUpThumbnail);
		imgThumbnail.setOnClickListener(new ThumbnailClickHandler(this));

		btnSignUp = (Button)findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(new SignUpButtonHandler(this));

		edtEmail = (EditText)findViewById(R.id.edtSignUpEmail);
		edtPassword = (EditText)findViewById(R.id.edtSignUpPassword);
		edtConfirmPassword = (EditText)findViewById(R.id.edtSignUpPasswordConfirm);
		edtName = (EditText)findViewById(R.id.edtSignUpName);

		SocketService.getInstance().addEventHandler(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode != ThumbnailClickHandler.REQUEST_CODE || resultCode != RESULT_OK || data == null) {
			return;
		}

		Uri selectedImage = data.getData();
		String[] filePathColumn = { Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int thumbnailIndex = cursor.getColumnIndex(filePathColumn[0]);
		String thumbnailPath = cursor.getString(thumbnailIndex);
		cursor.close();

		bitThumbnail = getScaledBitmap(thumbnailPath, 300, 300);

		if(bitThumbnail != null) {
			imgThumbnail.setImageBitmap(BitmapUtils.getCircularBitmap(bitThumbnail));
		} else {
			imgThumbnail.setImageBitmap(null);
		}
	}

	@Override
	public void onMessage(JSONObject data) {
		try {
			if(data.getInt("ret") == EventResult.FAILURE) {
				Toast.makeText(this, "회원 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "회원 가입에 실패하였습니다.");
				return;
			}

			Toast.makeText(this, "회원 가입이 정상적으로 완료되었습니다.", Toast.LENGTH_SHORT).show();
			Log.i(TAG, "회원 가입이 정상적으로 완료되었습니다.");
			finish();
		} catch(Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private Bitmap getScaledBitmap(String picturePath, int width, int height) {
		BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
		sizeOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picturePath, sizeOptions);

		int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

		sizeOptions.inJustDecodeBounds = false;
		sizeOptions.inSampleSize = inSampleSize;

		return BitmapFactory.decodeFile(picturePath, sizeOptions);
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int requestWidth, int requestHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > requestHeight || width > requestWidth) {
			int heightRatio = Math.round((float) height / (float) requestHeight);
			int widthRatio = Math.round((float) width / (float) requestWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	@Override
	public ImageView getThumbnailImage() {
		return imgThumbnail;
	}

	@Override
	public Bitmap getThumbnailBitmap() {
		return bitThumbnail;
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
	public EditText getConfirmPassword() {
		return edtConfirmPassword;
	}

	@Override
	public EditText getName() {
		return edtName;
	}

	@Override
	public Button getSignUpButton() {
		return btnSignUp;
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	@Override
	public Activity getActivity() {
		return this;
	}
}