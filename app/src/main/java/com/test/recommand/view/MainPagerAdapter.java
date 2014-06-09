package com.test.recommand.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.recommand.app.R;

/**
 * Created by sooyoungbyun on 2014. 6. 9..
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {


    public MainPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new RestaurantMapFragment();
        } else {
            return new TweetListFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "맛집";
        } else {
            return "맛집 트윗";
        }
    }
}
