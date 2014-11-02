package kr.ac.gachon.clo.listener;

import kr.ac.gachon.clo.ReadyActivity;
import kr.ac.gachon.clo.apprtc.impl.SignalingService;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class OnAirButtonHandler implements Runnable, OnClickListener {

	private ReadyActivity activity;

	public OnAirButtonHandler(ReadyActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(this);
	}

	@Override
	public void run() {
		String title = activity.getTitleName().getText().toString();

		if(title.length() == 0) {
			Toast.makeText(activity, "방송 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
			return;
		}

		SignalingService.getInstance().create(title);
	}
}