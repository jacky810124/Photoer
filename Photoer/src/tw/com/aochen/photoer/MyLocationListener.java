package tw.com.aochen.photoer;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

	private Context context;

	public Boolean gpsService = false, networkService = false;

	private String provider;

	private LocationManager lm;

	private Location location;

	public MyLocationListener(Context context) {
		this.context = context;
		gpsStatus();
		networkStatus();
	}

	public void gpsStatus() {

		LocationManager status = (LocationManager) (context
				.getSystemService(Context.LOCATION_SERVICE));

		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(context, "Location service is enable",
					Toast.LENGTH_SHORT).show();

			gpsService = true;

		} else {
			Toast.makeText(context, "Location service is disable",
					Toast.LENGTH_SHORT).show();
			gpsService = false;
		}
	}

	private void networkStatus() {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			Toast.makeText(context, "Network service is enable",
					Toast.LENGTH_SHORT).show();
			networkService = true;
		} else {
			Toast.makeText(context, "Network service is disable",
					Toast.LENGTH_SHORT).show();
			networkService = false;
		}
	}

	private void getLastLocation() {
		lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		provider = lm.getBestProvider(new Criteria(), true);

		lm.requestLocationUpdates(provider, 1, 60000, this);
		location = lm.getLastKnownLocation(provider);
	}

	public String getAddress() {
		String addr = "";
		if (networkService && gpsService) {

			getLastLocation();

			if (location != null) {
				List<Address> address = null;
				Geocoder gc = new Geocoder(context, Locale.getDefault());

				try {
					address = gc.getFromLocation(location.getLatitude(),
							location.getLongitude(), 1);

					if (address.get(0).getLocality() != null)
						addr = address.get(0).getAdminArea()
								+ address.get(0).getLocality();
					else
						addr = address.get(0).getAdminArea();

				} catch (Exception e) {
				}
			}
		}
		return addr;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		location = lm.getLastKnownLocation(provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
