package com.soo.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Created by sooyoungbyun on 2014. 6. 3..
 */
public class GeoCoordTranslate {

    private RequestQueue mRequestQueue;


    public GeoCoordTranslate(Context context) {

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(context);
        }
    }

    //json으로 ..
    public void GeoCoordTranslateAddressToLatLng(String address) {
        //request
        String url = Uri.parse("http://maps.google.com/maps/api/geocode/xml")
                .buildUpon()
                .appendQueryParameter("address", address)
                .appendQueryParameter("sensor", "false")
                .build().toString();

        Log.d("tag", "url :" + url);

        StringRequest jr = new StringRequest(Request.Method.POST, url ,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("tag", "response : " + response);

                Serializer serializer = new Persister();
                try {
                    Geocode geo = serializer.read(Geocode.class, response);
                    Log.d("TAG", "lat: " + geo.getResult().getGeoMetry().getLocation().getLat());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("tag", "error" + volleyError.getMessage());
            }
        });

        mRequestQueue.add(jr);
    }
}
