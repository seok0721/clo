package kr.ac.gachon.clo;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;

public class CameraView extends GLSurfaceView {

	private Point screenSize;

	public CameraView(Context context, Point screenPoint) {
		super(context);

		this.screenSize = screenPoint;
	}

	public void updateScreenSize(Point screenPoint) {
		this.screenSize = screenPoint;
	}

	@Override
	protected void onMeasure(int unusedX, int unusedY) {
		setMeasuredDimension(screenSize.x, screenSize.y);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION |
				SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}
