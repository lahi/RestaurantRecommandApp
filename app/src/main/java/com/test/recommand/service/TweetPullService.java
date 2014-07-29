package com.test.recommand.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.parse.ParseAnalytics;
import com.test.recommand.model.RestaurantTweet;
import com.test.recommand.model.RestaurantTweetList;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetPullService extends IntentService {

    public static final int SERVICE_ID = 1000;

    final static String mTag = "TweetPullService";

    final static String CONSUMER_KEY = "Fzf0yX6zkEy9Xv1HgJd7uw";
    final static String CONSUMER_SECRET = "1159G2uqQzVCEgkPUkaGawMlWE5QVg4IZk0Vjzoq90M";

    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json";

    private Context mContext;

    private RequestQueue mRequestQueue;
    private Intent mIntent;

    private String mAccessToken;

    public TweetPullService() {
        super("TweetPullService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        mContext = this;

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(getApplicationContext());
        }

        mIntent = workIntent;

        try {
            if (mAccessToken == null)
                requestTwitterAuth();
            else
                requestTweetList();
        } catch (Exception e) {

            e.printStackTrace();
            ParseAnalytics.trackEvent("TweetPullService onHandleIntent Exception");
        }
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

        Log.d(mTag, "tweet auth url : " + url);

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

        //request
        String url;

        try {

            String queryString = mIntent.getStringExtra("query");
            url = Uri.parse(TwitterSearchURL)
                    .buildUpon()
                    .appendQueryParameter("q", mIntent.getStringExtra("locality") + " OR " + queryString + " filter:links")
                    .appendQueryParameter("geocode", mIntent.getStringExtra("latitude")+","+mIntent.getStringExtra("longitude")+","+"1000km")
                    .appendQueryParameter("result_type", "recent")
                    .appendQueryParameter("count","50")
                    .build().toString();

            Log.d(mTag, "url twitter : " + url);
        } catch (Exception e) {

            e.printStackTrace();
            ParseAnalytics.trackEvent("RequestTweetList Exception");

            return;
        }

        JsonObjectRequest streamRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(mTag, "tweet response :: " + response);

                ObjectMapper om = new ObjectMapper();

                JsonNode root = null;
                try {
                    root = om.readTree(response.toString());

                    ArrayNode tweetJsonList = (ArrayNode)root.get("statuses");
                    ArrayList<RestaurantTweet> tweetList = new ArrayList<RestaurantTweet>();

                    for (JsonNode object : tweetJsonList) {
                        RestaurantTweet tw = new RestaurantTweet();
                        tw.setId(object.findValue("id").asText());
                        tw.setText(object.findValue("text").asText());
                        tw.setProfile_image_url(object.findValue("profile_image_url").asText());

                        tweetList.add(tw);
                    }

                    //send event
                    Intent localIntent =
                            new Intent(Constants.BROADCAST_STATUS_UPDATE).putExtra(Constants.DATA_RESTAURANT_TWEET_LIST, new RestaurantTweetList(tweetList));
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

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

