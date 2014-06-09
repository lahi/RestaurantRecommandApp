package com.test.recommand.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.recommand.app.R;
import com.test.recommand.service.Constants;
import com.test.recommand.service.TweetPullService;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class TweetListFragment extends Fragment {

    Intent mServiceIntent;
    IntentFilter mStatusIntentFilter;
    TweetUpdateStateReceiver mTweetUpdateStateReceiver;

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

    private class TweetUpdateStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("Tag", "extra ::: " + intent.getExtras());
        }
    }
}
