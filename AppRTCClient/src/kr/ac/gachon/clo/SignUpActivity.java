package kr.ac.gachon.clo;

import java.io.ByteArrayOutputStream;
import java.util.EventObject;

import kr.ac.gachon.clo.apprtc.handler.ActivityEventListener;
import kr.ac.gachon.clo.apprtc.impl.SignalingService;
import kr.ac.gachon.clo.utils.HashUtils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements ActivityEventListener, View.OnClickListener {
	
	public static final String EVENT = "signup";

	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final int ACTION_PICK_EVENT = 0;
	private SignalingService signalingService = SignalingService.getInstance();
	private ImageView imgThumbnail;
	private Bitmap thumbnail;
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
		imgThumbnail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, ACTION_PICK_EVENT);
			}
		});

		btnSignUp = (Button)findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(this);

		edtEmail = (EditText)findViewById(R.id.edtSignUpEmail);
		edtPassword = (EditText)findViewById(R.id.edtSignUpPassword);
		edtConfirmPassword = (EditText)findViewById(R.id.edtSignUpPasswordConfirm);
		edtName = (EditText)findViewById(R.id.edtSignUpName);
	}

	@Override
	protected void onStart() {
		super.onStart();

		signalingService.setSignUpActivity(this);
	}

	@Override
	public void onClick(View view) {
		String email = edtEmail.getText().toString();
		String password = edtPassword.getText().toString();
		String confirmPassword = edtConfirmPassword.getText().toString();
		String name = edtName.getText().toString();

		if(email.length() == 0) {
			Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(password.length() == 0) {
			Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(confirmPassword.length() == 0) {
			Toast.makeText(this, "비밀번호 확인란을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(name.length() == 0) {
			Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		if(!password.equals(confirmPassword)) {
			Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		signalingService.signup(email, HashUtils.md5(password), name, Base64.encodeToString(bitmapToByteArray(thumbnail), 0));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ACTION_PICK_EVENT && resultCode == RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int thumbnailIndex = cursor.getColumnIndex(filePathColumn[0]);
			String thumbnailPath = cursor.getString(thumbnailIndex);
			cursor.close();

			thumbnail = getScaledBitmap(thumbnailPath, 300, 300);

			imgThumbnail.setImageBitmap(getCircularBitmap(thumbnail));
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

	public static Bitmap getCircularBitmap(Bitmap bitmap) {
		Bitmap output;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		} else {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		float r = 0;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			r = bitmap.getHeight() / 2;
		} else {
			r = bitmap.getWidth() / 2;
		}

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(r, r, r, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
		bitmap.compress( Bitmap.CompressFormat.PNG, 100, stream) ;
		byte[] byteArray = stream.toByteArray() ;
		return byteArray ;
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public void onSuccess() {
		String message = "회원 가입이 정상적으로 완료되었습니다.";

		Log.i(TAG, message);

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finishActivity(RESULT_OK);
	}

	public void onFailure() {
		String message = "회원 가입에 실패하였습니다.";

		Log.i(TAG, message);

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		finishActivity(RESULT_CANCELED);
	}

	@Override
	public EventObject getEvent() {
		return null;
	}
}