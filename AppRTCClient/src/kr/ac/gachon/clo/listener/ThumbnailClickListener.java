package kr.ac.gachon.clo;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class ThumbnailClickListener implements Runnable, View.OnClickListener {

	private static final int EVENT = 0;
	private Activity activity;

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		activity.startActivityForResult(intent, EVENT);
	}
}
