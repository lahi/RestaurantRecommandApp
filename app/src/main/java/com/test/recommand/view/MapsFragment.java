package com.test.recommand.view;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.soo.util.GeoCoordTranslate;
import com.test.recommand.app.R;
import com.test.recommand.model.ItemType;
import com.test.recommand.model.RssType;
import com.test.recommand.network.NetworkManager;
import com.test.recommand.service.Constants;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.abs;

public class MapsFragment extends Fragment implements LocationListener, GoogleMap.OnMarkerClickListener {

    private FragmentActivity context;

    private double currLatitude;
    private double currLongitude;

    private final Map<String, MarkerOptions> mMarkers = new ConcurrentHashMap<String, MarkerOptions>();

    public interface OnUpdate {
        void update(RssType rssType);
    }

    public interface OnReset {
        void reset();
    }

    public interface OnMarkerClicked {
        void updateList(String title);
    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    final private static String mTag = "MapLog";

    OnUpdate onUpdate;
    OnReset onReset;
    OnMarkerClicked markerClicked;

    public MapsFragment setUpdateListener (OnUpdate updateListener) {
        onUpdate = updateListener;
        return this;
    }

    public MapsFragment setResetListener (OnReset resetListener) {
        onReset = resetListener;
        return this;
    }

    public MapsFragment setUpdateMarkerClickListener (OnMarkerClicked markerClickListener) {
        markerClicked = markerClickListener;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = (FragmentActivity)container.getContext();
        setUpMapIfNeeded();

        return  null;
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.

            mMap = ((SupportMapFragment) context.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 0, this);
    }

    private  void updateMap(List<ItemType> itemList) {

        try {
                for (final ItemType item : itemList) {

                //add restaurant marker
                GeoCoordTranslate trans = new GeoCoordTranslate();
                trans.GeoCoordTranslateAddressToLatLng(getActivity(), item.getAddress());

                trans.setUpdateListener( new GeoCoordTranslate.OnGeoCodeUpdate() {
                    @Override
                    public void update(LatLng geocode) {

                        if (mMarkers.get(item.getTitle()) != null )
                            return;

                        MarkerOptions marker = new MarkerOptions()
                                .position(geocode)
                                .title(item.getTitle())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .alpha(0.7f);

                        mMarkers.put(item.getTitle(), marker);
                        mMap.addMarker(marker);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(mTag, "marker : " + marker);

        marker.showInfoWindow();
        markerClicked.updateList(marker.getTitle());

        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if ((abs(latitude - currLatitude) < 10) && (abs(longitude - currLongitude) < 10))
            return;

        currLatitude = latitude;
        currLongitude = longitude;

        Log.d(mTag, "on location changed");

        mMap.clear();

        //update
        LatLng position = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(position).title("현재 위치"));
        mMap.setOnMarkerClickListener(this);

        onReset.reset();
        requestNearRestaurant(position, 1);

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

    public void requestRestaurantMore(int reqPageNum) {
        //update
        LatLng position = new LatLng(currLatitude, currLongitude);
        requestNearRestaurant(position, reqPageNum+1);
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

    private void requestNearRestaurant (LatLng position, int currpageNum) {

        Log.d("TAG", "Request Near Restaurant : " + currpageNum);

        // get location address name
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String locality = "";
        String thorough = "";

        try {
            Log.d(mTag, "lat : " + position.latitude + "longitude : " + position.longitude);
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);

            locality = addresses.get(0).getLocality();
            thorough = addresses.get(0).getThoroughfare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //request
        String url = Uri.parse("http://openapi.naver.com/search")
                .buildUpon()
                .appendQueryParameter("key", "c83cbb516fb18f80ba9243ff2af08ced")
                .appendQueryParameter("query", locality + " " + thorough + " 맛집")
                .appendQueryParameter("target", "local")
                .appendQueryParameter("start", "" + currpageNum)
                .appendQueryParameter("display", "30")
                .appendQueryParameter("sort", "vote")
                .build().toString();
        Log.d(mTag, "url : " + url);

        NetworkManager.getInstance(getActivity()).stringRequest(Request.Method.POST, url ,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i(mTag, "naver rest response : " + response.toString());

                Serializer serializer = new Persister();
                try {
                    RssType rssType = serializer.read(RssType.class, response);
                    onUpdate.update(rssType);
                    updateMap(rssType.getChannel().getItemList());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, null);

        //send event
        Intent localIntent =
                new Intent(Constants.BROADCAST_LOCATION_UPDATE).putExtra(Constants.DATA_UPDATE_CURRENT_LATITUDE, "" + currLatitude);
        localIntent.putExtra(Constants.DATA_UPDATE_CURRENT_LONGITUDE, "" + currLongitude);
        localIntent.putExtra(Constants.DATA_UPDATE_CURRENT_LOCALITY, thorough);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(localIntent);

    }

}
