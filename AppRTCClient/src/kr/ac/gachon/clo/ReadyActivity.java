package kr.ac.gachon.clo;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReadyActivity extends FragmentActivity implements View.OnClickListener{

	private TextView mAddress;
	private EditText mTitle; //broadcast title
	private TextView mName;

	String name;
	double x,y;
	Timer timer;
	LocationManager lm;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	String completeAddress = "Cannot found Location";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ready);

		Button onAir = (Button)findViewById(R.id.btn_onair);
		onAir.setOnClickListener(this);

		mTitle = (EditText)findViewById(R.id.pro_title); //방송 제목을 받는 부분
		mAddress = (TextView)findViewById(R.id.pro_location); //현재 위치를 받는 부분
		mName = (TextView)findViewById(R.id.pro_name);

		Intent intent = getIntent();
		name = intent.getStringExtra("name");

		mName.setText(name);

		//=========================================================================================
		// GPS와 Wifi기지국을 이용해 현재 위치를 가져오는 부분

		/*
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gps_enabled && !network_enabled) {  Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "nothing is enabled", duration);
            toast.show();
        }

        if (gps_enabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }

        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }

        timer=new Timer();
        timer.schedule(new GetLastLocation(), 20000);
		 */
	}

	public void onClick(View view) {
		Intent intent = new Intent(ReadyActivity.this, onAir.class);
		intent.putExtra("title", mTitle.getText().toString());
		intent.putExtra("name", name);
		intent.putExtra("address", completeAddress);
		startActivity(intent);

		//Toast.makeText(prepareActivity.this, "Test Click", Toast.LENGTH_SHORT).show();
	}

	// Geocoder를 이용해서 latitude와 longitude를 통해 주소를 얻는 부분
	public String getAddress(double latitude, double longitude){
		Geocoder geocoder;
		List<Address> addresses;

		try {
			geocoder = new Geocoder(this, Locale.getDefault());
			addresses = geocoder.getFromLocation(latitude, longitude, 1);

			String address = addresses.get(0).getAddressLine(0);
			//String city = addresses.get(0).getAddressLine(1);
			//String country = addresses.get(0).getAddressLine(2);

			completeAddress = address;

			return completeAddress;

		}
		catch(Exception e){

		};

		return completeAddress;
	}

	// latitude를 구하고 longtitude를 구하기 위한 소스들
	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			x =location.getLatitude();
			y = location.getLongitude();
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);

			mAddress.setText(getAddress(x,y)); //get Current Address used x,y

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

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			x = location.getLatitude();
			y = location.getLongitude();
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);

			mAddress.setText(getAddress(x,y)); //get Current Address used x,y

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
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			if (gps_enabled)
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (network_enabled)
				net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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
			Toast toast = Toast.makeText(context, "no last know avilable", duration);
			toast.show();
		}
	}
}
