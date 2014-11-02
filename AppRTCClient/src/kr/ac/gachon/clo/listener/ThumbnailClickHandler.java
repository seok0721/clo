package kr.ac.gachon.clo.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ThumbnailClickHandler implements Runnable, OnClickListener {

	public static final int REQUEST_CODE = 0;

	private Activity activity;

	public ThumbnailClickHandler(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		activity.startActivityForResult(intent, REQUEST_CODE);
	}
}