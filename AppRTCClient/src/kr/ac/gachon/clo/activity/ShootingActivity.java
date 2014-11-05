package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.listener.BroadcastStartButtonHandler;
import kr.ac.gachon.clo.listener.BroadcastStopButtonHandler;
import kr.ac.gachon.clo.view.ShootingView;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class ShootingActivity extends Activity {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private ShootingView shootingView;
	private Button btnStart;
	private Button btnStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		shootingView = (ShootingView)findViewById(R.id.shootingView);
		shootingView.init(this);

		btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new BroadcastStartButtonHandler());

		btnStop = (Button)findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new BroadcastStopButtonHandler());
	}
}