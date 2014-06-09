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
        }

        Fragment fragment = new MainObjectFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(MainObjectFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
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

class MainObjectFragment extends Fragment {
    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView;
        Bundle args = getArguments();

        if (args.getInt(ARG_OBJECT) == 1) {
            rootView = inflater.inflate( R.layout.map_fragment, container, false);
        } else {
            rootView = inflater.inflate( R.layout.tweet_fragment, container, false);
        }

        return rootView;
    }
}
