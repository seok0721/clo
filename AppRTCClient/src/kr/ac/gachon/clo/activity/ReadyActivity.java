package kr.ac.gachon.clo.activity;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.gachon.clo.R;
import kr.ac.gachon.clo.event.ActivityEventHandler;
import kr.ac.gachon.clo.event.EventResult;
import kr.ac.gachon.clo.handler.OnAirButtonHandler;
import kr.ac.gachon.clo.service.SocketService;
import kr.ac.gachon.clo.utils.BitmapUtils;
import kr.ac.gachon.clo.view.ReadyView;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends Activity implements ActivityEventHandler, ReadyView {

	private static final String TAG = ReadyActivity.class.getSimpleName();
	private static final String EVENT = "create";
	private static Boolean waitForDisconnect = true;
	private TextView txtAddress;
	private TextView txtName;
	private EditText edtTitle;
	private Button btnOnAir;
	private ImageView imgThumbnail;

	String name;
	double x,y;
	Timer timer;
	LocationManager locationMananger;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ready);

		btnOnAir = (Button)findViewById(R.id.btnOnAir);
		btnOnAir.setOnClickListener(new OnAirButtonHandler(this));

		edtTitle = (EditText)findViewById(R.id.edtTitle);
		txtAddress = (TextView)findViewById(R.id.txtLocation);
		txtName = (TextView)findViewById(R.id.txtLoadName);
		imgThumbnail = (ImageView)findViewById(R.id.imgLoadThumbnail);

		SocketService.getInstance().addEventHandler(this);

		super.onStart();

		String encodedImage = getIntent().getStringExtra("img");
		byte[] decodedBytes = Base64.decode(encodedImage, 0);
		Bitmap bitmapImage = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

		try {
			imgThumbnail.setImageBitmap(BitmapUtils.getCircularBitmap(bitmapImage));
		} catch (Exception e) {
			Toast.makeText(this, "디폴트 썸네일을 사용합니다.", Toast.LENGTH_SHORT).show();
		}

		try {
			txtName.setText(new String(Base64.decode(getIntent().getStringExtra("name"), 0)));
		} catch (Exception e) {
			Toast.makeText(this, "알 수 없는 이름입니다.", Toast.LENGTH_SHORT).show();
			txtName.setText("Unknown");
		}

		locationMananger = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		isGPSEnabled = locationMananger.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationMananger.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if(!isGPSEnabled && !isNetworkEnabled) {
			Toast.makeText(this, "nothing is enabled", Toast.LENGTH_SHORT).show();
			return;
		}

		if(isGPSEnabled) {
			locationMananger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
		}

		if(isNetworkEnabled) {
			locationMananger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
		}

		timer = new Timer();
		timer.schedule(new GetLastLocation(), 20000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(waitForDisconnect) {
			SocketService.getInstance().stop();

			finish();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		waitForDisconnect = true;
	}

	@Override
	public void onMessage(JSONObject data) {
		String message;

		if(!waitForDisconnect) {
			return;
		}

		try {
			if(data.getInt("ret") == EventResult.FAILURE) {
				throw new Exception();
			}

			waitForDisconnect = false;

			Intent intent = new Intent(this, ShootingActivity.class);
			intent.putExtra("name", getName().getText().toString());
			intent.putExtra("title", getTitleName().getText().toString());
			intent.putExtra("address", getAddress().getText().toString());
			startActivity(intent);

			message = "방송을 시작합니다.";
		} catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);

			message = "방을 만드는 도중 오류가 발생하였습니다.";
		}

		Log.i(TAG, message);

		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	// Geocoder를 이용해서 latitude와 longitude를 통해 주소를 얻는 부분
	public String getAddress(double latitude, double longitude){
		try {
			Geocoder geocoder = new Geocoder(this, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

			String address = addresses.get(0).getAddressLine(0);
			//String city = addresses.get(0).getAddressLine(1);
			//String country = addresses.get(0).getAddressLine(2);

			return address;

		} catch(Exception e) {}

		return "Cannot found Location";
	}

	// latitude를 구하고 longtitude를 구하기 위한 소스들
	LocationListener gpsLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			x = location.getLatitude();
			y = location.getLongitude();
			locationMananger.removeUpdates(this);
			locationMananger.removeUpdates(networkLocationListener);

			txtAddress.setText(getAddress(x,y)); //get Current Address used x,y

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, "gps enabled "+x + "\n" + y, duration);
			toast.show();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener networkLocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			timer.cancel();
			x = location.getLatitude();
			y = location.getLongitude();
			locationMananger.removeUpdates(this);
			locationMananger.removeUpdates(gpsLocationListener);

			txtAddress.setText(getAddress(x,y)); //get Current Address used x,y

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, "network enabled"+x + "\n" + y, duration);
			toast.show();
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation extends TimerTask {

		@Override
		public void run() {
			locationMananger.removeUpdates(gpsLocationListener);
			locationMananger.removeUpdates(networkLocationListener);

			Location net_loc = null, gps_loc = null;
			if (isGPSEnabled)
				gps_loc = locationMananger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (isNetworkEnabled)
				net_loc = locationMananger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			//if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					x = gps_loc.getLatitude();
					y = gps_loc.getLongitude();
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, "gps lastknown " + x + "\n" + y, duration);
					toast.show();
				} else {
					x = net_loc.getLatitude();
					y = net_loc.getLongitude();
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, "network lastknown " + x + "\n" + y, duration);
					toast.show();
				}
			}

			if (gps_loc != null) {
				x = gps_loc.getLatitude();
				y = gps_loc.getLongitude();
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, "gps lastknown " + x + "\n" + y, duration);
				toast.show();
			}

			if (net_loc != null) {
				x = net_loc.getLatitude();
				y = net_loc.getLongitude();
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, "network lastknown " + x + "\n" + y, duration);
				toast.show();
			}

			Context context = getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, "no last know avilable", duration).show();
		}
	}

	@Override
	public TextView getAddress() {
		return txtAddress;
	}

	@Override
	public TextView getName() {
		return txtName;
	}

	@Override
	public EditText getTitleName() {
		return edtTitle;
	}

	@Override
	public Button getOnAirButton() {
		return btnOnAir;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public String getEvent() {
		return EVENT;
	}

	@Override
	public ImageView getThumbnailImage() {
		return imgThumbnail;
	}
}