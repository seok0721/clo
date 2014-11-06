package kr.ac.gachon.clo.activity;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.service.PeerConnectionGenerator;
import kr.ac.gachon.clo.view.ShootingView;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.os.Bundle;

public class ShootingActivity extends Activity {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private ShootingView shootingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		shootingView = (ShootingView)findViewById(R.id.shootingView);
		shootingView.init(this);

		PeerConnectionGenerator.getInstance().start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		PeerConnectionGenerator.getInstance().stop();
	}
}