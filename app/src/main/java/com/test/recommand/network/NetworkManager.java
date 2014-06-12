package com.test.recommand.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by sooyoungbyun on 2014. 6. 12..
 */
public class NetworkManager {

    private static NetworkManager networkManager;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    final private String mTag = "NetworkManager";

    private NetworkManager(Context context) {

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(context);
        }

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }
    }

    public static NetworkManager getInstance(Context context){

        if (networkManager == null) {
            networkManager = new NetworkManager(context);
        }

        return networkManager;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void jsonRequest(int method, String url, JSONObject jsonRequest, final Response.Listener<org.json.JSONObject> listener, final Response.ErrorListener errorListener) {

        JsonObjectRequest jr = new JsonObjectRequest(method, url , jsonRequest, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(mTag, "Response : " + response);

                try {
                    listener.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse (VolleyError volleyError){

                    Log.i(mTag, "error" + volleyError.getMessage());

                    try {
                        errorListener.onErrorResponse(volleyError);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        });

        mRequestQueue.add(jr);
    }

    public void stringRequest(int method, String url, final Response.Listener<String> listener,
                              final Response.ErrorListener errorListener) {

        StringRequest jr = new StringRequest(method, url ,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i(mTag, "response : " + response.toString());

                try {

                   listener.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(mTag, "error" + volleyError.getMessage());

                try {
                    errorListener.onErrorResponse(volleyError);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mRequestQueue.add(jr);
    }
}
