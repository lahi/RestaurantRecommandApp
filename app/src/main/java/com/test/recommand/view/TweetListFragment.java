package com.test.recommand.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.test.recommand.app.R;
import com.test.recommand.app.RestaurantActivity;
import com.test.recommand.model.RestaurantTweet;
import com.test.recommand.model.RestaurantTweetList;
import com.test.recommand.service.Constants;
import com.test.recommand.service.TweetPullService;

import java.util.List;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class TweetListFragment extends Fragment {

    private Intent mServiceIntent;
    private IntentFilter mStatusIntentFilter;
    private TweetUpdateStateReceiver mTweetUpdateStateReceiver;

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tweet_fragment, container, false);

        if (mServiceIntent == null) {
            mServiceIntent = new Intent(getActivity(), TweetPullService.class);
            mServiceIntent.putExtra("query", "서교동 맛집");
            getActivity().startService(mServiceIntent);
        }

        if (mStatusIntentFilter == null)
            mStatusIntentFilter = new IntentFilter(Constants.BROADCAST_STATUS_UPDATE);

        mStatusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Registers the DownloadStateReceiver and its intent filters
        if (mTweetUpdateStateReceiver == null)
            mTweetUpdateStateReceiver = new TweetUpdateStateReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTweetUpdateStateReceiver, mStatusIntentFilter);

        return rootView;
    }

    private void updateListView(List<RestaurantTweet> itemList) {
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
    }

    private class TweetUpdateStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Tag", "extra ::: " + intent.getSerializableExtra(Constants.DATA_RESTAURANT_TWEET_LIST));
            RestaurantTweetList list = (RestaurantTweetList)intent.getSerializableExtra(Constants.DATA_RESTAURANT_TWEET_LIST);
            updateListView(list.getRestaurantList());

        }
    }
}
