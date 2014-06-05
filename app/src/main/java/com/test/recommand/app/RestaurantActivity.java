package com.test.recommand.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by sooyoungbyun on 2014. 6. 5..
 */
public class RestaurantActivity extends Activity {

    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.restaurant_activity);

        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new RestaurantBrowser());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);

        Bundle ext = getIntent().getExtras();
        String url = ext.getString("openUrl");

        webview.loadUrl(url);
    }

    private class RestaurantBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
