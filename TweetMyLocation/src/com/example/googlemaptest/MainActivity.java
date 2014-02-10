package com.example.googlemaptest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	private static final String TAG = null;
	private String showText ="";
	private TextView CurrentInfoView ; 
	private String tweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*get device ID*/
		//String deviceID =Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
		
		/*get model and version*/
		String PhoneModel = android.os.Build.MODEL;
		String AndroidVersion = android.os.Build.VERSION.RELEASE;
		String deviceModelVersion =PhoneModel+" "+AndroidVersion;

		/*concatenate above info */
		showText = "\n"+"Model&Version: "+deviceModelVersion+"\n"; 
		tweet = "/"+deviceModelVersion;
		Button geoLocationButton = (Button)findViewById(R.id.button1);
		geoLocationButton.setOnClickListener(btnListener1);

	}

	// set on-click event handler
	private OnClickListener btnListener1 = new OnClickListener() {
		public void onClick(View v) {   
			Location address = this.getBestLocation();
			double longtitude = address.getLongitude();
			double latitude= address.getLatitude();

			SimpleDateFormat timeEST = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			timeEST.setTimeZone(TimeZone.getTimeZone("GMT-4"));
			String currentTime =timeEST.format(new Date())+" EST";

			Geocoder geoCoder = new Geocoder(
					getBaseContext(), Locale.getDefault());
			try {
				List<Address> addresses = geoCoder.getFromLocation(latitude,longtitude,1);

				String add = "";
				if (addresses.size() > 0) {
					for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
							i++)
						add += addresses.get(0).getAddressLine(i) + "\n";


					/* Keep both time and location updated when user click the button */
					String temp = showText; 
					showText = "Hi, there! "+temp+"Cuurrent time: "+currentTime+"\n"+"Current location: "+add; 
					tweet = tweet+"/"+currentTime+"/"+add;

					CurrentInfoView = (TextView)findViewById(R.id.text1);
					CurrentInfoView.setText(showText);
					GoogleMap googleMap;
					googleMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getMap();
					googleMap.clear();//clear icon of last time

					googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
						@Override
						public View getInfoWindow(Marker arg0) {
							return null;
						}

						@Override
						public View getInfoContents(Marker marker) {

							View v = getLayoutInflater().inflate(R.layout.new_maker,
									null);
							TextView info = (TextView) v.findViewById(R.id.textView1);

							/*get full info of snippet and show them on textView*/
							String snippetInfo = marker.getSnippet().toString();
							info.setText(snippetInfo);
							return v;
						}
					});

					LatLng latLng = new LatLng(latitude, longtitude);
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.addMarker(new MarkerOptions()
					.position(latLng)
					.title("Hello! My current location and info: ")
					.snippet(showText)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					googleMap.getUiSettings().setCompassEnabled(true);
					googleMap.getUiSettings().setZoomControlsEnabled(true);
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
					showText = temp;
				}

			}
			catch (IOException e) {                
				e.printStackTrace();
			} 

			/*tweet happens here*/
			//	String status = tweet;
			//	new updateTwitterStatusTask().execute(status);

		} 

		/**
		 * Try to get the best location provider
		 * GPS takes first priority, but if GPS is not available, use network location
		 * @return
		 */
		private Location getBestLocation() {
			Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
			Location networkLocation =
					getLocationByProvider(LocationManager.NETWORK_PROVIDER);

			if (gpslocation == null) {
				Log.d(TAG, "No GPS Location available.");
				return networkLocation;
			}
			else  {
				Log.d(TAG, "No Network Location available");
				return gpslocation;
			}
		}

		/**
		 * get the last known location from a specific provider (network/gps)
		 * @return
		 */
		private Location getLocationByProvider(String provider) {
			Location location = null;

			LocationManager locationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
			try {
				if (locationManager.isProviderEnabled(provider)) {
					location = locationManager.getLastKnownLocation(provider);
				}
			} catch (IllegalArgumentException e) {
				Log.d(TAG, "Cannot acces Provider " + provider);
			}
			return location;
		}
	};

	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
