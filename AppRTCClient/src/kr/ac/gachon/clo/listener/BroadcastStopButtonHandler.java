package kr.ac.gachon.clo.listener;

import kr.ac.gachon.clo.BroadcastService;
import android.view.View;
import android.view.View.OnClickListener;

public class BroadcastStopButtonHandler implements OnClickListener {

	@Override
	public void onClick(View v) {
		BroadcastService.getInstance().stop();
	}
}