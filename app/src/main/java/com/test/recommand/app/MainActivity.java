package com.test.recommand.app;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.parse.ParseAnalytics;
import com.soo.util.AES256Cipher;
import com.test.recommand.network.NetworkManager;
import com.test.recommand.view.MainPagerAdapter;

import com.parse.Parse;
import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
public class MainActivity extends FragmentActivity  {

    MainPagerAdapter mMainCollectionPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        //loginTest();

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //view pager
        mMainCollectionPagerAdapter =
                new MainPagerAdapter(this.getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mMainCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        //tab
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                mViewPager.setCurrentItem(tab.getPosition());
                actionBar.setTitle(mMainCollectionPagerAdapter.getPageTitle(tab.getPosition()));
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 2 tabs, specifying the tab's text and TabListener
        for (int i = 0; i < 2; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mMainCollectionPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }

        //parse
        Parse.initialize(this, "iyM9J8wFfOos3k2Ek94CIKG3r8yUo6W3fWmBN1k1", "KWeXSsYIzG11a3miS71bywxaBuhaQumhPFTsjLPf");
        //PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseAnalytics.trackAppOpened(getIntent());
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void loginTest() {

        String e_key = "1758901758901604";

        byte[] keyBytes = new byte[0];
        try {
            keyBytes = e_key.getBytes("UTF-8");

            Log.d("TAG", "key bypte : " + keyBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] ivBytes = { 0x00, 0x00,0x00 ,0x00 ,
                0x00 ,0x00 ,0x00 ,0x00 ,
                0x00 ,0x00 ,0x00, 0x00,
                0x00 ,0x00 ,0x00, 0x00};

        String plainText;
        byte[] cipherData = new byte[0];
        String base64Text;

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("email", "byunsoo@naver.com");
            jsonRequest.put("phone_number", "1234556");
            jsonRequest.put("nick_name", "soo");
            jsonRequest.put("real_name", "변수영");
            jsonRequest.put("password", "aaaaaa111");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            cipherData = AES256Cipher.encrypt(ivBytes, keyBytes, jsonRequest.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            Log.d( "tag", "cipher data ::" + cipherData);
            base64Text = Base64.encodeToString(cipherData, Base64.DEFAULT);
            Log.d("encrypt ::", base64Text);

            //request
            String url = Uri.parse("http://192.168.1.152:3000/account/join")
                    .buildUpon()
                    .appendQueryParameter("e_key", "12312312312311234")
                    .appendQueryParameter("params", base64Text)
                    .build().toString();
            Log.d("TAG", "url : " + url);

            NetworkManager.getInstance(this).jsonRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.i("TAG", "join response : " + response.toString());
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
