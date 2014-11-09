package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.activity.ShootingActivity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

public class InfoButtonHandler implements OnClickListener {

	private ShootingActivity activity;

	public InfoButtonHandler(ShootingActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				activity.getInfoPanel().setVisibility(View.VISIBLE);

				activity.getCommentPanel().setVisibility(View.GONE);

				activity.getCommentButton().setBackgroundColor(Color.rgb(255,255,255));
				activity.getCommentButton().setTextColor(Color.rgb(38,38,38));

				activity.getInfoButton().setBackgroundColor(Color.rgb(232,232,232));
				activity.getInfoButton().setTextColor(Color.rgb(155,155,155));
			}
		});
	}
}