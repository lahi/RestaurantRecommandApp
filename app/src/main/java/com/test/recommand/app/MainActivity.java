package com.test.recommand.app;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.test.recommand.view.MapsFragment;

/**
 * Created by sooyoungbyun on 2014. 6. 2..
 */
public class MainActivity extends Activity {

    private MapsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            // map
            mFragment = new MapsFragment();

            LinearLayout fragContainer = (LinearLayout) findViewById(R.id.map_fragment_container);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(fragContainer.getId(), mFragment.getFragmentManager().findFragmentById(R.id.map));
            ft.commit();
        }

        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
}
