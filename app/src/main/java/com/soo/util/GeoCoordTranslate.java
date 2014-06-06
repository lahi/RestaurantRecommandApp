package com.soo.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by sooyoungbyun on 2014. 6. 3..
 */
public class GeoCoordTranslate {

    final private String mTag = "geo";

    public interface OnGeoCodeUpdate {
        void update(LatLng geocode);
    }

    private RequestQueue mRequestQueue;

    OnGeoCodeUpdate onGeoCodeUpdate;

    public GeoCoordTranslate setUpdateListener (OnGeoCodeUpdate updateListener) {
        onGeoCodeUpdate = updateListener;
        return this;
    }

    public GeoCoordTranslate(Context context) {

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(context);
        }
    }

    public void GeoCoordTranslateAddressToLatLng(String address) {
        //request
        String url = Uri.parse("http://maps.google.com/maps/api/geocode/json")
                .buildUpon()
                .appendQueryParameter("address", address)
                .appendQueryParameter("sensor", "false")
                .build().toString();

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url , null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(mTag, "Response : " + response);

                ObjectMapper om = new ObjectMapper();

                // JSON 문자열을 xml 다루는것과 비슷하게 트리로 맨들어서 트래버싱하기(Tree Model)
                JsonNode root = null;
                try {
                    root = om.readTree(response.toString());

                    if (root.findValue("error_message") == null) {

                        Double lat = root.findPath("location").get("lat").asDouble();
                        Double lng = root.findPath("location").get("lng").asDouble();
                        LatLng geocode = new LatLng(lat.doubleValue(), lng.doubleValue());

                        onGeoCodeUpdate.update(geocode);
                    } else {
                        Log.d(mTag, "Error : " + root.findPath("error_message").get("error_message").toString());
                    }

                } catch (IOException e) {
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
