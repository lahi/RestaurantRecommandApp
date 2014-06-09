package com.test.recommand.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.recommand.app.R;
import com.test.recommand.service.TweetPullService;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class TweetListFragment extends Fragment {

    Intent mServiceIntent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tweet_fragment, container, false);

        if (mServiceIntent == null) {
            mServiceIntent = new Intent(getActivity(), TweetPullService.class);
            Log.d("Strat", "Start service");
            mServiceIntent.putExtra("query", "서교동 맛집");
            getActivity().startService(mServiceIntent);
        }

        return rootView;
    }
}
