package com.test.recommand.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.test.recommand.app.R;
import com.test.recommand.app.RestaurantActivity;
import com.test.recommand.model.RestaurantTweet;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 10..
 */
public class TweetListAdapter extends BaseAdapter {

    private Context context;
    private int resource;
    List<RestaurantTweet> items;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public TweetListAdapter(Context context, int resource, List<RestaurantTweet> itemList) {
        this.context = context;
        this.resource = resource;
        this.items = itemList;

        mRequestQueue = Volley.newRequestQueue(context);
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

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder h;

        if (convertView == null) {
            h = new Holder();
            convertView = View.inflate(context, resource, null);
            convertView.setTag(h);

            h.profileImgView = (NetworkImageView) convertView.findViewById(R.id.tweet_profile_img);
            h.textView = (TextView) convertView.findViewById(R.id.tweet_text);
        } else {
            h = (Holder) convertView.getTag();
        }

        RestaurantTweet model = (RestaurantTweet) getItem(position);

        if (model == null) {
            return convertView;
        }

        //async
        h.profileImgView.setImageUrl(model.getProfile_image_url(), mImageLoader);
        h.textView.setText(model.getText());

        return convertView;
    }

    private class Holder {
        NetworkImageView profileImgView;
        TextView textView;
    }
}
