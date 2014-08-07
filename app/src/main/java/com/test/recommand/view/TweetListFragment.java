package com.test.recommand.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.test.recommand.app.R;
import com.test.recommand.app.RestaurantActivity;
import com.test.recommand.model.RestaurantTweet;
import com.test.recommand.service.Constants;

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
public class TweetListFragment extends Fragment {

    final static String mTag = "TweetListFragment";

    final static String CONSUMER_KEY = "Fzf0yX6zkEy9Xv1HgJd7uw";
    final static String CONSUMER_SECRET = "1159G2uqQzVCEgkPUkaGawMlWE5QVg4IZk0Vjzoq90M";

    final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    final static String TwitterSearchURL = "https://api.twitter.com/1.1/search/tweets.json";

    private IntentFilter mStatusIntentFilter;
    private TweetUpdateStateReceiver mTweetUpdateStateReceiver;

    private ListView mListView;

    private RequestQueue mRequestQueue;
    private String mAccessToken;

    private String currLatitude;
    private String currLongitude;
    private String currLocality;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tweet_fragment, container, false);

        if (mRequestQueue == null) {
            mRequestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        if (mStatusIntentFilter == null)
            mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_LOCATION_UPDATE);

        mStatusIntentFilter.addAction(Constants.BROADCAST_LOCATION_UPDATE);
        mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Registers the DownloadStateReceiver and its intent filters
        if (mTweetUpdateStateReceiver == null)
            mTweetUpdateStateReceiver = new TweetUpdateStateReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTweetUpdateStateReceiver, mStatusIntentFilter);

        return rootView;
    }

    private void updateListView(List<RestaurantTweet> itemList) {

        if (itemList.size() == 0)
            return;

        try {
            if (mListView == null) {
                mListView = (ListView) getActivity().findViewById(R.id.tweet_list_view);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        try {
                            URLSpan[] urlSpans = ((TextView)view.findViewById(R.id.tweet_text)).getUrls();

                            for ( URLSpan urlSpan : urlSpans )
                            {
                                Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                                intent.putExtra("openUrl", urlSpan.getURL());
                                getActivity().startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            TweetListAdapter adapter = new TweetListAdapter(getActivity(), R.layout.tweet_list_item, itemList);
            mListView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class TweetUpdateStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {

            Log.d("Update", "action ::" + intent.getAction());

            if (intent.getAction().toString().equals(Constants.BROADCAST_LOCATION_UPDATE)) {

                currLatitude = intent.getStringExtra(Constants.DATA_UPDATE_CURRENT_LATITUDE);
                currLongitude = intent.getStringExtra(Constants.DATA_UPDATE_CURRENT_LONGITUDE);
                currLocality = intent.getStringExtra(Constants.DATA_UPDATE_CURRENT_LOCALITY);

                // update
                if (mAccessToken == null)
                    requestTwitterAuth();
                else
                    requestTweetList();
            }
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

        JsonObjectRequest jr = new JsonObjectRequest(Request.Method.POST, url , null, new Response.Listener<JSONObject>() {

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

            url = Uri.parse(TwitterSearchURL)
                    .buildUpon()
                    .appendQueryParameter("q", currLocality + " AND " + "맛집" + " filter:links")
                            //.appendQueryParameter("geocode", mIntent.getStringExtra("latitude")+","+mIntent.getStringExtra("longitude")+","+"1000km")
                    .appendQueryParameter("result_type", "recent")
                    .appendQueryParameter("count","100")
                    .build().toString();

            Log.d(mTag, "url twitter : " + url);
        } catch (Exception e) {

            e.printStackTrace();
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

                    //update ui
                    updateListView(tweetList);
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
