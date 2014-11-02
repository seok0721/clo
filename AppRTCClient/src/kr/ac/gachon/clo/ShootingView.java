package kr.ac.gachon.clo;

import org.webrtc.VideoRendererGui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class ShootingView extends GLSurfaceView {

	private Point screenSize = new Point();

	public ShootingView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public void init(Activity activity) {
		VideoRendererGui.setView(this);

		activity.getWindowManager().getDefaultDisplay().getRealSize(screenSize);
	}

	@Override
	protected void onMeasure(int unusedX, int unusedY) {
		setMeasuredDimension(screenSize.x - 1, screenSize.y - 1);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| SYSTEM_UI_FLAG_FULLSCREEN
				| SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}