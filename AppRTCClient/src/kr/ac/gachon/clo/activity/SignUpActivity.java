package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.SocketService;
import kr.ac.gachon.clo.event.EventResult;
import kr.ac.gachon.clo.event.Worker;
import kr.ac.gachon.clo.listener.SignUpButtonHandler;
import kr.ac.gachon.clo.listener.ThumbnailClickHandler;
import kr.ac.gachon.clo.view.SignUpView;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements SignUpView, Worker {

	private static final String TAG = SignUpActivity.class.getSimpleName();
	private static final String EVENT = "signup";
	private SocketService socketService = SocketService.getInstance();
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
		imgThumbnail.setOnClickListener(new ThumbnailClickHandler(this));

		btnSignUp = (Button)findViewById(R.id.btnSignUp);
		btnSignUp.setOnClickListener(new SignUpButtonHandler(this));

		edtEmail = (EditText)findViewById(R.id.edtSignUpEmail);
		edtPassword = (EditText)findViewById(R.id.edtSignUpPassword);
		edtConfirmPassword = (EditText)findViewById(R.id.edtSignUpPasswordConfirm);
		edtName = (EditText)findViewById(R.id.edtSignUpName);

		socketService.addWorker(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ThumbnailClickHandler.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
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

	@Override
	public void onMessage(JSONObject data) {
		int ret = EventResult.FAILURE;

		try {
			ret = data.getInt("ret");
		} catch(JSONException e) {}

		String message = (ret == EventResult.SUCCESS)
				? "회원 가입이 정상적으로 완료되었습니다." : "회원 가입에 실패하였습니다.";
		Log.i(TAG, message);
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

		finish();
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
		int length = (bitmap.getWidth() < bitmap.getHeight()) ? bitmap.getWidth() : bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		float r = length / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(r, r, r, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
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

	@Override
	public ImageView getThumbnail() {
		return imgThumbnail;
	}

	@Override
	public Bitmap getThumbnailBitmap() {
		return thumbnail;
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