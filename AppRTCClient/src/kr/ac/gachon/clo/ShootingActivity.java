package kr.ac.gachon.clo;

import kr.ac.gachon.clo.apprtc.impl.AnswerHandler;
import kr.ac.gachon.clo.apprtc.impl.OfferHandler;
import kr.ac.gachon.clo.apprtc.impl.SocketClient;

import org.webrtc.PeerConnectionFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShootingActivity extends Activity {

	private static final String TAG = ShootingActivity.class.getSimpleName();
	private SocketClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);

		initShootingView();
		bind();

		client = new SocketClient();
		

		AnswerHandler.start();
//		OfferHandler.start(client);
	}

	private void initShootingView() {
		setContentView(R.layout.shooting);

		ShootingView shootingView = (ShootingView)findViewById(R.id.shootingView);
		shootingView.setScreenSize(this);
	}

	private void bind() {
		Button btnStart = (Button)findViewById(R.id.btnStart);

		btnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				client.sendOffer();
			}
		});
	}
}