package kr.ac.gachon.clo;

import io.socket.SocketIO;

import java.net.MalformedURLException;

import kr.ac.gachon.clo.apprtc.AppRTCManager;
import kr.ac.gachon.clo.apprtc.SocketHandler;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoRendererGui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private CameraView cameraView;
	private AppRTCManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PeerConnectionFactory.initializeAndroidGlobals(this, true, true);
		manager  = new AppRTCManager();

		initSocket();
		initWindow();
		initCameraView();

		runTestStart();
	}

	private void initSocket() {
		try {
			manager.setSocketHandler(new SocketHandler());
			manager.setSocket(new SocketIO("http://211.189.20.193:10080/"));
			manager.connect();
		} catch(MalformedURLException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	private void initWindow() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void initCameraView() {
		Point screenSize = new Point();
		getWindowManager().getDefaultDisplay().getRealSize(screenSize);

		cameraView = new CameraView(this, screenSize);

		VideoRendererGui.setView(cameraView);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(cameraView);
	}

	private void runTestStart() {
		try {
			manager.login("seok0721@gmail.com", "0000");
			Thread.sleep(500);
			manager.create("news");
			Thread.sleep(500);
			manager.start();
		} catch(InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
}