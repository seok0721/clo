package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.activity.ShootingActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraViewHandler implements OnClickListener {

	private ShootingActivity activity;
	private boolean isVisible = false;

	public CameraViewHandler(ShootingActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(isVisible) {
					isVisible = false;

					activity.getPlayButton().setVisibility(View.GONE);
					activity.getTitleView().setVisibility(View.GONE);
					activity.getClientCount().setVisibility(View.GONE);
				} else {
					isVisible = true;

					activity.getPlayButton().setVisibility(View.VISIBLE);
					activity.getTitleView().setVisibility(View.VISIBLE);
					activity.getClientCount().setVisibility(View.VISIBLE);
				}
			}
		});
	}
}