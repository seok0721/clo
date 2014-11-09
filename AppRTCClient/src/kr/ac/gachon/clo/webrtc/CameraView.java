package kr.ac.gachon.clo.webrtc;

import org.webrtc.VideoRendererGui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public abstract class CameraView extends GLSurfaceView {

	private Point screenSize = new Point();

	public CameraView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void initialize(Activity activity) {
		VideoRendererGui.setView(this);

		activity.getWindowManager().getDefaultDisplay().getRealSize(screenSize);
	}

	@Override
	public void onMeasure(int unusedX, int unusedY) {
		setMeasuredDimension(screenSize.x - 1, screenSize.y - 1);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| SYSTEM_UI_FLAG_FULLSCREEN
				| SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}