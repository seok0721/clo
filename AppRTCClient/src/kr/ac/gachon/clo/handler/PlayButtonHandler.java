package kr.ac.gachon.clo.handler;

import kr.ac.gachon.clo.Global;
import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.activity.ShootingActivity;
import kr.ac.gachon.clo.service.PeerConnectionPool;
import kr.ac.gachon.clo.service.SocketService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class PlayButtonHandler implements OnClickListener {

	private ShootingActivity activity;
	private boolean isStarted = true;

	public PlayButtonHandler(ShootingActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onClick(View v) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(!isStarted) {
					SocketService.getInstance().createRoom(Global.getChannel());
					activity.getPlayButton().setImageBitmap(bitmapResize(R.drawable.stop, 200, 200));
					isStarted = true;

					SocketService.getInstance().addEventHandler(new HandshakeHandler());

					Toast.makeText(activity, "방송을 시작합니다.", Toast.LENGTH_SHORT).show();
				} else {
					SocketService.getInstance().destroy();

					activity.getPlayButton().setImageBitmap(bitmapResize(R.drawable.start, 200, 200));
					isStarted = false;

					PeerConnectionPool.getInstance().release();
					SocketService.getInstance().clearHandshakeHandler();

					Toast.makeText(activity, "방송을 종료합니다.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private Bitmap bitmapResize(int res, int height, int width){
		BitmapFactory.Options bitOption = new BitmapFactory.Options();
		bitOption.inSampleSize = 2;

		Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), res, bitOption);
		bitmap = Bitmap.createScaledBitmap(bitmap, height, width, true);

		return bitmap;
	}
}