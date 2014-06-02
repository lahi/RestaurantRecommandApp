package com.test.recommand.app;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.recommand.model.RssType;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private RequestQueue mRequestQueue;

    final private static String mTag = "Map_Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(this);
        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 20000, 0, this);

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(position).title("Marker"));

        requestNearRestaurant(position);

    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d(mTag, "on location changed");

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(mTag, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(mTag, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(mTag, "onProviderDisabled");
    }

    private void requestNearRestaurant (LatLng position) {

        // get location address name
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String thorough = "강남구";
        try {
            Log.d(mTag, "lat : " + position.latitude + "longitude : " + position.longitude);
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            thorough = addresses.get(0).getThoroughfare();

            Log.d(mTag, "address list" + addresses);
            Log.d(mTag, "address :" + address + " 동 : " + thorough);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        String url = Uri.parse("http://openapi.naver.com/search")
                .buildUpon()
                .appendQueryParameter("key", "c83cbb516fb18f80ba9243ff2af08ced")
                .appendQueryParameter("query", thorough+"맛집")
                .appendQueryParameter("target", "local")
                .appendQueryParameter("start", "1")
                .appendQueryParameter("display", "10")
                .build().toString();
        Log.d(mTag, "url : " + url);

        StringRequest jr = new StringRequest(Request.Method.POST, url ,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i(mTag, "response : " + response.toString());

                Serializer serializer = new Persister();
                try {
                    RssType rssType = serializer.read(RssType.class, response);
                    Log.d(mTag, "title: " + rssType.getChannel().getItemList().get(0).getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(mTag, "error" + volleyError.getMessage());
            }
        });

        mRequestQueue.add(jr);

    }

}
