package kr.ac.gachon.clo;

import kr.ac.gachon.clo.apprtc.impl.BroadcastService;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShootingActivity extends Activity {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private ShootingView shootingView;
	private Button btnStart;
	private Button btnStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		initView();
		bindEvent();
	}

	private void initView() {
		setContentView(R.layout.shooting);

		shootingView = (ShootingView)findViewById(R.id.shootingView);
		shootingView.init(this);
	}

	private void bindEvent() {
		btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BroadcastService.getInstance().start();
			}
		});

		btnStop = (Button)findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BroadcastService.getInstance().stop();
			}
		});
	}
}