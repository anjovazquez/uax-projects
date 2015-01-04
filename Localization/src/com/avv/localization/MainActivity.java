package com.avv.localization;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

public class MainActivity extends Activity implements OnItemClickListener {

	private Button bLocalize;
	private AutoCompleteTextView origin;
	private AutoCompleteTextView destination;
	private GoogleMap map;
	private String apiKey;
	private boolean sattelite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sattelite = false;
		try {
			ApplicationInfo app = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = app.metaData;
			apiKey = bundle.getString("com.google.places.API_KEY");
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			Toast.makeText(this, "Los mapas no están disponibles",
					Toast.LENGTH_LONG).show();
		} else {

			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			origin = (AutoCompleteTextView) findViewById(R.id.origin);
			destination = (AutoCompleteTextView) findViewById(R.id.destination);
			origin.setAdapter(new PlacesAutoCompleteAdapter(this,
					R.layout.place_list_item));
			destination.setAdapter(new PlacesAutoCompleteAdapter(this,
					R.layout.place_list_item));
			origin.setOnItemClickListener(this);
			destination.setOnItemClickListener(this);

			bLocalize = (Button) findViewById(R.id.localize);
			bLocalize.setOnClickListener(new LocalizeListener());
		}
	}

	private class LocalizeListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (origin.getText() != null && origin.getText().length() > 0
					&& destination.getText() != null
					&& destination.getText().length() > 0) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(destination.getWindowToken(), 0);
				map.clear();
				Geocoder geocoder = new Geocoder(MainActivity.this);
				try {
					List<Address> mOrigins = geocoder.getFromLocationName(
							origin.getText().toString(), 5);
					List<Address> mDestinations = geocoder.getFromLocationName(
							destination.getText().toString(), 5);

					LatLng positionOr = new LatLng(mOrigins.get(0)
							.getLatitude(), mOrigins.get(0).getLongitude());
					final LatLng positionDest = new LatLng(mDestinations.get(0)
							.getLatitude(), mDestinations.get(0).getLongitude());

					Toast.makeText(
							MainActivity.this,
							"Distancia "
									+ SphericalUtil.computeDistanceBetween(
											positionOr, positionDest) / 1000
									+ " km", Toast.LENGTH_LONG).show();

					map.addMarker(new MarkerOptions()
							.position(positionOr)
							.title("origen")
							.snippet(mOrigins.get(0).getLocality())
							.draggable(true)
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					map.addMarker(new MarkerOptions()
							.position(positionDest)
							.title("destino")
							.snippet(mDestinations.get(0).getLocality())
							.draggable(true)
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

					map.addPolyline(new PolylineOptions()
							.add(positionOr)
							.add(positionDest)
							.color(getResources().getColor(
									android.R.color.holo_red_dark)));

					map.moveCamera(CameraUpdateFactory.newLatLngZoom(
							positionOr, map.getMaxZoomLevel()));
					final Handler handler = new Handler();
					handler.postDelayed(new ZoomOutRunnable(), 1000);
					handler.postDelayed(new AnimateToRunnable(positionDest),
							1000);

					handler.postDelayed(new ShowDialogRunnable(positionOr,
							positionDest), 3000);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				findViewById(R.id.search_box).setVisibility(View.GONE);
			} else {
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.fill_fields),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class ShowDialogRunnable implements Runnable {

		private LatLng positionOr;
		private LatLng positionDest;

		public ShowDialogRunnable(LatLng positionOr, LatLng positionDest) {
			this.positionOr = positionOr;
			this.positionDest = positionDest;
		}

		@Override
		public void run() {
			MapInfoFragment dialog = new MapInfoFragment(origin.getText()
					.toString(), destination.getText().toString(),
					SphericalUtil.computeDistanceBetween(positionOr,
							positionDest) / 1000 + " km");
			dialog.show(getFragmentManager(), "distance_dialog");
		}
	}

	private class ZoomOutRunnable implements Runnable {

		@Override
		public void run() {
			map.animateCamera(CameraUpdateFactory.zoomOut());
		}

	}

	private class AnimateToRunnable implements Runnable {

		private LatLng positionTo;

		public AnimateToRunnable(LatLng positionTo) {
			this.positionTo = positionTo;
		}

		@Override
		public void run() {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(positionTo,
					map.getMaxZoomLevel()));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_search:
			findViewById(R.id.search_box).setVisibility(View.VISIBLE);
			break;
		case R.id.action_map:
			if (!sattelite) {
				map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				sattelite = true;
			} else {
				map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				sattelite = false;
			}
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// El enfoque utilizado en este caso es el de usar el API de google Places
	// y disponer de dos campos de autocompletado en los cuales seleccionar
	// origen y destino unívocamente
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected Filter.FilterResults performFiltering(
						CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?sensor=false&key=" + apiKey);
			sb.append("&components=country:es");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(getClass().getCanonicalName(),
					"Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(getClass().getCanonicalName(),
					"Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e(getClass().getCanonicalName(), "Cannot process JSON results",
					e);
		}

		return resultList;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

	}
}
