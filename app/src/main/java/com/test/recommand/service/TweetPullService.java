package com.test.recommand.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.recommand.model.RestaurantTweet;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class TweetPullService extends IntentService {

    final static String mTag = "TweetPullService";

    final static String CONSUMER_KEY = "Fzf0yX6zkEy9Xv1HgJd7uw";
    final static String CONSUMER_SECRET = "1159G2uqQzVCEgkPUkaGawMlWE5QVg4IZk0Vjzoq90M";

    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json";

    private RequestQueue mRequestQueue;
    private Intent mIntent;

    private String mAccessToken;

    public TweetPullService() {
        super("TweetPullService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(getApplicationContext());
        }

        mIntent = workIntent;

        if (mAccessToken == null)
            requestTwitterAuth();
        else
            requestTweetList();
    }

    private void requestTwitterAuth() {


        String urlApiKey = null;
        String urlApiSecret = null;
        try {
            urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
            urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //request
        String url = Uri.parse(TwitterTokenURL)
                .buildUpon()
                .appendQueryParameter("grant_type", "client_credentials")
                .build().toString();

        Log.d(mTag, "url : " + url);

        final String finalUrlApiKey = urlApiKey;
        final String finalUrlApiSecret = urlApiSecret;

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url , null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                ObjectMapper om = new ObjectMapper();

                JsonNode root = null;
                try {
                    root = om.readTree(response.toString());

                    String tokenType = root.get("token_type").asText();

                    if (tokenType.equals("bearer")) {

                        mAccessToken = root.get("access_token").asText();
                        requestTweetList();
                    } else {
                        Log.d(mTag, "Differnent token type");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(mTag, "error " + volleyError.getMessage());
            }

        }) {

            String combined = finalUrlApiKey + ":" + finalUrlApiSecret;

            // Base64 encode the string
            String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Basic " + base64Encoded);

                return params;
            }
        };

        mRequestQueue.add(jr);
    }

    private void requestTweetList() {

        String queryString = mIntent.getStringExtra("query");

        //request
        String url = Uri.parse(TwitterSearchURL)
                .buildUpon()
                .appendQueryParameter("q", queryString)
                .build().toString();

        Log.d(mTag, "url twitter : " + url);

        JsonObjectRequest streamRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(mTag, "response twitter : " + response);

                ObjectMapper om = new ObjectMapper();

                JsonNode root = null;
                try {
                    root = om.readTree(response.toString());

                    List<JsonNode> tweetJsonList = root.findValues("statuses");
                    ArrayList<RestaurantTweet> tweetList = new ArrayList<RestaurantTweet>();
                    for (JsonNode object : tweetJsonList) {
                        RestaurantTweet tw = new RestaurantTweet();
                        tw.setId(object.findValue("id").asText());
                        tw.setText(object.findValue("text").asText());
                        tw.setProfile_image_url(object.findValue("profile_image_url").asText());

                        tweetList.add(tw);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(mTag, "error " + volleyError.getMessage());
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + mAccessToken);

                return params;
            }
        };

        mRequestQueue.add(streamRequest);
    }
}