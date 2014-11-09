package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.activity.ShootingActivity;
import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.EventResult;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class ViewerCountHandler implements ActivityEventHandler {

	private static final String TAG = ViewerCountHandler.class.getSimpleName();
	private static final String EVENT = "viewer_count";
	private static ViewerCountHandler instance;

	// FIXME 나중에 인터페이스 만들기
	private ShootingActivity activity;

	public static ViewerCountHandler getInstance() {
		if(instance == null) {
			instance = new ViewerCountHandler();
		}

		return instance;
	}

	public void setShootingActivity(ShootingActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onMessage(JSONObject data) {
		try {
			int ret = data.getInt("ret");
			final int count = data.getInt("count");

			if(ret == EventResult.FAILURE) {
				return;
			}

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					activity.getClientCount().setText("접속자수 : " + count);
				}
			});
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	@Override
	public Activity getActivity() {
		return activity;
	}

	private ViewerCountHandler() {}
}